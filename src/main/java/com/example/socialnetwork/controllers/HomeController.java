package com.example.socialnetwork.controllers;

import com.example.socialnetwork.HelloApplication;
import com.example.socialnetwork.business.FriendRequestsService;
import com.example.socialnetwork.business.MessagesService;
import com.example.socialnetwork.business.Service;
import com.example.socialnetwork.domain.FriendRequest;
import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.persistence.DatabaseMessageRepository;
import com.example.socialnetwork.utils.events.Event;
import com.example.socialnetwork.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class HomeController  implements Observer<Event> {
    Long currentUser;
    Service service;
    MessagesService messagesService;
    FriendRequestsService friendRequestsService;
    void setService(Service service, MessagesService messagesService, FriendRequestsService friendRequestsService, Long currentUser){
        this.service = service;
        this.messagesService = messagesService;
        this.friendRequestsService = friendRequestsService;
        this.currentUser = currentUser;
        initModel();
        friendRequestsService.addObserver(this);
    }

    ObservableList<User> model = FXCollections.observableArrayList();
    @FXML
    Button chatsButton;
    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User, Long> idColumn;
    @FXML
    TableColumn<User, String> firstNameColumn;
    @FXML
    TableColumn<User, String> lastNameColumn;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableView.setItems(model);
    }

    private void initModel(){
        Iterable<User> friends = service.getFriends(currentUser);
        Iterable<FriendRequest> friendRequestsSent = friendRequestsService.getAllSendRequestsOfUser(currentUser);
        Iterable<FriendRequest> friendRequestsReceived = friendRequestsService.getAllRequestsOfUser(currentUser);
        Iterable<User> users = service.getAll();
        List<User> userList= StreamSupport.stream(users.spliterator(), false)
                .filter(x->{
                    if(x.getId().equals(currentUser))
                        return false;
                    for(User user : friends)
                        if(user.getId().equals(x.getId()))
                            return false;
                    for(FriendRequest friendRequest : friendRequestsSent)
                        if(x.getId().equals(friendRequest.getUser2()))
                            return false;
                    for(FriendRequest friendRequest : friendRequestsReceived)
                        if(x.getId().equals(friendRequest.getUser1()))
                            return false;
                    return true;
                })
                .collect(Collectors.toList());
        model.setAll(userList);
    }

    @FXML
    void sendFriendRequest(){
        User user = tableView.getSelectionModel().getSelectedItem();
        friendRequestsService.add(currentUser, user.getId());
        model.remove(user);
        initModel();
    }

    @FXML
    void openChats() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("chats.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        Stage stage = (Stage)chatsButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setMinHeight(200);
        stage.setMinWidth(600);

        ChatsController chatsController = fxmlLoader.getController();
        chatsController.setService(service, messagesService, currentUser);
        chatsController.initialize();

        stage.setOnHidden(e -> chatsController.shutdown());
    }

    @FXML
    void openProfile() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("profile.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        Stage stage = (Stage)chatsButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setMinHeight(200);
        stage.setMinWidth(600);

        ProfileController profileController = fxmlLoader.getController();
        profileController.setService(service, messagesService, friendRequestsService, currentUser);
        profileController.initialize();
    }

    @Override
    public void update(Event event) {
        initModel();
    }
}
