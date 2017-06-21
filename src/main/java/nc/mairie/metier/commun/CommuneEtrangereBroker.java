package nc.mairie.metier.commun;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier CommuneEtrangere
 */
public class CommuneEtrangereBroker extends BasicBroker {
	/**
	 * Constructeur CommuneEtrangereBroker.
	 */
	public CommuneEtrangereBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * Retourne un ArrayList d'objet metier : CommuneEtrangere.
	 * @return CommuneEtrangere
	 */
	public CommuneEtrangere chercherCommuneEtrangere(Transaction aTransaction, String codPays, String scdPays) throws Exception {
		return (CommuneEtrangere) executeSelect(aTransaction, "select * from " + getTable() + " where CODPAY=" + codPays + " and SCODPA=" + scdPays + " order by LIBCOP " + " WITH UR");
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("SCODPA", new BasicRecord("SCODPA", "NUMERIC", getMyCommuneEtrangere().getClass().getField("codCommuneEtrangere"), "STRING"));
		mappage.put("LIBCOP", new BasicRecord("LIBCOP", "CHAR", getMyCommuneEtrangere().getClass().getField("libCommuneEtrangere"), "STRING"));
		mappage.put("CODPAY", new BasicRecord("CODPAY", "NUMERIC", getMyCommuneEtrangere().getClass().getField("codPays"), "STRING"));
		return mappage;
	}

	/**
	 * @return CommuneEtrangereMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new CommuneEtrangere();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SIVIET";
	}

	/**
	 * @return CommuneEtrangereMetier
	 */
	protected CommuneEtrangere getMyCommuneEtrangere() {
		return (CommuneEtrangere) getMyBasicMetier();
	}

	/**
	 * Retourne un ArrayList d'objet metier : CommuneEtrangere.
	 * @return ArrayList
	 */
	public ArrayList<CommuneEtrangere> listerCommuneEtrangerePays(Transaction aTransaction, String codPays) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " where CODPAY = " + codPays + " order by LIBCOP" + " WITH UR");
	}
}
