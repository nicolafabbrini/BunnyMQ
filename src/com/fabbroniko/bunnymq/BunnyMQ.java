package com.fabbroniko.bunnymq;

import java.util.Map;
import java.util.Queue;
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
	private Queue<Message> queue;
	private Persister persister;
	
	public BunnyMQ() {
		this(null);
	}
	
	public BunnyMQ(final Properties properties) {
		if(properties == null)
			this.properties = new Properties();
		
		this.properties = properties;
		this.queue = new DelayQueue<>();
	}
	
	public void push(final String message) {
		// TODO add message to the queue
	}
	
	public void push(final Message message) {
		// TODO
	}
	
	public Message pull() {
		// TODO pull message from the given queue
		return null;
	}
	
	/** 
	 * Forces the consumers to be closed.
	 */
	public void close() {
		queue.add(new Message(POISON_MESSAGE_CONTENT, 0));
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
