package com.example.socialnetwork.business;

import com.example.socialnetwork.domain.FriendRequest;
import com.example.socialnetwork.domain.FriendRequestDTO;
import com.example.socialnetwork.domain.Tuple;
import com.example.socialnetwork.persistence.DatabaseFriendRequestsRepository;
import com.example.socialnetwork.persistence.paging.Page;
import com.example.socialnetwork.persistence.paging.Pageable;
import com.example.socialnetwork.utils.events.AddEvent;
import com.example.socialnetwork.utils.events.DeleteEvent;
import com.example.socialnetwork.utils.events.Event;
import com.example.socialnetwork.utils.observer.Observable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestsService extends Observable<Event> {
    DatabaseFriendRequestsRepository databaseFriendRequestsRepository;

    public FriendRequestsService(DatabaseFriendRequestsRepository databaseFriendRequestsRepository) {
        this.databaseFriendRequestsRepository = databaseFriendRequestsRepository;
    }

    public void add(Long from, Long to){
        databaseFriendRequestsRepository.add(new FriendRequest(from, to));
        notifyObservers(new AddEvent());
    }

    public Iterable<FriendRequest> getAllRequestsOfUser(Long user){
        return databaseFriendRequestsRepository.getAllRequestsOfUser(user);
    }

    public Iterable<FriendRequest> getAllSendRequestsOfUser(Long user){
        return databaseFriendRequestsRepository.getAllSendRequestsOfUser(user);
    }

    public void remove(Long user1, Long user2){
        databaseFriendRequestsRepository.delete(new Tuple<>(user1, user2));
        notifyObservers(new DeleteEvent());
    }

    public Page<FriendRequest> findAllOnPage(Pageable pageable, Long user) {
        return databaseFriendRequestsRepository.findAllOnPage(pageable, user);
    }
}
