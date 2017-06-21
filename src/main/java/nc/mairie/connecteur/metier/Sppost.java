package nc.mairie.connecteur.metier;

import java.util.ArrayList;

import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;

/**
 * Objet metier Sppost
 */
public class Sppost extends BasicMetier {
	public String poanne;
	public String ponuor;
	public String cdlieu;
	public String poserv;
	public String ctitre;
	public String poetud;
	public String crespo;
	public String pograd;
	public String pomis1;
	public String pomis2;
	public String pomis3;
	public String pomis4;
	public String pomatr;
	public String pocond;
	public String podval;
	public String podsup;
	public String poserp;
	public String codfon;
	public String codact;
	public String noacti;
	public String reglem;
	public String commen;
	public String budget;
	public String soanne;
	public String sonuor;
	public String primaire;

	/**
	 * Constructeur Sppost.
	 */
	public Sppost() {
		super();
	}

	/**
	 * Getter de l'attribut poanne.
	 */
	public String getPoanne() {
		return poanne;
	}

	/**
	 * Setter de l'attribut poanne.
	 */
	public void setPoanne(String newPoanne) {
		poanne = newPoanne;
	}

	/**
	 * Getter de l'attribut ponuor.
	 */
	public String getPonuor() {
		return ponuor;
	}

	/**
	 * Setter de l'attribut ponuor.
	 */
	public void setPonuor(String newPonuor) {
		ponuor = newPonuor;
	}

	/**
	 * Getter de l'attribut cdlieu.
	 */
	public String getCdlieu() {
		return cdlieu;
	}

	/**
	 * Setter de l'attribut cdlieu.
	 */
	public void setCdlieu(String newCdlieu) {
		cdlieu = newCdlieu;
	}

	/**
	 * Getter de l'attribut poserv.
	 */
	public String getPoserv() {
		return poserv;
	}

	/**
	 * Setter de l'attribut poserv.
	 */
	public void setPoserv(String newPoserv) {
		poserv = newPoserv;
	}

	/**
	 * Getter de l'attribut ctitre.
	 */
	public String getCtitre() {
		return ctitre;
	}

	/**
	 * Setter de l'attribut ctitre.
	 */
	public void setCtitre(String newCtitre) {
		ctitre = newCtitre;
	}

	/**
	 * Getter de l'attribut poetud.
	 */
	public String getPoetud() {
		return poetud;
	}

	/**
	 * Setter de l'attribut poetud.
	 */
	public void setPoetud(String newPoetud) {
		poetud = newPoetud;
	}

	/**
	 * Getter de l'attribut crespo.
	 */
	public String getCrespo() {
		return crespo;
	}

	/**
	 * Setter de l'attribut crespo.
	 */
	public void setCrespo(String newCrespo) {
		crespo = newCrespo;
	}

	/**
	 * Getter de l'attribut pograd.
	 */
	public String getPograd() {
		return pograd;
	}

	/**
	 * Setter de l'attribut pograd.
	 */
	public void setPograd(String newPograd) {
		pograd = newPograd;
	}

	/**
	 * Getter de l'attribut pomis1.
	 */
	public String getPomis1() {
		return pomis1;
	}

	/**
	 * Setter de l'attribut pomis1.
	 */
	public void setPomis1(String newPomis1) {
		pomis1 = newPomis1;
	}

	/**
	 * Getter de l'attribut pomis2.
	 */
	public String getPomis2() {
		return pomis2;
	}

	/**
	 * Setter de l'attribut pomis2.
	 */
	public void setPomis2(String newPomis2) {
		pomis2 = newPomis2;
	}

	/**
	 * Getter de l'attribut pomis3.
	 */
	public String getPomis3() {
		return pomis3;
	}

	/**
	 * Setter de l'attribut pomis3.
	 */
	public void setPomis3(String newPomis3) {
		pomis3 = newPomis3;
	}

	/**
	 * Getter de l'attribut pomis4.
	 */
	public String getPomis4() {
		return pomis4;
	}

	/**
	 * Setter de l'attribut pomis4.
	 */
	public void setPomis4(String newPomis4) {
		pomis4 = newPomis4;
	}

	/**
	 * Getter de l'attribut pomatr.
	 */
	public String getPomatr() {
		return pomatr;
	}

	/**
	 * Setter de l'attribut pomatr.
	 */
	public void setPomatr(String newPomatr) {
		pomatr = newPomatr;
	}

	/**
	 * Getter de l'attribut pocond.
	 */
	public String getPocond() {
		return pocond;
	}

	/**
	 * Setter de l'attribut pocond.
	 */
	public void setPocond(String newPocond) {
		pocond = newPocond;
	}

	/**
	 * Getter de l'attribut podval.
	 */
	public String getPodval() {
		return podval;
	}

	/**
	 * Setter de l'attribut podval.
	 */
	public void setPodval(String newPodval) {
		podval = newPodval;
	}

	/**
	 * Getter de l'attribut podsup.
	 */
	public String getPodsup() {
		return podsup;
	}

	/**
	 * Setter de l'attribut podsup.
	 */
	public void setPodsup(String newPodsup) {
		podsup = newPodsup;
	}

	/**
	 * Getter de l'attribut poserp.
	 */
	public String getPoserp() {
		return poserp;
	}

	/**
	 * Setter de l'attribut poserp.
	 */
	public void setPoserp(String newPoserp) {
		poserp = newPoserp;
	}

	/**
	 * Getter de l'attribut codfon.
	 */
	public String getCodfon() {
		return codfon;
	}

	/**
	 * Setter de l'attribut codfon.
	 */
	public void setCodfon(String newCodfon) {
		codfon = newCodfon;
	}

	/**
	 * Getter de l'attribut codact.
	 */
	public String getCodact() {
		return codact;
	}

	/**
	 * Setter de l'attribut codact.
	 */
	public void setCodact(String newCodact) {
		codact = newCodact;
	}

	/**
	 * Getter de l'attribut noacti.
	 */
	public String getNoacti() {
		return noacti;
	}

	/**
	 * Setter de l'attribut noacti.
	 */
	public void setNoacti(String newNoacti) {
		noacti = newNoacti;
	}

	/**
	 * Getter de l'attribut reglem.
	 */
	public String getReglem() {
		return reglem;
	}

	/**
	 * Setter de l'attribut reglem.
	 */
	public void setReglem(String newReglem) {
		reglem = newReglem;
	}

	/**
	 * Getter de l'attribut commen.
	 */
	public String getCommen() {
		return commen;
	}

	/**
	 * Setter de l'attribut commen.
	 */
	public void setCommen(String newCommen) {
		commen = newCommen;
	}

	/**
	 * Getter de l'attribut budget.
	 */
	public String getBudget() {
		return budget;
	}

	/**
	 * Setter de l'attribut budget.
	 */
	public void setBudget(String newBudget) {
		budget = newBudget;
	}

	/**
	 * Getter de l'attribut soanne.
	 */
	public String getSoanne() {
		return soanne;
	}

	/**
	 * Setter de l'attribut soanne.
	 */
	public void setSoanne(String newSoanne) {
		soanne = newSoanne;
	}

	/**
	 * Getter de l'attribut sonuor.
	 */
	public String getSonuor() {
		return sonuor;
	}

	/**
	 * Setter de l'attribut sonuor.
	 */
	public void setSonuor(String newSonuor) {
		sonuor = newSonuor;
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected BasicBroker definirMyBroker() {
		return new SppostBroker(this);
	}

	/**
	 Methode a definir dans chaque objet Metier pour instancier un Broker 
	 */
	protected SppostBroker getMySppostBroker() {
		return (SppostBroker) getMyBasicBroker();
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
	 * Retourne un ArrayList d'objet metier : Sppost.
	 * @return ArrayList
	 */
	public static ArrayList<Sppost> listerSppost(Transaction aTransaction) throws Exception {
		Sppost unSppost = new Sppost();
		return unSppost.getMySppostBroker().listerSppost(aTransaction);
	}

	/**
	 * Retourne un Sppost.
	 * @return Sppost
	 */
	public static Sppost chercherSppost(Transaction aTransaction, String poanne, String ponuor) throws Exception {
		Sppost unSppost = new Sppost();
		return unSppost.getMySppostBroker().chercherSppost(aTransaction, poanne, ponuor);
	}

	/**
	 * Methode creerObjetMetier qui retourne
	 * true ou false
	 */
	public boolean creerSppost(Transaction aTransaction) throws Exception {
		//Creation du Sppost
		return getMySppostBroker().creerSppost(aTransaction);
	}

	/**
	 * Methode modifierObjetMetier qui retourne
	 * true ou false
	 */
	public boolean modifierSppost(Transaction aTransaction) throws Exception {
		//Modification du Sppost
		return getMySppostBroker().modifierSppost(aTransaction);
	}

	public String getPrimaire() {
		return primaire;
	}

	public void setPrimaire(String primaire) {
		this.primaire = primaire;
	}

	public boolean supprimerSppost(Transaction aTransaction) throws Exception {
		//Modification du Sppost
		return getMySppostBroker().supprimerSppost(aTransaction);
	}
}
