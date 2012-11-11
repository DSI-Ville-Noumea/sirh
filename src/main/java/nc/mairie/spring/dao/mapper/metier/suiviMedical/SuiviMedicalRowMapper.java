package nc.mairie.spring.dao.mapper.metier.suiviMedical;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class SuiviMedicalRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		SuiviMedicalResultSetExtractor extractor = new SuiviMedicalResultSetExtractor();
		return extractor.extractData(rs);
	}

}
