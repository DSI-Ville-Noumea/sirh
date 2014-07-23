package nc.mairie.spring.dao.metier.agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.metier.agent.Contrat;
import nc.mairie.spring.dao.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class ContratDao extends SirhDao implements ContratDaoInterface {

	public static final String CHAMP_ID_TYPE_CONTRAT = "ID_TYPE_CONTRAT";
	public static final String CHAMP_ID_MOTIF = "ID_MOTIF";
	public static final String CHAMP_ID_AGENT = "ID_AGENT";
	public static final String CHAMP_ID_DOCUMENT = "ID_DOCUMENT";
	public static final String CHAMP_NUM_CONTRAT = "NUM_CONTRAT";
	public static final String CHAMP_AVENANT = "AVENANT";
	public static final String CHAMP_ID_CONTRAT_REF = "ID_CONTRAT_REF";
	public static final String CHAMP_DATDEB = "DATDEB";
	public static final String CHAMP_DATE_FIN_PERIODE_ESS = "DATE_FIN_PERIODE_ESS";
	public static final String CHAMP_DATE_FIN = "DATE_FIN";
	public static final String CHAMP_JUSTIFICATION = "JUSTIFICATION";

	public ContratDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "CONTRAT";
		super.CHAMP_ID = "ID_CONTRAT";
	}

	@Override
	public Contrat chercherContratAgentDateComprise(Integer idAgent, Date date) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + " = ? and ((" + CHAMP_DATDEB
				+ "<=? and " + CHAMP_DATE_FIN + " >?) or (?>=" + CHAMP_DATDEB + " and " + CHAMP_DATE_FIN + " is null))";
		Contrat doc = (Contrat) jdbcTemplate.queryForObject(sql, new Object[] { idAgent, date, date, date },
				new BeanPropertyRowMapper<Contrat>(Contrat.class));
		return doc;
	}

	@Override
	public Contrat chercherDernierContrat(Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? and " + CHAMP_DATDEB
				+ "=(select max(" + CHAMP_DATDEB + ") from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=?)";
		Contrat doc = (Contrat) jdbcTemplate.queryForObject(sql, new Object[] { idAgent, idAgent },
				new BeanPropertyRowMapper<Contrat>(Contrat.class));
		return doc;
	}

	@Override
	public String getNumContratChrono() throws Exception {
		String sql = "select NEXT VALUE FOR NUM_CONTRAT_SEQ FROM SYSDUMMY1";
		String doc = (String) jdbcTemplate.queryForObject(sql, String.class);
		return doc;
	}

	@Override
	public void supprimerContrat(Integer idContrat) throws Exception {
		// Suppression des avenants au contrat
		for (Contrat c : listerContratAvenantAvecContratReference(idContrat))
			supprimerContrat(c.getIdContrat());
		super.supprimerObject(idContrat);
	}

	@Override
	public void creerContrat(Integer idTypeContrat, Integer idMotif, Integer idAgent, Integer idDocument,
			String numContrat, boolean avenant, Integer idContratRef, Date dateDebut, Date dateFinPeriodeEssai,
			Date dateFin, String justification) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID_TYPE_CONTRAT + "," + CHAMP_ID_MOTIF + ","
				+ CHAMP_ID_AGENT + "," + CHAMP_ID_DOCUMENT + "," + CHAMP_NUM_CONTRAT + "," + CHAMP_AVENANT + ","
				+ CHAMP_ID_CONTRAT_REF + "," + CHAMP_DATDEB + "," + CHAMP_DATE_FIN_PERIODE_ESS + "," + CHAMP_DATE_FIN
				+ "," + CHAMP_JUSTIFICATION + ") " + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { idTypeContrat, idMotif, idAgent, idDocument, numContrat, avenant,
				idContratRef, dateDebut, dateFinPeriodeEssai, dateFin, justification });
	}

	@Override
	public void modifierContrat(Integer idContrat, Integer idTypeContrat, Integer idMotif, Integer idAgent,
			Integer idDocument, String numContrat, boolean avenant, Integer idContratRef, Date dateDebut,
			Date dateFinPeriodeEssai, Date dateFin, String justification) throws Exception {

		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_TYPE_CONTRAT + "=?," + CHAMP_ID_MOTIF + "=?,"
				+ CHAMP_ID_AGENT + "=?," + CHAMP_ID_DOCUMENT + "=?," + CHAMP_NUM_CONTRAT + "=?," + CHAMP_AVENANT
				+ "=?," + CHAMP_ID_CONTRAT_REF + "=?," + CHAMP_DATDEB + "=?," + CHAMP_DATE_FIN_PERIODE_ESS + "=?,"
				+ CHAMP_DATE_FIN + "=?," + CHAMP_JUSTIFICATION + "=? where " + CHAMP_ID + " =?";
		jdbcTemplate.update(sql, new Object[] { idTypeContrat, idMotif, idAgent, idDocument, numContrat, avenant,
				idContratRef, dateDebut, dateFinPeriodeEssai, dateFin, justification, idContrat });
	}

	@Override
	public Contrat chercherContrat(Integer idContrat) throws Exception {
		return super.chercherObject(Contrat.class, idContrat);
	}

	@Override
	public ArrayList<Contrat> listerContratAvenantAvecContratReference(Integer idContratRef) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_CONTRAT_REF + "=? order by " + CHAMP_DATDEB;

		ArrayList<Contrat> liste = new ArrayList<Contrat>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idContratRef });
		for (Map<String, Object> row : rows) {
			Contrat a = new Contrat();
			a.setIdContrat((Integer) row.get(CHAMP_ID));
			a.setIdTypeContrat((Integer) row.get(CHAMP_ID_TYPE_CONTRAT));
			a.setIdMotif((Integer) row.get(CHAMP_ID_MOTIF));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setIdDocument((Integer) row.get(CHAMP_ID_DOCUMENT));
			a.setNumContrat((String) row.get(CHAMP_NUM_CONTRAT));
			Integer avenant = (Integer) row.get(CHAMP_AVENANT);
			a.setAvenant(avenant == 0 ? false : true);
			a.setIdContratRef((Integer) row.get(CHAMP_ID_CONTRAT_REF));
			a.setDatdeb((Date) row.get(CHAMP_DATDEB));
			a.setDateFinPeriodeEss((Date) row.get(CHAMP_DATE_FIN_PERIODE_ESS));
			a.setDateFin((Date) row.get(CHAMP_DATE_FIN));
			a.setJustification((String) row.get(CHAMP_JUSTIFICATION));
			liste.add(a);
		}
		return liste;
	}

	@Override
	public ArrayList<Contrat> listerContratAvecAgent(Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_ID_AGENT + "=? order by " + CHAMP_DATDEB;

		ArrayList<Contrat> liste = new ArrayList<Contrat>();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idAgent });
		for (Map<String, Object> row : rows) {
			Contrat a = new Contrat();
			a.setIdContrat((Integer) row.get(CHAMP_ID));
			a.setIdTypeContrat((Integer) row.get(CHAMP_ID_TYPE_CONTRAT));
			a.setIdMotif((Integer) row.get(CHAMP_ID_MOTIF));
			a.setIdAgent((Integer) row.get(CHAMP_ID_AGENT));
			a.setIdDocument((Integer) row.get(CHAMP_ID_DOCUMENT));
			a.setNumContrat((String) row.get(CHAMP_NUM_CONTRAT));
			Integer avenant = (Integer) row.get(CHAMP_AVENANT);
			a.setAvenant(avenant == 0 ? false : true);
			a.setIdContratRef((Integer) row.get(CHAMP_ID_CONTRAT_REF));
			a.setDatdeb((Date) row.get(CHAMP_DATDEB));
			a.setDateFinPeriodeEss((Date) row.get(CHAMP_DATE_FIN_PERIODE_ESS));
			a.setDateFin((Date) row.get(CHAMP_DATE_FIN));
			a.setJustification((String) row.get(CHAMP_JUSTIFICATION));
			liste.add(a);
		}
		return liste;
	}
}
