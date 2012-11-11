package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class EaeTypeObjectifRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		EaeTypeObjectifResultSetExtractor extractor = new EaeTypeObjectifResultSetExtractor();
		return extractor.extractData(rs);
	}

}
