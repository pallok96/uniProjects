import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Arrays;


public class Client {
    private Socket socket = null;           	// inizializziamo socket e i canali di input e output
    private DataInputStream inClient = null;
    private DataOutputStream outClient = null;
    private SecureRandom random = new SecureRandom();
    private byte[] chiaveAES;
    byte[] IV;
    String Pk;

    public Client(String address, int port) throws IOException {
        socket = new Socket(address, port);
        System.out.println("Connected!");

        inClient = new DataInputStream(socket.getInputStream());
        outClient = new DataOutputStream(socket.getOutputStream());
    }


    public byte[] generate16randBytes() {
        byte[] arr = new byte[16];
        
	do {		
            random.nextBytes(arr);		// serve per assiucurarsi che il primo byte della chiave non sia 0 (ossia 0x00) poichè nella conversione a numero per mano dell'encrypt di RSA e più
   	} while (arr[0] == 0);			// precisamente con il comando mpz_import crea problemi di lettura e si sballa tutto

        return arr;
    }
	

    public void keyExchange() throws IOException {
        Pk = inClient.readUTF();

        chiaveAES = generate16randBytes();		// generazione chiave AES
	sendCriptedAESkey();

        IV = generate16randBytes();                	// generazione IV
        sendIV();
    }


    public void sendCriptedAESkey() throws IOException {
        String[] ne = Pk.split(":");
        String str_n = ne[0];
        String str_e = ne[1];

        String criptedAESKey = new jniRSA().encRSA(chiaveAES, str_n, str_e);
        outClient.writeUTF(criptedAESKey);
    }

    public void sendIV() throws IOException {
        outClient.write(IV);
    }


    public void sendMessage(byte[] messaggio) throws IOException {
        int len = messaggio.length;
        byte[] cipherText = new byte[len + (16 - len % 16)];
	jniAES cifrario = new jniAES();
        cifrario.encAES(messaggio, chiaveAES, IV, cipherText);
	
	outClient.writeInt(cipherText.length);
        outClient.write(cipherText);
    }


    public void newMessage() throws IOException {
        outClient.writeInt(1);				// è la nostra convenzione per comunicare al receiver che vogliamo mandargli un messaggio
    }


    public void stopMessages() throws IOException {
        outClient.writeInt(0);				// è la nostra convenzione per comunicare al receiver che vogliamo smettere di comunicare
    }


    public void close() {
        try {
            inClient.close();           // chiude la connessione
            outClient.close();
            socket.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }
}

