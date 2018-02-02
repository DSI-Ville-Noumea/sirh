package nc.mairie.metier.agent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicBroker;
import nc.mairie.technique.BasicMetier;
import nc.mairie.technique.MairieMessages;
import nc.mairie.technique.Services;
import nc.mairie.technique.Transaction;
import nc.mairie.technique.UserAppli;

/**
 * Objet metier PositionAdmAgent
 */
public class PositionAdmAgent extends BasicMetier {
	public String nomatr;
	public String datdeb;
	public String oldDateDeb;
	public String cdpadm;
	public String datfin;
	public String refarr;
	public String dateArrete;

	/**
	 * Constructeur PositionAdmAgent.
	 */
	public PositionAdmAgent() {
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
	 * Getter de l'attribut datdeb.
	 */
	public String getDatdeb() {
		return datdeb;
	}

	/**
	 * Setter de l'attribut datdeb.
	 */
	public void setDatdeb(String newDatdeb) {
		if (oldDateDeb == null)
			oldDateDeb = datdeb;
		datdeb = newDatdeb;
	}

	/**
	 * Getter de l'attribut cdpadm.
	 */
	public String getCdpadm() {
		return cdpadm;
	}

	/**
	 * Setter de l'attribut cdpadm.
	 */
	public void setCdpadm(String newCdpadm) {
		cdpadm = newCdpadm;
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
	 * Getter de l'attribut refarr.
	 */
	public String getRefarr() {
		return refarr == null ? Const.CHAINE_VIDE : refarr.trim();
	}

	/**
	 * Setter de l'attribut refarr.
	 */
	public void setRefarr(String newRefarr) {
		if (newRefarr == null || Const.CHAINE_VIDE.equals(newRefarr))
			refarr = Const.ZERO;
		else
			refarr = newRefarr;
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected BasicBroker definirMyBroker() {
		return new PositionAdmAgentBroker(this);
	}

	/**
	 * Methode a definir dans chaque objet Metier pour instancier un Broker
	 */
	protected PositionAdmAgentBroker getMyPositionAdmAgentBroker() {
		return (PositionAdmAgentBroker) getMyBasicBroker();
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
	 * Retourne un ArrayList d'objet metier : PositionAdmAgent.
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<PositionAdmAgent> listerPositionAdmAgentAvecAgent(Transaction aTransaction, Agent agent)
			throws Exception {

		// Test du parametre Agent
		if (agent == null || agent.getIdAgent() == null) {
			aTransaction.declarerErreur(MairieMessages.getMessage("ERR003", "Agent"));
			return new ArrayList<PositionAdmAgent>();
		}
		PositionAdmAgent unPositionAdmAgent = new PositionAdmAgent();
		return unPositionAdmAgent.getMyPositionAdmAgentBroker().listerPositionAdmAgentAvecAgent(aTransaction,
				agent.getNomatr());
	}

	/**
	 * Retourne un PositionAdmAgent.
	 * 
	 * @return PositionAdmAgent
	 */
	public static PositionAdmAgent chercherPositionAdmAgent(Transaction aTransaction, String nomatr, String datedeb)
			throws Exception {
		PositionAdmAgent unPositionAdmAgent = new PositionAdmAgent();
		return unPositionAdmAgent.getMyPositionAdmAgentBroker().chercherPositionAdmAgent(aTransaction, nomatr,
				Services.convertitDate(Services.formateDate(datedeb), "dd/MM/yy", "yyyyMMdd"));
	}

	/**
	 * Retourne la PositionAdmAgent de l'agent la plus recente.
	 * 
	 * @return PositionAdmAgent
	 */
	public static PositionAdmAgent chercherDernierePositionAdmAgentAvecAgent(Transaction aTransaction, Agent aAgent)
			throws Exception {
		PositionAdmAgent unPositionAdmAgent = new PositionAdmAgent();
		return unPositionAdmAgent.getMyPositionAdmAgentBroker().chercherDernierePositionAdmAgentAvecAgent(aTransaction,
				aAgent);
	}

	/**
	 * Retourne la PositionAdmAgent precedant celle passee en parametre.
	 * 
	 * @param aTransaction
	 * @param nomatr
	 *            Numero de matricule
	 * @param datedeb
	 *            Date de debut
	 * @return PositionAdmAgent
	 * @throws Exception
	 */
	public static PositionAdmAgent chercherPositionAdmAgentPrec(Transaction aTransaction, Integer nomatr, String datedeb)
			throws Exception {
		PositionAdmAgent unPositionAdmAgent = new PositionAdmAgent();
		return unPositionAdmAgent.getMyPositionAdmAgentBroker().chercherPositionAdmAgentPrec(aTransaction,
				nomatr.toString(), datedeb);
	}

	/**
	 * Retourne la PositionAdmAgent suivant celle passee en parametre.
	 * 
	 * @param aTransaction
	 * @param nomatr
	 *            Numero de matricule
	 * @param codepa
	 *            Code de PA
	 * @param datedeb
	 *            Date de debut
	 * @return PositionAdmAgent
	 * @throws Exception
	 */
	public static PositionAdmAgent chercherPositionAdmAgentSuiv(Transaction aTransaction, String nomatr, String codepa,
			String datedeb) throws Exception {
		PositionAdmAgent unPositionAdmAgent = new PositionAdmAgent();
		return unPositionAdmAgent.getMyPositionAdmAgentBroker().chercherPositionAdmAgentSuiv(aTransaction, nomatr,
				codepa, datedeb);
	}
	

	public static PositionAdmAgent chercherPositionAdmAgentSuiv(Transaction aTransaction, String nomatr, String datedeb) throws Exception {
		PositionAdmAgent unPositionAdmAgent = new PositionAdmAgent();
		return unPositionAdmAgent.getMyPositionAdmAgentBroker().chercherPositionAdmAgentSuiv(aTransaction, nomatr, datedeb);
	}

	/**
	 * Methode creerObjetMetier qui retourne true ou false RG_AG_PA_A01
	 */
	public boolean creerPositionAdmAgent(Transaction aTransaction, UserAppli user) throws Exception {
		// Creation du PositionAdmAgent
		return getMyPositionAdmAgentBroker().creerPositionAdmAgent(aTransaction);
	}

	/**
	 * Methode modifierObjetMetier qui retourne true ou false RG_AG_PA_A01
	 */
	public boolean modifierPositionAdmAgent(Transaction aTransaction, Agent agent, UserAppli user) throws Exception {
		// Modification du PositionAdmAgent
		if (oldDateDeb == null || datdeb.equals(oldDateDeb))
			return getMyPositionAdmAgentBroker().modifierPositionAdmAgent(aTransaction);

		// gestion du cas ou on modifie la date de debut,
		oldDateDeb = null;

		return getMyPositionAdmAgentBroker().modifierPositionAdmAgent(aTransaction);
	}

	/**
	 * Methode supprimerObjetMetier qui retourne true ou false RG_AG_PA_A01
	 */
	public boolean supprimerPositionAdmAgent(Transaction aTransaction, UserAppli user) throws Exception {
		// Suppression de l'PositionAdmAgent
		return getMyPositionAdmAgentBroker().supprimerPositionAdmAgent(aTransaction);
	}

	public boolean isActive(Transaction aTransaction) throws Exception {
		return (getDatfin() == null || getDatfin().equals(Const.CHAINE_VIDE) || Services.compareDates(getDatfin(),
				Services.dateDuJour()) <= 0) && !estPAInactive(aTransaction);
	}

	public String getDateArrete() {
		return dateArrete;
	}

	public void setDateArrete(String dateArrete) {
		this.dateArrete = dateArrete;
	}

	public boolean estPAInactive(Transaction aTransaction) throws Exception {

		PositionAdm posa = PositionAdm.chercherPositionAdm(aTransaction, this.getCdpadm());

		if (posa != null && posa.getPosit() != null && posa.getPosit().equals("FS"))
			return true;

		return false;
	}

	/**
	 * Determine si la position administrative donne le droit a un AT
	 */
	public boolean permetAT() {

		if (getCdpadm().equals("01") || getCdpadm().equals("02") || getCdpadm().equals("03")
				|| getCdpadm().equals("04") || getCdpadm().equals("23") || getCdpadm().equals("24")
				|| getCdpadm().equals("40") || getCdpadm().equals("41") || getCdpadm().equals("56")
				|| getCdpadm().equals("60") || getCdpadm().equals("61") || getCdpadm().equals("62")
				|| getCdpadm().equals("63") || getCdpadm().equals("64") || getCdpadm().equals("65")
				|| getCdpadm().equals("71") || getCdpadm().equals("AC"))
			return true;
		return false;
	}

	/**
	 * Determine si la position administrative donne le droit a une Visite
	 * medicale
	 */
	public boolean permetVM() {

		if (getCdpadm().equals("01") || getCdpadm().equals("02") || getCdpadm().equals("03")
				|| getCdpadm().equals("04") || getCdpadm().equals("40") || getCdpadm().equals("41")
				|| getCdpadm().equals("56") || getCdpadm().equals("60") || getCdpadm().equals("61")
				|| getCdpadm().equals("62") || getCdpadm().equals("63") || getCdpadm().equals("64")
				|| getCdpadm().equals("65") || getCdpadm().equals("66") || getCdpadm().equals("71")
				|| getCdpadm().equals("AC"))
			return true;
		return false;
	}

	/**
	 * Verifie si deux objets sont egaux. Retourne un booleen qui indique si cet
	 * objet equivaut a celui indique. Cette methode est utilisee lorsqu'un
	 * objet est stocke dans une table de hachage.
	 * 
	 * @param obj
	 *            l'objet a comparer avec
	 * @return true si ces objets sont egaux ; false dans le cas contraire.
	 * @see Hashtable
	 */
	public boolean equals(BasicMetier obj) throws Exception {

		// Si pas la meme classe alors faux
		if (!obj.getClass().equals(getClass()))
			return false;

		Field[] fieldsThis = getClass().getFields();
		// parcours des attributs
		for (int i = 0; i < fieldsThis.length; i++) {
			String name = fieldsThis[i].getName();
			Object attributThis = fieldsThis[i].get(this);
			Object attributObj = obj.getClass().getField(name).get(obj);

			// si attribut this null et pas l'autre alors faux
			if (attributThis == null && attributObj != null)
				return false;

			if (attributThis == null && attributObj == null)
				return true;

			// Si differents alurs faux
			if (!attributThis.equals(attributObj))
				return false;
		}

		// Tout OK alors true
		return true;
	}

	public static PositionAdmAgent chercherPositionAdmAgentDateComprise(Transaction aTransaction, Integer noMatricule,
			String date) throws Exception {
		PositionAdmAgent unPositionAdmAgent = new PositionAdmAgent();
		return unPositionAdmAgent.getMyPositionAdmAgentBroker().chercherPositionAdmAgentDateComprise(aTransaction,
				noMatricule.toString(), date);
	}

	public static PositionAdmAgent chercherPositionAdmAgentDateFinExclu(Transaction aTransaction, Integer noMatricule,
			String date) throws Exception {
		PositionAdmAgent unPositionAdmAgent = new PositionAdmAgent();
		return unPositionAdmAgent.getMyPositionAdmAgentBroker().chercherPositionAdmAgentDateFinExclu(aTransaction,
				noMatricule.toString(), date);
	}

	public static PositionAdmAgent chercherPositionAdmAgentDateDebutFinComprise(Transaction transaction,
			String noMatricule, String datdeb, String datfin) throws Exception {
		PositionAdmAgent unPositionAdmAgent = new PositionAdmAgent();
		return unPositionAdmAgent.getMyPositionAdmAgentBroker().chercherPositionAdmAgentDateDebutFinComprise(
				transaction, noMatricule, datdeb, datfin);
	}

	public static ArrayList<PositionAdmAgent> listerPositionAdmAgentHorsEffectif(Transaction aTransaction,
			Integer moisChoisi, Integer anneeChoisi) throws Exception {
		PositionAdmAgent unPositionAdmAgent = new PositionAdmAgent();
		return unPositionAdmAgent.getMyPositionAdmAgentBroker().listerPositionAdmAgentHorsEffectif(aTransaction,
				moisChoisi, anneeChoisi);
	}

	/**
	 * Retourne la PositionAdmAgent en cours.
	 * 
	 * @param nomatr
	 * @return PositionAdmAgent
	 */
	public static PositionAdmAgent chercherPositionAdmAgentEnCoursAvecAgent(Transaction aTransaction, Integer nomatr)
			throws Exception {
		PositionAdmAgent unPositionAdmAgent = new PositionAdmAgent();
		return unPositionAdmAgent.getMyPositionAdmAgentBroker().chercherPositionAdmAgentEnCoursAvecAgent(aTransaction,
				nomatr.toString());
	}

	public String getPositionAdmEAE(String codePA) {
		if (Services.estNumerique(codePA) && !codePA.equals("54") && !codePA.equals("56") && !codePA.equals("57")) {
			// activite
			return "AC";
		} else if (codePA.equals("56") || codePA.equals("57")) {
			// Mise a disposition
			return "MD";
		} else if (codePA.equals("54")) {
			// Detachement
			return "D";
		} else {
			// Autre
			return "A";
		}
	}

	public static PositionAdmAgent chercherPositionAdmAgentAncienne(Transaction aTransaction, Integer noMatricule)
			throws Exception {
		PositionAdmAgent unPositionAdmAgent = new PositionAdmAgent();
		return unPositionAdmAgent.getMyPositionAdmAgentBroker().chercherPositionAdmAgentAncienne(aTransaction,
				noMatricule.toString());
	}

	public static PositionAdmAgent chercherPositionAdmAgentActive(Transaction aTransaction, Integer noMatricule)
			throws Exception {
		PositionAdmAgent unPositionAdmAgent = new PositionAdmAgent();
		return unPositionAdmAgent.getMyPositionAdmAgentBroker().chercherPositionAdmAgentActive(aTransaction,
				noMatricule.toString());
	}

	public static ArrayList<PositionAdmAgent> listerPositionAdmAgentEnActivite(Transaction aTransaction)
			throws Exception {
		PositionAdmAgent unPositionAdmAgent = new PositionAdmAgent();
		return unPositionAdmAgent.getMyPositionAdmAgentBroker().listerPositionAdmAgentEnActivite(aTransaction);
	}
}
