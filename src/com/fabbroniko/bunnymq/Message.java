package com.fabbroniko.bunnymq;

import java.nio.ByteBuffer;
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
		if(msg.length < Long.BYTES)
			throw new IllegalArgumentException("The encoded message must be at least Long.SIZE bytes.");
		
		final ByteBuffer expirationTimestamp = ByteBuffer.allocate(Long.BYTES);
		expirationTimestamp.put(msg, 0, Long.BYTES);
		final String message = new String(msg, Long.BYTES, msg.length - Long.BYTES);
		
		this.expirationTimestamp = expirationTimestamp.getLong(0);
		this.message = message;
	}
	
	public long getExpirationTimestamp() {
		return expirationTimestamp;
	}
	
	public String getMessage() {
		return message;
	}

	public byte[] getMessageAsBytes() {
		final ByteBuffer expirationTimestamp = ByteBuffer.allocate(Long.BYTES);
		expirationTimestamp.putLong(this.expirationTimestamp);
		final byte[] expirationTimestampArray = expirationTimestamp.array();
		final byte[] message = this.message.getBytes();
		final byte[] finalMessage = new byte[expirationTimestampArray.length + message.length];
		
		System.arraycopy(expirationTimestampArray, 0, finalMessage, 0, expirationTimestampArray.length);
		System.arraycopy(message, 0, finalMessage, expirationTimestampArray.length, message.length);
		
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
