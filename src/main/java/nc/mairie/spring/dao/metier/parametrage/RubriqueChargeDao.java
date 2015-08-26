package nc.mairie.spring.dao.metier.parametrage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.RubriqueCharge;
import nc.mairie.spring.dao.utils.SirhDao;

public class RubriqueChargeDao extends SirhDao implements RubriqueChargeDaoInterface {

	public static final String CHAMP_SHOW_CODE_CHARGE = "SHOW_CODE_CHARGE";
	public static final String CHAMP_SHOW_CREANCIER = "SHOW_CREANCIER";
	public static final String CHAMP_SHOW_MATRICULE_CHARGE = "SHOW_MATRICULE_CHARGE";
	public static final String CHAMP_SHOW_MONTANT = "SHOW_MONTANT";
	public static final String CHAMP_SHOW_DONNEES_MUTU = "SHOW_DONNEES_MUTU";

	public RubriqueChargeDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_RUBRIQUE_CHARGE";
		super.CHAMP_ID = "NORUBR";
	}

	@Override
	public List<RubriqueCharge> getListRubriqueCharge() {
		ArrayList<RubriqueCharge> liste = new ArrayList<RubriqueCharge>();
		String sql = "SELECT * from " + NOM_TABLE + " order by " + CHAMP_ID;

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			RubriqueCharge a = mapEntite(row);
			liste.add(a);
		}

		return liste;
	}

	private RubriqueCharge mapEntite(Map<String, Object> row) {
		RubriqueCharge a = new RubriqueCharge();
		BigDecimal id = (BigDecimal) row.get(CHAMP_ID);
		a.setNorubr(id.intValue());
		a.setShowCodeCharge(((Integer) row.get(CHAMP_SHOW_CODE_CHARGE)) == 1 ? true : false);
		a.setShowCreancier(((Integer) row.get(CHAMP_SHOW_CREANCIER)) == 1 ? true : false);
		a.setShowMatriculeCharge(((Integer) row.get(CHAMP_SHOW_MATRICULE_CHARGE)) == 1 ? true : false);
		a.setShowMontant(((Integer) row.get(CHAMP_SHOW_MONTANT)) == 1 ? true : false);
		a.setShowDonneesMutu(((Integer) row.get(CHAMP_SHOW_DONNEES_MUTU)) == 1 ? true : false);
		return a;
	}

	@Override
	public void creerRubriqueCharge(RubriqueCharge rubriqueChargeCourant) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID + "," + CHAMP_SHOW_CODE_CHARGE + ","
				+ CHAMP_SHOW_CREANCIER + "," + CHAMP_SHOW_MATRICULE_CHARGE + "," + CHAMP_SHOW_MONTANT + ","
				+ CHAMP_SHOW_DONNEES_MUTU + ") VALUES (?,?,?,?,?,?)";
		jdbcTemplate.update(sql,
				new Object[] { rubriqueChargeCourant.getNorubr(), rubriqueChargeCourant.isShowCodeCharge(),
						rubriqueChargeCourant.isShowCreancier(), rubriqueChargeCourant.isShowMatriculeCharge(),
						rubriqueChargeCourant.isShowMontant(), rubriqueChargeCourant.isShowDonneesMutu() });

	}

	@Override
	public void modifierRubriqueCharge(RubriqueCharge rubriqueChargeCourant) {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_SHOW_CODE_CHARGE + "=? ," + CHAMP_SHOW_CREANCIER + "=? ,"
				+ CHAMP_SHOW_MATRICULE_CHARGE + "=? ," + CHAMP_SHOW_MONTANT + "=? ," + CHAMP_SHOW_DONNEES_MUTU
				+ "=?  where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql,
				new Object[] { rubriqueChargeCourant.isShowCodeCharge(), rubriqueChargeCourant.isShowCreancier(),
						rubriqueChargeCourant.isShowMatriculeCharge(), rubriqueChargeCourant.isShowMontant(),
						rubriqueChargeCourant.isShowDonneesMutu(), rubriqueChargeCourant.getNorubr() });
	}

	@Override
	public void supprimerRubriqueCharge(RubriqueCharge rubriqueChargeCourant) throws Exception {

		super.supprimerObject(rubriqueChargeCourant.getNorubr());

	}
}
