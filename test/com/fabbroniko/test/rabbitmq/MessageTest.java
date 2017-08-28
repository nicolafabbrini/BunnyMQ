package com.fabbroniko.test.rabbitmq;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import org.junit.Test;

import com.fabbroniko.bunnymq.Message;

public class MessageTest {

	@Test(expected=NullPointerException.class)
	public void testNullMessage() {
		new Message(null, 1000);
	}
	
	@Test(expected=NullPointerException.class)
	public void testNullTimeZone() {
		new Message("Junit test", null, 1000);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidDelay() {
		new Message("Junit test", -1);
	}
	
	@Test
	public void testDefaultTimeZone() throws InterruptedException {
		final Message message = new Message("JUnit test", 500);
		
		assertEquals("JUnit test", message.getMessage());
		assertTrue(message.getDelay(TimeUnit.NANOSECONDS) > 0);
		
		Thread.sleep(600);
		assertTrue(message.getDelay(TimeUnit.NANOSECONDS) < 0);
	}
	
	@Test
	public void testCustomTimeZone() throws InterruptedException {
		final Message message = new Message("JUnit test", TimeZone.getTimeZone("UTC"),500);
		
		assertEquals("JUnit test", message.getMessage());
		assertTrue(message.getDelay(TimeUnit.NANOSECONDS) > 0);
		
		Thread.sleep(600);
		assertTrue(message.getDelay(TimeUnit.NANOSECONDS) < 0);
	}
	
	@Test
	public void testByteConversion() {
		final String testMessage = "This is a test message.";
		final Message message = new Message(testMessage, 3000);
		
		final Message messageFromBytes = new Message(message.getMessageAsBytes());
		
		assertEquals(message, messageFromBytes);
	}
}
