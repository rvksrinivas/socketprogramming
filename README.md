Instructions for compilation of the server and client source code:


Server side

Execution command:
~$ makefile 
~$ java Server

where:
a. makefile will compile the .java file
b. java Server is for running the executable of server source code


Client side

Execution command:
~$ makefile 
~$ java Client ServerIPAddress

where:
a. java Client is for running the executable of client src code
b. ServerIPAddress is the IP address of the server
c. (ADD FirstName LastName PhoneNumber) is to add new item to the addressbook
d. (DELETE RecordID) is to delete the record from the addressbook
e. (LIST) is list the item in the addressbook
f. (QUIT) is to stop the client 
g. (SHUDDOWN) is to stop the server


