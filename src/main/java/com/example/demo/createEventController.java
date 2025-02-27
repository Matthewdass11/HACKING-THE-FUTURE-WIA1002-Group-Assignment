package com.example.demo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class createEventController {
    @FXML
    private Stage stage;

    @FXML
    private TextField eventTitle;

    @FXML
    private Text eventTitleErrorMessage;

    @FXML
    private TextField eventDescription;

    @FXML
    private Text eventDescriptionErrorMessage;

    @FXML
    private TextField eventVenue;

    @FXML
    private Text eventVenueErrorMessage;

    @FXML
    private DatePicker eventDate;

    @FXML
    private Text eventDateErrorMessage;

    @FXML
    private TextField eventStartTime;

    @FXML
    private Text eventStartTimeErrorMessage;

    @FXML
    private TextField eventEndTime;

    @FXML
    private Text eventEndTimeErrorMessage;

    @FXML
    private TextField eventVenueLatitude;

    @FXML
    private TextField eventVenueLongitude;

    private boolean titleValid = false, descriptionValid = false, venueValid = false, dateValid = false,
            timeStartValid = false, timeEndValid = false;

    private Timer venueTimer;
    private LocalDate date = null;
    private LocalTime startTime;
    private LocalTime endTime;

    private boolean eventCreationInProgress = false;

    String Title, Description, Venue;

    @FXML
    public void createEventStartUp(ActionEvent event) throws Exception {
        Parent root2 = FXMLLoader.load(getClass().getResource("eventCreate.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene homeScene = new Scene(root2, stage.getScene().getWidth(), stage.getScene().getHeight());
        stage.setScene(homeScene);
    }

    public void eventDescriptionValidation() throws Exception {
        Description = eventDescription.getText();
        String[] words = Description.split("\\s+");

        if (words.length > 100) {
            eventDescriptionErrorMessage.setText("Event description should not contain more than 100 words");
            descriptionValid = false;
        } else if (Description.isEmpty()) {
            eventDescriptionErrorMessage.setText("Event description should not be empty");
            descriptionValid = false;
        } else {
            eventDescriptionErrorMessage.setText("");
            descriptionValid = true;
        }
    }

    public void eventDateValidation() {
        String dateString = eventDate.getEditor().getText();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            date = LocalDate.parse(dateString, dateFormatter);
            if (date.isBefore(LocalDate.now())) {
                eventDateErrorMessage.setText("The selected date has already passed.");
                dateValid = false;
            } else {
                eventDateErrorMessage.setText("");
                dateValid = true;
            }
        } catch (DateTimeParseException e) {
            eventDateErrorMessage.setText("Event date should not be empty");
            dateValid = false;
        }
    }

    public void eventStartTimeValidation() {
        String startTimeText = eventStartTime.getText();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

        try {
            startTime = LocalTime.parse(startTimeText, formatter);
            LocalTime earliestStartTime = LocalTime.of(8, 0); // 8:00 AM
            LocalTime latestStartTime = LocalTime.of(22, 0); // 10:00 PM

            if (startTime.isBefore(earliestStartTime)) {
                eventStartTimeErrorMessage.setText("Event start time must be after 8:00 am");
                timeStartValid = false;
            } else if (startTime.isAfter(latestStartTime)) {
                eventStartTimeErrorMessage.setText("Event start time must be before 10:00 pm");
                timeStartValid = false;
            } else {
                eventStartTimeErrorMessage.setText("");
                timeStartValid = true;
            }
        } catch (DateTimeParseException e) {
            eventStartTimeErrorMessage.setText("Incorrect format of time (Exp: 11:00 pm)");
            if (startTimeText.isBlank()) {
                eventStartTimeErrorMessage.setText("Event start time should not be empty");
            }
            timeStartValid = false;
        }
    }

    public void eventEndTimeValidation() {
        String endTimeText = eventEndTime.getText();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

        try {
            startTime = LocalTime.parse(eventStartTime.getText(), formatter);
            endTime = LocalTime.parse(endTimeText, formatter);
            System.out.println("Start Time: " + startTime);
            System.out.println("End Time: " + endTime);

            LocalTime earliestEndTime = LocalTime.of(9, 0); // 9:00 AM
            LocalTime latestEndTime = LocalTime.of(23, 0); // 11:00 PM
            LocalTime oneHourAfterStartTime = startTime.plusHours(1);

            if (endTime.isBefore(earliestEndTime)) {
                eventEndTimeErrorMessage.setText("Event end time must be after 9:00 am");
                timeEndValid = false;
            } else if (endTime.isAfter(latestEndTime)) {
                eventEndTimeErrorMessage.setText("Event end time must be before 11:00 pm");
                timeEndValid = false;
            } else if (endTime.isAfter(oneHourAfterStartTime) || endTime.equals(oneHourAfterStartTime)) {
                eventEndTimeErrorMessage.setText("");
                timeEndValid = true;
            } else {
                eventEndTimeErrorMessage.setText("End time must be at least one hour after start time");
                timeEndValid = false;
            }
        } catch (DateTimeParseException e) {
            eventEndTimeErrorMessage.setText("Incorrect format of time (Exp: 8:00 am)");
            if (endTimeText.isBlank()) {
                eventEndTimeErrorMessage.setText("Event end time should not be empty");
            }
            timeEndValid = false;
        }
    }

    public void eventTitleValidation() throws Exception {
        Title = eventTitle.getText();
        String[] words = Title.split("\\s+");
        if (words.length > 20) {
            eventTitleErrorMessage.setText("Event Title should not have more than 20 words");
            venueValid = false;
        }
        if (Title.isBlank()) {
            eventTitleErrorMessage.setText("Event Title should not be empty");
            titleValid = false;
        } else {
            eventTitleErrorMessage.setText("");
            titleValid = true;
        }
    }

    public void eventVenueValidation() {
        Venue = eventVenue.getText();

        if (Venue.isBlank()) {
            eventVenueErrorMessage.setText("Event venue should not be empty");
            eventVenueLatitude.setText("-");
            eventVenueLongitude.setText("-");
            venueValid = false;
        } else {
            eventVenueErrorMessage.setText("");
            venueValid = true;

            // Cancel any previously scheduled timer task
            if (venueTimer != null) {
                venueTimer.cancel();
            }

            if (!Venue.isEmpty()) {
                // set a delay timer task
                venueTimer = new Timer();
                venueTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Generate coordinates
                        String[] newCoordinates = generateCoordinates();
                        eventVenueLatitude.setText(newCoordinates[0]);
                        eventVenueLongitude.setText(newCoordinates[1]);
                    }
                }, 2000); // Delay in milliseconds
            }
        }
    }

    @FXML
    public void createEvent(ActionEvent event) throws Exception {
        eventCreationInProgress = true; // Mark event creation as in progress

        eventTitleValidation();
        eventDescriptionValidation();
        eventVenueValidation();
        eventDateValidation();
        eventStartTimeValidation();
        eventEndTimeValidation();

        if (!titleValid || !descriptionValid || !venueValid || !dateValid || !timeStartValid || !timeEndValid) {
            showError("Please make sure you correct all the fields stated.");
            if (Title.isBlank()) eventTitleErrorMessage.setText("Event Title should not be empty");
            if (Description.isBlank()) eventDescriptionErrorMessage.setText("Event Description should not be empty");
            if (Venue.isBlank()) eventVenueErrorMessage.setText("Event venue should not be empty");
            if (eventDate == null) eventDateErrorMessage.setText("Event date should not be empty");
            if (startTime == null) eventStartTimeErrorMessage.setText("Event start time should not be empty");
            if (endTime == null) eventEndTimeErrorMessage.setText("Event end time should not be empty");
            eventCreationInProgress = false; // Reset flag on validation failure
        } else {
            // store event or proceed further
            storeUser(event);
        }
    }

    @FXML
    public void homeButton(ActionEvent event) throws Exception {
        Parent root2 = FXMLLoader.load(getClass().getResource("homePage.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene homeScene = new Scene(root2, stage.getScene().getWidth(), stage.getScene().getHeight());
        stage.setScene(homeScene);
    }

    public void quizButton(ActionEvent event) throws Exception {
        attemptQuizController attemptQuizController = new attemptQuizController();
        attemptQuizController.attemptQuizStartUp(event);
    }

    public void eventButton(ActionEvent event) throws Exception {
        viewEventController viewEventController = new viewEventController();
        viewEventController.viewEventStartUp(event);
    }

    public void bookingButton(ActionEvent event) throws Exception {
        bookingController bookingController = new bookingController();
        bookingController.bookingStartUp(event);
    }
    public void leaderBoardButton(ActionEvent event) throws Exception {
        leaderBoardController leaderBoardController = new leaderBoardController();
        leaderBoardController.leaderBoardStartUp(event);
    }

    public void profileButton(ActionEvent event) throws Exception {
        personalProfileController personalProfileController = new personalProfileController();
        personalProfileController.personalProfileStartUp(event);
    }

    public void logOutButton(ActionEvent event) throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log Out");
        alert.setContentText("Are you sure want to log out?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isEmpty()) {
            System.out.println("Alert closed");

        } else if (result.get() == ButtonType.OK) {
            loginController loginController = new loginController();
            loginController.loginStartUp(event);

        } else if (result.get() == ButtonType.CANCEL) ;

    }

    private void showCreateEventSuccess() {
        Alert alertSU = new Alert(AlertType.INFORMATION);
        alertSU.setTitle("Successful");
        alertSU.setHeaderText(null);
        alertSU.setContentText("Event " + Title + " successfully created.");

        alertSU.showAndWait();

        eventCreationInProgress = false;
    }

    public void showError(String errorMessage) {
        Alert alertError = new Alert(AlertType.ERROR);
        alertError.setTitle("Error");
        alertError.setHeaderText(null);
        alertError.setContentText(errorMessage);

        alertError.showAndWait();
    }

    public static String[] generateCoordinates() { // Generate coordinates for venue
        Random random = new Random();

        String[] coordination = new String[2];

        for (int i = 0; i < coordination.length; i++) {
            double coords = -500.0 + (1000.0 * random.nextDouble());
            coordination[i] = String.format("%.2f", coords);
        }

        return coordination;
    }

    public void storeUser(ActionEvent event){
        String fileName = "src/main/java/Data/newevent.csv";


        try{

            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))){
                writer.println(Title + "," + Description + "," + Venue + "," + eventVenueLatitude.getText()
                        + "," + eventVenueLongitude.getText()+ "," + date+ "," +startTime+","+endTime);
                writer.flush();
                showCreateEventSuccess();
                createEventController createEventController = new createEventController();
                createEventController.createEventStartUp(event);

            }catch (IOException e){
                showError("Error appending new event data to file: " + e.getMessage());
            }
        }catch (Exception e){
            showError("Error storing new event data: " + e.getMessage());
        }

    }
}