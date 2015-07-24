# Follower Maze Server
This application will act as a server and handle two kind of clients.

- *event source*: It will send you a
stream of events which may or may not require clients to be notified
- *user clients*: Each one representing a specific user,
these wait for notifications for events which would be relevant to the
user they represent

## Communication Protocol for clients
The protocol used by the clients is string-based (i.e. a `CRLF` control
character terminates each message). All strings are encoded in `UTF-8`.

The *event source client* must connect to the port specified in ```src/main/resources/application.conf``` file and can start sending
events as soon as the connection is accepted. Default port is *9090*.

*User clients* must must connect to the port specified in ```src/main/resources/application.conf``` file. Default port is *9099*. As soon
as the connection is accepted, they must send to the server the ID of
the represented user, so that the server knows which events to
inform them of. For example, once connected a *user client* may send down:
`2932\r\n`, indicating that they are representing user 2932.

After the identification is sent, the *user client* starts waiting for
events to be sent to them. Events coming from *event source* should be
sent to relevant *user clients* exactly like read, no modification is
required or allowed.

## Events received from event source 
There are five possible events. The table below describe payloads
sent by the *event source* and what they represent:

| Payload    | Sequence #| Type         | From User Id | To User Id |
|------------|-----------|--------------|--------------|------------|
|666|F|60|50 | 666       | Follow       | 60           | 50         |
|1|U|12|9    | 1         | Unfollow     | 12           | 9          |
|542532|B    | 542532    | Broadcast    | -            | -          |
|43|P|32|56  | 43        | Private Msg  | 32           | 56         |
|634|S|32    | 634       | Status Update| 32           | -          |

Events may generate notifications for *user clients*. **If there is a
*user client* ** connected for them, these are the users to be
informed for different event types:

* **Follow**: Only the `To User Id` should be notified
* **Unfollow**: No clients should be notified
* **Broadcast**: All connected *user clients* should be notified
* **Private Message**: Only the `To User Id` should be notified
* **Status Update**: All current followers of the `From User ID` should be notified

If there are no *user client* connected for a user, any notifications
for will be silently ignored. *user clients* will be notified of
events **in the correct order**, regardless of the order in which the
*event source* sent them.

## How to run
For running this application you must have JVM installed as this application in implemented in Scala. 
You can run the application in two ways.

### Using [sbt](http://www.scala-sbt.org/) 
For this approach, you must need to install sbt, which can be done by following given link. 
Once you are done with the installation you just have to go to the project's base directory and run ```sbt run``` which
will start server on specified ports provided by ```src/main/resources/application.conf``` file to handle client connections

### Using jar file (follower-maze-server-assembly-1.0.jar) provided with coding challenge
For this approach, you just need to run a command ```jar -jar follower-maze-server-assembly-1.0.jar```
which will start server to handler client connections.

## How to run tests and package application
For both of these tasks you need to install [sbt](http://www.scala-sbt.org/) which can be done by following given link.

Tests are ran by ```sbt test``` command from project's base directory

To package an application, run ```sbt assembly``` command from project's base directory. This will create a jar file ```follower-maze-server-assembly-1.0.jar```
in directory ```target/scala-2.11```  
