package nc.mairie.spring.dao.mapper.metier.specificites;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class PrimePointageRowMapper implements RowMapper<Object> {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		PrimePointageResultSetExtractor extractor = new PrimePointageResultSetExtractor();
		return extractor.extractData(rs);
	}

}
