package com.example.socialnetwork.controllers;

import com.example.socialnetwork.HelloApplication;
import com.example.socialnetwork.business.FriendRequestsService;
import com.example.socialnetwork.business.MessagesService;
import com.example.socialnetwork.business.Service;
import com.example.socialnetwork.domain.FriendRequest;
import com.example.socialnetwork.domain.FriendRequestDTO;
import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.persistence.paging.Page;
import com.example.socialnetwork.persistence.paging.Pageable;
import com.example.socialnetwork.utils.events.Event;
import com.example.socialnetwork.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ProfileController implements Observer<Event> {


    Long currentUser;
    Service service;
    MessagesService messagesService;
    FriendRequestsService friendRequestsService;
    void setService(Service service, MessagesService messagesService, FriendRequestsService friendRequestsService, Long currentUser){
        this.service = service;
        this.messagesService = messagesService;
        this.friendRequestsService = friendRequestsService;
        friendRequestsService.addObserver(this);
        this.currentUser = currentUser;
        initModel();
    }

    @FXML
    HBox logo;
    @FXML
    void openApp() throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("home.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 500);
            Stage stage = (Stage) logo.getScene().getWindow();
            stage.setScene(scene);

            HomeController homeController = fxmlLoader.getController();
            homeController.setService(service, messagesService, friendRequestsService, currentUser);
    }

    @FXML
    Button chatsButton;
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
    TableView<FriendRequestDTO> tableView;
    @FXML
    TableColumn<FriendRequestDTO, String> usernameColumn;
    @FXML
    TableColumn<FriendRequestDTO, LocalDateTime> dateColumn;

    ObservableList<FriendRequestDTO> model = FXCollections.observableArrayList();

    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableView.setItems(model);
    }

    private int pageSize = 2;
    private int currentPage = 0;
    private int totalNrOfElems = 0;

    @FXML
    Button prevButton;
    @FXML
    Button nextButton;
    @FXML
    Label pageNumberLabel;
    private void initModel(){
//        Iterable<FriendRequest> friendRequests = friendRequestsService.getAllRequestsOfUser(currentUser);
//
//        List<FriendRequestDTO> friendRequestDTOS = new ArrayList<>();
//        for(FriendRequest friendRequest : friendRequests){
//            User user = service.search(friendRequest.getUser1());
//            friendRequestDTOS.add(new FriendRequestDTO(user.getId(), user.getUsername(), friendRequest.getDate()));
//        }
//
//        model.setAll(friendRequestDTOS);

        Page<FriendRequest> page = friendRequestsService.findAllOnPage(new Pageable(currentPage, pageSize), currentUser);

        int maxPage = (int) Math.ceil((double) page.getTotalNrOfElems() / pageSize) - 1;

        if (maxPage == -1) {
            maxPage = 0;
        }

        if (currentPage > maxPage) {
            currentPage = maxPage;

            page = friendRequestsService.findAllOnPage(new Pageable(currentPage, pageSize), currentUser);
        }

        List<FriendRequestDTO> friendRequestDTOS = new ArrayList<>();
        Iterable<FriendRequest> friendRequests = page.getElementsOnPage();
        for(FriendRequest friendRequest : friendRequests){
            User user = service.search(friendRequest.getUser1());
            friendRequestDTOS.add(new FriendRequestDTO(user.getId(), user.getUsername(), friendRequest.getDate()));
        }


        model.setAll(friendRequestDTOS);

        totalNrOfElems = page.getTotalNrOfElems();

        prevButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage + 1) * pageSize >= totalNrOfElems);

        pageNumberLabel.setText("Page " + (currentPage + 1) + "/" + (maxPage + 1));
    }

    @FXML
    private void acceptRequest(){
        service.addFriend(currentUser, tableView.getSelectionModel().getSelectedItem().getUserId());
        friendRequestsService.remove(currentUser, tableView.getSelectionModel().getSelectedItem().getUserId());
    }

    @FXML
    private void removeRequest(){
        friendRequestsService.remove(currentUser, tableView.getSelectionModel().getSelectedItem().getUserId());
    }


    @Override
    public void update(Event event) {
        initModel();
    }

    public void onPressPrev(ActionEvent actionEvent) {
        currentPage--;
        initModel();
    }
    public void onPressNext(ActionEvent actionEvent) {
        currentPage++;
        initModel();
    }

    @FXML
    TextField pageSizeLabel;

    public void resetModel(ActionEvent actionEvent){
        currentPage = 0;
        try {
            pageSize = Integer.parseInt(pageSizeLabel.getText());
        }
        catch (NumberFormatException n){
            pageSizeLabel.clear();
        }
        initModel();
    }
}
