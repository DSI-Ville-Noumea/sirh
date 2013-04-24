package nc.mairie.spring.dao.mapper.metier.avancement;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class AvancementCapPrintJobRowMapper implements RowMapper<Object> {

	@Override
	public Object mapRow(ResultSet rs, int line) throws SQLException {
		AvancementCapPrintJobResultSetExtractor extractor = new AvancementCapPrintJobResultSetExtractor();
		return extractor.extractData(rs);
	}

}
