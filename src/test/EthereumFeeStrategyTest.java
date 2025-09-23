package test;

import static org.junit.Assert.*;
import strategy.*;
import model.BitcoinTransaction;
import model.Transaction;
import org.junit.Test;

public class EthereumFeeStrategyTest {

	@Test
	public void testCalculateFee(){
		EthereumFeeStrategy calculator = new EthereumFeeStrategy();
		BitcoinTransaction obj = new BitcoinTransaction();
        double fee = calculator.calculateFees(obj);

         
        double expected = 21000 * 50 * 1e-9; 
        assertEquals(expected, fee, 0.00000001); 
	}
	

}
