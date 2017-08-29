package com.fabbroniko.test.rabbitmq.consumer;

import org.junit.Ignore;
import org.junit.Test;

public class ConsumerTest {

	@Ignore
	@Test
	public void testConsumer() throws InterruptedException {
//		final ConsumerListener consumerListener = mock(ConsumerListener.class);
//		final BunnyMQ bunnyMQ = mock(BunnyMQ.class);
//		final String[] messages = {"Unit1","Unit2","Unit3","Unit4","Unit5"};
//		final Consumer consumer = new Consumer(bunnyMQ, "junit", consumerListener);
//		
//		final Object sync = new Object();
//		boolean consumable = false;
//		int currentIndex = 0;
//		
//		doAnswer(new Answer<Message>() {
//
//			@Override
//			public Message answer(final InvocationOnMock invocation) throws Throwable {
//				synchronized (sync) {
//					while(consumable)
//						sync.wait();
//				}
//				return new Message(messages[currentIndex], 1000);
//			}
//		}).when(bunnyMQ).pull(anyString());
//		
//		synchronized (sync) {
//			while(consumable)
//				sync.wait();
//			
//			consumable = true;
//			currentIndex++;
//			sync.notify();
//		}
//		
//		
//		verify(consumerListener, never()).isClosing();
//		
//		// TODO add poison message
//		
//		consumer.join();
//		
//		verify(consumerListener, times(1)).isClosing();
	}
}
