import java.io.*;
import java.net.*;

public class client {
    public static void main(String[] args) throws IOException {
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter your name: ");
        String name = keyboard.readLine();

        Socket socket = new Socket("192.168.14.45", 12345); // Replace with your server IP
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Send your name to the server
        out.println(name);

        // Thread to listen for messages from the server
        new Thread(() -> {
            String msgFromServer;
            try {
                while ((msgFromServer = in.readLine()) != null) {
                    System.out.println(msgFromServer);
                }
            } catch (IOException e) {
                System.out.println("Disconnected from server.");
            }
        }).start();

        // Read messages from keyboard and send
        String input;
        while ((input = keyboard.readLine()) != null) {
            out.println(input); // Format: TO:Client2|Hello!
        }
    }
}
