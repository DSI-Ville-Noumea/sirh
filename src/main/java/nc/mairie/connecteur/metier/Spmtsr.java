package nc.mairie.connecteur.metier;

import java.util.ArrayList;

import nc.mairie.metier.agent.Agent;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier Spmtsr
 */
public class Spmtsr extends BasicMetier {
	public String nomatr;
	public String servi;
	public String refarr;
	public String datdeb;
	public String datfin;
	public String cdecol;

	/**
	 * Constructeur Spmtsr.
	 */
	public Spmtsr() {
		super();
	}

	/**
	 * Getter de l'attribut nomatr.
	 */
	public String getNomatr() {
		return nomatr;
	}

	/**
	 * Setter de l'attribut nomatr.
	 */
	public void setNomatr(String newNomatr) {
		nomatr = newNomatr;
	}

	/**
	 * Getter de l'attribut servi.
	 */
	public String getServi() {
		return servi;
	}

	/**
	 * Setter de l'attribut servi.
	 */
	public void setServi(String newServi) {
		servi = newServi;
	}

	/**
	 * Getter de l'attribut refarr.
	 */
	public String getRefarr() {
		return refarr;
	}

	/**
	 * Setter de l'attribut refarr.
	 */
	public void setRefarr(String newRefarr) {
		refarr = newRefarr;
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
	 * Getter de l'attribut cdecol.
	 */
	public String getCdecol() {
		return cdecol;
	}

	/**
	 * Setter de l'attribut cdecol.
	 */
	public void setCdecol(String newCdecol) {
		cdecol = newCdecol;
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected BasicBroker definirMyBroker() {
		return new SpmtsrBroker(this);
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected SpmtsrBroker getMySpmtsrBroker() {
		return (SpmtsrBroker) getMyBasicBroker();
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
	 * Retourne un ArrayList d'objet metier : Spmtsr.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Spmtsr> listerSpmtsr(Transaction aTransaction) throws Exception {
		Spmtsr unSpmtsr = new Spmtsr();
		return unSpmtsr.getMySpmtsrBroker().listerSpmtsr(aTransaction);
	}

	/**
	 * Retourne un Spmtsr.
	 * 
	 * @return Spmtsr
	 */
	public static Spmtsr chercherSpmtsr(Transaction aTransaction, String code) throws Exception {
		Spmtsr unSpmtsr = new Spmtsr();
		return unSpmtsr.getMySpmtsrBroker().chercherSpmtsr(aTransaction, code);
	}

	/**
	 * Retourne un SPMTSR a partir d'un numero de matricule, un service et une
	 * date de debut
	 * 
	 * @param aTransaction
	 * @param noMatr
	 *            Numero de matricule
	 * @param idService
	 *            Id service
	 * @param dateDeb
	 *            Date de debut de l'affectation
	 * @return SpmTsr
	 * @throws Exception
	 */
	public static Spmtsr chercherSpmtsrAvecNoMatrDateDeb(Transaction aTransaction, Integer noMatr, String dateDeb)
			throws Exception {
		Spmtsr unSpmtsr = new Spmtsr();
		return unSpmtsr.getMySpmtsrBroker().chercherSpmtsrAvecNoMatrDateDeb(aTransaction, noMatr.toString(), dateDeb);
	}

	/**
	 * Methode creerObjetMetier qui retourne true ou false
	 */
	public boolean creerSpmtsr(Transaction aTransaction) throws Exception {
		// Creation du Spmtsr
		return getMySpmtsrBroker().creerSpmtsr(aTransaction);
	}

	/**
	 * Methode modifierObjetMetier qui retourne true ou false
	 */
	public boolean modifierSpmtsr(Transaction aTransaction) throws Exception {
		// Modification du Spmtsr
		return getMySpmtsrBroker().modifierSpmtsr(aTransaction);
	}

	/**
	 * Methode supprimerObjetMetier qui retourne true ou false
	 */
	public boolean supprimerSpmtsr(Transaction aTransaction) throws Exception {
		// Suppression de l'Spmtsr
		return getMySpmtsrBroker().supprimerSpmtsr(aTransaction);
	}

	public static Spmtsr chercherSpmtsrSansDatFin(Transaction aTransaction, Integer noMatr) throws Exception {
		Spmtsr unSpmtsr = new Spmtsr();
		return unSpmtsr.getMySpmtsrBroker().chercherSpmtsrSansDatFin(aTransaction, noMatr.toString());
	}

	public static ArrayList<Spmtsr> listerSpmtsrAvecAgentOrderDateDeb(Transaction aTransaction, Agent ag)
			throws Exception {
		Spmtsr unSpmtsr = new Spmtsr();
		return unSpmtsr.getMySpmtsrBroker().listerSpmtsrAvecAgentOrderDateDeb(aTransaction, ag.getNomatr().toString());
	}

	public static Spmtsr chercherSpmtsrAvecAgentEtDateDebut(Transaction aTransaction, Integer noMatricule,
			Integer datefin) throws Exception {
		Spmtsr unSpmtsr = new Spmtsr();
		return unSpmtsr.getMySpmtsrBroker().chercherSpmtsrAvecAgentEtDateDebut(aTransaction, noMatricule.toString(),
				datefin);
	}

	public static ArrayList<Spmtsr> listerSpmtsrAvecAgentAPartirDateOrderDateDeb(Transaction aTransaction, Agent ag,
			Integer date) throws Exception {
		Spmtsr unSpmtsr = new Spmtsr();
		return unSpmtsr.getMySpmtsrBroker().listerSpmtsrAvecAgentAPartirDateOrderDateDeb(aTransaction,
				ag.getNomatr().toString(), date);
	}
}
