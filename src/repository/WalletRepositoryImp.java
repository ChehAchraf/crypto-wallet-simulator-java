package repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import model.Wallet;

public class WalletRepositoryImp implements Repository {


    @Override
    public void save(Object entity) {

    }

    @Override
    public Optional findbyCode(String code) {
        return Optional.empty();
    }

    @Override
    public List findAll() {
        return Collections.emptyList();
    }

    @Override
    public void update(Object entity) {

    }

    @Override
    public void delete(Object entity) {

    }
}
