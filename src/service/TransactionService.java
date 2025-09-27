package service;
import model.Transaction;
import repository.TransactionRepository;

import java.util.List;


public class TransactionService {
    private final TransactionRepository transactionRepo;

    public TransactionService(TransactionRepository transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    public boolean  creatTransaction(Transaction transaction) {
        try{
            transactionRepo.save(transaction);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public List<Transaction> getTransactions() {
        return transactionRepo.findAll();
    }

    public boolean updateTransaction(Transaction transaction) {
        try{
            transactionRepo.update(transaction);
            return true;
        }catch(Exception e){
            return false;
        }
    }


    
    
}
