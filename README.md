# BunnyMQ

## Queue Support

- [DelayQueue](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/DelayQueue.html): Push a message with a delay value - it will actually be pushed in the queue after the specified delay.
- [SynchronousQueue](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/SynchronousQueue.html): Thread-safe queue
- [LinkedList](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedList.html): Simple not synchronized queue.

## File Structure

The persistence file is accessed with [RandomAccessFile](https://docs.oracle.com/javase/8/docs/api/java/io/RandomAccessFile.html) that allows read and write in a specific section of the file (after setting up the pointer).

| Pull Pointer | Push Pointer | Message | Message | Message |
| --- | --- | --- | --- | --- |
| 4 Bytes | 4 Bytes | N Bytes | N Bytes | N Bytes

Every message stored in the file is composed as follows:

| Length | Payload |
| --- | --- |
| 4 Bytes | N Bytes |

If the message (Payload) is set up for a [DelayQueue](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/DelayQueue.html) it also has an expireTime (when it actually enter the queue and available for the consumer to take())and the final payload is structured as follows:

| Expire timestamp | Data |
| --- | --- |
| 8 Bytes | N Bytes |

So if a message of N Bytes is pushed to a [DelayQueue](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/DelayQueue.html), this will be persisted in the hard disk as a message structured as follows:

| Length | Expire timestamp | Data |
| --- | --- | --- |
| 4 Bytes | 8 Bytes | N Bytes |

## Persist operations

Operations are performed using 2 pointers: pullPointer and pushPointer.

- Pull Pointer: pointing to the current head of the queue, the position of this pointer indicates the first byte of the message (first byte of the int representing the length of the message).
- Push Pointer: pointing to the byte after the last byte of the last message (the position where a new push can write the new message)

These pointers must always point to the head and queue of the current valid queue.

### Startup

Initialise the persister:

- Open the file.
- Load the pull pointer (position 0, 4 bytes)
- Load the push pointer (position 4, 4 bytes)

### Get all messages

Create a list of messages reading the informations in the file:

- 

### Optimisation

Optimisation is performed when pushPointer > maxFileSizeBeforeOptimisation because every pull operation shifts the pointer without deleting the actual data from the file.
The optimisation consists in shifting the messages from the current pullPointer position to the beginning of the file and updating the pointers accordingly.

- Shift bytes from pullPointer to pushPointer at the beginning of the file.
- update and save new pullPointer
- update and save new pushPointer

That guarantees that the file will not grow up more than the specified size unless its capacity is too low for its current load of work.

Example

Before optimisation:

| Pull Pointer | Push Pointer | Old Message | Old Message | Message | Message | Message |  |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 4 Bytes | 4 Bytes | N Bytes | N Bytes | N Bytes | N Bytes | N Bytes |  |
|  |  |  |  | ^--- Pull pointer |  |  | Push pointer ---^ |

After optimisation:

| Pull Pointer | Push Pointer | Message | Message | Message |  |
| --- | --- | --- | --- | --- | --- |
| 4 Bytes | 4 Bytes | N Bytes | N Bytes | N Bytes |  |
|  |  | ^--- Pull pointer |  |  | Push pointer ---^ |

### Push

- Save the new message in the current pushPointer position.
- Calculate and save the new pushPointer position.

### Pull

Pull operations shift the pull pointer from the current message to the next one (pullPointer = pullPointer + messageLength) because we are not allowed to delete partial bytes - that means that the file is going to grow up indefinetely. 
It's possible to set up the persister to check the file dimension for every push and if its length is > than X bytes, then it will perform the optimisation (see above operation).

- Save the new pullPointer inside the file.

## Possible issues that need fixing

I/O operations includes pull/push messages from the file (
