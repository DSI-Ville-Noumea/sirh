package nc.mairie.metier.carriere;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier StatutCarriere
 */
public class StatutCarriereBroker extends BasicBroker {
	/**
	 * Constructeur StatutCarriereBroker.
	 */
	public StatutCarriereBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return StatutCarriereMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new StatutCarriere();
	}

	/**
	 * @return StatutCarriereMetier
	 */
	protected StatutCarriere getMyStatutCarriere() {
		return (StatutCarriere) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPCATG";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CDCATE", new BasicRecord("CDCATE", "NUMERIC", getMyStatutCarriere().getClass().getField("cdCate"), "String"));
		mappage.put("LICATE", new BasicRecord("LICATE", "CHAR", getMyStatutCarriere().getClass().getField("liCate"), "String"));
		return mappage;
	}

	/**
	 * Retourne un StatutCarriere.
	 * @return StatutCarriere
	 */
	public StatutCarriere chercherStatutCarriere(Transaction aTransaction, String cdcate) throws Exception {
		return (StatutCarriere) executeSelect(aTransaction, "select * from " + getTable() + " where CDCATE = " + cdcate + " WITH UR ");
	}

	/**
	 * Liste les StatutCarriere.
	 * @return StatutCarriere
	 */
	public ArrayList<StatutCarriere> listerStatutCarriere(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " order by CDCATE WITH UR ");
	}

}
