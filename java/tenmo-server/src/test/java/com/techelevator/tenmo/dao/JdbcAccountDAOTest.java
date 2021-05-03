package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.*;
import org.junit.runners.MethodSorters;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

class JdbcAccountDAOTest {

    private static final int TEST_ACCOUNT_ID = 5000;
    private static final int TEST_USER_ID = 6000;
    private static final BigDecimal TEST_USER_BALANCE = new BigDecimal("1500.00");
    private static final String TEST_USERNAME = "Donald";
    private static final String TEST_PASSWORD = "Duck";
    private static SingleConnectionDataSource dataSource;
    private JdbcAccountDAO dao;
    private JdbcUserDAO userDAO;
    private JdbcTemplate jdbcTemplate;

    @BeforeAll
    public static void setupDataSource(){
        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/tenmo");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        dataSource.setAutoCommit(false);
    }

    @AfterAll
    public static void closeDataSource() throws SQLException {
        dataSource.destroy();
    }

    @BeforeEach
    public void setup(){

        String sqlInsertUser = "INSERT INTO users (user_id, username, password_hash) VALUES (?, ?, ?)";
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(sqlInsertUser, TEST_USER_ID, TEST_USERNAME, TEST_PASSWORD);
        String sqlInsertAccount = "INSERT INTO accounts (account_id, user_id, balance) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlInsertAccount, TEST_ACCOUNT_ID, TEST_USER_ID, TEST_USER_BALANCE);
        dao = new JdbcAccountDAO(jdbcTemplate);
        userDAO = new JdbcUserDAO(jdbcTemplate);

    }

    @AfterEach
    public void rollBack() throws SQLException {
        dataSource.getConnection().rollback();
    }


    @Test
    void sends_in_test_user_id_returns_account_object() {
        //ARRANGE
        Account theAccount = new Account(TEST_ACCOUNT_ID, TEST_USER_ID, TEST_USER_BALANCE);

        //ACT

        Account actualAccount = dao.showBalance(theAccount.getUserId());
        Account expectedAccount = theAccount;

        //ASSERT
        assertAccountsAreEqual(expectedAccount, actualAccount);
    }

    @Test
    void sends_in_sql_row_set_returns_test_account() {
        //ARRANGE
        Account theAccount = new Account(TEST_ACCOUNT_ID, TEST_USER_ID, TEST_USER_BALANCE);
        String sqlGetAccount = "SELECT account_id, user_id, balance FROM accounts WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetAccount, TEST_USER_ID);

        Account actualAccount = null;
        if (results.next()) {
            actualAccount = dao.mapRowToAccount(results);
        }
        //ACT

        Account expectedAccount = theAccount;

        //ASSERT
        assertAccountsAreEqual(expectedAccount, actualAccount);

    }

    private void assertAccountsAreEqual(Account expected, Account actual) {
        assertEquals(expected.getAccountId(), actual.getAccountId());
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getBalance(), actual.getBalance());

    }
}