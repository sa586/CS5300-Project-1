==Project 1==

==Request Flow
===New Session
1. Initialize session
2. Send session to all servers in Group, wait for NQ responses
3. Return the session that was sent to the Group, with the first NQ responses added to it
4. Save the session to the client's cookie
===Existing Session
1. Retrieve session id, version and servers from cookie
2. Send get request to listed servers
3. After first server responds, build a session and send it back to the client
4. If no server responds affirmatively, initialize a session and follow the new session flow
5. Update session with new data, and issue put to servers in session. If there are less than NQ servers responding, add additional servers from the server list

==Structure
The main webserver class is Project1 in the default package.
The Startup class initializes the static variables in Project1. This is done when AWS does a health check on the instance, and starts the RPCServer and other threads.

===Project1
Project1 is responsible for managing what happens to a request when it is received by the server. It also manages what happens at startup and shutdown.
At startup it starts SessionCleaner, and at shutdown it cleans up SessionCleaner.
On a POST or GET request, it looks for a session by first checking the local session table and then by issuing an RPC get call. 
It modifies the contents of the session appropriately and outputs the HTML of the form and desired data as well. At the end, it puts the session into its local SSM and issues an RPC put call to store the session.

===Server
Server object that has an IP and a port of a RPCServer.

===GroupMembership
Keep track of all servers in group.

===RPCClient/RPCServer
RPCClient contains the probe, get and put functions. All the functions follow a similar layout. The necessary message is marshalled into a byte[] and sent as a UDP packet.
The client then waits for a response and unmarshals it. If the response has the appropriate callid, it processes the response (probe returns successfully, get sets the session data, put returns successfully after the right amount of servers responded).
RPCServer loops continually and when it receives a request, it calls computeResponse() to process the request. computeResponse parses the request, performs the necessary actions (acknowledging a probe, retrieving a session for get, and adding a session for put), and returns a response to be sent back.

===Session
Session is responsible for the session attributes. As specified in the assignment, a Session has a sessionID, version, timestamp and locations. It also is able to store data on the server side, and thus has a HashTable on the server. This HashTable is not threadsafe, as we assume that sessions cannot be accessed concurrently.

===SessionManager
SessionManager stores the Session objects into a threadsafe hashtable. It contains functions to add/delete sessions and to retrieve sessions with cookie information.
getAndIncrement retrieves the session for a given sessionID (first by checking the local table and then by issuing the RPC get call). If it finds it and the version numbers match, it increments the version number and sets the cookie with the new version number. If it does not find it, it initializes a session with a globally unique id (GUID). This ensures that the sessionIDs will never conflict.
destroy looks in the session table after reading the session cookie. It then deletes the session from the table and expires the session cookie.
startCleaner starts SessionCleaner and has it run in specified intervals.
cleanup cancels the scheduling of SessionCleaner

===SessionCleaner
SessionCleaner iterates through the session table and checks the timestamp on the sessions. If the timestamp is older than the expire time, it delete the session from the table.

==How To Run
As a .war file, the project can be deployed to a web server or platform such as Amazon's Elastic Beanstalk. Simply update the provided CS5300.war file onto the service and run it. Doing so will deploy the application to http://yoururl/Project1