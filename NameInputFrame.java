import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class NameInputFrame implements ActionListener {
    public JButton button;
    public JLabel label;
    public TextField text;
    public JFrame frame;
    public JPanel panel;
    public String name = "";
    
    public NameInputFrame(){
        frame = new JFrame();
        
        button = new JButton("Entra nella chat");
        text = new TextField("");
        label = new JLabel("Inserisci il tuo nome:");
        label.setPreferredSize(new Dimension(300, 30));
        label.setHorizontalAlignment(JLabel.CENTER);
        text.setPreferredSize(new Dimension(300, 30));
        button.setPreferredSize(new Dimension(300, 30));
        
        button.addActionListener(this);
        
        panel = new JPanel();
        panel.setBorder(new EmptyBorder(145, 0, 0, 0));
        frame.setLayout(new GridLayout(1, 1));
        panel.add(label);
        panel.add(text);
        panel.add(button);
        
        frame.setContentPane(panel);
        frame.setTitle("Accesso chat");
        frame.setPreferredSize(new Dimension(450, 500));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        name = text.getText();
        
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

}
