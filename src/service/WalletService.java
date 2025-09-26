package service;
import model.Wallet;
import repository.WalletRepository;
import java.util.List;
import java.util.Optional;

public class WalletService {
    private final WalletRepository walletRepo;


    public WalletService(WalletRepository walletRepo) {
        this.walletRepo = walletRepo;
    }

    public void createWallet(Wallet wallet) {
        walletRepo.save(wallet);
    }
    
    public List<Wallet> getAllWallets(){
    		return this.walletRepo.findAll();
    }

    public Optional<Wallet> findByAddress(String address) {
        return walletRepo.findByAddress(address);
    }

    public Optional<Double> getBalance(String address) {
        return walletRepo.getBalance(address);
    }
    
    public void updateBalance(String address, double newBalance) {
        walletRepo.updateBalance(address, newBalance);
    }

}
