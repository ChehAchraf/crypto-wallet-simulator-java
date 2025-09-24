package model;

import util.Identifiable;

public abstract class Wallet implements Identifiable {
	private int id ;
    private String walletAdresse ;
	private double amount;
    private String type;
	
	public Wallet() {
		
	}

    public void setWalletAdresse(String walletAdresse) {
        this.walletAdresse = walletAdresse;
    }
	public Wallet(double amount,String type) {
        this.walletAdresse = generateAdresse();
		this.amount = amount;
        this.type = type;
	}

    public abstract String getType();
    public abstract String generateAdresse();



    public String getAddress() {
        return walletAdresse;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }


    public double getAmount() {
        return amount;
    }

    @Override
    public int getId() {
        return id;
    }


}
