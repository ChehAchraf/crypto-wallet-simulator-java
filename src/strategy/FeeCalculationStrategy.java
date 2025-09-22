package strategy;
import model.Transaction;

public interface FeeCalculationStrategy {
    double calculateFees(Transaction ts);
    int estimateConfirmationTime(Transaction ts);
}
