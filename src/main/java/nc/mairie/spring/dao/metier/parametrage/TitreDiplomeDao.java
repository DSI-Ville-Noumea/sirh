package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.TitreDiplome;
import nc.mairie.spring.dao.SirhDao;

public class TitreDiplomeDao extends SirhDao implements TitreDiplomeDaoInterface {

	public static final String CHAMP_LIB_TITRE_DIPLOME = "LIB_TITRE_DIPLOME";
	public static final String CHAMP_NIVEAU_ETUDE = "NIVEAU_ETUDE";

	public TitreDiplomeDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_TITRE_DIPLOME";
		super.CHAMP_ID = "ID_TITRE_DIPLOME";
	}

	@Override
	public void creerTitreDiplome(String libelleTitreDiplome, String niveauTitreDiplome) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_TITRE_DIPLOME + "," + CHAMP_NIVEAU_ETUDE
				+ ") VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { libelleTitreDiplome.toUpperCase(), niveauTitreDiplome.toUpperCase() });
	}

	@Override
	public void supprimerTitreDiplome(Integer idTitreDiplome) throws Exception {
		super.supprimerObject(idTitreDiplome);
	}

	@Override
	public void modifierTitreDiplome(Integer idTitreDiplome, String libelleTitreDiplome, String niveauTitreDiplome)
			throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_LIB_TITRE_DIPLOME + "=?," + CHAMP_NIVEAU_ETUDE
				+ "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { libelleTitreDiplome.toUpperCase(), niveauTitreDiplome.toUpperCase(),
				idTitreDiplome });
	}

	@Override
	public TitreDiplome chercherTitreDiplome(Integer idTitreDiplome) throws Exception {
		return super.chercherObject(TitreDiplome.class, idTitreDiplome);
	}

	@Override
	public ArrayList<TitreDiplome> listerTitreDiplome() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_TITRE_DIPLOME;

		ArrayList<TitreDiplome> listeFam = new ArrayList<TitreDiplome>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			TitreDiplome fam = new TitreDiplome();
			fam.setIdTitreDiplome((Integer) row.get(CHAMP_ID));
			fam.setLibTitreDiplome((String) row.get(CHAMP_LIB_TITRE_DIPLOME));
			fam.setNiveauEtude((String) row.get(CHAMP_NIVEAU_ETUDE));

			listeFam.add(fam);
		}

		return listeFam;
	}
}
