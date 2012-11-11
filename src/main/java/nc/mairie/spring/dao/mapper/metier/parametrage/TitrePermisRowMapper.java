package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class TitrePermisRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		TitrePermisResultSetExtractor extractor = new TitrePermisResultSetExtractor();
		return extractor.extractData(rs);
	}

}
