import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe que implementa un servidor de xat. Espera connexions de clients i permet
 * establir una conversa bidireccional entre ells.
 */
public class ChatServer {

    /**
     * Mètode principal que executa el servidor de xat.
     * @param args Paràmetres de línia de comandament (no s'utilitzen en aquest cas).
     */
    public static void main(String[] args) {

        try {
            // Creem un servidor de sockets i l'associem al port 1234.
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Servidor iniciant. Esperant connexió...");

            // Esperem que un client es connecti al servidor i acceptem la connexió.
            Socket socket = serverSocket.accept();
            System.out.println("Connexió acceptada.");

            // Creem les entrades i sortides de dades per comunicar-nos amb el client.
            DataInputStream inStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // Creem un fil d'execució que escolta les dades que introdueix l'usuari.
            Thread inputThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Llegim les dades que introdueix l'usuari fins que es tanqui el fil.
                        while (!Thread.interrupted()) {
                            String message = reader.readLine();
                            if (!message.isEmpty()) {
                                // Enviem les dades al client i comprovem si es vol tancar la connexió.
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

            // Iniciem el fil que escolta les dades de l'usuari.
            inputThread.start();

            // Llegim les dades que envia el client fins que es tanqui la connexió.
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

            // Aturem el fil que escolta les dades de l'usuari i tanquem el servidor de sockets.
            inputThread.interrupt();
            serverSocket.close();
            System.out.println("Servidor finalitzat.");

        } catch (IOException e) {
            // Mostrem el missatge d'error si hi ha problemes amb el servidor.
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}
