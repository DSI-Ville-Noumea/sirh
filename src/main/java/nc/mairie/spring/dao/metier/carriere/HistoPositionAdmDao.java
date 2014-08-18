package nc.mairie.spring.dao.metier.carriere;

import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.metier.carriere.HistoPositionAdm;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.technique.UserAppli;

public class HistoPositionAdmDao extends SirhDao implements HistoPositionAdmDaoInterface {

	public static final String CHAMP_NO_MATRICULE = "NO_MATRICULE";
	public static final String CHAMP_DATE_DEBUT = "DATE_DEBUT";
	public static final String CHAMP_CODE_POSA = "CODE_POSA";
	public static final String CHAMP_DATE_FIN = "DATE_FIN";
	public static final String CHAMP_REF_ARR = "REF_ARR";
	public static final String CHAMP_TYPE_HISTO = "TYPE_HISTO";
	public static final String CHAMP_USER_HISTO = "USER_HISTO";

	public HistoPositionAdmDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "HISTO_POSITION_ADMINISTRATIVE";
	}

	private void creerHistoPositionAdmBD(HistoPositionAdm histo) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_NO_MATRICULE + "," + CHAMP_DATE_DEBUT + ","
				+ CHAMP_CODE_POSA + "," + CHAMP_DATE_FIN + "," + CHAMP_REF_ARR + "," + CHAMP_TYPE_HISTO + ","
				+ CHAMP_USER_HISTO + ") " + "VALUES (?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { histo.getNoMatricule(), histo.getDateDebut(), histo.getCodePosa(),
				histo.getDateFin(), histo.getRefArr(), histo.getTypeHisto(), histo.getUserHisto() });
	}

	@Override
	public void creerHistoPositionAdm(HistoPositionAdm histo, UserAppli user, EnumTypeHisto typeHisto) throws Exception {
		histo.setUserHisto(user.getUserName());
		histo.setTypeHisto(typeHisto.getValue());

		// Creation du HistoPositionAdm
		creerHistoPositionAdmBD(histo);
	}
}
