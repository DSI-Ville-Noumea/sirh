package nc.mairie.metier.commun;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier VoieQuartier
 */
public class VoieQuartier extends BasicMetier {
	public String codCommune;
	public String codVoie;
	public String libVoie;
	public String codQuartier;
	public String libQuartier;

	/**
	 * Constructeur VoieQuartier.
	 */
	public VoieQuartier() {
		super();
	}

	/**
	 * Retourne un ArrayList d'objet metier : VoieQuartier.
	 * @return java.util.Vector
	 */
	public static VoieQuartier chercherVoieQuartierAvecCodVoie(Transaction aTransaction, String codVoie) throws Exception {
		VoieQuartier unVoieQuartier = new VoieQuartier();
		return unVoieQuartier.getMyVoieQuartierBroker().chercherVoieQuartierAvecCodVoie(aTransaction, codVoie);
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected BasicBroker definirMyBroker() {
		return new VoieQuartierBroker(this);
	}

	/**
	 * Getter de l'attribut codCommune.
	 */
	public String getCodCommune() {
		return codCommune;
	}

	/**
	 * Getter de l'attribut codQuartier.
	 */
	public String getCodQuartier() {
		return codQuartier;
	}

	/**
	 * Getter de l'attribut codVoie.
	 */
	public String getCodVoie() {
		return codVoie==null ? Const.CHAINE_VIDE : codVoie.trim();
	}

	/**
	 * Getter de l'attribut libQuartier.
	 */
	public String getLibQuartier() {
		return libQuartier != null ? libQuartier.trim() : Const.CHAINE_VIDE;
	}

	/**
	 * Getter de l'attribut libVoie.
	 */
	public String getLibVoie() {
		return libVoie==null ? Const.CHAINE_VIDE : libVoie.trim();
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected VoieQuartierBroker getMyVoieQuartierBroker() {
		return (VoieQuartierBroker) getMyBasicBroker();
	}

	/**
	 * Retourne un ArrayList d'objet metier : VoieQuartier.
	 * @return java.util.Vector
	 */
	public static ArrayList<VoieQuartier> listerVoieQuartierAvecLibVoieContenant(Transaction aTransaction, String texte) throws Exception {
		VoieQuartier unVoieQuartier = new VoieQuartier();
		return unVoieQuartier.getMyVoieQuartierBroker().listerVoieQuartierAvecLibVoieContenant(aTransaction, texte);
	}

	/**
	 * Setter de l'attribut codCommune.
	 */
	public void setCodCommune(String newCodCommune) {
		codCommune = newCodCommune;
	}

	/**
	 * Setter de l'attribut codQuartier.
	 */
	public void setCodQuartier(String newCodQuartier) {
		codQuartier = newCodQuartier;
	}

	/**
	 * Setter de l'attribut codVoie.
	 */
	public void setCodVoie(String newCodVoie) {
		codVoie = newCodVoie;
	}

	/**
	 * Setter de l'attribut libQuartier.
	 */
	public void setLibQuartier(String newLibQuartier) {
		libQuartier = newLibQuartier;
	}

	/**
	 * Setter de l'attribut libVoie.
	 */
	public void setLibVoie(String newLibVoie) {
		libVoie = newLibVoie;
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
}
