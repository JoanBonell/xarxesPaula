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

            DataInputStream inStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            Thread inputThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.interrupted()) {
                        try {
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
                        } catch (IOException e) {
                            System.out.println("Error al enviar missatge.");
                            break;
                        }
                    }
                }
            });

            inputThread.start();

            while (true) {
                String message = inStream.readUTF();
                if (!message.isEmpty()) {
                    System.out.println("Servidor: \"" + message + "\"");
                    if (message.equals("FI")) {
                        System.out.println("Connexió tancada.");
                        socket.close();
                        break;
                    }
                }
            }

            inputThread.interrupt();

        } catch (IOException e) {
            System.out.println("Error al connectar el servidor.");
        }
    }
}
