package com.fabbroniko.bunnymq.consumer;

public interface ConsumerListener {

	/**
	 * Process the message got from the queue.
	 * @param message The message content.
	 * @return true if successfully processed, false if you want to re-queue the message resetting its delay.
	 */
	boolean processMessage(final String message);
	
	/**
	 * Notifies the listener that the consumer has been closed.
	 */
	void isClosing();
}
