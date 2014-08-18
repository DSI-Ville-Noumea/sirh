package nc.mairie.spring.dao.metier.carriere;

import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.metier.carriere.HistoCarriere;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.technique.UserAppli;

public class HistoCarriereDao extends SirhDao implements HistoCarriereDaoInterface {

	public static final String CHAMP_NO_MATRICULE = "NO_MATRICULE";
	public static final String CHAMP_CODE_CATEGORIE = "CODE_CATEGORIE";
	public static final String CHAMP_CODE_GRADE = "CODE_GRADE";
	public static final String CHAMP_REF_ARRETE = "REF_ARRETE";
	public static final String CHAMP_DATE_DEBUT = "DATE_DEBUT";
	public static final String CHAMP_DATE_FIN = "DATE_FIN";
	public static final String CHAMP_MODE_REG = "MODE_REG";
	public static final String CHAMP_MONTANT_FORFAIT = "MONTANT_FORFAIT";
	public static final String CHAMP_CODE_EMPLOI = "CODE_EMPLOI";
	public static final String CHAMP_CODE_BASE = "CODE_BASE";
	public static final String CHAMP_CODE_TYPE_EMPLOI = "CODE_TYPE_EMPLOI";
	public static final String CHAMP_CODE_BASE_HOR2 = "CODE_BASE_HOR2";
	public static final String CHAMP_IBAN = "IBAN";
	public static final String CHAMP_CODE_MOTIF_PROMO = "CODE_MOTIF_PROMO";
	public static final String CHAMP_ACC_JOUR = "ACC_JOUR";
	public static final String CHAMP_ACC_MOIS = "ACC_MOIS";
	public static final String CHAMP_ACC_ANNEE = "ACC_ANNEE";
	public static final String CHAMP_BM_JOUR = "BM_JOUR";
	public static final String CHAMP_BM_MOIS = "BM_MOIS";
	public static final String CHAMP_BM_ANNEE = "BM_ANNEE";
	public static final String CHAMP_DATE_ARRETE = "DATE_ARRETE";
	public static final String CHAMP_CDDCDICA = "CDDCDICA";
	public static final String CHAMP_TYPE_HISTO = "TYPE_HISTO";
	public static final String CHAMP_USER_HISTO = "USER_HISTO";

	public HistoCarriereDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "HISTO_CARRIERE";
	}

	@Override
	public void creerHistoCarriere(HistoCarriere histo, UserAppli user, EnumTypeHisto typeHisto) throws Exception {
		histo.setUserHisto(user.getUserName());
		histo.setTypeHisto(typeHisto.getValue());

		// Creation du HistoCarriere
		creerHistoCarriereBD(histo);
	}

	private void creerHistoCarriereBD(HistoCarriere histo) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_NO_MATRICULE + "," + CHAMP_CODE_CATEGORIE + ","
				+ CHAMP_CODE_GRADE + "," + CHAMP_REF_ARRETE + "," + CHAMP_DATE_DEBUT + "," + CHAMP_DATE_FIN + ","
				+ CHAMP_MODE_REG + "," + CHAMP_MONTANT_FORFAIT + "," + CHAMP_CODE_EMPLOI + "," + CHAMP_CODE_BASE + ","
				+ CHAMP_CODE_TYPE_EMPLOI + "," + CHAMP_CODE_BASE_HOR2 + "," + CHAMP_IBAN + "," + CHAMP_CODE_MOTIF_PROMO
				+ "," + CHAMP_ACC_JOUR + "," + CHAMP_ACC_MOIS + "," + CHAMP_ACC_ANNEE + "," + CHAMP_BM_JOUR + ","
				+ CHAMP_BM_MOIS + "," + CHAMP_BM_ANNEE + "," + CHAMP_DATE_ARRETE + "," + CHAMP_CDDCDICA + ","
				+ CHAMP_TYPE_HISTO + "," + CHAMP_USER_HISTO + ") "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(
				sql,
				new Object[] { histo.getNoMatricule(), histo.getCodeCategorie(), histo.getCodeGrade(),
						histo.getRefArrete(), histo.getDateDebut(), histo.getDateFin(), histo.getModeReg(),
						histo.getMontantForfait(), histo.getCodeEmploi(), histo.getCodeBase(),
						histo.getCodeTypeEmploi(), histo.getCodeBaseHor2(), histo.getIban(), histo.getCodeMotifPromo(),
						histo.getAccJour(), histo.getAccMois(), histo.getAccAnnee(), histo.getBmJour(),
						histo.getBmMois(), histo.getBmAnnee(), histo.getDateArrete(), histo.getCddcdica(),
						histo.getTypeHisto(), histo.getUserHisto() });
	}
}
