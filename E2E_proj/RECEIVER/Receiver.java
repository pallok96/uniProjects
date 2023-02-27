import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import javax.imageio.ImageIO;


public class Receiver {
    private Socket socket = null;           // inizializziamo socket e i canali di input e output 
    private ServerSocket server = null;
    private static DataInputStream inReceiver = null;
    private DataOutputStream outReceiver = null;

    byte[] messaggio;
    private byte[] decifrato;
    private String Sk;
    String Pk;
    private String[] nepqd;
    private byte[] AESkey = new byte[16];
    byte[] IV = new byte[16];
    int session_counter = 1;

    public Receiver(int port) throws IOException {
        server = new ServerSocket(port);
        System.out.println("Server started!");           // fa partire il server e aspetta che qualcuno si connetta 
        System.out.println("Waiting for a client...");

        socket = server.accept();
        System.out.println("Client accepted!");

        inReceiver = new DataInputStream(socket.getInputStream());          // raccoglie gli input dal client socket
        outReceiver = new DataOutputStream(socket.getOutputStream());       // e manda gli output al client socket
    }

    public void run() throws IOException {
	sendRSApublicKey();
        getAESkey();
        getIV();
        getMessage();
        decriptMessage();
        splitMessage();
    }

    public void sendRSApublicKey() throws IOException {
        Sk = new jniRSA().genKeyRSA();
        nepqd = Sk.split(":");
        Pk = nepqd[0] + ":" + nepqd[1];

	System.out.println("\nSESSION " + session_counter + " KEY EXCHANGE"); session_counter++;
        System.out.println("Generated RSA public key:\nn = " + nepqd[0] + "\ne = " + nepqd[1]);
        outReceiver.writeUTF(Pk);
    }

    public void getAESkey() throws IOException {
        String criptedAESkey = inReceiver.readUTF();

        String str_n = nepqd[0];		// recupero i valori della secretKey che ho precedentemente concatenato (con le funzioni di libreria di C) usando ':' come separatore
        String str_e = nepqd[1];
        String str_p = nepqd[2];
        String str_q = nepqd[3];
        String str_d = nepqd[4];

        AESkey = new jniRSA().decRSA(criptedAESkey, str_n, str_e, str_p, str_q, str_d);
    }

    public void getIV() throws IOException {
        inReceiver.readFully(IV, 0, IV.length);
        
	System.out.print("IV = ");
	for (int i = 0; i < IV.length; i++) {
	    String hex = String.format("%02X", IV[i]);
            System.out.print(hex);
	}
    }

    public void getMessage() throws IOException {
        int count = inReceiver.readInt();		// recupero innanzitutto la lunghezza del messaggio (foto in byte) per sapere quanto spazio dedicare a "messaggio"
        messaggio = new byte[count];

        if (count > 0) {
            inReceiver.readFully(messaggio, 0, messaggio.length);
        }
    }

    public void decriptMessage() throws IOException {
        int res = new jniAES().findRes(messaggio, AESkey, IV);		// in questo modo recupero l'informazione relativa al depadding per capire quanto spazio dedicare a "decifrato"
        decifrato = new byte[messaggio.length - 1 - (16 - res - 1)];
        new jniAES().decAES(messaggio, AESkey, IV, decifrato);
        System.out.println("\n\nMessage received and decrypted! Waiting for the next message...");
    }

    public void splitMessage() throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss");		//decide il nome della foto in base al tempo a cui è stata mandata
        LocalDateTime now = LocalDateTime.now();
        String name = dtf.format(now) + ".png";

        byte[] tagsByte = new byte[decifrato[0]];                               // recupera la lunghezza dei tags dalla prima parte del decifrato e converte la sequenza di byte corrispondente
        System.arraycopy(decifrato, 1, tagsByte, 0, tagsByte.length);		// in stringa, per poi salvarla nel file "tags&names.txt"
        String tagsString = new String(tagsByte);
        tagsString = tagsString + " " + name;
        saveTagsName(tagsString);

        byte[] imageByte = new byte[decifrato.length - decifrato[0] - 1];                   // recupera il restante messaggio che corrisponde a tutti e soli i byte della foto, per poi
        System.arraycopy(decifrato, decifrato[0] + 1, imageByte, 0, imageByte.length);      // per poi salvarlo sotto forma di file immagine nella cartella "New Photos"
        BufferedImage bImage2 = ImageIO.read(new ByteArrayInputStream(imageByte));
        String directory = findDirectory("New Photos/");
        ImageIO.write(bImage2, "png", new File(directory + name));
    }

    public void saveTagsName(String tagsString) throws IOException {
        String directory = findDirectory("tags&names.txt");

        try (FileWriter writer = new FileWriter(directory, true)) {
            writer.write(tagsString + "\n");
        }
    }

    public String findDirectory(String tagsNames) {
        String fs = System.getProperty("file.separator");
        int intPos = System.getProperty("user.dir").lastIndexOf(fs);
        String directory = System.getProperty("user.dir").substring(0, intPos) + fs + "DATABASE/New Messages/" + tagsNames;

        return directory;
    }

    public void close() throws IOException {
        socket.close();
        inReceiver.close();
        outReceiver.close();
        System.out.println("Connection closed.");
    }

    public static void main(String args[]) throws IOException {
        Receiver receiver = new Receiver(5000);
        int notifica = 1;

        while (notifica != 0) {
            notifica = inReceiver.readInt();		// lavora in simbiosi con le due funzioni definite in Client.java, che fungono da notifica di inizio o fine comunicazione

            if (notifica == 1) {
                receiver.run();
            }
        }

        receiver.close();
    }
}

