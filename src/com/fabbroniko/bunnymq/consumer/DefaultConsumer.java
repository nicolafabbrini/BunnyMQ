package com.fabbroniko.bunnymq.consumer;

import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultConsumer implements Consumer {

	private static final long POLLING_TIMEOUT = 100; // ms
	
	private String queueName;
	private ConsumerListener consumerListener;
	private AtomicBoolean isClosing;
	
	public DefaultConsumer(final String queueName, final ConsumerListener consumerListener) {
		if((queueName == null) || (consumerListener == null))
			throw new NullPointerException();
		
		this.queueName = queueName;
		this.consumerListener = consumerListener;
		this.isClosing = new AtomicBoolean(false);
		
		new Thread(this).start();
	}

	@Override
	public void run() {
		do {
			// TODO Polling to get data from the queue every x specific milliseconds
			// TODO call listener with message
			// TODO ACK (tells the queue manager to remove it from the queue and persistence data).
			
			try {
				Thread.sleep(POLLING_TIMEOUT);
			} catch (final InterruptedException e) {
				isClosing.set(true);
			}
		} while (!isClosing.get());
		
		// Notify the listener that the consumer is closing.
		consumerListener.isClosing();
	}
	
	@Override
	public void close() {
		isClosing.set(true);
	}
}
