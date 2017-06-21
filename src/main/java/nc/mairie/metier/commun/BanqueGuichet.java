package nc.mairie.metier.commun;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier BanqueGuichet
 */
public class BanqueGuichet extends BasicMetier {
	public String codBanque;
	public String libBanque;
	public String codGuichet;
	public String libGuichet;

	/**
	 * Constructeur BanqueGuichet.
	 */
	public BanqueGuichet() {
		super();
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected BasicBroker definirMyBroker() {
		return new BanqueGuichetBroker(this);
	}

	/**
	 * Getter de l'attribut codBanque.
	 */
	public String getCodBanque() {
		return codBanque;
	}

	/**
	 * Getter de l'attribut codGuichet.
	 */
	public String getCodGuichet() {
		return codGuichet;
	}

	/**
	 * Getter de l'attribut libBanque.
	 */
	public String getLibBanque() {
		return libBanque==null ? Const.CHAINE_VIDE : libBanque.trim();
	}

	/**
	 * Getter de l'attribut libGuichet.
	 */
	public String getLibGuichet() {
		return libGuichet;
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected BanqueGuichetBroker getMyBanqueGuichetBroker() {
		return (BanqueGuichetBroker) getMyBasicBroker();
	}

	/**
	 * Retourne un ArrayList d'objet metier : BanqueGuichet.
	 * @return java.util.Vector
	 */
	public static ArrayList<BanqueGuichet> listerBanqueGuichet(Transaction aTransaction) throws Exception {
		BanqueGuichet unBanqueGuichet = new BanqueGuichet();
		return unBanqueGuichet.getMyBanqueGuichetBroker().listerBanqueGuichet(aTransaction);
	}

	/**
	 * Retourne un BanqueGuichet.
	 * @param codeGuichet 
	 * @return BanqueGuichet
	 */
	public static BanqueGuichet chercherBanqueGuichet(Transaction aTransaction, String codeBanque, String codeGuichet) throws Exception {
		BanqueGuichet unBanqueGuichet = new BanqueGuichet();
		return unBanqueGuichet.getMyBanqueGuichetBroker().chercherBanqueGuichet(aTransaction, codeBanque, codeGuichet);
	}

	/**
	 * Setter de l'attribut codBanque.
	 */
	public void setCodBanque(String newCodBanque) {
		codBanque = newCodBanque;
	}

	/**
	 * Setter de l'attribut codGuichet.
	 */
	public void setCodGuichet(String newCodGuichet) {
		codGuichet = newCodGuichet;
	}

	/**
	 * Setter de l'attribut libBanque.
	 */
	public void setLibBanque(String newLibBanque) {
		libBanque = newLibBanque;
	}

	/**
	 * Setter de l'attribut libGuichet.
	 */
	public void setLibGuichet(String newLibGuichet) {
		libGuichet = newLibGuichet;
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
