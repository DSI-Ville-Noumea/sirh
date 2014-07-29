package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.DiplomeGenerique;
import nc.mairie.metier.poste.DiplomeFE;
import nc.mairie.spring.dao.utils.SirhDao;

public class DiplomeGeneriqueDao extends SirhDao implements DiplomeGeneriqueDaoInterface {

	public static final String CHAMP_LIB_DIPLOME_GENERIQUE = "LIB_DIPLOME_GENERIQUE";

	public DiplomeGeneriqueDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_DIPLOME_GENERIQUE";
		super.CHAMP_ID = "ID_DIPLOME_GENERIQUE";
	}

	@Override
	public void creerDiplomeGenerique(String libelleDiplomeGenerique) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_DIPLOME_GENERIQUE + ") VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { libelleDiplomeGenerique.toUpperCase() });

	}

	@Override
	public void supprimerDiplomeGenerique(Integer idDiplomeGenerique) throws Exception {
		super.supprimerObject(idDiplomeGenerique);

	}

	@Override
	public ArrayList<DiplomeGenerique> listerDiplomeGenerique() throws Exception {
		String sql = "select * from " + NOM_TABLE + " order by " + CHAMP_LIB_DIPLOME_GENERIQUE;

		ArrayList<DiplomeGenerique> listeDip = new ArrayList<DiplomeGenerique>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			DiplomeGenerique dip = new DiplomeGenerique();
			dip.setIdDiplomeGenerique((Integer) row.get(CHAMP_ID));
			dip.setLibDiplomeGenerique((String) row.get(CHAMP_LIB_DIPLOME_GENERIQUE));

			listeDip.add(dip);
		}

		return listeDip;
	}

	@Override
	public DiplomeGenerique chercherDiplomeGenerique(Integer idDiplomeGenerique) throws Exception {
		return super.chercherObject(DiplomeGenerique.class, idDiplomeGenerique);
	}

	@Override
	public ArrayList<DiplomeGenerique> listerDiplomeGeneriqueAvecFE(Integer idFicheEmploi, ArrayList<DiplomeFE> liens)
			throws Exception {

		// Construction de la liste
		ArrayList<DiplomeGenerique> result = new ArrayList<DiplomeGenerique>();
		for (int i = 0; i < liens.size(); i++) {
			DiplomeFE aLien = (DiplomeFE) liens.get(i);
			try {
				DiplomeGenerique diplome = chercherDiplomeGenerique(aLien.getIdDiplomeGenerique());
				result.add(diplome);
			} catch (Exception e) {
				return new ArrayList<DiplomeGenerique>();
			}
		}
		return result;
	}
}
