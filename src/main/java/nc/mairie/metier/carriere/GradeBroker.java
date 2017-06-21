package nc.mairie.metier.carriere;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Grade
 */
public class GradeBroker extends BasicBroker {
	/**
	 * Retourne un Grade.
	 * 
	 * @return Grade
	 */
	public Grade chercherGrade(Transaction aTransaction, String cdgrad) throws Exception {
		return (Grade) executeSelect(aTransaction, "select * from " + getTable() + " where cdgrad = '" + cdgrad + "'"
				+ " WITH UR ");
	}

	/**
	 * Retourne un Grade.
	 * 
	 * @return Grade
	 */
	public Grade chercherGradeByCodeGradeGenerique(Transaction aTransaction, String codGradeGenerique) throws Exception {
		return (Grade) executeSelect(aTransaction, "select * from " + getTable() + " where CODGRG = '"
				+ codGradeGenerique + "' WITH UR ");
	}

	/**
	 * Methode creerObjetMetierBroker qui retourne true ou false
	 */
	public boolean creerGrade(Transaction aTransaction) throws Exception {
		return creer(aTransaction);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Grade.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Grade> listerGradeActif(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable()
				+ " where CODACT='A' order by cdgrad WITH UR ");
	}

	/**
	 * Retourne un ArrayList d'objet metier : Grade.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Grade> listerGradeAvecTypeGrade(Transaction aTransaction, String typeGrade) throws Exception {
		return executeSelectListe(aTransaction,
				"select * from " + getTable() + " WHERE GRADE = '" + typeGrade.replace("'", "''") + "' order by cdgrad"
						+ " WITH UR ");
	}

	/**
	 * Retourne un ArrayList d'objet metier : Grade.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Grade> listerTypeGrade(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select GRADE, CODACT from " + getTable()
				+ " where GRADE<>'' group by GRADE, CODACT order by GRADE" + " WITH UR ");
	}

	/**
	 * Retourne un ArrayList d'objets metier Grade lies a un grade generique
	 * passe en parametre.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Grade> listerGradeAvecGradeGenerique(Transaction aTransaction, String codeGradeGenerique)
			throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WHERE  CODGRG = '"
				+ codeGradeGenerique + "' order by cdgrad" + " WITH UR ");
	}

	/**
	 * Retourne un ArrayList d'objet metier : Grade.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Grade> listerGradeAvecCodeContenant(Transaction aTransaction, String texte) throws Exception {
		return executeSelectListe(aTransaction,
				"select * from " + getTable() + " where upper(cdgrad) like '%" + texte.toUpperCase()
						+ "%' order by cdgrad" + " WITH UR ");
	}

	/**
	 * Retourne un ArrayList d'objet metier : Grade.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Grade> listerGradeAvecLibelleContenant(Transaction aTransaction, String texte) throws Exception {
		return executeSelectListe(aTransaction,
				"select * from " + getTable() + " where upper(ligrad) like '%" + texte.toUpperCase()
						+ "%' order by cdgrad" + " WITH UR ");

	}

	/**
	 * Methode modifierObjetMetierBroker qui retourne true ou false
	 */
	public boolean modifierGrade(Transaction aTransaction) throws Exception {
		return modifier(aTransaction);
	}

	/**
	 * Constructeur GradeBroker.
	 */
	public GradeBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return GradeMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Grade();
	}

	/**
	 * @return GradeMetier
	 */
	protected Grade getMyGrade() {
		return (Grade) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPGRADN";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CDGRAD",
				new BasicRecord("CDGRAD", "CHAR", getMyGrade().getClass().getField("codeGrade"), "String"));
		mappage.put("LIGRAD", new BasicRecord("LIGRAD", "CHAR", getMyGrade().getClass().getField("libGrade"), "String"));
		mappage.put("MTFORF", new BasicRecord("MTFORF", "DECIMAL", getMyGrade().getClass().getField("montantForfait"),
				"String"));
		mappage.put("MTPRI", new BasicRecord("MTPRI", "DECIMAL", getMyGrade().getClass().getField("montantPrime"),
				"String"));
		mappage.put("CODACT",
				new BasicRecord("CODACT", "CHAR", getMyGrade().getClass().getField("codeActif"), "String"));
		mappage.put("IBAN", new BasicRecord("IBAN", "CHAR", getMyGrade().getClass().getField("iban"), "String"));
		mappage.put("CDGRIL", new BasicRecord("CDGRIL", "CHAR", getMyGrade().getClass().getField("codeGrille"),
				"String"));
		mappage.put("CODCLA", new BasicRecord("CODCLA", "CHAR", getMyGrade().getClass().getField("codeClasse"),
				"String"));
		mappage.put("CODECH", new BasicRecord("CODECH", "CHAR", getMyGrade().getClass().getField("codeEchelon"),
				"String"));
		mappage.put("CODGRG", new BasicRecord("CODGRG", "CHAR", getMyGrade().getClass().getField("codeGradeGenerique"),
				"String"));
		mappage.put("ACC", new BasicRecord("ACC", "CHAR", getMyGrade().getClass().getField("acc"), "String"));
		mappage.put("BM", new BasicRecord("BM", "CHAR", getMyGrade().getClass().getField("bm"), "String"));
		mappage.put("DURMIN", new BasicRecord("DURMIN", "NUMERIC", getMyGrade().getClass().getField("dureeMin"),
				"String"));
		mappage.put("DURMOY", new BasicRecord("DURMOY", "NUMERIC", getMyGrade().getClass().getField("dureeMoy"),
				"String"));
		mappage.put("DURMAX", new BasicRecord("DURMAX", "NUMERIC", getMyGrade().getClass().getField("dureeMax"),
				"String"));
		mappage.put("CDTAVA", new BasicRecord("CDTAVA", "CHAR", getMyGrade().getClass().getField("codeTava"), "String"));
		mappage.put("CDSUIV", new BasicRecord("CDSUIV", "CHAR", getMyGrade().getClass().getField("codeGradeSuivant"),
				"String"));
		mappage.put("CDCADR",
				new BasicRecord("CDCADR", "CHAR", getMyGrade().getClass().getField("codeCadre"), "String"));
		mappage.put("GRADE", new BasicRecord("GRADE", "CHAR", getMyGrade().getClass().getField("grade"), "String"));
		return mappage;
	}

	public ArrayList<Grade> listerGradeConvCol(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable()
				+ " where CODACT='A' and IBAN='0000000' order by cdgrad WITH UR ");
	}

	public ArrayList<Grade> listerGradeAvecTypeGradeCodAct(Transaction aTransaction, String typeGrade, String codAct)
			throws Exception {
		return executeSelectListe(aTransaction,
				"select * from " + getTable() + " WHERE GRADE = '" + typeGrade.replace("'", "''") + "' and CODACT='"
						+ codAct + "' order by cdgrad" + " WITH UR ");
	}

	public ArrayList<Grade> listerGradeInitialActif(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select distinct(grade) from " + getTable()
				+ " where CODACT='A' and grade!= '' order by grade ");
	}

	public Grade chercherGradeByGradeInitial(Transaction aTransaction, String gradeInitial) throws Exception {
		return (Grade) executeSelect(aTransaction,
				"select * from " + getTable() + " where GRADE = '" + gradeInitial.replace("'", "''") + "' WITH UR ");
	}

	public ArrayList<Grade> listerCategorieGrade(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select distinct(cdcadr) from " + getTable()
				+ " where cdcadr is not null and cdcadr != '' WITH UR ");
	}

	public ArrayList<Grade> listerGradeAvecGradeGeneriqueEtGrade(Transaction aTransaction, String cdgeng,
			String libGradeGenerique) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WHERE  CODGRG = '" + cdgeng
				+ "' and trim(upper(GRADE)) = '" + libGradeGenerique.replace("'", "''").toUpperCase().trim()
				+ "' WITH UR ");
	}
}
