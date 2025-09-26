package model;

import enums.CryptoType;
import enums.FeePriority;
import strategy.BitcoinFeeStrategy;

public class BitcoinTransaction extends Transaction {

    public BitcoinTransaction(String from, String to, double amount, FeePriority feePriority) {
        super(from, to, amount, feePriority, new BitcoinFeeStrategy());
        this.setCryptoType(CryptoType.BITCOIN);
    }



}
