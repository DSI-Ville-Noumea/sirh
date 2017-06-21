package nc.mairie.connecteur.metier;

import java.util.ArrayList;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier Spcopa
 */
public class Spcopa extends BasicMetier {
	public String cdcopa;
	public String licopa;

	/**
	 * Constructeur Spcopa.
	 */
	public Spcopa() {
		super();
	}

	/**
	 * Getter de l'attribut cdcopa.
	 */
	public String getCdcopa() {
		return cdcopa;
	}

	/**
	 * Setter de l'attribut cdcopa.
	 */
	public void setCdcopa(String newCdcopa) {
		cdcopa = newCdcopa;
	}

	/**
	 * Getter de l'attribut licopa.
	 */
	public String getLicopa() {
		return licopa;
	}

	/**
	 * Setter de l'attribut licopa.
	 */
	public void setLicopa(String newLicopa) {
		licopa = newLicopa;
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected BasicBroker definirMyBroker() {
		return new SpcopaBroker(this);
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected SpcopaBroker getMySpcopaBroker() {
		return (SpcopaBroker) getMyBasicBroker();
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
	 * Retourne un ArrayList d'objet metier : Spcopa.
	 * @return ArrayList
	 */
	public static ArrayList<Spcopa> listerSpcopa(Transaction aTransaction) throws Exception {
		Spcopa unSpcopa = new Spcopa();
		return unSpcopa.getMySpcopaBroker().listerSpcopa(aTransaction);
	}

	/**
	 * Retourne un Spcopa.
	 * @return Spcopa
	 */
	public static Spcopa chercherSpcopa(Transaction aTransaction, String libCommune) throws Exception {
		Spcopa unSpcopa = new Spcopa();
		return unSpcopa.getMySpcopaBroker().chercherSpcopa(aTransaction, libCommune);
	}
}
