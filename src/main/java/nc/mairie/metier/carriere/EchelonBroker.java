package nc.mairie.metier.carriere;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Echelon
 */
public class EchelonBroker extends BasicBroker {
	/**
	 * Constructeur EchelonBroker.
	 */
	public EchelonBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return EchelonMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Echelon();
	}

	/**
	 * @return EchelonMetier
	 */
	protected Echelon getMyEchelon() {
		return (Echelon) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPECHE";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CODECH", new BasicRecord("CODECH", "CHAR", getMyEchelon().getClass().getField("codEchelon"), "String"));
		mappage.put("LIBECH", new BasicRecord("LIBECH", "CHAR", getMyEchelon().getClass().getField("libEchelon"), "String"));
		return mappage;
	}

	/**
	 * Methode creerObjetMetierBroker qui retourne
	 * true ou false
	 */
	public boolean creerEchelon(Transaction aTransaction) throws Exception {
		return creer(aTransaction);
	}

	/**
	 * Methode modifierObjetMetierBroker qui retourne
	 * true ou false
	 */
	public boolean modifierEchelon(Transaction aTransaction) throws Exception {
		return modifier(aTransaction);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Echelon.
	 * @return ArrayList
	 */
	public ArrayList<Echelon> listerEchelon(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " ORDER BY CODECH WITH UR ");
	}

	/**
	 * Retourne un Echelon.
	 * @return Echelon
	 */
	public Echelon chercherEchelon(Transaction aTransaction, String cle) throws Exception {
		return (Echelon) executeSelect(aTransaction, "select * from " + getTable() + " where CODECH = '" + cle + "' WITH UR ");
	}
}
