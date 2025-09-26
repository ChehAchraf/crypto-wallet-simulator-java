package repository;
import db.DBConnection;

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
        try(Connection conn = connection.getConnection();PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setInsertParams(stmt, entity);
            stmt.executeUpdate();
            
            // Get the generated ID and set it to the entity
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    // Try to set the ID if the entity has a setId method
                    try {
                        entity.getClass().getMethod("setId", int.class).invoke(entity, generatedId);
                    } catch (Exception e) {
                        // Entity doesn't have setId method, that's okay
                    }
                }
            }
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
            ResultSet rs = stmt.executeQuery();
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
