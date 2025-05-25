package aki;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class ChatServer {
    private static final int PORT = 12345;
    private static ArrayList<PrintWriter> clientOutputStreams;
    private static Map<String, PrintWriter> clientMap; // 存储客户端ID和对应的输出流

    public static void main(String[] args) {
        clientOutputStreams = new ArrayList<>();
        clientMap = new HashMap<>();

        try (ServerSocket serverSock = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSock.accept();
                String clientId = UUID.randomUUID().toString(); // 生成唯一ID
                
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);
                clientMap.put(clientId, writer);
                
                // 先向客户端发送其ID
                writer.println("[Server]YourID:" + clientId);
                writer.flush();

                Thread clientHandler = new Thread(new ClientHandler(clientSocket, clientId));
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Scanner input;
        private String clientId;

        public ClientHandler(Socket clientSocket, String clientId) {
            this.clientId = clientId;
            try {
                input = new Scanner(clientSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;
            while (input.hasNextLine()) {
                message = input.nextLine();
                System.out.println("Received from " + clientId + ": " + message);
                tellEveryone(clientId, message);
            }
        }

        private void tellEveryone(String senderId, String message) {
            for (Map.Entry<String, PrintWriter> entry : clientMap.entrySet()) {
                String recipientId = entry.getKey();
                PrintWriter writer = entry.getValue();
                
                // 格式: [SenderID]消息内容
                writer.println("[" + senderId + "]" + message);
                writer.flush();
            }
        }
    }
}