package nc.mairie.connecteur.metier;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Spmtsr
 */
public class SpmtsrBroker extends BasicBroker {
	/**
	 * Constructeur SpmtsrBroker.
	 */
	public SpmtsrBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return SpmtsrMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Spmtsr();
	}

	/**
	 * @return SpmtsrMetier
	 */
	protected Spmtsr getMySpmtsr() {
		return (Spmtsr) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPMTSR";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("NOMATR", new BasicRecord("NOMATR", "NUMERIC", getMySpmtsr().getClass().getField("nomatr"),
				"String"));
		mappage.put("SERVI", new BasicRecord("SERVI", "CHAR", getMySpmtsr().getClass().getField("servi"), "String"));
		mappage.put("REFARR", new BasicRecord("REFARR", "NUMERIC", getMySpmtsr().getClass().getField("refarr"),
				"String"));
		mappage.put("DATDEB", new BasicRecord("DATDEB", "NUMERIC", getMySpmtsr().getClass().getField("datdeb"),
				"String"));
		mappage.put("DATFIN", new BasicRecord("DATFIN", "NUMERIC", getMySpmtsr().getClass().getField("datfin"),
				"String"));
		mappage.put("CDECOL", new BasicRecord("CDECOL", "NUMERIC", getMySpmtsr().getClass().getField("cdecol"),
				"String"));
		return mappage;
	}

	/**
	 * Methode creerObjetMetierBroker qui retourne true ou false
	 */
	public boolean creerSpmtsr(Transaction aTransaction) throws Exception {
		return creer(aTransaction);
	}

	/**
	 * Methode modifierObjetMetierBroker qui retourne true ou false
	 */
	public boolean modifierSpmtsr(Transaction aTransaction) throws Exception {
		return modifier(aTransaction);
	}

	/**
	 * Methode supprimerObjetMetierBroker qui retourne true ou false
	 */
	public boolean supprimerSpmtsr(Transaction aTransaction) throws Exception {
		return supprimer(aTransaction);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Spmtsr.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Spmtsr> listerSpmtsr(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WITH UR");
	}

	/**
	 * Retourne un Spmtsr.
	 * 
	 * @return Spmtsr
	 */
	public Spmtsr chercherSpmtsr(Transaction aTransaction, String cle) throws Exception {
		return (Spmtsr) executeSelect(aTransaction, "select * from " + getTable() + " where CODE = " + cle + " WITH UR");
	}

	/**
	 * Retourne un SPMTSR a partir d'un numero de matricule et une
	 * date de debut
	 * 
	 * @param aTransaction
	 * @param noMatr
	 *            Numero de matricule
	 * @param dateDeb
	 *            Date de debut de l'affectation
	 * @return SpmTsr
	 * @throws Exception
	 */
	public Spmtsr chercherSpmtsrAvecNoMatrDateDeb(Transaction aTransaction, String noMatr, String dateDeb)
			throws Exception {
		return (Spmtsr) executeSelect(aTransaction, "select * from " + getTable() + " where NOMATR = " + noMatr
				+ " AND DATDEB = " + dateDeb + " WITH UR");
	}

	public Spmtsr chercherSpmtsrSansDatFin(Transaction aTransaction, String noMatr) throws Exception {
		return (Spmtsr) executeSelect(aTransaction, "select * from " + getTable() + " where NOMATR = " + noMatr
				+ "  AND DATFIN = 0 WITH UR");
	}

	public ArrayList<Spmtsr> listerSpmtsrAvecAgentOrderDateDeb(Transaction aTransaction, String noMatricule)
			throws Exception {
		String req = "select * from " + getTable() + " where NOMATR = " + noMatricule + " order by DATDEB WITH UR";
		return executeSelectListe(aTransaction, req);
	}

	public Spmtsr chercherSpmtsrAvecAgentEtDateDebut(Transaction aTransaction, String noMatricule, Integer datefin)
			throws Exception {
		String req = "select * from " + getTable() + " where NOMATR = " + noMatricule + " AND DATDEB = " + datefin
				+ "  WITH UR";
		return (Spmtsr) executeSelect(aTransaction, req);
	}

	public ArrayList<Spmtsr> listerSpmtsrAvecAgentAPartirDateOrderDateDeb(Transaction aTransaction, String noMatricule,
			Integer date) throws Exception {
		String req = "select * from " + getTable() + " where NOMATR = " + noMatricule + " and DATDEB < " + date
				+ " order by DATDEB WITH UR";
		return executeSelectListe(aTransaction, req);
	}
}
