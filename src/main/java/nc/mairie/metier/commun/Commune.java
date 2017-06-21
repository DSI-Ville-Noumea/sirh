package nc.mairie.metier.commun;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier Commune
 */
public class Commune extends BasicMetier {
	public String codCommune;
	public String libCommune;
	public String codDepartement;
	public String articleCommune;

	/**
	 * Constructeur Commune.
	 * 
	 * @param codCommune
	 * @param libCommune
	 * @param codDepartement
	 * @param articleCommune
	 */
	public Commune(String codCommune, String libCommune, String codDepartement, String articleCommune) {
		super();
		this.codCommune = codCommune;
		this.libCommune = libCommune;
		this.codDepartement = codDepartement;
		this.articleCommune = articleCommune;
	}

	/**
	 * Constructeur Commune.
	 */
	public Commune() {
		super();
	}

	/**
	 * Retourne un ArrayList d'objet metier : Commune.
	 * 
	 * @return java.util.Vector
	 */
	public static Commune chercherCommune(Transaction aTransaction, Integer codCommune) throws Exception {
		Commune unCommune = new Commune();
		return unCommune.getMyCommuneBroker().chercherCommune(aTransaction, codCommune.toString());
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected BasicBroker definirMyBroker() {
		return new CommuneBroker(this);
	}

	/**
	 * Getter de l'attribut articleCommune.
	 */
	public String getArticleCommune() {
		return articleCommune;
	}

	/**
	 * Getter de l'attribut codCommune.
	 */
	public String getCodCommune() {
		return codCommune;
	}

	/**
	 * Getter de l'attribut codDepartement.
	 */
	public String getCodDepartement() {
		return codDepartement;
	}

	/**
	 * Getter de l'attribut libVille.
	 */
	public String getLibCommune() {
		return libCommune == null ? Const.CHAINE_VIDE : libCommune.trim();
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected CommuneBroker getMyCommuneBroker() {
		return (CommuneBroker) getMyBasicBroker();
	}

	/**
	 * Setter de l'attribut articleCommune.
	 */
	public void setArticleCommune(String newArticleCommune) {
		articleCommune = newArticleCommune;
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
	 * Setter de l'attribut libVille.
	 */
	public void setLibCommune(String newLibCommune) {
		libCommune = newLibCommune;
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
