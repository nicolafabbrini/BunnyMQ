package com.fabbroniko.bunnymq.consumer;

import java.io.IOException;

import com.fabbroniko.bunnymq.BunnyMQ;
import com.fabbroniko.bunnymq.Message;

public class Consumer extends Thread {
	
	private BunnyMQ bunnyMQ;
	private ConsumerListener consumerListener;
	
	public Consumer(final BunnyMQ bunnyMQ, final ConsumerListener consumerListener) {
		if((bunnyMQ == null) || (consumerListener == null))
			throw new NullPointerException();
		
		this.bunnyMQ = bunnyMQ;
		this.consumerListener = consumerListener;
		
		this.start();
	}

	@Override
	public void run() {
		Message currentMessage;
		
		do {
			try {
				currentMessage = bunnyMQ.pull();
				
				// Don't send to the listener if it's a poison message
				if(!currentMessage.equals(BunnyMQ.POISON_MESSAGE_CONTENT)) {
					// if the processing returns false, the message is re-queued.
					if(!consumerListener.processMessage(currentMessage.getMessage())) {
						currentMessage.reset();
						bunnyMQ.push(currentMessage);
					}
				}
			} catch (final IOException | InterruptedException e) {
				currentMessage = new Message(BunnyMQ.POISON_MESSAGE_CONTENT, 0);
			}
		} while (!currentMessage.getMessage().equals(BunnyMQ.POISON_MESSAGE_CONTENT));
		
		// Notify the listener that the consumer is closing.
		consumerListener.isClosing();
	}
}
