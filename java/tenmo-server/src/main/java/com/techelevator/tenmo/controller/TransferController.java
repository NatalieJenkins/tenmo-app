package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping ("tenmo")
public class TransferController {
    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private TransferDAO transferDAO;


    public TransferController (AccountDAO accountDAO, UserDAO userDAO, TransferDAO transferDAO) {

        this.accountDAO = accountDAO;
        this.userDAO = userDAO;
        this.transferDAO = transferDAO;
    }

    @RequestMapping( path = "/transfers", method = RequestMethod.PUT)
    public Transfer transferBalance(@Valid @RequestBody Transfer transfer, Principal principal) throws Exception {
        int accountId = userDAO.findIdByUsername(principal.getName());
        transfer.setTransferFromAccountId(accountId);
        return transferDAO.transferBalance(transfer);
    }

    @RequestMapping (path = "/transfers", method = RequestMethod.GET)
    public List<Transfer> transferList (Principal principal) throws Exception {
        long userId = userDAO.findIdByUsername(principal.getName());
        Account account = accountDAO.showBalance(userId);
        List<Transfer> transfersList= transferDAO.userTransfers(account.getAccountId());
        return transfersList;
    }

    @RequestMapping ( path = "/transfers/{id}", method = RequestMethod.GET)
    public Transfer transferDetails(@PathVariable int id, Principal principal){
        return transferDAO.transferDetails(id);
    }
}

