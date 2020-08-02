# Receiver

A silly android app that allows you to send notifications to your phone. Definitely needs more testing.

### Usage

Open the app and toggle the switch on. This will start a socket that listens on port 9999 of your phone.
Anytime the socket accepts a new connection, it will only read the first 3 lines and interpret them as follows:
First line is the title of the notification, second line is the text and the third line is the priority (an integer).
No response is sent back.

Examples of sending notifications (using openbsd netcat):

```
echo -n "<Title>\n<text>\n <priority> \n <the rest \n doesn't matter>" | nc -q 1 <ip address of your phone> 9999
```

```
echo -n "Hello\nSomeone is at the door\n 1" | nc -q 1 192.168.2.27 9999
```

#### Priorities:
    High priority: 1
    Low priority: -1
    Default priority: 0
    Max priority: 2
    Min priority: -2
