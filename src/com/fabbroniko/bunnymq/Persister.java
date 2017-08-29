package com.fabbroniko.bunnymq;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Persister {
	
	private RandomAccessFile randomAccessFile;
	
	public Persister(final String fileLocation) throws FileNotFoundException {
		this.randomAccessFile = new RandomAccessFile(fileLocation, "rwd"); // Read/Write and immediately synch to the underlying storage system
	}
	
	public void open() throws IOException {
		
	}
	
	public void persist(final Message message) throws IOException {
		// TODO write or update
		// TODO if it can't write the message, try closing and reopening the connection, if it fails again then throw the exception
	}
	
	public void delete(final String message) throws IOException {
		// TODO write or update
		// TODO if it can't write the message, try closing and reopening the connection, if it fails again then throw the exception
	}
	
	
	public void close() throws IOException {
		randomAccessFile.close();
	}
}
