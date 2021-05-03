package com.techelevator.tenmo;

import com.techelevator.tenmo.models.*;
import com.techelevator.tenmo.services.*;
import com.techelevator.view.ConsoleService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
    private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
    private static final String[] LOGIN_MENU_OPTIONS = {LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};
    private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
    private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
    private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
    private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
    private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};

    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private AccountService accountService;
    private UserService userService;
    private TransferService transferService;

    public static void main(String[] args) {
        App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL),
                new AccountService(API_BASE_URL), new UserService(API_BASE_URL), new TransferService(API_BASE_URL));
        app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService, AccountService accountService,
               UserService userService, TransferService transferService) {
        this.console = console;
        this.authenticationService = authenticationService;
        this.accountService = accountService;
        this.userService = userService;
        this.transferService = transferService;
    }

    public void run() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");

        registerAndLogin();
        mainMenu();
    }

    private void mainMenu() {
        while (true) {
            String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
            if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
                viewCurrentBalance();
            } else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
                viewTransferHistory();
            } else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
                viewPendingRequests();
            } else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
                sendBucks();
                //sub menu
            } else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
                requestBucks();
            } else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else {
                // the only other option on the main menu is to exit
                exitProgram();
            }
        }
    }


    private void viewCurrentBalance() {

        Account account = accountService.getAccount(currentUser.getToken());
        BigDecimal balance = account.getBalance();

        System.out.println("Your current balance is: $" + balance);

    }


    private void sendBucks() {
        User[] userArray = userService.userList(currentUser.getToken());
        for (User user : userArray) {
            System.out.println(user.getId() + "   " + user.getUsername());
        }
        System.out.println("");
        String toUser = console.getUserInput("Enter user Id to transfer to (0 to cancel)");

        boolean userFound = false;
        for (User user : userArray) {
            try {
                int userId = Integer.parseInt(toUser);
                if (user.getId() == Integer.parseInt(toUser)) {
                    userFound = true;
                    String amount = console.getUserInput("Enter amount to transfer");
                    amountToSend(amount, userId);
                }
            } catch (NumberFormatException exception) {
                userFound = true;
                System.out.println("Please enter a valid user Id");
                break;
            }
        }
        if (!userFound && !(toUser.equals("0"))) {
            System.out.println("Invalid user account number!");
        }
    }


    public void amountToSend(String transferAmt, int toUserId) {

        try {
            BigDecimal bigDecimalAmt = new BigDecimal(transferAmt);
            Account account = accountService.getAccount(currentUser.getToken());
            BigDecimal balance = account.getBalance();

            if (balance.compareTo(bigDecimalAmt) < 0 ) {
                System.out.println("You don't have enough money in your account!");
            } else if (bigDecimalAmt.compareTo(new BigDecimal("0.00")) < 0) {
                System.out.println("You cannot enter negative numbers!!");
            }
            else{
                transferService.transfer(currentUser.getToken(), toUserId, bigDecimalAmt);
                viewCurrentBalance();
            }

        } catch (NumberFormatException ex) {
            System.out.println("You must enter a number!");
        }
    }


    private void viewTransferHistory() {
        System.out.println("Transfer Id   From/To              Amount");
        System.out.println("___________________________________________");
        Transfer[] transfersArray = transferService.transfersList(currentUser.getToken());
        int accountId = accountService.getAccount(currentUser.getToken()).getAccountId();
        List<Transfer> userTransfers = new ArrayList<>();

        for (Transfer transfer : transfersArray) {

            if (accountId == transfer.getTransferFromAccountId()) {
                String accountUserFrom = userService.getUserName(currentUser.getToken(), transfer.getTransferToAccountId(), "from");
                System.out.printf("%8d \t To:\t%-12s $ %-60s\n", transfer.getTransferId(), accountUserFrom, transfer.getTransferAmount());
                userTransfers.add(transfer);

            } else if (accountId == transfer.getTransferToAccountId()) {
                String accountUserTo = userService.getUserName(currentUser.getToken(), transfer.getTransferFromAccountId(), "to");
                System.out.printf("%8d \t From:\t%-12s $ %-60s\n", transfer.getTransferId(), accountUserTo, transfer.getTransferAmount());
                userTransfers.add(transfer);
            }
        }
        transferDetailsPrintOut(userTransfers);
    }


    public void transferDetailsPrintOut(List<Transfer> currentUserTransfers) {

        String transferId = console.getUserInput("Please enter transfer ID to view details (0 to cancel)");
        boolean transferFound = false;
        Transfer detailsReturned = new Transfer();
        for (Transfer transfer : currentUserTransfers) {
            try {
                int transferDetailId = Integer.parseInt(transferId);
                if (transfer.getTransferId() == Integer.parseInt(transferId)) {
                    transferFound = true;
                    detailsReturned = transferService.getTransferDetails(currentUser.getToken(), transferDetailId);
                    System.out.println("");
                    System.out.println("----------Transfer Details-----------");
                    System.out.println("Id: " + detailsReturned.getTransferId());
                    System.out.println("From: " + userService.getUserName(currentUser.getToken(),
                            detailsReturned.getTransferFromAccountId(), "from"));
                    System.out.println("To: " + userService.getUserName(currentUser.getToken(),
                            detailsReturned.getTransferToAccountId(), "to"));
                    System.out.println("Type: Send");
                    System.out.println("Status: Approved");
                    System.out.println("Amount: $" + detailsReturned.getTransferAmount());
                }
            } catch (NumberFormatException exception) {
                transferFound = true;
                System.out.println("Must be a valid transfer Id number!");
                break;
            }
        }
        if (!transferFound && !(transferId.equals("0"))) {
            System.out.println("Invalid transfer Id number!");
        }
    }


    private void requestBucks() {
        System.out.println("Not implemented");

    }

    private void viewPendingRequests() {
        System.out.println("Not Implemented");

    }

    private void exitProgram() {
        System.exit(0);
    }

    private void registerAndLogin() {
        while (!isAuthenticated()) {
            String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
            if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
                register();
            } else {
                // the only other option on the login menu is to exit
                exitProgram();
            }
        }
    }

    private boolean isAuthenticated() {
        return currentUser != null;
    }

    private void register() {
        System.out.println("Please register a new user account");
        boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                authenticationService.register(credentials);
                isRegistered = true;
                System.out.println("Registration successful. You can now login.");
            } catch (AuthenticationServiceException e) {
                System.out.println("REGISTRATION ERROR: " + e.getMessage());
                System.out.println("Please attempt to register again.");
            }
        }
    }

    private void login() {
        System.out.println("Please log in");
        currentUser = null;
        while (currentUser == null) //will keep looping until user is logged in
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                currentUser = authenticationService.login(credentials);
            } catch (AuthenticationServiceException e) {
                System.out.println("LOGIN ERROR: " + e.getMessage());
                System.out.println("Please attempt to login again.");
            }
        }
    }

    private UserCredentials collectUserCredentials() {
        String username = console.getUserInput("Username");
        String password = console.getUserInput("Password");
        return new UserCredentials(username, password);
    }
}
