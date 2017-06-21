package nc.mairie.metier.commun;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier CommunePostal
 */
public class CommunePostal extends BasicMetier {
	public String codCodePostal;
	public String libCodePostal;
	public String codCommune;
	public String libCommune;

	/**
	 * Constructeur CommunePostal.
	 */
	public CommunePostal() {
		super();
	}

	/**
	 * Retourne : CommunePostal.
	 * 
	 * @return CommunePostal
	 */
	public static CommunePostal chercherCommunePostal(Transaction aTransaction, Integer codCodePostal, Integer codCommunePostal) throws Exception {
		CommunePostal unCommunePostal = new CommunePostal();
		return unCommunePostal.getMyCommunePostalBroker().chercherCommunePostal(aTransaction, codCodePostal.toString(), codCommunePostal.toString());
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected BasicBroker definirMyBroker() {
		return new CommunePostalBroker(this);
	}

	/**
	 * Getter de l'attribut codCodePostal.
	 */
	public String getCodCodePostal() {
		return codCodePostal;
	}

	/**
	 * Getter de l'attribut codCommune.
	 */
	public String getCodCommune() {
		return codCommune;
	}

	/**
	 * Getter de l'attribut libCodePostal.
	 */
	public String getLibCodePostal() {
		return libCodePostal == null ? Const.CHAINE_VIDE : libCodePostal.trim();
	}

	/**
	 * Getter de l'attribut libCommune.
	 */
	public String getLibCommune() {
		return libCommune == null ? Const.CHAINE_VIDE : libCommune.trim();
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected CommunePostalBroker getMyCommunePostalBroker() {
		return (CommunePostalBroker) getMyBasicBroker();
	}

	/**
	 * Retourne un ArrayList d'objet metier : Commune.
	 * 
	 * @return java.util.Vector
	 */
	public static ArrayList<CommunePostal> listerCommunePostalAvecCodCommuneCommencant(Transaction aTransaction, String texte) throws Exception {
		CommunePostal unCommunePostal = new CommunePostal();
		return unCommunePostal.getMyCommunePostalBroker().listerCommunePostalAvecCodCommuneCommencant(aTransaction, texte);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Commune.
	 * 
	 * @return java.util.Vector
	 */
	public static ArrayList<CommunePostal> listerCommunePostalAvecLibCommuneCommencant(Transaction aTransaction, String texte) throws Exception {
		CommunePostal unCommunePostal = new CommunePostal();
		return unCommunePostal.getMyCommunePostalBroker().listerCommunePostalAvecLibCommuneCommencant(aTransaction, texte);
	}

	/**
	 * Retourne la liste des "comunes postales" de Noumea.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<CommunePostal> listerCommunePostalNoumea(Transaction aTransaction) throws Exception {
		CommunePostal unCommunePostal = new CommunePostal();
		return unCommunePostal.getMyCommunePostalBroker().listerCommunePostalNoumea(aTransaction);
	}

	/**
	 * Retourne la liste des "comunes postales" hors Noumea.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<CommunePostal> listerCommunePostalHorsNoumea(Transaction aTransaction) throws Exception {
		CommunePostal unCommunePostal = new CommunePostal();
		return unCommunePostal.getMyCommunePostalBroker().listerCommunePostalHorsNoumea(aTransaction);
	}

	/**
	 * Setter de l'attribut codCodePostal.
	 */
	public void setCodCodePostal(String newCodCodePostal) {
		codCodePostal = newCodCodePostal;
	}

	/**
	 * Setter de l'attribut codCommune.
	 */
	public void setCodCommune(String newCodCommune) {
		codCommune = newCodCommune;
	}

	/**
	 * Setter de l'attribut libCodePostal.
	 */
	public void setLibCodePostal(String newLibCodePostal) {
		libCodePostal = newLibCodePostal;
	}

	/**
	 * Setter de l'attribut libCommune.
	 */
	public void setLibCommune(String newLibCommune) {
		libCommune = newLibCommune;
	}

	/**
	 * Renvoie une chaine correspondant a la valeur de cet objet.
	 * 
	 * @return une representation sous forme de chaine du destinataire
	 */
	public String toString() {
		// Inserez ici le code pour finaliser le destinataire
		// Cette implementation transmet le message au super. Vous pouvez
		// remplacer ou completer le message.
		return super.toString();
	}
}
