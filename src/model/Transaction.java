package model;
import enums.*;
import strategy.FeeCalculationStrategy;

import java.time.LocalDateTime;

public abstract class Transaction {
    private static int counter = 0;
    protected String id;
    protected String fromAddress;
    protected String toAddress;
    protected double amount;
    protected LocalDateTime createdAt;
    protected double fees;
    protected FeePriority feePriority;
    protected TransactionStatus status;
    protected CryptoType cryptoType;
    protected FeeCalculationStrategy feeStrategy;

    public Transaction(String from, String to, double amount, FeePriority feePriority, FeeCalculationStrategy strategy) {
        this.fromAddress = from;
        this.toAddress = to;
        this.amount = amount;
        this.feePriority = feePriority;
        this.feeStrategy = strategy;
        this.status = TransactionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.fees = feeStrategy.calculateFees(this);
        this.id = generateTransactionId();
    }

    protected abstract String generateTransactionId();
}
