package test;

import model.BitcoinWallet;
import model.Wallet;
import org.junit.Before;
import org.junit.Test;
import repository.WalletRepository;
import service.WalletService;
import static org.mockito.Mockito.*;

public class WalletServiceTest {
    private WalletRepository walletRepo;
    private WalletService walletService;

    @Before
    public void setUp() {
        walletRepo = mock(WalletRepository.class);
        walletService = new WalletService(walletRepo);
    }
    @Test
    public void createWallet() {
        Wallet btcWallet = new BitcoinWallet(12);
        walletService.createWallet(btcWallet);

        verify(walletRepo, times(1)).save(btcWallet);
    }
}
