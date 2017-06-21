package nc.mairie.metier.agent;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Prime
 */
public class PrimeBroker extends BasicBroker {
	/**
	 * Methode creerObjetMetierBroker qui retourne true ou false
	 */
	public boolean creerPrime(Transaction aTransaction) throws Exception {
		return creer(aTransaction);
	}

	/**
	 * Methode modifierObjetMetierBroker qui retourne true ou false
	 */
	public boolean modifierPrime(Transaction aTransaction) throws Exception {
		return modifier(aTransaction);
	}

	/**
	 * Methode supprimerObjetMetierBroker qui retourne true ou false
	 */
	public boolean supprimerPrime(Transaction aTransaction) throws Exception {
		return supprimer(aTransaction);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Prime.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Prime> listerPrime(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WITH UR ");
	}

	/**
	 * Retourne un ArrayList d'objet metier : Prime.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Prime> listerPrimeAvecAgent(Transaction aTransaction, String nomatr) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " MP  WHERE MP.NOMATR = " + nomatr + " ORDER BY MP.NORUBR, MP.DATDEB WITH UR ");
	}

	public Prime chercherDernierePrimeOuverteAvecRubrique(Transaction aTransaction, String noMatricule, String noRubr) throws Exception {
		return (Prime) executeSelect(aTransaction, "select * from " + getTable() + " where nomatr = " + noMatricule + " and NORUBR=" + noRubr + " and datfin = 0  WITH UR ");
	}

	/**
	 * Constructeur PrimeBroker.
	 */
	public PrimeBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return PrimeMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Prime();
	}

	/**
	 * @return PrimeMetier
	 */
	protected Prime getMyPrime() {
		return (Prime) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPPRIM";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("NOMATR", new BasicRecord("NOMATR", "NUMERIC", getMyPrime().getClass().getField("noMatr"), "String"));
		mappage.put("NORUBR", new BasicRecord("NORUBR", "NUMERIC", getMyPrime().getClass().getField("noRubr"), "String"));
		mappage.put("REFARR", new BasicRecord("REFARR", "NUMERIC", getMyPrime().getClass().getField("refArr"), "String"));
		mappage.put("DATDEB", new BasicRecord("DATDEB", "NUMERIC", getMyPrime().getClass().getField("datDeb"), "DATE"));
		mappage.put("DATFIN", new BasicRecord("DATFIN", "NUMERIC", getMyPrime().getClass().getField("datFin"), "DATE"));
		mappage.put("MTPRI", new BasicRecord("MTPRI", "DECIMAL", getMyPrime().getClass().getField("mtPri"), "String"));
		mappage.put("DATARR", new BasicRecord("DATARR", "NUMERIC", getMyPrime().getClass().getField("dateArrete"), "DATE"));
		return mappage;
	}

	public Prime chercherPrime1200ByRubrAndDate(Transaction aTransaction, String noMatricule, String date) throws Exception {
		return (Prime) executeSelect(aTransaction, "select * from " + getTable() + " where nomatr = " + noMatricule + " and NORUBR=1200 and datdeb = " + date + "  WITH UR ");
	}

	public List<Prime> listerPrime1200ByAgent(Transaction aTransaction, String noMatricule) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " where NORUBR=1200 and nomatr = " + noMatricule + " WITH UR ");
	}
}
