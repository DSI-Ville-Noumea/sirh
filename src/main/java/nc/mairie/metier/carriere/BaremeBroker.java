package nc.mairie.metier.carriere;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Services;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Bareme
 */
public class BaremeBroker extends BasicBroker {
	/**
	 * Constructeur BaremeBroker.
	 */
	public BaremeBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return BaremeMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Bareme();
	}

	/**
	 * @return BaremeMetier
	 */
	protected Bareme getMyBareme() {
		return (Bareme) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPBAREM";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("IBAN", new BasicRecord("IBAN", "CHAR", getMyBareme().getClass().getField("iban"), "String"));
		mappage.put("INA", new BasicRecord("INA", "NUMERIC", getMyBareme().getClass().getField("ina"), "String"));
		mappage.put("INM", new BasicRecord("INM", "NUMERIC", getMyBareme().getClass().getField("inm"), "String"));
		return mappage;
	}

	/**
	 * Methode creerObjetMetierBroker qui retourne
	 * true ou false
	 */
	public boolean creerBareme(Transaction aTransaction) throws Exception {
		return creer(aTransaction);
	}

	/**
	 * Methode modifierObjetMetierBroker qui retourne
	 * true ou false
	 */
	public boolean modifierBareme(Transaction aTransaction) throws Exception {
		return modifier(aTransaction);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Bareme.
	 * @return ArrayList
	 * 
	 */
	public ArrayList<Bareme> listerBareme(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " ORDER BY IBAN WITH UR ");
	}

	/**
	 * Retourne un Bareme.
	 * @return Bareme
	 */
	public Bareme chercherBareme(Transaction aTransaction, String cle) throws Exception {
		String IBAN = Const.CHAINE_VIDE;
		if (Services.estNumerique(cle)) {
			IBAN = Services.lpad(cle.trim(), 7, "0");
		} else {
			IBAN = cle.trim();
		}
		return (Bareme) executeSelect(aTransaction, "select * from " + getTable() + " where IBAN = '" + IBAN + "'" + " WITH UR ");
	}

	/**
	 * Retourne un Bareme.
	 * @return Bareme
	 */
	public ArrayList<Bareme> listerBaremeByINM(Transaction aTransaction, String inm) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " where INM = " + inm + " order by INA WITH UR ");
	}
	

	public Bareme getPreviousBareme(Transaction aTransaction, String inm) throws Exception {
		ArrayList<Bareme> list = executeSelectListe(aTransaction, "select * from " + getTable() + " WHERE inm < " + inm + " ORDER BY INM desc, INA WITH UR ");
		return list.get(0);
	}
}
