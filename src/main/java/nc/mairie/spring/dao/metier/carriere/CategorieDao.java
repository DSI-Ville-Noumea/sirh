package nc.mairie.spring.dao.metier.carriere;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.carriere.Categorie;
import nc.mairie.metier.poste.CategorieFE;
import nc.mairie.spring.dao.metier.poste.CategorieFEDao;
import nc.mairie.spring.dao.utils.SirhDao;

public class CategorieDao extends SirhDao implements CategorieDaoInterface {

	public static final String CHAMP_LIB_CATEGORIE_STATUT = "LIB_CATEGORIE_STATUT";

	public CategorieDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_CATEGORIE_STATUT";
		super.CHAMP_ID = "ID_CATEGORIE_STATUT";
	}

	@Override
	public ArrayList<Categorie> listerCategorie() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_CATEGORIE_STATUT;

		ArrayList<Categorie> liste = new ArrayList<Categorie>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			Categorie a = new Categorie();
			a.setIdCategorieStatut((Integer) row.get(CHAMP_ID));
			a.setLibCategorieStatut((String) row.get(CHAMP_LIB_CATEGORIE_STATUT));
			liste.add(a);
		}
		return liste;
	}

	@Override
	public ArrayList<Categorie> listerCategorieAvecFE(Integer idFicheEmploi, CategorieFEDao categorieFEDao)
			throws Exception {
		// Recherche de tous les liens FicheEmploi / Categorie
		ArrayList<CategorieFE> liens = categorieFEDao.listerCategorieFEAvecFE(idFicheEmploi);

		// Construction de la liste
		ArrayList<Categorie> result = new ArrayList<Categorie>();
		for (int i = 0; i < liens.size(); i++) {
			CategorieFE aLien = (CategorieFE) liens.get(i);
			try {
				Categorie categorie = chercherCategorie(aLien.getIdCategorieStatut());
				result.add(categorie);
			} catch (Exception e) {
				return new ArrayList<Categorie>();
			}
		}

		return result;
	}

	@Override
	public Categorie chercherCategorie(Integer idCategorie) throws Exception {
		return super.chercherObject(Categorie.class, idCategorie);
	}

	@Override
	public void creerCategorie(String libCategorie) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_CATEGORIE_STATUT + ") VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libCategorie.toUpperCase() });
	}

	@Override
	public void supprimerCategorie(Integer idCategorie) throws Exception {
		super.supprimerObject(idCategorie);
	}
}
