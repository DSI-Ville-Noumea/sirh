package nc.mairie.metier.poste;

import java.util.ArrayList;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier Horaire
 */
public class Horaire extends BasicMetier {
	public String cdtHor;
	public String libHor;
	public String cdTaux;

	/**
	 * Constructeur Horaire.
	 */
	public Horaire() {
		super();
	}

	/**
	 * Getter de l'attribut cdtHor.
	 */
	public String getCdtHor() {
		return cdtHor;
	}

	/**
	 * Setter de l'attribut cdtHor.
	 */
	public void setCdtHor(String newCdtHor) {
		cdtHor = newCdtHor;
	}

	/**
	 * Getter de l'attribut libHor.
	 */
	public String getLibHor() {
		return libHor;
	}

	/**
	 * Setter de l'attribut libHor.
	 */
	public void setLibHor(String newLibHor) {
		libHor = newLibHor;
	}

	/**
	 * Getter de l'attribut cdTaux.
	 */
	public String getCdTaux() {
		return cdTaux;
	}

	/**
	 * Setter de l'attribut cdTaux.
	 */
	public void setCdTaux(String newCdTaux) {
		cdTaux = newCdTaux;
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected BasicBroker definirMyBroker() {
		return new HoraireBroker(this);
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected HoraireBroker getMyHoraireBroker() {
		return (HoraireBroker) getMyBasicBroker();
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
	 * Retourne un ArrayList d'objet metier : Horaire.
	 * @return ArrayList
	 */
	public static ArrayList<Horaire> listerHoraire(Transaction aTransaction) throws Exception {
		Horaire unHoraire = new Horaire();
		return unHoraire.getMyHoraireBroker().listerHoraire(aTransaction);
	}

	/**
	 * Retourne un Horaire.
	 * @return Horaire
	 */
	public static Horaire chercherHoraire(Transaction aTransaction, String code) throws Exception {
		Horaire unHoraire = new Horaire();
		return unHoraire.getMyHoraireBroker().chercherHoraire(aTransaction, code);
	}

	/** (cree par CQ le 13/06/2012)
	 * Retourne un ArrayList d'objet metier : Horaire sans le "nul".
	 * @return ArrayList
	 */
	public static ArrayList<Horaire> listerHoraireSansNul(Transaction aTransaction) throws Exception {
		Horaire unHoraire = new Horaire();
		return unHoraire.getMyHoraireBroker().listerHoraireSansNul(aTransaction);
	}

	public static ArrayList<Horaire> listerHoraireSansNulSansComplet(Transaction aTransaction)  throws Exception {
		Horaire unHoraire = new Horaire();
		return unHoraire.getMyHoraireBroker().listerHoraireSansNulSansComplet(aTransaction);
	}
}
