package nc.mairie.metier.commun;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier CommuneEtrangere
 */
public class CommuneEtrangere extends BasicMetier {
	public String codPays;
	public String codCommuneEtrangere;
	public String libCommuneEtrangere;

	/**
	 * Constructeur CommuneEtrangere.
	 */
	public CommuneEtrangere() {
		super();
	}

	/**
	 * Retourne CommuneEtrangere.
	 * 
	 * @return CommuneEtrangere
	 */
	public static CommuneEtrangere chercherCommuneEtrangere(Transaction aTransaction, String codPays, Integer scdPays)
			throws Exception {
		CommuneEtrangere unCommuneEtrangere = new CommuneEtrangere();
		return unCommuneEtrangere.getMyCommuneEtrangereBroker().chercherCommuneEtrangere(aTransaction, codPays,
				scdPays.toString());
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected BasicBroker definirMyBroker() {
		return new CommuneEtrangereBroker(this);
	}

	/**
	 * Getter de l'attribut codCommuneEtrangere.
	 */
	public String getCodCommuneEtrangere() {
		return codCommuneEtrangere == null ? Const.CHAINE_VIDE : codCommuneEtrangere.trim();
	}

	/**
	 * Getter de l'attribut codPays.
	 */
	public String getCodPays() {
		return codPays;
	}

	/**
	 * Getter de l'attribut libCommuneEtrangere.
	 */
	public String getLibCommuneEtrangere() {
		return libCommuneEtrangere == null ? Const.CHAINE_VIDE : libCommuneEtrangere.trim();
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected CommuneEtrangereBroker getMyCommuneEtrangereBroker() {
		return (CommuneEtrangereBroker) getMyBasicBroker();
	}

	/**
	 * Retourne un ArrayList d'objet metier : CommuneEtrangere.
	 * 
	 * @return java.util.Vector
	 */
	public static ArrayList<CommuneEtrangere> listerCommuneEtrangerePays(Transaction aTransaction, Pays aPays)
			throws Exception {
		CommuneEtrangere unCommuneEtrangere = new CommuneEtrangere();
		return unCommuneEtrangere.getMyCommuneEtrangereBroker().listerCommuneEtrangerePays(aTransaction,
				aPays.getCodPays());
	}

	/**
	 * Setter de l'attribut codCommuneEtrangere.
	 */
	public void setCodCommuneEtrangere(String newCodCommuneEtrangere) {
		codCommuneEtrangere = newCodCommuneEtrangere;
	}

	/**
	 * Setter de l'attribut codPays.
	 */
	public void setCodPays(String newCodPays) {
		codPays = newCodPays;
	}

	/**
	 * Setter de l'attribut libCommuneEtrangere.
	 */
	public void setLibCommuneEtrangere(String newLibCommuneEtrangere) {
		libCommuneEtrangere = newLibCommuneEtrangere;
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
