package com.example.socialnetwork.controllers;

import com.example.socialnetwork.HelloApplication;
import com.example.socialnetwork.business.MessagesService;
import com.example.socialnetwork.business.Service;
import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChatsController {
    Long currentUser;
    Service service;
    MessagesService messagesService;
    void setService(Service service, MessagesService messagesService, Long currentUser){
        this.service = service;
        this.messagesService = messagesService;
        this.currentUser = currentUser;
    }

    //Thread in which messages from the server are received
    public void startReadingThread() {
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    Long senderId = socketIn.readLong();
                    String receivedMessage = socketInMessage.readLine();

                    if(senderId.equals(Long.parseLong(rightChatBox.getId()))){
                        Platform.runLater(() -> {
                            //update(new AddEvent());
                            Label msgLabel = new Label(receivedMessage);
                            msgLabel.setWrapText(true);
                            msgLabel.maxWidthProperty().bind(chatAnchorPane.prefWidthProperty().divide(2));
                            msgLabel.getStyleClass().addAll("other-user-bubble", "text-area", "text-area-focused");
                            AnchorPane anchorPane = new AnchorPane();
                            anchorPane.getChildren().add(msgLabel); AnchorPane.setLeftAnchor(msgLabel, 0.0);

                            rightChatBox.getChildren().add(anchorPane);
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    Message repliedTo = null;

    @FXML
    private VBox rightChatBox;
    @FXML
    private VBox chatsBox;
    @FXML
    private ScrollPane chatsBoxSP;
    @FXML
    private ScrollPane chatBoxSP;
    @FXML
    private AnchorPane chatAnchorPane;
    @FXML
    private TextField messageField;

    //When enter is pressed for the message to be sent
    @FXML
    private void pressedEnter(KeyEvent keyEvent) throws IOException {
        if(keyEvent.getCode() == KeyCode.ENTER){
            if(chats.get(0).getChildren().size() == 1) {
                messagesService.add(currentUser, Long.parseLong(rightChatBox.getId()), messageField.getText(), repliedTo);
                repliedTo = null;
            }
            else{
                for(HBox hBox : chats){
                    RadioButton rb = (RadioButton) hBox.getChildren().get(0);
                    if(rb.isSelected())
                        messagesService.add(currentUser, Long.parseLong(hBox.getId()), messageField.getText(), null);
                }
            }

            Label msgLabel = new Label(messageField.getText());
            msgLabel.setWrapText(true);
            msgLabel.maxWidthProperty().bind(chatAnchorPane.prefWidthProperty().divide(2));
            msgLabel.getStyleClass().addAll("user-input", "text-area", "text-area-focused");
            AnchorPane anchorPane = new AnchorPane();
            anchorPane.getChildren().add(msgLabel); AnchorPane.setRightAnchor(msgLabel, 0.0);

            rightChatBox.getChildren().add(anchorPane);

            socketOut.writeLong(Long.parseLong(rightChatBox.getId()));
            socketOutMessage.write(messageField.getText());
            socketOutMessage.newLine();
            socketOutMessage.flush();


            VBox currectChatBoxDetails = (VBox) currentChatBox.getChildren().get(0);
            Label label = (Label) currectChatBoxDetails.getChildren().get(1);
            label.setText("Me : " + messageField.getText());

            messageField.clear();

        }
    }

    //When the left ChatBox for a specific user is clicked, the chat with that user is initialized
    //All messages between the current user and the selected user are loaded
    void initializeChat(List<Message> chat) {
        Scene scene = chatsBoxSP.getScene();
        scene.getStylesheets().add(HelloApplication.class.getResource("chatBubbles.css").toExternalForm());
        for(Message message : chat) {
            Label msgLabel;
            if(message.getReply() != null)
                msgLabel = new Label("Replied to: " + message.getReply().getMessage() + " : \n" + message.getMessage());
            else
                msgLabel = new Label(message.getMessage());


            msgLabel.setId(message.getId().toString());
            msgLabel.maxWidthProperty().bind(chatAnchorPane.prefWidthProperty().divide(2));
            msgLabel.setWrapText(true);
            AnchorPane anchorPane = new AnchorPane();
            rightChatBox.getChildren().add(anchorPane);

            if (message.getFrom().equals(currentUser)) {
                anchorPane.getChildren().add(msgLabel);
                AnchorPane.setRightAnchor(msgLabel, 0.0);
                msgLabel.getStyleClass().addAll("user-input", "text-area", "text-area-focused");
            } else {
                anchorPane.getChildren().add(msgLabel);
                AnchorPane.setLeftAnchor(msgLabel, 0.0);
                msgLabel.getStyleClass().addAll("other-user-bubble", "text-area", "text-area-focused");
            }

            MenuItem replyItem = new MenuItem("Reply");
            ContextMenu contextMenu = new ContextMenu();
            contextMenu.getItems().add(replyItem);

            msgLabel.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY)
                    contextMenu.show(msgLabel, event.getScreenX(), event.getScreenY());
            });

            replyItem.setOnAction(event -> {
                repliedTo = new Message(message.getFrom(), message.getTo(), message.getMessage(), message.getReply());
                repliedTo.setId(message.getId());
            });
        }
    }

    @FXML
    SplitPane splitPane;
    @FXML
    Button button;
    List<HBox> chats;
    HBox currentChatBox;

    // makes the divider disappear
    // sets bindings
    void setBindings(){
        for (Node node : splitPane.lookupAll(".split-pane-divider")) {
            node.setVisible(false);
        }

        chatsBox.prefHeightProperty().bind(chatsBoxSP.heightProperty());
        chatsBox.prefWidthProperty().bind(chatsBoxSP.widthProperty());

        chatBoxSP.vvalueProperty().bind(chatAnchorPane.heightProperty()); //scrolleaza pana jos in conversatie cand se trimite un mesaj

        //chatAnchorPane.prefHeightProperty().bind(chatBoxSP.heightProperty());
        chatAnchorPane.prefWidthProperty().bind(chatBoxSP.widthProperty());
    }


    Socket socket;
    DataInputStream socketIn;
    BufferedReader socketInMessage;
    DataOutputStream socketOut;
    BufferedWriter socketOutMessage;
    void initialize() throws IOException {

        socket = new Socket("127.0.0.1", 1234);

        socketIn = new DataInputStream(socket.getInputStream());
        socketInMessage = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        socketOut = new DataOutputStream(socket.getOutputStream());
        socketOutMessage = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

        socketOut.writeLong(currentUser);
        startReadingThread();




        chatBoxSP.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setBindings();

        List<User> friends = service.getFriends(currentUser);

        chats = new ArrayList<>();
        for(User user : friends){
            HBox leftChatBox = new HBox();
            currentChatBox = leftChatBox;
            chatsBox.getChildren().add(leftChatBox);

            leftChatBox.setId(user.getId().toString());
            leftChatBox.setStyle("-fx-background-color: #035656");
//            leftChatBox.getChildren().add();

            VBox leftChatBoxDetails = new VBox();
            leftChatBox.getChildren().add(leftChatBoxDetails);

            Text usernameText = new Text(user.getUsername());
            TextFlow usernameLabel = new TextFlow(usernameText);
            usernameLabel.setStyle("-fx-font-weight: bold");
            usernameLabel.setStyle("-fx-font-size: 100");
            usernameLabel.setStyle("-fx-font-family: Ebrima");
            leftChatBoxDetails.getChildren().add(usernameLabel);
            leftChatBoxDetails.getChildren().add(new Label());

            //leftChatBoxDetails.getChildren().add(new Label(chat.get(chat.size() - 1).getMessage()));
            chats.add(leftChatBox);

            leftChatBox.setOnMouseClicked(x->{
                rightChatBox = new VBox();
                rightChatBox.setSpacing(10);
                rightChatBox.setId(user.getId().toString());
                rightChatBox.prefWidthProperty().bind(chatAnchorPane.prefWidthProperty());

                currentChatBox = leftChatBox;
                List<Message> messages = messagesService.getMessagesBetween2Users(currentUser, user.getId());
                initializeChat(messages);

                Label label = (Label)(leftChatBoxDetails.getChildren().get(1));
                String username = service.search(messages.get(messages.size()-1).getFrom()).getUsername();
                if(username.equals(service.search(currentUser).getUsername()))
                    username = "Me";
                label.setText(username + " : " +  messages.get(messages.size()-1).getMessage());

                chatAnchorPane.getChildren().clear();
                chatAnchorPane.getChildren().add(rightChatBox);
                AnchorPane.setTopAnchor(rightChatBox, 0.0);
                AnchorPane.setBottomAnchor(rightChatBox, 0.0);
                AnchorPane.setLeftAnchor(rightChatBox, 0.0);
                AnchorPane.setRightAnchor(rightChatBox, 30.0);
            });
        }
        currentChatBox.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, javafx.scene.input.MouseButton.PRIMARY, 1,
                true, true, true, true, true, true, true, true, true, true, null));
    }

    //called when the chat window is closed and
    //closes the socket so that the server can delete the <userId, socket> key pair from the map
    public void shutdown() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    VBox leftPane;


    //Adds radio buttons for multiple selection of users(for multiple messages sending)
    @FXML
    void addRadioButtons(){
        if(chats.get(0).getChildren().size() == 2) {
            for (HBox hBox : chats) {
                hBox.getChildren().remove(0);
            }
            leftPane.getChildren().remove(1);
        }
        else {
            for (HBox hBox : chats) {
                hBox.getChildren().add(0, new RadioButton());
            }
            Button sendMessageButton = new Button("SendAll");
            leftPane.getChildren().add(sendMessageButton);
            sendMessageButton.setOnMouseClicked(x->{
                rightChatBox = new VBox();
                rightChatBox.setSpacing(10);
                rightChatBox.prefWidthProperty().bind(chatAnchorPane.prefWidthProperty());

                chatAnchorPane.getChildren().clear();
                chatAnchorPane.getChildren().add(rightChatBox);
                AnchorPane.setTopAnchor(rightChatBox, 0.0);
                AnchorPane.setBottomAnchor(rightChatBox, 0.0);
                AnchorPane.setLeftAnchor(rightChatBox, 0.0);
                AnchorPane.setRightAnchor(rightChatBox, 30.0);
            });
        }
    }







//    void initialize2(){
//        chatBoxSP.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//        setBindings();
//
//        allChats = messagesService.getUserMessages(currentUser);
//        Map<HBox, VBox> map = new HashMap<>(); // tine chat-urile
//
//        List<User> friends = service.getFriends(currentUser);
//
//        boolean first = true;
//        VBox firstChat = null;
//        for(List<Message> chat : allChats) {
//            String username = "";
//            Long userId = null;
//
//            try {
//                User user = chat.get(0).getFrom().equals(currentUser) ? service.search(chat.get(0).getTo()) : service.search(chat.get(0).getFrom());
//                username = user.getUsername();
//                userId = user.getId();
//                friends.remove(user);
//            }catch (RuntimeException runtimeException) {
//                Alert alert = new Alert(Alert.AlertType.WARNING);
//                alert.setContentText(runtimeException.toString());
//                alert.show();
//            }
//
////left side of split pane
//            HBox leftChatBox = new HBox();
//            chatsBox.getChildren().add(leftChatBox);
//
//            leftChatBox.setId(userId.toString());
//            leftChatBox.setStyle("-fx-background-color: #035656");
////            leftChatBox.getChildren().add();
//
//            VBox leftChatBoxDetails = new VBox();
//            leftChatBox.getChildren().add(leftChatBoxDetails);
//
//            Text usernameText = new Text(username);
//            TextFlow usernameLabel = new TextFlow(usernameText);
//            usernameLabel.setStyle("-fx-font-weight: bold");
//            usernameLabel.setStyle("-fx-font-size: 100");
//            usernameLabel.setStyle("-fx-font-family: Ebrima");
//            leftChatBoxDetails.getChildren().add(usernameLabel);
//
//            leftChatBoxDetails.getChildren().add(new Label(chat.get(chat.size() - 1).getMessage()));
//
////right side of split pane
//            rightChatBox = new VBox();
//            rightChatBox.setSpacing(10);
//            rightChatBox.setId(userId.toString());
//            rightChatBox.prefWidthProperty().bind(chatAnchorPane.prefWidthProperty());
//
//            map.put(leftChatBox, rightChatBox);
//            initializeChat(chat);
//
//            if(first){
//                first = false;
//                firstChat = rightChatBox;
//            }
//
//
//            leftChatBox.setOnMouseClicked(x->{
//                rightChatBox = map.get(leftChatBox);
//
//                chatAnchorPane.getChildren().clear();
//                chatAnchorPane.getChildren().add(rightChatBox);
//                AnchorPane.setTopAnchor(rightChatBox, 0.0);
//                AnchorPane.setBottomAnchor(rightChatBox, 0.0);
//                AnchorPane.setLeftAnchor(rightChatBox, 0.0);
//                AnchorPane.setRightAnchor(rightChatBox, 30.0);
//
//            });
//        }
//        rightChatBox = firstChat;
//        chatAnchorPane.getChildren().clear();
//        chatAnchorPane.getChildren().add(rightChatBox);
//        AnchorPane.setTopAnchor(rightChatBox, 0.0);
//        AnchorPane.setBottomAnchor(rightChatBox, 0.0);
//        AnchorPane.setLeftAnchor(rightChatBox, 0.0);
//        AnchorPane.setRightAnchor(rightChatBox, 30.0);
//        for(User user : friends){
//            HBox leftChatBox = new HBox();
//            chatsBox.getChildren().add(leftChatBox);
//
//            leftChatBox.setId(user.getId().toString());
//            leftChatBox.setStyle("-fx-background-color: #035656");
//            leftChatBox.getChildren().add(new ImageView());
//
//            VBox leftChatBoxDetails = new VBox();
//            leftChatBox.getChildren().add(leftChatBoxDetails);
//
//            Text usernameText = new Text(user.getUsername());
//            TextFlow usernameLabel = new TextFlow(usernameText);
//            usernameLabel.setStyle("-fx-font-weight: bold");
//            usernameLabel.setStyle("-fx-font-size: 50");
//            usernameLabel.setStyle("-fx-font-family: Ebrima");
//            leftChatBoxDetails.getChildren().add(usernameLabel);
//
//            leftChatBoxDetails.getChildren().add(new Label("Start new conversation..."));
//
//            rightChatBox = new VBox();
//            rightChatBox.setId(user.getId().toString());
//            map.put(leftChatBox, rightChatBox);
//
//            leftChatBox.setOnMouseClicked(x->{
//                rightChatBox = map.get(leftChatBox);
//
//                chatAnchorPane.getChildren().clear();
//                chatAnchorPane.getChildren().add(rightChatBox);
//                AnchorPane.setTopAnchor(rightChatBox, 0.0);
//                AnchorPane.setBottomAnchor(rightChatBox, 0.0);
//                AnchorPane.setLeftAnchor(rightChatBox, 0.0);
//                AnchorPane.setRightAnchor(rightChatBox, 30.0);
//
//            });
//        }
//    }

}
