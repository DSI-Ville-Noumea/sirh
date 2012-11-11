package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class EaeFDPActiviteRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		EaeFDPActiviteResultSetExtractor extractor = new EaeFDPActiviteResultSetExtractor();
		return extractor.extractData(rs);
	}

}
