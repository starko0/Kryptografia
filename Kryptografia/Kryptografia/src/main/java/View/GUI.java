package View;

import Logic.DES;
import Logic.Helper;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

public class GUI extends Application {
    private byte[] key;
    private TextArea leftTextArea;
    private TextArea rightTextArea;

    private TextField keyTextField;

    private boolean isFileLoaded = false; // nowa zmienna

    ComboBox<String> FileOrText;// = new ComboBox<>();

    @Override
    public void start(Stage primaryStage) {
        key = Helper.generateKey();

        leftTextArea = new TextArea();
        FileOrText = new ComboBox<>(FXCollections.observableArrayList("Tekst", "Plik"));
        FileOrText.setPrefWidth(400);
        FileOrText.setPrefHeight(50);
        rightTextArea = new TextArea();

        Button loadFileButton = new Button("Wczytaj plik");
        loadFileButton.setOnAction(e -> loadFileToLeftTextArea(primaryStage));

        Button saveLeftFileButton = new Button("Zapisz plik (odszyfrowany)");
        saveLeftFileButton.setOnAction(e -> saveFileFromLeftTextArea(primaryStage));

        Button saveRightFileButton = new Button("Zapisz plik (zaszyfrowany)");
        saveRightFileButton.setOnAction(e -> saveFileFromRightTextArea(primaryStage));

        Button encryptButton = new Button("Szyfruj");
        encryptButton.setOnAction(e -> encryptText());

        Button decryptButton = new Button("Deszyfruj");
        decryptButton.setOnAction(e -> decryptText());

        HBox buttonBox = new HBox(10, loadFileButton, saveLeftFileButton, saveRightFileButton, encryptButton, decryptButton);
        buttonBox.setPadding(new Insets(10));

        HBox textAreaBox = new HBox(20, leftTextArea, FileOrText ,rightTextArea);
        textAreaBox.setPadding(new Insets(20));

        VBox root = new VBox(textAreaBox, buttonBox);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Szyfrowanie i deszyfrowanie DES");
        primaryStage.setScene(scene);
        primaryStage.show();



        keyTextField = new TextField();
        keyTextField.setPromptText("Klucz");
        keyTextField.setPrefColumnCount(20);
        keyTextField.setOnKeyReleased(e -> {
            String newKey = keyTextField.getText();
        });

        Button generateKeyButton = new Button("Generuj klucz");
        generateKeyButton.setOnAction(e -> generateNewKey());

        // Dodanie pola tekstowego i przycisku do kontenera
        HBox keyBox = new HBox(10);
        keyBox.setAlignment(Pos.CENTER);
        keyBox.getChildren().addAll(new Label("Klucz: "), keyTextField, generateKeyButton);

        root.setPadding(new Insets(10));
        root.getChildren().addAll(
                keyBox);
    }

    String fakeLeftTextArea = new String();


    private void generateNewKey() {
        key = Helper.generateKey();
        updateKeyTextField();
    }

    private void updateKeyTextField() {
        String encodedKey = Base64.getEncoder().encodeToString(key);
        keyTextField.setText(encodedKey);
    }
    private void loadFileToLeftTextArea(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik");
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
                fakeLeftTextArea = Base64.getEncoder().encodeToString(fileBytes);
                String fileBytesToString = new String(fileBytes);
                leftTextArea.setText(fileBytesToString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFileFromLeftTextArea(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz plik (odszyfrowany)");
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            try {
                String fileContent = fakeLeftTextArea;
                byte[] fileBytes = Base64.getDecoder().decode(fileContent);
                Files.write(selectedFile.toPath(), fileBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFileFromRightTextArea(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz plik (zaszyfrowany)");
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            try {
                String encryptedText = rightTextArea.getText();
                byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
                Files.write(selectedFile.toPath(), encryptedBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void encryptText() {
        String plaintext;
        if(FileOrText.getValue().equals("Tekst")){
            plaintext = leftTextArea.getText();
            byte[] plaintextBytes = plaintext.getBytes(); // Pobieranie bajtów tekstu
            byte[] encryptedBytes = DES.encrypt(plaintextBytes, key);
            String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
            rightTextArea.setText(encryptedText);
        }
        else{
            plaintext = fakeLeftTextArea;
            byte[] plaintextBytes = Base64.getDecoder().decode(plaintext);
            byte[] encryptedBytes = DES.encrypt(plaintextBytes, key);
            String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
            rightTextArea.setText(encryptedText);
        }
    }
    private void decryptText() {

        if (FileOrText.getValue().equals("Tekst")) {
            String encryptedText = rightTextArea.getText();
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = DES.decrypt(encryptedBytes, key);
            String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);
            leftTextArea.setText(decryptedText);
        } else {
            String encryptedText = rightTextArea.getText();
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = DES.decrypt(encryptedBytes, key);
            String decryptedText = new String(decryptedBytes);
            fakeLeftTextArea = Base64.getEncoder().encodeToString(decryptedBytes);
            leftTextArea.setText(decryptedText);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
