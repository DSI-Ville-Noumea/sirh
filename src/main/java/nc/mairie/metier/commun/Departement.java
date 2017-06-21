package nc.mairie.metier.commun;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier Departement
 */
public class Departement extends BasicMetier {
	public String codDepartement;
	public String libDepartement;

	/**
	 * Constructeur Departement.
	 */
	public Departement() {
		super();
	}

	/**
	 * Retourne un ArrayList d'objet metier : Departement.
	 * @return java.util.Vector
	 */
	public static Departement chercherDepartementCommune(Transaction aTransaction, Commune aCommune) throws Exception {
		Departement unDepartement = new Departement();
		return unDepartement.getMyDepartementBroker().chercherDepartementCommune(aTransaction, aCommune.getCodDepartement());
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected BasicBroker definirMyBroker() {
		return new DepartementBroker(this);
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
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected DepartementBroker getMyDepartementBroker() {
		return (DepartementBroker) getMyBasicBroker();
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
	 * Renvoie une chaine correspondant a la valeur de cet objet.
	 * @return une representation sous forme de chaine du destinataire
	 */
	public String toString() {
		// Inserez ici le code pour finaliser le destinataire
		// Cette implementation transmet le message au super. Vous pouvez remplacer ou completer le message.
		return super.toString();
	}
}
