package nc.mairie.metier.poste;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier EntiteGeo
 */
public class EntiteGeo extends BasicMetier {
	public String idEntiteGeo;
	public String libEntiteGeo;
	public String ridet;
	public String cdEcol;

	/**
	 * Constructeur EntiteGeo.
	 */
	public EntiteGeo() {
		super();
	}

	/**
	 * Getter de l'attribut idEntiteGeo.
	 */
	public String getIdEntiteGeo() {
		return idEntiteGeo;
	}

	/**
	 * Setter de l'attribut idEntiteGeo.
	 */
	public void setIdEntiteGeo(String newIdEntiteGeo) {
		idEntiteGeo = newIdEntiteGeo;
	}

	/**
	 * Getter de l'attribut libEntiteGeo.
	 */
	public String getLibEntiteGeo() {
		return libEntiteGeo==null ? Const.CHAINE_VIDE : libEntiteGeo.trim();
	}

	/**
	 * Setter de l'attribut libEntiteGeo.
	 */
	public void setLibEntiteGeo(String newLibEntiteGeo) {
		libEntiteGeo = newLibEntiteGeo;
	}

	/**
	 * @return cdEcol
	 */
	public String getCdEcol() {
		return cdEcol;
	}

	/**
	 * @param cdEcol cdEcol a definir
	 */
	public void setCdEcol(String cdEcol) {
		this.cdEcol = cdEcol;
	}

	/**
	 * @return ridet
	 */
	public String getRidet() {
		return ridet;
	}

	/**
	 * @param ridet ridet a definir
	 */
	public void setRidet(String ridet) {
		this.ridet = ridet;
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected BasicBroker definirMyBroker() {
		return new EntiteGeoBroker(this);
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected EntiteGeoBroker getMyEntiteGeoBroker() {
		return (EntiteGeoBroker) getMyBasicBroker();
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
	 * Retourne un ArrayList d'objet metier : EntiteGeo.
	 * @return ArrayList
	 */
	public static ArrayList<EntiteGeo> listerEntiteGeo(Transaction aTransaction) throws Exception {
		EntiteGeo unEntiteGeo = new EntiteGeo();
		return unEntiteGeo.getMyEntiteGeoBroker().listerEntiteGeo(aTransaction);
	}

	/**
	 * Retourne un EntiteGeo.
	 * @return EntiteGeo
	 */
	public static EntiteGeo chercherEntiteGeo(Transaction aTransaction, String code) throws Exception {
		EntiteGeo unEntiteGeo = new EntiteGeo();
		return unEntiteGeo.getMyEntiteGeoBroker().chercherEntiteGeo(aTransaction, code);
	}

	/**
	 * Methode creerObjetMetier qui retourne
	 * true ou false
	 */
	public boolean creerEntiteGeo(Transaction aTransaction) throws Exception {
		//Affectation du matricule et de l'id
		setIdEntiteGeo(String.valueOf(getMyEntiteGeoBroker().recupMaxCdLieu(aTransaction) + 1));
		setLibEntiteGeo(getLibEntiteGeo().toUpperCase());
		//Creation du EntiteGeo
		return getMyEntiteGeoBroker().creerEntiteGeo(aTransaction);
	}

	/**
	 * Methode supprimerObjetMetier qui retourne
	 * true ou false
	 */
	public boolean supprimerEntiteGeo(Transaction aTransaction) throws Exception {
		//Suppression de l'EntiteGeo
		return getMyEntiteGeoBroker().supprimerEntiteGeo(aTransaction);
	}

	/**
	 * Methode modifierObjetMetier qui retourne
	 * true ou false
	 */
	public boolean modifierEntiteGeo(Transaction aTransaction) throws Exception {
		//Modification de l'EntiteGeo
		setLibEntiteGeo(getLibEntiteGeo().toUpperCase());
		return getMyEntiteGeoBroker().modifierEntiteGeo(aTransaction);
	}
}
