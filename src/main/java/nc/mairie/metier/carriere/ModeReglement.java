package nc.mairie.metier.carriere;

import java.util.ArrayList;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier ModeReglement
 */
public class ModeReglement extends BasicMetier {
	public String modReg;
	public String libModReg;

	/**
	 * Constructeur ModeReglement.
	 */
	public ModeReglement() {
		super();
	}

	/**
	 * Getter de l'attribut modReg.
	 */
	public String getModReg() {
		return modReg;
	}

	/**
	 * Setter de l'attribut modReg.
	 */
	public void setModReg(String newModReg) {
		modReg = newModReg;
	}

	/**
	 * Getter de l'attribut libModReg.
	 */
	public String getLibModReg() {
		return libModReg;
	}

	/**
	 * Setter de l'attribut libModReg.
	 */
	public void setLibModReg(String newLibModReg) {
		libModReg = newLibModReg;
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected BasicBroker definirMyBroker() {
		return new ModeReglementBroker(this);
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected ModeReglementBroker getMyModeReglementBroker() {
		return (ModeReglementBroker) getMyBasicBroker();
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
	 * Retourne un ArrayList d'objet metier : ModeReglement.
	 * @return ArrayList
	 */
	public static ArrayList<ModeReglement> listerModeReglement(Transaction aTransaction) throws Exception {
		ModeReglement unModeReglement = new ModeReglement();
		return unModeReglement.getMyModeReglementBroker().listerModeReglement(aTransaction);
	}
}
