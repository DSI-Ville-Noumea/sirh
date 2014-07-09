package nc.mairie.spring.dao.metier.parametrage;

import java.util.List;

import nc.mairie.metier.parametrage.Employeur;
import nc.mairie.spring.dao.SirhDao;

public class EmployeurDao extends SirhDao implements EmployeurDaoInterface {

	public static final String CHAMP_LIB_EMPLOYEUR = "LIB_EMPLOYEUR";
	public static final String CHAMP_TITRE_EMPLOYEUR = "TITRE_EMPLOYEUR";

	public EmployeurDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_EMPLOYEUR";
		super.CHAMP_ID = "ID_EMPLOYEUR";
	}

	@Override
	public List<Employeur> listerEmployeur() throws Exception {
		return super.getListe(Employeur.class);
	}

	@Override
	public Employeur chercherEmployeur(Integer idEmployeur) throws Exception {
		return super.chercherObject(Employeur.class, idEmployeur);
	}

	@Override
	public void creerEmployeur(String libelleEmployeur, String titreEmployeur) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_EMPLOYEUR + "," + CHAMP_TITRE_EMPLOYEUR + ") "
				+ "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { libelleEmployeur, titreEmployeur });
	}

	@Override
	public void modifierEmployeur(Integer idEmployeur, String libelleEmployeur, String titreEmployeur) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_LIB_EMPLOYEUR + "=?," + CHAMP_TITRE_EMPLOYEUR
				+ "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { libelleEmployeur, titreEmployeur, idEmployeur });
	}

	@Override
	public void supprimerEmployeur(Integer idEmployeur) throws Exception {
		super.supprimerObject(idEmployeur);
	}
}
