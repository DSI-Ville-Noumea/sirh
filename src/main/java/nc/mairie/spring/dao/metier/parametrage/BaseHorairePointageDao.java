package nc.mairie.spring.dao.metier.parametrage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.BaseHorairePointage;
import nc.mairie.spring.dao.utils.SirhDao;

public class BaseHorairePointageDao extends SirhDao implements BaseHorairePointageDaoInterface {

	public static final String CHAMP_CODE_BASE_HORAIRE_POINTAGE = "CODE_BASE_HORAIRE_POINTAGE";
	public static final String CHAMP_LIBELLE_BASE_HORAIRE_POINTAGE = "LIBELLE_BASE_HORAIRE_POINTAGE";
	public static final String CHAMP_DESCRIPTION_BASE_HORAIRE_POINTAGE = "DESCRIPTION_BASE_HORAIRE_POINTAGE";
	public static final String CHAMP_HEURE_LUNDI = "HEURE_LUNDI";
	public static final String CHAMP_HEURE_MARDI = "HEURE_MARDI";
	public static final String CHAMP_HEURE_MERCREDI = "HEURE_MERCREDI";
	public static final String CHAMP_HEURE_JEUDI = "HEURE_JEUDI";
	public static final String CHAMP_HEURE_VENDREDI = "HEURE_VENDREDI";
	public static final String CHAMP_HEURE_SAMEDI = "HEURE_SAMEDI";
	public static final String CHAMP_HEURE_DIMANCHE = "HEURE_DIMANCHE";
	public static final String CHAMP_BASE_LEGALE = "BASE_LEGALE";
	public static final String CHAMP_BASE_CALCULEE = "BASE_CALCULEE";

	public BaseHorairePointageDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_BASE_HORAIRE_POINTAGE";
		super.CHAMP_ID = "ID_BASE_HORAIRE_POINTAGE";
	}

	@Override
	public List<BaseHorairePointage> listerBaseHorairePointageOrderByCode() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_CODE_BASE_HORAIRE_POINTAGE + " desc ";

		ArrayList<BaseHorairePointage> listeFam = new ArrayList<BaseHorairePointage>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			BaseHorairePointage fam = new BaseHorairePointage();
			fam.setIdBaseHorairePointage((Integer) row.get(CHAMP_ID));
			fam.setCodeBaseHorairePointage((String) row.get(CHAMP_CODE_BASE_HORAIRE_POINTAGE));
			fam.setLibelleBaseHorairePointage((String) row.get(CHAMP_LIBELLE_BASE_HORAIRE_POINTAGE));
			fam.setDescriptionBaseHorairePointage((String) row.get(CHAMP_DESCRIPTION_BASE_HORAIRE_POINTAGE));
			BigDecimal lundi = (BigDecimal) row.get(CHAMP_HEURE_LUNDI);
			fam.setHeureLundi(lundi.doubleValue());
			BigDecimal mardi = (BigDecimal) row.get(CHAMP_HEURE_MARDI);
			fam.setHeureMardi(mardi.doubleValue());
			BigDecimal mercredi = (BigDecimal) row.get(CHAMP_HEURE_MERCREDI);
			fam.setHeureMercredi(mercredi.doubleValue());
			BigDecimal jeudi = (BigDecimal) row.get(CHAMP_HEURE_JEUDI);
			fam.setHeureJeudi(jeudi.doubleValue());
			BigDecimal vendredi = (BigDecimal) row.get(CHAMP_HEURE_VENDREDI);
			fam.setHeureVendredi(vendredi.doubleValue());
			BigDecimal samedi = (BigDecimal) row.get(CHAMP_HEURE_SAMEDI);
			fam.setHeureSamedi(samedi.doubleValue());
			BigDecimal dimanche = (BigDecimal) row.get(CHAMP_HEURE_DIMANCHE);
			fam.setHeureDimanche(dimanche.doubleValue());
			BigDecimal legale = (BigDecimal) row.get(CHAMP_BASE_LEGALE);
			fam.setBaseLegale(legale.doubleValue());
			BigDecimal calcul = (BigDecimal) row.get(CHAMP_HEURE_MARDI);
			fam.setBaseCalculee(calcul.doubleValue());
			listeFam.add(fam);
		}

		return listeFam;
	}

	@Override
	public void creerBaseHorairePointage(BaseHorairePointage base) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_CODE_BASE_HORAIRE_POINTAGE + ","
				+ CHAMP_LIBELLE_BASE_HORAIRE_POINTAGE + "," + CHAMP_DESCRIPTION_BASE_HORAIRE_POINTAGE + ","
				+ CHAMP_HEURE_LUNDI + "," + CHAMP_HEURE_MARDI + "," + CHAMP_HEURE_MERCREDI + "," + CHAMP_HEURE_JEUDI
				+ "," + CHAMP_HEURE_VENDREDI + "," + CHAMP_HEURE_SAMEDI + "," + CHAMP_HEURE_DIMANCHE + ","
				+ CHAMP_BASE_LEGALE + "," + CHAMP_BASE_CALCULEE + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(
				sql,
				new Object[] { base.getCodeBaseHorairePointage().toUpperCase(),
						base.getLibelleBaseHorairePointage().toUpperCase(), base.getDescriptionBaseHorairePointage(),
						base.getHeureLundi(), base.getHeureMardi(), base.getHeureMercredi(), base.getHeureJeudi(),
						base.getHeureVendredi(), base.getHeureSamedi(), base.getHeureDimanche(), base.getBaseLegale(),
						base.getBaseCalculee() });
	}

	@Override
	public void modifierBaseHorairePointage(BaseHorairePointage base) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_CODE_BASE_HORAIRE_POINTAGE + "=?,"
				+ CHAMP_LIBELLE_BASE_HORAIRE_POINTAGE + "=?," + CHAMP_DESCRIPTION_BASE_HORAIRE_POINTAGE + "=?,"
				+ CHAMP_HEURE_LUNDI + "=?," + CHAMP_HEURE_MARDI + "=?," + CHAMP_HEURE_MERCREDI + "=?,"
				+ CHAMP_HEURE_JEUDI + "=?," + CHAMP_HEURE_VENDREDI + "=?," + CHAMP_HEURE_SAMEDI + "=?,"
				+ CHAMP_HEURE_DIMANCHE + "=?," + CHAMP_BASE_LEGALE + "=?," + CHAMP_BASE_CALCULEE + "=? where "
				+ CHAMP_ID + " =?";
		jdbcTemplate.update(
				sql,
				new Object[] { base.getCodeBaseHorairePointage().toUpperCase(),
						base.getLibelleBaseHorairePointage().toUpperCase(), base.getDescriptionBaseHorairePointage(),
						base.getHeureLundi(), base.getHeureMardi(), base.getHeureMercredi(), base.getHeureJeudi(),
						base.getHeureVendredi(), base.getHeureSamedi(), base.getHeureDimanche(), base.getBaseLegale(),
						base.getBaseCalculee(), base.getIdBaseHorairePointage() });
	}

	@Override
	public BaseHorairePointage chercherBaseHorairePointage(Integer idBaseHorairePointage) throws Exception {
		return super.chercherObject(BaseHorairePointage.class, idBaseHorairePointage);
	}
}
