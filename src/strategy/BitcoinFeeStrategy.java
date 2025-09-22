package strategy;
import model.Transaction;
public class BitcoinFeeStrategy implements FeeCalculationStrategy {
    private static final int AVERAGE_BLOCK_TIME_SEC = 10 * 60;
    private static final int ESTIMATED_TX_SIZE_BYTES = 250;
    private static final double SATOSHI_PER_BYTE = 50;

    @Override
    public double calculateFees(Transaction ts) {
        return ts.getAmount() * 0.001 ;
    }

    @Override
    public int estimateConfirmationTime(Transaction tx) {
        return 0;
    }
}
