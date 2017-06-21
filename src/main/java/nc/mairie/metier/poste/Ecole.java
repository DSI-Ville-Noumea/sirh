package nc.mairie.metier.poste;

import java.util.ArrayList;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier Ecole
 */
public class Ecole extends BasicMetier {
	public String cdecol;
	public String liecol;
	public String quecol;
	public String secter;

	/**
	 * Constructeur Ecole.
	 */
	public Ecole() {
		super();
	}

	/**
	 * Getter de l'attribut cdecol.
	 */
	public String getCdecol() {
		return cdecol;
	}

	/**
	 * Setter de l'attribut cdecol.
	 */
	public void setCdecol(String newCdecol) {
		cdecol = newCdecol;
	}

	/**
	 * Getter de l'attribut liecol.
	 */
	public String getLiecol() {
		return liecol;
	}

	/**
	 * Setter de l'attribut liecol.
	 */
	public void setLiecol(String newLiecol) {
		liecol = newLiecol;
	}

	/**
	 * Getter de l'attribut quecol.
	 */
	public String getQuecol() {
		return quecol;
	}

	/**
	 * Setter de l'attribut quecol.
	 */
	public void setQuecol(String newQuecol) {
		quecol = newQuecol;
	}

	/**
	 * Getter de l'attribut secter.
	 */
	public String getSecter() {
		return secter;
	}

	/**
	 * Setter de l'attribut secter.
	 */
	public void setSecter(String newSecter) {
		secter = newSecter;
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected BasicBroker definirMyBroker() {
		return new EcoleBroker(this);
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected EcoleBroker getMyEcoleBroker() {
		return (EcoleBroker) getMyBasicBroker();
	}

	/**
	 * Renvoie une chaine correspondant a la valeur de cet objet.
	 * @return une representation sous forme de chaine du destinataire
	 */
	public String toString() {
		// Inserez ici le code pour finaliser le destinataire
		// Cette implementation transmet le message au super. Vous pouvez remplacer ou completer le message.
		return super.toString();
	}

	/**
	 * Retourne un ArrayList d'objet metier : Ecole.
	 * @return ArrayList
	 */
	public static ArrayList<Ecole> listerEcole(Transaction aTransaction) throws Exception {
		Ecole unEcole = new Ecole();
		return unEcole.getMyEcoleBroker().listerEcole(aTransaction);
	}

	/**
	 * Retourne un Ecole.
	 * @return Ecole
	 */
	public static Ecole chercherEcole(Transaction aTransaction, String code) throws Exception {
		Ecole unEcole = new Ecole();
		return unEcole.getMyEcoleBroker().chercherEcole(aTransaction, code);
	}

	/**
	 * Methode creerObjetMetier qui retourne
	 * true ou false
	 */
	public boolean creerEcole(Transaction aTransaction) throws Exception {
		//Creation du Ecole
		setLiecol(getLiecol().toUpperCase());
		return getMyEcoleBroker().creerEcole(aTransaction);
	}

	/**
	 * Methode modifierObjetMetier qui retourne
	 * true ou false
	 */
	public boolean modifierEcole(Transaction aTransaction) throws Exception {
		//Modification de l'ecole
		setLiecol(getLiecol().toUpperCase());
		return getMyEcoleBroker().modifierEcole(aTransaction);
	}

	/**
	 * Methode supprimerObjetMetier qui retourne
	 * true ou false
	 */
	public boolean supprimerEcole(Transaction aTransaction) throws Exception {
		//Suppression de l'Ecole
		return getMyEcoleBroker().supprimerEcole(aTransaction);
	}
}
