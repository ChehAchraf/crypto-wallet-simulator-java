package repository;
import db.DBConnection;
import model.Wallet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;

public abstract class JdbcRepository<T> implements Repository<T>{
	protected final Logger logger = Logger.getLogger(this.getClass().getName());
    protected final DBConnection connection;

    public JdbcRepository(DBConnection connection) {
        this.connection = connection;
    }

    protected abstract String getTableName();
    protected abstract T mapToEntity(ResultSet rs) throws SQLException;
    protected abstract void setInsertParams(PreparedStatement stmt, T entity) throws SQLException;
    protected abstract void setUpdateParams(PreparedStatement stmt, T entity) throws SQLException;
    protected abstract String getUpdateQuery();
    protected abstract String getInsertQuery();
	
	@Override
	public void save(T entity) {
		String sql = getInsertQuery();
        try(Connection conn = connection.getConnection();PreparedStatement stmt = conn.prepareStatement(sql)) {
            setInsertParams(stmt, entity);
            stmt.executeUpdate();
        }catch(SQLException e){
            logger.log(Level.SEVERE, "Error saving entity in " + getTableName(), e);
        }
	}

	@Override
	public Optional<T> findbyCode(String code) {
		String sql = "select * from " + getTableName() + " where code = ?";
        try(Connection conn = connection.getConnection();PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, code);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return Optional.of(mapToEntity(rs));
            }

        }catch(SQLException e){
            logger.log(Level.SEVERE, "Error Finding entity by id " + getTableName(), e);
        }
        return Optional.empty();
	}

	@Override
	public List<T> findAll() {
		List<T> list = new ArrayList<>();
		String sql = "select * from " + getTableName();
        try(Connection conn = connection.getConnection();PreparedStatement stmt = conn.prepareStatement(sql)) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                list.add(mapToEntity(rs));
            }
        }catch(SQLException e){
            logger.log(Level.SEVERE, "Error Finding all entitie data in " + getTableName(), e);
        }
        return list;
	}

	@Override
	public void update(T entity) {
        String sql = getUpdateQuery();
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setUpdateParams(stmt, entity);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating entity in " + getTableName(), e);
        }
	}

	@Override
	public void delete(T entity) {
        String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, entity);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting entity in " + getTableName(), e);
        }
	}

}
