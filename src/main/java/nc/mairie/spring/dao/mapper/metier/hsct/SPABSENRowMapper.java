package nc.mairie.spring.dao.mapper.metier.hsct;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class SPABSENRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		SPABSENResultSetExtractor extractor = new SPABSENResultSetExtractor();
		return extractor.extractData(rs);
	}

}
