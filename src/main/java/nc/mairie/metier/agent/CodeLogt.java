package nc.mairie.metier.agent;

import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier CodeLogt
 */
public class CodeLogt extends BasicMetier {
	public String cdlogt;
	public String liblog;
	public String txsal;
	public String datdeb;
	public String datfin;

	/**
	 * Constructeur CodeLogt.
	 */
	public CodeLogt() {
		super();
	}

	/**
	 * Getter de l'attribut cdlogt.
	 */
	public String getCdlogt() {
		return cdlogt;
	}

	/**
	 * Setter de l'attribut cdlogt.
	 */
	public void setCdlogt(String newCdlogt) {
		cdlogt = newCdlogt;
	}

	/**
	 * Getter de l'attribut liblog.
	 */
	public String getLiblog() {
		return liblog==null ? Const.CHAINE_VIDE : liblog.trim();
	}

	/**
	 * Setter de l'attribut liblog.
	 */
	public void setLiblog(String newLiblog) {
		liblog = newLiblog;
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
		return new CodeLogtBroker(this);
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected CodeLogtBroker getMyCodeLogtBroker() {
		return (CodeLogtBroker) getMyBasicBroker();
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
	 * Retourne un ArrayList d'objet metier : CodeLogt.
	 * @return ArrayList
	 */
	public static ArrayList<CodeLogt> listerCodeLogt(Transaction aTransaction) throws Exception {
		CodeLogt unCodeLogt = new CodeLogt();
		return unCodeLogt.getMyCodeLogtBroker().listerCodeLogt(aTransaction);
	}

	/**
	 * Retourne un CodeLogt.
	 * @return CodeLogt
	 */
	public static CodeLogt chercherCodeLogt(Transaction aTransaction, String code) throws Exception {
		CodeLogt unCodeLogt = new CodeLogt();
		return unCodeLogt.getMyCodeLogtBroker().chercherCodeLogt(aTransaction, code);
	}
}
