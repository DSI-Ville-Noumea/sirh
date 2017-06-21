package nc.mairie.metier.poste;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier EntiteGeo
 */
public class EntiteGeoBroker extends BasicBroker {

	/**
	 * Constructeur EntiteGeoBroker.
	 */
	public EntiteGeoBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return EntiteGeoMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new EntiteGeo();
	}

	/**
	 * @return EntiteGeoMetier
	 */
	protected EntiteGeo getMyEntiteGeo() {
		return (EntiteGeo) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SILIEU";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CDLIEU", new BasicRecord("CDLIEU", "DECIMAL", getMyEntiteGeo().getClass().getField("idEntiteGeo"), "STRING"));
		mappage.put("LILIEU", new BasicRecord("LILIEU", "VARCHAR", getMyEntiteGeo().getClass().getField("libEntiteGeo"), "STRING"));
		mappage.put("RIDET", new BasicRecord("RIDET", "NUMERIC", getMyEntiteGeo().getClass().getField("ridet"), "STRING"));
		mappage.put("CDECOL", new BasicRecord("CDECOL", "NUMERIC", getMyEntiteGeo().getClass().getField("cdEcol"), "STRING"));
		return mappage;
	}

	/**
	 * Methode creerObjetMetierBroker qui retourne
	 * true ou false
	 */
	public boolean creerEntiteGeo(Transaction aTransaction) throws Exception {
		return creer(aTransaction);
	}

	/**
	 * Retourne le plus grand numero de CDLIEU existant.
	 * @return int
	 */
	public int recupMaxCdLieu(Transaction aTransaction) throws Exception {
		return executeCompter(aTransaction, "select max(CDLIEU) from " + getTable() + " WITH UR");
	}

	/**
	 * Methode supprimerObjetMetierBroker qui retourne
	 * true ou false
	 */
	public boolean supprimerEntiteGeo(Transaction aTransaction) throws Exception {
		return supprimer(aTransaction);
	}

	/**
	 * Retourne un ArrayList d'objet metier : EntiteGeo.
	 * @return ArrayList
	 */
	public ArrayList<EntiteGeo> listerEntiteGeo(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " ORDER BY LILIEU" + " WITH UR");
	}

	/**
	 * Retourne un EntiteGeo.
	 * @return EntiteGeo
	 */
	public EntiteGeo chercherEntiteGeo(Transaction aTransaction, String cle) throws Exception {
		return (EntiteGeo) executeSelect(aTransaction, "select * from " + getTable() + " where CDLIEU = " + cle + " WITH UR");
	}

	public boolean modifierEntiteGeo(Transaction aTransaction) throws Exception {
		return modifier(aTransaction);
	}
}
