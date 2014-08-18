package nc.mairie.spring.dao.metier.agent;

import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.metier.agent.HistoPrime;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.technique.UserAppli;

public class HistoPrimeDao extends SirhDao implements HistoPrimeDaoInterface {

	public static final String CHAMP_NO_MATRICULE = "NO_MATRICULE";
	public static final String CHAMP_NO_RUBRIQUE = "NO_RUBRIQUE";
	public static final String CHAMP_REF_ARRETE = "REF_ARRETE";
	public static final String CHAMP_DATE_DEBUT = "DATE_DEBUT";
	public static final String CHAMP_DATE_FIN = "DATE_FIN";
	public static final String CHAMP_DATE_ARRETE = "DATE_ARRETE";
	public static final String CHAMP_MONTANT = "MONTANT";
	public static final String CHAMP_TYPE_HISTO = "TYPE_HISTO";
	public static final String CHAMP_USER_HISTO = "USER_HISTO";

	public HistoPrimeDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "HISTO_PRIME";
	}

	private void creerHistoPrimeBD(HistoPrime histo) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_NO_MATRICULE + "," + CHAMP_NO_RUBRIQUE + ","
				+ CHAMP_REF_ARRETE + "," + CHAMP_DATE_DEBUT + "," + CHAMP_DATE_FIN + "," + CHAMP_DATE_ARRETE + ","
				+ CHAMP_MONTANT + "," + CHAMP_TYPE_HISTO + "," + CHAMP_USER_HISTO + ") " + "VALUES (?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(
				sql,
				new Object[] { histo.getNoMatricule(), histo.getNoRubrique(), histo.getRefArrete(),
						histo.getDateDebut(), histo.getDateFin(), histo.getDateArrete(), histo.getMontant(),
						histo.getTypeHisto(), histo.getUserHisto() });
	}

	@Override
	public void creerHistoPrime(HistoPrime histo, UserAppli user, EnumTypeHisto typeHisto) throws Exception {
		histo.setUserHisto(user.getUserName());
		histo.setTypeHisto(typeHisto.getValue());

		// Creation du HistoPrime
		creerHistoPrimeBD(histo);
	}
}
