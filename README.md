Instructions for compilation of the server and client source code:


Server side

Execution command:
~$ makefile 
~$ java ServerMulti

where:
a. makefile will compile the .java file
b. java ServerMulti is for running the executable of server source code


Client side

Execution command:
~$ makefile 
~$ java ClientMulti

where:
a. java ClientMulti is for running the executable of client source code
b. (ADD FirstName LastName PhoneNumber) is to add new item to the addressbook
c. (DELETE RecordID) is to delete the record from the addressbook
d. (LIST) is list the item in the addressbook
e. (QUIT) is to stop the client 
f. (SHUDDOWN) is to stop the server
g. (LOGIN) is to execute the privilege commands (ADD, DELETE and SHUTDOWN)
h. (LOGOUT) is remove the pribilege as mentioned above
i. (WHO) is to list the active login user list
j. (LOOK) is to lookup address book by 1 - firstname, 2-lastname, 3-phonenumber


