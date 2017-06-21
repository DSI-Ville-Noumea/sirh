package nc.mairie.metier.agent;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier CodeMutu
 */
public class CodeMutuBroker extends BasicBroker {
	/**
	 * Constructeur CodeMutuBroker.
	 */
	public CodeMutuBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return CodeMutuMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new CodeMutu();
	}

	/**
	 * @return CodeMutuMetier
	 */
	protected CodeMutu getMyCodeMutu() {
		return (CodeMutu) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPMUTU";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CDMUTU", new BasicRecord("CDMUTU", "NUMERIC", getMyCodeMutu().getClass().getField("cdmutu"), "String"));
		mappage.put("TXSAL", new BasicRecord("TXSAL", "DECIMAL", getMyCodeMutu().getClass().getField("txsal"), "String"));
		mappage.put("TXPAT", new BasicRecord("TXPAT", "DECIMAL", getMyCodeMutu().getClass().getField("txpat"), "String"));
		mappage.put("LIMUTU", new BasicRecord("LIMUTU", "CHAR", getMyCodeMutu().getClass().getField("limutu"), "String"));
		return mappage;
	}

	/**
	 * Retourne un ArrayList d'objet metier : CodeMutu.
	 * @return ArrayList
	 */
	public ArrayList<CodeMutu> listerCodeMutu(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WITH UR ");
	}

	/**
	 * Retourne un CodeMutu.
	 * @return CodeMutu
	 */
	public CodeMutu chercherCodeMutu(Transaction aTransaction, String cle) throws Exception {
		return (CodeMutu) executeSelect(aTransaction, "select * from " + getTable() + " where CDMUTU = " + cle + " WITH UR ");
	}
}
