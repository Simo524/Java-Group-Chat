import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

class ChatGroupClientThread extends Thread {
    private final ChatGroupClient client;
    private final ChatFrame frame;
    
    public ChatGroupClientThread(ChatGroupClient client, ChatFrame frame) {
        this.client = client;
        this.frame = frame;
    }
    
    @Override
    public void run() {
        // Ascolta per i messaggi che vengono invati da altre persone (che passano attraverso il server)
        while(true) {
            try {
                // legge e stampa i messaggi
                String msg = client.readMessage();
                frame.newMessage(msg);
                
            } catch (IOException ex) {
                System.err.println("\nServer disconnesso!!\nUscita forzata dalla chat.");
                try { client.closeConnection(); } catch (IOException ex1) {}
                break;
            }
        }
    }
}

public class ChatGroupClient {
    private final Socket socket;
    
    private final PrintWriter out;
    private final BufferedReader in;
    
    private ChatGroupClientThread thread;

    public ChatGroupClient(InetAddress address, int port) throws IOException {
        // Costruttore: crea socket e lo connette, crea oggetti di IN e OUT, resta in ascolto per eventuali messaggi
        this.socket = new Socket(address, port);
        
        this.out = new PrintWriter(this.socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }
    
    public void sendMessage(String msg) {
        // Scrive messaggio
        this.out.println(msg);
    }
    
    public String readMessage() throws IOException {
        // Legge messaggio
        String msg = in.readLine();
        
        return msg;
    }
    
    public void closeConnection() throws IOException {
        // Chiude la connessione al server
        this.socket.close();
    }
    
    public void setThread(ChatFrame frame) {
        thread = new ChatGroupClientThread(this, frame);
    }
    
    public void startThread() {
        thread.start();
    }
    
    public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
        BufferedReader stream = new BufferedReader(new InputStreamReader(System.in));
        NameInputFrame nameInput = new NameInputFrame();
        String name;
        
        while(true) {
            Thread.sleep(250);
            if(!nameInput.name.equals("")) {
                name = nameInput.name;
                break;
            }
        }
        
        System.out.println(name);
        
        // Connetto al server
        ChatGroupClient client = new ChatGroupClient(InetAddress.getByName("127.0.0.1"), 3001);
        ChatFrame frame = new ChatFrame(client);
        
        client.setThread(frame);
        client.startThread();
        
        client.sendMessage(name);
    }
    
}
