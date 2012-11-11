package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class CampagneEAERowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		CampagneEAEResultSetExtractor extractor = new CampagneEAEResultSetExtractor();
		return extractor.extractData(rs);
	}

}
