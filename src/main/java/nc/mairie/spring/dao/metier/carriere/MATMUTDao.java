package nc.mairie.spring.dao.metier.carriere;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import nc.mairie.metier.carriere.MATMUT;
import nc.mairie.spring.dao.utils.MairieDao;

public class MATMUTDao extends MairieDao implements MATMUTDaoInterface {

	public static final String CHAMP_PKEY = "PKEY";
	public static final String CHAMP_NOMATR = "NOMATR";
	public static final String CHAMP_PERREP = "PERREP";
	public static final String CHAMP_CODVAL = "CODVAL";
	public static final String CHAMP_TIMELOG = "TIMELOG";
	public static final String CHAMP_IDUSER = "IDUSER";

	public static final String NOM_TABLE_HISTO = "MATMUTHISTO";

	public MATMUTDao(MairieDao mairieDao) {
		super.dataSource = mairieDao.getDataSource();
		super.jdbcTemplate = mairieDao.getJdbcTemplate();
		super.NOM_TABLE = "MATMUT";
		super.CHAMP_ID = CHAMP_PKEY;
	}

	@Override
	public MATMUT chercherMatmutByMatrAndPeriod(Integer matricule, Integer dateMonth) {
		MATMUT matmut = null;
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_NOMATR + " = ? AND " + CHAMP_PERREP + " = ?";
		try {
			matmut = (MATMUT) jdbcTemplate.queryForObject(sql, new Object[] { matricule, dateMonth }, new BeanPropertyRowMapper<MATMUT>(MATMUT.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
		return matmut;
	}

	@Override
	public void creerMATMUT(MATMUT matmut) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_PKEY + "," + CHAMP_NOMATR + "," + CHAMP_PERREP
				+ "," + CHAMP_CODVAL + "," + CHAMP_TIMELOG + "," + CHAMP_IDUSER +") " 
				+ "VALUES (?,?,?,?,?,?)";
		jdbcTemplate.update(
				sql,
				new Object[] { matmut.getPkey(), matmut.getNomatr(), matmut.getPerrep(),
						matmut.getCodval(), matmut.getTimelog(), matmut.getIduser() });
	}

	@Override
	public void supprimerMATMUT(MATMUT matmut) {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_PKEY + " = ?";
		jdbcTemplate.update(sql, new Object[] { matmut.getPkey() });
	}

	/**
	 * Récupère la dernière valeur du champ 'pkey'.
	 * Il n'y a pas d'auto-increment sur ces tables, à cause de l'AS400...
	 * Il faut bien regarder dans la table d'historique si l'increment n'est pas supérieur à la table MATMUT
	 * Car les enregistrements ventilés ou non valides (CODE 'V' ou 'N') sont ajoutés directement dans la table d'historique.
	 */
	@Override
	public Integer getNextPKVal() {
		String sql = "select max(" + CHAMP_PKEY + ") from " + NOM_TABLE;
		Integer num = (Integer) jdbcTemplate.queryForObject(sql, Integer.class);
		
		String sqlHist = "select max(" + CHAMP_PKEY + ") from MATMUTHIST";
		Integer numHist = (Integer) jdbcTemplate.queryForObject(sqlHist, Integer.class);
		
		if (num == null && numHist == null)
			return 1;
		
		if (num == null)
			return numHist + 1;
		
		return (numHist == null ? (num + 1) : num > numHist ? (num+1) : (numHist+1));
	}

}
