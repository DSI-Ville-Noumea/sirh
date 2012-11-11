package nc.mairie.spring.dao.mapper.metier.referentiel;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class TypeRepresentantRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		TypeRepresentantResultSetExtractor extractor = new TypeRepresentantResultSetExtractor();
		return extractor.extractData(rs);
	}

}
