import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatClient {

    public static void main(String[] args) throws IOException {

        try {
            Socket socket = new Socket("localhost", 1234);
            System.out.println("Connexió establerta.");

            //creem el socket i li passem el nom del host i el port
            DataInputStream inStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            Thread inputThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.interrupted()) {
                        try {
                            //aqui li passem el missatge que volem enviar
                            String message = reader.readLine();
                            if (!message.isEmpty()) {
                                outStream.writeUTF(message);
                                outStream.flush();
                                if (message.equals("FI")) {
                                    break;
                                }
                            }
                        } catch (IOException e) {
                            System.out.println("Error al enviar missatge.");
                            break;
                        }
                    }
                    try {
                        socket.close();
                        System.out.println("Client finalitzat.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            inputThread.start();

            while (true) {
                //aqui li llegim el missatge que ens envia el servidor
                String message = inStream.readUTF();
                if (!message.isEmpty()) {
                    System.out.println("Servidor: \"" + message + "\"");
                    if (message.equals("FI")) {
                        break;
                    }
                }
            }
            //tanquem el socket
            inputThread.interrupt();

        } catch (IOException e) {
            System.out.println("Error al connectar el servidor.");
        }
    }
}
