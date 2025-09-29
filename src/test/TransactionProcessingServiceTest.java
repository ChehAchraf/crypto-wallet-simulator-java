package test;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import service.TransactionProcessingService;
import service.TransactionValidationService;
import service.BalanceService;
import service.WalletService;
import repository.TransactionRepository;
import repository.WalletRepository;
import model.BitcoinTransaction;
import model.EthereumTransaction;
import model.Wallet;
import model.BitcoinWallet;
import model.EthereumWallet;
import db.DBConnection;
import enums.FeePriority;

import java.util.Optional;

public class TransactionProcessingServiceTest {
    
    private TransactionProcessingService processingService;
    private TransactionRepository transactionRepo;
    private WalletRepository walletRepo;
    private WalletService walletService;
    private TransactionValidationService validationService;
    private BalanceService balanceService;

    @Before
    public void setUp() {
        transactionRepo = new TransactionRepository(DBConnection.getInstance());
        walletRepo = new WalletRepository(DBConnection.getInstance());
        walletService = new WalletService(walletRepo);
        validationService = new TransactionValidationService(walletService);
        balanceService = new BalanceService(walletService);
        processingService = new TransactionProcessingService(transactionRepo, validationService, balanceService);
    }

    @Test
    public void testProcessTransaction_Success() {
        // Arrange
        Wallet sourceWallet = new BitcoinWallet(10.0);
        Wallet destWallet = new EthereumWallet(5.0);
        walletService.createWallet(sourceWallet);
        walletService.createWallet(destWallet);
        
        BitcoinTransaction transaction = new BitcoinTransaction(
            sourceWallet.getAddress(), 
            destWallet.getAddress(), 
            2.0, 
            FeePriority.STANDARD
        );
        
        // Act
        TransactionProcessingService.TransactionResult result = processingService.processTransaction(transaction);
        
        // Assert
        Assert.assertTrue("Transaction processing should succeed", result.isSuccess());
        Assert.assertNull("Error message should be null for successful transaction", result.getErrorMessage());
        Assert.assertNotNull("Transaction should not be null", result.getTransaction());
    }

    @Test
    public void testProcessTransaction_ValidationFailure() {
        // Arrange - Create only destination wallet, source wallet doesn't exist
        Wallet destWallet = new EthereumWallet(5.0);
        walletService.createWallet(destWallet);
        
        BitcoinTransaction transaction = new BitcoinTransaction(
            "nonexistentSourceAddress", 
            destWallet.getAddress(), 
            2.0, 
            FeePriority.STANDARD
        );
        
        // Act
        TransactionProcessingService.TransactionResult result = processingService.processTransaction(transaction);
        
        // Assert
        Assert.assertFalse("Transaction processing should fail", result.isSuccess());
        Assert.assertNotNull("Error message should not be null", result.getErrorMessage());
        Assert.assertTrue("Error message should indicate source wallet not found", 
                        result.getErrorMessage().contains("Source wallet address not found"));
        Assert.assertNull("Transaction should be null for failed processing", result.getTransaction());
    }

    @Test
    public void testProcessTransaction_InsufficientBalance() {
        // Arrange
        Wallet sourceWallet = new BitcoinWallet(1.0); // Low balance
        Wallet destWallet = new EthereumWallet(5.0);
        walletService.createWallet(sourceWallet);
        walletService.createWallet(destWallet);
        
        BitcoinTransaction transaction = new BitcoinTransaction(
            sourceWallet.getAddress(), 
            destWallet.getAddress(), 
            2.0, // More than available balance
            FeePriority.STANDARD
        );
        
        // Act
        TransactionProcessingService.TransactionResult result = processingService.processTransaction(transaction);
        
        // Assert
        Assert.assertFalse("Transaction processing should fail", result.isSuccess());
        Assert.assertNotNull("Error message should not be null", result.getErrorMessage());
        Assert.assertTrue("Error message should indicate insufficient balance", 
                        result.getErrorMessage().contains("You don't have enough balance"));
        Assert.assertNull("Transaction should be null for failed processing", result.getTransaction());
    }

    @Test
    public void testProcessTransaction_ZeroAmount() {
        // Arrange
        Wallet sourceWallet = new BitcoinWallet(10.0);
        Wallet destWallet = new EthereumWallet(5.0);
        walletService.createWallet(sourceWallet);
        walletService.createWallet(destWallet);
        
        BitcoinTransaction transaction = new BitcoinTransaction(
            sourceWallet.getAddress(), 
            destWallet.getAddress(), 
            0.0, // Zero amount
            FeePriority.STANDARD
        );
        
        // Act
        TransactionProcessingService.TransactionResult result = processingService.processTransaction(transaction);
        
        // Assert
        Assert.assertFalse("Transaction processing should fail", result.isSuccess());
        Assert.assertNotNull("Error message should not be null", result.getErrorMessage());
        Assert.assertTrue("Error message should indicate amount must be greater than 0", 
                        result.getErrorMessage().contains("Amount must be greater than 0"));
        Assert.assertNull("Transaction should be null for failed processing", result.getTransaction());
    }

    @Test
    public void testProcessTransaction_SameSourceAndDestination() {
        // Arrange
        Wallet wallet = new BitcoinWallet(10.0);
        walletService.createWallet(wallet);
        
        BitcoinTransaction transaction = new BitcoinTransaction(
            wallet.getAddress(), 
            wallet.getAddress(), // Same address
            2.0, 
            FeePriority.STANDARD
        );
        
        // Act
        TransactionProcessingService.TransactionResult result = processingService.processTransaction(transaction);
        
        // Assert
        Assert.assertFalse("Transaction processing should fail", result.isSuccess());
        Assert.assertNotNull("Error message should not be null", result.getErrorMessage());
        Assert.assertTrue("Error message should indicate can't send to self", 
                        result.getErrorMessage().contains("You can't send to yourself"));
        Assert.assertNull("Transaction should be null for failed processing", result.getTransaction());
    }

    @Test
    public void testProcessTransaction_EthereumTransaction() {
        // Arrange
        Wallet sourceWallet = new EthereumWallet(10.0);
        Wallet destWallet = new BitcoinWallet(5.0);
        walletService.createWallet(sourceWallet);
        walletService.createWallet(destWallet);
        
        EthereumTransaction transaction = new EthereumTransaction(
            sourceWallet.getAddress(), 
            destWallet.getAddress(), 
            3.0, 
            FeePriority.RAPIDE
        );
        
        // Act
        TransactionProcessingService.TransactionResult result = processingService.processTransaction(transaction);
        
        // Assert
        Assert.assertTrue("Ethereum transaction processing should succeed", result.isSuccess());
        Assert.assertNull("Error message should be null for successful transaction", result.getErrorMessage());
        Assert.assertNotNull("Transaction should not be null", result.getTransaction());
        // Note: getType() method is not available in the Transaction class
        // This assertion would need to be implemented based on the actual model structure
    }

    @Test
    public void testProcessTransaction_BalanceUpdateVerification() {
        // Arrange
        Wallet sourceWallet = new BitcoinWallet(10.0);
        Wallet destWallet = new EthereumWallet(5.0);
        walletService.createWallet(sourceWallet);
        walletService.createWallet(destWallet);
        
        String sourceAddress = sourceWallet.getAddress();
        String destAddress = destWallet.getAddress();
        
        // Get initial balances
        Optional<Double> initialSourceBalance = walletService.getBalance(sourceAddress);
        Optional<Double> initialDestBalance = walletService.getBalance(destAddress);
        
        BitcoinTransaction transaction = new BitcoinTransaction(
            sourceAddress, 
            destAddress, 
            2.0, 
            FeePriority.STANDARD
        );
        
        // Act
        TransactionProcessingService.TransactionResult result = processingService.processTransaction(transaction);
        
        // Assert
        Assert.assertTrue("Transaction processing should succeed", result.isSuccess());
        
        // Verify balance updates
        Optional<Double> finalSourceBalance = walletService.getBalance(sourceAddress);
        Optional<Double> finalDestBalance = walletService.getBalance(destAddress);
        
        Assert.assertTrue("Final source balance should be present", finalSourceBalance.isPresent());
        Assert.assertTrue("Final dest balance should be present", finalDestBalance.isPresent());
        
        // Source should have: initial - amount - fees
        double expectedSourceBalance = initialSourceBalance.get() - 2.0 - transaction.getFees();
        Assert.assertEquals("Source balance should be updated correctly", 
                          expectedSourceBalance, finalSourceBalance.get(), 0.001);
        
        // Destination should have: initial + amount
        double expectedDestBalance = initialDestBalance.get() + 2.0;
        Assert.assertEquals("Destination balance should be updated correctly", 
                          expectedDestBalance, finalDestBalance.get(), 0.001);
    }

    @Test
    public void testTransactionResult_Success() {
        // Arrange
        BitcoinTransaction transaction = new BitcoinTransaction("from", "to", 1.0, FeePriority.STANDARD);
        
        // Act
        TransactionProcessingService.TransactionResult result = 
            TransactionProcessingService.TransactionResult.success(transaction);
        
        // Assert
        Assert.assertTrue("Success result should be successful", result.isSuccess());
        Assert.assertNull("Success result should have null error message", result.getErrorMessage());
        Assert.assertEquals("Success result should contain the transaction", transaction, result.getTransaction());
    }

    @Test
    public void testTransactionResult_Error() {
        // Arrange
        String errorMessage = "Test error message";
        
        // Act
        TransactionProcessingService.TransactionResult result = 
            TransactionProcessingService.TransactionResult.error(errorMessage);
        
        // Assert
        Assert.assertFalse("Error result should not be successful", result.isSuccess());
        Assert.assertEquals("Error result should have correct error message", errorMessage, result.getErrorMessage());
        Assert.assertNull("Error result should have null transaction", result.getTransaction());
    }

    @Test
    public void testProcessTransaction_DifferentFeePriorities() {
        // Arrange
        Wallet sourceWallet = new BitcoinWallet(10.0);
        Wallet destWallet = new EthereumWallet(5.0);
        walletService.createWallet(sourceWallet);
        walletService.createWallet(destWallet);
        
        String sourceAddress = sourceWallet.getAddress();
        String destAddress = destWallet.getAddress();
        
        // Test different fee priorities
        FeePriority[] priorities = {FeePriority.ECONOMIQUE, FeePriority.STANDARD, FeePriority.RAPIDE};
        
        for (FeePriority priority : priorities) {
            // Act
            BitcoinTransaction transaction = new BitcoinTransaction(sourceAddress, destAddress, 1.0, priority);
            TransactionProcessingService.TransactionResult result = processingService.processTransaction(transaction);
            
            // Assert
            Assert.assertTrue("Transaction with " + priority + " priority should succeed", result.isSuccess());
            Assert.assertNotNull("Transaction should not be null", result.getTransaction());
        }
    }

    @Test
    public void testProcessTransaction_CrossCurrencyTransaction() {
        // Arrange - Bitcoin wallet sending to Ethereum wallet
        Wallet btcSourceWallet = new BitcoinWallet(10.0);
        Wallet ethDestWallet = new EthereumWallet(5.0);
        walletService.createWallet(btcSourceWallet);
        walletService.createWallet(ethDestWallet);
        
        BitcoinTransaction transaction = new BitcoinTransaction(
            btcSourceWallet.getAddress(), 
            ethDestWallet.getAddress(), 
            2.0, 
            FeePriority.STANDARD
        );
        
        // Act
        TransactionProcessingService.TransactionResult result = processingService.processTransaction(transaction);
        
        // Assert
        Assert.assertTrue("Cross-currency transaction should succeed", result.isSuccess());
        Assert.assertNull("Error message should be null for successful cross-currency transaction", result.getErrorMessage());
        Assert.assertNotNull("Transaction should not be null", result.getTransaction());
    }
}
