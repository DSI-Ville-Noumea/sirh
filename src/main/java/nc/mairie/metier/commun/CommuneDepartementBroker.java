package nc.mairie.metier.commun;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier CommuneDepartement
 */
public class CommuneDepartementBroker extends BasicBroker {
	/**
	 * Constructeur CommuneDepartementBroker.
	 */
	public CommuneDepartementBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CODCOM", new BasicRecord("CODCOM", "NUMERIC", getMyCommuneDepartement().getClass().getField("codCommune"), "STRING"));
		mappage.put("LIBVIL", new BasicRecord("LIBVIL", "CHAR", getMyCommuneDepartement().getClass().getField("libVille"), "STRING"));
		mappage.put("CODDEP", new BasicRecord("CODDEP", "NUMERIC", getMyCommuneDepartement().getClass().getField("codDepartement"), "STRING"));
		mappage.put("LIBDEP", new BasicRecord("LIBDEP", "CHAR", getMyCommuneDepartement().getClass().getField("libDepartement"), "STRING"));
		return mappage;
	}

	/**
	 * @return CommuneDepartementMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new CommuneDepartement();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SICOMMX1";
	}

	/**
	 * @return CommuneDepartementMetier
	 */
	protected CommuneDepartement getMyCommuneDepartement() {
		return (CommuneDepartement) getMyBasicMetier();
	}

	/**
	 * Retourne un ArrayList d'objet metier : Commune.
	 * @return ArrayList
	 */
	public ArrayList<CommuneDepartement> listerCommuneDepartementAvecCodCommuneCommencant(Transaction aTransaction, String texte) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " where char(CODCOM) like '" + texte + "%' order by LIBVIL" + " WITH UR");
	}

	/**
	 * Retourne un ArrayList d'objet metier : Commune.
	 * @return ArrayList
	 */
	public ArrayList<CommuneDepartement> listerCommuneDepartementAvecLibCommuneCommencant(Transaction aTransaction, String texte) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " where upper(LIBVIL) like '" + texte.toUpperCase() + "%' order by LIBVIL" + " WITH UR");
	}
}
