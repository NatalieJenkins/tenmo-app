package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping ("tenmo")
public class AccountController {

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private UserDAO userDAO;


    public AccountController (AccountDAO accountDAO, UserDAO userDAO) {

        this.accountDAO = accountDAO;
        this.userDAO = userDAO;
    }

    @RequestMapping (path = "/accounts", method = RequestMethod.GET)
    public Account getBalance(Principal principal) throws Exception {
        long userId = userDAO.findIdByUsername(principal.getName());
        return accountDAO.showBalance(userId);
    }


}
