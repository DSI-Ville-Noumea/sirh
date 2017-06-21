package nc.mairie.metier.carriere;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier Echelon
 */
public class Echelon extends BasicMetier {
	public String codEchelon;
	public String libEchelon;

	/**
	 * Constructeur Echelon.
	 */
	public Echelon() {
		super();
	}

	/**
	 * Getter de l'attribut codEchelon.
	 */
	public String getCodEchelon() {
		return codEchelon == null ? Const.CHAINE_VIDE : codEchelon.trim();
	}

	/**
	 * Setter de l'attribut codEchelon.
	 */
	public void setCodEchelon(String newCodEchelon) {
		codEchelon = newCodEchelon;
	}

	/**
	 * Getter de l'attribut libEchelon.
	 */
	public String getLibEchelon() {
		return libEchelon == null ? Const.CHAINE_VIDE : libEchelon.trim();
	}

	/**
	 * Setter de l'attribut libEchelon.
	 */
	public void setLibEchelon(String newLibEchelon) {
		libEchelon = newLibEchelon;
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected BasicBroker definirMyBroker() {
		return new EchelonBroker(this);
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected EchelonBroker getMyEchelonBroker() {
		return (EchelonBroker) getMyBasicBroker();
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

	/**
	 * Retourne un ArrayList d'objet metier : Echelon.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Echelon> listerEchelon(Transaction aTransaction) throws Exception {
		Echelon unEchelon = new Echelon();
		return unEchelon.getMyEchelonBroker().listerEchelon(aTransaction);
	}

	/**
	 * Retourne un Echelon.
	 * 
	 * @return Echelon
	 */
	public static Echelon chercherEchelon(Transaction aTransaction, String code) throws Exception {
		Echelon unEchelon = new Echelon();
		return unEchelon.getMyEchelonBroker().chercherEchelon(aTransaction, code);
	}

	/**
	 * Methode creerObjetMetier qui retourne true ou false
	 */
	public boolean creerEchelon(Transaction aTransaction) throws Exception {
		// Creation du Echelon
		setCodEchelon(getCodEchelon().toUpperCase());
		setLibEchelon(getLibEchelon());
		return getMyEchelonBroker().creerEchelon(aTransaction);
	}

	/**
	 * Methode modifierObjetMetier qui retourne true ou false
	 */
	public boolean modifierEchelon(Transaction aTransaction) throws Exception {
		// Modification du Echelon
		setCodEchelon(getCodEchelon().toUpperCase());
		setLibEchelon(getLibEchelon());
		return getMyEchelonBroker().modifierEchelon(aTransaction);
	}
}
