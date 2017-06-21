package nc.mairie.metier.agent;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier CodeAcci
 */
public class CodeAcci extends BasicMetier {
	public String cdacci;
	public String libacc;
	public String txpat;
	public String refemp;
	public String datdeb;
	public String datfin;

	/**
	 * Constructeur CodeAcci.
	 */
	public CodeAcci() {
		super();
	}

	/**
	 * Getter de l'attribut cdacci.
	 */
	public String getCdacci() {
		return cdacci;
	}

	/**
	 * Setter de l'attribut cdacci.
	 */
	public void setCdacci(String newCdacci) {
		cdacci = newCdacci;
	}

	/**
	 * Getter de l'attribut libacc.
	 */
	public String getLibacc() {
		return libacc==null ? Const.CHAINE_VIDE : libacc.trim();
	}

	/**
	 * Setter de l'attribut libacc.
	 */
	public void setLibacc(String newLibacc) {
		libacc = newLibacc;
	}

	/**
	 * Getter de l'attribut txpat.
	 */
	public String getTxpat() {
		return txpat;
	}

	/**
	 * Setter de l'attribut txpat.
	 */
	public void setTxpat(String newTxpat) {
		txpat = newTxpat;
	}

	/**
	 * Getter de l'attribut refemp.
	 */
	public String getRefemp() {
		return refemp;
	}

	/**
	 * Setter de l'attribut refemp.
	 */
	public void setRefemp(String newRefemp) {
		refemp = newRefemp;
	}

	/**
	 * Getter de l'attribut datdeb.
	 */
	public String getDatdeb() {
		return datdeb;
	}

	/**
	 * Setter de l'attribut datdeb.
	 */
	public void setDatdeb(String newDatdeb) {
		datdeb = newDatdeb;
	}

	/**
	 * Getter de l'attribut datfin.
	 */
	public String getDatfin() {
		return datfin;
	}

	/**
	 * Setter de l'attribut datfin.
	 */
	public void setDatfin(String newDatfin) {
		datfin = newDatfin;
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected BasicBroker definirMyBroker() {
		return new CodeAcciBroker(this);
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected CodeAcciBroker getMyCodeAcciBroker() {
		return (CodeAcciBroker) getMyBasicBroker();
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
	 * Retourne un ArrayList d'objet metier : CodeAcci.
	 * @return ArrayList
	 */
	public static ArrayList<CodeAcci> listerCodeAcci(Transaction aTransaction) throws Exception {
		CodeAcci unCodeAcci = new CodeAcci();
		return unCodeAcci.getMyCodeAcciBroker().listerCodeAcci(aTransaction);
	}

	/**
	 * Retourne un CodeAcci.
	 * @return CodeAcci
	 */
	public static CodeAcci chercherCodeAcci(Transaction aTransaction, String code) throws Exception {
		CodeAcci unCodeAcci = new CodeAcci();
		return unCodeAcci.getMyCodeAcciBroker().chercherCodeAcci(aTransaction, code);
	}
}
