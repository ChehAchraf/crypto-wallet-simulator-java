package service;

import model.Transaction;
import repository.TransactionRepository;

public class TransactionProcessingService {
    private final TransactionRepository transactionRepository;
    private final TransactionValidationService validationService;
    private final BalanceService balanceService;
    
    public TransactionProcessingService(TransactionRepository transactionRepository, 
                                      TransactionValidationService validationService,
                                      BalanceService balanceService) {
        this.transactionRepository = transactionRepository;
        this.validationService = validationService;
        this.balanceService = balanceService;
    }
    
    public TransactionResult processTransaction(Transaction transaction) {
        // Validate the transaction
        TransactionValidationService.ValidationResult validation = validationService.validateTransaction(
            transaction.getFromAddress(), 
            transaction.getToAddress(), 
            transaction.getAmount()
        );
        
        if (!validation.isValid()) {
            return TransactionResult.error(validation.getErrorMessage());
        }
        
        // Create transaction in database
        try {
            transactionRepository.save(transaction);
        } catch (Exception e) {
            return TransactionResult.error("Failed to create transaction in database");
        }
        
        // Update balances
        if (!balanceService.updateBalances(
            transaction.getFromAddress(), 
            transaction.getToAddress(), 
            transaction.getAmount()
        )) {
            return TransactionResult.error("Failed to update wallet balances");
        }
        
        return TransactionResult.success(transaction);
    }
    
    public static class TransactionResult {
        private final boolean isSuccess;
        private final String errorMessage;
        private final Transaction transaction;
        
        private TransactionResult(boolean isSuccess, String errorMessage, Transaction transaction) {
            this.isSuccess = isSuccess;
            this.errorMessage = errorMessage;
            this.transaction = transaction;
        }
        
        public static TransactionResult success(Transaction transaction) {
            return new TransactionResult(true, null, transaction);
        }
        
        public static TransactionResult error(String errorMessage) {
            return new TransactionResult(false, errorMessage, null);
        }
        
        public boolean isSuccess() {
            return isSuccess;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public Transaction getTransaction() {
            return transaction;
        }
    }
}
