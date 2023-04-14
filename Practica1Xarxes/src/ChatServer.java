import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Servidor iniciant. Esperant connexió...");

            Socket socket = serverSocket.accept();
            System.out.println("Connexió acceptada.");

            DataInputStream inStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            Thread inputThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!Thread.interrupted()) {
                            String message = reader.readLine();
                            if (!message.isEmpty()) {
                                outStream.writeUTF(message);
                                outStream.flush();
                                if (message.equals("FI")) {
                                    System.out.println("Connexió tancada.");
                                    socket.close();
                                    break;
                                }
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Error al enviar missatge.");
                    }
                }
            });

            inputThread.start();

            while (!Thread.interrupted()) {
                String message = inStream.readUTF();
                if (!message.isEmpty()) {
                    System.out.println("Client: \"" + message + "\"");
                    if (message.equals("FI")) {
                        System.out.println("Connexió tancada.");
                        socket.close();
                        break;
                    }
                }
            }

            inputThread.interrupt();
            serverSocket.close();
            System.out.println("Servidor finalitzat.");

        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}
