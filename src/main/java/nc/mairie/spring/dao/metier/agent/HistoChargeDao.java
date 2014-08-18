package nc.mairie.spring.dao.metier.agent;

import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.metier.agent.HistoCharge;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.technique.UserAppli;

public class HistoChargeDao extends SirhDao implements HistoChargeDaoInterface {

	public static final String CHAMP_NO_MATRICULE = "NO_MATRICULE";
	public static final String CHAMP_NO_RUBRIQUE = "NO_RUBRIQUE";
	public static final String CHAMP_CODE_CREANCIER = "CODE_CREANCIER";
	public static final String CHAMP_NO_MATE = "NO_MATE";
	public static final String CHAMP_CODE_CHARGE = "CODE_CHARGE";
	public static final String CHAMP_TAUX = "TAUX";
	public static final String CHAMP_MONTANT = "MONTANT";
	public static final String CHAMP_DATE_FIN = "DATE_FIN";
	public static final String CHAMP_DATE_DEBUT = "DATE_DEBUT";
	public static final String CHAMP_TYPE_HISTO = "TYPE_HISTO";
	public static final String CHAMP_USER_HISTO = "USER_HISTO";

	public HistoChargeDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "HISTO_CHARGE";
	}

	private void creerHistoChargeBD(HistoCharge histo) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_NO_MATRICULE + "," + CHAMP_NO_RUBRIQUE + ","
				+ CHAMP_CODE_CREANCIER + "," + CHAMP_NO_MATE + "," + CHAMP_CODE_CHARGE + "," + CHAMP_TAUX + ","
				+ CHAMP_MONTANT + "," + CHAMP_DATE_FIN + "," + CHAMP_DATE_DEBUT + "," + CHAMP_TYPE_HISTO + ","
				+ CHAMP_USER_HISTO + ") " + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(
				sql,
				new Object[] { histo.getNoMatricule(), histo.getNoRubrique(), histo.getCodeCreancier(),
						histo.getNoMate(), histo.getCodeCharge(), histo.getTaux(), histo.getMontant(),
						histo.getDateFin(), histo.getDateDebut(), histo.getTypeHisto(), histo.getUserHisto() });
	}

	@Override
	public void creerHistoCharge(HistoCharge histo, UserAppli user, EnumTypeHisto typeHisto) throws Exception {
		histo.setUserHisto(user.getUserName());
		histo.setTypeHisto(typeHisto.getValue());

		// Creation du HistoCarriere
		creerHistoChargeBD(histo);
	}
}
