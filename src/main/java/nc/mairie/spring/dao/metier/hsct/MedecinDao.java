package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.hsct.Medecin;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class MedecinDao extends SirhDao implements MedecinDaoInterface {

	public static final String CHAMP_NOM_MEDECIN = "NOM_MEDECIN";
	public static final String CHAMP_PRENOM_MEDECIN = "PRENOM_MEDECIN";
	public static final String CHAMP_TITRE_MEDECIN = "TITRE_MEDECIN";

	public MedecinDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_MEDECIN";
		super.CHAMP_ID = "ID_MEDECIN";
	}

	@Override
	public Medecin chercherMedecin(Integer idMedecin) throws Exception {
		return super.chercherObject(Medecin.class, idMedecin);
	}

	@Override
	public void creerMedecin(String titreMedecin, String prenomMedecin, String nomMedecin) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_TITRE_MEDECIN + "," + CHAMP_PRENOM_MEDECIN + ","
				+ CHAMP_NOM_MEDECIN + ") VALUES (?,?,?)";
		jdbcTemplate.update(sql,
				new Object[] { titreMedecin.toUpperCase(), prenomMedecin.toUpperCase(), nomMedecin.toUpperCase() });
	}

	@Override
	public void supprimerMedecin(Integer idMedecin) throws Exception {
		super.supprimerObject(idMedecin);
	}

	@Override
	public Medecin chercherMedecinARenseigner(String prenomMedecin, String nomMedecin) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_PRENOM_MEDECIN + " = ? and " + CHAMP_NOM_MEDECIN
				+ " = ?";
		Medecin med = (Medecin) jdbcTemplate.queryForObject(sql, new Object[] { prenomMedecin, nomMedecin },
				new BeanPropertyRowMapper<Medecin>(Medecin.class));
		return med;
	}

	@Override
	public ArrayList<Medecin> listerMedecin() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_PRENOM_MEDECIN + "," + CHAMP_NOM_MEDECIN;

		ArrayList<Medecin> liste = new ArrayList<Medecin>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			Medecin a = new Medecin();
			a.setIdMedecin((Integer) row.get(CHAMP_ID));
			a.setTitreMedecin((String) row.get(CHAMP_TITRE_MEDECIN));
			a.setPrenomMedecin((String) row.get(CHAMP_PRENOM_MEDECIN));
			a.setNomMedecin((String) row.get(CHAMP_NOM_MEDECIN));
			liste.add(a);
		}

		return liste;
	}
}
