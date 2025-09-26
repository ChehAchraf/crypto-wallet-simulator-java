package repository;

import db.DBConnection;
import enums.CryptoType;
import enums.FeePriority;
import enums.TransactionStatus;
import model.BitcoinTransaction;
import model.EthereumTransaction;
import model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionRepository extends JdbcRepository<Transaction> {

    public TransactionRepository(DBConnection connection) {
        super(connection);
    }

    @Override
    protected String getTableName() {
        return "transactions";
    }

    @Override
    protected Transaction mapToEntity(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String fromAddress = rs.getString("from_address");
        String toAddress = rs.getString("to_address");
        double amount = rs.getDouble("amount");
        double fees = rs.getDouble("fees");
        FeePriority feePriority = FeePriority.valueOf(rs.getString("fee_priority"));
        TransactionStatus status = TransactionStatus.valueOf(rs.getString("status"));
        CryptoType cryptoType = CryptoType.valueOf(rs.getString("crypto_type"));

        // Create appropriate transaction object
        Transaction transaction;
        if (cryptoType == CryptoType.BITCOIN) {
            transaction = new BitcoinTransaction(fromAddress, toAddress, amount, feePriority);
        } else if (cryptoType == CryptoType.ETHEREUM) {
            transaction = new EthereumTransaction(fromAddress, toAddress, amount, feePriority);
        } else {
            throw new IllegalArgumentException("Unknown crypto type: " + cryptoType);
        }

        // Set additional properties
        transaction.setId(id);
        transaction.setFees(fees);
        transaction.setStatus(status);
        transaction.setCryptoType(cryptoType);

        return transaction;
    }

    @Override
    protected void setInsertParams(PreparedStatement stmt, Transaction entity) throws SQLException {
        stmt.setString(1, entity.getFromAddress());
        stmt.setString(2, entity.getToAddress());
        stmt.setDouble(3, entity.getAmount());
        stmt.setDouble(4, entity.getFees());
        stmt.setString(5, entity.getFeePriority().toString());
        stmt.setString(6, entity.getStatus().toString());
        stmt.setString(7, entity.getCryptoType().toString());
    }

    @Override
    protected void setUpdateParams(PreparedStatement stmt, Transaction entity) throws SQLException {
        stmt.setString(1, "CONFIRMED");
        stmt.setInt(2, entity.getId());
    }

    @Override
    protected String getUpdateQuery() {
        return "UPDATE transactions SET status = ? WHERE id = ?";
    }

    @Override
    protected String getInsertQuery() {
        return "INSERT INTO transactions(from_address,to_address, amount,fees,fee_priority,status,crypto_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    public int getLastInsertedId() throws SQLException {
        String sql = "SELECT LASTVAL()";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }
}  