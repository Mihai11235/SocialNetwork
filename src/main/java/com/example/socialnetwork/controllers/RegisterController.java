package com.example.socialnetwork.controllers;

import com.example.socialnetwork.HelloApplication;
import com.example.socialnetwork.business.FriendRequestsService;
import com.example.socialnetwork.business.MessagesService;
import com.example.socialnetwork.business.Service;
import com.example.socialnetwork.domain.validators.ValidationException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;

public class RegisterController {
    Service service;
    MessagesService messagesService;
    FriendRequestsService friendRequestsService;
    public void setService(Service service, MessagesService messagesService, FriendRequestsService friendRequestsService){
        this.service = service;
        this.messagesService = messagesService;
        this.friendRequestsService = friendRequestsService;
    }

    @FXML
    Hyperlink loginButton;
    @FXML
    Button registerButton;
    @FXML
    TextField firstName;
    @FXML
    TextField lastName;
    @FXML
    TextField username;
    @FXML
    TextField email;
    @FXML
    TextField password;



    @FXML
    void openLoginWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        Stage stage = (Stage)loginButton.getScene().getWindow();
        stage.setScene(scene);

        LoginController loginController = fxmlLoader.getController();
        loginController.setService(service, messagesService, friendRequestsService);
    }

    @FXML
    void registerUser(){
        try {
            service.add(firstName.getText(), lastName.getText(), username.getText(), email.getText(), password.getText());
            openLoginWindow();
        }
        catch (ValidationException validationException){
            Alert alert = new Alert(Alert.AlertType.valueOf(validationException.toString()));
            alert.show();
        }
        catch (RuntimeException runtimeException){
            Alert alert = new Alert(Alert.AlertType.valueOf(runtimeException.toString()));
            alert.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
