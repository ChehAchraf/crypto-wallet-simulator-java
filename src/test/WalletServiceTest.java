package test;

import model.BitcoinWallet;
import model.EthereumWallet;
import model.Wallet;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import repository.WalletRepository;
import service.WalletService;
import db.DBConnection;

import java.util.List;
import java.util.Optional;

public class WalletServiceTest {
    private WalletRepository walletRepo;
    private WalletService walletService;

    @Before
    public void setUp() {
        walletRepo = new WalletRepository(DBConnection.getInstance());
        walletService = new WalletService(walletRepo);
    }

    @Test
    public void testCreateBitcoinWallet() {
        // Arrange
        Wallet btcWallet = new BitcoinWallet(0.5);
        
        // Act
        walletService.createWallet(btcWallet);
        
        // Assert
        Assert.assertNotNull("Wallet address should not be null", btcWallet.getAddress());
        Assert.assertTrue("Wallet address should start with bc1", btcWallet.getAddress().startsWith("bc1"));
        Assert.assertEquals("Wallet type should be BITCOIN", "BITCOIN", btcWallet.getType());
        Assert.assertEquals("Wallet amount should be 0.5", 0.5, btcWallet.getAmount(), 0.001);
    }

    @Test
    public void testCreateEthereumWallet() {
        // Arrange
        Wallet ethWallet = new EthereumWallet(2.0);
        
        // Act
        walletService.createWallet(ethWallet);
        
        // Assert
        Assert.assertNotNull("Wallet address should not be null", ethWallet.getAddress());
        Assert.assertTrue("Wallet address should start with 0x", ethWallet.getAddress().startsWith("0x"));
        Assert.assertEquals("Wallet type should be ETHEREUM", "ETHEREUM", ethWallet.getType());
        Assert.assertEquals("Wallet amount should be 2.0", 2.0, ethWallet.getAmount(), 0.001);
    }

    @Test
    public void testGetAllWallets() {
        // Arrange - create some wallets first
        Wallet btcWallet1 = new BitcoinWallet(1.0);
        Wallet ethWallet1 = new EthereumWallet(3.0);
        
        walletService.createWallet(btcWallet1);
        walletService.createWallet(ethWallet1);
        
        // Act
        List<Wallet> wallets = walletService.getAllWallets();
        
        // Assert
        Assert.assertNotNull("Wallets list should not be null", wallets);
        // Note: We can't assert exact size as it depends on database state
    }

    @Test
    public void testFindByAddress() {
        // Arrange
        Wallet testWallet = new BitcoinWallet(1.5);
        walletService.createWallet(testWallet);
        String walletAddress = testWallet.getAddress();
        
        // Act
        Optional<Wallet> foundWallet = walletService.findByAddress(walletAddress);
        
        // Assert
        Assert.assertTrue("Wallet should be found", foundWallet.isPresent());
        Assert.assertEquals("Found wallet should have correct address", walletAddress, foundWallet.get().getAddress());
        Assert.assertEquals("Found wallet should have correct amount", 1.5, foundWallet.get().getAmount(), 0.001);
    }

    @Test
    public void testFindByAddress_NotFound() {
        // Arrange
        String nonExistentAddress = "bc1nonexistentaddress123456789012345678901234567890";
        
        // Act
        Optional<Wallet> foundWallet = walletService.findByAddress(nonExistentAddress);
        
        // Assert
        Assert.assertFalse("Wallet should not be found", foundWallet.isPresent());
    }

    @Test
    public void testGetBalance() {
        // Arrange
        Wallet testWallet = new EthereumWallet(2.5);
        walletService.createWallet(testWallet);
        String walletAddress = testWallet.getAddress();
        
        // Act
        Optional<Double> balance = walletService.getBalance(walletAddress);
        
        // Assert
        Assert.assertTrue("Balance should be found", balance.isPresent());
        Assert.assertEquals("Balance should be 2.5", 2.5, balance.get(), 0.001);
    }

    @Test
    public void testGetBalance_NotFound() {
        // Arrange
        String nonExistentAddress = "0xnonexistentaddress123456789012345678901234567890";
        
        // Act
        Optional<Double> balance = walletService.getBalance(nonExistentAddress);
        
        // Assert
        Assert.assertFalse("Balance should not be found for non-existent address", balance.isPresent());
    }

    @Test
    public void testUpdateBalance() {
        // Arrange
        Wallet testWallet = new BitcoinWallet(1.0);
        walletService.createWallet(testWallet);
        String walletAddress = testWallet.getAddress();
        double newBalance = 5.5;
        
        // Act
        walletService.updateBalance(walletAddress, newBalance);
        
        // Assert
        Optional<Double> updatedBalance = walletService.getBalance(walletAddress);
        Assert.assertTrue("Updated balance should be found", updatedBalance.isPresent());
        Assert.assertEquals("Balance should be updated to 5.5", newBalance, updatedBalance.get(), 0.001);
    }

    @Test
    public void testUpdateBalance_ZeroBalance() {
        // Arrange
        Wallet testWallet = new EthereumWallet(10.0);
        walletService.createWallet(testWallet);
        String walletAddress = testWallet.getAddress();
        
        // Act
        walletService.updateBalance(walletAddress, 0.0);
        
        // Assert
        Optional<Double> balance = walletService.getBalance(walletAddress);
        Assert.assertTrue("Balance should be found", balance.isPresent());
        Assert.assertEquals("Balance should be 0.0", 0.0, balance.get(), 0.001);
    }

    @Test
    public void testUpdateBalance_NegativeBalance() {
        // Arrange
        Wallet testWallet = new BitcoinWallet(5.0);
        walletService.createWallet(testWallet);
        String walletAddress = testWallet.getAddress();
        
        // Act
        walletService.updateBalance(walletAddress, -1.0);
        
        // Assert
        Optional<Double> balance = walletService.getBalance(walletAddress);
        Assert.assertTrue("Balance should be found", balance.isPresent());
        Assert.assertEquals("Balance should be -1.0", -1.0, balance.get(), 0.001);
    }

    @Test
    public void testWalletAddressGeneration() {
        // Arrange & Act
        Wallet btcWallet1 = new BitcoinWallet(1.0);
        Wallet btcWallet2 = new BitcoinWallet(1.0);
        Wallet ethWallet1 = new EthereumWallet(1.0);
        Wallet ethWallet2 = new EthereumWallet(1.0);
        
        // Assert
        Assert.assertNotEquals("Bitcoin wallet addresses should be different", 
                              btcWallet1.getAddress(), btcWallet2.getAddress());
        Assert.assertNotEquals("Ethereum wallet addresses should be different", 
                              ethWallet1.getAddress(), ethWallet2.getAddress());
        
        // Verify address format
        Assert.assertTrue("Bitcoin address should start with bc1", 
                         btcWallet1.getAddress().startsWith("bc1"));
        Assert.assertTrue("Ethereum address should start with 0x", 
                         ethWallet1.getAddress().startsWith("0x"));
        
        // Verify address length (bc1 + 40 hex chars = 43, 0x + 40 hex chars = 42)
        Assert.assertEquals("Bitcoin address should be 43 characters", 43, btcWallet1.getAddress().length());
        Assert.assertEquals("Ethereum address should be 42 characters", 42, ethWallet1.getAddress().length());
    }
}
