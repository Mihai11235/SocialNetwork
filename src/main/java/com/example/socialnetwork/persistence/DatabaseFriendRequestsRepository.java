package com.example.socialnetwork.persistence;

import com.example.socialnetwork.domain.FriendRequest;
import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.Tuple;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.persistence.paging.Page;
import com.example.socialnetwork.persistence.paging.Pageable;
import com.example.socialnetwork.persistence.paging.PagingRepository;
import javafx.util.Pair;

import java.io.PipedReader;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseFriendRequestsRepository implements PagingRepository<Tuple<Long, Long>, FriendRequest> {
    private final String url;
    private final String username;
    private final String password;

    public DatabaseFriendRequestsRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<FriendRequest> findOne(Tuple<Long, Long> longLongTuple) {
        return Optional.empty();
    }

    @Override
    public Iterable<FriendRequest> getAll() {
        return null;
    }

    public Iterable<FriendRequest> getAllRequestsOfUser(Long user) {
        List<FriendRequest> friendRequests = new ArrayList<>();
        try(Connection connection= DriverManager.getConnection(this.url,this.username,this.password);
            PreparedStatement preparedStatement=connection.prepareStatement("SELECT * FROM friend_requests WHERE touser = ?");

        ){
            preparedStatement.setLong(1, user);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
            {
                Long fromuser = resultSet.getLong("fromuser");
                Long touser = resultSet.getLong("touser");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                FriendRequest friendRequest = new FriendRequest(fromuser, touser); friendRequest.setDate(date);

                friendRequests.add(friendRequest);
            }
            return friendRequests;

        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Iterable<FriendRequest> getAllSendRequestsOfUser(Long user){
        List<FriendRequest> friendRequests = new ArrayList<>();
        try(Connection connection= DriverManager.getConnection(this.url,this.username,this.password);
            PreparedStatement preparedStatement=connection.prepareStatement("SELECT * FROM friend_requests WHERE fromuser = ?");

        ){
            preparedStatement.setLong(1, user);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
            {
                Long fromuser = resultSet.getLong("fromuser");
                Long touser = resultSet.getLong("touser");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                FriendRequest friendRequest = new FriendRequest(fromuser, touser); friendRequest.setDate(date);

                friendRequests.add(friendRequest);
            }
            return friendRequests;

        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> add(FriendRequest entity) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO friend_requests(fromuser, touser, date) VALUES (?,?,?)");
            ){
            preparedStatement.setLong(1, entity.getUser1());
            preparedStatement.setLong(2, entity.getUser2());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows == 0 ? Optional.empty() : Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> delete(Tuple<Long, Long> tuple) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM friend_requests WHERE fromuser = ? and touser = ?");
        ){
            preparedStatement.setLong(1, tuple.getRight());
            preparedStatement.setLong(2, tuple.getLeft());
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows == 0 ? Optional.empty() : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
        return Optional.empty();
    }


    @Override
    public Page<FriendRequest> findAllOnPage(Pageable pageable, Long user) {
        List<FriendRequest> friendRequests = new ArrayList<>();
        String pageSQL = "SELECT * FROM friend_requests";
        String countSQL = "SELECT COUNT(*) AS count FROM friend_requests";

        if(user != null){
            pageSQL += " WHERE touser = ?";
            countSQL += " WHERE touser = ?";
        }

        pageSQL += " LIMIT ? OFFSET ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement pageStatement = connection.prepareStatement(pageSQL);
             PreparedStatement countStatement = connection.prepareStatement(countSQL)
        ){

            if(user != null){
                pageStatement.setInt(1, user.intValue());
                pageStatement.setInt(2, pageable.getPageSize());
                pageStatement.setInt(3, pageable.getPageSize() * pageable.getPageNr());

                countStatement.setInt(1, user.intValue());
            }
            else{
                pageStatement.setInt(1, pageable.getPageSize());
                pageStatement.setInt(2, pageable.getPageSize() * pageable.getPageNr());
            }


            try (
                    ResultSet pageResultSet = pageStatement.executeQuery();
                    ResultSet countResultSet = countStatement.executeQuery();
            ) {
                int count = 0;
                if (countResultSet.next()) {
                    count = countResultSet.getInt("count");
                }

                while(pageResultSet.next()){
                    Long fromuser = pageResultSet.getLong("fromuser");
                    Long touser = pageResultSet.getLong("touser");
                    LocalDateTime date = pageResultSet.getTimestamp("date").toLocalDateTime();
                    FriendRequest friendRequest = new FriendRequest(fromuser, touser); friendRequest.setDate(date);

                    friendRequests.add(friendRequest);
                }
                return new Page<>(friendRequests, count);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<FriendRequest> findAllOnPage(Pageable pageable){
        return findAllOnPage(pageable, null);
    }
}
