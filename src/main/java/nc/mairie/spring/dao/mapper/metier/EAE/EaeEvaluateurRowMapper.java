package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class EaeEvaluateurRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		EaeEvaluateurResultSetExtractor extractor = new EaeEvaluateurResultSetExtractor();
		return extractor.extractData(rs);
	}

}
