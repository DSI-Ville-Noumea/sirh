package nc.mairie.metier.agent;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier CodeChargeLogt
 */
public class CodeChargeLogtBroker extends BasicBroker {
	/**
	 * Constructeur CodeChargeLogtBroker.
	 */
	public CodeChargeLogtBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return CodeChargeLogtMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new CodeChargeLogt();
	}

	/**
	 * @return CodeChargeLogtMetier
	 */
	protected CodeChargeLogt getMyCodeChargeLogt() {
		return (CodeChargeLogt) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPCCHG";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CDLOGT", new BasicRecord("CDLOGT", "NUMERIC", getMyCodeChargeLogt().getClass().getField("cdlogt"), "String"));
		mappage.put("LIBLOG", new BasicRecord("LIBLOG", "CHAR", getMyCodeChargeLogt().getClass().getField("liblog"), "String"));
		mappage.put("MTREGR", new BasicRecord("MTREGR", "DECIMAL", getMyCodeChargeLogt().getClass().getField("mtregr"), "String"));
		mappage.put("DATDEB", new BasicRecord("DATDEB", "NUMERIC", getMyCodeChargeLogt().getClass().getField("datdeb"), "String"));
		mappage.put("DATFIN", new BasicRecord("DATFIN", "NUMERIC", getMyCodeChargeLogt().getClass().getField("datfin"), "String"));
		return mappage;
	}

	/**
	 * Retourne un ArrayList d'objet metier : CodeChargeLogt.
	 * @return ArrayList
	 */
	public ArrayList<CodeChargeLogt> listerCodeChargeLogt(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WITH UR ");
	}

	/**
	 * Retourne un CodeChargeLogt.
	 * @return CodeChargeLogt
	 */
	public CodeChargeLogt chercherCodeChargeLogt(Transaction aTransaction, String cle) throws Exception {
		return (CodeChargeLogt) executeSelect(aTransaction, "select * from " + getTable() + " where CDLOGT = " + cle + " WITH UR ");
	}
}
