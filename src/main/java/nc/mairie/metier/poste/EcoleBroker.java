package nc.mairie.metier.poste;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Ecole
 */
public class EcoleBroker extends BasicBroker {
	/**
	 * Constructeur EcoleBroker.
	 */
	public EcoleBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return EcoleMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Ecole();
	}

	/**
	 * @return EcoleMetier
	 */
	protected Ecole getMyEcole() {
		return (Ecole) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPECOL";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CDECOL", new BasicRecord("CDECOL", "NUMERIC", getMyEcole().getClass().getField("cdecol"), "String"));
		mappage.put("LIECOL", new BasicRecord("LIECOL", "CHAR", getMyEcole().getClass().getField("liecol"), "String"));
		mappage.put("QUECOL", new BasicRecord("QUECOL", "CHAR", getMyEcole().getClass().getField("quecol"), "String"));
		mappage.put("SECTER", new BasicRecord("SECTER", "CHAR", getMyEcole().getClass().getField("secter"), "String"));
		return mappage;
	}

	/**
	 * Methode creerObjetMetierBroker qui retourne
	 * true ou false
	 */
	public boolean creerEcole(Transaction aTransaction) throws Exception {
		return creer(aTransaction);
	}

	/**
	 * Methode supprimerObjetMetierBroker qui retourne
	 * true ou false
	 */
	public boolean supprimerEcole(Transaction aTransaction) throws Exception {
		return supprimer(aTransaction);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Ecole.
	 * @return ArrayList
	 */
	public ArrayList<Ecole> listerEcole(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " order by CDECOL" + " WITH UR");
	}

	/**
	 * Retourne un Ecole.
	 * @return Ecole
	 */
	public Ecole chercherEcole(Transaction aTransaction, String cle) throws Exception {
		return (Ecole) executeSelect(aTransaction, "select * from " + getTable() + " where CDECOL = " + cle + " WITH UR");
	}

	/**
	 * Methode modifierObjetMetierBroker qui retourne
	 * true ou false
	 */
	public boolean modifierEcole(Transaction aTransaction) throws Exception {
		return modifier(aTransaction);
	}
}
