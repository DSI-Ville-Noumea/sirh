package nc.mairie.metier.carriere;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier FiliereGrade
 */
public class FiliereGrade extends BasicMetier {
	public String codeFiliere;
	public String libFiliere;

	/**
	 * Constructeur FiliereGrade.
	 */
	public FiliereGrade() {
		super();
	}

	/**
	 * Getter de l'attribut codeFiliere.
	 */
	public String getCodeFiliere() {
		return codeFiliere==null ? Const.CHAINE_VIDE : codeFiliere.trim();
	}

	/**
	 * Setter de l'attribut codeFiliere.
	 */
	public void setCodeFiliere(String newCodeFiliere) {
		codeFiliere = newCodeFiliere;
	}

	/**
	 * Getter de l'attribut libFiliere.
	 */
	public String getLibFiliere() {
		return libFiliere==null ? Const.CHAINE_VIDE : libFiliere.trim();
	}

	/**
	 * Setter de l'attribut libFiliere.
	 */
	public void setLibFiliere(String newLibFiliere) {
		libFiliere = newLibFiliere;
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected BasicBroker definirMyBroker() {
		return new FiliereGradeBroker(this);
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected FiliereGradeBroker getMyFiliereGradeBroker() {
		return (FiliereGradeBroker) getMyBasicBroker();
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
	 * Retourne un ArrayList d'objet metier : FiliereGrade.
	 * @return ArrayList
	 */
	public static ArrayList<FiliereGrade> listerFiliereGrade(Transaction aTransaction) throws Exception {
		FiliereGrade unFiliereGrade = new FiliereGrade();
		return unFiliereGrade.getMyFiliereGradeBroker().listerFiliereGrade(aTransaction);
	}

	/**
	 * Retourne un FiliereGrade.
	 * @return FiliereGrade
	 */
	public static FiliereGrade chercherFiliereGrade(Transaction aTransaction, String code) throws Exception {
		FiliereGrade unFiliereGrade = new FiliereGrade();
		return unFiliereGrade.getMyFiliereGradeBroker().chercherFiliereGrade(aTransaction, code);
	}
}
