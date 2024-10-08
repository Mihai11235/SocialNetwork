//package com.example.socialnetwork.controllers;
//
//import com.example.socialnetwork.business.Service;
//import com.example.socialnetwork.domain.User;
//import com.example.socialnetwork.utils.events.Event;
//import com.example.socialnetwork.utils.observer.Observer;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.StreamSupport;
//
//public class HelloController implements Observer<Event> {
//
//    private Service service;
//    ObservableList<User> model = FXCollections.observableArrayList();
//
//    @FXML
//    TableView<User> tableView;
//    @FXML
//    TableColumn<User, Long> idColumn;
//    @FXML
//    TableColumn<User, String> firstNameColumn;
//    @FXML
//    TableColumn<User, String> lastNameColumn;
//    @FXML
//    private TextField firstName;
//    @FXML
//    private TextField lastName;
//
//
//    @Override
//    public void update(Event e){
//        initModel();
//    }
//
//    public void setService(Service service){
//        this.service = service;
//        service.addObserver(this);
//        initModel();
//    }
//
//    @FXML
//    public void initialize() {
//        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
//        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
//        tableView.setItems(model);
//    }
//
//    private void initModel(){
//        Iterable<User> users = service.getAll();
//        List<User> userList= StreamSupport.stream(users.spliterator(), false)
//                .collect(Collectors.toList());
//        model.setAll(userList);
//    }
//
//    @FXML
//    private void addButtonClicked(){
//        try {
//            service.add(firstName.getText(), lastName.getText());
//            firstName.clear();
//            lastName.clear();
//        }
//        catch (RuntimeException r){
//            Alert a = new Alert(Alert.AlertType.WARNING);
//            a.setContentText(r.toString());
//            a.show();
//        }
//    }
//
//    @FXML
//    private void deleteButtonClicked(){
//        try {
//            User selected = tableView.getSelectionModel().getSelectedItem();
//            if(selected != null){
//                service.delete(selected.getId());
//            }
//            firstName.clear();
//            lastName.clear();
//        }
//        catch (RuntimeException r){
//            Alert a = new Alert(Alert.AlertType.WARNING);
//            a.setContentText(r.toString());
//            a.show();
//        }
//    }
//
//    @FXML
//    private void updateButtonClicked(){
//        try {
//            User selected = tableView.getSelectionModel().getSelectedItem();
//            service.update(selected.getId(), firstName.getText(), lastName.getText());
//            firstName.clear();
//            lastName.clear();
//        }
//        catch (RuntimeException r){
//            Alert a = new Alert(Alert.AlertType.WARNING);
//            a.setContentText(r.toString());
//            a.show();
//        }
//    }
//
//
//
//
//
//}