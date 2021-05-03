package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;

import java.util.List;

public interface UserDAO {

    List<User> findAll();

    List<User> findAllExceptCurrentUser(int id);

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);

    String findUserNameByAccount(int userId);

    //User findUserNameByAccountFrom(int userId);
}
