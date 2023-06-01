import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/*
 * 1° processo: aspetta la connessione di n client e crea un processo per ognuno di loro
 * n_client processo: contiene una LinkedList di tutti i client che si connettono,
 *                   viene aggiornato quando si connette un nuovo client oppure se 
 *                   ne disconnette uno
*/

class ChatGroupServerThread extends Thread {
    private String clientName;
    
    private LinkedList<Socket> allClients;
    private LinkedList<ChatGroupServerThread> allThreads;
    private Socket client;
    
    private final BufferedReader in;
    
    public ChatGroupServerThread (Socket client, LinkedList<Socket> allClients, String clientName) throws IOException{
        // Creo il nuovo thread con il nome del client, gli array e l'oggetto di in
        this.clientName = clientName;
        
        this.client = client;
        this.allClients = allClients;
        
        this.in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
        
    }
    
    public void setAllClients(LinkedList allClients) {
        // Aggiorna l'array dei clients
        this.allClients = allClients;
    }
    
    public void setAllThreads(LinkedList allThreads) {
        // Aggiorna l'array dei threads
        this.allThreads = allThreads;
    }
    
    public void closeConnection() throws IOException {
        // Rimuove questo thread dall'array di tutti i thread 
        this.allClients.remove(this);
        
        // Aggiorna l'array di tutti i thread
        for(ChatGroupServerThread myServerThread : allThreads) {
            myServerThread.setAllThreads(this.allThreads);
        }
        
        // Chiude la connessione
        this.client.close();
    }
    
    @Override
    public void run() {
        try {
            // Il primo messaggio è l'annuncio di un nuovo partecipante
            for(Socket myClient : allClients) {
                PrintWriter output = new PrintWriter(myClient.getOutputStream(), true);
                
                output.println("\nServer>> " + this.clientName + " entered the chat");
            }
            
            while(true) {
                // Quando arriva un messaggio lo inoltro a tutti i client
                String msg = in.readLine();

                if(msg.equals("exit")) {
                    break;
                }

                System.out.println("message received: '" + msg + "'");
                
                for(Socket myClient : allClients) {
                    PrintWriter output = new PrintWriter(myClient.getOutputStream(), true);

                    output.println(this.clientName + ">> " + msg);
                }
            }
            
            /* 
             * Se la chat viene terminata da questo specifico client chiudo la connessione,
             * aggiorno gli array di tutti i thread e stampo un messaggio di uscita a tutti
             * i client
            */
            this.closeConnection();
            
            for(Socket myClient : allClients) {
                PrintWriter output = new PrintWriter(myClient.getOutputStream());
                
                output.println(this.clientName + " left the chat");
            }
        } catch(IOException ex) {
            System.err.println(ex);
        }
    }
}

public class ChatGroupServer {
    private final ServerSocket server;
    private Socket newClient;
    
    private BufferedReader in;
    
    public ChatGroupServer(int port) throws IOException {
        // Creo server
        this.server = new ServerSocket(port);
    }
    
    public void listen() throws IOException {
        // Aspetto la connessione di un client e creo l'oggetto di in
        this.newClient = this.server.accept();
        
        this.in = new BufferedReader(new InputStreamReader(this.newClient.getInputStream()));
    }
    
    public String readName() throws IOException {
        // Leggo un messaggio e lo ritorno
        String name = this.in.readLine();
        
        return name;
    }
    
    public Socket getNewClient() {
        // ritorna il parametro private "newClient"
        return this.newClient;
    }
    
    public static void main(String[] args) throws IOException {
        ChatGroupServer server = new ChatGroupServer(3001);
        LinkedList<Socket> allClients = new LinkedList<>();
        LinkedList<ChatGroupServerThread> allThreads = new LinkedList<>();
        
        while(true) {
            /*
             * Processo numero 1: aspetta per le connessioni
             * Quando uno si connette aspetta il nome
             * Crea il nuovo thread che gestirà la ricezione e l'inoltro di messaggi
             * Aggiorno gli array
            */
            System.out.println("Waiting for connections");
            server.listen();
            System.out.println("Client connected");
            
            String name = server.readName();
            
            System.out.println("Hello " + name);
            
            allClients.add(server.getNewClient());
            ChatGroupServerThread newServerThread = new ChatGroupServerThread(server.getNewClient(), allClients, name);
            allThreads.add(newServerThread);
            newServerThread.setAllThreads(allThreads);
            
            newServerThread.start();
        }
    }
}
