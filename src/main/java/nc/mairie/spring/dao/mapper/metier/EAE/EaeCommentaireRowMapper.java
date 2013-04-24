package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class EaeCommentaireRowMapper implements RowMapper<Object> {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		EaeCommentaireResultSetExtractor extractor = new EaeCommentaireResultSetExtractor();
		return extractor.extractData(rs);
	}

}
