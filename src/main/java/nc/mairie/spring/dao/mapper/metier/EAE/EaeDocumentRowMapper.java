package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class EaeDocumentRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		EaeDocumentResultSetExtractor extractor = new EaeDocumentResultSetExtractor();
		return extractor.extractData(rs);
	}

}
