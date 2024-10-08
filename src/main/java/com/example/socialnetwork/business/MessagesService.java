package com.example.socialnetwork.business;

import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.persistence.DatabaseMessageRepository;
import com.example.socialnetwork.utils.events.AddEvent;
import com.example.socialnetwork.utils.events.Event;
import com.example.socialnetwork.utils.observer.Observable;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessagesService extends Observable<Event> {
    DatabaseMessageRepository databaseMessageRepository;

    public MessagesService(DatabaseMessageRepository databaseMessageRepository) {
        this.databaseMessageRepository = databaseMessageRepository;
    }

    public void add(Long from, Long to, String message, Message reply){
        Message m = new Message(from, to, message, reply);
        databaseMessageRepository.add(m);
        notifyObservers(new AddEvent());
    }

    public Message find(Long id){
        return databaseMessageRepository.findOne(id).get();
    }

    public List<Message> getMessagesBetween2Users(Long user1, Long user2){
        return StreamSupport.stream(databaseMessageRepository.getMessagesBetween2Users(user1, user2).spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<List<Message>> getUserMessages(Long currentUser){
        List<List<Message>> allChats = new ArrayList<>();

        Iterable<Message> messages = databaseMessageRepository.getAllFromUser(currentUser);
        List<Message> distinctMessages = StreamSupport.stream(messages.spliterator(), false)
                .distinct()
                .collect(Collectors.toList());


        System.out.println(distinctMessages.size());
        for (Message distinctMessage : distinctMessages) {
            Long user = distinctMessage.getTo().equals(currentUser) ? distinctMessage.getFrom() : distinctMessage.getTo();

            allChats.add(StreamSupport.stream(messages.spliterator(), false)
                    .filter(x -> x.getTo().equals(user) || x.getFrom().equals(user))
                    .sorted(Comparator.comparing(Message::getData))
                    .collect(Collectors.toList()));
        }
        return allChats;
    }
}
