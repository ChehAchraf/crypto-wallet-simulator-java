package strategy;
import model.Transaction;


import static enums.FeePriority.*;

public class EthereumFeeStrategy implements FeeCalculationStrategy{
    private static final int AVERAGE_BLOCK_TIME_SEC = 13;
    private static final int DEFAULT_GAS_LIMIT = 21000;
    private static final double DEFAULT_GAS_PRICE_GWEI = 50;

    @Override
    public double calculateFees(Transaction ts) {
        double gasPriceEth = DEFAULT_GAS_PRICE_GWEI * 1e-9;
        return DEFAULT_GAS_LIMIT * gasPriceEth;
    }

    @Override
    public int estimateConfirmationTime(Transaction ts) {
        switch (ts.getFeePriority()) {
            case ECONOMIQUE: return 5 * AVERAGE_BLOCK_TIME_SEC; // tx lente
            case STANDARD:   return 2 * AVERAGE_BLOCK_TIME_SEC; // tx normale
            case RAPIDE:     return 1 * AVERAGE_BLOCK_TIME_SEC; // tx rapide
            default: return 2 * AVERAGE_BLOCK_TIME_SEC;
        }
    }
}
