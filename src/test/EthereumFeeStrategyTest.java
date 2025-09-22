package test;

import static org.junit.Assert.*;
import strategy.*;
import org.junit.Test;

public class EthereumFeeStrategyTest {

	@Test
	public void testCalculateFee(){
		EthereumFeeStrategy calculator = new EthereumFeeStrategy();
        double fee = calculator.calculateFees();

         
        double expected = 21000 * 50 * 1e-9; 
        assertEquals(expected, fee, 0.00000001); 
	}
	

}
