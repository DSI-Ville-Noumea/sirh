package nc.mairie.metier.commun;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier CommunePostal
 */
public class CommunePostalBroker extends BasicBroker {
	/**
	 * Constructeur CommunePostalBroker.
	 */
	public CommunePostalBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * Retourne un CommunePostal.
	 * @return CommunePostal
	 */
	public CommunePostal chercherCommunePostal(Transaction aTransaction, String codCodePostal, String codCommunePostal) throws Exception {
		return (CommunePostal) executeSelect(aTransaction, "select * from " + getTable() + " where CDCPOS = " + codCodePostal + " AND CODCOM = " + codCommunePostal + " WITH UR");
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CDCPOS", new BasicRecord("CDCPOS", "NUMERIC", getMyCommunePostal().getClass().getField("codCodePostal"), "STRING"));
		mappage.put("LIPOST", new BasicRecord("LIPOST", "CHAR", getMyCommunePostal().getClass().getField("libCodePostal"), "STRING"));
		mappage.put("CODCOM", new BasicRecord("CODCOM", "NUMERIC", getMyCommunePostal().getClass().getField("codCommune"), "STRING"));
		mappage.put("LIBVIL", new BasicRecord("LIBVIL", "CHAR", getMyCommunePostal().getClass().getField("libCommune"), "STRING"));
		return mappage;
	}

	/**
	 * @return CommunePostalMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new CommunePostal();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SICDPOX1";
	}

	/**
	 * @return CommunePostalMetier
	 */
	protected CommunePostal getMyCommunePostal() {
		return (CommunePostal) getMyBasicMetier();
	}

	/**
	 * Retourne un ArrayList d'objet metier : Commune.
	 * @return ArrayList
	 */
	public ArrayList<CommunePostal> listerCommunePostalAvecCodCommuneCommencant(Transaction aTransaction, String texte) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " where char(CODCOM) like '" + texte + "%' order by LIBVIL" + " WITH UR");
	}

	/**
	 * Retourne un ArrayList d'objet metier : Commune.
	 * @return ArrayList
	 */
	public ArrayList<CommunePostal> listerCommunePostalAvecLibCommuneCommencant(Transaction aTransaction, String texte) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " where upper(LIBVIL) like '" + texte.toUpperCase() + "%' order by LIBVIL" + " WITH UR");
	}

	/**
	 * Retourne la liste des "comunes postales" de Noumea.
	 * @return ArrayList
	 */
	public ArrayList<CommunePostal> listerCommunePostalNoumea(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " where CODCOM = '98818' order by LIBVIL" + " WITH UR");
	}

	/**
	 * Retourne la liste des "comunes postales" hors Noumea.
	 * @return ArrayList
	 */
	public ArrayList<CommunePostal> listerCommunePostalHorsNoumea(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " where CODCOM LIKE '988%' AND CODCOM != '98818' order by LIBVIL" + " WITH UR");
	}
}
