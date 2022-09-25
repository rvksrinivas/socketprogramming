import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    public static File addressBook = null;
    public static final int SERVER_PORT = 5432;

    public static int maxRecordId = 1000;

    public static void main(String[] args) {

        Map addressBookList = new HashMap<String, AddressBook>();

        ServerSocket myServerice = null;
        String line;
        BufferedReader is;
        PrintStream os;
        Socket serviceSocket = null;

        // Try to open a server socket
        try {
            addressBook = new File("./out/production/SocketProgramming/AddressBook.txt");
            addressBookList = readAddressBook(addressBookList);
            myServerice = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            System.err.println(e);
        }

        // Create a socket object from the ServerSocket to listen and accept connections
        // Open input and output steams

        while (true) {
            try{
                serviceSocket = myServerice.accept();
                is = new BufferedReader(new InputStreamReader(serviceSocket.getInputStream()));
                os = new PrintStream(serviceSocket.getOutputStream());

                // As long as we receive data, echo that data back to the client.
                while ((line = is.readLine()) != null) {
                    System.out.println(line);
                    String command = parseInputCommand(line);
                    switch (command) {
                        case "ADD":
                            addressBookList = add(addressBookList, line);
                            os.println("s:200 OK");
                            os.println("s:The new Record ID is " + maxRecordId);
                            break;
                        case "DELETE":
                            delete(addressBookList, line);
                            os.println("s:200 OK");
                            break;
                        case "LIST":
                            os.println("s:200 OK");
                            os.println("s:The list of records in the book:");
                            for(Object entry: addressBookList.values()) {
                                os.println(((Server.AddressBook)entry).getRecordId() + " " + ((Server.AddressBook)entry).getFirstName() + "" + ((Server.AddressBook)entry).getLastName() + " " + ((Server.AddressBook)entry).getPhoneNumber());
                            }
                            break;
                        case "SHUDDOWN":
                            os.println("s:200 OK");
                            is.close();
                            os.close();
                            serviceSocket.close();
                            writeAddressBook(addressBookList);
                            System.exit(-1);
                            break;
                        case "QUIT":
                            os.println("s:200 OK");
                            break;
                    }
                }
                // close input and output stream and socket
                is.close();
                os.close();
                serviceSocket.close();
            }catch (IOException e) {
                System.out.println(e);
            } finally {
                try {
                    serviceSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    private static String parseInputCommand(String line) {
        return line.split(" ")[0];
    }

    public static Map<String, AddressBook> add(Map addressBookList, String record) {
        AddressBook addressBook1 = new AddressBook();
        maxRecordId = maxRecordId + 1;
        addressBook1.setRecordId(maxRecordId);
        addressBook1.setFirstName(record.split(" ")[1]);
        addressBook1.setLastName(record.split(" ")[2]);
        addressBook1.setPhoneNumber(record.split(" ")[3]);
        addressBookList.put(addressBook1.getRecordId(), addressBook1);
        return addressBookList;
    }

    public static void delete(Map addressBookList, String record) {
        String recordId = record.split(" ")[1];
        addressBookList.remove(recordId);
    }

    public static Map readAddressBook(Map addressBookList) {
        BufferedReader is = null;
        String line;
        int maxRecordIdInput = 0;
        int preVal = 0;
        try {
            is = new BufferedReader(new FileReader(addressBook));

            while ((line = is.readLine()) != null) {
                if(!line.startsWith("RecordID")) {
                    AddressBook record = new AddressBook();
                    preVal = Integer.parseInt(line.split(" ")[0]);
                    if(preVal > maxRecordIdInput) {
                        maxRecordIdInput = preVal;
                    }
                    record.setRecordId(preVal);
                    record.setFirstName(line.split(" ")[1]);
                    record.setLastName(line.split(" ")[2]);
                    record.setPhoneNumber(line.split(" ")[3]);
                    addressBookList.put(record.getRecordId(), record);
                }
            }
            maxRecordId = maxRecordIdInput;
        } catch (IOException e) {
            System.err.println("Error cause while reading file" + e);
        }

        return addressBookList;
    }

    public static void writeAddressBook(Map addressBookList) {
        BufferedWriter os = null;
        String line;
        try {
            os = new BufferedWriter(new FileWriter(addressBook));
            for(Object entry: addressBookList.values()) {
                line = ((Server.AddressBook)entry).getRecordId() + " " + ((Server.AddressBook)entry).getFirstName() + "" + ((Server.AddressBook)entry).getLastName() + " " + ((Server.AddressBook)entry).getPhoneNumber();
                os.write(line + "\n");
            }
            os.close();
        } catch (IOException e) {
            System.err.println("Error caused while writing to file" + e);
        }
    }

    public static class AddressBook {
        private int recordId;
        private String firstName;
        private String lastName;
        private String phoneNumber;

        public int getRecordId() {
            return recordId;
        }

        public void setRecordId(int recordId) {
            this.recordId = recordId;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }


}
