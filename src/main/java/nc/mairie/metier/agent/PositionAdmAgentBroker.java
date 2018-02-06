package nc.mairie.metier.agent;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Services;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier PositionAdmAgent
 */
public class PositionAdmAgentBroker extends BasicBroker {
	/**
	 * Constructeur PositionAdmAgentBroker.
	 */
	public PositionAdmAgentBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return PositionAdmAgentMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new PositionAdmAgent();
	}

	/**
	 * @return PositionAdmAgentMetier
	 */
	protected PositionAdmAgent getMyPositionAdmAgent() {
		return (PositionAdmAgent) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPADMN";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("NOMATR", new BasicRecord("NOMATR", "NUMERIC", getMyPositionAdmAgent().getClass()
				.getField("nomatr"), "String"));
		mappage.put("DATDEB", new BasicRecord("DATDEB", "NUMERIC", getMyPositionAdmAgent().getClass()
				.getField("datdeb"), "DATE"));
		mappage.put("CDPADM", new BasicRecord("CDPADM", "CHAR", getMyPositionAdmAgent().getClass().getField("cdpadm"),
				"String"));
		mappage.put("DATFIN", new BasicRecord("DATFIN", "NUMERIC", getMyPositionAdmAgent().getClass()
				.getField("datfin"), "DATE"));
		mappage.put("REFARR", new BasicRecord("REFARR", "NUMERIC", getMyPositionAdmAgent().getClass()
				.getField("refarr"), "String"));
		mappage.put("DATARR",
				new BasicRecord("DATARR", "NUMERIC", getMyPositionAdmAgent().getClass().getField("dateArrete"), "DATE"));
		return mappage;
	}

	/**
	 * Methode creerObjetMetierBroker qui retourne true ou false
	 */
	public boolean creerPositionAdmAgent(Transaction aTransaction) throws Exception {
		return creer(aTransaction);
	}

	/**
	 * Methode modifierObjetMetierBroker qui retourne true ou false
	 */
	public boolean modifierPositionAdmAgent(Transaction aTransaction) throws Exception {
		return modifier(aTransaction);
	}

	/**
	 * Methode supprimerObjetMetierBroker qui retourne true ou false
	 */
	public boolean supprimerPositionAdmAgent(Transaction aTransaction) throws Exception {
		return supprimer(aTransaction);
	}

	/**
	 * Retourne un PositionAdmAgent.
	 * 
	 * @return PositionAdmAgent
	 */
	public PositionAdmAgent chercherPositionAdmAgent(Transaction aTransaction, String nomatr, String datedeb)
			throws Exception {
		return (PositionAdmAgent) executeSelect(aTransaction, "select * from " + getTable() + " where nomatr = "
				+ nomatr + " and datdeb=" + datedeb + " WITH UR ");
	}

	/**
	 * Retourne la PositionAdmAgent de l'agent la plus recente.
	 * 
	 * @return PositionAdmAgent
	 */
	public PositionAdmAgent chercherDernierePositionAdmAgentAvecAgent(Transaction aTransaction, Agent agent)
			throws Exception {
		return (PositionAdmAgent) executeSelect(aTransaction, "select pa.* from " + getTable()
				+ " pa where pa.nomatr = " + agent.getNomatr() + " AND pa.DATDEB = (SELECT MAX(pa2.DATDEB) FROM "
				+ getTable() + " pa2 WHERE pa2.NOMATR = " + agent.getNomatr() + ")  WITH UR ");
	}

	/**
	 * Retourne la PositionAdmAgent precedant celle passee en parametre.
	 * 
	 * @param aTransaction
	 * @param nomatr
	 * @param datedeb
	 * @return PositionAdmAgent
	 * @throws Exception
	 */
	public PositionAdmAgent chercherPositionAdmAgentPrec(Transaction aTransaction, String nomatr, String datedeb)
			throws Exception {
		return (PositionAdmAgent) executeSelect(aTransaction, "select * from " + getTable() + " where nomatr = "
				+ nomatr + " and datdeb in (select max(datdeb) from " + getTable() + " where datdeb<" + datedeb
				+ " and nomatr=" + nomatr + ")" + " WITH UR ");
	}

	/**
	 * Retourne la PositionAdmAgent suivant celle passee en parametre.
	 * 
	 * @param aTransaction
	 * @param nomatr
	 * @param codepa
	 * @param datedeb
	 * @return PositionAdmAgent
	 * @throws Exception
	 */
	public PositionAdmAgent chercherPositionAdmAgentSuiv(Transaction aTransaction, String nomatr, String codepa,
			String datedeb) throws Exception {
		return (PositionAdmAgent) executeSelect(aTransaction, "select * from " + getTable() + " where nomatr = "
				+ nomatr + " and cdpadm='" + codepa + "' and datdeb in (select min(datdeb) from " + getTable()
				+ " where datdeb>" + datedeb + ")" + " WITH UR ");
	}

	public PositionAdmAgent chercherPositionAdmAgentSuiv(Transaction aTransaction, String nomatr, String datedeb) throws Exception {
		return (PositionAdmAgent) executeSelect(aTransaction, "select * from " + getTable() + " where nomatr = "
				+ nomatr + " and datdeb in (select min(datdeb) from " + getTable()
				+ " where datdeb>" + datedeb + " and nomatr = " + nomatr + " )" + " WITH UR ");
	}

	public PositionAdmAgent chercherPositionAdmAgentDateComprise(Transaction aTransaction, String noMatricule,
			String date) throws Exception {
		return (PositionAdmAgent) executeSelect(aTransaction, "select * from " + getTable() + " where nomatr = "
				+ noMatricule + " and (('" + date + "' >= DATDEB and '" + date + "' < DATFIN) or ('" + date
				+ "'>=datdeb and datfin=0)) WITH UR ");
	}

	public PositionAdmAgent chercherPositionAdmAgentDateFinExclu(Transaction aTransaction, String noMatricule,
			String date) throws Exception {
		return (PositionAdmAgent) executeSelect(aTransaction, "select * from " + getTable() + " where nomatr = "
				+ noMatricule + " and (('" + date + "' between DATDEB and DATFIN-1) or ('" + date
				+ "'>=datdeb and datfin=0)) WITH UR ");
	}

	public ArrayList<PositionAdmAgent> listerPositionAdmAgentHorsEffectif(Transaction aTransaction, Integer moisChoisi,
			Integer anneeChoisi) throws Exception {
		String mois = moisChoisi.toString();
		if (mois.length() == 1) {
			mois = "0" + mois;
		}
		String sql = "select a.* from "
				+ getTable()
				+ " a where ((LENGTH(TRIM(TRANSLATE(a.cdpadm,' ', ' +-.0123456789')))!=0 and a.cdpadm!='FI') "
				+ "or (LENGTH(TRIM(TRANSLATE(a.cdpadm,' ', ' +-.0123456789')))=0 and (a.cdpadm='46' or a.cdpadm='47' or a.cdpadm='48' or a.cdpadm='49' or a.cdpadm='50' or a.cdpadm='51' or a.cdpadm='52' or a.cdpadm='53' or a.cdpadm='54' or a.cdpadm='56' or a.cdpadm='57' or a.cdpadm='58' or a.cdpadm='59' or a.cdpadm='67')))  "
				+ "and datfin!=0 and substr(a.DATFIN,1,4)=" + anneeChoisi + " and substr(a.DATFIN,5,2)=" + mois
				+ " WITH UR";
		return executeSelectListe(aTransaction, sql);

	}

	public PositionAdmAgent chercherPositionAdmAgentDateDebutFinComprise(Transaction transaction, String noMatricule,
			String datdeb, String datfin) throws Exception {
		return (PositionAdmAgent) executeSelect(transaction, "select * from " + getTable() + " where nomatr = "
				+ noMatricule + " and ((('" + datdeb + "' >= DATDEB and '" + datdeb + "' < DATFIN) or ('" + datdeb
				+ "'>=datdeb and datfin=0)) or ('" + datfin + "' < DATFIN or datfin=0)) WITH UR ");

	}

	public PositionAdmAgent chercherPositionAdmAgentEnCoursAvecAgent(Transaction aTransaction, String nomatr)
			throws Exception {
		String req = "select pa.* from " + getTable() + " pa where ("
				+ Services.formateDateInternationale(Services.dateDuJour()).replace("-", Const.CHAINE_VIDE)
				+ " between pa.DATDEB and pa.DATFIN or pa.DATDEB<="
				+ Services.formateDateInternationale(Services.dateDuJour()).replace("-", Const.CHAINE_VIDE)
				+ " and pa.DATFIN=0) and pa.nomatr = " + nomatr + " WITH UR ";
		return (PositionAdmAgent) executeSelect(aTransaction, req);
	}

	public PositionAdmAgent chercherPositionAdmAgentAncienne(Transaction aTransaction, String noMatricule)
			throws Exception {
		String sql = "select * from " + getTable() + " c where c.nomatr = " + noMatricule
				+ " and  c.datdeb = (select min(datdeb) from " + getTable() + " pa where pa.nomatr=c.nomatr) WITH UR";
		return (PositionAdmAgent) executeSelect(aTransaction, sql);
	}

	public PositionAdmAgent chercherPositionAdmAgentActive(Transaction aTransaction, String noMatricule)
			throws Exception {
		return (PositionAdmAgent) executeSelect(
				aTransaction,
				"select * from " + getTable() + " pa where pa.nomatr = " + noMatricule + " and ("
						+ Services.formateDateInternationale(Services.dateDuJour()).replace("-", Const.CHAINE_VIDE)
						+ " between pa.DATDEB and pa.DATFIN or pa.DATDEB<="
						+ Services.formateDateInternationale(Services.dateDuJour()).replace("-", Const.CHAINE_VIDE)
						+ " and pa.DATFIN=0) and LENGTH(TRIM(TRANSLATE(pa.cdpadm,' ', ' +-.0123456789')))=0 WITH UR ");
	}

	public ArrayList<PositionAdmAgent> listerPositionAdmAgentEnActivite(Transaction aTransaction) throws Exception {
		String sql = "select distinct pa.nomatr from " + getTable()
				+ " pa inner join spposa po  on pa.cdpadm = po.cdpadm  where (("
				+ Services.formateDateInternationale(Services.dateDuJour()).replace("-", Const.CHAINE_VIDE)
				+ " between pa.DATDEB and pa.DATFIN) or (pa.DATDEB<="
				+ Services.formateDateInternationale(Services.dateDuJour()).replace("-", Const.CHAINE_VIDE)
				+ " and pa.DATFIN=0)) and po.POSIT='AC' WITH UR";
		return executeSelectListe(aTransaction, sql);
	}

	public ArrayList<PositionAdmAgent> listerPositionAdmAgentAvecAgent(Transaction aTransaction, Integer noMatricule)
			throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " MP  WHERE MP.NOMATR = " + noMatricule
				+ " ORDER BY  MP.DATDEB WITH UR ");
	}
}
