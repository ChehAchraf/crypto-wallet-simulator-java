package test;

import static org.junit.Assert.*;
import strategy.*;
import model.Transaction;
import org.junit.Test;

public class EthereumFeeStrategyTest {

	@Test
	public void testCalculateFee(){
		EthereumFeeStrategy calculator = new EthereumFeeStrategy();
		Transaction obj = new Transaction();
        double fee = calculator.calculateFees();

         
        double expected = 21000 * 50 * 1e-9; 
        assertEquals(expected, fee, 0.00000001); 
	}
	

}
