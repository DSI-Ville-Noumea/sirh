package nc.mairie.spring.dao.mapper.metier.suiviMedical;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class MotifVisiteMedRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		MotifVisiteMedResultSetExtractor extractor = new MotifVisiteMedResultSetExtractor();
		return extractor.extractData(rs);
	}

}
