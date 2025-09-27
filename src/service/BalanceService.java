package service;

import java.util.Optional;

public class BalanceService {
    private final WalletService walletService;
    
    public BalanceService(WalletService walletService) {
        this.walletService = walletService;
    }
    
    public boolean updateBalances(String sourceAddress, String destinationAddress, double amount, double fees) {
        try {
            Optional<Double> sourceBalance = walletService.getBalance(sourceAddress);
            Optional<Double> destBalance = walletService.getBalance(destinationAddress);
            
            if (!sourceBalance.isPresent() || !destBalance.isPresent()) {
                return false;
            }
            
            walletService.updateBalance(sourceAddress, sourceBalance.get() - amount - fees);
            walletService.updateBalance(destinationAddress, destBalance.get() + amount);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getBalanceInfo(String address) {
        Optional<Double> balance = walletService.getBalance(address);
        if (balance.isPresent()) {
            return "Balance: " + balance.get();
        }
        return "Could not retrieve balance";
    }
}
