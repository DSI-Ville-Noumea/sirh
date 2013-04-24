package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class CampagneActeurRowMapper implements RowMapper<Object> {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		CampagneActeurResultSetExtractor extractor = new CampagneActeurResultSetExtractor();
		return extractor.extractData(rs);
	}

}
