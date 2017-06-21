package nc.mairie.metier.carriere;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier FiliereGrade
 */
public class FiliereGradeBroker extends BasicBroker {
	/**
	 * Constructeur FiliereGradeBroker.
	 */
	public FiliereGradeBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return FiliereGradeMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new FiliereGrade();
	}

	/**
	 * @return FiliereGradeMetier
	 */
	protected FiliereGrade getMyFiliereGrade() {
		return (FiliereGrade) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPFILI";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CDFILI", new BasicRecord("CDFILI", "CHAR", getMyFiliereGrade().getClass().getField("codeFiliere"), "String"));
		mappage.put("LIFILI", new BasicRecord("LIFILI", "CHAR", getMyFiliereGrade().getClass().getField("libFiliere"), "String"));
		return mappage;
	}

	/**
	 * Retourne un ArrayList d'objet metier : FiliereGrade.
	 * @return ArrayList
	 */
	public ArrayList<FiliereGrade> listerFiliereGrade(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WITH UR ");
	}

	/**
	 * Retourne un FiliereGrade.
	 * @return FiliereGrade
	 */
	public FiliereGrade chercherFiliereGrade(Transaction aTransaction, String cle) throws Exception {
		return (FiliereGrade) executeSelect(aTransaction, "select * from " + getTable() + " where CDFILI = '" + cle + "' WITH UR ");
	}
}
