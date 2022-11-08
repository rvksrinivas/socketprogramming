import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientNew {
    public static void main(String[] args) throws IOException {
//        Socket clientSocket = null;
//        DataOutputStream dos = null;
//        DataInputStream dis = null;
//        Scanner scn = null;

//        if (args.length < 1) {
//            System.out.println("Usage: client <Server ID Address>");
//            System.exit(1);
//        }

        try {
            Scanner scn = new Scanner(System.in);
            InetAddress ip = InetAddress.getByName("localhost");
            Socket clientSocket = new Socket(ip, ServerNew.SERVER_PORT);
            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());



//        if (clientSocket != null && dos != null && dis != null) {
            while (true) {
                System.out.println(dis.readUTF());
                String tosend = scn.nextLine();
                dos.writeUTF(tosend);

                if (tosend.equals("QUIT")) {
                    System.out.println("Closing this connection : " + clientSocket);
                    clientSocket.close();
                    System.out.println("Connection closed");
                    break;
                }
                String received = dis.readUTF();
                System.out.println(received);
            }
            scn.close();
            dis.close();
            dos.close();
//        }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: hostname");
        } catch (Exception e) {
            System.err.println("Couldn't get I/O for the connection to: hostname" + e);
        }
    }
}
