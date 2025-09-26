    package strategy;
    import model.Transaction;
    import enums.*;
    public class BitcoinFeeStrategy implements FeeCalculationStrategy {
        private static final int AVERAGE_BLOCK_TIME_SEC = 10 * 60;
        private static final int ESTIMATED_TX_SIZE_BYTES = 250;
        private static final double SATOSHI_PER_BYTE = 50;

        @Override
        public double calculateFees(Transaction ts) {
            double baseFee = ESTIMATED_TX_SIZE_BYTES * SATOSHI_PER_BYTE * 1e-8;
            switch(ts.getFeePriority()) {
                case ECONOMIQUE: return baseFee * 0.8;
                case STANDARD:   return baseFee;
                case RAPIDE:     return baseFee * 1;
                default: return baseFee;
            }
        }

        @Override
        public int estimateConfirmationTime(Transaction tx) {
            switch(tx.getFeePriority()){
                case ECONOMIQUE: return 240;
                case STANDARD:   return 120;
                case RAPIDE:     return 30;
                default: return 2 * AVERAGE_BLOCK_TIME_SEC;
            }
        }
    }
