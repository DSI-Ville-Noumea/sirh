package nc.mairie.metier.agent;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Creancier
 */
public class CreancierBroker extends BasicBroker {
	/**
	 * Constructeur CreancierBroker.
	 */
	public CreancierBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return CreancierMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Creancier();
	}

	/**
	 * @return CreancierMetier
	 */
	protected Creancier getMyCreancier() {
		return (Creancier) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPCREA";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CDCREA", new BasicRecord("CDCREA", "NUMERIC", getMyCreancier().getClass().getField("cdCrea"), "String"));
		mappage.put("DESIGN", new BasicRecord("DESIGN", "CHAR", getMyCreancier().getClass().getField("design"), "String"));
		mappage.put("CDBANQ", new BasicRecord("CDBANQ", "DECIMAL", getMyCreancier().getClass().getField("cdBanq"), "String"));
		mappage.put("CDGUIC", new BasicRecord("CDGUIC", "DECIMAL", getMyCreancier().getClass().getField("cdGuic"), "String"));
		mappage.put("NOCPTE", new BasicRecord("NOCPTE", "CHAR", getMyCreancier().getClass().getField("noCpte"), "String"));
		mappage.put("CLERIB", new BasicRecord("CLERIB", "NUMERIC", getMyCreancier().getClass().getField("cleRIB"), "String"));
		mappage.put("IDETBS", new BasicRecord("IDETBS", "NUMERIC", getMyCreancier().getClass().getField("idetbs"), "String"));
		return mappage;
	}

	/**
	 * Retourne un ArrayList d'objet metier : Creancier.
	 * @return ArrayList
	 */
	public ArrayList<Creancier> listerCreancier(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " order by DESIGN WITH UR ");
	}
}
