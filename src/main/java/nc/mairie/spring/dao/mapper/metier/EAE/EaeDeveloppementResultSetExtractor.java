package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaeEvolutionDao;
import nc.mairie.spring.domain.metier.EAE.EaeEvolution;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaeDeveloppementResultSetExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		

		return null;
	}
}
