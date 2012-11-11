package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class EaeFichePosteRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		EaeFichePosteResultSetExtractor extractor = new EaeFichePosteResultSetExtractor();
		return extractor.extractData(rs);
	}

}
