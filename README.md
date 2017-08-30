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
