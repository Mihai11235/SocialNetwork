package com.example.socialnetwork.persistence;


import com.example.socialnetwork.domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseUserRepository implements Repository<Long, User> {

    String url;
    String user;
    String DBpassword;

    public DatabaseUserRepository(String url, String user, String password){
        this.url = url;
        this.user = user;
        this.DBpassword = password;
    }
    @Override
    public Optional<User> findOne(Long id) {
        try(Connection connection = DriverManager.getConnection(url, user, DBpassword);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Users WHERE id = ?");
        ){
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User user = new User(firstName, lastName, username, email, password);
                user.setId(id);
                return Optional.ofNullable(user);
            }
            return Optional.empty();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<User> getAll() {
        List<User> users = new ArrayList<>();
        try(Connection connection= DriverManager.getConnection(this.url,this.user,this.DBpassword);
            PreparedStatement preparedStatement=connection.prepareStatement("SELECT * FROM Users");

        ){
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
            {
                Long id=resultSet.getLong("id");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User user = new User(firstName, lastName, username, email, password);
                user.setId(id);

                users.add(user);
            }
            return users;

        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> add(User entity) {
        try(Connection connection = DriverManager.getConnection(url, user, DBpassword);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Users (firstName, lastName, username, email, password) VALUES (?,?,?,?,?)");
        ){
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setString(3, entity.getUsername());
            preparedStatement.setString(4, entity.getEmail());
            preparedStatement.setString(5, entity.getPassword());
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows == 0 ? Optional.empty() : Optional.ofNullable(entity);
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> delete(Long id) {
        try(Connection connection= DriverManager.getConnection(this.url,this.user,this.DBpassword);
            PreparedStatement preparedStatement=connection.prepareStatement("DELETE FROM Users WHERE id = ?");

        ){
            if(findOne(id).isPresent()) {
                User u = findOne(id).get();
                preparedStatement.setLong(1, id);
                preparedStatement.executeUpdate();
                return Optional.of(u);
            }
            return Optional.empty();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> update(User entity) {
        try(Connection connection= DriverManager.getConnection(this.url,this.user,this.DBpassword);
            PreparedStatement preparedStatement=connection.prepareStatement("UPDATE Users SET firstName = ?, lastName = ?, username = ?, email = ?, password = ? WHERE id = ?");

        ){
            preparedStatement.setString(1,entity.getFirstName());
            preparedStatement.setString(2,entity.getLastName());
            preparedStatement.setString(3,entity.getUsername());
            preparedStatement.setString(4,entity.getEmail());
            preparedStatement.setString(5,entity.getPassword());
            preparedStatement.setLong(6,entity.getId());
            int affectedRows=preparedStatement.executeUpdate();
            return affectedRows != 0 ? Optional.ofNullable(entity) : Optional.empty();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
