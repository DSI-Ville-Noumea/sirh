package nc.mairie.metier.agent;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier PositionAdm
 */
public class PositionAdm extends BasicMetier {
	public String cdpadm;
	public String liPAdm;
	public String droitc;
	public String duree;
	public String posit;

	/**
	 * Constructeur PositionAdm.
	 */
	public PositionAdm() {
		super();
	}

	/**
	 * Getter de l'attribut cdpadm.
	 */
	public String getCdpadm() {
		return cdpadm==null ? Const.CHAINE_VIDE : cdpadm.trim();
	}

	/**
	 * Setter de l'attribut cdpadm.
	 */
	public void setCdpadm(String newCdpadm) {
		cdpadm = newCdpadm;
	}

	/**
	 * Getter de l'attribut liPAdm.
	 */
	public String getLiPAdm() {
		return liPAdm==null ? Const.CHAINE_VIDE : liPAdm.trim();
	}

	/**
	 * Setter de l'attribut liPAdm.
	 */
	public void setLiPAdm(String newLiPAdm) {
		liPAdm = newLiPAdm;
	}

	/**
	 * Getter de l'attribut droitc.
	 */
	public String getDroitc() {
		return droitc;
	}

	/**
	 * Setter de l'attribut droitc.
	 */
	public void setDroitc(String newDroitc) {
		droitc = newDroitc;
	}

	/**
	 * Getter de l'attribut duree.
	 */
	public String getDuree() {
		return duree;
	}

	/**
	 * Setter de l'attribut duree.
	 */
	public void setDuree(String newDuree) {
		duree = newDuree;
	}

	/**
	 * Getter de l'attribut posit.
	 */
	public String getPosit() {
		return posit;
	}

	/**
	 * Setter de l'attribut posit.
	 */
	public void setPosit(String newPosit) {
		posit = newPosit;
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected BasicBroker definirMyBroker() {
		return new PositionAdmBroker(this);
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected PositionAdmBroker getMyPositionAdmBroker() {
		return (PositionAdmBroker) getMyBasicBroker();
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
	 * Retourne un ArrayList d'objet metier : PositionAdm.
	 * @return ArrayList
	 */
	public static ArrayList<PositionAdm> listerPositionAdm(Transaction aTransaction) throws Exception {
		PositionAdm unPositionAdm = new PositionAdm();
		return unPositionAdm.getMyPositionAdmBroker().listerPositionAdm(aTransaction);
	}

	/**
	 * Retourne un PositionAdm.
	 * @return PositionAdm
	 */
	public static PositionAdm chercherPositionAdm(Transaction aTransaction, String code) throws Exception {
		PositionAdm unPositionAdm = new PositionAdm();
		return unPositionAdm.getMyPositionAdmBroker().chercherPositionAdm(aTransaction, code);
	}
}
