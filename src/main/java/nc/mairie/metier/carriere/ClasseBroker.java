package nc.mairie.metier.carriere;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Classe
 */
public class ClasseBroker extends BasicBroker {
	/**
	 * Constructeur ClasseBroker.
	 */
	public ClasseBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return ClasseMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Classe();
	}

	/**
	 * @return ClasseMetier
	 */
	protected Classe getMyClasse() {
		return (Classe) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPCLAS";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CODCLA", new BasicRecord("CODCLA", "CHAR", getMyClasse().getClass().getField("codClasse"), "String"));
		mappage.put("LIBCLA", new BasicRecord("LIBCLA", "CHAR", getMyClasse().getClass().getField("libClasse"), "String"));
		return mappage;
	}

	/**
	 * Methode creerObjetMetierBroker qui retourne
	 * true ou false
	 */
	public boolean creerClasse(Transaction aTransaction) throws Exception {
		return creer(aTransaction);
	}

	/**
	 * Methode modifierObjetMetierBroker qui retourne
	 * true ou false
	 */
	public boolean modifierClasse(Transaction aTransaction) throws Exception {
		return modifier(aTransaction);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Classe.
	 * @return ArrayList
	 */
	public ArrayList<Classe> listerClasse(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " ORDER BY CODCLA WITH UR ");
	}

	/**
	 * Retourne un Classe.
	 * @return Classe
	 */
	public Classe chercherClasse(Transaction aTransaction, String cle) throws Exception {
		return (Classe) executeSelect(aTransaction, "select * from " + getTable() + " where CODCLA = '" + cle + "' WITH UR ");
	}
}
