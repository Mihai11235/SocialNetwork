package com.example.socialnetwork.controllers;

import com.example.socialnetwork.HelloApplication;
import com.example.socialnetwork.business.FriendRequestsService;
import com.example.socialnetwork.business.MessagesService;
import com.example.socialnetwork.business.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BubbleChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    Long currentUser;
    Service service;
    MessagesService messagesService;
    FriendRequestsService friendRequestsService;

    public void setService(Service service, MessagesService messagesService, FriendRequestsService friendRequestsService){
        this.service = service;
        this.messagesService = messagesService;
        this.friendRequestsService = friendRequestsService;
    }

    @FXML
    Hyperlink registerButton;
    @FXML
    Button loginButton;
    @FXML
    TextField username;
    @FXML
    TextField password;
    @FXML
    VBox loginBox;

    @FXML
    void openRegisterWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("register.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        Stage stage = (Stage)registerButton.getScene().getWindow();
        stage.setScene(scene);

        RegisterController registerController = fxmlLoader.getController();
        registerController.setService(service, messagesService, friendRequestsService);
    }

    @FXML
    void openApp() throws IOException {
        currentUser = service.login(username.getText(), password.getText());
        if(currentUser != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("home.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 500);
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(scene);

            HomeController homeController = fxmlLoader.getController();
            homeController.setService(service, messagesService, friendRequestsService, currentUser);
        }
        else{
            if(loginBox.getChildren().size() == 6){
                loginBox.getChildren().remove(3);
            }
            Label label = new Label("Invalid Credentials!");
            label.setStyle("-fx-border-color: #910707");
            loginBox.getChildren().add(3, label);
        }
    }

    @FXML
    void pressedEnter(KeyEvent event) throws IOException {
        if(event.getCode() == KeyCode.ENTER){
            openApp();
        }
    }

    @FXML
    void openWindow() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        stage.setScene(scene);

        LoginController loginController = fxmlLoader.getController();
        loginController.setService(service, messagesService, friendRequestsService);
        stage.show();
    }
}
