package nc.mairie.connecteur.metier;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Sprens
 */
public class SprensBroker extends BasicBroker {
	/**
	 * Constructeur SprensBroker.
	 */
	public SprensBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return SprensMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Sprens();
	}

	/**
	 * @return SprensMetier
	 */
	protected Sprens getMySprens() {
		return (Sprens) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPRENS";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("NOMATR", new BasicRecord("NOMATR", "NUMERIC", getMySprens().getClass().getField("nomatr"), "String"));
		mappage.put("NOMP", new BasicRecord("NOMP", "CHAR", getMySprens().getClass().getField("nomp"), "String"));
		mappage.put("PREP", new BasicRecord("PREP", "CHAR", getMySprens().getClass().getField("prep"), "String"));
		mappage.put("NOMM", new BasicRecord("NOMM", "CHAR", getMySprens().getClass().getField("nomm"), "String"));
		mappage.put("PREM", new BasicRecord("PREM", "CHAR", getMySprens().getClass().getField("prem"), "String"));
		mappage.put("NOMC", new BasicRecord("NOMC", "CHAR", getMySprens().getClass().getField("nomc"), "String"));
		mappage.put("PREC", new BasicRecord("PREC", "CHAR", getMySprens().getClass().getField("prec"), "String"));
		mappage.put("MATC", new BasicRecord("MATC", "NUMERIC", getMySprens().getClass().getField("matc"), "String"));
		mappage.put("DATMAR", new BasicRecord("DATMAR", "NUMERIC", getMySprens().getClass().getField("datmar"), "String"));
		mappage.put("PREV", new BasicRecord("PREV", "CHAR", getMySprens().getClass().getField("prev"), "String"));
		mappage.put("OBS1", new BasicRecord("OBS1", "CHAR", getMySprens().getClass().getField("obs1"), "String"));
		mappage.put("OBS2", new BasicRecord("OBS2", "CHAR", getMySprens().getClass().getField("obs2"), "String"));
		mappage.put("NBSCOL", new BasicRecord("NBSCOL", "NUMERIC", getMySprens().getClass().getField("nbscol"), "String"));
		mappage.put("CDCNAI", new BasicRecord("CDCNAI", "NUMERIC", getMySprens().getClass().getField("cdcnai"), "String"));
		mappage.put("CDCHAB", new BasicRecord("CDCHAB", "NUMERIC", getMySprens().getClass().getField("cdchab"), "String"));
		mappage.put("CODEVP", new BasicRecord("CODEVP", "CHAR", getMySprens().getClass().getField("codevp"), "String"));
		mappage.put("CODNAI", new BasicRecord("CODNAI", "NUMERIC", getMySprens().getClass().getField("codnai"), "String"));
		mappage.put("SCODPA", new BasicRecord("SCODPA", "NUMERIC", getMySprens().getClass().getField("scodpa"), "String"));
		return mappage;
	}

	/**
	 * Methode creerObjetMetierBroker qui retourne true ou false
	 */
	public boolean creerSprens(Transaction aTransaction) throws Exception {
		return creer(aTransaction);
	}

	/**
	 * Methode modifierObjetMetierBroker qui retourne true ou false
	 */
	public boolean modifierSprens(Transaction aTransaction) throws Exception {
		return modifier(aTransaction);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Sprens.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Sprens> listerSprens(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WITH UR");
	}

	/**
	 * Retourne un Sprens.
	 * 
	 * @return Sprens
	 */
	public Sprens chercherSprens(Transaction aTransaction, String noMatr) throws Exception {
		return (Sprens) executeSelect(aTransaction, "select * from " + getTable() + " where NOMATR = " + noMatr + " WITH UR");
	}
}
