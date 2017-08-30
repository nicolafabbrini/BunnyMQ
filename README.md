# BunnyMQ

## File Structure

The persistence file is accessed with [RandomAccessFile](https://docs.oracle.com/javase/8/docs/api/java/io/RandomAccessFile.html).

| Pull Pointer | Push Pointer | Message | Message | Message |
| --- | --- | --- | --- | --- |
| 4 Bytes | 4 Bytes | N Bytes | N Bytes | N Bytes

Every message stored in the file is composed as follows:

| Length | Payload |
| --- | --- |
| 4 Bytes | N Bytes |

Since Every Message (Payload) inside the queue also has and expireTime (when it actually enter the queue and available for the consumer to take()), so the payload is structured as follows:

| Expire timestamp | Data |
| --- | --- |
| 8 Bytes | N Bytes |

So if a message of N Bytes is pushed to a [DelayQueue](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/DelayQueue.html), this will be persisted in the hard disk as a message structured as follows:

| Length | Expire timestamp | Data |
| --- | --- | --- |
| 4 Bytes | 8 Bytes | N Bytes |
