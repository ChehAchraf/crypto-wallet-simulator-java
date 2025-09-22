package strategy;
import model.Transaction;
import enums.*;
public class BitcoinFeeStrategy implements FeeCalculationStrategy {
    private static final int AVERAGE_BLOCK_TIME_SEC = 10 * 60;
    private static final int ESTIMATED_TX_SIZE_BYTES = 250;
    private static final double SATOSHI_PER_BYTE = 50;

    @Override
    public double calculateFees(Transaction ts) {
        return ESTIMATED_TX_SIZE_BYTES *  SATOSHI_PER_BYTE * 1e-8 ;
    }

    @Override
    public int estimateConfirmationTime(Transaction tx) {
        switch(tx.getFeePriority()){
            case ECONOMIQUE: return 3 * AVERAGE_BLOCK_TIME_SEC;
            case STANDARD:   return 2 * AVERAGE_BLOCK_TIME_SEC;
            case RAPIDE:     return 1 * AVERAGE_BLOCK_TIME_SEC;
            default: return 2 * AVERAGE_BLOCK_TIME_SEC;
        }
    }
}
