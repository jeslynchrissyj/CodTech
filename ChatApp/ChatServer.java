import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private static final int PORT = 12345;
    // thread-safe list for client handlers
    private static CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("Chat server starting on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New connection from " + socket.getRemoteSocketAddress());
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);                       // add to list
                broadcast(handler.getName() + " joined the chat", handler);
                new Thread(handler).start();                // start handler thread
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // send message to all clients except 'exclude' (can be null)
    static void broadcast(String message, ClientHandler exclude) {
        for (ClientHandler client : clients) {
            if (client != exclude) {
                client.sendMessage(message);
            }
        }
    }

    static void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String name = "Anonymous";

        ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true); // autoFlush
                out.println("Enter your name:");           // ask client for name
                this.name = in.readLine();                 // read name
                if (this.name == null || this.name.trim().isEmpty()) {
                    this.name = "Anonymous";
                }
                out.println("Welcome " + name + "! Type /quit to leave.");
            } catch (IOException e) {
                closeEverything();
            }
        }

        public String getName() {
            return name;
        }

        @Override
        public void run() {
            String msg;
            try {
                while ((msg = in.readLine()) != null) {
                    if (msg.equalsIgnoreCase("/quit")) {
                        break;
                    }
                    System.out.println(name + ": " + msg);
                    broadcast(name + ": " + msg, this);
                }
            } catch (IOException e) {
                // ignore or log
            } finally {
                closeEverything();
            }
        }

        void sendMessage(String message) {
            out.println(message);
        }

        void closeEverything() {
            try {
                if (socket != null) socket.close();
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {}
            removeClient(this);
            broadcast(name + " left the chat", this);
            System.out.println(name + " disconnected.");
        }
    }
}
