package com.example.socialnetwork;

import com.example.socialnetwork.business.FriendRequestsService;
import com.example.socialnetwork.business.MessagesService;
import com.example.socialnetwork.business.Service;
//import com.example.socialnetwork.controllers.HelloController;
import com.example.socialnetwork.controllers.LoginController;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.domain.validators.UserValidator;
import com.example.socialnetwork.domain.validators.Validator;
import com.example.socialnetwork.persistence.*;
import com.example.socialnetwork.utils.observer.Observer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Repository<Long, User> repo = new InMemoryRepository<>();
        DatabaseUserRepository DBUserRepo = new DatabaseUserRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "Unbreakableqwertyuiop08.");
        DatabaseFriendshipRepository DBFriendshipRepo = new DatabaseFriendshipRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "Unbreakableqwertyuiop08.");
        DatabaseMessageRepository DBMessageRepo = new DatabaseMessageRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "Unbreakableqwertyuiop08.");
        DatabaseFriendRequestsRepository DBFriendRequestsRepository = new DatabaseFriendRequestsRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "Unbreakableqwertyuiop08.");
        Validator<User> validator = UserValidator.getInstance();
        Service service = new Service(DBUserRepo, DBFriendshipRepo,validator);
        MessagesService messagesService = new MessagesService(DBMessageRepo);
        FriendRequestsService friendRequestsService = new FriendRequestsService(DBFriendRequestsRepository);

//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
//        stage.setTitle("Social Network");
//        stage.setScene(scene);
//
//        HelloController helloController = fxmlLoader.getController();
//        helloController.setService(service);
//        stage.show();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        stage.setScene(scene);

        LoginController loginController = fxmlLoader.getController();
        loginController.setService(service, messagesService, friendRequestsService);
        stage.show();
    }

    public static void main(String[] args) {
//        UserInterface ui = new UserInterface(service);
//
//        ui.runUi();
//
//        System.out.println("ok");
        launch();
    }
}