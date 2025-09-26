package app;

import db.DBConnection;
import repository.TransactionRepository;
import service.MempoolService;

public class MainApp {

    public static void main(String[] args) {
        try {
        DBConnection dbConnection = DBConnection.getInstance();
        TransactionRepository transactionRepository = new TransactionRepository(dbConnection);
        MempoolService mempoolService = new MempoolService(transactionRepository);
        Menu menu = new Menu(mempoolService);
        menu.startApplication();
        } catch (Exception e) {
            System.out.println("Error while launchin app : " + e.getMessage());
        }
    }
}
