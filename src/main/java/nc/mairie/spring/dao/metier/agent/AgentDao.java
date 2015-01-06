package nc.mairie.spring.dao.metier.agent;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.mairie.connecteur.Connecteur;
import nc.mairie.enums.EnumCollectivite;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.Contact;
import nc.mairie.metier.referentiel.SituationFamiliale;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.technique.Services;
import nc.mairie.technique.Transaction;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class AgentDao extends SirhDao implements AgentDaoInterface {

	public static final String CHAMP_ID_VOIE = "ID_VOIE";
	public static final String CHAMP_ID_COLLECTIVITE = "ID_COLLECTIVITE";
	public static final String CHAMP_ID_SITUATION_FAMILIALEE = "ID_SITUATION_FAMILIALE";
	public static final String CHAMP_ID_ETAT_SERVICE = "ID_ETAT_SERVICE";
	public static final String CHAMP_CPOS_VILLE_DOM = "CPOS_VILLE_DOM";
	public static final String CHAMP_CCOM_VILLE_DOM = "CCOM_VILLE_DOM";
	public static final String CHAMP_CPOS_VILLE_BP = "CPOS_VILLE_BP";
	public static final String CHAMP_CCOM_VILLE_BP = "CCOM_VILLE_BP";
	public static final String CHAMP_NOMATR = "NOMATR";
	public static final String CHAMP_NOM_MARITAL = "NOM_MARITAL";
	public static final String CHAMP_PRENOM = "PRENOM";
	public static final String CHAMP_PRENOM_USAGE = "PRENOM_USAGE";
	public static final String CHAMP_CIVILITE = "CIVILITE";
	public static final String CHAMP_NOM_PATRONYMIQUE = "NOM_PATRONYMIQUE";
	public static final String CHAMP_NOM_USAGE = "NOM_USAGE";
	public static final String CHAMP_DATE_NAISSANCE = "DATE_NAISSANCE";
	public static final String CHAMP_DATE_DECES = "DATE_DECES";
	public static final String CHAMP_SEXE = "SEXE";
	public static final String CHAMP_DATE_PREMIERE_EMBAUCHE = "DATE_PREMIERE_EMBAUCHE";
	public static final String CHAMP_DATE_DERNIERE_EMBAUCHE = "DATE_DERNIERE_EMBAUCHE";
	public static final String CHAMP_NATIONALITE = "NATIONALITE";
	public static final String CHAMP_CODE_PAYS_NAISS_ET = "CODE_PAYS_NAISS_ET";
	public static final String CHAMP_CODE_COMMUNE_NAISS_ET = "CODE_COMMUNE_NAISS_ET";
	public static final String CHAMP_CODE_COMMUNE_NAISS_FR = "CODE_COMMUNE_NAISS_FR";
	public static final String CHAMP_NUM_CARTE_SEJOUR = "NUM_CARTE_SEJOUR";
	public static final String CHAMP_DATE_VALIDITE_CARTE_SEJOUR = "DATE_VALIDITE_CARTE_SEJOUR";
	public static final String CHAMP_NUM_RUE = "NUM_RUE";
	public static final String CHAMP_NUM_RUE_BIS_TER = "NUM_RUE_BIS_TER";
	public static final String CHAMP_ADRESSE_COMPLEMENTAIRE = "ADRESSE_COMPLEMENTAIRE";
	public static final String CHAMP_BP = "BP";
	public static final String CHAMP_CD_BANQUE = "CD_BANQUE";
	public static final String CHAMP_CD_GUICHET = "CD_GUICHET";
	public static final String CHAMP_NUM_COMPTE = "NUM_COMPTE";
	public static final String CHAMP_RIB = "RIB";
	public static final String CHAMP_INTITULE_COMPTE = "INTITULE_COMPTE";
	public static final String CHAMP_VCAT = "VCAT";
	public static final String CHAMP_DEBUT_SERVICE = "DEBUT_SERVICE";
	public static final String CHAMP_FIN_SERVICE = "FIN_SERVICE";
	public static final String CHAMP_NUM_CAFAT = "NUM_CAFAT";
	public static final String CHAMP_NUM_RUAMM = "NUM_RUAMM";
	public static final String CHAMP_NUM_MUTUELLE = "NUM_MUTUELLE";
	public static final String CHAMP_NUM_CRE = "NUM_CRE";
	public static final String CHAMP_NUM_IRCAFEX = "NUM_IRCAFEX";
	public static final String CHAMP_NUM_CLR = "NUM_CLR";
	public static final String CHAMP_CODE_ELECTION = "CODE_ELECTION";
	public static final String CHAMP_RUE_NON_NOUMEA = "RUE_NON_NOUMEA";

	public AgentDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "AGENT";
		super.CHAMP_ID = "ID_AGENT";
	}

	@Override
	public ArrayList<Agent> listerAgentWithStatut(String population) throws Exception {
		String anneeJour = new SimpleDateFormat("yyyy").format(new Date());

		String reqWhere = Const.CHAINE_VIDE;
		if (population.equals("Fonctionnaire")) {
			reqWhere = " and (carr.cdcate=1 or carr.cdcate=2 or carr.cdcate=18 or carr.cdcate=20) ";
		} else if (population.equals("Contractuel")) {
			reqWhere = " and carr.cdcate=4 ";
		} else if (population.equals("Convention collective")) {
			reqWhere = " and carr.cdcate=7 ";
		} else if (population.equals("Detache")) {
			reqWhere = " and (carr.cdcate=6 or carr.cdcate=16 or carr.cdcate=17 or carr.cdcate=19) ";
		}

		String sql = "select ag.* from "
				+ NOM_TABLE
				+ " ag  inner join SPCARR carr on ag."
				+ CHAMP_NOMATR
				+ " = carr.nomatr inner join SPADMN pa on carr.nomatr = pa.nomatr where (pa.datfin = 0 or substr(pa.datfin,0,5) >=?) and LENGTH(TRIM(TRANSLATE(pa.cdpadm,' ', ' +-.0123456789')))=0 "
				+ reqWhere
				+ " and carr.datdeb = (select max(c.datdeb) from spcarr c where c.nomatr=carr.nomatr and substr(c.datdeb,0,5) <=? and (c.datfin=0 or substr(c.datfin,0,5) >=? ))";

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql,
				new Object[] { anneeJour, anneeJour, anneeJour });
		return mapAgent(rows);
	}

	@Override
	public ArrayList<Agent> listerAgentAvecCafatCommencant(String zone) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where trim(" + CHAMP_NUM_CAFAT + ") like ? or trim("
				+ CHAMP_NUM_RUAMM + ") like ? order by " + CHAMP_NOMATR;

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { zone + "%", zone + "%" });
		return mapAgent(rows);
	}

	@Override
	public ArrayList<Agent> listerAgentAvecServicesETMatricules(ArrayList<String> codesServices, Integer idAgentMin,
			Integer idAgentMax) throws Exception {
		String reqWhere = Const.CHAINE_VIDE;
		if (idAgentMin != null && idAgentMax != null) {
			reqWhere = " and ag." + CHAMP_ID + " between " + idAgentMin + " and " + idAgentMax + " ";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("('");
		for (String s : codesServices) {
			sb.append(s);
			sb.append("','");
		}
		sb.deleteCharAt(sb.lastIndexOf("'"));
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append(")");

		String sql = "select distinct ag.* from "
				+ NOM_TABLE
				+ " ag, affectation af, fiche_poste fp where (af.DATE_FIN_AFF is null or af.DATE_FIN_AFF >=?) and fp.id_servi in "
				+ sb.toString() + " and fp.id_fiche_poste = af.id_fiche_poste and af.id_agent = ag." + CHAMP_ID + " "
				+ reqWhere + " order by ag." + CHAMP_NOMATR;

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { new Date() });
		return mapAgent(rows);
	}

	@Override
	public ArrayList<Agent> listerAgentEntreDeuxIdAgent(Integer idAgentMin, Integer idAgentMax) throws Exception {
		String sql = "select a.* from "
				+ NOM_TABLE
				+ " a inner join AFFECTATION aff on a."
				+ CHAMP_ID
				+ " = aff.id_agent where aff.date_debut_aff <=? and (aff.date_fin_aff is null or aff.date_fin_aff>=?) and a.id_agent between ?  and ? ";

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { new Date(), new Date(),
				idAgentMin, idAgentMax });
		return mapAgent(rows);
	}

	@Override
	public Agent chercherIrcafex(String numIrcafex, Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where trim(" + CHAMP_NUM_IRCAFEX + ") = ? ";
		if (idAgent != null) {
			sql += " and ID_AGENT<>" + idAgent;
		}
		Agent doc = (Agent) jdbcTemplate.queryForObject(sql, new Object[] { numIrcafex },
				new BeanPropertyRowMapper<Agent>(Agent.class));
		return doc;
	}

	@Override
	public Agent chercherCre(String numCre, Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where trim(" + CHAMP_NUM_CRE + ") = ? ";
		if (idAgent != null) {
			sql += " and ID_AGENT<>" + idAgent;
		}
		Agent doc = (Agent) jdbcTemplate.queryForObject(sql, new Object[] { numCre }, new BeanPropertyRowMapper<Agent>(
				Agent.class));
		return doc;
	}

	@Override
	public Agent chercherClr(String numClr, Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where trim(" + CHAMP_NUM_CLR + ") = ? ";
		if (idAgent != null) {
			sql += " and ID_AGENT<>" + idAgent;
		}
		Agent doc = (Agent) jdbcTemplate.queryForObject(sql, new Object[] { numClr }, new BeanPropertyRowMapper<Agent>(
				Agent.class));
		return doc;
	}

	@Override
	public Agent chercherMutuelle(String numMutuelle, Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where trim(" + CHAMP_NUM_MUTUELLE + ") = ? ";
		if (idAgent != null) {
			sql += " and ID_AGENT<>" + idAgent;
		}
		Agent doc = (Agent) jdbcTemplate.queryForObject(sql, new Object[] { numMutuelle },
				new BeanPropertyRowMapper<Agent>(Agent.class));
		return doc;
	}

	@Override
	public Agent chercherRuam(String numRuamm, Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where trim(" + CHAMP_NUM_RUAMM + ") = ? ";
		if (idAgent != null) {
			sql += " and ID_AGENT<>" + idAgent;
		}
		Agent doc = (Agent) jdbcTemplate.queryForObject(sql, new Object[] { numRuamm },
				new BeanPropertyRowMapper<Agent>(Agent.class));
		return doc;
	}

	@Override
	public Agent chercherCafat(String numCafat, Integer idAgent) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where trim(" + CHAMP_NUM_CAFAT + ") = ? ";
		if (idAgent != null) {
			sql += " and ID_AGENT<>" + idAgent;
		}
		Agent doc = (Agent) jdbcTemplate.queryForObject(sql, new Object[] { numCafat },
				new BeanPropertyRowMapper<Agent>(Agent.class));
		return doc;
	}

	@Override
	public ArrayList<Agent> listerAgentWithListNomatr(String listNoMatr) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_NOMATR + " in (" + listNoMatr + ") order by "
				+ CHAMP_NOM_MARITAL + "," + CHAMP_NOM_USAGE + "," + CHAMP_NOM_PATRONYMIQUE;

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		return mapAgent(rows);
	}

	@Override
	public ArrayList<Agent> listerAgentSansVMPAEnCours(String listeNomatr) throws Exception {
		String sql = "select * from " + NOM_TABLE + "  where " + CHAMP_ID
				+ " not in (select vm.id_agent from visite_medicale vm) and " + CHAMP_NOMATR + " in(" + listeNomatr
				+ ") ";

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		return mapAgent(rows);
	}

	@Override
	public ArrayList<Agent> listerAgentNouveauxArrivant(Integer moisChoisi, Integer anneeChoisi) throws Exception {
		String sql = "select * from " + NOM_TABLE + "  where month(" + CHAMP_DATE_DERNIERE_EMBAUCHE + ") =? and year("
				+ CHAMP_DATE_DERNIERE_EMBAUCHE + ") =?";

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { moisChoisi, anneeChoisi });
		return mapAgent(rows);
	}

	@Override
	public Agent chercherAgentParMatricule(Integer noMatr) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_NOMATR + " = ? ";
		Agent doc = (Agent) jdbcTemplate.queryForObject(sql, new Object[] { noMatr }, new BeanPropertyRowMapper<Agent>(
				Agent.class));
		return doc;
	}

	@Override
	public Agent chercherAgentAffecteFichePosteSecondaire(Integer idFichePoste) throws Exception {
		String sql = "select a.* from "
				+ NOM_TABLE
				+ " a,affectation aff,fiche_poste fp WHERE  aff.id_fiche_poste_secondaire = fp.id_fiche_poste and aff.id_agent = a."
				+ CHAMP_ID
				+ " and fp.id_fiche_poste = ? AND DATE_DEBUT_AFF <= ? AND (DATE_FIN_AFF is null or DATE_FIN_AFF >=?)";

		Agent doc = (Agent) jdbcTemplate.queryForObject(sql, new Object[] { idFichePoste, new Date(), new Date() },
				new BeanPropertyRowMapper<Agent>(Agent.class));
		return doc;
	}

	@Override
	public Agent chercherAgentAffecteFichePoste(Integer idFichePoste) throws Exception {
		String sql = "select a.* from "
				+ NOM_TABLE
				+ " a,affectation aff,fiche_poste fp WHERE  aff.id_fiche_poste = fp.id_fiche_poste and aff.id_agent = a."
				+ CHAMP_ID
				+ " and fp.id_fiche_poste = ? AND DATE_DEBUT_AFF <= ? AND (DATE_FIN_AFF is null or DATE_FIN_AFF >=?)";

		Agent doc = (Agent) jdbcTemplate.queryForObject(sql, new Object[] { idFichePoste, new Date(), new Date() },
				new BeanPropertyRowMapper<Agent>(Agent.class));
		return doc;
	}

	@Override
	public ArrayList<Agent> listerAgentAvecEnfant(Integer idEnfant) throws Exception {
		String sql = "select agt.* from " + NOM_TABLE + " agt, PARENT_ENFANT pe WHERE pe.ID_ENFANT = ? AND agt."
				+ CHAMP_ID + " = pe.ID_AGENT";

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { idEnfant });
		return mapAgent(rows);
	}

	@Override
	public ArrayList<Agent> listerAgentHomonyme(String nom, String prenom, Date dateNaiss) throws Exception {
		String sql = "select * from " + NOM_TABLE + " WHERE upper(" + CHAMP_NOM_USAGE + ") =? and upper("
				+ CHAMP_PRENOM_USAGE + ") =? and " + CHAMP_DATE_NAISSANCE + "=?";

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql,
				new Object[] { nom.toUpperCase(), prenom.toUpperCase(), dateNaiss });
		return mapAgent(rows);
	}

	@Override
	public ArrayList<Agent> listerAgentAvecPrenomCommencant(String debPrenom) throws Exception {
		String sql = "select * from " + NOM_TABLE + " WHERE upper(" + CHAMP_PRENOM + ") like ? order by "
				+ CHAMP_NOMATR;

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { debPrenom.replace("'", "''")
				.toUpperCase() + "%" });
		return mapAgent(rows);
	}

	@Override
	public ArrayList<Agent> listerAgentAvecServiceCommencant(String debCodeService) throws Exception {
		String sql = "select distinct ag.* from "
				+ NOM_TABLE
				+ " ag, affectation af, fiche_poste fp where (af.DATE_FIN_AFF is null or af.DATE_FIN_AFF >=?) and fp.id_servi like ? and fp.id_fiche_poste = af.id_fiche_poste and af.id_agent = ag."
				+ CHAMP_ID + " order by ag." + CHAMP_NOMATR;

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql,
				new Object[] { new Date(), debCodeService + "%" });
		return mapAgent(rows);
	}

	@Override
	public ArrayList<Agent> listerAgentAvecNomCommencant(String debNom) throws Exception {
		String sql = "select * from " + NOM_TABLE + " WHERE upper(" + CHAMP_NOM_MARITAL + ") like ? or upper("
				+ CHAMP_NOM_PATRONYMIQUE + ") like ? or upper(" + CHAMP_NOM_USAGE + ") like ? order by " + CHAMP_NOMATR;

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] {
				debNom.replace("'", "''").toUpperCase() + "%", debNom.replace("'", "''").toUpperCase() + "%",
				debNom.replace("'", "''").toUpperCase() + "%" });
		return mapAgent(rows);
	}

	@Override
	public Agent chercherAgent(Integer idAgent) throws Exception {
		return super.chercherObject(Agent.class, idAgent);
	}

	@Override
	public ArrayList<Agent> listerAgentEligibleAvct(ArrayList<String> listeSousService, String listeNoMatr)
			throws Exception {
		String reqWhere = Const.CHAINE_VIDE;
		String reqInner = Const.CHAINE_VIDE;
		if (listeSousService != null) {
			String list = Const.CHAINE_VIDE;
			for (String codeServ : listeSousService) {
				list += "'" + codeServ + "',";
			}
			if (!list.equals(Const.CHAINE_VIDE)) {
				list = list.substring(0, list.length() - 1);
			}
			reqWhere += " and fp.id_servi in (" + list + ") ";
			reqInner = " inner join AFFECTATION aff on a." + CHAMP_ID
					+ " = aff.id_agent inner join FICHE_POSTE fp on aff.id_fiche_poste= fp.id_fiche_poste ";
		}
		String sql = "select a.* from " + NOM_TABLE + " a " + reqInner + " where a." + CHAMP_NOMATR + " in("
				+ listeNoMatr + ") " + reqWhere;

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		return mapAgent(rows);
	}

	@Override
	public List<Agent> listerAgent() throws Exception {
		return super.getListe(Agent.class);
	}

	private ArrayList<Agent> mapAgent(List<Map<String, Object>> rows) {
		ArrayList<Agent> liste = new ArrayList<Agent>();
		for (Map<String, Object> row : rows) {
			Agent a = new Agent();
			a.setIdAgent((Integer) row.get(CHAMP_ID));
			BigDecimal idVoie = (BigDecimal) row.get(CHAMP_ID_VOIE);
			a.setIdVoie(idVoie == null ? null : idVoie.intValue());
			a.setIdCollectivite((Integer) row.get(CHAMP_ID_COLLECTIVITE));
			a.setIdSituationFamiliale((Integer) row.get(CHAMP_ID_SITUATION_FAMILIALEE));
			a.setIdEtatService((Integer) row.get(CHAMP_ID_ETAT_SERVICE));
			BigDecimal cposDom = (BigDecimal) row.get(CHAMP_CPOS_VILLE_DOM);
			a.setCposVilleDom(cposDom == null ? null : cposDom.intValue());
			BigDecimal ccomDom = (BigDecimal) row.get(CHAMP_CCOM_VILLE_DOM);
			a.setCcomVilleDom(ccomDom == null ? null : ccomDom.intValue());
			BigDecimal cposBP = (BigDecimal) row.get(CHAMP_CPOS_VILLE_BP);
			a.setCposVilleBp(cposBP == null ? null : cposBP.intValue());
			BigDecimal ccomBP = (BigDecimal) row.get(CHAMP_CCOM_VILLE_BP);
			a.setCcomVilleBp(ccomBP == null ? null : ccomBP.intValue());
			a.setNomatr((Integer) row.get(CHAMP_NOMATR));
			a.setNomMarital((String) row.get(CHAMP_NOM_MARITAL));
			a.setPrenom((String) row.get(CHAMP_PRENOM));
			a.setPrenomUsage((String) row.get(CHAMP_PRENOM_USAGE));
			a.setCivilite((String) row.get(CHAMP_CIVILITE));
			a.setNomPatronymique((String) row.get(CHAMP_NOM_PATRONYMIQUE));
			a.setNomUsage((String) row.get(CHAMP_NOM_USAGE));
			a.setDateNaissance((Date) row.get(CHAMP_DATE_NAISSANCE));
			a.setDateDeces((Date) row.get(CHAMP_DATE_DECES));
			a.setSexe((String) row.get(CHAMP_SEXE));
			a.setDatePremiereEmbauche((Date) row.get(CHAMP_DATE_PREMIERE_EMBAUCHE));
			a.setDateDerniereEmbauche((Date) row.get(CHAMP_DATE_DERNIERE_EMBAUCHE));
			a.setNationalite((String) row.get(CHAMP_NATIONALITE));
			BigDecimal codePayEt = (BigDecimal) row.get(CHAMP_CODE_PAYS_NAISS_ET);
			a.setCodePaysNaissEt(codePayEt == null ? null : codePayEt.intValue());
			BigDecimal commEt = (BigDecimal) row.get(CHAMP_CODE_COMMUNE_NAISS_ET);
			a.setCodeCommuneNaissEt(commEt == null ? null : commEt.intValue());
			BigDecimal commFr = (BigDecimal) row.get(CHAMP_CODE_COMMUNE_NAISS_FR);
			a.setCodeCommuneNaissFr(commFr == null ? null : commFr.intValue());
			a.setNumCarteSejour((String) row.get(CHAMP_NUM_CARTE_SEJOUR));
			a.setDateValiditeCarteSejour((Date) row.get(CHAMP_DATE_VALIDITE_CARTE_SEJOUR));
			a.setNumRue((String) row.get(CHAMP_NUM_RUE));
			a.setNumRueBisTer((String) row.get(CHAMP_NUM_RUE_BIS_TER));
			a.setAdresseComplementaire((String) row.get(CHAMP_ADRESSE_COMPLEMENTAIRE));
			a.setBp((String) row.get(CHAMP_BP));
			BigDecimal banque = (BigDecimal) row.get(CHAMP_CD_BANQUE);
			a.setCdBanque(banque == null ? null : banque.intValue());
			BigDecimal guichet = (BigDecimal) row.get(CHAMP_CD_GUICHET);
			a.setCdGuichet(guichet == null ? null : guichet.intValue());
			a.setNumCompte((String) row.get(CHAMP_NUM_COMPTE));
			BigDecimal rib = (BigDecimal) row.get(CHAMP_RIB);
			a.setRib(rib == null ? null : rib.intValue());
			a.setIntituleCompte((String) row.get(CHAMP_INTITULE_COMPTE));
			a.setVcat((String) row.get(CHAMP_VCAT));
			a.setDebutService((Date) row.get(CHAMP_DEBUT_SERVICE));
			a.setFinService((Date) row.get(CHAMP_FIN_SERVICE));
			a.setNumCafat((String) row.get(CHAMP_NUM_CAFAT));
			a.setNumRuamm((String) row.get(CHAMP_NUM_RUAMM));
			a.setNumMutuelle((String) row.get(CHAMP_NUM_MUTUELLE));
			a.setNumCre((String) row.get(CHAMP_NUM_CRE));
			a.setNumIrcafex((String) row.get(CHAMP_NUM_IRCAFEX));
			a.setNumClr((String) row.get(CHAMP_NUM_CLR));
			a.setCodeElection((String) row.get(CHAMP_CODE_ELECTION));
			a.setRueNonNoumea((String) row.get(CHAMP_RUE_NON_NOUMEA));
			liste.add(a);
		}
		return liste;
	}

	private int recupMaxNomatr() throws Exception {
		String sql = "select max(" + CHAMP_NOMATR + ") from " + NOM_TABLE + " where " + CHAMP_NOMATR + "<9000 ";

		Integer doc = (Integer) jdbcTemplate.queryForObject(sql, Integer.class);
		return doc;
	}

	@Override
	public void creerAgent(Transaction aTransaction, Agent agent, ArrayList<Contact> lContact,
			SituationFamiliale situFam) throws Exception {

		if (agent.getNomatr() == null) {
			// Affectation du matricule et de l'id
			agent.setNomatr(recupMaxNomatr() + 1);
			agent.setIdAgent(Integer.valueOf(EnumCollectivite.MAIRIE.getCode()
					+ Services.lpad(agent.getNomatr().toString(), 5, "0")));
			// Creation du Agent
			creerAgentBD(agent);

			Connecteur.creerSPPERS(aTransaction, agent, lContact, situFam);
		}

		// Modification du Agent
		modifierAgent(aTransaction, agent, lContact, situFam);
	}

	@Override
	public void modifierAgent(Transaction aTransaction, Agent agent, ArrayList<Contact> lContact,
			SituationFamiliale situFam) throws Exception {
		// Modification du Agent
		modifierAgentBD(agent);

		Connecteur.modifierSPPERS(aTransaction, agent, lContact, situFam);

	}

	private void creerAgentBD(Agent agent) {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_ID + "," + CHAMP_ID_VOIE + "," + CHAMP_ID_COLLECTIVITE
				+ "," + CHAMP_ID_SITUATION_FAMILIALEE + "," + CHAMP_ID_ETAT_SERVICE + "," + CHAMP_CPOS_VILLE_DOM + ","
				+ CHAMP_CCOM_VILLE_DOM + "," + CHAMP_CPOS_VILLE_BP + "," + CHAMP_CCOM_VILLE_BP + "," + CHAMP_NOMATR
				+ "," + CHAMP_NOM_MARITAL + "," + CHAMP_PRENOM + "," + CHAMP_PRENOM_USAGE + "," + CHAMP_CIVILITE + ","
				+ CHAMP_NOM_PATRONYMIQUE + "," + CHAMP_NOM_USAGE + "," + CHAMP_DATE_NAISSANCE + "," + CHAMP_DATE_DECES
				+ "," + CHAMP_SEXE + "," + CHAMP_DATE_PREMIERE_EMBAUCHE + "," + CHAMP_DATE_DERNIERE_EMBAUCHE + ","
				+ CHAMP_NATIONALITE + "," + CHAMP_CODE_PAYS_NAISS_ET + "," + CHAMP_CODE_COMMUNE_NAISS_ET + ","
				+ CHAMP_CODE_COMMUNE_NAISS_FR + "," + CHAMP_NUM_CARTE_SEJOUR + "," + CHAMP_DATE_VALIDITE_CARTE_SEJOUR
				+ "," + CHAMP_NUM_RUE + "," + CHAMP_NUM_RUE_BIS_TER + "," + CHAMP_ADRESSE_COMPLEMENTAIRE + ","
				+ CHAMP_BP + "," + CHAMP_CD_BANQUE + "," + CHAMP_CD_GUICHET + "," + CHAMP_NUM_COMPTE + "," + CHAMP_RIB
				+ "," + CHAMP_INTITULE_COMPTE + "," + CHAMP_VCAT + "," + CHAMP_DEBUT_SERVICE + "," + CHAMP_FIN_SERVICE
				+ "," + CHAMP_NUM_CAFAT + "," + CHAMP_NUM_RUAMM + "," + CHAMP_NUM_MUTUELLE + "," + CHAMP_NUM_CRE + ","
				+ CHAMP_NUM_IRCAFEX + "," + CHAMP_NUM_CLR + "," + CHAMP_CODE_ELECTION + "," + CHAMP_RUE_NON_NOUMEA
				+ ") " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" + ",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
				+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(
				sql,
				new Object[] { agent.getIdAgent(), agent.getIdVoie(), agent.getIdCollectivite(),
						agent.getIdSituationFamiliale(), agent.getIdEtatService(), agent.getCposVilleDom(),
						agent.getCcomVilleDom(), agent.getCposVilleBp(), agent.getCcomVilleBp(), agent.getNomatr(),
						agent.getNomMarital(), agent.getPrenom(), agent.getPrenomUsage(), agent.getCivilite(),
						agent.getNomPatronymique(), agent.getNomUsage(), agent.getDateNaissance(),
						agent.getDateDeces(), agent.getSexe(), agent.getDatePremiereEmbauche(),
						agent.getDateDerniereEmbauche(), agent.getNationalite(), agent.getCodePaysNaissEt(),
						agent.getCodeCommuneNaissEt(), agent.getCodeCommuneNaissFr(), agent.getNumCarteSejour(),
						agent.getDateValiditeCarteSejour(), agent.getNumRue(), agent.getNumRueBisTer(),
						agent.getAdresseComplementaire(), agent.getBp(), agent.getCdBanque(), agent.getCdGuichet(),
						agent.getNumCompte(), agent.getRib(), agent.getIntituleCompte(), agent.getVcat(),
						agent.getDebutService(), agent.getFinService(), agent.getNumCafat(), agent.getNumRuamm(),
						agent.getNumMutuelle(), agent.getNumCre(), agent.getNumIrcafex(), agent.getNumClr(),
						agent.getCodeElection(), agent.getRueNonNoumea() });
	}

	private void modifierAgentBD(Agent agent) {

		String sql = "UPDATE " + NOM_TABLE + " set " + CHAMP_ID_VOIE + "=?," + CHAMP_ID_COLLECTIVITE + "=?,"
				+ CHAMP_ID_SITUATION_FAMILIALEE + "=?," + CHAMP_ID_ETAT_SERVICE + "=?," + CHAMP_CPOS_VILLE_DOM + "=? ,"
				+ CHAMP_CCOM_VILLE_DOM + "=?" + " ," + CHAMP_CPOS_VILLE_BP + "=? ," + CHAMP_CCOM_VILLE_BP + "=? ,"
				+ CHAMP_NOMATR + "=? ," + CHAMP_NOM_MARITAL + "=? ," + CHAMP_PRENOM + "=? ," + CHAMP_PRENOM_USAGE
				+ "=? " + "," + CHAMP_CIVILITE + "=? ," + CHAMP_NOM_PATRONYMIQUE + "=? ," + CHAMP_NOM_USAGE + "=? ,"
				+ CHAMP_DATE_NAISSANCE + "=?," + CHAMP_DATE_DECES + "=?," + CHAMP_SEXE + "=?,"
				+ CHAMP_DATE_PREMIERE_EMBAUCHE + "=?," + CHAMP_DATE_DERNIERE_EMBAUCHE + "=?" + "," + CHAMP_NATIONALITE
				+ "=?," + CHAMP_CODE_PAYS_NAISS_ET + "=?," + CHAMP_CODE_COMMUNE_NAISS_ET + "=?,"
				+ CHAMP_CODE_COMMUNE_NAISS_FR + "=?," + CHAMP_NUM_CARTE_SEJOUR + "=?" + ","
				+ CHAMP_DATE_VALIDITE_CARTE_SEJOUR + "=?," + CHAMP_NUM_RUE + "=?," + CHAMP_NUM_RUE_BIS_TER + "=?,"
				+ CHAMP_ADRESSE_COMPLEMENTAIRE + "=?," + CHAMP_BP + "=?" + "," + CHAMP_CD_BANQUE + "=?,"
				+ CHAMP_CD_GUICHET + "=?," + CHAMP_NUM_COMPTE + "=?," + CHAMP_RIB + "=?," + CHAMP_INTITULE_COMPTE
				+ "=?," + CHAMP_VCAT + "=?," + CHAMP_DEBUT_SERVICE + "=?," + CHAMP_FIN_SERVICE + "=? ,"
				+ CHAMP_NUM_CAFAT + "=? ," + CHAMP_NUM_RUAMM + "=? ," + CHAMP_NUM_MUTUELLE + "=? ," + CHAMP_NUM_CRE
				+ "=? ," + CHAMP_NUM_IRCAFEX + "=? ," + CHAMP_NUM_CLR + "=? ," + CHAMP_CODE_ELECTION + "=? ,"
				+ CHAMP_RUE_NON_NOUMEA + "=? where " + CHAMP_ID + " =?";

		jdbcTemplate.update(
				sql,
				new Object[] { agent.getIdVoie(), agent.getIdCollectivite(), agent.getIdSituationFamiliale(),
						agent.getIdEtatService(), agent.getCposVilleDom(), agent.getCcomVilleDom(),
						agent.getCposVilleBp(), agent.getCcomVilleBp(), agent.getNomatr(), agent.getNomMarital(),
						agent.getPrenom(), agent.getPrenomUsage(), agent.getCivilite(), agent.getNomPatronymique(),
						agent.getNomUsage(), agent.getDateNaissance(), agent.getDateDeces(), agent.getSexe(),
						agent.getDatePremiereEmbauche(), agent.getDateDerniereEmbauche(), agent.getNationalite(),
						agent.getCodePaysNaissEt(), agent.getCodeCommuneNaissEt(), agent.getCodeCommuneNaissFr(),
						agent.getNumCarteSejour(), agent.getDateValiditeCarteSejour(), agent.getNumRue(),
						agent.getNumRueBisTer(), agent.getAdresseComplementaire(), agent.getBp(), agent.getCdBanque(),
						agent.getCdGuichet(), agent.getNumCompte(), agent.getRib(), agent.getIntituleCompte(),
						agent.getVcat(), agent.getDebutService(), agent.getFinService(), agent.getNumCafat(),
						agent.getNumRuamm(), agent.getNumMutuelle(), agent.getNumCre(), agent.getNumIrcafex(),
						agent.getNumClr(), agent.getCodeElection(), agent.getRueNonNoumea(), agent.getIdAgent() });
	}
}
