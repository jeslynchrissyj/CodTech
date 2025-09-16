import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER = "127.0.0.1";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        String host = (args.length > 0) ? args[0] : SERVER;
        int port = (args.length > 1) ? Integer.parseInt(args[1]) : PORT;

        try (Socket socket = new Socket(host, port);
             BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("Connected to chat server " + host + ":" + port);

            // Thread to print messages from server
            Thread reader = new Thread(() -> {
                String serverMsg;
                try {
                    while ((serverMsg = serverIn.readLine()) != null) {
                        System.out.println(serverMsg);
                    }
                } catch (IOException e) {
                    // Connection closed
                }
            });
            reader.setDaemon(true);
            reader.start();

            // Send user-typed lines to server
            String line;
            while ((line = userIn.readLine()) != null) {
                serverOut.println(line);
                if (line.equalsIgnoreCase("/quit")) {
                    break;
                }
            }

            System.out.println("You left the chat.");
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }
}
