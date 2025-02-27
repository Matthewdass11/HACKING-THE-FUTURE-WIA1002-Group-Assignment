package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

public class bookingController {
    @FXML
    private Stage stage;

    @FXML
    public void bookingStartUp(ActionEvent event) throws Exception{
        Parent root2 = FXMLLoader.load(getClass().getResource("booking.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene homeScene = new Scene(root2, stage.getScene().getWidth(), stage.getScene().getHeight());
        stage.setScene(homeScene);
    }

    @FXML
    public void homeButton(ActionEvent event) throws Exception{
        Parent root2 = FXMLLoader.load(getClass().getResource("homePage.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene homeScene = new Scene(root2, stage.getScene().getWidth(), stage.getScene().getHeight());
        stage.setScene(homeScene);
    }

    public void quizButton(ActionEvent event) throws Exception{
        attemptQuizController attemptQuizController = new attemptQuizController();
        attemptQuizController.attemptQuizStartUp(event);
    }

    public void eventButton(ActionEvent event) throws Exception{
        viewEventController viewEventController = new viewEventController();
        viewEventController.viewEventStartUp(event);
    }

    public void bookingButton(ActionEvent event) throws Exception{
        bookingController bookingController = new bookingController();
        bookingController.bookingStartUp(event);
    }
       @FXML
    private ChoiceBox<String> destinationChoiceBox;

    @FXML
    private TextArea timeSlotsTextArea;

    @FXML
    private TextField timeSlotTextField;

    private ArrayList<BookingDestination> bookingDestinations;

    @FXML
    public void initialize() {
        loadBookingDestinations();
        displayBookingDestinations();
    }

    private void loadBookingDestinations() {
        bookingDestinations = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("BookingDestination.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String name = line;
                line = br.readLine();
                String[] parts = line.split(",");
                double x = Double.parseDouble(parts[0].trim());
                double y = Double.parseDouble(parts[1].trim());
                BookingDestination destination = new BookingDestination(name, x, y);
                bookingDestinations.add(destination);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayBookingDestinations() {
        double userX = 0; // Example user coordinate
        double userY = 0; // Example user coordinate

        for (BookingDestination destination : bookingDestinations) {
            double distance = Math.sqrt(Math.pow(userX - destination.getX(), 2) + Math.pow(userY - destination.getY(), 2));
            destination.setDistance(distance);
        }

        Collections.sort(bookingDestinations, Comparator.comparing(BookingDestination::getDistance));

        for (BookingDestination destination : bookingDestinations) {
            destinationChoiceBox.getItems().add(destination.getName() + "\n" + String.format("%.2f km away", destination.getDistance()));
        }
    }

    @FXML
    public void destinationSelected() {
        timeSlotsTextArea.clear();
        LocalDate currentDate = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            timeSlotsTextArea.appendText("[" + (i + 1) + "] " + currentDate.plusDays(i) + "\n");
        }
    }

    @FXML
    public void bookDestination() {
        int selectedIndex = destinationChoiceBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            showAlert("Error", "Please select a destination.");
            return;
        }
        String timeSlot = timeSlotTextField.getText();
        if (timeSlot.isBlank()) {
            showAlert("Error", "Please enter a time slot.");
            return;
        }

        // Additional logic to check for event clashes can be implemented here

        showAlert("Success", "Booking confirmed for: " + destinationChoiceBox.getValue() + " on " + timeSlot);
    }

    @FXML
    public void cancelBooking() {
        destinationChoiceBox.getSelectionModel().clearSelection();
        timeSlotsTextArea.clear();
        timeSlotTextField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void leaderBoardButton(ActionEvent event) throws Exception{
        leaderBoardController leaderBoardController = new leaderBoardController();
        leaderBoardController.leaderBoardStartUp(event);
    }

    public void profileButton(ActionEvent event) throws Exception{
        personalProfileController personalProfileController = new personalProfileController();
        personalProfileController.personalProfileStartUp(event);
    }

    public void logOutButton(ActionEvent event) throws Exception{
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log Out");
        alert.setContentText("Are you sure want to log out?");
        Optional<ButtonType> result = alert.showAndWait();

        if(result.isEmpty()){
            System.out.println("Alert closed");

        } else if(result.get() == ButtonType.OK) {
            loginController loginController = new loginController();
            loginController.loginStartUp(event);

        } else if (result.get() == ButtonType.CANCEL);

    }

    @FXML
    public void loginButton(ActionEvent event) throws Exception{
        Parent root2 = FXMLLoader.load(getClass().getResource("homePage.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene homeScene = new Scene(root2, stage.getScene().getWidth(), stage.getScene().getHeight());
        stage.setScene(homeScene);
    }

    @FXML
    public void cAAButton(ActionEvent event) throws Exception{
        Parent root2 = FXMLLoader.load(getClass().getResource("signUp.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene signUpScene = new Scene(root2, stage.getScene().getWidth(), stage.getScene().getHeight());
        stage.setScene(signUpScene);

    }
}