package service;
import model.Wallet;
import repository.WalletRepository;
import java.util.List;

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
    

}
