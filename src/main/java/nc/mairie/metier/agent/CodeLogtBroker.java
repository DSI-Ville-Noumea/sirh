package nc.mairie.metier.agent;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier CodeLogt
 */
public class CodeLogtBroker extends BasicBroker {
	/**
	 * Constructeur CodeLogtBroker.
	 */
	public CodeLogtBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return CodeLogtMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new CodeLogt();
	}

	/**
	 * @return CodeLogtMetier
	 */
	protected CodeLogt getMyCodeLogt() {
		return (CodeLogt) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPCLOG";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CDLOGT", new BasicRecord("CDLOGT", "NUMERIC", getMyCodeLogt().getClass().getField("cdlogt"), "String"));
		mappage.put("LIBLOG", new BasicRecord("LIBLOG", "CHAR", getMyCodeLogt().getClass().getField("liblog"), "String"));
		mappage.put("TXSAL", new BasicRecord("TXSAL", "DECIMAL", getMyCodeLogt().getClass().getField("txsal"), "String"));
		mappage.put("DATDEB", new BasicRecord("DATDEB", "NUMERIC", getMyCodeLogt().getClass().getField("datdeb"), "String"));
		mappage.put("DATFIN", new BasicRecord("DATFIN", "NUMERIC", getMyCodeLogt().getClass().getField("datfin"), "String"));
		return mappage;
	}

	/**
	 * Retourne un ArrayList d'objet metier : CodeLogt.
	 * @return ArrayList
	 */
	public ArrayList<CodeLogt> listerCodeLogt(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WITH UR ");
	}

	/**
	 * Retourne un CodeLogt.
	 * @return CodeLogt
	 */
	public CodeLogt chercherCodeLogt(Transaction aTransaction, String cle) throws Exception {
		return (CodeLogt) executeSelect(aTransaction, "select * from " + getTable() + " where CDLOGT = " + cle + " WITH UR ");
	}
}
