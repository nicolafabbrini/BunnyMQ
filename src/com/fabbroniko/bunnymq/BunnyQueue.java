package com.fabbroniko.bunnymq;

import java.util.Queue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BunnyQueue <T> {

	final private Queue<T> queue;
	final private Sync sync;
	private T currentElement;
	
	public BunnyQueue() {
		this.queue = new SynchronousQueue<>();
		this.sync = new Sync();
		this.currentElement = null;
	}
	
	public void add(final T element) {
		if(element == null)
			throw new NullPointerException("Can't insert a null element.");
		
		synchronized(sync) {
			
			
			// Notify the thread waiting to get an element (if it exists);
			sync.notify();
		}
	}
	
	/**
	 * Gets the first element of the queue, the thread is blocked
	 * until ther is an element available.
	 * @return The first element of the queue.
	 */
	public T get() {
		synchronized(sync) {
			// Checking if the previous element has been acked, if not throw an exception.
			if(currentElement != null)
				throw new IllegalStateException("Last element must be acked before being able to get the another element from the queue.");
			
			while(queue.peek() == null) {
				try {
					sync.wait();
				} catch (final InterruptedException e) {
					System.out.println("[BunnyQueue] Wait has been interrupted.");
					return null;
				}
			}
			
			currentElement = queue.element();
			return currentElement;
		}
	}
	
	/**
	 * Nack is a negative ack (meaning that it's possible to get another element after this call).
	 * The last element is added back at the end of the queue.
	 * @return true if there was enough space to add the element back to the queue, false otherwise.
	 */
	public void nack() {
		if(currentElement == null)
			throw new IllegalStateException("The current element shouldn't be null.");
		
		// TODO If persistence is enabled, remove first element from the queue in the disk;
		
		if(!queue.offer(currentElement)) {
			// TODO unable to insert the element - if persistence is enabled, store it as the last element of the queue in the disk.
		}
		
		// Setting the current element to null allows the queue to be fetched again.
		currentElement = null;
	}
	
	public void ack() {
		if(currentElement == null)
			throw new IllegalStateException("The current element shouldn't be null.");
		
		// TODO if persistence is enabled, remove fist element from the queue in the disk;
		currentElement = null;
	}
	
	private class Sync {}
}
