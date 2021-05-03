package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("tenmo")
public class UserController {

    @Autowired
    private UserDAO userDAO;


    public UserController (UserDAO userDAO) {

        this.userDAO = userDAO;

    }

    @RequestMapping (path = "/users", method = RequestMethod.GET)
    public List<User> listOfUsers(Principal principal) {
        int userId = userDAO.findIdByUsername(principal.getName());
        return userDAO.findAllExceptCurrentUser(userId);
    }

    @RequestMapping (path = "/users/{id}/{accountType}", method = RequestMethod.GET)
    public String userAccountToName(@PathVariable int id, @PathVariable String accountType, Principal principal){
        return userDAO.findUserNameByAccount(id);
    }

}
