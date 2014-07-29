package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.eae.EaeParcoursPro;
import nc.mairie.spring.dao.utils.EaeDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EaeParcoursProDao extends EaeDao implements EaeParcoursProDaoInterface {

	private Logger logger = LoggerFactory.getLogger(EaeParcoursProDao.class);

	public static final String CHAMP_ID_EAE = "ID_EAE";
	public static final String CHAMP_DATE_DEBUT = "DATE_DEBUT";
	public static final String CHAMP_DATE_FIN = "DATE_FIN";
	public static final String CHAMP_LIBELLE_PARCOURS_PRO = "LIBELLE_PARCOURS_PRO";

	public EaeParcoursProDao(EaeDao eaeDao) {
		super.dataSource = eaeDao.getDataSource();
		super.jdbcTemplate = eaeDao.getJdbcTemplate();
		super.NOM_TABLE = "EAE_PARCOURS_PRO";
		super.CHAMP_ID = "ID_EAE_PARCOURS_PRO";
	}

	@Override
	public void creerParcoursPro(Integer idEae, Date dateDebut, Date dateFin, String libParcours) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_EAE + "," + CHAMP_DATE_DEBUT + "," + CHAMP_DATE_FIN
				+ "," + CHAMP_LIBELLE_PARCOURS_PRO + ") " + "VALUES (?,?,?,?)";
		try {
			jdbcTemplate.update(sql, new Object[] { idEae, dateDebut, dateFin, libParcours });
		} catch (Exception e) {
			logger.debug("Erreur dans la creation du parcours pro pour l' idEae=" + idEae);
		}
	}

	@Override
	public ArrayList<EaeParcoursPro> listerEaeParcoursPro(Integer idEAE) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_EAE + "=?";

		ArrayList<EaeParcoursPro> listeEaeParcoursPro = new ArrayList<EaeParcoursPro>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEAE });
		for (Map<String, Object> row : rows) {
			EaeParcoursPro parc = new EaeParcoursPro();
			parc.setIdEaeParcoursPro((Integer) row.get(CHAMP_ID));
			parc.setIdEAE((Integer) row.get(CHAMP_ID_EAE));
			parc.setDateDebut((Date) row.get(CHAMP_DATE_DEBUT));
			parc.setDateFin((Date) row.get(CHAMP_DATE_FIN));
			parc.setLibelleParcoursPro((String) row.get(CHAMP_LIBELLE_PARCOURS_PRO));

			listeEaeParcoursPro.add(parc);
		}
		return listeEaeParcoursPro;
	}

	@Override
	public void supprimerEaeParcoursPro(Integer idParcoursPro) throws Exception {
		super.supprimerObject(idParcoursPro);
	}
}
