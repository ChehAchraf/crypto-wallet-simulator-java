package model;
import enums.*;
import strategy.FeeCalculationStrategy;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Transaction {
    private int id;
    private String fromAddress;
    private String toAddress;
    private double amount;
    private LocalDateTime createdAt;
    private double fees;
    private FeePriority feePriority;
    private TransactionStatus status;
    private CryptoType cryptoType;
    private FeeCalculationStrategy feeStrategy;

    public Transaction(String from, String to, double amount, FeePriority feePriority, FeeCalculationStrategy strategy) {
        this.fromAddress = from;
        this.toAddress = to;
        this.amount = amount;
        this.feePriority = feePriority;
        this.feeStrategy = strategy;
        this.status = TransactionStatus.PENDING;
        this.fees = feeStrategy.calculateFees(this);
    }
    
  
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
