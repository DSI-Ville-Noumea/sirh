package nc.mairie.metier.commun;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Pays
 */
public class PaysBroker extends BasicBroker {
	/**
	 * Constructeur PaysBroker.
	 */
	public PaysBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CODPAY", new BasicRecord("CODPAY", "NUMERIC", getMyPays().getClass().getField("codPays"), "STRING"));
		mappage.put("LIBPAY", new BasicRecord("LIBPAY", "CHAR", getMyPays().getClass().getField("libPays"), "STRING"));
		mappage.put("LIBNAT", new BasicRecord("LIBNAT", "CHAR", getMyPays().getClass().getField("libNationalite"), "STRING"));
		return mappage;
	}

	/**
	 * @return PaysMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Pays();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SIPAYS";
	}

	/**
	 * @return PaysMetier
	 */
	protected Pays getMyPays() {
		return (Pays) getMyBasicMetier();
	}

	/**
	 * Retourne un objet metier Pays a partir de son code.
	 * @return Pays
	 */
	public Pays chercherPays(Transaction aTransaction, String codePays) throws Exception {
		return (Pays) executeSelect(aTransaction, "select * from " + getTable() + " where CODPAY = " + codePays + " WITH UR");
	}

	/**
	 * Retourne un ArrayList d'objet metier : Pays.
	 * @return ArrayList
	 */
	public ArrayList<Pays> listerPaysAvecCodPaysCommencant(Transaction aTransaction, String codPart) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " where char(CODPAY) like '" + codPart + "%'" + " WITH UR");
	}

	/**
	 * Retourne un ArrayList d'objet metier : Pays.
	 * @return ArrayList
	 */
	public ArrayList<Pays> listerPaysAvecLibPaysCommencant(Transaction aTransaction, String libPart) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " where upper(LIBPAY) like '" + libPart.toUpperCase() + "%'" + " WITH UR");
	}
}
