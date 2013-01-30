package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class RepresentantCapRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		RepresentantCapResultSetExtractor extractor = new RepresentantCapResultSetExtractor();
		return extractor.extractData(rs);
	}

}
