package nc.mairie.metier.commun;

import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Departement
 */
public class DepartementBroker extends BasicBroker {
	/**
	 * Constructeur DepartementBroker.
	 */
	public DepartementBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Departement.
	 * @return ArrayList
	 */
	public Departement chercherDepartementCommune(Transaction aTransaction, String codeDep) throws Exception {
		return (Departement) executeSelect(aTransaction, "select * from " + getTable() + " where CODDEP = " + codeDep + " WITH UR");
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CODDEP", new BasicRecord("CODDEP", "NUMERIC", getMyDepartement().getClass().getField("codDepartement"), "STRING"));
		mappage.put("LIBDEP", new BasicRecord("LIBDEP", "CHAR", getMyDepartement().getClass().getField("libDepartement"), "STRING"));
		return mappage;
	}

	/**
	 * @return DepartementMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Departement();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SIDEPT";
	}

	/**
	 * @return DepartementMetier
	 */
	protected Departement getMyDepartement() {
		return (Departement) getMyBasicMetier();
	}
}
