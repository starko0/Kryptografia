import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SchnorrKeyGenerator extends JFrame {
    private JTextField keyQ, keyH, keyV, keyA, keyMod;
    private JTextField signatureTextField, verificationTextField;
    private SecureRandom random = new SecureRandom();

    public SchnorrKeyGenerator() {
        setTitle("Kryptografia - Algorytm Schnorra");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        keyQ = new JTextField(20);
        keyH = new JTextField(20);
        keyV = new JTextField(20);
        keyA = new JTextField(20);
        keyMod = new JTextField(20);

        signatureTextField = new JTextField(20);
        verificationTextField = new JTextField(20);

        JButton generateButton = new JButton("Generuj klucze");
        JButton saveButton = new JButton("Zapisz do pliku");
        JButton loadButton = new JButton("Wczytaj z pliku");
        JButton signButton = new JButton("Utwórz podpis");
        JButton verifyButton = new JButton("Weryfikuj");

        JPanel panel = new JPanel(new GridLayout(11, 2, 10, 10));
        panel.add(new JLabel("Klucz q:"));
        panel.add(keyQ);
        panel.add(new JLabel("Klucz h:"));
        panel.add(keyH);
        panel.add(new JLabel("Klucz Publiczny: v"));
        panel.add(keyV);
        panel.add(new JLabel("Klucz Prywatny: a"));
        panel.add(keyA);
        panel.add(new JLabel("MOD p:"));
        panel.add(keyMod);
        panel.add(new JLabel("Podpis:"));
        panel.add(signatureTextField);
        panel.add(verifyButton);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(generateButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(signButton);
        buttonPanel.add(verifyButton);

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateKeys();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveKeysToFile();
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadKeysFromFile();
            }
        });

        signButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createSignature();
            }
        });

        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verifySignature();
            }
        });

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void generateKeys() {
        int bitLength = 256;
        BigInteger q = BigInteger.probablePrime(bitLength, random);
        BigInteger h = new BigInteger(bitLength, random);
        BigInteger a = new BigInteger(bitLength, random);
        BigInteger p = generatePrime(bitLength);
        BigInteger g = BigInteger.valueOf(3);
        BigInteger v = g.modPow(a, p);

        keyQ.setText(q.toString(16));
        keyH.setText(h.toString(16));
        keyV.setText(v.toString(16));
        keyA.setText(a.toString(16));
        keyMod.setText(p.toString(16));
    }

    private BigInteger generatePrime(int bitLength) {
        BigInteger prime = BigInteger.probablePrime(bitLength, random);
        prime = prime.subtract(prime.mod(BigInteger.valueOf(2).add(BigInteger.ONE))).add(BigInteger.valueOf(2));
        return prime;
    }

    private void saveKeysToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                FileWriter writer = new FileWriter(file);
                writer.write("Klucz q: " + keyQ.getText() + "\n");
                writer.write("Klucz h: " + keyH.getText() + "\n");
                writer.write("Klucz Publiczny v: " + keyV.getText() + "\n");
                writer.write("Klucz Prywatny a: " + keyA.getText() + "\n");
                writer.write("MOD p: " + keyMod.getText() + "\n");
                writer.close();
                JOptionPane.showMessageDialog(this, "Klucze zostały zapisane do pliku " + file.getPath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Błąd podczas zapisywania kluczy do pliku");
            }
        }
    }

    private void loadKeysFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                FileReader reader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] parts = line.split(": ");
                    if (parts.length == 2) {
                        String key = parts[0];
                        String value = parts[1];
                        switch (key) {
                            case "Klucz q":
                                keyQ.setText(value);
                                break;
                            case "Klucz h":
                                keyH.setText(value);
                                break;
                            case "Klucz Publiczny v":
                                keyV.setText(value);
                                break;
                            case "Klucz Prywatny a":
                                keyA.setText(value);
                                break;
                            case "MOD p":
                                keyMod.setText(value);
                                break;
                        }
                    }
                }
                bufferedReader.close();
                JOptionPane.showMessageDialog(this, "Klucze zostały wczytane z pliku " + file.getPath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Błąd podczas wczytywania kluczy z pliku");
            }
        }
    }

    private void createSignature() {
        // Tworzenia podpisu
        String message = JOptionPane.showInputDialog(this, "Wprowadź wiadomość do podpisania:");
        if (message != null) {
            try {
                byte[] messageBytes = message.getBytes();
                byte[] signatureBytes = sha256(messageBytes);
                String signature = new BigInteger(1, signatureBytes).toString(16);
                signatureTextField.setText(signature);
                JOptionPane.showMessageDialog(this, "Podpis został utworzony.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Błąd podczas tworzenia podpisu.");
            }
        }
    }

    private void verifySignature() {
        // Weryfikacja podpisu
        String signature = signatureTextField.getText();
        String message = JOptionPane.showInputDialog(this, "Wprowadź wiadomość do zweryfikowania:");
        if (message != null && !signature.isEmpty()) {
            try {
                byte[] messageBytes = message.getBytes();
                byte[] signatureBytes = new BigInteger(signature, 16).toByteArray();
                byte[] calculatedSignature = sha256(messageBytes);
                if (MessageDigest.isEqual(signatureBytes, calculatedSignature)) {
                    verificationTextField.setText("Podpis poprawny");
                    JOptionPane.showMessageDialog(this, verificationTextField.getText());
                } else {
                    verificationTextField.setText("Podpis niepoprawny");
                    JOptionPane.showMessageDialog(this, verificationTextField.getText());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Błąd podczas weryfikacji podpisu.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Wprowadź wiadomość i podpis.");
        }
    }

    private byte[] sha256(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorytm SHA-256 nie został znaleziony.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SchnorrKeyGenerator().setVisible(true);
            }
        });
    }
}