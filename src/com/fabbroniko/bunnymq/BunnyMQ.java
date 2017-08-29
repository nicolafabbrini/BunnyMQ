package com.fabbroniko.bunnymq;

import java.io.FileNotFoundException;
import java.io.IOException;
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
	private DelayQueue<Message> queue;
	private Persister persister;
	
	private boolean isClosing;
	
	public BunnyMQ() throws FileNotFoundException {
		this(null);
	}
	
	public BunnyMQ(final Properties properties) throws FileNotFoundException {
		if(properties == null)
			this.properties = new Properties();
		
		this.properties = properties;
		this.queue = new DelayQueue<>();
		this.persister = new Persister(null); // TODO file name
		
		// TODO try to load the content of previously written file
	}
	
	public synchronized void push(final String message) {
		this.push(new Message(message, properties.getDelay()));
	}
	
	public synchronized void push(final Message message) {
		if(message == null)
			throw new NullPointerException();
		
		// TODO persist
		
		if(!isClosing)
			queue.add(message);
	}
	
	public Message pull() {
		Message message;
		
		// Get the first expired element in the queue
		try {
			message = queue.take();
			
			// if it's a poison message, add it back to the queue to allow the other consumers to consume it until none is left.
			if(message.getMessage().equals(POISON_MESSAGE_CONTENT)) {
				queue.add(message);
			} else {
				// TODO remove from persistence
			}
		} catch (final InterruptedException e) {
			// If interrupted, return the poison message to close the consumer;
			message = new Message(POISON_MESSAGE_CONTENT, 0);
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
		
		private boolean persistent;
		private long delay;
		
		public Properties() {
			this(false);
		}
		
		public Properties(final boolean persistent) {
			this(persistent, 0);
		}
		
		public Properties(final boolean persistent, final long delay) {
			if(delay < 0)
				throw new IllegalArgumentException("Delay can't be negative.");
			
			this.persistent = persistent;
			this.delay = delay;
		}
		
		public void setPersistent(final boolean persistent) {
			this.persistent = persistent;
		}
		
		public void setDelay(final long delay) {
			this.delay = delay;
		}
		
		public boolean isPersistent() {
			return persistent;
		}
		
		public long getDelay() {
			return delay;
		}
	}
}
