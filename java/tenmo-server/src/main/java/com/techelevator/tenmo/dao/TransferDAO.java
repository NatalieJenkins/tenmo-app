package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDAO {

    Transfer transferBalance(Transfer transfer) throws Exception;

    int decreaseBalance(long senderId, BigDecimal sendAmt) throws Exception;

    int addBalance(int receiverId, BigDecimal sendAmt) throws Exception;

    List<Transfer> userTransfers (int accountId);

    Transfer transferDetails(int id);

}
