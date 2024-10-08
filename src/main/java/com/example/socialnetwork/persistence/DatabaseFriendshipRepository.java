package com.example.socialnetwork.persistence;



import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.Tuple;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseFriendshipRepository implements Repository<Tuple<Long, Long>, Friendship> {
    private final String url;
    private final String username;
    private final String password;

    public DatabaseFriendshipRepository(String url, String username, String password){
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Friendship> add(Friendship friendship) {
        String sql = "INSERT INTO Friendships(user1, user2, friendsFrom) VALUES(?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setLong(1, friendship.getUser1());
            statement.setLong(2, friendship.getUser2());
            statement.setTimestamp(3, Timestamp.valueOf(friendship.getFriendsFrom()));
            int affectedRows = statement.executeUpdate();
            return affectedRows == 0 ? Optional.empty() : Optional.of(friendship);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> t) {
        String sql = "SELECT * FROM Friendships WHERE user1 = ? and user2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            //return this.getByColumn(id, sql, connection);
            statement.setObject(1, t.getLeft());
            statement.setObject(2, t.getRight());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Friendship friendship = this.mapToEntity(resultSet);
                return Optional.of(friendship);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Optional<Friendship> update(Friendship friendship) { return Optional.empty();}
//        String sql = """
//                UPDATE Friendship
//                SET user1 = ?, user2 = ?, friendsFrom = ?
//                WHERE user1 = ? and user2 = ?
//                """;
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement(sql)
//        ) {
//            statement.setLong(1, friendship.getUser1());
//            statement.setLong(2, friendship.getUser2());
//            statement.setTimestamp(3, Timestamp.valueOf(friendship.getFriendsFrom()));
//            statement.setLong(4, friendship.getId());
//            int affectedRows = statement.executeUpdate();
//            if (affectedRows > 0) {
//                return Optional.of(friendship);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return Optional.empty();
//    }

    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> t) {return Optional.empty();}
//        String sql = "DELETE FROM Friendship WHERE id = ?";
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement(sql)
//        ) {
//            Optional<Friendship> friendshipToDelete = this.getByColumn(id, "SELECT * FROM friendship WHERE id = ?", connection);
//            statement.setLong(1, id);
//            int affectedRows = statement.executeUpdate();
//            if (affectedRows > 0) {
//                return friendshipToDelete;
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return Optional.empty();
//    }

    @Override
    public Iterable<Friendship> getAll() {
        String sql = "SELECT * FROM Friendships";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            ResultSet resultSet = statement.executeQuery();
            return this.mapToEntities(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Optional<Friendship> getByUserIds(Long idFirstUser, Long idSecondUser) {
        String sql = "SELECT * FROM Friendships WHERE user1 = ? AND user2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, idFirstUser);
            statement.setLong(2, idSecondUser);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Friendship friendship = this.mapToEntity(resultSet);
                return Optional.of(friendship);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }


    public Iterable<Friendship> getAllByUserId(Long userId) {
        String sql = "SELECT * FROM Friendships WHERE user1 = ? OR user2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, userId);
            statement.setLong(2, userId);
            ResultSet resultSet = statement.executeQuery();
            return this.mapToEntities(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Friendship mapToEntity(ResultSet resultSet) {
        try {
            Friendship friendship = new Friendship(
                    resultSet.getLong("user1"),
                    resultSet.getLong("user2")
            );
            friendship.setFriendsFrom(resultSet.getTimestamp("friendsFrom").toLocalDateTime());
            friendship.setId(resultSet.getLong("user1"), resultSet.getLong("user2"));
            return friendship;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Iterable<Friendship> mapToEntities(ResultSet resultSet) throws SQLException {
        List<Friendship> friendships = new ArrayList<>();
        while(resultSet.next()) {
            friendships.add(this.mapToEntity(resultSet));
        }
        return friendships;
    }



    private <T> Optional<Friendship> getByColumn(T column, String sql, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, column);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Friendship friendship = this.mapToEntity(resultSet);
                return Optional.of(friendship);
            }
        }
        catch(SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}