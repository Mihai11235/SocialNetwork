package com.example.socialnetwork.persistence;

import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DatabaseMessageRepository implements Repository<Long, Message> {

    String url;
    String username;
    String password;

    public DatabaseMessageRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Message> findOne(Long id) {
        String sql = "SELECT MF.id, MF.fromuser, MT.touser, MF.message, MF.date, MF.reply " +
                     "FROM messages_from MF INNER JOIN messages_to MT ON MF.id = MT.message_from_id" +
                     "WHERE MF.id = ?";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                Long from = resultSet.getLong("fromuser");
                Long to = resultSet.getLong("touser");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                Message reply = null;
                Optional<Message> optionalMessage = findOne(resultSet.getLong("reply"));
                if (optionalMessage.isPresent())
                    reply = optionalMessage.get();

                Message m = new Message(from, to, message, reply); m.setData(date);
                return Optional.of(m);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Message> getAll() {
        return null;
    }

    @Override
    public Optional<Message> add(Message entity) {
        Long messageId = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO messages_from(fromuser, message, date, reply) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setLong(1, entity.getFrom());
            preparedStatement.setString(2, entity.getMessage());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(entity.getData()));
            if(entity.getReply() != null)
                preparedStatement.setLong(4, entity.getReply().getId());
            else
                preparedStatement.setNull(4, java.sql.Types.INTEGER);

            int affectedRows = preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            while(generatedKeys.next())
                messageId = generatedKeys.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO messages_to(touser, message_from_id) VALUES(?,?)");
        ) {
            preparedStatement.setLong(1, entity.getTo());
            preparedStatement.setLong(2, messageId);

            int affectedRows = preparedStatement.executeUpdate();

            return affectedRows == 0 ? Optional.empty() : Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        @Override
    public Optional<Message> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }

    public Iterable<Message> getMessagesBetween2Users(Long userId1, Long userId2) {
        String sql = "SELECT MF.id, MF.fromuser, MT.touser, MF.message, MF.date, MF.reply " +
                     "FROM messages_from MF INNER JOIN messages_to MT ON MF.id = MT.message_from_id " +
                     "WHERE MF.fromuser = ? and MT.touser = ? or MF.fromuser = ? and MT.touser = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setLong(1, userId1);
            preparedStatement.setLong(2, userId2);
            preparedStatement.setLong(3, userId2);
            preparedStatement.setLong(4, userId1);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Message> messages = new ArrayList<>();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long from = resultSet.getLong("fromuser");
                Long to = resultSet.getLong("touser");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();


                //cauta mesajul pentru care mesajul curent este reply
                Message reply = null;
                List<Message> replyList = messages.stream().filter(x-> {
                    try {
                        return x.getId() == resultSet.getLong("reply");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());

                if(!replyList.isEmpty())
                    reply = replyList.get(0);

                Message m = new Message(from, to, message, reply); m.setId(id); m.setData(date);
                messages.add(m);
            }
            return messages;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Iterable<Message> getAllFromUser(Long userId){
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM messages WHERE fromuser = ? or touser = ?");
        ) {
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("dupa execute query");

            List<Message> messages = new ArrayList<>();
            while (resultSet.next()){
                Long id = resultSet.getLong("id");
                Long from = resultSet.getLong("fromuser");
                Long to = resultSet.getLong("touser");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                //cauta mesajul pentru care mesajul curent este reply
                Message reply = null;
                List<Message> replyList = messages.stream().filter(x-> {
                        try {
                            return x.getId() == resultSet.getLong("reply");
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                }).collect(Collectors.toList());

                if(!replyList.isEmpty())
                    reply = replyList.get(0);

//                Optional<Message> optionalMessage = findOne(resultSet.getLong("reply"));
//                if(optionalMessage.isPresent())
//                    reply = optionalMessage.get();

                Message m = new Message(from, to, message, reply); m.setId(id); m.setData(date);
                messages.add(m);
            }
            System.out.println("ok");
            return messages;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
