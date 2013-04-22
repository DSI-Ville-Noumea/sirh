package nc.mairie.spring.dao.mapper.metier.specificites;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class PrimePointageAffRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		PrimePointageAffResultSetExtractor extractor = new PrimePointageAffResultSetExtractor();
		return extractor.extractData(rs);
	}

}
