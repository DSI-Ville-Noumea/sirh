package nc.mairie.metier.commun;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier VoieQuartier
 */
public class VoieQuartierBroker extends BasicBroker {
	/**
	 * Constructeur VoieQuartierBroker.
	 */
	public VoieQuartierBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * Retourne un VoieQuartier
	 * @return VoieQuartier
	 */
	public VoieQuartier chercherVoieQuartierAvecCodVoie(Transaction aTransaction, String codVoie) throws Exception {
		return (VoieQuartier) executeSelect(aTransaction, "select * from " + getTable() + " where CDVOIE = " + codVoie + " WITH UR");
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CODCOM", new BasicRecord("CODCOM", "NUMERIC", getMyVoieQuartier().getClass().getField("codCommune"), "STRING"));
		mappage.put("CDVOIE", new BasicRecord("CDVOIE", "NUMERIC", getMyVoieQuartier().getClass().getField("codVoie"), "STRING"));
		mappage.put("LIVOIE", new BasicRecord("LIVOIE", "CHAR", getMyVoieQuartier().getClass().getField("libVoie"), "STRING"));
		mappage.put("QUARTI", new BasicRecord("QUARTI", "NUMERIC", getMyVoieQuartier().getClass().getField("codQuartier"), "STRING"));
		mappage.put("LIQUAR", new BasicRecord("LIQUAR", "CHAR", getMyVoieQuartier().getClass().getField("libQuartier"), "STRING"));
		return mappage;
	}

	/**
	 * @return VoieQuartierMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new VoieQuartier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SIVOQTX1";
	}

	/**
	 * @return VoieQuartierMetier
	 */
	protected VoieQuartier getMyVoieQuartier() {
		return (VoieQuartier) getMyBasicMetier();
	}

	/**
	 * Retourne un ArrayList d'objet metier : VoieQuartier.
	 * @return ArrayList
	 */
	public ArrayList<VoieQuartier> listerVoieQuartierAvecLibVoieContenant(Transaction aTransaction, String texte) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " where upper(LIVOIE) like '%" + texte.toUpperCase() + "%' order by CDVOIE" + " WITH UR");
	}
}
