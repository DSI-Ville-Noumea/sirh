package nc.mairie.metier.carriere;

import java.lang.reflect.Field;
import java.util.ArrayList;

import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.parametrage.MotifCarriere;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.Transaction;
import nc.mairie.technique.UserAppli;

/**
 * Objet metier Carriere
 */
public class Carriere extends BasicMetier {
	public String noMatricule;
	public String codeCategorie;
	public String codeGrade;
	public String referenceArrete;
	public String oldDateDebut;
	public String dateDebut;
	public String dateFin;
	public String modeReglement;
	public String codeBaseHoraire;
	public String iba;
	public String monantForfait;
	public String codeEmploi;
	public String codeBase;
	public String codeTypeEmploi;
	public String codeBaseHoraire2;
	public String iban;
	public String idMotif;

	public String getIdMotif() {
		return idMotif;
	}

	public void setIdMotif(String idMotif) {
		this.idMotif = idMotif;
	}

	public String ACCJour;
	public String ACCMois;
	public String ACCAnnee;
	public String BMJour;
	public String BMMois;
	public String BMAnnee;
	public String dateArrete;
	public String typeContrat;

	/**
	 * Constructeur Carriere.
	 */
	public Carriere() {
		super();
	}

	/**
	 * Getter de l'attribut noMatricule.
	 */
	public String getNoMatricule() {
		return noMatricule;
	}

	/**
	 * Setter de l'attribut noMatricule.
	 */
	public void setNoMatricule(String newNoMatricule) {
		noMatricule = newNoMatricule;
	}

	/**
	 * Getter de l'attribut codeCategorie.
	 */
	public String getCodeCategorie() {
		return codeCategorie == null ? Const.CHAINE_VIDE : codeCategorie.trim();
	}

	/**
	 * Setter de l'attribut codeCategorie.
	 */
	public void setCodeCategorie(String newCodeCategorie) {
		codeCategorie = newCodeCategorie;
	}

	/**
	 * Getter de l'attribut codeGrade.
	 */
	public String getCodeGrade() {
		return codeGrade == null ? Const.CHAINE_VIDE : codeGrade.trim();
	}

	/**
	 * Setter de l'attribut codeGrade.
	 */
	public void setCodeGrade(String newCodeGrade) {
		codeGrade = newCodeGrade;
	}

	/**
	 * Getter de l'attribut referenceArrete.
	 */
	public String getReferenceArrete() {
		return referenceArrete;
	}

	/**
	 * Setter de l'attribut referenceArrete.
	 */
	public void setReferenceArrete(String newReferenceArrete) {
		referenceArrete = newReferenceArrete;
	}

	/**
	 * Getter de l'attribut dateDebut.
	 */
	public String getDateDebut() {
		return dateDebut;
	}

	/**
	 * Setter de l'attribut dateDebut.
	 */
	public void setDateDebut(String newDateDebut) {
		if (getOldDateDebut() == null)
			setOldDateDebut(dateDebut);
		dateDebut = newDateDebut;
	}

	/**
	 * Getter de l'attribut dateFin.
	 */
	public String getDateFin() {
		return dateFin;
	}

	/**
	 * Setter de l'attribut dateFin.
	 */
	public void setDateFin(String newDateFin) {
		dateFin = newDateFin;
	}

	/**
	 * Getter de l'attribut modeReglement.
	 */
	public String getModeReglement() {
		return modeReglement;
	}

	/**
	 * Setter de l'attribut modeReglement.
	 */
	public void setModeReglement(String newModeReglement) {
		modeReglement = newModeReglement;
	}

	/**
	 * Getter de l'attribut codeBaseHoraire.
	 */
	public String getCodeBaseHoraire() {
		return codeBaseHoraire;
	}

	/**
	 * Setter de l'attribut codeBaseHoraire.
	 */
	public void setCodeBaseHoraire(String newCodeBaseHoraire) {
		codeBaseHoraire = newCodeBaseHoraire;
	}

	/**
	 * Getter de l'attribut iba.
	 */
	public String getIba() {
		return iba;
	}

	/**
	 * Setter de l'attribut iba.
	 */
	public void setIba(String newIba) {
		iba = newIba;
	}

	/**
	 * Getter de l'attribut monantForfait.
	 */
	public String getMonantForfait() {
		return monantForfait;
	}

	/**
	 * Setter de l'attribut monantForfait.
	 */
	public void setMonantForfait(String newMonantForfait) {
		monantForfait = newMonantForfait;
	}

	/**
	 * Getter de l'attribut codeEmploi.
	 */
	public String getCodeEmploi() {
		return codeEmploi;
	}

	/**
	 * Setter de l'attribut codeEmploi.
	 */
	public void setCodeEmploi(String newCodeEmploi) {
		codeEmploi = newCodeEmploi;
	}

	/**
	 * Getter de l'attribut codeBase.
	 */
	public String getCodeBase() {
		return codeBase;
	}

	/**
	 * Setter de l'attribut codeBase.
	 */
	public void setCodeBase(String newCodeBase) {
		codeBase = newCodeBase;
	}

	/**
	 * Getter de l'attribut codeTypeEmploi.
	 */
	public String getCodeTypeEmploi() {
		return codeTypeEmploi;
	}

	/**
	 * Setter de l'attribut codeTypeEmploi.
	 */
	public void setCodeTypeEmploi(String newCodeTypeEmploi) {
		codeTypeEmploi = newCodeTypeEmploi;
	}

	/**
	 * Getter de l'attribut codeBaseHoraire2.
	 */
	public String getCodeBaseHoraire2() {
		return codeBaseHoraire2;
	}

	/**
	 * Setter de l'attribut codeBaseHoraire2.
	 */
	public void setCodeBaseHoraire2(String newCodeBaseHoraire2) {
		codeBaseHoraire2 = newCodeBaseHoraire2;
	}

	/**
	 * Getter de l'attribut iban.
	 */
	public String getIban() {
		return iban;
	}

	/**
	 * Setter de l'attribut iban.
	 */
	public void setIban(String newIban) {
		iban = newIban;
	}

	/**
	 * Getter de l'attribut ACCJour.
	 */
	public String getACCJour() {
		return ACCJour;
	}

	/**
	 * Setter de l'attribut ACCJour.
	 */
	public void setACCJour(String newACCJour) {
		ACCJour = newACCJour;
		if (Const.CHAINE_VIDE.equals(newACCJour))
			ACCJour = Const.ZERO;
	}

	/**
	 * Getter de l'attribut ACCMois.
	 */
	public String getACCMois() {
		return ACCMois;
	}

	/**
	 * Setter de l'attribut ACCMois.
	 */
	public void setACCMois(String newACCMois) {
		ACCMois = newACCMois;
		if (Const.CHAINE_VIDE.equals(newACCMois))
			ACCMois = Const.ZERO;
	}

	/**
	 * Getter de l'attribut ACCAnnee.
	 */
	public String getACCAnnee() {
		return ACCAnnee;
	}

	/**
	 * Setter de l'attribut ACCAnnee.
	 */
	public void setACCAnnee(String newACCAnnee) {
		ACCAnnee = newACCAnnee;
		if (Const.CHAINE_VIDE.equals(newACCAnnee))
			ACCAnnee = Const.ZERO;
	}

	/**
	 * Getter de l'attribut BMJour.
	 */
	public String getBMJour() {
		return BMJour;
	}

	/**
	 * Setter de l'attribut BMJour.
	 */
	public void setBMJour(String newBMJour) {
		BMJour = newBMJour;
		if (Const.CHAINE_VIDE.equals(newBMJour))
			BMJour = Const.ZERO;
	}

	/**
	 * Getter de l'attribut BMMois.
	 */
	public String getBMMois() {
		return BMMois;
	}

	/**
	 * Setter de l'attribut BMMois.
	 */
	public void setBMMois(String newBMMois) {
		BMMois = newBMMois;
		if (Const.CHAINE_VIDE.equals(newBMMois))
			BMMois = Const.ZERO;
	}

	/**
	 * Getter de l'attribut BMAnnee.
	 */
	public String getBMAnnee() {
		return BMAnnee;
	}

	/**
	 * Setter de l'attribut BMAnnee.
	 */
	public void setBMAnnee(String newBMAnnee) {
		BMAnnee = newBMAnnee;
		if (Const.CHAINE_VIDE.equals(newBMAnnee))
			BMAnnee = Const.ZERO;
	}

	/**
	 * Getter de l'attribut dateArrete.
	 */
	public String getDateArrete() {
		return dateArrete;
	}

	/**
	 * Setter de l'attribut dateArrete.
	 */
	public void setDateArrete(String newDateArrete) {
		dateArrete = newDateArrete;
	}

	/**
	 * Getter de l'attribut typeContrat.
	 */
	public String getTypeContrat() {
		return typeContrat == null ? Const.CHAINE_VIDE : typeContrat.trim();
	}

	/**
	 * Setter de l'attribut typeContrat.
	 */
	public void setTypeContrat(String newTypeContrat) {
		typeContrat = newTypeContrat;
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected BasicBroker definirMyBroker() {
		return new CarriereBroker(this);
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected CarriereBroker getMyCarriereBroker() {
		return (CarriereBroker) getMyBasicBroker();
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
	 * Retourne un ArrayList d'objet metier : Carriere.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Carriere> listerCarriereAvecAgent(Transaction aTransaction, Agent agent) throws Exception {
		Carriere uneCarriere = new Carriere();
		return uneCarriere.getMyCarriereBroker().listerCarriereAvecAgent(aTransaction, agent.getNomatr());
	}

	/**
	 * Retourne la Carriere en cours.
	 * 
	 * @param agent
	 * @return Carriere
	 */
	public static Carriere chercherCarriereEnCoursAvecAgent(Transaction aTransaction, Agent agent) throws Exception {
		Carriere unCarriere = new Carriere();
		return unCarriere.getMyCarriereBroker().chercherCarriereEnCoursAvecAgent(aTransaction, agent.getNomatr());
	}

	/**
	 * Retourne la Carriere en cours.
	 * 
	 * @param agent
	 * @return Carriere
	 */
	public static Carriere chercherCarriereEnCoursAvecAgentEtDate(Transaction aTransaction, Integer date, Agent agent) throws Exception {
		Carriere unCarriere = new Carriere();
		return unCarriere.getMyCarriereBroker().chercherCarriereEnCoursAvecAgentEtDate(aTransaction, date, agent.getNomatr());
	}

	/**
	 * Retourne la derniere Carriere.
	 * 
	 * @param agent
	 * @return Carriere
	 */
	public static Carriere chercherDerniereCarriereAvecAgent(Transaction aTransaction, Agent agent) throws Exception {
		Carriere unCarriere = new Carriere();
		return unCarriere.getMyCarriereBroker().chercherDerniereCarriereAvecAgent(aTransaction, agent.getNomatr());
	}

	public static Carriere chercherDerniereCarriereAvecAgentEtAnnee(Transaction aTransaction, Integer nomatr, String annee) throws Exception {
		Carriere unCarriere = new Carriere();
		return unCarriere.getMyCarriereBroker().chercherDerniereCarriereAvecAgentEtAnnee(aTransaction, nomatr, annee);
	}

	/**
	 * Methode creerObjetMetier qui retourne true ou false RG_AG_CA_A03
	 */
	public boolean creerCarriere(Transaction aTransaction, Agent agent, UserAppli user) throws Exception {

		setMonantForfait(Const.ZERO);
		setCodeEmploi(Const.ZERO);

		if (Const.CHAINE_VIDE.equals(getACCJour()) || getACCJour() == null)
			setACCJour(Const.ZERO);
		if (Const.CHAINE_VIDE.equals(getACCMois()) || getACCMois() == null)
			setACCMois(Const.ZERO);
		if (Const.CHAINE_VIDE.equals(getACCAnnee()) || getACCAnnee() == null)
			setACCAnnee(Const.ZERO);
		if (Const.CHAINE_VIDE.equals(getBMJour()) || getBMJour() == null)
			setBMJour(Const.ZERO);
		if (Const.CHAINE_VIDE.equals(getBMMois()) || getBMMois() == null)
			setBMMois(Const.ZERO);
		if (Const.CHAINE_VIDE.equals(getBMAnnee()) || getBMAnnee() == null)
			setBMAnnee(Const.ZERO);
		setCodeBaseHoraire(Const.ZERO);
		setIba(Const.ZERO);

		setNoMatricule(agent.getNomatr().toString());

		return getMyCarriereBroker().creerCarriere(aTransaction);
	}

	/**
	 * Methode modifierObjetMetier qui retourne true ou false RG_AG_CA_A03
	 */
	public boolean modifierCarriere(Transaction aTransaction, Agent agent, UserAppli user) throws Exception {
		if (getOldDateDebut() != null && !getOldDateDebut().equals(getDateDebut())) {

			if (!getMyCarriereBroker().modifierCarriere(aTransaction))
				return false;

			setOldDateDebut(null);
			return true;
		}

		setOldDateDebut(null);
		// Modification de la Carriere
		return getMyCarriereBroker().modifierCarriere(aTransaction);
	}

	/**
	 * Methode supprimerObjetMetier qui retourne true ou false RG_AG_CA_A03
	 */
	public boolean supprimerCarriere(Transaction aTransaction, UserAppli user) throws Exception {
		// Suppression de l'Carriere
		return getMyCarriereBroker().supprimerCarriere(aTransaction);
	}

	public boolean isActive() {
		return getDateFin() == null || getDateFin().equals(Const.CHAINE_VIDE);
	}

	/**
	 * 
	 */
	public boolean equals(Carriere obj) throws Exception {

		Field[] fieldsThis = getClass().getFields();
		// parcours des attributs
		for (int i = 0; i < fieldsThis.length; i++) {
			String name = fieldsThis[i].getName();
			Object attributThis = fieldsThis[i].get(this);
			Object attributObj = obj.getClass().getField(name).get(obj);

			// si attribut this null et pas l'autre alors faux
			if (attributThis == null && attributObj != null)
				return false;

			// si attribut this null et l'autre aussi alors continue
			if (attributThis == null && attributObj == null)
				continue;

			// Si differents alurs faux
			if (!attributThis.equals(attributObj))
				return false;
		}

		// Tout OK alors true
		return true;
	}

	public String getOldDateDebut() {
		return oldDateDebut;
	}

	public void setOldDateDebut(String oldDateDebut) {
		this.oldDateDebut = oldDateDebut;
	}

	/**
	 * Retourne un ArrayList d'objet metier : Carriere.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Carriere> listerCarriereParCategorieSPCARR(Transaction aTransaction, String idCate) throws Exception {
		Carriere uneCarriere = new Carriere();
		return uneCarriere.getMyCarriereBroker().listerCarriereParCategorieSPCARR(aTransaction, idCate);
	}

	/**
	 * Retourne la Carriere precedant celle passee en parametre.
	 * 
	 * @param aTransaction
	 * @param nomatr
	 *            Numero de matricule
	 * @param datedeb
	 *            Date de debut
	 * @return Carriere
	 * @throws Exception
	 */
	public static Carriere chercherCarriereAgentPrec(Transaction aTransaction, Integer nomatr, String datedeb) throws Exception {
		Carriere uneCarriere = new Carriere();
		return uneCarriere.getMyCarriereBroker().chercherCarriereAgentPrec(aTransaction, nomatr.toString(), datedeb);
	}

	/**
	 * Retourne la Carriere suivant celle passee en parametre.
	 * 
	 * @param aTransaction
	 * @param nomatr
	 *            Numero de matricule
	 * @param datedeb
	 *            Date de debut
	 * @return Carriere
	 * @throws Exception
	 */
	public static Carriere chercherCarriereAgentSuiv(Transaction aTransaction, Integer nomatr, String datedeb) throws Exception {
		Carriere uneCarriere = new Carriere();
		return uneCarriere.getMyCarriereBroker().chercherCarriereAgentSuiv(aTransaction, nomatr.toString(), datedeb);
	}

	/**
	 * @param aTransaction
	 * @param noMatr
	 * @param codeGrade
	 * @return ArrayList
	 * @throws Exception
	 */
	public static ArrayList<Carriere> listerCarriereAvecGradeEtStatut(Transaction aTransaction, Integer noMatr, String codeGrade, String codeCategorie) throws Exception {
		Carriere uneCarriere = new Carriere();
		return uneCarriere.getMyCarriereBroker().listerCarriereAvecGradeEtStatut(aTransaction, noMatr.toString(), codeGrade, codeCategorie);
	}

	public static String getStatutCarriere(String codeCategorie) {
		if (codeCategorie.equals("1") || codeCategorie.equals("2") || codeCategorie.equals("3") || codeCategorie.equals("6") || codeCategorie.equals("16") || codeCategorie.equals("17")
				|| codeCategorie.equals("18") || codeCategorie.equals("19") || codeCategorie.equals("20")) {
			return "F";
		} else if (codeCategorie.equals("7") || codeCategorie.equals("8")) {
			return "CC";
		} else if (codeCategorie.equals("4")) {
			return "C";
		} else {
			return Const.CHAINE_VIDE;
		}
	}

	public static String getStatutCarriereEAE(String codeCategorie) {
		if (codeCategorie.equals("1") || codeCategorie.equals("2") || codeCategorie.equals("6") || codeCategorie.equals("16") || codeCategorie.equals("17") || codeCategorie.equals("18")
				|| codeCategorie.equals("19") || codeCategorie.equals("20")) {
			return "F";
		} else if (codeCategorie.equals("7")) {
			return "CC";
		} else if (codeCategorie.equals("4")) {
			return "C";
		} else if (codeCategorie.equals("3")) {
			return "AL";
		} else if (codeCategorie.equals("5") || codeCategorie.equals("8") || codeCategorie.equals("9") || codeCategorie.equals("10") || codeCategorie.equals("15") || codeCategorie.equals("11")) {
			return "A";
		} else {
			return Const.CHAINE_VIDE;
		}

	}

	public static Carriere chercherCarriereFonctionnaireAncienne(Transaction aTransaction, Integer noMatricule) throws Exception {
		Carriere unCarriere = new Carriere();
		return unCarriere.getMyCarriereBroker().chercherCarriereFonctionnaireAncienne(aTransaction, noMatricule.toString());
	}

	public static ArrayList<Carriere> listerCarriereActive(Transaction aTransaction, String annee, String statut) throws Exception {
		Carriere uneCarriere = new Carriere();
		return uneCarriere.getMyCarriereBroker().listerCarriereActive(aTransaction, annee, statut);
	}

	public static Carriere chercherCarriereSuperieurOuEgaleDate(Transaction aTransaction, Agent agentCarr, String dateAvctFinale) throws Exception {
		Carriere unCarriere = new Carriere();
		return unCarriere.getMyCarriereBroker().chercherCarriereSuperieurOuEgaleDate(aTransaction, agentCarr.getNomatr(), dateAvctFinale);
	}

	public static boolean isCarriereConseilMunicipal(String codeCategorie) {
		if (codeCategorie.equals("9") || codeCategorie.equals("10") || codeCategorie.equals("11")) {
			return true;
		}
		return false;
	}

	public static ArrayList<Carriere> listerCarriereAgentByType(Transaction aTransaction, Integer noMatricule, String type) throws Exception {
		Carriere unCarriere = new Carriere();
		return unCarriere.getMyCarriereBroker().listerCarriereAgentByType(aTransaction, noMatricule.toString(), type);
	}

	/**
	 * Retourne un ArrayList d'objet metier : Avancement.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Carriere> listerCarriereAvecMotif(Transaction aTransaction, MotifCarriere motif) throws Exception {
		Carriere unCarriere = new Carriere();
		return unCarriere.getMyCarriereBroker().listerCarriereAvecMotif(aTransaction, motif.getIdMotifCarriere().toString());
	}

}
