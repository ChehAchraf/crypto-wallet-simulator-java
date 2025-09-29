package app;
import java.util.logging.Logger;
import db.DBConnection;
import repository.TransactionRepository;
import service.MempoolService;

public class MainApp {

    private static final Logger logger = Logger.getLogger(MainApp.class.getName());

    public static void main(String[] args) {
        try {
            logger.info("Starting Main App");
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
