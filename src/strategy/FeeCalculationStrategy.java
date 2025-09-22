package strategy;
import model.Transaction;

public interface FeeCalculationStrategy {
    double calculateFees();
    int estimateConfirmationTime(Transaction tx);
}
