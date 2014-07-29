package nc.mairie.spring.dao.metier.referentiel;

import java.util.ArrayList;
import java.util.List;

import nc.mairie.metier.poste.NiveauEtudeFE;
import nc.mairie.metier.referentiel.NiveauEtude;
import nc.mairie.spring.dao.utils.SirhDao;

public class NiveauEtudeDao extends SirhDao implements NiveauEtudeDaoInterface {

	public static final String CHAMP_CODE_NIVEAU_ETUDE = "CODE_NIVEAU_ETUDE";

	public NiveauEtudeDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "R_NIVEAU_ETUDE";
		super.CHAMP_ID = "ID_NIVEAU_ETUDE";
	}

	@Override
	public NiveauEtude chercherNiveauEtude(Integer idNiveau) throws Exception {
		return super.chercherObject(NiveauEtude.class, idNiveau);
	}

	@Override
	public ArrayList<NiveauEtude> listerNiveauEtudeAvecFE(ArrayList<NiveauEtudeFE> liens) throws Exception {
		// Construction de la liste
		ArrayList<NiveauEtude> result = new ArrayList<NiveauEtude>();
		for (int i = 0; i < liens.size(); i++) {
			NiveauEtudeFE aLien = (NiveauEtudeFE) liens.get(i);
			try {
				NiveauEtude niveau = chercherNiveauEtude(Integer.valueOf(aLien.getIdNiveauEtude()));
				result.add(niveau);
			} catch (Exception e) {
				return new ArrayList<NiveauEtude>();
			}
		}

		return result;
	}

	@Override
	public List<NiveauEtude> listerNiveauEtude() throws Exception {
		return super.getListe(NiveauEtude.class);
	}
}
