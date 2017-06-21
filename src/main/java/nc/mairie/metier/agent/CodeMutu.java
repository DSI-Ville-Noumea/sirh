package nc.mairie.metier.agent;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier CodeMutu
 */
public class CodeMutu extends BasicMetier {
	public String cdmutu;
	public String txsal;
	public String txpat;
	public String limutu;

	/**
	 * Constructeur CodeMutu.
	 */
	public CodeMutu() {
		super();
	}

	/**
	 * Getter de l'attribut cdmutu.
	 */
	public String getCdmutu() {
		return cdmutu;
	}

	/**
	 * Setter de l'attribut cdmutu.
	 */
	public void setCdmutu(String newCdmutu) {
		cdmutu = newCdmutu;
	}

	/**
	 * Getter de l'attribut txsal.
	 */
	public String getTxsal() {
		return txsal;
	}

	/**
	 * Setter de l'attribut txsal.
	 */
	public void setTxsal(String newTxsal) {
		txsal = newTxsal;
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
	 * Getter de l'attribut limutu.
	 */
	public String getLimutu() {
		return limutu==null ? Const.CHAINE_VIDE : limutu.trim();
	}

	/**
	 * Setter de l'attribut limutu.
	 */
	public void setLimutu(String newLimutu) {
		limutu = newLimutu;
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected BasicBroker definirMyBroker() {
		return new CodeMutuBroker(this);
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected CodeMutuBroker getMyCodeMutuBroker() {
		return (CodeMutuBroker) getMyBasicBroker();
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
	 * Retourne un ArrayList d'objet metier : CodeMutu.
	 * @return ArrayList
	 */
	public static ArrayList<CodeMutu> listerCodeMutu(Transaction aTransaction) throws Exception {
		CodeMutu unCodeMutu = new CodeMutu();
		return unCodeMutu.getMyCodeMutuBroker().listerCodeMutu(aTransaction);
	}

	/**
	 * Retourne un CodeMutu.
	 * @return CodeMutu
	 */
	public static CodeMutu chercherCodeMutu(Transaction aTransaction, String code) throws Exception {
		CodeMutu unCodeMutu = new CodeMutu();
		return unCodeMutu.getMyCodeMutuBroker().chercherCodeMutu(aTransaction, code);
	}
}
