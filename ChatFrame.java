import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ChatFrame extends JFrame implements ActionListener {
    private final TextArea sendMsg;
    private final JPanel messaggi, bottomPanel;
    private final JScrollPane chat;
    private final JButton sendBtn;
    private final ChatGroupClient client;
        
    public ChatFrame(ChatGroupClient client) {
        this.client = client;
        
        // Aggiungi panello con la scrollbar per i messaggi della chat
        messaggi = new JPanel(new GridLayout(500, 1), true);
        chat = new JScrollPane(messaggi);
        chat.setPreferredSize(new Dimension(550, 500));        
        
        this.add(chat);
        System.out.println("SocketGroupChat1.ChatFrame.<init>()");
        // Aggiungi textarea con bottone per inviare messaggi
        bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setPreferredSize(new Dimension(550, 35));
        
        sendMsg = new TextArea();
        sendMsg.setPreferredSize(new Dimension(440, 30));
        
        sendBtn = new JButton("Send");
        sendBtn.setPreferredSize(new Dimension(100, 30));
        sendBtn.addActionListener(this);
        
        bottomPanel.add(sendMsg);
        bottomPanel.add(sendBtn);
        
        this.add(bottomPanel);
        
        this.defineFrame();
    }
    
    private void defineFrame() {
        this.setTitle("Chat Group");
        
        this.setLayout(new FlowLayout());
        this.setPreferredSize(new Dimension(600, 600));
        this.setResizable(false);
        
        this.pack();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.toFront();
    }
    
    public void newMessage(String msg) {
        JLabel label = new JLabel(msg);
        label.setPreferredSize(new Dimension(500, 30));
        messaggi.add(label);
        
        messaggi.updateUI();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = sendMsg.getText();
        sendMsg.setText("");        
                
        client.sendMessage(msg);
    }
}
