package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JDBCTransferDAO implements TransferDAO {

    private JdbcTemplate jdbcTemplate;

    private AccountDAO accountDAO;

    public JDBCTransferDAO(JdbcTemplate jdbcTemplate) {
        accountDAO = new JdbcAccountDAO(jdbcTemplate);
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Transfer transferBalance(Transfer transfer) throws Exception {
        long senderId = transfer.getTransferFromAccountId();
        int receiverId = transfer.getTransferToAccountId();
        BigDecimal sendAmt = transfer.getTransferAmount();

            int senderAccountId = decreaseBalance(senderId, sendAmt);
            int receiverAccountId = addBalance(receiverId, sendAmt);
            String sqlInsert = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, " +
                    "account_to, amount) VALUES(?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlInsert, 2, 2, senderAccountId, receiverAccountId, sendAmt);

            return transfer;

    }

        @Override
        public int decreaseBalance ( long senderId, BigDecimal sendAmt) throws Exception {
            Account account;
            account = accountDAO.showBalance(senderId);

            BigDecimal updatedAmount = account.getBalance().subtract(sendAmt);
            String sqlSubtractBalance = "UPDATE accounts SET balance = ? WHERE user_id = ? ";
            jdbcTemplate.update(sqlSubtractBalance, updatedAmount, senderId);

            return account.getAccountId();

        }

        @Override
        public int addBalance ( int receiverId, BigDecimal sendAmt) throws Exception {
            Account account;
            long longReceiverId = (long) receiverId;
            account = accountDAO.showBalance(longReceiverId);
            BigDecimal receivedAmount = account.getBalance().add(sendAmt);
            String sqlAddBalance = "UPDATE accounts SET balance = ? WHERE user_id = ?";
            jdbcTemplate.update(sqlAddBalance, receivedAmount, receiverId);
            return account.getAccountId();
        }


        @Override
        public List<Transfer> userTransfers (int accountId) {
            List<Transfer> transfersList = new ArrayList<>();
            String sqlGetTransfers = "SELECT transfer_id, transfer_type_id, transfer_status_id, " +
                    "account_from, account_to, amount FROM transfers t WHERE account_from = ? OR account_to = ?";
            SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetTransfers, accountId, accountId);
            while (results.next()) {
                Transfer transfer = mapRowToTransfer(results);
                transfersList.add(transfer);
            }
            return transfersList;
        }

        @Override
        public Transfer transferDetails(int id){
        String sqlDetails = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to," +
                " amount FROM transfers WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlDetails, id);
        results.next();
        Transfer transferDetails = mapRowToTransfer(results);
            return transferDetails;
        }

        private Transfer mapRowToTransfer (SqlRowSet results) {
            Transfer transfer = new Transfer();
            transfer.setTransferId(results.getInt("transfer_id"));
            transfer.setTransferTypeId(results.getInt("transfer_type_id"));
            transfer.setTransferStatusId(results.getInt("transfer_status_id"));
            transfer.setTransferFromAccountId(results.getInt("account_from"));
            transfer.setTransferToAccountId(results.getInt("account_to"));
            transfer.setTransferAmount(results.getBigDecimal("amount"));

            return transfer;
        }

}
