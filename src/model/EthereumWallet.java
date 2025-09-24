package model;

import java.security.SecureRandom;

public class EthereumWallet extends Wallet{

    private static final String HEX_CHARS = "0123456789abcdef";
    private static final SecureRandom random = new SecureRandom();

    public EthereumWallet(double amount) {
        super(amount,"ETHEREUM");
        super.setWalletAdresse(generateAdresse());
    }

    @Override
    public String getType() {
        return "ETHEREUM";
    }



    @Override
    public String generateAdresse() {
        StringBuilder  adresse = new StringBuilder("0x"); 
        for(int i = 0 ; i < 40 ; i++) {
        		int index = random.nextInt(HEX_CHARS.length());
        		adresse.append(HEX_CHARS.charAt(index));
        }
        return adresse.toString();
    }
}
