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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public double getFees() {
        return fees;
    }

    public void setFees(double fees) {
        this.fees = fees;
    }

    public FeePriority getFeePriority() {
        return feePriority;
    }

    public void setFeePriority(FeePriority feePriority) {
        this.feePriority = feePriority;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public CryptoType getCryptoType() {
        return cryptoType;
    }

    public void setCryptoType(CryptoType cryptoType) {
        this.cryptoType = cryptoType;
    }

    public FeeCalculationStrategy getFeeStrategy() {
        return feeStrategy;
    }

    public void setFeeStrategy(FeeCalculationStrategy feeStrategy) {
        this.feeStrategy = feeStrategy;
    }

}
