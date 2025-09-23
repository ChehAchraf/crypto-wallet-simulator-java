package model;

public abstract class Wallet {
	private int counter = 1;
	private String id ;
	private double amount;
	
	public Wallet() {
		
	}
	
	public Wallet(double amount) {
		this.id = genereateWalletId();
		this.amount = amount;
	}
	
	public abstract String genereateWalletId();
	
}
