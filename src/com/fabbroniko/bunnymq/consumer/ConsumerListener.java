package com.fabbroniko.bunnymq.consumer;

public interface ConsumerListener {

	boolean processMessage(final String message);
	
	void isClosing();
}
