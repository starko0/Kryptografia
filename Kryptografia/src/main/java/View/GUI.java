package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

import Logic.EncryptDecryptFIle;
import Logic.Helper;

public class GUI extends Application {

    private File fileToEncrypt;
    private File fileToDecrypt;
    private byte[] key;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("DES File Encryption/Decryption");

        // Generowanie losowego klucza
        SecureRandom random = new SecureRandom();
        key = new byte[8];
        random.nextBytes(key);

        // Tworzenie kontrolek View.GUI
        Label fileToEncryptNameLabel = new Label("");
        Label fileToDecryptNameLabel = new Label("");
        Label fileToEncryptLabel = new Label("Wybierz plik do zaszyfrowania:");
        Label fileToDecryptLabel = new Label("Wybierz plik do odszyfrowania:");
        Button selectFileToEncryptButton = new Button("Wybierz plik");
        Button selectFileToDecryptButton = new Button("Wybierz plik");
        Button encryptButton = new Button("Zaszyfruj");
        Button decryptButton = new Button("Odszyfruj");
        Button generateKeyButton = new Button("Generuj klucz");
        Button saveKeyButton = new Button("Zapisz klucz do pliku");
        Button loadKeyButton = new Button("Wczytaj klucz z pliku");
        Label keyLabel = new Label(Helper.bytesToHex(key));;

        // Tworzenie układu
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(20);
        gridPane.setHgap(10);
        gridPane.add(fileToEncryptLabel, 0, 0);
        gridPane.add(selectFileToEncryptButton, 1, 0);
        gridPane.add(fileToEncryptNameLabel, 2, 0);
        gridPane.add(fileToDecryptLabel, 0, 1);
        gridPane.add(selectFileToDecryptButton, 1, 1);
        gridPane.add(fileToDecryptNameLabel, 2, 1);
        gridPane.add(encryptButton, 0, 2);
        gridPane.add(decryptButton, 1, 2);
        gridPane.add(generateKeyButton, 0, 6);
        gridPane.add(saveKeyButton, 1, 6);
        gridPane.add(loadKeyButton, 2, 6);
        gridPane.add(keyLabel, 0, 7, 3, 1);



        // Tworzenie sceny
        Scene scene = new Scene(gridPane);
        scene.getStylesheets().add(getClass().getResource("/gui.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        // Obsługa zdarzeń
        selectFileToEncryptButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Wybierz plik do zaszyfrowania");
            fileToEncrypt = fileChooser.showOpenDialog(primaryStage);
            if(fileToEncrypt != null) {
                fileToEncryptNameLabel.setText(fileToEncrypt.getName());
            }
        });
        selectFileToDecryptButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Wybierz plik do odszyfrowania");
            fileToDecrypt = fileChooser.showOpenDialog(primaryStage);
            if(fileToDecrypt != null) {
                fileToDecryptNameLabel.setText(fileToDecrypt.getName());
            }
        });
        encryptButton.setOnAction(event -> encryptFile());
        decryptButton.setOnAction(event -> decryptFile());

        generateKeyButton.setOnAction(event -> {
            key = Helper.generateKey();
            keyLabel.setText(Helper.bytesToHex(key));
        });

        saveKeyButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Zapisz klucz do pliku");
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                Helper.saveKeyToFile(key, file.getAbsolutePath());
            }
        });

        loadKeyButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Wczytaj klucz z pliku");
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                key = Helper.loadKeyFromFile(file.getAbsolutePath());
                keyLabel.setText(Helper.bytesToHex(key));
            }
        });

    }

    private void selectFileToEncrypt(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik do zaszyfrowania");
        fileToEncrypt = fileChooser.showOpenDialog(stage);
    }

    private void selectFileToDecrypt(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik do odszyfrowania");
        fileToDecrypt = fileChooser.showOpenDialog(stage);
    }

    private void encryptFile() {
        if (fileToEncrypt != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Zapisz zaszyfrowany plik");
            File file = fileChooser.showSaveDialog(null);
            if(file!=null){
                String encryptedFilePath = file.getAbsolutePath();
                EncryptDecryptFIle.encryptFile(fileToEncrypt.getAbsolutePath(), encryptedFilePath, key);
            }
        }
    }

    private void decryptFile() {
        if (fileToDecrypt != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Zapisz odszyfrowany plik");
            File file = fileChooser.showSaveDialog(null);
            if(file!=null){
                String decryptedFilePath = file.getAbsolutePath();
                EncryptDecryptFIle.decryptFile(fileToDecrypt.getAbsolutePath(), decryptedFilePath, key);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}