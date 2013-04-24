package nc.mairie.spring.dao.mapper.metier.specificites;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class PrimePointageFPRowMapper implements RowMapper<Object> {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		PrimePointageFPResultSetExtractor extractor = new PrimePointageFPResultSetExtractor();
		return extractor.extractData(rs);
	}

}
