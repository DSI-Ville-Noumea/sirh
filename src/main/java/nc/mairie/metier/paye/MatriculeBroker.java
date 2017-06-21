package nc.mairie.metier.paye;

import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Matricule
 */
public class MatriculeBroker extends BasicBroker {
	/**
	 * Constructeur MatriculeBroker.
	 */
	public MatriculeBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return MatriculeMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Matricule();
	}

	/**
	 * @return MatriculeMetier
	 */
	protected Matricule getMyMatricule() {
		return (Matricule) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPMATR";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("NOMATR", new BasicRecord("NOMATR", "NUMERIC", getMyMatricule().getClass().getField("nomatr"), "STRING"));
		mappage.put("PERPRE", new BasicRecord("PERPRE", "NUMERIC", getMyMatricule().getClass().getField("perpre"), "STRING"));
		mappage.put("PERRAP", new BasicRecord("PERRAP", "NUMERIC", getMyMatricule().getClass().getField("perrap"), "STRING"));
		mappage.put("CDVALI", new BasicRecord("CDVALI", "CHAR", getMyMatricule().getClass().getField("cdvali"), "STRING"));
		mappage.put("CDCHAI", new BasicRecord("CDCHAI", "CHAR", getMyMatricule().getClass().getField("cdchai"), "STRING"));
		return mappage;
	}

	/**
	 * Methode creerObjetMetierBroker qui retourne
	 * true ou false
	 */
	public boolean creerMatricule(Transaction aTransaction) throws Exception {
		return creer(aTransaction);
	}

	/**
	 * Methode modifierObjetMetierBroker qui retourne
	 * true ou false
	 */
	public boolean modifierMatricule(Transaction aTransaction) throws Exception {
		return modifier(aTransaction);
	}

	/**
	 * Retourne un Matricule.
	 * @return Matricule
	 */
	public Matricule chercherMatriculeDebut(Transaction aTransaction, String nomatr, String debut) throws Exception {
		return (Matricule) executeSelect(aTransaction, "select * from " + getTable() + " where nomatr = " + nomatr + " and perrap<=" + debut + " WITH UR ");
	}

	public Matricule chercherMatricule(Transaction aTransaction, Integer nomatr) throws Exception {
		return (Matricule) executeSelect(aTransaction, "select * from " + getTable() + " where nomatr = " + nomatr + " WITH UR ");
	}
}
