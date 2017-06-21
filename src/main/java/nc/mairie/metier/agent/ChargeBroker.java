package nc.mairie.metier.agent;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Charge
 */
public class ChargeBroker extends BasicBroker {
	/**
	 * Constructeur ChargeBroker.
	 */
	public ChargeBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return ChargeMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Charge();
	}

	/**
	 * @return ChargeMetier
	 */
	protected Charge getMyCharge() {
		return (Charge) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPCHGE";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("NOMATR", new BasicRecord("NOMATR", "NUMERIC", getMyCharge().getClass().getField("noMatr"),
				"String"));
		mappage.put("NORUBR", new BasicRecord("NORUBR", "NUMERIC", getMyCharge().getClass().getField("noRubr"),
				"String"));
		mappage.put("CDCREA", new BasicRecord("CDCREA", "NUMERIC", getMyCharge().getClass().getField("cdCrea"),
				"String"));
		mappage.put("NOMATE", new BasicRecord("NOMATE", "CHAR", getMyCharge().getClass().getField("noMate"), "String"));
		mappage.put("CDCHAR", new BasicRecord("CDCHAR", "NUMERIC", getMyCharge().getClass().getField("cdChar"),
				"String"));
		mappage.put("TXSAL", new BasicRecord("TXSAL", "DECIMAL", getMyCharge().getClass().getField("txSal"), "String"));
		mappage.put("MTTREG", new BasicRecord("MTTREG", "DECIMAL", getMyCharge().getClass().getField("mttreg"),
				"String"));
		mappage.put("DATDEB", new BasicRecord("DATDEB", "NUMERIC", getMyCharge().getClass().getField("datDeb"), "DATE"));
		mappage.put("DATFIN", new BasicRecord("DATFIN", "NUMERIC", getMyCharge().getClass().getField("datFin"), "DATE"));
		return mappage;
	}

	/**
	 * Methode creerObjetMetierBroker qui retourne true ou false
	 */
	public boolean creerCharge(Transaction aTransaction) throws Exception {
		return creer(aTransaction);
	}

	/**
	 * Methode modifierObjetMetierBroker qui retourne true ou false
	 */
	public boolean modifierCharge(Transaction aTransaction) throws Exception {
		return modifier(aTransaction);
	}

	/**
	 * Methode supprimerObjetMetierBroker qui retourne true ou false
	 */
	public boolean supprimerCharge(Transaction aTransaction) throws Exception {
		return supprimer(aTransaction);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Charge.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Charge> listerCharge(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WITH UR ");
	}

	/**
	 * Retourne un Charge.
	 * 
	 * @return Charge
	 */
	public Charge chercherCharge(Transaction aTransaction, String nomatr, String norubr, String datdeb)
			throws Exception {
		return (Charge) executeSelect(aTransaction, "select * from " + getTable() + " where nomatr = " + nomatr
				+ " and norubr=" + norubr + " and datdeb=" + datdeb + " WITH UR ");
	}

	public ArrayList<Charge> listerChargeAvecAgent(Transaction aTransaction, Integer noMatricule) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " MP  WHERE MP.NOMATR = " + noMatricule
				+ " ORDER BY MP.NORUBR, MP.DATDEB WITH UR ");
	}
}
