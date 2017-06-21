package nc.mairie.metier.agent;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier CodeAcci
 */
public class CodeAcciBroker extends BasicBroker {
	/**
	 * Constructeur CodeAcciBroker.
	 */
	public CodeAcciBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return CodeAcciMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new CodeAcci();
	}

	/**
	 * @return CodeAcciMetier
	 */
	protected CodeAcci getMyCodeAcci() {
		return (CodeAcci) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPACCI";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CDACCI", new BasicRecord("CDACCI", "NUMERIC", getMyCodeAcci().getClass().getField("cdacci"), "String"));
		mappage.put("LIBACC", new BasicRecord("LIBACC", "CHAR", getMyCodeAcci().getClass().getField("libacc"), "String"));
		mappage.put("TXPAT", new BasicRecord("TXPAT", "DECIMAL", getMyCodeAcci().getClass().getField("txpat"), "String"));
		mappage.put("REFEMP", new BasicRecord("REFEMP", "CHAR", getMyCodeAcci().getClass().getField("refemp"), "String"));
		mappage.put("DATDEB", new BasicRecord("DATDEB", "NUMERIC", getMyCodeAcci().getClass().getField("datdeb"), "String"));
		mappage.put("DATFIN", new BasicRecord("DATFIN", "NUMERIC", getMyCodeAcci().getClass().getField("datfin"), "String"));
		return mappage;
	}

	/**
	 * Retourne un ArrayList d'objet metier : CodeAcci.
	 * @return ArrayList
	 */
	public ArrayList<CodeAcci> listerCodeAcci(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WHERE DATFIN = '0' WITH UR ");
	}

	/**
	 * Retourne un CodeAcci.
	 * @return CodeAcci
	 */
	public CodeAcci chercherCodeAcci(Transaction aTransaction, String cle) throws Exception {
		return (CodeAcci) executeSelect(aTransaction, "select * from " + getTable() + " where CDACCI = " + cle + " WITH UR ");
	}
}
