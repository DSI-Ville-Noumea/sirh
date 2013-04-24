package nc.mairie.spring.dao.mapper.metier.diplome;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class FormationAgentRowMapper implements RowMapper<Object> {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		FormationAgentResultSetExtractor extractor = new FormationAgentResultSetExtractor();
		return extractor.extractData(rs);
	}

}
