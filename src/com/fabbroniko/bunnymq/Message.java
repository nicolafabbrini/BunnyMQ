package com.fabbroniko.bunnymq;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class Message implements Delayed {

	private String message;
	private long expirationTimestamp;
	private TimeZone timeZone;
	
	public Message(final String message, final TimeZone timeZone, final long delay) {
		if((message == null) || (timeZone == null))
			throw new NullPointerException("All parameters must be set.");
		if(delay < 0)
			throw new IllegalArgumentException("Delay can't be less than 0.");
		
		this.message = message;
		this.timeZone = timeZone;
		this.expirationTimestamp = Calendar.getInstance(timeZone).getTime().getTime() + delay;
	}
	
	public Message(final String message, final long delay) {
		this(message, TimeZone.getDefault(), delay);
	}
	
	/**
	 * Creates the message from an array of bytes.
	 * The message is encoded as follows:
	 * _____________________________________________________________________
	 * | Expiration Timestamp | Message 									|
	 * | 8 Bytes              | n Bytes										|
	 * |______________________|_____________________________________________|
	 * 
	 * So the minimum message length is 8 bytes and the total length would be 8+n bytes
	 */
	public Message(final byte[] msg) {
		if(msg.length < Long.SIZE)
			throw new IllegalArgumentException("The encoded message must be at least Long.SIZE bytes.");
		
		final String expirationTimestampString = new String(msg, 0, Long.SIZE);
		final String message = new String(msg, Long.SIZE, msg.length - Long.SIZE);
		
		this.expirationTimestamp = Long.valueOf(expirationTimestampString);
		this.message = message;
	}
	
	public long getExpirationTimestamp() {
		return expirationTimestamp;
	}
	
	public String getMessage() {
		return message;
	}

	public byte[] getMessageAsBytes() {
		final byte[] expirationTimestamp = String.valueOf(this.expirationTimestamp).getBytes();
		final byte[] message = this.message.getBytes();
		final byte[] finalMessage = new byte[expirationTimestamp.length + message.length];
		
		System.arraycopy(expirationTimestamp, 0, finalMessage, 0, expirationTimestamp.length);
		System.arraycopy(message, 0, finalMessage, expirationTimestamp.length, message.length);
		
		return finalMessage;
	}
	
	@Override
	public int compareTo(final Delayed arg0) {
		return 0; // No need to implement this method
	}

	@Override
	public long getDelay(final TimeUnit arg0) {
		return expirationTimestamp - Calendar.getInstance(timeZone).getTime().getTime();
	}
	
	@Override
	public boolean equals(final Object o) {
		if((o == null) || !(o instanceof Message))
			return false;
		
		final Message msg = (Message)o;
		return (msg.getMessage().equals(this.message) && (msg.getExpirationTimestamp() == this.expirationTimestamp));
	}
}
