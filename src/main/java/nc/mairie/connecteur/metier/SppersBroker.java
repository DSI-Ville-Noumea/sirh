package nc.mairie.connecteur.metier;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Sppers
 */
public class SppersBroker extends BasicBroker {
	/**
	 * Constructeur SppersBroker.
	 */
	public SppersBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return SppersMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Sppers();
	}

	/**
	 * @return SppersMetier
	 */
	protected Sppers getMySppers() {
		return (Sppers) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPPERS";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("IDINDI", new BasicRecord("IDINDI", "DECIMAL", getMySppers().getClass().getField("idindi"), "String"));
		mappage.put("NOMATR", new BasicRecord("NOMATR", "NUMERIC", getMySppers().getClass().getField("nomatr"), "String"));
		mappage.put("NOM", new BasicRecord("NOM", "CHAR", getMySppers().getClass().getField("nom"), "String"));
		mappage.put("PRENOM", new BasicRecord("PRENOM", "CHAR", getMySppers().getClass().getField("prenom"), "String"));
		mappage.put("NOMJFI", new BasicRecord("NOMJFI", "CHAR", getMySppers().getClass().getField("nomjfi"), "String"));
		mappage.put("DATNAI", new BasicRecord("DATNAI", "NUMERIC", getMySppers().getClass().getField("datnai"), "String"));
		mappage.put("LIEUNA", new BasicRecord("LIEUNA", "CHAR", getMySppers().getClass().getField("lieuna"), "String"));
		mappage.put("CDDESI", new BasicRecord("CDDESI", "CHAR", getMySppers().getClass().getField("cddesi"), "String"));
		mappage.put("SEXE", new BasicRecord("SEXE", "CHAR", getMySppers().getClass().getField("sexe"), "String"));
		mappage.put("NATION", new BasicRecord("NATION", "CHAR", getMySppers().getClass().getField("nation"), "String"));
		mappage.put("CDFAMI", new BasicRecord("CDFAMI", "CHAR", getMySppers().getClass().getField("cdfami"), "String"));
		mappage.put("NINSEE", new BasicRecord("NINSEE", "DECIMAL", getMySppers().getClass().getField("ninsee"), "String"));
		mappage.put("DATTIT", new BasicRecord("DATTIT", "NUMERIC", getMySppers().getClass().getField("dattit"), "String"));
		mappage.put("DATDEC", new BasicRecord("DATDEC", "NUMERIC", getMySppers().getClass().getField("datdec"), "String"));
		mappage.put("CDREGL", new BasicRecord("CDREGL", "NUMERIC", getMySppers().getClass().getField("cdregl"), "String"));
		mappage.put("IDADRS", new BasicRecord("IDADRS", "NUMERIC", getMySppers().getClass().getField("idadrs"), "String"));
		mappage.put("IDCPTE", new BasicRecord("IDCPTE", "NUMERIC", getMySppers().getClass().getField("idcpte"), "String"));
		mappage.put("TELDOM", new BasicRecord("TELDOM", "NUMERIC", getMySppers().getClass().getField("teldom"), "String"));
		mappage.put("NOPORT", new BasicRecord("NOPORT", "DECIMAL", getMySppers().getClass().getField("noport"), "String"));
		mappage.put("BISTER", new BasicRecord("BISTER", "CHAR", getMySppers().getClass().getField("bister"), "String"));
		mappage.put("LIDOPU", new BasicRecord("LIDOPU", "CHAR", getMySppers().getClass().getField("lidopu"), "String"));
		mappage.put("LIRUE", new BasicRecord("LIRUE", "CHAR", getMySppers().getClass().getField("lirue"), "String"));
		mappage.put("BP", new BasicRecord("BP", "CHAR", getMySppers().getClass().getField("bp"), "String"));
		mappage.put("LICARE", new BasicRecord("LICARE", "CHAR", getMySppers().getClass().getField("licare"), "String"));
		mappage.put("CDVILL", new BasicRecord("CDVILL", "DECIMAL", getMySppers().getClass().getField("cdvill"), "String"));
		mappage.put("LIVILL", new BasicRecord("LIVILL", "CHAR", getMySppers().getClass().getField("livill"), "String"));
		mappage.put("CDBANQ", new BasicRecord("CDBANQ", "DECIMAL", getMySppers().getClass().getField("cdbanq"), "String"));
		mappage.put("CDGUIC", new BasicRecord("CDGUIC", "DECIMAL", getMySppers().getClass().getField("cdguic"), "String"));
		mappage.put("NOCPTE", new BasicRecord("NOCPTE", "CHAR", getMySppers().getClass().getField("nocpte"), "String"));
		mappage.put("CLERIB", new BasicRecord("CLERIB", "NUMERIC", getMySppers().getClass().getField("clerib"), "String"));
		mappage.put("CDELEC", new BasicRecord("CDELEC", "CHAR", getMySppers().getClass().getField("cdelec"), "String"));
		mappage.put("DATEMB", new BasicRecord("DATEMB", "NUMERIC", getMySppers().getClass().getField("datemb"), "String"));
		mappage.put("CDETUD", new BasicRecord("CDETUD", "NUMERIC", getMySppers().getClass().getField("cdetud"), "String"));
		mappage.put("MOBPRIV", new BasicRecord("MOBPRIV", "NUMERIC", getMySppers().getClass().getField("mobpriv"), "String"));
		mappage.put("DIFFU1", new BasicRecord("DIFFU1", "CHAR", getMySppers().getClass().getField("diffu1"), "String"));
		mappage.put("DIFFU2", new BasicRecord("DIFFU2", "CHAR", getMySppers().getClass().getField("diffu2"), "String"));
		mappage.put("MOBPROF", new BasicRecord("MOBPROF", "NUMERIC", getMySppers().getClass().getField("mobprof"), "String"));
		mappage.put("PRENUS", new BasicRecord("PRENUS", "CHAR", getMySppers().getClass().getField("prenus"), "String"));
		return mappage;
	}

	/**
	 * Methode creerObjetMetierBroker qui retourne
	 * true ou false
	 */
	public boolean creerSppers(Transaction aTransaction) throws Exception {
		return creer(aTransaction);
	}

	/**
	 * Methode modifierObjetMetierBroker qui retourne
	 * true ou false
	 */
	public boolean modifierSppers(Transaction aTransaction) throws Exception {
		return modifier(aTransaction);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Sppers.
	 * @return ArrayList
	 */
	public ArrayList<Sppers> listerSppers(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WITH UR");
	}

	/**
	 * Retourne un Sppers.
	 * @return Sppers
	 */
	public Sppers chercherSppers(Transaction aTransaction, String noMatr) throws Exception {
		return (Sppers) executeSelect(aTransaction, "select * from " + getTable() + " where NOMATR = " + noMatr + " WITH UR");
	}
}
