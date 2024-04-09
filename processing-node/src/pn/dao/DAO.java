package pn.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DAO<T> {
	Optional<T> get(long id);
	List<T> getAll();
	void save(T t);
	void update(T t, String[] params);
	void delete(T t);
	/**
	 * Returns an area set by username from database<br>
	 * @param username
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	List<Integer> getAreasByUsername(String username) throws ClassNotFoundException, SQLException;
}
