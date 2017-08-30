package com.fabbroniko.bunnymq;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.List;

public class Persister {
	
	private static final int PULL_POINTER_POSITION = 0;
	private static final int PUSH_POINTER_POSITION = PULL_POINTER_POSITION + Integer.BYTES;
	private static final int FIRST_MESSAGE_INDEX = Integer.BYTES * 2;
	
	private RandomAccessFile randomAccessFile;
	private int maxFileLengthBeforeOptimisation;
	private int pullIndex = 0;
	private int pushIndex = 0;
	
	public Persister(final String fileLocation, final int maxFileLengthBeforeOptimisation) throws IOException {
		this.randomAccessFile = new RandomAccessFile(fileLocation, "rwd"); // Read/Write and immediately synch to the underlying storage system
		this.pullIndex = FIRST_MESSAGE_INDEX;
		this.pushIndex = FIRST_MESSAGE_INDEX;
		this.maxFileLengthBeforeOptimisation = (maxFileLengthBeforeOptimisation < 0) ? 0 : maxFileLengthBeforeOptimisation;
		
		randomAccessFile.seek(PULL_POINTER_POSITION);
		pullIndex = randomAccessFile.readInt();
		randomAccessFile.seek(PUSH_POINTER_POSITION);
		pushIndex = randomAccessFile.readInt();
	}
	
	public List<Message> getStoredQueue() {
		// TODO
		return null;
	}
	
	public void pull() throws IOException {
		int messageLength;
		
		randomAccessFile.seek(pullIndex);
		messageLength = randomAccessFile.readInt();
		pullIndex += Integer.BYTES + messageLength; // Skipping length of message + the message itself. See above for further informations.
		randomAccessFile.seek(PULL_POINTER_POSITION);
		randomAccessFile.writeInt(pullIndex);
	}
	
	public void push(final Message message) throws IOException {
		byte[] messageBytes = message.getMessageAsBytes();
		final ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES);
		byteBuffer.putInt(messageBytes.length);
		byte[] messageLength = byteBuffer.array();
		byte[] finalMessage = new byte[messageBytes.length + Integer.BYTES];
		
		System.arraycopy(messageLength, 0, finalMessage, 0, messageLength.length);
		System.arraycopy(messageBytes, 0, finalMessage, messageLength.length, messageBytes.length);
		
		randomAccessFile.seek(pushIndex);
		randomAccessFile.write(finalMessage);
		pushIndex += finalMessage.length;
		randomAccessFile.seek(PUSH_POINTER_POSITION);
		randomAccessFile.writeInt(pushIndex);
		
		optimiseIfNeeded();
	}
	
	public boolean optimiseIfNeeded() {
		// Check if needed
		if(pushIndex <= maxFileLengthBeforeOptimisation)
			return false;
		
		// TODO shift all bytes from 
		return true;
	}
	
	public void close() throws IOException {
		if(randomAccessFile != null)
			randomAccessFile.close();
	}
}
