package nc.mairie.metier.carriere;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier Classe
 */
public class Classe extends BasicMetier {
	public String codClasse;
	public String libClasse;

	/**
	 * Constructeur Classe.
	 */
	public Classe() {
		super();
	}

	/**
	 * Getter de l'attribut codClasse.
	 */
	public String getCodClasse() {
		return codClasse == null ? Const.CHAINE_VIDE : codClasse.trim();
	}

	/**
	 * Setter de l'attribut codClasse.
	 */
	public void setCodClasse(String newCodClasse) {
		codClasse = newCodClasse;
	}

	/**
	 * Getter de l'attribut libClasse.
	 */
	public String getLibClasse() {
		return libClasse == null ? Const.CHAINE_VIDE : libClasse.trim();
	}

	/**
	 * Setter de l'attribut libClasse.
	 */
	public void setLibClasse(String newLibClasse) {
		libClasse = newLibClasse;
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected BasicBroker definirMyBroker() {
		return new ClasseBroker(this);
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected ClasseBroker getMyClasseBroker() {
		return (ClasseBroker) getMyBasicBroker();
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
	 * Retourne un ArrayList d'objet metier : Classe.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Classe> listerClasse(Transaction aTransaction) throws Exception {
		Classe unClasse = new Classe();
		return unClasse.getMyClasseBroker().listerClasse(aTransaction);
	}

	/**
	 * Retourne un Classe.
	 * 
	 * @return Classe
	 */
	public static Classe chercherClasse(Transaction aTransaction, String code) throws Exception {
		Classe unClasse = new Classe();
		return unClasse.getMyClasseBroker().chercherClasse(aTransaction, code);
	}

	/**
	 * Methode creerObjetMetier qui retourne true ou false
	 */
	public boolean creerClasse(Transaction aTransaction) throws Exception {
		// Creation du Classe
		setCodClasse(getCodClasse().toUpperCase());
		setLibClasse(getLibClasse());
		return getMyClasseBroker().creerClasse(aTransaction);
	}

	/**
	 * Methode modifierObjetMetier qui retourne true ou false
	 */
	public boolean modifierClasse(Transaction aTransaction) throws Exception {
		// Modification du Classe
		setCodClasse(getCodClasse().toUpperCase());
		setLibClasse(getLibClasse());
		return getMyClasseBroker().modifierClasse(aTransaction);
	}
}