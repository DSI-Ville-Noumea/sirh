package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.hsct.BeneficiaireObligationAmenage;
import nc.mairie.spring.dao.utils.SirhDao;

public class BeneficiaireObligationAmenageDao extends SirhDao implements BeneficiaireObligationAmenageDaoInterface {

	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_TYPE = "TYPE";
	public static final String CHAMP_DATE_DEBUT = "DATE_DEBUT";
	public static final String CHAMP_DATE_ATTRIBUTION = "DATE_ATTRIBUTION";
	public static final String CHAMP_DATE_FIN = "DATE_FIN";
	public static final String CHAMP_ID_NATURE_POSTE_AMENAGE = "ID_NATURE_POSTE_AMENAGE";
	public static final String CHAMP_NATURE_HANDICAP = "NATURE_HANDICAP";
	public static final String CHAMP_TAUX = "TAUX";
	public static final String CHAMP_ORIGINE_IPP = "ORIGINE_IPP";

	public BeneficiaireObligationAmenageDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "BENEFICIAIRE_OBLIGATION_EMPLOI";
		super.CHAMP_ID = "ID_BOE";
	}

	@Override
	public void creerBeneficiaireObligationAmenage(BeneficiaireObligationAmenage boe) throws Exception {

		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_AGENT + "," + CHAMP_TYPE + "," + CHAMP_DATE_DEBUT + ","
				+ CHAMP_DATE_ATTRIBUTION + "," + CHAMP_DATE_FIN + "," + CHAMP_ID_NATURE_POSTE_AMENAGE + ","
				+ CHAMP_NATURE_HANDICAP + "," + CHAMP_TAUX + "," + CHAMP_ORIGINE_IPP + ") " + "VALUES (?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { boe.getIdAgent() , boe.getType(), boe.getDateDebut(), boe.getDateAttribution(), boe.getDateFin(),
				boe.getIdNaturePosteAmenage(), boe.getNatureHandicap(), boe.getTaux(), boe.getOrigineIpp() });
	}

	@Override
	public void modifierBeneficiaireObligationAmenage(BeneficiaireObligationAmenage boe) throws Exception {

		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_AGENT + "=?," + CHAMP_TYPE + "=?," + CHAMP_DATE_DEBUT + "=?,"
				+ CHAMP_DATE_ATTRIBUTION + "=?," + CHAMP_DATE_FIN + "=?," + CHAMP_ID_NATURE_POSTE_AMENAGE + "=?,"
				+ CHAMP_NATURE_HANDICAP + "=?," + CHAMP_TAUX + "=?," + CHAMP_ORIGINE_IPP + "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { boe.getIdAgent(), boe.getType(), boe.getDateDebut(), boe.getDateAttribution(), boe.getDateFin(),
				boe.getIdNaturePosteAmenage(), boe.getNatureHandicap(), boe.getTaux(), boe.getOrigineIpp(), boe.getIdBoe() });
	}

	@Override
	public void supprimerBeneficiaireObligationAmenage(Integer idBoe) throws Exception {
		super.supprimerObject(idBoe);
	}

	@Override
	public BeneficiaireObligationAmenage chercherBeneficiaireObligationAmenage(Integer idBoe) throws Exception {
		return super.chercherObject(BeneficiaireObligationAmenage.class, idBoe);
	}

	@Override
	public ArrayList<BeneficiaireObligationAmenage> listerBeneficiaireObligationAmenageByAgent(Integer idAgent) throws Exception {
		
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? order by "
				+ CHAMP_DATE_DEBUT + " desc";

		ArrayList<BeneficiaireObligationAmenage> liste = new ArrayList<BeneficiaireObligationAmenage>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent });
		for (Map<String, Object> row : rows) {
			
			BeneficiaireObligationAmenage a = new BeneficiaireObligationAmenage();
			a.setIdBoe((Integer) row.get(CHAMP_ID));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setType((String) row.get(CHAMP_TYPE));
			a.setDateDebut((Date) row.get(CHAMP_DATE_DEBUT));
			a.setDateAttribution((Date) row.get(CHAMP_DATE_ATTRIBUTION));
			a.setDateFin((Date) row.get(CHAMP_DATE_FIN));
			a.setIdNaturePosteAmenage((Integer) row.get(CHAMP_ID_NATURE_POSTE_AMENAGE));
			a.setNatureHandicap((String) row.get(CHAMP_NATURE_HANDICAP));
			a.setTaux((Integer) row.get(CHAMP_TAUX));
			a.setOrigineIpp((String) row.get(CHAMP_ORIGINE_IPP));

			liste.add(a);
		}

		return liste;
	}

}
