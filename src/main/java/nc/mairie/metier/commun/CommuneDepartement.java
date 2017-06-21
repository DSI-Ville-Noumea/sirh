package nc.mairie.metier.commun;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier CommuneDepartement
 */
public class CommuneDepartement extends BasicMetier {
	public String codCommune;
	public String libVille;
	public String codDepartement;
	public String libDepartement;

	/**
	 * Constructeur CommuneDepartement.
	 */
	public CommuneDepartement() {
		super();
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected BasicBroker definirMyBroker() {
		return new CommuneDepartementBroker(this);
	}

	/**
	 * Getter de l'attribut codCommune.
	 */
	public String getCodCommune() {
		return codCommune==null ?Const.CHAINE_VIDE : codCommune.trim();
	}

	/**
	 * Getter de l'attribut codDepartement.
	 */
	public String getCodDepartement() {
		return codDepartement;
	}

	/**
	 * Getter de l'attribut libDepartement.
	 */
	public String getLibDepartement() {
		return libDepartement;
	}

	/**
	 * Getter de l'attribut libVille.
	 */
	public String getLibVille() {
		return libVille==null ? Const.CHAINE_VIDE : libVille.trim();
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected CommuneDepartementBroker getMyCommuneDepartementBroker() {
		return (CommuneDepartementBroker) getMyBasicBroker();
	}

	/**
	 * Retourne un ArrayList d'objet metier : Commune.
	 * @return java.util.Vector
	 */
	public static ArrayList<CommuneDepartement> listerCommuneDepartementAvecCodCommuneCommencant(Transaction aTransaction, String texte) throws Exception {
		CommuneDepartement unCommuneDepartement = new CommuneDepartement();
		return unCommuneDepartement.getMyCommuneDepartementBroker().listerCommuneDepartementAvecCodCommuneCommencant(aTransaction, texte);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Commune.
	 * @return java.util.Vector
	 */
	public static ArrayList<CommuneDepartement> listerCommuneDepartementAvecLibCommuneCommencant(Transaction aTransaction, String texte) throws Exception {
		CommuneDepartement unCommuneDepartement = new CommuneDepartement();
		return unCommuneDepartement.getMyCommuneDepartementBroker().listerCommuneDepartementAvecLibCommuneCommencant(aTransaction, texte);
	}

	/**
	 * Setter de l'attribut codCommune.
	 */
	public void setCodCommune(String newCodCommune) {
		codCommune = newCodCommune;
	}

	/**
	 * Setter de l'attribut codDepartement.
	 */
	public void setCodDepartement(String newCodDepartement) {
		codDepartement = newCodDepartement;
	}

	/**
	 * Setter de l'attribut libDepartement.
	 */
	public void setLibDepartement(String newLibDepartement) {
		libDepartement = newLibDepartement;
	}

	/**
	 * Setter de l'attribut libVille.
	 */
	public void setLibVille(String newLibVille) {
		libVille = newLibVille;
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
