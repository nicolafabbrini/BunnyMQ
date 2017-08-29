package com.fabbroniko.bunnymq.consumer;

import com.fabbroniko.bunnymq.BunnyMQ;
import com.fabbroniko.bunnymq.Message;

public class Consumer implements Runnable {
	
	private BunnyMQ bunnyMQ;
	private String queueName;
	private ConsumerListener consumerListener;
	
	public Consumer(final BunnyMQ bunnyMQ, final String queueName, final ConsumerListener consumerListener) {
		if((bunnyMQ == null) || (queueName == null) || (consumerListener == null))
			throw new NullPointerException();
		
		this.bunnyMQ = bunnyMQ;
		this.queueName = queueName;
		this.consumerListener = consumerListener;
		
		new Thread(this).start();
	}

	@Override
	public void run() {
		Message currentMessage;
		
		do {
			currentMessage = bunnyMQ.pull(queueName);
			
			// Don't send to the listener if it's a poison message
			if(!currentMessage.equals(BunnyMQ.POISON_MESSAGE_CONTENT)) {
				consumerListener.processMessage(currentMessage.getMessage());
			}
		} while (!currentMessage.getMessage().equals(BunnyMQ.POISON_MESSAGE_CONTENT));
		
		// Notify the listener that the consumer is closing.
		consumerListener.isClosing();
	}
}
