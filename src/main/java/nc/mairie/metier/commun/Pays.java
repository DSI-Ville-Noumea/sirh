package nc.mairie.metier.commun;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier Pays
 */
public class Pays extends BasicMetier {
	public String codPays;
	public String libPays;
	public String libNationalite;

	/**
	 * Constructeur Pays.
	 */
	public Pays() {
		super();
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected BasicBroker definirMyBroker() {
		return new PaysBroker(this);
	}

	/**
	 * Getter de l'attribut codPays.
	 */
	public String getCodPays() {
		return codPays == null ? Const.CHAINE_VIDE : codPays.trim();
	}

	/**
	 * Getter de l'attribut libNationalite.
	 */
	public String getLibNationalite() {
		return libNationalite;
	}

	/**
	 * Getter de l'attribut libpay.
	 */
	public String getLibPays() {
		return libPays == null ? Const.CHAINE_VIDE : libPays.trim();
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected PaysBroker getMyPaysBroker() {
		return (PaysBroker) getMyBasicBroker();
	}

	/**
	 * Retourne un objet metier Pays a partir de son code.
	 * 
	 * @return Pays
	 */
	public static Pays chercherPays(Transaction aTransaction, Integer codePays) throws Exception {
		Pays unPays = new Pays();
		return unPays.getMyPaysBroker().chercherPays(aTransaction, codePays.toString());
	}

	/**
	 * Retourne un ArrayList d'objet metier : Pays.
	 * 
	 * @return java.util.Vector
	 */
	public static ArrayList<Pays> listerPaysAvecCodPaysCommencant(Transaction aTransaction, String codPart)
			throws Exception {
		Pays unPays = new Pays();
		return unPays.getMyPaysBroker().listerPaysAvecCodPaysCommencant(aTransaction, codPart);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Pays.
	 * 
	 * @return java.util.Vector
	 */
	public static ArrayList<Pays> listerPaysAvecLibPaysCommencant(Transaction aTransaction, String libPart)
			throws Exception {
		Pays unPays = new Pays();
		return unPays.getMyPaysBroker().listerPaysAvecLibPaysCommencant(aTransaction, libPart);
	}

	/**
	 * Setter de l'attribut codPays.
	 */
	public void setCodPays(String newCodPays) {
		codPays = newCodPays;
	}

	/**
	 * Setter de l'attribut libNationalite.
	 */
	public void setLibNationalite(String newLibNationalite) {
		libNationalite = newLibNationalite;
	}

	/**
	 * Setter de l'attribut libpay.
	 */
	public void setLibPays(String newLibPays) {
		libPays = newLibPays;
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
