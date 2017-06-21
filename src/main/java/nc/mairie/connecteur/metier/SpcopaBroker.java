package nc.mairie.connecteur.metier;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Spcopa
 */
public class SpcopaBroker extends BasicBroker {
	/**
	 * Constructeur SpcopaBroker.
	 */
	public SpcopaBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return SpcopaMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Spcopa();
	}

	/**
	 * @return SpcopaMetier
	 */
	protected Spcopa getMySpcopa() {
		return (Spcopa) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPCOPA";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CDCOPA", new BasicRecord("CDCOPA", "NUMERIC", getMySpcopa().getClass().getField("cdcopa"), "String"));
		mappage.put("LICOPA", new BasicRecord("LICOPA", "CHAR", getMySpcopa().getClass().getField("licopa"), "String"));
		return mappage;
	}

	/**
	 * Retourne un ArrayList d'objet metier : Spcopa.
	 * @return ArrayList
	 */
	public ArrayList<Spcopa> listerSpcopa(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WITH UR");
	}

	/**
	 * Retourne un Spcopa.
	 * @return Spcopa
	 */
	public Spcopa chercherSpcopa(Transaction aTransaction, String libCommune) throws Exception {
		return (Spcopa) executeSelect(aTransaction, "select * from " + getTable() + " where LICOPA = '" + libCommune + "'" + " WITH UR");
	}
}
