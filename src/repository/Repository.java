package repository;
import java.util.List;
import java.util.Optional;
import model.Wallet;

public interface Repository<T> {
	void save (T entity);
	Optional<T> findbyCode(String code);
	List<T> findAll();
	void update (T entity);
    void delete (T entity);
}
