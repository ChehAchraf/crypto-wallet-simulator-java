package model;

import enums.FeePriority;
import strategy.FeeCalculationStrategy;

public class BitcoinTransaction extends Transaction {
	
	public BitcoinTransaction() {
		
	}
	
	public BitcoinTransaction(String from, String to, double amount, FeePriority feePriority,FeeCalculationStrategy strategy) {
		super(from, to, amount, feePriority, strategy);
		
	}
	
	@Override
	protected String generateTransactionId() {
		return null;
	}

}
