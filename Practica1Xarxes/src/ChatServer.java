// Individual, nom: Paula Silland

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
            System.out.println("Servidor iniciant. Esperant conexió...");

            Socket socket = serverSocket.accept();
            System.out.println("Conexió acceptada.");

            //creem el socket i li passem el nom del host i el port
            DataInputStream inStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            //creem el thread que s'encarrega de llegir el que escriu l'usuari
            Thread inputThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while(true) {
                            //aqui li passem el missatge que volem enviar
                            String message = reader.readLine();
                            if(!message.isEmpty()) {
                                outStream.writeUTF(message);
                                outStream.flush();
                                if(message.equals("FI")) {
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
            //iniciem el thread
            inputThread.start();

            while(true) {

                String message = inStream.readUTF();
                if(!message.isEmpty()) {
                    System.out.println("Client: \"" + message + "\"");
                    if(message.equals("FI")) {
                        socket.close();
                        break;
                    }
                }
            }
            //tanquem el socket
            inputThread.interrupt();
            serverSocket.close();
            System.out.println("Servidor finalitzat.");

        } catch (IOException e) {
            //mostrem el missatge d'error
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}
