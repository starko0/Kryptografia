import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.io.*;
import java.nio.file.Files;

public class SchnorrMenu extends JFrame {

    private JTextField textFieldQ, textFieldPublicKey, textFieldPrivateKey, textFieldP, textFieldA;
    private JTextArea textAreaMessage, textAreaVerification;
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
        textFieldA = new JTextField();
        textFieldPublicKey = new JTextField();
        textFieldPrivateKey = new JTextField();
        textFieldP = new JTextField();

        panelKeys.add(new JLabel("q:"));
        panelKeys.add(textFieldQ);
        panelKeys.add(new JLabel("a:"));
        panelKeys.add(textFieldA);
        panelKeys.add(new JLabel("Klucz Publiczny v:"));
        panelKeys.add(textFieldPublicKey);
        panelKeys.add(new JLabel("Klucz Prywatny s:"));
        panelKeys.add(textFieldPrivateKey);
        panelKeys.add(new JLabel("MOD p:"));
        panelKeys.add(textFieldP);

        textAreaVerification = new JTextArea();
        JButton buttonVerify = new JButton("Weryfikuj");

        JButton buttonGenerateKeys = new JButton("Generuj klucze");
        buttonGenerateKeys.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                schnorr.generateKey();
                textFieldQ.setText(schnorr.q.toString(16));
                textFieldA.setText(schnorr.a.toString(16));
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
                        textFieldA.setText(schnorr.a.toString(16));
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

        JPanel panelSign = new JPanel(new GridLayout(5, 1));
        panelSignVerify.add(panelSign);

        textAreaMessage = new JTextArea();
        JButton buttonSign = new JButton("Podpisuj");

        buttonSign.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(textAreaMessage.getText().isEmpty() || textFieldPrivateKey.getText().isEmpty() || textFieldPublicKey.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Uzupełnij dane do podpisu", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String message = textAreaMessage.getText();
                schnorr.s = new BigInteger(textFieldPrivateKey.getText(), 16);
                schnorr.v = new BigInteger(textFieldPublicKey.getText(), 16);
                signature = schnorr.sign(message);
                textAreaVerification.setText(signature[0].toString(16) + "," + signature[1].toString(16));
            }
        });

        panelSign.add(new JLabel("Tekst do podpisania"));
        panelSign.add(new JScrollPane(textAreaMessage));
        panelSign.add(buttonSign);

        JPanel panelVerify = new JPanel(new GridLayout(5, 1));
        panelSignVerify.add(panelVerify);

        buttonVerify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textAreaMessage.getText();
                schnorr.v = new BigInteger(textFieldPublicKey.getText(), 16);  // Pobierz klucz publiczny z pola tekstowego
                boolean result = schnorr.verify(message.getBytes(), signature);
                if(result)
                    JOptionPane.showMessageDialog(null, "Podpis poprawny", "Sukces", JOptionPane.INFORMATION_MESSAGE);
                else
                    JOptionPane.showMessageDialog(null, "Podpis niepoprawny", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        panelVerify.add(new JLabel("Weryfikacja podpisu"));
        panelVerify.add(new JScrollPane(textAreaVerification));
        panelVerify.add(buttonVerify);

        JButton buttonLoadTextFromFile = new JButton("Wczytaj tekst z pliku");
        buttonLoadTextFromFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(SchnorrMenu.this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                                                String text = loadTextFromFile(file);
                        textAreaMessage.setText(text);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        panelSign.add(buttonLoadTextFromFile);

        JButton buttonSaveSignatureToFile = new JButton("Zapisz podpis do pliku");
        buttonSaveSignatureToFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(SchnorrMenu.this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        saveSignatureToFile(file.getPath());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        JButton buttonLoadSignatureFromFile = new JButton("Wczytaj podpis z pliku");
        buttonLoadSignatureFromFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(SchnorrMenu.this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        String signatureString = loadTextFromFile(file);
                        if (signatureString.length() % 2 == 0) {
                            int halfLength = signatureString.length() / 2;
                            String part1 = signatureString.substring(0, halfLength);
                            String part2 = signatureString.substring(halfLength);
                            signature = new BigInteger[2];
                            signature[0] = new BigInteger(part1, 16);
                            signature[1] = new BigInteger(part2, 16);
                            textAreaVerification.setText(part1 + part2);
                        } else {
                            JOptionPane.showMessageDialog(null, "Niepoprawny format podpisu w pliku", "Błąd", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        panelVerify.add(buttonSaveSignatureToFile);
        panelVerify.add(buttonLoadSignatureFromFile);

        buttonSign.setPreferredSize(buttonVerify.getPreferredSize());
    }

    private String loadTextFromFile(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }

    private void saveSignatureToFile(String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(signature[0].toString(16) + signature[1].toString(16));
        writer.close();
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


