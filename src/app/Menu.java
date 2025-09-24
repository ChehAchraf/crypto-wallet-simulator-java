package app;
import db.DBConnection;
import model.*;
import service.*;

import java.util.List;
import java.util.Scanner;
import repository.WalletRepository;
import util.InputValidation;
public class Menu {

    private  WalletService walletService;
    private Scanner scanner;
    private boolean isActive;

    public Menu() {
        this.scanner = new Scanner(System.in);
        this.isActive = true;
        DBConnection dbConnection = DBConnection.getInstance();
        WalletRepository walletRepository = new WalletRepository(dbConnection);
        this.walletService = new WalletService(walletRepository);
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
            case "0":
                isActive = false;
                break;
        }
    }



	private void showMenu() {
        System.out.println("1. Create Wallet");
        System.out.println("2. Display all Wallets");
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
            			while(!InputValidation.isDouble(amountAsString)) {
            				System.out.print("\nPlease enter a valid amount : ");
            				amountAsString = scanner.nextLine();
            			}
            			
            			double amount = Double.parseDouble(amountAsString);
            			
                		Wallet btcWallet = new BitcoinWallet(amount);
                    walletService.createWallet(btcWallet);
                    System.out.println("Bitcoin wallet created successfully");
                    
            		}catch (NumberFormatException e) {
            		    System.out.println("Invalid number format. Please enter a valid numeric value.");
            		} catch (Exception e) {
            		    System.out.println("Unexpected error while creating Bitcoin wallet: " + e.getMessage());
            		}
            break;
        // case two => creating Etherium wallet 
            case "2" :
            	try {
        			System.out.print("\nPlease enter your amount : ");
        			String amountAsString = scanner.nextLine();
        			while(!InputValidation.isDouble(amountAsString)) {
        				System.out.print("\nPlease enter a valid amount : ");
        				amountAsString = scanner.nextLine();
        			}

        			double amount = Double.parseDouble(amountAsString);

            		Wallet ethWallet = new EthereumWallet(amount);
                walletService.createWallet(ethWallet);
                System.out.println("Ethereum wallet created successfully");
                
        		}catch (NumberFormatException e) {
        		    System.out.println("Invalid number format. Please enter a valid numeric value.");
        		} catch (Exception e) {
        		    System.out.println("Unexpected error while creating Ethereum wallet: " + e.getMessage());
        		}
            	break;
            
        }
    }
    
    private void DisplayWallets() {
    		System.out.println("====== list of wallets =======");
    		List<Wallet> listWallet =  walletService.getAllWallets();
    		listWallet.stream().forEach(p -> System.out.println("adresse : " + p.getAddress()));
    		System.out.println("==============================");
    		
		
	}

}
