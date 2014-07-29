package nc.mairie.spring.dao.metier.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.agent.Contact;
import nc.mairie.spring.dao.utils.SirhDao;

public class ContactDao extends SirhDao implements ContactDaoInterface {

	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_ID_TYPE_CONTACT = "ID_TYPE_CONTACT";
	public static final String CHAMP_DESCRIPTION = "DESCRIPTION";
	public static final String CHAMP_DIFFUSABLE = "DIFFUSABLE";
	public static final String CHAMP_PRIORITAIRE = "PRIORITAIRE";

	public ContactDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "CONTACT";
		super.CHAMP_ID = "ID_CONTACT";
	}

	@Override
	public List<Contact> listerContact() throws Exception {
		return super.getListe(Contact.class);
	}

	@Override
	public ArrayList<Contact> listerContactAgent(Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? ";

		ArrayList<Contact> liste = new ArrayList<Contact>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent });
		for (Map<String, Object> row : rows) {
			Contact a = new Contact();
			a.setIdContact((Integer) row.get(CHAMP_ID));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setIdTypeContact((Integer) row.get(CHAMP_ID_TYPE_CONTACT));
			a.setDescription((String) row.get(CHAMP_DESCRIPTION));
			String diffu = (String) row.get(CHAMP_DIFFUSABLE);
			a.setDiffusable(diffu.equals("0") ? false : true);
			Integer prio = (Integer) row.get(CHAMP_PRIORITAIRE);
			a.setPrioritaire(prio == 0 ? false : true);
			liste.add(a);
		}
		return liste;
	}

	@Override
	public ArrayList<Contact> listerContactAgentAvecTypeContact(Integer idAgent, Integer idTypeContact)
			throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? and " + CHAMP_ID_TYPE_CONTACT
				+ "=?";

		ArrayList<Contact> liste = new ArrayList<Contact>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent, idTypeContact });
		for (Map<String, Object> row : rows) {
			Contact a = new Contact();
			a.setIdContact((Integer) row.get(CHAMP_ID));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setIdTypeContact((Integer) row.get(CHAMP_ID_TYPE_CONTACT));
			a.setDescription((String) row.get(CHAMP_DESCRIPTION));
			String diffu = (String) row.get(CHAMP_DIFFUSABLE);
			a.setDiffusable(diffu.equals("0") ? false : true);
			Integer prio = (Integer) row.get(CHAMP_PRIORITAIRE);
			a.setPrioritaire(prio == 0 ? false : true);
			liste.add(a);
		}
		return liste;
	}

	@Override
	public void creerContact(Integer idAgent, Integer idTypeContact, String description, boolean diffusable,
			boolean prioritaire) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_AGENT + "," + CHAMP_ID_TYPE_CONTACT + ","
				+ CHAMP_DESCRIPTION + "," + CHAMP_DIFFUSABLE + "," + CHAMP_PRIORITAIRE + ") VALUES (?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idAgent, idTypeContact, description, diffusable ? "1" : "0",
				prioritaire });
	}

	@Override
	public void modifierContact(Integer idContact, Integer idAgent, Integer idTypeContact, String description,
			boolean diffusable, boolean prioritaire) throws Exception {

		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_AGENT + "=?," + CHAMP_ID_TYPE_CONTACT + "=?,"
				+ CHAMP_DESCRIPTION + "=?," + CHAMP_DIFFUSABLE + "=?," + CHAMP_PRIORITAIRE + "=? where " + CHAMP_ID
				+ " =?";
		jdbcTemplate.update(sql, new Object[] { idAgent, idTypeContact, description, diffusable ? "1" : "0",
				prioritaire, idContact });
	}

	@Override
	public void supprimerContact(Integer idContact) throws Exception {
		super.supprimerObject(idContact);
	}
}
