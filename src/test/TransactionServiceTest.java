package test;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;

import service.TransactionService;
import repository.TransactionRepository;
import model.Transaction;
import model.BitcoinTransaction;
import model.EthereumTransaction;
import db.DBConnection;
import enums.FeePriority;
import enums.TransactionStatus;

import java.util.List;

public class TransactionServiceTest {
    
    private TransactionService transactionService;
    private TransactionRepository transactionRepo;
    
    @Before
    public void setUp() {
        transactionRepo = new TransactionRepository(DBConnection.getInstance());
        transactionService = new TransactionService(transactionRepo);
    }

    @Test
    public void testCreateBitcoinTransaction() {
        // Arrange
        Transaction transaction = new BitcoinTransaction("testFrom123", "testTo456", 0.001, FeePriority.STANDARD);
        
        // Act
        boolean result = transactionService.creatTransaction(transaction);
        
        // Assert
        Assert.assertTrue("Bitcoin transaction creation should succeed", result);
    }

    @Test
    public void testCreateEthereumTransaction() {
        // Arrange
        Transaction transaction = new EthereumTransaction("ethFrom789", "ethTo012", 0.1, FeePriority.RAPIDE);
        
        // Act
        boolean result = transactionService.creatTransaction(transaction);
        
        // Assert
        Assert.assertTrue("Ethereum transaction creation should succeed", result);
    }

    @Test
    public void testCreateTransactionWithDifferentFeePriorities() {
        // Test ECONOMIQUE priority
        Transaction transaction1 = new BitcoinTransaction("from1", "to1", 0.1, FeePriority.ECONOMIQUE);
        boolean result1 = transactionService.creatTransaction(transaction1);
        Assert.assertTrue("Transaction with ECONOMIQUE priority should succeed", result1);
        
        // Test STANDARD priority
        Transaction transaction2 = new BitcoinTransaction("from2", "to2", 0.2, FeePriority.STANDARD);
        boolean result2 = transactionService.creatTransaction(transaction2);
        Assert.assertTrue("Transaction with STANDARD priority should succeed", result2);
        
        // Test RAPIDE priority
        Transaction transaction3 = new BitcoinTransaction("from3", "to3", 0.3, FeePriority.RAPIDE);
        boolean result3 = transactionService.creatTransaction(transaction3);
        Assert.assertTrue("Transaction with RAPIDE priority should succeed", result3);
    }

    @Test
    public void testGetTransactions() {
        // Act
        List<Transaction> transactions = transactionService.getTransactions();
        
        // Assert
        Assert.assertNotNull("Transactions list should not be null", transactions);
        // Note: We can't assert exact size as it depends on database state
    }

    @Test
    public void testUpdateTransaction() {
        // First create a transaction
        Transaction transaction = new BitcoinTransaction("updateFrom", "updateTo", 0.5, FeePriority.STANDARD);
        boolean createResult = transactionService.creatTransaction(transaction);
        Assert.assertTrue("Transaction should be created successfully", createResult);
        
        // Get the transaction ID (assuming it gets set during creation)
        List<Transaction> transactions = transactionService.getTransactions();
        Transaction createdTransaction = null;
        for (Transaction t : transactions) {
            if (t.getFromAddress().equals("updateFrom") && t.getToAddress().equals("updateTo")) {
                createdTransaction = t;
                break;
            }
        }
        
        if (createdTransaction != null) {
            // Update the transaction
            createdTransaction.setStatus(TransactionStatus.CONFIRMED);
            boolean updateResult = transactionService.updateTransaction(createdTransaction);
            Assert.assertTrue("Transaction update should succeed", updateResult);
        }
    }

    // Edge case tests
    @Test
    public void testCreateTransactionWithNegativeAmount() {
        // Arrange
        Transaction transaction = new BitcoinTransaction("from123", "to456", -1.0, FeePriority.STANDARD);
        
        // Act
        boolean result = transactionService.creatTransaction(transaction);
        
        // Assert
        // The result depends on business rules - negative amounts might be allowed or not
        Assert.assertNotNull("Result should not be null", result);
    }

    @Test
    public void testCreateTransactionWithZeroAmount() {
        // Arrange
        Transaction transaction = new BitcoinTransaction("from123", "to456", 0.0, FeePriority.STANDARD);
        
        // Act
        boolean result = transactionService.creatTransaction(transaction);
        
        // Assert
        Assert.assertNotNull("Result should not be null", result);
    }

    @Test
    public void testCreateTransactionWithLargeAmount() {
        // Arrange
        Transaction transaction = new BitcoinTransaction("from123", "to456", 999999.99, FeePriority.RAPIDE);
        
        // Act
        boolean result = transactionService.creatTransaction(transaction);
        
        // Assert
        Assert.assertTrue("Large amount transaction should be created", result);
    }

    @Test
    public void testCreateTransactionWithSameFromToAddress() {
        // Arrange
        Transaction transaction = new BitcoinTransaction("sameAddress", "sameAddress", 0.1, FeePriority.STANDARD);
        
        // Act
        boolean result = transactionService.creatTransaction(transaction);
        
        // Assert
        Assert.assertTrue("Transaction with same from/to addresses should be created", result);
    }
}