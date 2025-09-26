package repository;

import java.util.Optional;
import db.DBConnection;
import enums.CryptoType;
import model.BitcoinWallet;
import model.EthereumWallet;
import model.Wallet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WalletRepository extends JdbcRepository<Wallet>{
    public WalletRepository(DBConnection connection) {
        super(connection);
    }

    @Override
    protected String getTableName() {
        return "wallets";
    }

    @Override
    protected Wallet mapToEntity(ResultSet rs) throws SQLException {
        CryptoType type = CryptoType.valueOf(rs.getString("type"));
        double amount = rs.getDouble("amount");

        Wallet wallet;
        switch(type){
            case BITCOIN:
                wallet = new BitcoinWallet(amount);
                break;
            case ETHEREUM:
                wallet = new EthereumWallet(amount);
                break;
            default:
                throw new IllegalArgumentException("Unknown wallet type: " + type);
        }
        wallet.setId(rs.getInt("id"));
        return wallet;
    }

    @Override
    protected void setInsertParams(PreparedStatement stmt, Wallet wallet) throws SQLException {
        stmt.setString(1, wallet.getAddress());  
        stmt.setDouble(2, wallet.getAmount());   
        stmt.setString(3, wallet.getType());
    }

    @Override
    protected void setUpdateParams(PreparedStatement stmt, Wallet wallet) throws SQLException {
        stmt.setString(1, wallet.getAddress());  
        stmt.setDouble(2, wallet.getAmount());   
        stmt.setString(3, wallet.getType());

    }


    @Override
    protected String getUpdateQuery() {
        return "UPDATE wallets SET amount = ?, type = ? WHERE id = ?";
    }



    @Override
    protected String getInsertQuery() {
        return "INSERT INTO wallets(address,amount, type) VALUES (?, ?, ?)";
    }

    public Optional<Wallet> findByAddress(String address) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE address = ?";
        try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, address);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapToEntity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Double> getBalance(String address) {
        String sql = "SELECT amount FROM " + getTableName() + " WHERE address = ?";
        try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, address);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                    return Optional.of(rs.getDouble("amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    public void updateBalance(String address, double newBalance) {
        String sql = "UPDATE " + getTableName() + " SET amount = ? WHERE address = ?";
        try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql)) {
            stmt.setDouble(1, newBalance);
            stmt.setString(2, address);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
