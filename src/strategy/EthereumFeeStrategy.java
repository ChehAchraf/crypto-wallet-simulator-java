package strategy;

import model.Transaction;
import enums.*;

public class EthereumFeeStrategy implements FeeCalculationStrategy {
    private static final int AVERAGE_BLOCK_TIME_SEC = 13;
    private static final int DEFAULT_GAS_LIMIT = 21000;
    private static final double DEFAULT_GAS_PRICE_GWEI = 50;

    @Override
    public double calculateFees(Transaction ts) {
        double baseFee = DEFAULT_GAS_LIMIT * (DEFAULT_GAS_PRICE_GWEI * 1e-9);
        switch (ts.getFeePriority()) {
            case ECONOMIQUE: return baseFee * 0.8;
            case STANDARD:   return baseFee;
            case RAPIDE:     return baseFee * 1.2;
            default: return baseFee;
        }
    }

    @Override
    public int estimateConfirmationTime(Transaction ts) {
        switch (ts.getFeePriority()) {
            case ECONOMIQUE: return 240;
            case STANDARD:   return 120;
            case RAPIDE:     return 30;
            default: return 2 * AVERAGE_BLOCK_TIME_SEC;
        }
    }
}
