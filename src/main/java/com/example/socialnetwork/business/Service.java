package com.example.socialnetwork.business;


import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.domain.validators.Validator;
import com.example.socialnetwork.persistence.DatabaseFriendshipRepository;
import com.example.socialnetwork.persistence.DatabaseUserRepository;
import com.example.socialnetwork.persistence.Repository;
import com.example.socialnetwork.utils.Encryption;
import com.example.socialnetwork.utils.Graph;
import com.example.socialnetwork.utils.events.AddEvent;
import com.example.socialnetwork.utils.events.DeleteEvent;
import com.example.socialnetwork.utils.events.Event;
import com.example.socialnetwork.utils.events.UpdateEvent;
import com.example.socialnetwork.utils.observer.Observable;
import com.example.socialnetwork.utils.observer.Observer;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Service extends Observable<Event> {
    private DatabaseUserRepository userRepository;
    //    private Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private DatabaseFriendshipRepository friendshipRepository;
    private Validator<User> validator;

    public Service(DatabaseUserRepository userRepository, DatabaseFriendshipRepository friendshipRepository, Validator<User> validator){
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.validator = validator;
    }

    public List<User> getFriends(Long id){
        List<Friendship> friendships = StreamSupport.stream(friendshipRepository.getAllByUserId(id).spliterator(), false).collect(Collectors.toList());
        List<User> friends = new ArrayList<>();
        for(Friendship friendship : friendships){
            if(friendship.getUser1().equals(id))
               friends.add(userRepository.findOne(friendship.getUser2()).get());
            else
                friends.add((userRepository.findOne(friendship.getUser1()).get()));
        }
        return friends;
    }
    public User search(Long id){
        Optional<User> user = userRepository.findOne(id);
        if(user.isPresent())
            return user.get();
        else{
            throw new RuntimeException("User doesn't exist");
        }
    }
    public Long login(String username, String password){
        List<User> list = StreamSupport.stream(userRepository.getAll().spliterator(), false)
                .filter(x->x.getUsername().equals(username))
                .collect(Collectors.toList());
        System.out.println(Encryption.toHexString(password.getBytes()));
        System.out.println(list.get(0).getPassword());
        try {
            System.out.println(list.get(0).getPassword().equals(Encryption.toHexString(Encryption.getSHA(password))));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        try {
            if(!list.isEmpty() && list.get(0).getPassword().equals(Encryption.toHexString(Encryption.getSHA(password)))){
                return list.get(0).getId();
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void add(String firstName, String lastName, String username, String email, String password){
        User user = null;
        try {
            user = new User(firstName, lastName, username, email, Encryption.toHexString(Encryption.getSHA(password)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        validator.validate(user);
        if(userRepository.add(user).isEmpty()){
            throw new RuntimeException("User already exists!\n");
        }
        notifyObservers(new AddEvent());
    }

    public void delete(Long id){
        if(userRepository.delete(id).isEmpty()){
            throw new RuntimeException("User doesn't exist!\n");
        }
        notifyObservers(new DeleteEvent());
    }

    public void update(Long id, String firstName, String lastName, String username, String email, String password){
        User u = new User(firstName, lastName, username, email, password); u.setId(id);
        if(userRepository.update(u).isEmpty()){
            throw new RuntimeException("User doesn't exist");
        }
        notifyObservers(new UpdateEvent());
    }

    public void addFriend(Long id1, Long id2){
        if(userRepository.findOne(id1).isPresent() && userRepository.findOne(id2).isPresent()) {
            if (userRepository.findOne(id1).get().getFriends().contains(userRepository.findOne(id2).get()))
                throw new RuntimeException("Friendship already exists!\n");
            userRepository.findOne(id1).get().addFriend(userRepository.findOne(id2).get());
            userRepository.findOne(id2).get().addFriend(userRepository.findOne(id1).get());
            friendshipRepository.add(new Friendship(id1, id2));
        }
        else{
            throw new RuntimeException("User doesn't exist!\n");
        }
    }

    public void removeFriend(Long id1, Long id2){
        if(userRepository.findOne(id1).isPresent() && userRepository.findOne(id2).isPresent()) {
            if(!userRepository.findOne(id1).get().getFriends().contains(userRepository.findOne(id2).get())){
                throw new RuntimeException("Friendship doesn't exists!\n");
            }
            userRepository.findOne(id1).get().removeFriend(userRepository.findOne(id2).get());
            userRepository.findOne(id2).get().removeFriend(userRepository.findOne(id1).get());
        }
        else{
            throw new RuntimeException("User doesn't exists!\n");
        }
    }

    public Map<Long, List<User>> getGraph(){
        Map<Long, List<User>> map = new HashMap<>();
        for(User u : userRepository.getAll()){
            map.put(u.getId(), u.getFriends());
        }
        return map;
    }

    public int nrOfCommunities(){
        return Graph.getInstance().NumberOfConnectedComponents(getGraph());
    }

    public List<Long> mostSociableCommunity(){
        return Graph.getInstance().mostSociableCommunity(getGraph());
    }

    public Iterable<User> getAll(){
        Iterable<User> users = userRepository.getAll();
        List<User> friends;

        for(User u : users){
            friends = new ArrayList<>();
            Iterable<Friendship> friendships = friendshipRepository.getAllByUserId(u.getId());
            for(Friendship f : friendships){
                if(f.getUser1() == u.getId())
                    friends.add(userRepository.findOne(f.getUser2()).get());
                if(f.getUser2() == u.getId())
                    friends.add(userRepository.findOne(f.getUser1()).get());
            }
            u.setFriends(friends);
        }
        return users;
    }

    public List<String> friendshipsMonth(Long id, String month){
        List<Friendship> friendships = StreamSupport.stream(friendshipRepository.getAllByUserId(id).spliterator(), false)
                .filter(x->x.getDate().getMonth().toString().equals(month))
                .collect(Collectors.toList());

        User currentUser = userRepository.findOne(id).get();

        List<String> toReturn = new ArrayList<>();
        for(Friendship f : friendships){
            User friend;
            if(f.getUser1() == id){
                friend = userRepository.findOne(f.getUser2()).get();
            }
            else{
                friend = userRepository.findOne(f.getUser1()).get();
            }

            toReturn.add(new String(currentUser.getFirstName() + ' ' + currentUser.getLastName() + '|' +
                    friend.getFirstName() + ' ' + friend.getLastName() + '|' + f.getDate()));
        }
        return toReturn;
    }

//    public List<User> useri_cu_cel_putin_n_prieteni(Integer n){
//        return StreamSupport.stream(repository.getAll().spliterator(), false)
//                .filter(x->x.getFriends().size() >= n)
//                .sorted((x, y)->{return x.getFriends().size() - y.getFriends().size();})
//                .collect(Collectors.toList());
//
//    }


}
