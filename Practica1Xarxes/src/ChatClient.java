import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Aquesta classe implementa un client de xat que es connecta a un servidor de xat
 * mitjançant un socket. El programa permet que l'usuari envii missatges al servidor
 * i també rep missatges del servidor.
 */
public class ChatClient {

    /**
     * Aquesta funció principal és l'entrada del programa. Crea un socket per connectar-se
     * al servidor de xat i configura els fluxos d'entrada i sortida del socket.
     * També crea un fil per llegir les dades d'entrada del socket i un altre fil per
     * llegir les dades d'entrada del teclat de l'usuari.
     *
     * @param args els arguments de la línia de comandes (no s'utilitzen en aquest programa)
     * @throws IOException si hi ha problemes de connexió o de fluxos d'entrada/sortida
     */
    public static void main(String[] args) throws IOException {

        try {
            Socket socket = new Socket("localhost", 1234);
            System.out.println("Connexió establerta.");

            DataInputStream inStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // Fil per llegir les dades d'entrada del socket
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

            // Bucle per llegir les dades d'entrada del socket
            while (true) {
                String message = inStream.readUTF();
                if (!message.isEmpty()) {
                    System.out.println("Servidor: \"" + message + "\"");
                    if (message.equals("FI")) {
                        System.out.println("Connexió tancada.");
                        socket.close();
                        inputThread.interrupt();
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
