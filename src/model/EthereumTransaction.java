package model;

import enums.CryptoType;
import enums.FeePriority;
import strategy.EthereumFeeStrategy;

public class EthereumTransaction extends Transaction {

    public EthereumTransaction(String from, String to, double amount, FeePriority feePriority) {
        super(from, to, amount, feePriority, new EthereumFeeStrategy());
        this.setCryptoType(CryptoType.ETHEREUM);
    }

}
