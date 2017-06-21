package nc.mairie.metier.commun;

import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Commune
 */
public class CommuneBroker extends BasicBroker {
	/**
	 * Constructeur CommuneBroker.
	 */
	public CommuneBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Commune.
	 * @return ArrayList
	 */
	public Commune chercherCommune(Transaction aTransaction, String codCommune) throws Exception {
		return (Commune) executeSelect(aTransaction, "select CODCOM, TRIM(LIBVIL) AS LIBVIL, CODDEP, ARTCOM from " + getTable() + " where CODCOM = " + codCommune + " WITH UR");
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CODCOM", new BasicRecord("CODCOM", "NUMERIC", getMyCommune().getClass().getField("codCommune"), "STRING"));
		mappage.put("LIBVIL", new BasicRecord("LIBVIL", "CHAR", getMyCommune().getClass().getField("libCommune"), "STRING"));
		mappage.put("CODDEP", new BasicRecord("CODDEP", "NUMERIC", getMyCommune().getClass().getField("codDepartement"), "STRING"));
		mappage.put("ARTCOM", new BasicRecord("ARTCOM", "CHAR", getMyCommune().getClass().getField("articleCommune"), "STRING"));
		return mappage;
	}

	/**
	 * @return CommuneMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Commune();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SICOMM";
	}

	/**
	 * @return CommuneMetier
	 */
	protected Commune getMyCommune() {
		return (Commune) getMyBasicMetier();
	}
}
