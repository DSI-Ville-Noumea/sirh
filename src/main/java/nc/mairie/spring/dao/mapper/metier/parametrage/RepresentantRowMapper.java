package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class RepresentantRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		RepresentantResultSetExtractor extractor = new RepresentantResultSetExtractor();
		return extractor.extractData(rs);
	}

}
