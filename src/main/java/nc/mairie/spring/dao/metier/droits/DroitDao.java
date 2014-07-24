package nc.mairie.spring.dao.metier.droits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.droits.Droit;
import nc.mairie.spring.dao.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class DroitDao extends SirhDao implements DroitDaoInterface {

	public static final String CHAMP_ID_ELEMENT = "ID_ELEMENT";
	public static final String CHAMP_ID_GROUPE = "ID_GROUPE";
	public static final String CHAMP_ID_TYPE_DROIT = "ID_TYPE_DROIT";

	public DroitDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "DROITS";
	}

	@Override
	public ArrayList<Droit> listerDroit() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_ID_ELEMENT + ", " + CHAMP_ID_GROUPE;

		ArrayList<Droit> liste = new ArrayList<Droit>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			Droit a = new Droit();
			a.setIdElement((Integer) row.get(CHAMP_ID_ELEMENT));
			a.setIdGroupe((Integer) row.get(CHAMP_ID_GROUPE));
			a.setIdTypeDroit((Integer) row.get(CHAMP_ID_TYPE_DROIT));
			liste.add(a);
		}

		return liste;
	}

	@Override
	public void supprimerDroitAvecGroupe(Integer idGroupe) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + " where " + CHAMP_ID_GROUPE + "=? ";
		jdbcTemplate.update(sql, new Object[] { idGroupe });
	}

	@Override
	public void modifierDroit(Integer idElement, Integer idGroupe, Integer idTypeDroit) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_TYPE_DROIT + "=? where " + CHAMP_ID_ELEMENT
				+ " =? and " + CHAMP_ID_GROUPE + "=?";
		jdbcTemplate.update(sql, new Object[] { idTypeDroit, idElement, idGroupe });
	}

	@Override
	public void creerDroit(Integer idElement, Integer idGroupe, Integer idTypeDroit) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_ELEMENT + "," + CHAMP_ID_GROUPE + ","
				+ CHAMP_ID_TYPE_DROIT + ") VALUES (?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idElement, idGroupe, idTypeDroit });
	}

	@Override
	public Droit chercherDroit(Integer idElement, Integer idGroupe) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_ELEMENT + " = ? and " + CHAMP_ID_GROUPE + "=?";
		Droit cadre = (Droit) jdbcTemplate.queryForObject(sql, new Object[] { idElement, idGroupe },
				new BeanPropertyRowMapper<Droit>(Droit.class));
		return cadre;
	}
}
