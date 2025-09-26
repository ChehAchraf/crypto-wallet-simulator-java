package repository;

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


}
