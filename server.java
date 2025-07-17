import java.io.*;
import java.net.*;
import java.util.*;

public class server {
    private static final int PORT = 12345;
    private static Map<String, PrintWriter> clientMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        while (true) {
            Socket socket = serverSocket.accept();
            new ClientHandler(socket).start();
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private String clientName;
        private BufferedReader in;
        private PrintWriter out;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // First message from client is its name
                clientName = in.readLine();
                synchronized (clientMap) {
                    clientMap.put(clientName, out);
                }

                System.out.println(clientName + " connected.");

                String msg;
                while ((msg = in.readLine()) != null) {
                    // Format: TO:ClientName|Message text
                    if (msg.startsWith("TO:")) {
                        int sep = msg.indexOf('|');
                        if (sep != -1) {
                            String targetName = msg.substring(3, sep);
                            String messageText = msg.substring(sep + 1);

                            PrintWriter targetOut;
                            synchronized (clientMap) {
                                targetOut = clientMap.get(targetName);
                            }

                            if (targetOut != null) {
                                targetOut.println("From " + clientName + ": " + messageText);
                            } else {
                                out.println("User " + targetName + " not found.");
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(clientName + " disconnected.");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {}
                synchronized (clientMap) {
                    clientMap.remove(clientName);
                }
            }
        }
    }
}
