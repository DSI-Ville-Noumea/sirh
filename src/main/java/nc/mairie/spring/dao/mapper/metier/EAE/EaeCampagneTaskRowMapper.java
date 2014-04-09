package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class EaeCampagneTaskRowMapper implements RowMapper<Object> {
	
	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		EaeCampagneTaskResultSetExtractor extractor = new EaeCampagneTaskResultSetExtractor();
		return extractor.extractData(rs);
	}
}