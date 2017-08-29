package com.fabbroniko.bunnymq;

import java.util.Map;
import java.util.Queue;

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
	private Map<String, Queue<String>> queueMap; // Queue name, queue
	
	public BunnyMQ() {
		this(null);
	}
	
	public BunnyMQ(final Properties properties) {
		if(properties == null)
			this.properties = new Properties();
		
		this.properties = properties;
		
		// TODO start manager thread 
		// TODO setup exhcange
	}
	
	public void newQueue(final String name) {
		// TODO Create a new pair name / queue
		// TODO set properties to the queue
	}
	
	public void push(final String queueName, final String message) {
		// TODO add message to the queue
	}
	
	public Message pull(final String queueName) {
		// TODO pull message from the given queue
		return null;
	}
	
	public void close() {
		// TODO add poison message to each queue
	}
	
	public class Properties {
		
		private boolean persistent;
		
		public Properties() {
			this(false);
		}
		
		public Properties(final boolean persistent) {
			this.persistent = persistent;
		}
		
		public void setPersistent(final boolean persistent) {
			this.persistent = persistent;
		}
		
		public boolean isPersistent() {
			return persistent;
		}
	}
}
