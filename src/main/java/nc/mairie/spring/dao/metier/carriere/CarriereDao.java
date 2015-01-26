package nc.mairie.spring.dao.metier.carriere;

import java.util.List;
import java.util.Map;

import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.spring.dao.utils.MairieDao;
import nc.mairie.technique.Services;

public class CarriereDao extends MairieDao implements CarriereDaoInterface {

	public static final String CHAMP_CODE_BASE = "CDBASE";
	
	public CarriereDao(MairieDao mairieDao) {
		super.dataSource = mairieDao.getDataSource();
		super.jdbcTemplate = mairieDao.getJdbcTemplate();
		super.NOM_TABLE = "SPCARR";
	}
	
	public Carriere chercherCarriereEnCoursAvecAgent(Agent agent) throws Exception{
		
		
		String req = "select c.* from " + NOM_TABLE + " c where ("
				+ Services.formateDateInternationale(Services.dateDuJour()).replace("-", Const.CHAINE_VIDE)
				+ " between c.DATDEB and c.DATFIN or c.DATDEB<="
				+ Services.formateDateInternationale(Services.dateDuJour()).replace("-", Const.CHAINE_VIDE)
				+ " and c.DATFIN=0) and c.nomatr =? WITH UR ";
		
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(req, new Object[] { agent.getNomatr() });
		
		return mapCarriere(rows);
	}
	
	private Carriere mapCarriere(List<Map<String, Object>> rows) {
		
		for (Map<String, Object> row : rows) {
			Carriere carr = new Carriere();
			
			carr.setCodeBase((String) row.get(CHAMP_CODE_BASE));

			return carr;
		}
		
		return null;
	}
}
