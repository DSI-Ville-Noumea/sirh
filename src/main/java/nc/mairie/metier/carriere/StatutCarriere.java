package nc.mairie.metier.carriere;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier StatutCarriere
 */
public class StatutCarriere extends BasicMetier {
	public String cdCate;
	public String liCate;

	/**
	 * Constructeur StatutCarriere.
	 */
	public StatutCarriere() {
		super();
	}

	/**
	 * Getter de l'attribut cdCate.
	 */
	public String getCdCate() {
		return cdCate;
	}

	/**
	 * Setter de l'attribut cdCate.
	 */
	public void setCdCate(String newCdCate) {
		cdCate = newCdCate;
	}

	/**
	 * Getter de l'attribut liCate.
	 */
	public String getLiCate() {
		return liCate==null ? Const.CHAINE_VIDE : liCate.trim();
	}

	/**
	 * Setter de l'attribut liCate.
	 */
	public void setLiCate(String newLiCate) {
		liCate = newLiCate;
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected BasicBroker definirMyBroker() {
		return new StatutCarriereBroker(this);
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected StatutCarriereBroker getMyStatutCarriereBroker() {
		return (StatutCarriereBroker) getMyBasicBroker();
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
	 * Retourne un StatutCarriere.
	 * @return StatutCarriere
	 */
	public static StatutCarriere chercherStatutCarriere(Transaction aTransaction, String cdcate) throws Exception {
		StatutCarriere unStatutCarriere = new StatutCarriere();
		return unStatutCarriere.getMyStatutCarriereBroker().chercherStatutCarriere(aTransaction, cdcate);
	}

	/**
	 * Retourne un StatutCarriere.
	 * @return StatutCarriere
	 */
	public static ArrayList<StatutCarriere> listerStatutCarriere(Transaction aTransaction) throws Exception {
		StatutCarriere unStatutCarriere = new StatutCarriere();
		return unStatutCarriere.getMyStatutCarriereBroker().listerStatutCarriere(aTransaction);
	}
}
