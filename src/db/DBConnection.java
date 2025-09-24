package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
	private static final Logger logger = Logger.getLogger(DBConnection.class.getClass().getName());
	private static DBConnection instance;
	
    private final String URL = "jdbc:postgresql://localhost:5432/crypto";
    private final String USERNAME = "postgres";
    private final String PASSWORD = "ashraf2025";
    private Connection connection;
    
    private DBConnection() {
    		try {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                logger.info("Connected to database successfully");
                logger.info("Driver was loaded successfully");
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }
    
    public static DBConnection getInstance() {
    		if(instance == null) {
    			instance = new DBConnection();
    		}
    		return instance;
    }
    
    public Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            logger.info("connected");
            return conn;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failer connection to db", e);
            throw new SQLException("Make sure your db data is correct" + e.getMessage());
        }
    }
    
}
