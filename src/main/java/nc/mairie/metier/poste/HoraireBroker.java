package nc.mairie.metier.poste;

import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.BasicRecord;
import nc.mairie.technique.Transaction;

/**
 * Broker de l'Objet metier Horaire
 */
public class HoraireBroker extends BasicBroker {
	/**
	 * Constructeur HoraireBroker.
	 */
	public HoraireBroker(BasicMetier aMetier) {
		super(aMetier);
	}

	/**
	 * @return HoraireMetier
	 */
	protected BasicMetier definirMyMetier() {
		return new Horaire();
	}

	/**
	 * @return HoraireMetier
	 */
	protected Horaire getMyHoraire() {
		return (Horaire) getMyBasicMetier();
	}

	/**
	 * Retourne le nom de la table.
	 */
	protected String definirNomTable() {
		return "SPBHOR";
	}

	/**
	 * Retourne le mappage de chaque colonne de la table.
	 */
	protected Hashtable<String, BasicRecord> definirMappageTable() throws NoSuchFieldException {
		Hashtable<String, BasicRecord> mappage = new Hashtable<String, BasicRecord>();
		mappage.put("CDTHOR", new BasicRecord("CDTHOR", "DECIMAL", getMyHoraire().getClass().getField("cdtHor"), "String"));
		mappage.put("LIBHOR", new BasicRecord("LIBHOR", "CHAR", getMyHoraire().getClass().getField("libHor"), "String"));
		mappage.put("CDTAUX", new BasicRecord("CDTAUX", "DECIMAL", getMyHoraire().getClass().getField("cdTaux"), "String"));
		return mappage;
	}

	/**
	 * Retourne un ArrayList d'objet metier : Horaire.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Horaire> listerHoraire(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " WITH UR ");
	}

	/**
	 * Retourne un Horaire.
	 * 
	 * @return Horaire
	 */
	public Horaire chercherHoraire(Transaction aTransaction, String cle) throws Exception {
		return (Horaire) executeSelect(aTransaction, "select * from " + getTable() + " where CDTHOR = " + cle + " WITH UR ");
	}

	/**
	 * (cree par CQ le 13/06/2012 Retourne un ArrayList d'objet metier :
	 * Horaire.
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Horaire> listerHoraireSansNul(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " where CDTAUX <> 0 WITH UR ");
	}

	public ArrayList<Horaire> listerHoraireSansNulSansComplet(Transaction aTransaction) throws Exception {
		return executeSelectListe(aTransaction, "select * from " + getTable() + " where CDTAUX <> 0 and CDTAUX <> 1 WITH UR ");
	}
}
