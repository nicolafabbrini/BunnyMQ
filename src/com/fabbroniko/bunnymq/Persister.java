package com.fabbroniko.bunnymq;

import java.io.IOException;

//TODO implement indexing strategy here to allow faster read/write
public class Persister {
	
	private static final Persister INSTANCE = new Persister();
	
	private Persister() {}
	
	public static Persister getInstance() {
		return INSTANCE;
	}
	
	public void open() throws IOException {
		// TODO open the file
	}
	
	public void persist(final String message) throws IOException {
		// TODO write or update
		// TODO if it can't write the message, try closing and reopening the connection, if it fails again then throw the exception
	}
	
	public void delete(final String message) throws IOException {
		// TODO write or update
		// TODO if it can't write the message, try closing and reopening the connection, if it fails again then throw the exception
	}
	
	
	public void close() throws IOException {
		// TODO Close the file 
	}
}
