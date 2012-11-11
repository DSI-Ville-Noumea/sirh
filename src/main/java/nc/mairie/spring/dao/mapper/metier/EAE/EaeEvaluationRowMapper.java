package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class EaeEvaluationRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		EaeEvaluationResultSetExtractor extractor = new EaeEvaluationResultSetExtractor();
		return extractor.extractData(rs);
	}

}
