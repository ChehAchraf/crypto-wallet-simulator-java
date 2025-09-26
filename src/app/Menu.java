package app;

import db.DBConnection;
import enums.*;
import java.util.List;
import java.util.Scanner;
import model.*;
import repository.TransactionRepository;
import repository.WalletRepository;
import service.*;
import util.InputValidation;

public class Menu {

    private TransactionService transactionService;
    private WalletService walletService;
    private TransactionValidationService validationService;
    private BalanceService balanceService;
    private TransactionProcessingService transactionProcessingService;
    private Scanner scanner;
    private boolean isActive;
    private MempoolService mempoolService;
    public Menu(MempoolService mempoolService) {
        this.scanner = new Scanner(System.in);
        this.isActive = true;
        DBConnection dbConnection = DBConnection.getInstance();
        WalletRepository walletRepository = new WalletRepository(dbConnection);
        TransactionRepository transactionRepository = new TransactionRepository(dbConnection);
        this.transactionService = new TransactionService(transactionRepository);
        this.walletService = new WalletService(walletRepository);
        this.validationService = new TransactionValidationService(walletService);
        this.balanceService = new BalanceService(walletService);
        this.transactionProcessingService = new TransactionProcessingService(transactionRepository, validationService, balanceService);
        this.mempoolService = mempoolService;
    }

    public void startApplication() {
        while (isActive) {
            System.out.println("================================");
            System.out.println("     crypto wallet simulator    ");
            System.out.println("================================");
            showMenu();
            handleUserChoice();
        }
    }

    private void handleUserChoice() {
        System.out.print("Enter your choice : ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
            		createWallet();
                break;
            case "2":
            		DisplayWallets();
                break;
            case "3":
                makeTransaction();
                break;
            case "4":
                showAllTransactions();
                break;
            case "5":
                checkMempool();
                break;
            case "0":
                isActive = false;
                break;
        }
    }

	private void showMenu() {
        System.out.println("1. Create Wallet");
        System.out.println("2. Display all Wallets");
        System.out.println("3. Make a transaction");
        System.out.println("4. Show all transactions");
        System.out.println("5. Check the Mempool");
    }

    // create wallet
    private void createWallet() {
        System.out.println("1 - Bitcoin Wallet");
        System.out.println("2 - Ethereum Wallet");
        System.out.print("\n[+] - enter your choice : ");
        String choice = scanner.nextLine();
        switch (choice) {
            // case one => creating Bitcoin wallet 😁
            case "1":
            		try {
                    System.out.print("\nPlease enter your amount : ");
                    String amountAsString = scanner.nextLine();
                    while (!InputValidation.isDouble(amountAsString)) {
                        System.out.print("\nPlease enter a valid amount : ");
                        amountAsString = scanner.nextLine();
                    }

                    double amount = Double.parseDouble(amountAsString);

                		Wallet btcWallet = new BitcoinWallet(amount);
                        walletService.createWallet(btcWallet);
                        System.out.println("Bitcoin wallet created successfully");

                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format. Please enter a valid numeric value.");
                } catch (Exception e) {
                    System.out.println("Unexpected error while creating Bitcoin wallet: " + e.getMessage());
                }
                break;
            // case two => creating Etherium wallet 
            case "2":
                try {
                    System.out.print("\nPlease enter your amount : ");
                    String amountAsString = scanner.nextLine();
                    while (!InputValidation.isDouble(amountAsString)) {
                        System.out.print("\nPlease enter a valid amount : ");
                        amountAsString = scanner.nextLine();
                    }

                    double amount = Double.parseDouble(amountAsString);

                    Wallet ethWallet = new EthereumWallet(amount);
                    walletService.createWallet(ethWallet);
                    System.out.println("Ethereum wallet created successfully");

                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format. Please enter a valid numeric value.");
                } catch (Exception e) {
                    System.out.println("Unexpected error while creating Ethereum wallet: " + e.getMessage());
            		}
                    break;

        }
    }
    
    private void DisplayWallets() {
    		System.out.println("====== list of wallets =======");
        List<Wallet> listWallet = walletService.getAllWallets();
        listWallet.stream().forEach(p -> System.out.println("adresse : " + p.getAddress()));
        System.out.println("==============================");

    }

    private void makeTransaction() {
        String sourceAdresse;
        String toAddress;
        String amountAsString;
        double amountDouble = 0;
        int priorityChoice = 0;

        System.out.println("[+] Please chose what wallet you wanna make transaction: ");
        System.out.println("1 - Bitcoin Wallet");
        System.out.println("2 - Ethereum Wallet");
        System.out.print("\n[+] - enter your choice : ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                System.out.print("\nPlease enter youre wallet address : ");
                sourceAdresse = scanner.nextLine();

                System.out.print("\nNow please add the wallet address you wanna send BTC to : ");
                toAddress = scanner.nextLine();

                System.out.print("\nPlease enter your amount : ");
                amountAsString = scanner.nextLine();
                while (!InputValidation.isDouble(amountAsString)) {
                    System.out.println("Please enter a valid amount : ");
                    amountAsString = scanner.nextLine();
                }
                amountDouble = Double.parseDouble(amountAsString);
                
                System.out.println("[+] Now please may you chose the fee type : ");
                while (priorityChoice == 0) {
                    System.out.println("1 - Rapide");
                    System.out.println("2 - Standard");
                    System.out.println("3 - Economique");
                    priorityChoice = Integer.parseInt(scanner.nextLine());
                    switch (priorityChoice) {
                        case 1:
                            Transaction btcTransactionRap = new BitcoinTransaction(sourceAdresse, toAddress, amountDouble, FeePriority.RAPIDE);
                            TransactionProcessingService.TransactionResult result1 = transactionProcessingService.processTransaction(btcTransactionRap);
                            
                            if (result1.isSuccess()) {
                                System.out.println("Bitcoin transaction created successfully with ID: " + result1.getTransaction().getId());
                                System.out.println("Balance updated: " + sourceAdresse + " " + balanceService.getBalanceInfo(sourceAdresse));
                                System.out.println("Balance updated: " + toAddress + " " + balanceService.getBalanceInfo(toAddress));
                                mempoolService.addTransaction(result1.getTransaction());
                            } else {
                                System.out.println(result1.getErrorMessage());
                            }
                            break;
                        case 2:
                            Transaction btcTransactionSta = new BitcoinTransaction(sourceAdresse, toAddress, amountDouble, FeePriority.STANDARD);
                            TransactionProcessingService.TransactionResult result2 = transactionProcessingService.processTransaction(btcTransactionSta);
                            
                            if (result2.isSuccess()) {
                                System.out.println("Bitcoin transaction created successfully with ID: " + result2.getTransaction().getId());
                                System.out.println("Balance updated: " + sourceAdresse + " " + balanceService.getBalanceInfo(sourceAdresse));
                                System.out.println("Balance updated: " + toAddress + " " + balanceService.getBalanceInfo(toAddress));
                                mempoolService.addTransaction(result2.getTransaction());
                            } else {
                                System.out.println(result2.getErrorMessage());
                            }
                            break;
                        case 3:
                            Transaction btcTransactionEco = new BitcoinTransaction(sourceAdresse, toAddress, amountDouble, FeePriority.ECONOMIQUE);
                            TransactionProcessingService.TransactionResult result3 = transactionProcessingService.processTransaction(btcTransactionEco);
                            
                            if (result3.isSuccess()) {
                                System.out.println("Bitcoin transaction created successfully with ID: " + result3.getTransaction().getId());
                                System.out.println("Balance updated: " + sourceAdresse + " " + balanceService.getBalanceInfo(sourceAdresse));
                                System.out.println("Balance updated: " + toAddress + " " + balanceService.getBalanceInfo(toAddress));
                                mempoolService.addTransaction(result3.getTransaction());
                            } else {
                                System.out.println(result3.getErrorMessage());
                            }
                            break;
                        default:
                            System.out.println("Invalid choice. Please enter a valid choice.");
                            break;
                    }
                }
                break;
            case "2":
                System.out.print("\nPlease enter youre wallet address : ");
                sourceAdresse = scanner.nextLine();

                System.out.print("\nNow please add the wallet address you wanna send ETH to : ");
                toAddress = scanner.nextLine();

                System.out.print("\nPlease enter your amount : ");
                amountAsString = scanner.nextLine();
                while (!InputValidation.isDouble(amountAsString)) {
                    System.out.println("Please enter a valid amount : ");
                    amountAsString = scanner.nextLine();
                }
                amountDouble = Double.parseDouble(amountAsString);
                
                System.out.println("[+] Now please may you chose the fee type : ");
                while (priorityChoice == 0) {
                    System.out.println("1 - Rapide");
                    System.out.println("2 - Standard");
                    System.out.println("3 - Economique");
                    priorityChoice = Integer.parseInt(scanner.nextLine());
                    switch (priorityChoice) {
                        case 1:
                            Transaction ethTransactionRap = new EthereumTransaction(sourceAdresse, toAddress, amountDouble, FeePriority.RAPIDE);
                            TransactionProcessingService.TransactionResult result1 = transactionProcessingService.processTransaction(ethTransactionRap);
                            
                            if (result1.isSuccess()) {
                                System.out.println("Ethereum transaction created successfully with ID: " + result1.getTransaction().getId());
                                System.out.println("Balance updated: " + sourceAdresse + " " + balanceService.getBalanceInfo(sourceAdresse));
                                System.out.println("Balance updated: " + toAddress + " " + balanceService.getBalanceInfo(toAddress));
                                mempoolService.addTransaction(result1.getTransaction());
                            } else {
                                System.out.println(result1.getErrorMessage());
                            }
                            break;
                        case 2:
                            Transaction ethTransactionSta = new EthereumTransaction(sourceAdresse, toAddress, amountDouble, FeePriority.STANDARD);
                            TransactionProcessingService.TransactionResult result2 = transactionProcessingService.processTransaction(ethTransactionSta);
                            
                            if (result2.isSuccess()) {
                                System.out.println("Ethereum transaction created successfully with ID: " + result2.getTransaction().getId());
                                System.out.println("Balance updated: " + sourceAdresse + " " + balanceService.getBalanceInfo(sourceAdresse));
                                System.out.println("Balance updated: " + toAddress + " " + balanceService.getBalanceInfo(toAddress));
                                mempoolService.addTransaction(result2.getTransaction());
                            } else {
                                System.out.println(result2.getErrorMessage());
                            }
                            break;
                        case 3:
                            Transaction ethTransactionEco = new EthereumTransaction(sourceAdresse, toAddress, amountDouble, FeePriority.ECONOMIQUE);
                            TransactionProcessingService.TransactionResult result3 = transactionProcessingService.processTransaction(ethTransactionEco);
                            
                            if (result3.isSuccess()) {
                                System.out.println("Ethereum transaction created successfully with ID: " + result3.getTransaction().getId());
                                System.out.println("Balance updated: " + sourceAdresse + " " + balanceService.getBalanceInfo(sourceAdresse));
                                System.out.println("Balance updated: " + toAddress + " " + balanceService.getBalanceInfo(toAddress));
                                mempoolService.addTransaction(result3.getTransaction());
                            } else {
                                System.out.println(result3.getErrorMessage());
                            }
                            break;
                        default:
                            System.out.println("Invalid choice. Please enter a valid choice.");
                            break;
                    }
                }
                break;
        }
    }

    private void showAllTransactions(){
        if(transactionService.getTransactions().isEmpty()){
            System.out.println("There are no transactions in the wallet");
        }
        transactionService.getTransactions().stream().forEach(t -> {
            System.out.println(t.getId() + " | " + t.getFromAddress() + " -> " + t.getToAddress() + " | Status: " + t.getStatus());
        });
    }

    private void checkMempool() {
        System.out.println("======= MEMPOOL ========");
        if(mempoolService.getMempool().isEmpty()){
            System.out.println("There are no transactions in the mempool");
        }
        mempoolService.getMempool().stream().forEach(t -> {
            System.out.println(t.getId() + " | " + t.getFromAddress() + " -> " + t.getToAddress() + " | Status: " + t.getStatus());
        });
    }

}
