package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.Const;
import nc.mairie.metier.poste.Activite;
import nc.mairie.metier.poste.ActiviteFE;
import nc.mairie.metier.poste.ActiviteFP;
import nc.mairie.spring.dao.utils.SirhDao;

public class ActiviteDao extends SirhDao implements ActiviteDaoInterface {

	public static final String CHAMP_NOM_ACTIVITE = "NOM_ACTIVITE";

	public ActiviteDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "ACTIVITE";
		super.CHAMP_ID = "ID_ACTIVITE";
	}

	@Override
	public void supprimerActivite(Integer idActivite) throws Exception {
		super.supprimerObject(idActivite);
	}

	@Override
	public void modifierActivite(Integer idActivite, String nomActi) throws Exception {
		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_NOM_ACTIVITE + "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { nomActi, idActivite });
	}

	@Override
	public void creerActivite(String nomActi) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_NOM_ACTIVITE + ") " + "VALUES (?)";
		jdbcTemplate.update(sql, new Object[] { nomActi });
	}

	@Override
	public Activite chercherActivite(Integer idActivite) throws Exception {
		return super.chercherObject(Activite.class, idActivite);
	}

	@Override
	public ArrayList<Activite> listerToutesActiviteAvecFP(ArrayList<ActiviteFP> liens) throws Exception {
		// Construction de la liste
		ArrayList<Activite> result = new ArrayList<Activite>();
		for (int i = 0; i < liens.size(); i++) {
			ActiviteFP aLien = (ActiviteFP) liens.get(i);
			try {
				Activite acti = chercherActivite(aLien.getIdActivite());
				result.add(acti);
			} catch (Exception e) {
				return new ArrayList<Activite>();
			}
		}

		return result;
	}

	@Override
	public ArrayList<Activite> listerActiviteAvecFP(ArrayList<ActiviteFP> liens) throws Exception {
		// Construction de la liste
		ArrayList<Activite> result = new ArrayList<Activite>();
		for (int i = 0; i < liens.size(); i++) {
			ActiviteFP aLien = (ActiviteFP) liens.get(i);
			try {
				Activite acti = chercherActivite(aLien.getIdActivite());
				result.add(acti);
			} catch (Exception e) {
				return new ArrayList<Activite>();
			}
		}

		return result;
	}

	@Override
	public ArrayList<Activite> listerActiviteAvecFE(ArrayList<ActiviteFE> liens) throws Exception {
		// Construction de la liste
		ArrayList<Activite> result = new ArrayList<Activite>();
		for (int i = 0; i < liens.size(); i++) {
			ActiviteFE aLien = (ActiviteFE) liens.get(i);
			try {
				Activite acti = chercherActivite(aLien.getIdActivite());
				result.add(acti);
			} catch (Exception e) {
				return new ArrayList<Activite>();
			}
		}

		return result;
	}

	@Override
	public ArrayList<Activite> listerActivite(boolean ordreAlpha) throws Exception {
		String sql = Const.CHAINE_VIDE;
		if (ordreAlpha) {
			sql = "select * from " + NOM_TABLE + " order by upper(" + CHAMP_NOM_ACTIVITE + ") ";
		} else {
			sql = "select * from " + NOM_TABLE;
		}

		ArrayList<Activite> liste = new ArrayList<Activite>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			Activite a = new Activite();
			a.setIdActivite((Integer) row.get(CHAMP_ID));
			a.setNomActivite((String) row.get(CHAMP_NOM_ACTIVITE));
			liste.add(a);
		}

		return liste;
	}
}
