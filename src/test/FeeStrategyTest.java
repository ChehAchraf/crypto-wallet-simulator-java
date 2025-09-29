package test;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import strategy.BitcoinFeeStrategy;
import strategy.EthereumFeeStrategy;
import model.BitcoinTransaction;
import model.EthereumTransaction;
import model.Transaction;
import enums.FeePriority;

public class FeeStrategyTest {
    
    private BitcoinFeeStrategy bitcoinFeeStrategy;
    private EthereumFeeStrategy ethereumFeeStrategy;

    @Before
    public void setUp() {
        bitcoinFeeStrategy = new BitcoinFeeStrategy();
        ethereumFeeStrategy = new EthereumFeeStrategy();
    }

    // Bitcoin Fee Strategy Tests
    @Test
    public void testBitcoinFeeStrategy_Economique() {
        // Arrange
        Transaction transaction = new BitcoinTransaction("from", "to", 1.0, FeePriority.ECONOMIQUE);
        
        // Act
        double fee = bitcoinFeeStrategy.calculateFees(transaction);
        
        // Assert
        Assert.assertTrue("Fee should be greater than 0", fee > 0);
        // Base fee calculation: 250 bytes * 50 satoshi/byte * 1e-8 = 0.00125 BTC
        // Economique: 0.00125 * 0.8 = 0.001 BTC
        Assert.assertEquals("Economique fee should be 80% of base fee", 0.001, fee, 0.0001);
    }

    @Test
    public void testBitcoinFeeStrategy_Standard() {
        // Arrange
        Transaction transaction = new BitcoinTransaction("from", "to", 1.0, FeePriority.STANDARD);
        
        // Act
        double fee = bitcoinFeeStrategy.calculateFees(transaction);
        
        // Assert
        Assert.assertTrue("Fee should be greater than 0", fee > 0);
        // Base fee calculation: 250 bytes * 50 satoshi/byte * 1e-8 = 0.00125 BTC
        Assert.assertEquals("Standard fee should equal base fee", 0.00125, fee, 0.0001);
    }

    @Test
    public void testBitcoinFeeStrategy_Rapide() {
        // Arrange
        Transaction transaction = new BitcoinTransaction("from", "to", 1.0, FeePriority.RAPIDE);
        
        // Act
        double fee = bitcoinFeeStrategy.calculateFees(transaction);
        
        // Assert
        Assert.assertTrue("Fee should be greater than 0", fee > 0);
        // Base fee calculation: 250 bytes * 50 satoshi/byte * 1e-8 = 0.00125 BTC
        // Rapide: 0.00125 * 1 = 0.00125 BTC
        Assert.assertEquals("Rapide fee should equal base fee", 0.00125, fee, 0.0001);
    }

    @Test
    public void testBitcoinFeeStrategy_ConfirmationTime_Economique() {
        // Arrange
        Transaction transaction = new BitcoinTransaction("from", "to", 1.0, FeePriority.ECONOMIQUE);
        
        // Act
        int confirmationTime = bitcoinFeeStrategy.estimateConfirmationTime(transaction);
        
        // Assert
        Assert.assertEquals("Economique confirmation time should be 240 minutes", 240, confirmationTime);
    }

    @Test
    public void testBitcoinFeeStrategy_ConfirmationTime_Standard() {
        // Arrange
        Transaction transaction = new BitcoinTransaction("from", "to", 1.0, FeePriority.STANDARD);
        
        // Act
        int confirmationTime = bitcoinFeeStrategy.estimateConfirmationTime(transaction);
        
        // Assert
        Assert.assertEquals("Standard confirmation time should be 120 minutes", 120, confirmationTime);
    }

    @Test
    public void testBitcoinFeeStrategy_ConfirmationTime_Rapide() {
        // Arrange
        Transaction transaction = new BitcoinTransaction("from", "to", 1.0, FeePriority.RAPIDE);
        
        // Act
        int confirmationTime = bitcoinFeeStrategy.estimateConfirmationTime(transaction);
        
        // Assert
        Assert.assertEquals("Rapide confirmation time should be 30 minutes", 30, confirmationTime);
    }

    // Ethereum Fee Strategy Tests
    @Test
    public void testEthereumFeeStrategy_FeeCalculation() {
        // Arrange
        Transaction transaction = new EthereumTransaction("from", "to", 1.0, FeePriority.STANDARD);
        
        // Act
        double fee = ethereumFeeStrategy.calculateFees(transaction);
        
        // Assert
        Assert.assertTrue("Fee should be greater than 0", fee > 0);
        // Fee calculation: 21000 gas * 50 gwei * 1e-9 = 0.00105 ETH
        Assert.assertEquals("Ethereum fee should be 0.00105 ETH", 0.00105, fee, 0.0001);
    }

    @Test
    public void testEthereumFeeStrategy_FeeCalculation_DifferentPriorities() {
        // Test that fee calculation is the same regardless of priority for Ethereum
        Transaction economique = new EthereumTransaction("from1", "to1", 1.0, FeePriority.ECONOMIQUE);
        Transaction standard = new EthereumTransaction("from2", "to2", 1.0, FeePriority.STANDARD);
        Transaction rapide = new EthereumTransaction("from3", "to3", 1.0, FeePriority.RAPIDE);
        
        double fee1 = ethereumFeeStrategy.calculateFees(economique);
        double fee2 = ethereumFeeStrategy.calculateFees(standard);
        double fee3 = ethereumFeeStrategy.calculateFees(rapide);
        
        Assert.assertEquals("All Ethereum fees should be the same", fee1, fee2, 0.0001);
        Assert.assertEquals("All Ethereum fees should be the same", fee2, fee3, 0.0001);
    }

    @Test
    public void testEthereumFeeStrategy_ConfirmationTime_Economique() {
        // Arrange
        Transaction transaction = new EthereumTransaction("from", "to", 1.0, FeePriority.ECONOMIQUE);
        
        // Act
        int confirmationTime = ethereumFeeStrategy.estimateConfirmationTime(transaction);
        
        // Assert
        // 5 * 13 = 65 seconds
        Assert.assertEquals("Economique confirmation time should be 65 seconds", 65, confirmationTime);
    }

    @Test
    public void testEthereumFeeStrategy_ConfirmationTime_Standard() {
        // Arrange
        Transaction transaction = new EthereumTransaction("from", "to", 1.0, FeePriority.STANDARD);
        
        // Act
        int confirmationTime = ethereumFeeStrategy.estimateConfirmationTime(transaction);
        
        // Assert
        // 2 * 13 = 26 seconds
        Assert.assertEquals("Standard confirmation time should be 26 seconds", 26, confirmationTime);
    }

    @Test
    public void testEthereumFeeStrategy_ConfirmationTime_Rapide() {
        // Arrange
        Transaction transaction = new EthereumTransaction("from", "to", 1.0, FeePriority.RAPIDE);
        
        // Act
        int confirmationTime = ethereumFeeStrategy.estimateConfirmationTime(transaction);
        
        // Assert
        // 1 * 13 = 13 seconds
        Assert.assertEquals("Rapide confirmation time should be 13 seconds", 13, confirmationTime);
    }

    // Cross-Strategy Comparison Tests
    @Test
    public void testFeeComparison_BitcoinVsEthereum() {
        // Arrange
        Transaction btcTransaction = new BitcoinTransaction("btcFrom", "btcTo", 1.0, FeePriority.STANDARD);
        Transaction ethTransaction = new EthereumTransaction("ethFrom", "ethTo", 1.0, FeePriority.STANDARD);
        
        // Act
        double btcFee = bitcoinFeeStrategy.calculateFees(btcTransaction);
        double ethFee = ethereumFeeStrategy.calculateFees(ethTransaction);
        
        // Assert
        Assert.assertNotEquals("Bitcoin and Ethereum fees should be different", btcFee, ethFee, 0.0001);
        Assert.assertTrue("Both fees should be positive", btcFee > 0 && ethFee > 0);
    }

    @Test
    public void testConfirmationTimeComparison_BitcoinVsEthereum() {
        // Arrange
        Transaction btcTransaction = new BitcoinTransaction("btcFrom", "btcTo", 1.0, FeePriority.STANDARD);
        Transaction ethTransaction = new EthereumTransaction("ethFrom", "ethTo", 1.0, FeePriority.STANDARD);
        
        // Act
        int btcConfirmationTime = bitcoinFeeStrategy.estimateConfirmationTime(btcTransaction);
        int ethConfirmationTime = ethereumFeeStrategy.estimateConfirmationTime(ethTransaction);
        
        // Assert
        Assert.assertTrue("Bitcoin confirmation time should be much longer than Ethereum", 
                        btcConfirmationTime > ethConfirmationTime);
        Assert.assertTrue("Both confirmation times should be positive", 
                        btcConfirmationTime > 0 && ethConfirmationTime > 0);
    }

    @Test
    public void testBitcoinFeeStrategy_PriorityOrder() {
        // Arrange
        Transaction economique = new BitcoinTransaction("from1", "to1", 1.0, FeePriority.ECONOMIQUE);
        Transaction standard = new BitcoinTransaction("from2", "to2", 1.0, FeePriority.STANDARD);
        Transaction rapide = new BitcoinTransaction("from3", "to3", 1.0, FeePriority.RAPIDE);
        
        // Act
        double feeEconomique = bitcoinFeeStrategy.calculateFees(economique);
        double feeStandard = bitcoinFeeStrategy.calculateFees(standard);
        double feeRapide = bitcoinFeeStrategy.calculateFees(rapide);
        
        // Assert
        Assert.assertTrue("Economique fee should be less than or equal to Standard", 
                        feeEconomique <= feeStandard);
        Assert.assertTrue("Standard fee should be less than or equal to Rapide", 
                        feeStandard <= feeRapide);
    }

    @Test
    public void testEthereumConfirmationTime_PriorityOrder() {
        // Arrange
        Transaction economique = new EthereumTransaction("from1", "to1", 1.0, FeePriority.ECONOMIQUE);
        Transaction standard = new EthereumTransaction("from2", "to2", 1.0, FeePriority.STANDARD);
        Transaction rapide = new EthereumTransaction("from3", "to3", 1.0, FeePriority.RAPIDE);
        
        // Act
        int timeEconomique = ethereumFeeStrategy.estimateConfirmationTime(economique);
        int timeStandard = ethereumFeeStrategy.estimateConfirmationTime(standard);
        int timeRapide = ethereumFeeStrategy.estimateConfirmationTime(rapide);
        
        // Assert
        Assert.assertTrue("Economique time should be greater than Standard", 
                        timeEconomique > timeStandard);
        Assert.assertTrue("Standard time should be greater than Rapide", 
                        timeStandard > timeRapide);
        Assert.assertTrue("Rapide should be the fastest", 
                        timeRapide < timeStandard && timeRapide < timeEconomique);
    }

    @Test
    public void testBitcoinConfirmationTime_PriorityOrder() {
        // Arrange
        Transaction economique = new BitcoinTransaction("from1", "to1", 1.0, FeePriority.ECONOMIQUE);
        Transaction standard = new BitcoinTransaction("from2", "to2", 1.0, FeePriority.STANDARD);
        Transaction rapide = new BitcoinTransaction("from3", "to3", 1.0, FeePriority.RAPIDE);
        
        // Act
        int timeEconomique = bitcoinFeeStrategy.estimateConfirmationTime(economique);
        int timeStandard = bitcoinFeeStrategy.estimateConfirmationTime(standard);
        int timeRapide = bitcoinFeeStrategy.estimateConfirmationTime(rapide);
        
        // Assert
        Assert.assertTrue("Economique time should be greater than Standard", 
                        timeEconomique > timeStandard);
        Assert.assertTrue("Standard time should be greater than Rapide", 
                        timeStandard > timeRapide);
        Assert.assertTrue("Rapide should be the fastest", 
                        timeRapide < timeStandard && timeRapide < timeEconomique);
    }
}
