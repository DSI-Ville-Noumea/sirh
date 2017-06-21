package nc.mairie.metier.agent;

import java.util.ArrayList;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier CodeChargeLogt
 */
public class CodeChargeLogt extends BasicMetier {
	public String cdlogt;
	public String liblog;
	public String mtregr;
	public String datdeb;
	public String datfin;

	/**
	 * Constructeur CodeChargeLogt.
	 */
	public CodeChargeLogt() {
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
		return liblog;
	}

	/**
	 * Setter de l'attribut liblog.
	 */
	public void setLiblog(String newLiblog) {
		liblog = newLiblog;
	}

	/**
	 * Getter de l'attribut mtregr.
	 */
	public String getMtregr() {
		return mtregr;
	}

	/**
	 * Setter de l'attribut mtregr.
	 */
	public void setMtregr(String newMtregr) {
		mtregr = newMtregr;
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
		return new CodeChargeLogtBroker(this);
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected CodeChargeLogtBroker getMyCodeChargeLogtBroker() {
		return (CodeChargeLogtBroker) getMyBasicBroker();
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
	 * Retourne un ArrayList d'objet metier : CodeChargeLogt.
	 * @return ArrayList
	 */
	public static ArrayList<CodeChargeLogt> listerCodeChargeLogt(Transaction aTransaction) throws Exception {
		CodeChargeLogt unCodeChargeLogt = new CodeChargeLogt();
		return unCodeChargeLogt.getMyCodeChargeLogtBroker().listerCodeChargeLogt(aTransaction);
	}

	/**
	 * Retourne un CodeChargeLogt.
	 * @return CodeChargeLogt
	 */
	public static CodeChargeLogt chercherCodeChargeLogt(Transaction aTransaction, String code) throws Exception {
		CodeChargeLogt unCodeChargeLogt = new CodeChargeLogt();
		return unCodeChargeLogt.getMyCodeChargeLogtBroker().chercherCodeChargeLogt(aTransaction, code);
	}
}
