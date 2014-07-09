package nc.mairie.spring.dao.metier.suiviMedical;

import java.util.List;

import nc.mairie.metier.suiviMedical.MotifVisiteMed;
import nc.mairie.spring.dao.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class MotifVisiteMedDao extends SirhDao implements MotifVisiteMedDaoInterface {

	public static final String CHAMP_LIB_MOTIF_VM = "LIB_MOTIF_VM";

	public MotifVisiteMedDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "R_MOTIF_VM";
		super.CHAMP_ID = "ID_MOTIF_VM";
	}

	@Override
	public MotifVisiteMed chercherMotifByLib(String lib) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_LIB_MOTIF_VM + " = ? ";

		MotifVisiteMed motifVM = (MotifVisiteMed) jdbcTemplate.queryForObject(sql, new Object[] { lib },
				new BeanPropertyRowMapper<MotifVisiteMed>(MotifVisiteMed.class));

		return motifVM;
	}

	@Override
	public MotifVisiteMed chercherMotif(Integer idMotif) throws Exception {
		return super.chercherObject(MotifVisiteMed.class, idMotif);
	}

	@Override
	public List<MotifVisiteMed> listerMotifVisiteMed() throws Exception {
		return super.getListe(MotifVisiteMed.class);
	}
}
