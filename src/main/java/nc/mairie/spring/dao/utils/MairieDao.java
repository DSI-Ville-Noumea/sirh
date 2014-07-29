package nc.mairie.spring.dao.utils;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class MairieDao implements IMairieDao{

	public String NOM_TABLE = null;
	public String CHAMP_ID = null;

	protected JdbcTemplate jdbcTemplate;
	protected DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void supprimerObject(Integer id) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID + "=? ";
		jdbcTemplate.update(sql, new Object[] { id });
	}

	@Override
	public <T> List<T> getListe(Class<T> T) throws Exception {

		String sql = "select * from " + NOM_TABLE;
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<T> rows = jdbcTemplate.query(sql, new BeanPropertyRowMapper(T));

		return rows;
	}

	@Override
	public <T> T chercherObject(Class<T> T, Integer id) throws Exception {

		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID + " =? ";
		@SuppressWarnings({ "unchecked", "rawtypes" })
		T rows = (T) jdbcTemplate.queryForObject(sql, new Object[] { id }, new BeanPropertyRowMapper(T));

		return rows;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

}
