package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class EaeTypeDeveloppementRowMapper implements RowMapper<Object> {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		EaeTypeDeveloppementResultSetExtractor extractor = new EaeTypeDeveloppementResultSetExtractor();
		return extractor.extractData(rs);
	}

}
