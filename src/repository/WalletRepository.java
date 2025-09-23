package repository;
import java.util.List;

import model.Wallet;

public interface WalletRepository {
	void save (Wallet wallet);
	Wallet findbyCode(String code);
	List<Wallet> findAll();
	void delete(String code);
}
