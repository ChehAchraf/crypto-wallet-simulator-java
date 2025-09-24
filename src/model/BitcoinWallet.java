package model;

import java.security.SecureRandom;

public class BitcoinWallet extends Wallet{


    private static final String HEX_CHARS = "0123456789abcdef";
    private static final SecureRandom random = new SecureRandom();

    public BitcoinWallet(double amount) {
        super(amount, "BITCOIN");
        super.setWalletAdresse(generateAdresse());
    }

    @Override
    public String getType() {
        return "BITCOIN";
    }

    

    @Override
    public String generateAdresse() {
        StringBuilder  adresse = new StringBuilder("bc1");
        for(int i = 0 ; i < 40 ; i++){
            int index = random.nextInt(HEX_CHARS.length());
            adresse.append(HEX_CHARS.charAt(index));
        }
        return adresse.toString();
    }


}
