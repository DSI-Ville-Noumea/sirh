package nc.mairie.metier.carriere;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier ModeReglement
 */
public class ModeReglementBroker extends BasicBroker {
	/**
	 * Constructeur ModeReglementBroker.
	 */
	public ModeReglementBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return ModeReglementMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new ModeReglement();
	}

	/**
	 * @return ModeReglementMetier
	 */
	protected ModeReglement getMyModeReglement() {
		return (ModeReglement) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPMRGL";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("MODREG", new BasicRecord("MODREG", "CHAR", getMyModeReglement().getClass().getField("modReg"), "String"));
		mappage.put("LIMODR", new BasicRecord("LIMODR", "CHAR", getMyModeReglement().getClass().getField("libModReg"), "String"));
		return mappage;
	}

	/**
	 * Retourne un ArrayList d'objet metier : ModeReglement.
	 * @return ArrayList
	 */
	public ArrayList<ModeReglement> listerModeReglement(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WITH UR ");
	}
}
