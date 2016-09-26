package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data access class for the PublicId object. This data structure maps a public
 * identifier to an object class.
 * 
 * @author evaldes
 *
 */
public class PublicIdDAO extends mx.nic.rdap.core.db.PublicId implements DatabaseObject {

	public PublicIdDAO() {
		super();
	}

	public PublicIdDAO(ResultSet resultSet) throws SQLException {
		super();
		try {
			this.loadFromDatabase(resultSet);
		} catch (SQLException e) {
			// TODO Manage exception
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mx.nic.rdap.server.db.DatabaseObject#loadFromDatabase(java.sql.ResultSet)
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		this.setId(resultSet.getLong("pid_id"));
		this.setType(resultSet.getString("pid_type"));
		this.setPublicId(resultSet.getString("pid_identifier"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setString(1, this.getType());
		preparedStatement.setString(2, this.getPublicId());
	}

}