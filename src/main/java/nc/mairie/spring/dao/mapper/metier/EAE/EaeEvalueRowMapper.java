package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class EaeEvalueRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		EaeEvalueResultSetExtractor extractor = new EaeEvalueResultSetExtractor();
		return extractor.extractData(rs);
	}

}
