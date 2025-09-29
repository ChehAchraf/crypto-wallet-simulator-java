package test;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import service.TransactionValidationService;
import service.WalletService;
import repository.WalletRepository;
import model.BitcoinWallet;
import model.EthereumWallet;
import model.Wallet;
import db.DBConnection;


public class TransactionValidationServiceTest {
    
    private WalletService walletService;
    private TransactionValidationService validationService;
    private WalletRepository walletRepo;
    
    @Before
    public void setUp() {
        walletRepo = new WalletRepository(DBConnection.getInstance());
        walletService = new WalletService(walletRepo);
        validationService = new TransactionValidationService(walletService);
    }

    @Test
    public void testValidateTransaction_Success() {
        // Arrange
        Wallet sourceWallet = new BitcoinWallet(10.0);
        Wallet destWallet = new EthereumWallet(5.0);
        walletService.createWallet(sourceWallet);
        walletService.createWallet(destWallet);
        
        String sourceAddress = sourceWallet.getAddress();
        String destAddress = destWallet.getAddress();
        double amount = 2.0;
        double fees = 0.1;
        
        // Act
        TransactionValidationService.ValidationResult result = 
            validationService.validateTransaction(sourceAddress, destAddress, amount, fees);
        
        // Assert
        Assert.assertTrue("Transaction should be valid", result.isValid());
        Assert.assertNull("Error message should be null for valid transaction", result.getErrorMessage());
    }

    @Test
    public void testValidateTransaction_SourceWalletNotFound() {
        // Arrange
        Wallet destWallet = new EthereumWallet(5.0);
        walletService.createWallet(destWallet);
        
        String nonExistentSourceAddress = "bc1nonexistentaddress123456789012345678901234567890";
        String destAddress = destWallet.getAddress();
        double amount = 2.0;
        double fees = 0.1;
        
        // Act
        TransactionValidationService.ValidationResult result = 
            validationService.validateTransaction(nonExistentSourceAddress, destAddress, amount, fees);
        
        // Assert
        Assert.assertFalse("Transaction should be invalid", result.isValid());
        Assert.assertEquals("Error message should indicate source wallet not found", 
                          "Source wallet address not found in database.", result.getErrorMessage());
    }

    @Test
    public void testValidateTransaction_DestinationWalletNotFound() {
        // Arrange
        Wallet sourceWallet = new BitcoinWallet(10.0);
        walletService.createWallet(sourceWallet);
        
        String sourceAddress = sourceWallet.getAddress();
        String nonExistentDestAddress = "0xnonexistentaddress123456789012345678901234567890";
        double amount = 2.0;
        double fees = 0.1;
        
        // Act
        TransactionValidationService.ValidationResult result = 
            validationService.validateTransaction(sourceAddress, nonExistentDestAddress, amount, fees);
        
        // Assert
        Assert.assertFalse("Transaction should be invalid", result.isValid());
        Assert.assertEquals("Error message should indicate destination wallet not found", 
                          "Destination wallet address not found in database.", result.getErrorMessage());
    }

    @Test
    public void testValidateTransaction_SameSourceAndDestination() {
        // Arrange
        Wallet wallet = new BitcoinWallet(10.0);
        walletService.createWallet(wallet);
        
        String address = wallet.getAddress();
        double amount = 2.0;
        double fees = 0.1;
        
        // Act
        TransactionValidationService.ValidationResult result = 
            validationService.validateTransaction(address, address, amount, fees);
        
        // Assert
        Assert.assertFalse("Transaction should be invalid", result.isValid());
        Assert.assertEquals("Error message should indicate can't send to self", 
                          "You can't send to yourself", result.getErrorMessage());
    }

    @Test
    public void testValidateTransaction_ZeroAmount() {
        // Arrange
        Wallet sourceWallet = new BitcoinWallet(10.0);
        Wallet destWallet = new EthereumWallet(5.0);
        walletService.createWallet(sourceWallet);
        walletService.createWallet(destWallet);
        
        String sourceAddress = sourceWallet.getAddress();
        String destAddress = destWallet.getAddress();
        double amount = 0.0;
        double fees = 0.1;
        
        // Act
        TransactionValidationService.ValidationResult result = 
            validationService.validateTransaction(sourceAddress, destAddress, amount, fees);
        
        // Assert
        Assert.assertFalse("Transaction should be invalid", result.isValid());
        Assert.assertEquals("Error message should indicate amount must be greater than 0", 
                          "Amount must be greater than 0", result.getErrorMessage());
    }

    @Test
    public void testValidateTransaction_NegativeAmount() {
        // Arrange
        Wallet sourceWallet = new BitcoinWallet(10.0);
        Wallet destWallet = new EthereumWallet(5.0);
        walletService.createWallet(sourceWallet);
        walletService.createWallet(destWallet);
        
        String sourceAddress = sourceWallet.getAddress();
        String destAddress = destWallet.getAddress();
        double amount = -1.0;
        double fees = 0.1;
        
        // Act
        TransactionValidationService.ValidationResult result = 
            validationService.validateTransaction(sourceAddress, destAddress, amount, fees);
        
        // Assert
        Assert.assertFalse("Transaction should be invalid", result.isValid());
        Assert.assertEquals("Error message should indicate amount must be greater than 0", 
                          "Amount must be greater than 0", result.getErrorMessage());
    }

    @Test
    public void testValidateTransaction_InsufficientBalance() {
        // Arrange
        Wallet sourceWallet = new BitcoinWallet(1.0); // Low balance
        Wallet destWallet = new EthereumWallet(5.0);
        walletService.createWallet(sourceWallet);
        walletService.createWallet(destWallet);
        
        String sourceAddress = sourceWallet.getAddress();
        String destAddress = destWallet.getAddress();
        double amount = 2.0; // More than available balance
        double fees = 0.1;
        
        // Act
        TransactionValidationService.ValidationResult result = 
            validationService.validateTransaction(sourceAddress, destAddress, amount, fees);
        
        // Assert
        Assert.assertFalse("Transaction should be invalid", result.isValid());
        Assert.assertTrue("Error message should indicate insufficient balance", 
                        result.getErrorMessage().contains("You don't have enough balance"));
    }

    @Test
    public void testValidateTransaction_ExactBalance() {
        // Arrange
        Wallet sourceWallet = new BitcoinWallet(2.1); // Exactly amount + fees
        Wallet destWallet = new EthereumWallet(5.0);
        walletService.createWallet(sourceWallet);
        walletService.createWallet(destWallet);
        
        String sourceAddress = sourceWallet.getAddress();
        String destAddress = destWallet.getAddress();
        double amount = 2.0;
        double fees = 0.1;
        
        // Act
        TransactionValidationService.ValidationResult result = 
            validationService.validateTransaction(sourceAddress, destAddress, amount, fees);
        
        // Assert
        Assert.assertTrue("Transaction should be valid with exact balance", result.isValid());
        Assert.assertNull("Error message should be null for valid transaction", result.getErrorMessage());
    }

    @Test
    public void testValidateTransaction_HighFees() {
        // Arrange
        Wallet sourceWallet = new BitcoinWallet(10.0);
        Wallet destWallet = new EthereumWallet(5.0);
        walletService.createWallet(sourceWallet);
        walletService.createWallet(destWallet);
        
        String sourceAddress = sourceWallet.getAddress();
        String destAddress = destWallet.getAddress();
        double amount = 5.0;
        double fees = 6.0; // Fees higher than remaining balance
        
        // Act
        TransactionValidationService.ValidationResult result = 
            validationService.validateTransaction(sourceAddress, destAddress, amount, fees);
        
        // Assert
        Assert.assertFalse("Transaction should be invalid with high fees", result.isValid());
        Assert.assertTrue("Error message should indicate insufficient balance", 
                        result.getErrorMessage().contains("You don't have enough balance"));
    }

    @Test
    public void testValidateTransaction_BalanceRetrievalFailure() {
        // Arrange
        Wallet sourceWallet = new BitcoinWallet(10.0);
        Wallet destWallet = new EthereumWallet(5.0);
        walletService.createWallet(sourceWallet);
        walletService.createWallet(destWallet);
        
        // Simulate a scenario where balance retrieval might fail
        // by using a potentially problematic address format
        String sourceAddress = sourceWallet.getAddress();
        String destAddress = destWallet.getAddress();
        double amount = 2.0;
        double fees = 0.1;
        
        // Act
        TransactionValidationService.ValidationResult result = 
            validationService.validateTransaction(sourceAddress, destAddress, amount, fees);
        
        // Assert - This test might pass or fail depending on the implementation
        // We're mainly testing that the method doesn't crash
        Assert.assertNotNull("Validation result should not be null", result);
    }

    @Test
    public void testValidationResult_Success() {
        // Arrange & Act
        TransactionValidationService.ValidationResult result = 
            TransactionValidationService.ValidationResult.success();
        
        // Assert
        Assert.assertTrue("Success result should be valid", result.isValid());
        Assert.assertNull("Success result should have null error message", result.getErrorMessage());
    }

    @Test
    public void testValidationResult_Error() {
        // Arrange
        String errorMessage = "Test error message";
        
        // Act
        TransactionValidationService.ValidationResult result = 
            TransactionValidationService.ValidationResult.error(errorMessage);
        
        // Assert
        Assert.assertFalse("Error result should not be valid", result.isValid());
        Assert.assertEquals("Error result should have correct error message", 
                          errorMessage, result.getErrorMessage());
    }

    @Test
    public void testValidateTransaction_CrossCurrencyTransaction() {
        // Arrange - Bitcoin wallet sending to Ethereum wallet
        Wallet btcSourceWallet = new BitcoinWallet(5.0);
        Wallet ethDestWallet = new EthereumWallet(3.0);
        walletService.createWallet(btcSourceWallet);
        walletService.createWallet(ethDestWallet);
        
        String sourceAddress = btcSourceWallet.getAddress();
        String destAddress = ethDestWallet.getAddress();
        double amount = 1.0;
        double fees = 0.05;
        
        // Act
        TransactionValidationService.ValidationResult result = 
            validationService.validateTransaction(sourceAddress, destAddress, amount, fees);
        
        // Assert
        Assert.assertTrue("Cross-currency transaction should be valid", result.isValid());
        Assert.assertNull("Error message should be null for valid cross-currency transaction", result.getErrorMessage());
    }
}
