package nc.mairie.metier.carriere;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier GradeGenerique
 */
public class GradeGeneriqueBroker extends BasicBroker {
	/**
	 * Retourne un ArrayList d'objet metier : GradeGenerique.
	 * 
	 * @return ArrayList
	 */
	public GradeGenerique chercherGradeGenerique(Transaction aTransaction, String code) throws Exception {
		return (GradeGenerique) executeSelect(aTransaction, "select * from " + getTable() + " where CDGENG ='" + code
				+ "' WITH UR ");
	}

	/**
	 * Retourne un ArrayList d'objet metier : GradeGenerique.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<GradeGenerique> listerGradeGenerique(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " order by CDGENG WITH UR ");
	}

	/**
	 * Retourne un ArrayList d'objet metier : GradeGenerique.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<GradeGenerique> listerGradeGeneriqueOrderLib(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " order by ligrad WITH UR ");
	}

	/**
	 * Retourne un ArrayList d'objet metier : GradeGenerique.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<GradeGenerique> listerGradeGeneriqueActif(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable()
				+ " WHERE CDINAC <> 'I' order by ligrad WITH UR ");
	}

	/**
	 * Retourne un booleen
	 * 
	 * @return boolean
	 */
	public boolean creerGradeGenerique(Transaction aTransaction) throws Exception {
		return creer(aTransaction);
	}

	/**
	 * Retourne un booleen
	 * 
	 * @return boolean
	 */
	public boolean modifierGradeGenerique(Transaction aTransaction) throws Exception {
		return modifier(aTransaction);
	}

	/**
	 * Constructeur GradeGeneriqueBroker.
	 */
	public GradeGeneriqueBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return GradeGeneriqueMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new GradeGenerique();
	}

	/**
	 * @return GradeGeneriqueMetier
	 */
	protected GradeGenerique getMyGradeGenerique() {
		return (GradeGenerique) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPGENG";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CDGENG", new BasicRecord("CDGENG", "CHAR", getMyGradeGenerique().getClass().getField("cdgeng"),
				"STRING"));
		mappage.put("LIGRAD",
				new BasicRecord("LIGRAD", "CHAR", getMyGradeGenerique().getClass().getField("libGradeGenerique"),
						"STRING"));
		mappage.put("CDCADR", new BasicRecord("CDCADR", "CHAR", getMyGradeGenerique().getClass().getField("codCadre"),
				"STRING"));
		mappage.put("CDINAC", new BasicRecord("CDINAC", "CHAR", getMyGradeGenerique().getClass()
				.getField("codeInactif"), "String"));
		mappage.put("NB_PTS_AVCT",
				new BasicRecord("NB_PTS_AVCT", "INTEGER", getMyGradeGenerique().getClass().getField("nbPointsAvct"),
						"String"));
		mappage.put("IDCADREEMPLOI", new BasicRecord("IDCADREEMPLOI", "INTEGER", getMyGradeGenerique().getClass()
				.getField("idCadreEmploi"), "String"));
		mappage.put("CDFILI", new BasicRecord("CDFILI", "CHAR", getMyGradeGenerique().getClass().getField("cdfili"),
				"STRING"));
		mappage.put("TEXTECAPCADREEMPLOI", new BasicRecord("TEXTECAPCADREEMPLOI", "CHAR", getMyGradeGenerique()
				.getClass().getField("texteCapCadreEmploi"), "STRING"));
		mappage.put(
				"IDDELIBTERR",
				new BasicRecord("IDDELIBTERR", "INTEGER", getMyGradeGenerique().getClass().getField(
						"idDeliberationTerritoriale"), "STRING"));
		mappage.put(
				"IDDELIBCOMM",
				new BasicRecord("IDDELIBCOMM", "INTEGER", getMyGradeGenerique().getClass().getField(
						"idDeliberationCommunale"), "STRING"));
		return mappage;
	}

	public ArrayList<GradeGenerique> listerGradeGeneriqueAvecCadreEmploi(Transaction aTransaction, String idCadreEmploi)
			throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WHERE IDCADREEMPLOI = "
				+ idCadreEmploi + " WITH UR");
	}

	public ArrayList<GradeGenerique> listerGradeGeneriqueAvecDeliberation(Transaction aTransaction,
			Integer idDeliberation) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WHERE IDDELIBTERR = "
				+ idDeliberation + " or IDDELIBCOMM =" + idDeliberation + " WITH UR");
	}
}
