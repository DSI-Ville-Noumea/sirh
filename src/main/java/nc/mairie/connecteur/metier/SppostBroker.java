package nc.mairie.connecteur.metier;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Sppost
 */
public class SppostBroker extends BasicBroker {
	/**
	 * Constructeur SppostBroker.
	 */
	public SppostBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return SppostMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Sppost();
	}

	/**
	 * @return SppostMetier
	 */
	protected Sppost getMySppost() {
		return (Sppost) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPPOST";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("POANNE", new BasicRecord("POANNE", "NUMERIC", getMySppost().getClass().getField("poanne"),
				"String"));
		mappage.put("PONUOR", new BasicRecord("PONUOR", "NUMERIC", getMySppost().getClass().getField("ponuor"),
				"String"));
		mappage.put("CDLIEU", new BasicRecord("CDLIEU", "DECIMAL", getMySppost().getClass().getField("cdlieu"),
				"String"));
		mappage.put("POSERV", new BasicRecord("POSERV", "CHAR", getMySppost().getClass().getField("poserv"), "String"));
		mappage.put("CTITRE", new BasicRecord("CTITRE", "NUMERIC", getMySppost().getClass().getField("ctitre"),
				"String"));
		mappage.put("POETUD", new BasicRecord("POETUD", "NUMERIC", getMySppost().getClass().getField("poetud"),
				"String"));
		mappage.put("CRESPO", new BasicRecord("CRESPO", "NUMERIC", getMySppost().getClass().getField("crespo"),
				"String"));
		mappage.put("POGRAD", new BasicRecord("POGRAD", "CHAR", getMySppost().getClass().getField("pograd"), "String"));
		mappage.put("POMIS1", new BasicRecord("POMIS1", "VARCHAR", getMySppost().getClass().getField("pomis1"),
				"String"));
		mappage.put("POMIS2", new BasicRecord("POMIS2", "VARCHAR", getMySppost().getClass().getField("pomis2"),
				"String"));
		mappage.put("POMIS3", new BasicRecord("POMIS3", "VARCHAR", getMySppost().getClass().getField("pomis3"),
				"String"));
		mappage.put("POMIS4", new BasicRecord("POMIS4", "VARCHAR", getMySppost().getClass().getField("pomis4"),
				"String"));
		mappage.put("POMATR", new BasicRecord("POMATR", "NUMERIC", getMySppost().getClass().getField("pomatr"),
				"String"));
		mappage.put("POCOND", new BasicRecord("POCOND", "CHAR", getMySppost().getClass().getField("pocond"), "String"));
		mappage.put("PODVAL", new BasicRecord("PODVAL", "NUMERIC", getMySppost().getClass().getField("podval"),
				"String"));
		mappage.put("PODSUP", new BasicRecord("PODSUP", "NUMERIC", getMySppost().getClass().getField("podsup"),
				"String"));
		mappage.put("POSERP", new BasicRecord("POSERP", "CHAR", getMySppost().getClass().getField("poserp"), "String"));
		mappage.put("CODFON", new BasicRecord("CODFON", "CHAR", getMySppost().getClass().getField("codfon"), "String"));
		mappage.put("CODACT", new BasicRecord("CODACT", "CHAR", getMySppost().getClass().getField("codact"), "String"));
		mappage.put("NOACTI", new BasicRecord("NOACTI", "CHAR", getMySppost().getClass().getField("noacti"), "String"));
		mappage.put("REGLEM", new BasicRecord("REGLEM", "NUMERIC", getMySppost().getClass().getField("reglem"),
				"String"));
		mappage.put("COMMEN", new BasicRecord("COMMEN", "CHAR", getMySppost().getClass().getField("commen"), "String"));
		mappage.put("BUDGET", new BasicRecord("BUDGET", "NUMERIC", getMySppost().getClass().getField("budget"),
				"String"));
		mappage.put("SOANNE", new BasicRecord("SOANNE", "NUMERIC", getMySppost().getClass().getField("soanne"),
				"String"));
		mappage.put("SONUOR", new BasicRecord("SONUOR", "NUMERIC", getMySppost().getClass().getField("sonuor"),
				"String"));
		mappage.put("PRIMAIRE", new BasicRecord("PRIMAIRE", "NUMERIC", getMySppost().getClass().getField("primaire"),
				"String"));
		return mappage;
	}

	/**
	 * Methode creerObjetMetierBroker qui retourne true ou false
	 */
	public boolean creerSppost(Transaction aTransaction) throws Exception {
		return creer(aTransaction);
	}

	/**
	 * Methode modifierObjetMetierBroker qui retourne true ou false
	 */
	public boolean modifierSppost(Transaction aTransaction) throws Exception {
		return modifier(aTransaction);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Sppost.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Sppost> listerSppost(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WITH UR");
	}

	/**
	 * Retourne un Sppost.
	 * 
	 * @return Sppost
	 */
	public Sppost chercherSppost(Transaction aTransaction, String poanne, String ponuor) throws Exception {
		return (Sppost) executeSelect(aTransaction, "select * from " + getTable() + " where POANNE = " + poanne
				+ " and PONUOR=" + ponuor + " WITH UR");
	}

	public boolean supprimerSppost(Transaction aTransaction) throws Exception {
		return supprimer(aTransaction);
	}
}
