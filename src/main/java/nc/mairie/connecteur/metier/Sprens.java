package nc.mairie.connecteur.metier;

import java.util.ArrayList;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier Sprens
 */
public class Sprens extends BasicMetier {
	public String nomatr;
	public String nomp;
	public String prep;
	public String nomm;
	public String prem;
	public String nomc;
	public String prec;
	public String matc;
	public String datmar;
	public String prev;
	public String obs1;
	public String obs2;
	public String nbscol;
	public String cdcnai;
	public String cdchab;
	public String codevp;
	public String codnai;
	public String scodpa;

	/**
	 * Constructeur Sprens.
	 */
	public Sprens() {
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
	 * Getter de l'attribut nomp.
	 */
	public String getNomp() {
		return nomp;
	}

	/**
	 * Setter de l'attribut nomp.
	 */
	public void setNomp(String newNomp) {
		nomp = newNomp;
	}

	/**
	 * Getter de l'attribut prep.
	 */
	public String getPrep() {
		return prep;
	}

	/**
	 * Setter de l'attribut prep.
	 */
	public void setPrep(String newPrep) {
		prep = newPrep;
	}

	/**
	 * Getter de l'attribut nomm.
	 */
	public String getNomm() {
		return nomm;
	}

	/**
	 * Setter de l'attribut nomm.
	 */
	public void setNomm(String newNomm) {
		nomm = newNomm;
	}

	/**
	 * Getter de l'attribut prem.
	 */
	public String getPrem() {
		return prem;
	}

	/**
	 * Setter de l'attribut prem.
	 */
	public void setPrem(String newPrem) {
		prem = newPrem;
	}

	/**
	 * Getter de l'attribut nomc.
	 */
	public String getNomc() {
		return nomc;
	}

	/**
	 * Setter de l'attribut nomc.
	 */
	public void setNomc(String newNomc) {
		nomc = newNomc;
	}

	/**
	 * Getter de l'attribut prec.
	 */
	public String getPrec() {
		return prec;
	}

	/**
	 * Setter de l'attribut prec.
	 */
	public void setPrec(String newPrec) {
		prec = newPrec;
	}

	/**
	 * Getter de l'attribut matc.
	 */
	public String getMatc() {
		return matc;
	}

	/**
	 * Setter de l'attribut matc.
	 */
	public void setMatc(String newMatc) {
		matc = newMatc;
	}

	/**
	 * Getter de l'attribut datmar.
	 */
	public String getDatmar() {
		return datmar;
	}

	/**
	 * Setter de l'attribut datmar.
	 */
	public void setDatmar(String newDatmar) {
		datmar = newDatmar;
	}

	/**
	 * Getter de l'attribut prev.
	 */
	public String getPrev() {
		return prev;
	}

	/**
	 * Setter de l'attribut prev.
	 */
	public void setPrev(String newPrev) {
		prev = newPrev;
	}

	/**
	 * Getter de l'attribut obs1.
	 */
	public String getObs1() {
		return obs1;
	}

	/**
	 * Setter de l'attribut obs1.
	 */
	public void setObs1(String newObs1) {
		obs1 = newObs1;
	}

	/**
	 * Getter de l'attribut obs2.
	 */
	public String getObs2() {
		return obs2;
	}

	/**
	 * Setter de l'attribut obs2.
	 */
	public void setObs2(String newObs2) {
		obs2 = newObs2;
	}

	/**
	 * Getter de l'attribut nbscol.
	 */
	public String getNbscol() {
		return nbscol;
	}

	/**
	 * Setter de l'attribut nbscol.
	 */
	public void setNbscol(String newNbscol) {
		nbscol = newNbscol;
	}

	/**
	 * Getter de l'attribut cdcnai.
	 */
	public String getCdcnai() {
		return cdcnai;
	}

	/**
	 * Setter de l'attribut cdcnai.
	 */
	public void setCdcnai(String newCdcnai) {
		cdcnai = newCdcnai;
	}

	/**
	 * Getter de l'attribut cdchab.
	 */
	public String getCdchab() {
		return cdchab;
	}

	/**
	 * Setter de l'attribut cdchab.
	 */
	public void setCdchab(String newCdchab) {
		cdchab = newCdchab;
	}

	/**
	 * Getter de l'attribut codevp.
	 */
	public String getCodevp() {
		return codevp;
	}

	/**
	 * Setter de l'attribut codevp.
	 */
	public void setCodevp(String newCodevp) {
		codevp = newCodevp;
	}

	/**
	 * Getter de l'attribut codnai.
	 */
	public String getCodnai() {
		return codnai;
	}

	/**
	 * Setter de l'attribut codnai.
	 */
	public void setCodnai(String newCodnai) {
		codnai = newCodnai;
	}

	/**
	 * Getter de l'attribut scodpa.
	 */
	public String getScodpa() {
		return scodpa;
	}

	/**
	 * Setter de l'attribut scodpa.
	 */
	public void setScodpa(String newScodpa) {
		scodpa = newScodpa;
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected BasicBroker definirMyBroker() {
		return new SprensBroker(this);
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected SprensBroker getMySprensBroker() {
		return (SprensBroker) getMyBasicBroker();
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
	 * Retourne un ArrayList d'objet metier : Sprens.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Sprens> listerSprens(Transaction aTransaction) throws Exception {
		Sprens unSprens = new Sprens();
		return unSprens.getMySprensBroker().listerSprens(aTransaction);
	}

	/**
	 * Retourne un Sprens.
	 * 
	 * @return Sprens
	 */
	public static Sprens chercherSprens(Transaction aTransaction, Integer noMatr) throws Exception {
		Sprens unSprens = new Sprens();
		return unSprens.getMySprensBroker().chercherSprens(aTransaction, noMatr.toString());
	}

	/**
	 * Methode creerObjetMetier qui retourne true ou false
	 */
	public boolean creerSprens(Transaction aTransaction) throws Exception {
		// Creation du Sprens
		return getMySprensBroker().creerSprens(aTransaction);
	}

	/**
	 * Methode modifierObjetMetier qui retourne true ou false
	 */
	public boolean modifierSprens(Transaction aTransaction) throws Exception {
		// Modification du Sprens
		return getMySprensBroker().modifierSprens(aTransaction);
	}
}
