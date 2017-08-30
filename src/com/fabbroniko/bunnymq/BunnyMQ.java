package com.fabbroniko.bunnymq;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

/**
 * Requirements V1.0:
 * 
 * Push and pull data from a synchronized queue
 * Persist information in disk if requested
 * Allow scheduled pull
 * 
 * V1.0 supports only one consumer per queue only (ack works only for 1 message only and only 1 message is sent until the previous one is acked)
 * @author fabbronikoGaming
 *
 */
public class BunnyMQ {

	public static final String POISON_MESSAGE_CONTENT = "BunnyMQPoisonMessage";
	
	private Properties properties;
	private BlockingQueue<Message> queue;
	private Persister persister;
	
	private boolean isClosing;
	
	public BunnyMQ() throws IOException {
		this(null);
	}
	
	public BunnyMQ(final Properties properties) throws IOException {
		if(properties == null)
			this.properties = new Properties();
		
		this.properties = properties;
		this.queue = new DelayQueue<>();
		
		if(properties.isPersistent())
			this.persister = new Persister(properties.getPersistenceFile(), properties.getMaxFileLengthBeforeOptimisation());
		
		if(this.persister == null) {
			for(final Message m : persister.getStoredQueue()) {
				this.queue.add(m);
			}
		}
	}
	
	public synchronized void push(final String message) throws IOException {
		this.push(new Message(message, properties.getDelay()));
	}
	
	public synchronized void push(final Message message) throws IOException {
		if(message == null)
			throw new NullPointerException();
		
		persister.push(message);
		
		if(!isClosing)
			queue.add(message);
	}
	
	public Message pull() throws InterruptedException, IOException {
		Message message;
		
		// Get the first expired element in the queue
		message = queue.take();
		
		// if it's a poison message, add it back to the queue to allow the other consumers to consume it until none is left.
		if(message.getMessage().equals(POISON_MESSAGE_CONTENT)) {
			queue.add(message);
		} else {
			persister.pull();
		}
			
		return message;
	}
	
	/** 
	 * Forces the consumers to be closed.
	 * @throws IOException 
	 */
	public synchronized void close() throws IOException {
		queue.add(new Message(POISON_MESSAGE_CONTENT, 0));
		isClosing = true;
		persister.close();
	}
	
	public class Properties {
		
		private String persistenceFile;
		private int maxFileLengthBeforeOptimisation;
		private long delay;
		
		public Properties() {
			this(null);
		}
		
		public Properties(final String persistenceFile) {
			this(persistenceFile, 0);
		}
		
		public Properties(final String persistenceFile, final long delay) {
			this(persistenceFile, 1024 * 512, delay); // Defaulting to half mb
		}
		
		public Properties(final String persistenceFile, final int maxFileLengthBeforeOptimisation, final long delay) {
			if(delay < 0)
				throw new IllegalArgumentException("Delay can't be negative.");
			
			this.persistenceFile = persistenceFile;
			this.delay = delay;
			this.maxFileLengthBeforeOptimisation = (maxFileLengthBeforeOptimisation < 0) ? 0 : maxFileLengthBeforeOptimisation;
		}
		
		public void setPersistent(final String persistenceFile) {
			this.persistenceFile = persistenceFile;
		}
		
		public void setMaxFileLengthBeforeOptimisation(final int maxFileLengthBeforeOptimisation) {
			this.maxFileLengthBeforeOptimisation = maxFileLengthBeforeOptimisation;
		}
		
		public void setDelay(final long delay) {
			this.delay = delay;
		}
		
		public boolean isPersistent() {
			return persistenceFile != null;
		}
		
		public String getPersistenceFile() {
			return persistenceFile;
		}
		
		public int getMaxFileLengthBeforeOptimisation() {
			return maxFileLengthBeforeOptimisation;
		}
		
		public long getDelay() {
			return delay;
		}
	}
}
