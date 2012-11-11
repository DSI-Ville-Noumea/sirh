package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class CentreFormationRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		CentreFormationResultSetExtractor extractor = new CentreFormationResultSetExtractor();
		return extractor.extractData(rs);
	}

}
