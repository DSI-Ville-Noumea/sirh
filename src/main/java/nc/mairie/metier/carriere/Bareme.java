package nc.mairie.metier.carriere;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Services;
import nc.mairie.technique.Transaction;

/**
 * Objet metier Bareme
 */
public class Bareme extends BasicMetier {
	public String iban;
	public String ina;
	public String inm;

	/**
	 * Constructeur Bareme.
	 */
	public Bareme() {
		super();
	}

	/**
	 * Getter de l'attribut iban.
	 */
	public String getIban() {
		return iban == null ? Const.CHAINE_VIDE : iban.trim();
	}

	/**
	 * Setter de l'attribut iban.
	 */
	public void setIban(String newIban) {
		iban = newIban;
	}

	/**
	 * Getter de l'attribut ina.
	 */
	public String getIna() {
		return ina == null ? Const.CHAINE_VIDE : ina.trim();
	}

	/**
	 * Setter de l'attribut ina.
	 */
	public void setIna(String newIna) {
		ina = newIna;
	}

	/**
	 * Getter de l'attribut inm.
	 */
	public String getInm() {
		return inm == null ? Const.CHAINE_VIDE : inm.trim();
	}

	/**
	 * Setter de l'attribut inm.
	 */
	public void setInm(String newInm) {
		inm = newInm;
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected BasicBroker definirMyBroker() {
		return new BaremeBroker(this);
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected BaremeBroker getMyBaremeBroker() {
		return (BaremeBroker) getMyBasicBroker();
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

	/**
	 * Retourne un ArrayList d'objet metier : Bareme.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Bareme> listerBareme(Transaction aTransaction) throws Exception {
		Bareme unBareme = new Bareme();
		return unBareme.getMyBaremeBroker().listerBareme(aTransaction);
	}

	/**
	 * Retourne un Bareme.
	 * 
	 * @return Bareme
	 */
	public static Bareme chercherBareme(Transaction aTransaction, String code) throws Exception {
		Bareme unBareme = new Bareme();
		return unBareme.getMyBaremeBroker().chercherBareme(aTransaction, code);
	}

	/**
	 * Retourne un Bareme.
	 * 
	 * @return Bareme
	 */
	public static ArrayList<Bareme> listerBaremeByINM(Transaction aTransaction, String inm) throws Exception {
		Bareme unBareme = new Bareme();
		return unBareme.getMyBaremeBroker().listerBaremeByINM(aTransaction, inm);
	}
	
	public static Bareme getPreviousBareme(Transaction aTransaction, String inm) throws Exception {
		Bareme unBareme = new Bareme();
		return unBareme.getMyBaremeBroker().getPreviousBareme(aTransaction, inm);
	}

	/**
	 * Methode creerObjetMetier qui retourne true ou false
	 */
	public boolean creerBareme(Transaction aTransaction) throws Exception {
		// Creation du Bareme
		if (!Services.estNumerique(getIban())) {
			setIban(getIban().toUpperCase());
		}
		return getMyBaremeBroker().creerBareme(aTransaction);
	}

	/**
	 * Methode modifierObjetMetier qui retourne true ou false
	 */
	public boolean modifierBareme(Transaction aTransaction) throws Exception {
		// Modification du Bareme
		if (!Services.estNumerique(getIban())) {
			setIban(getIban().toUpperCase());
		}
		return getMyBaremeBroker().modifierBareme(aTransaction);
	}
}
