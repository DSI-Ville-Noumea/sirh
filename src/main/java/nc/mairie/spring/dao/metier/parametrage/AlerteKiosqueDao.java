package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.AlerteKiosque;
import nc.mairie.spring.dao.utils.SirhDao;

public class AlerteKiosqueDao extends SirhDao implements AlerteKiosqueDaoInterface {

	public static final String CHAMP_TEXTE_ALERTE_KIOSQUE = "TEXTE_ALERTE_KIOSQUE";
	public static final String CHAMP_TITRE = "TITRE";
	public static final String CHAMP_DATE_DEBUT = "DATE_DEBUT";
	public static final String CHAMP_DATE_FIN = "DATE_FIN";
	public static final String CHAMP_AGENT = "AGENT";
	public static final String CHAMP_APPRO_ABS = "APPRO_ABS";
	public static final String CHAMP_APPRO_PTG = "APPRO_PTG";
	public static final String CHAMP_OPE_ABS = "OPE_ABS";
	public static final String CHAMP_OPE_PTG = "OPE_PTG";
	public static final String CHAMP_VISEUR_ABS = "VISEUR_ABS";

	public AlerteKiosqueDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_ALERTE_KIOSQUE";
		super.CHAMP_ID = "ID_ALERTE_KIOSQUE";
	}

	@Override
	public List<AlerteKiosque> getAlerteKiosque() throws Exception {
		ArrayList<AlerteKiosque> liste = new ArrayList<AlerteKiosque>();
		String sql = "SELECT * from " + NOM_TABLE;

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			AlerteKiosque a = new AlerteKiosque();
			a.setIdAlerteKiosque((Integer) row.get(CHAMP_ID));
			a.setTitre((String) row.get(CHAMP_TITRE));
			a.setTexteAlerteKiosque((String) row.get(CHAMP_TEXTE_ALERTE_KIOSQUE));
			a.setAgent(((Integer) row.get(CHAMP_AGENT)) == 1 ? true : false);
			a.setApprobateurABS(((Integer) row.get(CHAMP_APPRO_ABS)) == 1 ? true : false);
			a.setApprobateurPTG(((Integer) row.get(CHAMP_APPRO_PTG)) == 1 ? true : false);
			a.setOperateurABS(((Integer) row.get(CHAMP_OPE_ABS)) == 1 ? true : false);
			a.setOperateurPTG(((Integer) row.get(CHAMP_OPE_PTG)) == 1 ? true : false);
			a.setViseurABS(((Integer) row.get(CHAMP_VISEUR_ABS)) == 1 ? true : false);
			a.setDateDebut((Date) row.get(CHAMP_DATE_DEBUT));
			a.setDateFin((Date) row.get(CHAMP_DATE_FIN));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public void creerAlerteKiosque(AlerteKiosque alerte) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_TITRE + "," + CHAMP_TEXTE_ALERTE_KIOSQUE + ","
				+ CHAMP_DATE_DEBUT + "," + CHAMP_DATE_FIN + "," + CHAMP_AGENT + "," + CHAMP_APPRO_ABS + ","
				+ CHAMP_APPRO_PTG + "," + CHAMP_OPE_ABS + "," + CHAMP_OPE_PTG + "," + CHAMP_VISEUR_ABS + ") "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(
				sql,
				new Object[] { alerte.getTitre(), alerte.getTexteAlerteKiosque(), alerte.getDateDebut(),
						alerte.getDateFin(), alerte.isAgent(), alerte.isApprobateurABS(), alerte.isApprobateurPTG(),
						alerte.isOperateurABS(), alerte.isOperateurPTG(), alerte.isViseurABS() });
	}

	@Override
	public void modifierAlerteKiosque(Integer idAlerte, AlerteKiosque alerte) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_TITRE + "=?," + CHAMP_TEXTE_ALERTE_KIOSQUE + "=?,"
				+ CHAMP_DATE_DEBUT + "=?," + CHAMP_DATE_FIN + "=?," + CHAMP_AGENT + "=?," + CHAMP_APPRO_ABS + "=?,"
				+ CHAMP_APPRO_PTG + "=?," + CHAMP_OPE_ABS + "=?," + CHAMP_OPE_PTG + "=?," + CHAMP_VISEUR_ABS
				+ "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(
				sql,
				new Object[] { alerte.getTitre(), alerte.getTexteAlerteKiosque(), alerte.getDateDebut(),
						alerte.getDateFin(), alerte.isAgent(), alerte.isApprobateurABS(), alerte.isApprobateurPTG(),
						alerte.isOperateurABS(), alerte.isOperateurPTG(), alerte.isViseurABS(), idAlerte });
	}
}
