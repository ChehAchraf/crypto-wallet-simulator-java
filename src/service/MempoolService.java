package service;

import model.Transaction;
import enums.TransactionStatus;
import repository.TransactionRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

public class MempoolService {
    private final TransactionService transactionService;
    private final PriorityBlockingQueue<Transaction> mempool;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public List<Transaction> getMempool() {
        return new ArrayList<>(mempool);
    }


    public MempoolService(TransactionRepository transactionRepository) {
        this.transactionService = new TransactionService(transactionRepository);
        this.mempool = new PriorityBlockingQueue<>(
                100, 
                Comparator.comparing(Transaction::getFeePriority)
                        .reversed()
                        .thenComparing(Transaction::getFees).reversed()
        );
    }

    public boolean addTransaction(Transaction ts) {
        try {
            if (ts == null) return false;
            ts.setStatus(TransactionStatus.PENDING);
            mempool.add(ts);
            
            System.out.println("Transaction added to mempool with ID: " + ts.getId());

            int delay = ts.getFeeStrategy().estimateConfirmationTime(ts);
            scheduler.schedule(() -> confirmTransaction(ts), delay, TimeUnit.SECONDS);

            return true;
        } catch (Exception e) {
            System.out.println("Error adding transaction to mempool: " + e.getMessage());
            return false;
        }
    }

    private boolean confirmTransaction(Transaction tx) {
        try{
            if (mempool.remove(tx)) {
                tx.setStatus(TransactionStatus.CONFIRMED);
                transactionService.updateTransaction(tx);
                System.out.println("Transaction " + tx.getId() + " confirmed with fees " + tx.getFees());
                return true;
            }
        }catch(Exception e){
            mempool.add(tx);
            return false;
        }
        return false;
    }

    public Transaction pollNext() {
        return mempool.poll();
    }
}
