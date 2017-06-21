package nc.mairie.metier.commun;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier BanqueGuichet
 */
public class BanqueGuichetBroker extends BasicBroker {
	/**
	 * Constructeur BanqueGuichetBroker.
	 */
	public BanqueGuichetBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CDBANQ", new BasicRecord("CDBANQ", "DECIMAL", getMyBanqueGuichet().getClass().getField("codBanque"), "STRING"));
		mappage.put("LIBANQ", new BasicRecord("LIBANQ", "CHAR", getMyBanqueGuichet().getClass().getField("libBanque"), "STRING"));
		mappage.put("CDGUIC", new BasicRecord("CDGUIC", "DECIMAL", getMyBanqueGuichet().getClass().getField("codGuichet"), "STRING"));
		mappage.put("LIGUIC", new BasicRecord("LIGUIC", "CHAR", getMyBanqueGuichet().getClass().getField("libGuichet"), "STRING"));
		return mappage;
	}

	/**
	 * @return BanqueGuichetMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new BanqueGuichet();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SIGUICX1";
	}

	/**
	 * @return BanqueGuichetMetier
	 */
	protected BanqueGuichet getMyBanqueGuichet() {
		return (BanqueGuichet) getMyBasicMetier();
	}

	/**
	 * Retourne un ArrayList d'objet metier : BanqueGuichet.
	 * @return ArrayList
	 */
	public ArrayList<BanqueGuichet> listerBanqueGuichet(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " order by CDBANQ, CDGUIC " + " WITH UR");
	}

	public BanqueGuichet chercherBanqueGuichet(Transaction aTransaction, String codeBanque, String codeGuichet) throws Exception {
		return (BanqueGuichet) executeSelect(aTransaction, "select * from " + getTable() + " where CDBANQ = " + codeBanque + " and CDGUIC = " + codeGuichet + " WITH UR");
	}
}
