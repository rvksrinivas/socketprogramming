import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerNew {
    public static final int SERVER_PORT = 5432;
    public static int maxRecordId = 1000;

    static List<Thread> clientList;

    public static void main(String[] args) throws IOException {
        Map addressBookList = new HashMap<String, ClientHandlerNew.AddressBook>();
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        clientList = new ArrayList<>();

        while (true) {
            Socket socket = null;

            try {
                socket = serverSocket.accept();

                System.out.println("A new client is connected : " + socket);

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                System.out.println("Assigning new thread for this client");

                Thread t = new ClientHandlerNew(socket, dis, dos, addressBookList);
                clientList.add(t);
                t.start();

            } catch (Exception e) {
                socket.close();
                e.printStackTrace();
            }
        }
    }
}

class ClientHandlerNew extends Thread {

    final Socket serviceSocket;
    final DataInputStream dis;
    final DataOutputStream dos;

    boolean isUserLogin = false;

    String userName = null;
    Map addressBookList = new HashMap<String, ClientHandlerNew.AddressBook>();

    Map users = new HashMap<String, ClientHandlerNew.UserLogin>();
    File addressBook = null;

    // Constructor
    public ClientHandlerNew(Socket s, DataInputStream dis, DataOutputStream dos, Map addressBookList) {
        this.serviceSocket = s;
        this.dis = dis;
        this.dos = dos;
//        this.addressBookList = addressBookList;
//        this.addressBook = addressBook;
        this.addressBook = new File("./out/production/SocketProgramming/AddressBook.txt");
        this.addressBookList = readAddressBook(addressBookList, addressBook);
        loadLogin();
    }

    private void loadLogin() {
        ClientHandlerNew.UserLogin userLogin = new UserLogin();
        userLogin.setUserID("root");
        userLogin.setPassword("root05");
        userLogin.setStatus("LOGOUT");
        this.users.put(userLogin.getUserID(), userLogin);

        userLogin = new UserLogin();
        userLogin.setUserID("john");
        userLogin.setPassword("john05");
        userLogin.setStatus("LOGOUT");
        this.users.put(userLogin.getUserID(), userLogin);


        userLogin = new UserLogin();
        userLogin.setUserID("david");
        userLogin.setPassword("david07");
        userLogin.setStatus("LOGOUT");
        this.users.put(userLogin.getUserID(), userLogin);


        userLogin = new UserLogin();
        userLogin.setUserID("mary");
        userLogin.setPassword("mary08");
        userLogin.setStatus("LOGOUT");
        this.users.put(userLogin.getUserID(), userLogin);
    }


    @Override
    public void run() {
        String received;
        while (true) {
            try {
                dos.writeUTF("s: ...");
                received = dis.readUTF();
                System.out.println(received);
                dos.writeUTF(received);
                String command = parseInputCommand(received);
                switch (command) {
                    case "ADD":
                        if(isUserLogin) {
                            addressBookList = add(addressBookList, received);
                            dos.writeUTF("s:200 OK");
                            dos.writeUTF("s:The new Record ID is " + ServerNew.maxRecordId);
                        } else {
                            dos.writeUTF("s:401 You are not currently logged in, login first");
                        }
                        break;
                    case "DELETE":
                        if(isUserLogin) {
                            addressBookList = delete(addressBookList, received);
                            dos.writeUTF("s:200 OK");
                        } else {
                            dos.writeUTF("s:401 You are not currently logged in, login first");
                        }
                        break;
                    case "LIST":
                        dos.writeUTF("s:200 OK");
                        dos.writeUTF("s:The list of records in the book:");
                        for (Object entry : addressBookList.values()) {
                            dos.writeUTF(((ClientHandlerNew.AddressBook) entry).getRecordId() + " " + ((ClientHandlerNew.AddressBook) entry).getFirstName() + " " + ((ClientHandlerNew.AddressBook) entry).getLastName() + " " + ((ClientHandlerNew.AddressBook) entry).getPhoneNumber());
                        }
                        break;
                    case "SHUDDOWN":
                        if(isUserLogin) {
                            dos.writeUTF("s:200 OK");
                            for(Thread t: ServerNew.clientList) {
                                ClientHandlerNew cl = (ClientHandlerNew) t;
                                cl.dos.writeUTF("s: 210 the server is about to shutdown");
                                cl.dos.close();
                            }
                            dis.close();
                            dos.close();
                            serviceSocket.close();
                            writeAddressBook(addressBookList, addressBook);
                            System.exit(-1);
                        } else {
                            dos.writeUTF("s:402 User not allowed to execute this command");
                        }
                        break;
                    case "QUIT":
                        dos.writeUTF("s:200 OK");
                        logout(received);
                        break;
                    case "LOGIN":
                        login(received);
                        break;
                    case "LOGOUT":
                        logout(received);
                        break;
                    case "WHO":
                        who(received);
                        break;
                    case "LOOK":
                        look(received);
                        break;
                    case "":
                        break;
                }
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try
        {
            this.dis.close();
            this.dos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void look(String received) throws IOException {
        String type = received.split(" ")[1];
        String value = received.split(" ")[2];
        List<AddressBook> matchItems = new ArrayList<>();
        if(type.equals(1)) {
            for(Object addressBookItem : this.addressBookList.values()) {
                AddressBook addressBook1 = (AddressBook) addressBookItem;
                if(addressBook1.getFirstName().equals(value)) {
                    matchItems.add(addressBook1);
                }
            }
        } else if(type.equals(2)) {
            for(Object addressBookItem : this.addressBookList.values()) {
                AddressBook addressBook1 = (AddressBook) addressBookItem;
                if(addressBook1.getLastName().equals(value)) {
                    matchItems.add(addressBook1);
                }
            }
        } else if(type.equals(3)) {
            for(Object addressBookItem : this.addressBookList.values()) {
                AddressBook addressBook1 = (AddressBook) addressBookItem;
                if(addressBook1.getPhoneNumber().equals(value)) {
                    matchItems.add(addressBook1);
                }
            }
        }
        if(matchItems.size() > 0) {
            this.dos.writeUTF("s:200 OK");
            this.dos.writeUTF("Found " + matchItems.size() + " match");
        } else {
            this.dos.writeUTF("s:404 Your search did not match any records");
        }
        for(Object addressBookItem : matchItems) {
            AddressBook addressBook1 = (AddressBook) addressBookItem;
            this.dos.writeUTF(addressBook1.getRecordId() + " " + addressBook1.getFirstName() + " " + addressBook1.getLastName() + " " + addressBook1.getPhoneNumber());
        }
    }

    private void who(String received) throws IOException {
        this.dos.writeUTF("s:200 OK");
        this.dos.writeUTF(" The list of the active users:");
        for(Object user : this.users.values()) {
            UserLogin userLogin = (UserLogin) user;
            if(userLogin.getStatus().equals("LOGIN")) {
                this.dos.writeUTF(userLogin.getUserID() + "    " + userLogin.getIpAddress());
            }
        }
    }

    private void logout(String received) throws IOException {
        isUserLogin = false;
        this.dos.writeUTF("s:200 OK");
        UserLogin userLogin = (UserLogin) this.users.get(userName);
        if(userLogin != null) {
            userLogin.setStatus("LOGOUT");
            userLogin.setIpAddress(null);
            this.users.put(userName, userLogin);
        }
    }

    private static String parseInputCommand(String line) {
        return line.split(" ")[0];
    }

    public void login(String received) throws IOException {
        String userID = received.split(" ")[1];
        String password = received.split(" ")[2];
        UserLogin userLogin = (UserLogin) this.users.get(userID);
        if(userLogin != null && userLogin.getPassword().equals(password)) {
            isUserLogin = true;
            userName = userID;
            userLogin.setIpAddress(this.serviceSocket.getLocalSocketAddress().toString());
            userLogin.setStatus("LOGIN");
            this.users.put(userID, userLogin);
            this.dos.writeUTF("s: 200 OK");
        }  else {
            this.dos.writeUTF("s: 410 Wrong UserID or Password");
        }
    }

    public static Map<String, ClientHandlerNew.AddressBook> add(Map addressBookList, String record) {
        ClientHandlerNew.AddressBook addressBook1 = new ClientHandlerNew.AddressBook();
        ServerNew.maxRecordId = ServerNew.maxRecordId + 1;
        addressBook1.setRecordId(ServerNew.maxRecordId);
        addressBook1.setFirstName(record.split(" ")[1]);
        addressBook1.setLastName(record.split(" ")[2]);
        addressBook1.setPhoneNumber(record.split(" ")[3]);
        addressBookList.put(addressBook1.getRecordId(), addressBook1);
        return addressBookList;
    }

    public static Map delete(Map addressBookList, String record) {
        String recordId = record.split(" ")[1];
        addressBookList.remove(recordId);
        return addressBookList;
    }

    public static Map readAddressBook(Map addressBookList, File addressBook) {
        BufferedReader is = null;
        String line;
        int maxRecordIdInput = 0;
        int preVal = 0;
        try {
            is = new BufferedReader(new FileReader(addressBook));

            while ((line = is.readLine()) != null) {
                if(!line.startsWith("RecordID")) {
                    ClientHandlerNew.AddressBook record = new ClientHandlerNew.AddressBook();
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
            ServerNew.maxRecordId = maxRecordIdInput;
        } catch (IOException e) {
            System.err.println("Error cause while reading file" + e);
        }

        return addressBookList;
    }
    public static void writeAddressBook(Map addressBookList, File addressBook) {
        BufferedWriter os = null;
        String line;
        try {
            os = new BufferedWriter(new FileWriter(addressBook));
            for (Object entry : addressBookList.values()) {
                line = ((ClientHandlerNew.AddressBook) entry).getRecordId() + " " + ((ClientHandlerNew.AddressBook) entry).getFirstName() + " " + ((ClientHandlerNew.AddressBook) entry).getLastName() + " " + ((ClientHandlerNew.AddressBook) entry).getPhoneNumber();
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

    public static class UserLogin {
        private String userID;
        private String password;
        private String status;
        private String ipAddress;

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }


        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }


    }

}

