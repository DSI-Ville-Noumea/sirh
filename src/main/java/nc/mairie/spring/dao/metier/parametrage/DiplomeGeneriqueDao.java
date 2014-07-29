package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.parametrage.DiplomeGenerique;
import nc.mairie.metier.poste.DiplomeFE;
import nc.mairie.metier.poste.FicheEmploi;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.technique.MairieMessages;
import nc.mairie.technique.Transaction;

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
	public ArrayList<DiplomeGenerique> listerDiplomeGeneriqueAvecFE(Transaction aTransaction, FicheEmploi ficheEmploi)
			throws Exception {
		// Test du paramètre FicheEmploi
		if (ficheEmploi == null || ficheEmploi.getIdFicheEmploi() == null) {
			aTransaction.declarerErreur(MairieMessages.getMessage("ERR003", "FicheEmploi"));
			return new ArrayList<DiplomeGenerique>();
		}

		// Recherche de tous les liens FicheEmploi / DiplomeGenerique
		ArrayList<DiplomeFE> liens = DiplomeFE.listerDiplomeFEAvecFE(aTransaction, ficheEmploi);
		if (aTransaction.isErreur())
			return new ArrayList<DiplomeGenerique>();

		// Construction de la liste
		ArrayList<DiplomeGenerique> result = new ArrayList<DiplomeGenerique>();
		for (int i = 0; i < liens.size(); i++) {
			DiplomeFE aLien = (DiplomeFE) liens.get(i);
			DiplomeGenerique diplome = chercherDiplomeGenerique(Integer.valueOf(aLien.getIdDiplomeGenerique()));
			if (aTransaction.isErreur())
				return new ArrayList<DiplomeGenerique>();
			result.add(diplome);
		}

		return result;
	}
}
