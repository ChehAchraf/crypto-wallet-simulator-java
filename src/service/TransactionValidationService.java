package service;

import java.util.Optional;

public class TransactionValidationService {
    private final WalletService walletService;
    
    public TransactionValidationService(WalletService walletService) {
        this.walletService = walletService;
    }
    
    public ValidationResult validateTransaction(String sourceAddress, String destinationAddress, double amount) {
        // Check if source wallet exists
        if (!walletService.findByAddress(sourceAddress).isPresent()) {
            return ValidationResult.error("Source wallet address not found in database.");
        }
        
        // Check if destination wallet exists
        if (!walletService.findByAddress(destinationAddress).isPresent()) {
            return ValidationResult.error("Destination wallet address not found in database.");
        }
        
        // Check if user is trying to send to themselves
        if (sourceAddress.equals(destinationAddress)) {
            return ValidationResult.error("You can't send to yourself");
        }
        
        // Check if amount is valid
        if (amount <= 0) {
            return ValidationResult.error("Amount must be greater than 0");
        }
        
        // Check if source wallet has sufficient balance
        Optional<Double> balance = walletService.getBalance(sourceAddress);
        if (!balance.isPresent()) {
            return ValidationResult.error("Could not retrieve balance for source wallet.");
        }
        
        if (balance.get() < amount) {
            return ValidationResult.error("You don't have enough balance to send this amount. Your balance: " + balance.get());
        }
        
        return ValidationResult.success();
    }
    
    public static class ValidationResult {
        private final boolean isValid;
        private final String errorMessage;
        
        private ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult error(String errorMessage) {
            return new ValidationResult(false, errorMessage);
        }
        
        public boolean isValid() {
            return isValid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
