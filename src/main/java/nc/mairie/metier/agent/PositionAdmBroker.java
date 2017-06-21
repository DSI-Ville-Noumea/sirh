package nc.mairie.metier.agent;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier PositionAdm
 */
public class PositionAdmBroker extends BasicBroker {
	/**
	 * Constructeur PositionAdmBroker.
	 */
	public PositionAdmBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return PositionAdmMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new PositionAdm();
	}

	/**
	 * @return PositionAdmMetier
	 */
	protected PositionAdm getMyPositionAdm() {
		return (PositionAdm) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPPOSA";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CDPADM", new BasicRecord("CDPADM", "CHAR", getMyPositionAdm().getClass().getField("cdpadm"), "String"));
		mappage.put("LIPADM", new BasicRecord("LIPADM", "CHAR", getMyPositionAdm().getClass().getField("liPAdm"), "String"));
		mappage.put("DROITC", new BasicRecord("DROITC", "CHAR", getMyPositionAdm().getClass().getField("droitc"), "String"));
		mappage.put("DUREE", new BasicRecord("DUREE", "NUMERIC", getMyPositionAdm().getClass().getField("duree"), "String"));
		mappage.put("POSIT", new BasicRecord("POSIT", "CHAR", getMyPositionAdm().getClass().getField("posit"), "String"));
		return mappage;
	}

	/**
	 * Retourne un ArrayList d'objet metier : PositionAdm.
	 * @return ArrayList
	 */
	public ArrayList<PositionAdm> listerPositionAdm(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " ORDER BY LIPADM WITH UR ");
	}

	/**
	 * Retourne un PositionAdm.
	 * @return PositionAdm
	 */
	public PositionAdm chercherPositionAdm(Transaction aTransaction, String cle) throws Exception {
		return (PositionAdm) executeSelect(aTransaction, "select * from " + getTable() + " where CDPADM = '" + cle + "' WITH UR ");
	}

	public PositionAdmAgent chercherPositionAdmAgentDateDebutFinComprise(Transaction transaction, String noMatricule, String datdeb,String datfin) throws Exception {
		return (PositionAdmAgent) executeSelect(transaction, "select * from " + getTable() + " where nomatr = " + noMatricule + " and (('" + datdeb + "' >= DATDEB and '" + datdeb + "' < DATFIN) or ('" + datdeb
				+ "'>=datdeb and datfin=0)) and ('" + datfin + "' < DATFIN or datfin=0) WITH UR ");			
	}
}
