import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.io.*;

public class SchnorrMenu extends JFrame {

    private JTextField textFieldQ, textFieldPublicKey, textFieldPrivateKey, textFieldP;
    private JTextArea textAreaMessage;
    private Schnorr schnorr;
    private BigInteger[] signature;

    public SchnorrMenu() {
        schnorr = new Schnorr();

        setTitle("Kryptografia - Schnorr");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 1));

        JPanel panelKeys = new JPanel();
        panelKeys.setLayout(new GridLayout(7, 2));
        add(panelKeys);

        textFieldQ = new JTextField();
        textFieldPublicKey = new JTextField();
        textFieldPrivateKey = new JTextField();
        textFieldP = new JTextField();

        panelKeys.add(new JLabel("Klucz q oraz h:"));
        panelKeys.add(textFieldQ);
        panelKeys.add(new JLabel("Klucz Publiczny v:"));
        panelKeys.add(textFieldPublicKey);
        panelKeys.add(new JLabel("Klucz Prywatny a:"));
        panelKeys.add(textFieldPrivateKey);
        panelKeys.add(new JLabel("MOD p:"));
        panelKeys.add(textFieldP);

        JTextArea textAreaVerification = new JTextArea();
        JButton buttonVerify = new JButton("Weryfikuj");

        JButton buttonGenerateKeys = new JButton("Generuj klucze");
        buttonGenerateKeys.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                schnorr.generateKey();
                textFieldQ.setText(schnorr.q.toString(16));
                textFieldPublicKey.setText(schnorr.v.toString(16));
                textFieldPrivateKey.setText(schnorr.s.toString(16));
                textFieldP.setText(schnorr.p.toString(16));
            }
        });

        panelKeys.add(buttonGenerateKeys);

        JButton buttonSaveKeys = new JButton("Zapisz klucze do pliku");
        buttonSaveKeys.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(SchnorrMenu.this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        schnorr.saveKeysToFile(file.getPath());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        JButton buttonLoadKeys = new JButton("Wczytaj klucze z pliku");
        buttonLoadKeys.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(SchnorrMenu.this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        schnorr.loadKeysFromFile(file.getPath());
                        textFieldQ.setText(schnorr.q.toString(16));
                        textFieldPublicKey.setText(schnorr.v.toString(16));
                        textFieldPrivateKey.setText(schnorr.s.toString(16));
                        textFieldP.setText(schnorr.p.toString(16));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        panelKeys.add(buttonSaveKeys);
        panelKeys.add(buttonLoadKeys);

        JPanel panelSignVerify = new JPanel(new GridLayout(1, 2));
        add(panelSignVerify);

        JPanel panelSign = new JPanel(new GridLayout(4, 1));
        panelSignVerify.add(panelSign);

        textAreaMessage = new JTextArea();
        JButton buttonSign = new JButton("Podpisuj");

        buttonSign.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(textAreaMessage.getText().isEmpty() || textFieldPrivateKey.getText().isEmpty() || textFieldPublicKey.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Uzupelnij dane do podpisu", "Blad", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String message = textAreaMessage.getText();
                signature = schnorr.sign(message);
                textAreaVerification.setText(signature[0].toString(16) + signature[1].toString(16));
            }
        });

        panelSign.add(new JLabel("Tu podaj tekst jawny"));
        panelSign.add(new JScrollPane(textAreaMessage));
        panelSign.add(buttonSign);

        JPanel panelVerify = new JPanel(new GridLayout(4, 1));
        panelSignVerify.add(panelVerify);


        buttonVerify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textAreaMessage.getText();
                boolean result = schnorr.verify(message.getBytes(), signature);
                if(result)
                    JOptionPane.showMessageDialog(null, "Podpis poprawny", "Sukces", JOptionPane.INFORMATION_MESSAGE);
                else
                    JOptionPane.showMessageDialog(null, "Podpis niepoprawny", "Blad", JOptionPane.ERROR_MESSAGE);
            }

        });

        panelVerify.add(new JLabel("Weryfikacja podpisu"));
        panelVerify.add(new JScrollPane(textAreaVerification));
        panelVerify.add(buttonVerify);

        buttonSign.setPreferredSize(buttonVerify.getPreferredSize());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SchnorrMenu().setVisible(true);
            }
        });
    }
}