package nc.mairie.gestionagent.process.agent;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.Charge;
import nc.mairie.metier.agent.ChargeAgent;
import nc.mairie.metier.agent.CodeAcci;
import nc.mairie.metier.agent.CodeChargeLogt;
import nc.mairie.metier.agent.CodeLogt;
import nc.mairie.metier.agent.CodeMutu;
import nc.mairie.metier.agent.Creancier;
import nc.mairie.metier.paye.Matricule;
import nc.mairie.metier.specificites.Rubrique;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.CharacterDataArea;
import com.ibm.as400.access.QSYSObjectPathName;

/**
 * Process OeAGENTCharge Date de cr�ation : (10/08/11 09:33:52)
 * 
 */
public class OeAGENTCharge extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATUT_RECHERCHER_AGENT = 1;

	private String[] LB_CODE_CHARGE;
	private String[] LB_CREANCIER;

	private ArrayList<Charge> listeCharges;
	private ArrayList<Creancier> listeCreancier;
	private ArrayList<Rubrique> listeRubriques;
	private ArrayList<CodeAcci> listeCodesAcci;
	private ArrayList<CodeMutu> listeCodesMutu;
	private ArrayList<CodeLogt> listeCodesLogt;
	private ArrayList<CodeChargeLogt> listeCodesChargeLogt;

	private Hashtable<String, Creancier> hashCreanciers;
	private Hashtable<String, Rubrique> hashRubriques;
	private Hashtable<String, CodeAcci> hashCodesAcci;
	private Hashtable<String, CodeMutu> hashCodesMutu;
	private Hashtable<String, CodeLogt> hashCodesLogt;
	private Hashtable<String, CodeChargeLogt> hashCodesChargeLogt;

	private AgentNW agentCourant;
	private Charge chargeCourante;

	public String ACTION_SUPPRESSION = "Suppression d'une fiche Charge.";
	private String ACTION_MODIFICATION = "Modification d'une fiche Charge.";
	public String ACTION_CREATION = "Cr�ation d'une fiche Charge.";
	public String ACTION_CONSULTATION = "Consultation d'une fiche Charge.";

	public boolean showCodeCharge = false;
	public boolean showCreancier = false;
	public boolean showMatriculeCharge = false;
	public boolean showMontant = false;
	public boolean matriculeChargeEditable = false;
	public boolean matriculeChargeObligatoire = false;
	public boolean showDonneesMutu = false;
	public boolean montantObligatoire = false;

	private static QSYSObjectPathName CALC_PATH = new QSYSObjectPathName("PERSONNEL", "CALCUL", "DTAARA");
	public static CharacterDataArea DTAARA_CALC = new CharacterDataArea(new AS400((String) ServletAgent.getMesParametres().get("HOST_SGBD_PAYE"),
			(String) ServletAgent.getMesParametres().get("HOST_SGBD_ADMIN"), (String) ServletAgent.getMesParametres().get("HOST_SGBD_PWD")),
			CALC_PATH.getPath());
	private String calculPaye;

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 * RG_AG_CG_C07 RG_AG_CG_C01 RG_AG_CG_C02 RG_AG_CG_C03
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		}

		// V�rification des droits d'acc�s.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Op�ration impossible. Vous ne disposez pas des droits d'acc�s � cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		// SI CALCUL PAYE EN COURS
		String percou = DTAARA_CALC.read().toString();
		if (!percou.trim().equals(Const.CHAINE_VIDE)) {
			setCalculPaye(percou);
		} else {
			setCalculPaye(Const.CHAINE_VIDE);
		}

		// Si hashtable des rubriques vide
		// RG_AG_CG_C07
		// RG_AG_CG_C01
		// RG_AG_CG_C02
		if (getHashRubriques().size() == 0) {
			ArrayList<Rubrique> listeRubrique = Rubrique.listerRubriqueAvecTypeRubr(getTransaction(), "C");
			setListeRubriques(listeRubrique);

			// remplissage de la hashTable
			for (int i = 0; i < listeRubrique.size(); i++) {
				Rubrique r = (Rubrique) listeRubrique.get(i);
				getHashRubriques().put(r.getNumRubrique(), r);
			}
		}

		// Si hashtable des creanciers vide
		// RG_AG_CG_C03
		if (getHashCreanciers().size() == 0) {
			ArrayList<Creancier> listeCreancier = Creancier.listerCreancier(getTransaction());
			setListeCreancier(listeCreancier);

			int tailles[] = { 50 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);

			// remplissage de la hashTable
			for (int i = 0; i < listeCreancier.size(); i++) {
				Creancier c = (Creancier) listeCreancier.get(i);
				getHashCreanciers().put(c.getCdCrea(), c);
				String ligne[] = { c.getDesign() + " - " + c.getNoCpte() };
				aFormat.ajouteLigne(ligne);
			}
			setLB_CREANCIER(aFormat.getListeFormatee(true));
		}

		// Si hashtable des codes accident du travail vide
		if (getHashCodesAcci().size() == 0) {
			ArrayList<CodeAcci> listeCodesAcci = CodeAcci.listerCodeAcci(getTransaction());
			setListeCodesAcci(listeCodesAcci);

			// remplissage de la hashTable
			for (int i = 0; i < listeCodesAcci.size(); i++) {
				CodeAcci c = (CodeAcci) listeCodesAcci.get(i);
				getHashCodesAcci().put(c.getCdacci(), c);
			}
		}

		// Si hashtable des codes mutuelle vide
		if (getHashCodesMutu().size() == 0) {
			ArrayList<CodeMutu> listeCodesMutu = CodeMutu.listerCodeMutu(getTransaction());
			setListeCodesMutu(listeCodesMutu);

			// remplissage de la hashTable
			for (int i = 0; i < listeCodesMutu.size(); i++) {
				CodeMutu c = (CodeMutu) listeCodesMutu.get(i);
				getHashCodesMutu().put(c.getCdmutu(), c);
			}
		}

		// Si hashtable des codes logement vide
		if (getHashCodesLogt().size() == 0) {
			ArrayList<CodeLogt> listeCodes = CodeLogt.listerCodeLogt(getTransaction());
			setListeCodesLogt(listeCodes);

			// remplissage de la hashTable
			for (int i = 0; i < listeCodes.size(); i++) {
				CodeLogt c = (CodeLogt) listeCodes.get(i);
				getHashCodesLogt().put(c.getCdlogt(), c);
			}
		}

		// Si hashtable des codes logement vide
		if (getHashCodesChargeLogt().size() == 0) {
			ArrayList<CodeChargeLogt> listeCodes = CodeChargeLogt.listerCodeChargeLogt(getTransaction());
			setListeCodesChargeLogt(listeCodes);

			// remplissage de la hashTable
			for (int i = 0; i < listeCodes.size(); i++) {
				CodeChargeLogt c = (CodeChargeLogt) listeCodes.get(i);
				getHashCodesChargeLogt().put(c.getCdlogt(), c);
			}
		}

		// Si agentCourant vide ou si etat recherche
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListeCharges(request);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}
	}

	/**
	 * fonction qui permet de savoir si une paye est en cours.
	 * 
	 * @return calculPaye
	 */
	public String getCalculPaye() {
		return calculPaye;
	}

	private void setCalculPaye(String calculPaye) {
		this.calculPaye = calculPaye;
	}

	/**
	 * R�cup�ration du lib�l� de charge en fonction du type de rubrique et du
	 * code de charge
	 * 
	 * @param c
	 * @param code
	 * @return
	 */
	private String getLibCharge(Charge c, String code) throws Exception {
		Rubrique r = Rubrique.chercherRubrique(getTransaction(), c.getNoRubr());

		if (r.numRubrique.equals("2900") || r.numRubrique.equals("2850"))
			// SPACCI accident du tavail
			return CodeAcci.chercherCodeAcci(getTransaction(), code).getLibacc();

		if (r.numRubrique.equals("3000"))
			// SPMUTU code mutuelle
			return CodeMutu.chercherCodeMutu(getTransaction(), code).getLimutu();

		if (r.numRubrique.equals("4000"))
			// SPCLOG code logement
			return CodeLogt.chercherCodeLogt(getTransaction(), code).getLiblog();

		if (r.numRubrique.equals("4001"))
			// SPCCHG code charges logement
			return CodeChargeLogt.chercherCodeChargeLogt(getTransaction(), code).getLiblog();

		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();
			return Const.CHAINE_VIDE;
		}

		return Const.CHAINE_VIDE;
	}

	/**
	 * Initialisation de la liste des primes de l'agent courant Date de cr�ation
	 * : (04/08/11)
	 * 
	 */
	private void initialiseListeCharges(HttpServletRequest request) throws Exception {
		// Recherche des accidents du travail de l'agent
		ArrayList<Charge> listeCharges = Charge.listerChargeAvecAgent(getTransaction(), getAgentCourant());
		setListeCharges(listeCharges);

		int indiceCharge = 0;
		if (getListeCharges() != null) {
			for (int i = 0; i < getListeCharges().size(); i++) {
				Charge c = (Charge) getListeCharges().get(i);
				Rubrique r = (Rubrique) getHashRubriques().get(c.getNoRubr());
				String codeChge = c.getCdChar();

				addZone(getNOM_ST_CODE_RUBR(indiceCharge), r.getNumRubrique().equals(Const.CHAINE_VIDE) ? "&nbsp;" : r.getNumRubrique());
				addZone(getNOM_ST_RUBRIQUE(indiceCharge), r.getLibRubrique().equals(Const.CHAINE_VIDE) ? "&nbsp;" : r.getLibRubrique());
				addZone(getNOM_ST_MAT_CHARGE(indiceCharge), c.getNoMate().equals(Const.CHAINE_VIDE) ? "&nbsp;" : c.getNoMate());
				addZone(getNOM_ST_LIB_CHARGE(indiceCharge), getLibCharge(c, codeChge).equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: getLibCharge(c, codeChge));
				addZone(getNOM_ST_TAUX(indiceCharge), Float.valueOf(c.getTxSal()) == 0 ? "&nbsp;" : c.getTxSal());
				addZone(getNOM_ST_MONTANT(indiceCharge), Integer.valueOf(c.getMttreg()) == 0 ? "&nbsp;" : c.getMttreg());
				addZone(getNOM_ST_DATE_DEBUT(indiceCharge), c.getDatDeb());
				addZone(getNOM_ST_DATE_FIN(indiceCharge), c.getDatFin() == null ? "&nbsp;" : c.getDatFin());

				indiceCharge++;
			}
		}
	}

	/**
	 * Constructeur du process OeAGENTCharge. Date de cr�ation : (10/08/11
	 * 09:33:52)
	 * 
	 */
	public OeAGENTCharge() {
		super();
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER Date de cr�ation :
	 * (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_PB_AJOUTER() {
		return "NOM_PB_AJOUTER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public boolean performPB_AJOUTER(HttpServletRequest request) throws Exception {
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		// init de la visite courante
		setChargeCourante(new Charge());
		videZonesDeSaisie(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;

	}

	/**
	 * R�initilise les champs du formulaire de cr�ation/modification d'un
	 * accident de travail
	 * 
	 */
	private void videZonesDeSaisie(HttpServletRequest request) throws Exception {

		showCreancier = false;
		showCodeCharge = false;
		showMatriculeCharge = false;
		showMontant = false;
		matriculeChargeEditable = false;
		matriculeChargeObligatoire = false;
		montantObligatoire = false;

		// On vide les zone de saisie
		addZone(getNOM_EF_DATE_FIN(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_DEBUT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_MONTANT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_TAUX(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_CODE_CHARGE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_CREANCIER(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_MAT_CHARGE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_RUBRIQUE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_INFO_CODE_CHARGE(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_CREANCIER_SELECT(), Const.ZERO);
		addZone(getNOM_LB_CODE_CHARGE_SELECT(), Const.ZERO);
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de cr�ation :
	 * (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION())) {
			setStatut(STATUT_PROCESS_APPELANT);
		} else {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);
		}
		videZonesDeSaisie(request);
		return true;
	}

	/**
	 * Initilise les zones de saisie du formulaire de modification d'une prime
	 * Date de cr�ation : 11/08/10
	 * 
	 */
	private boolean initialiseChargeCourante(HttpServletRequest request) throws Exception {
		Rubrique r = (Rubrique) getHashRubriques().get(getChargeCourante().getNoRubr());
		Creancier c = (Creancier) getHashCreanciers().get(getChargeCourante().getCdCrea());

		initialiseChamp(r);

		afficheListeCodeCharge(r.getNumRubrique(), getChargeCourante().getCdChar());

		// Alim zones
		addZone(getNOM_ST_RUBRIQUE(), r.getLibRubrique());
		addZone(getNOM_EF_RUBRIQUE(), r.getNumRubrique() + " " + r.getLibRubrique());

		if (c != null) {
			int ligneCreancier = getListeCreancier().indexOf(c);
			addZone(getNOM_LB_CREANCIER_SELECT(), String.valueOf(ligneCreancier + 1));
			addZone(getNOM_ST_CREANCIER(), c.getDesign());
		}

		addZone(getNOM_EF_MAT_CHARGE(), getChargeCourante().getNoMate());
		addZone(getNOM_EF_MONTANT(), getChargeCourante().getMttreg());
		addZone(getNOM_EF_TAUX(), getChargeCourante().getTxSal());
		addZone(getNOM_EF_DATE_DEBUT(), getChargeCourante().getDatDeb());
		addZone(getNOM_EF_DATE_FIN(), getChargeCourante().getDatFin());

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de cr�ation :
	 * (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * V�rifie les r�gles de gestion de saisie (champs obligatoires, ...) du
	 * formulaire d'accident du travail
	 * 
	 * @param request
	 * @return true si les r�gles de gestion sont respect�es. false sinon.
	 * @throws Exception
	 * 
	 *             RG_AG_CG_A01 RG_AG_CG_C06
	 */
	public boolean performControlerChamps(HttpServletRequest request) throws Exception {
		// RG_AG_CG_A01

		// taux charge < 10
		if (Services.estNumerique(getVAL_EF_TAUX()) && Float.parseFloat(getVAL_EF_TAUX()) >= 10) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR101"));
			return false;
		}

		// date de d�but de la prime
		if ((Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_DEBUT()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "date de d�but"));
			return false;
		} else if (!Services.estUneDate(getZone(getNOM_EF_DATE_DEBUT()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de d�but"));
			return false;
		}

		// date de fin format date
		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_FIN())) && !Services.estUneDate(getZone(getNOM_EF_DATE_FIN()))) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de fin"));
			return false;
		}

		// date de fin > date de d�but
		// RG_AG_CG_C06
		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_FIN()))
				&& Services.compareDates(getZone(getNOM_EF_DATE_FIN()), getZone(getNOM_EF_DATE_DEBUT())) <= 0) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR205", "de fin", "de d�but"));
			return false;
		}

		// montant obligatoire
		if (montantObligatoire && showMontant && Const.CHAINE_VIDE.equals(getVAL_EF_MONTANT())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "montant forfait"));
			return false;
		}

		// montant numrique
		if (montantObligatoire && showMontant && !Services.estNumerique(getVAL_EF_MONTANT())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "montant forfait"));
			return false;
		}

		// cr�ancier obligatoire
		int indiceCreancier = (Services.estNumerique(getVAL_LB_CREANCIER_SELECT()) ? Integer.parseInt(getVAL_LB_CREANCIER_SELECT()) : -1);
		if (showCreancier && indiceCreancier < 1) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "cr�ancier"));
			return false;
		}

		// latricule charge employ�
		if (matriculeChargeObligatoire && Const.CHAINE_VIDE.equals(getVAL_EF_MAT_CHARGE())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "matricule charge employ�"));
			return false;
		}

		return true;
	}

	/**
	 * R�cupere la rubrique selectionn�e
	 * 
	 * @return
	 * @throws Exception
	 */
	private Rubrique getSelectedRubrique() throws Exception {
		// r�cup�ration de la rubrique et v�rification de son existence.
		String idRubrique = Const.CHAINE_VIDE;
		for (int i = 0; i < getListeRubriques().size(); i++) {
			Rubrique r = (Rubrique) getListeRubriques().get(i);
			String textRubr = r.getNumRubrique() + " " + r.getLibRubrique();
			if (textRubr.equals(getVAL_EF_RUBRIQUE())) {
				idRubrique = r.getNumRubrique();
				break;
			}
		}
		if (idRubrique.length() == 0) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "rubriques"));
		}
		return Rubrique.chercherRubrique(getTransaction(), idRubrique);
	}

	/**
	 * R�cupere le cr�ancier selectionn�
	 * 
	 * @return
	 */
	private Creancier getSelectedCreancier() {
		int numLigne = (Services.estNumerique(getZone(getNOM_LB_CREANCIER_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_CREANCIER_SELECT())) : -1);

		if (numLigne <= 0 || getListeCreancier().size() == 0 || numLigne > getListeCreancier().size()) {
			return null;
		}

		return (Creancier) getListeCreancier().get(numLigne - 1);
	}

	/**
	 * Verifie la regle de gestion du chevauchement des primes
	 * 
	 * @return RG_AG_CG_A02
	 */
	public boolean performConrolerRegleGestion(Rubrique rubrique) throws Exception {
		// RG_AG_CG_A02

		// verification des regles de gestions
		ArrayList<Charge> listeCharge = getListeCharges();
		for (int i = 0; i < listeCharge.size(); i++) {
			Charge c = (Charge) listeCharge.get(i);

			if (!c.getNoRubr().equals(rubrique.getNumRubrique()))
				continue;

			if (c == getChargeCourante())
				continue;

			if (Services.compareDates(c.getDatDeb(), getChargeCourante().getDatDeb()) >= 0) {
				// dateDeb >= dateDebCur
				if (getChargeCourante().getDatFin() == null || getChargeCourante().getDatFin().equals(Const.CHAINE_VIDE)) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR100"));
					return false;
				} else if (Services.compareDates(getChargeCourante().getDatFin(), c.getDatDeb()) > 0) {
					// dateFin > dateDebCur
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR100"));
					return false;
				}
			} else {
				// dateDebCur >= dateDEb
				if (c.getDatFin() == null || c.getDatFin().equals(Const.CHAINE_VIDE)) {
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR100"));
					return false;
				} else if (Services.compareDates(c.getDatFin(), getChargeCourante().getDatDeb()) > 0) {
					// dateFin > dateDebCur
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR100"));
					return false;
				}
			}
		}

		if (getChargeCourante().getNoRubr().equals("2900") && getChargeCourante().getNoMate() == null) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "rubriques"));
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @throws Exception
	 */
	public String getCdCharge() throws Exception {

		Rubrique r = getSelectedRubrique();

		showCreancier = false;
		showCodeCharge = false;
		showMontant = false;

		int numLigne = (Services.estNumerique(getVAL_LB_CODE_CHARGE_SELECT()) ? Integer.parseInt(getVAL_LB_CODE_CHARGE_SELECT()) : -1);

		if (r != null) {

			if (r.getNumRubrique().equals("2850") || r.getNumRubrique().equals("2900")) {
				// SPACCI accident du tavail

				if (numLigne < 0 || getListeCodesAcci().size() == 0 || numLigne > getListeCodesAcci().size()) {
					return Const.ZERO;
				}

				return getListeCodesAcci().get(numLigne).getCdacci();
			}

			if (r.getNumRubrique().equals("3000")) {
				// SPMUTU code mutuelle

				if (numLigne < 0 || getListeCodesMutu().size() == 0 || numLigne > getListeCodesMutu().size()) {
					return Const.ZERO;
				}

				return getListeCodesMutu().get(numLigne).getCdmutu();
			}

			if (r.getNumRubrique().equals("4000")) {
				// SPCLOG code logement

				if (numLigne < 0 || getListeCodesLogt().size() == 0 || numLigne > getListeCodesLogt().size()) {
					return Const.ZERO;
				}

				return getListeCodesLogt().get(numLigne).getCdlogt();
			}

			if (r.getNumRubrique().equals("4001")) {
				// SPCCHG code charges logement

				if (numLigne < 0 || getListeCodesChargeLogt().size() == 0 || numLigne > getListeCodesChargeLogt().size()) {
					return Const.ZERO;
				}

				return getListeCodesChargeLogt().get(numLigne).getCdlogt();
			}
		}

		addZone(getNOM_LB_CREANCIER_SELECT(), Const.ZERO);
		addZone(getNOM_LB_CODE_CHARGE_SELECT(), Const.ZERO);

		return Const.ZERO;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 * RG_AG_CG_A04
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);

		// Si Action Suppression
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_SUPPRESSION)) {

			// Suppression du lien
			ChargeAgent chargeAgent = ChargeAgent.chercherChargeAgent(getTransaction(), getAgentCourant().idAgent,
					getAgentCourant().getNoMatricule(), getChargeCourante().getNoRubr(), getChargeCourante().getDatDeb());
			chargeAgent.supprimerChargeAgent(getTransaction());

			// suppression
			getChargeCourante().supprimerCharge(getTransaction(), user);
			if (getTransaction().isErreur())
				return false;

			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);

		} else {

			// v�rification de la validit� du formulaire
			if (!performControlerChamps(request)) {
				return false;
			}

			Rubrique r = getSelectedRubrique();

			if (r == null) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "rubriques"));
				return false;
			}

			// r�cup�ration des informations remplies dans les zones de saisie
			String montant = getZone(getNOM_EF_MONTANT());
			String taux = getZone(getNOM_EF_TAUX());
			String dateFin = getZone(getNOM_EF_DATE_FIN());
			String dateDebut = getZone(getNOM_EF_DATE_DEBUT());
			String matriculeCharge = getZone(getNOM_EF_MAT_CHARGE());

			Creancier c = getSelectedCreancier();

			// Cr�ation de l'objet VisiteMedicale � cr�er/modifier
			AgentNW agentCourant = getAgentCourant();
			getChargeCourante().setNoMatr(agentCourant.getNoMatricule());
			getChargeCourante().setNoRubr(r.getNumRubrique());
			getChargeCourante().setCdCrea(c != null ? c.getCdCrea() : Const.ZERO);
			getChargeCourante().setMttreg(montant.equals(Const.CHAINE_VIDE) ? Const.ZERO : montant);
			getChargeCourante().setTxSal(taux);
			getChargeCourante().setDatDeb(dateDebut);
			getChargeCourante().setDatFin(dateFin);
			getChargeCourante().setNoMate(matriculeCharge.trim());
			getChargeCourante().setCdChar(getCdCharge());

			// r�gles de gestions
			if (!performConrolerRegleGestion(r))
				return false;

			if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
				// Modification
				getChargeCourante().modifierCharge(getTransaction(), getAgentCourant(), user);
			} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
				// Cr�ation
				getChargeCourante().creerCharge(getTransaction(), user);

				ChargeAgent chargeAgent = new ChargeAgent(getAgentCourant().getIdAgent(), getAgentCourant().getNoMatricule(), getChargeCourante()
						.getNoRubr(), getChargeCourante().getDatDeb());
				chargeAgent.creerChargeAgent(getTransaction());
			}
			// RG_AG_CG_A04
			Matricule.updateMatricule(getTransaction(), getAgentCourant(), getChargeCourante().getDatDeb());

			if (getTransaction().isErreur())
				return false;
		}

		initialiseListeCharges(request);

		if (getTransaction().isErreur())
			return false;

		// On a fini l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Tout s'est bien pass�
		commitTransaction();

		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACTION Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_CHARGE Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_CODE_CHARGE() {
		return "NOM_ST_CODE_CHARGE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_CODE_CHARGE
	 * Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_CODE_CHARGE() {
		return getZone(getNOM_ST_CODE_CHARGE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CREANCIER Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_CREANCIER() {
		return "NOM_ST_CREANCIER";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_CREANCIER Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_CREANCIER() {
		return getZone(getNOM_ST_CREANCIER());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RUBRIQUE Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_ST_RUBRIQUE() {
		return "NOM_ST_RUBRIQUE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_RUBRIQUE Date
	 * de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_ST_RUBRIQUE() {
		return getZone(getNOM_ST_RUBRIQUE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_DEBUT Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_EF_DATE_DEBUT() {
		return "NOM_EF_DATE_DEBUT";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_DATE_DEBUT Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_EF_DATE_DEBUT() {
		return getZone(getNOM_EF_DATE_DEBUT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_FIN Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_EF_DATE_FIN() {
		return "NOM_EF_DATE_FIN";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_DATE_FIN Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_EF_DATE_FIN() {
		return getZone(getNOM_EF_DATE_FIN());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_MONTANT Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_EF_MONTANT() {
		return "NOM_EF_MONTANT";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_MONTANT Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_EF_MONTANT() {
		return getZone(getNOM_EF_MONTANT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TAUX Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_EF_TAUX() {
		return "NOM_EF_TAUX";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie : EF_TAUX
	 * Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_EF_TAUX() {
		return getZone(getNOM_EF_TAUX());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_CODE_CHARGE Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	private String[] getLB_CODE_CHARGE() {
		if (LB_CODE_CHARGE == null)
			LB_CODE_CHARGE = initialiseLazyLB();
		return LB_CODE_CHARGE;
	}

	/**
	 * Setter de la liste: LB_CODE_CHARGE Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	private void setLB_CODE_CHARGE(String[] newLB_CODE_CHARGE) {
		LB_CODE_CHARGE = newLB_CODE_CHARGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CODE_CHARGE Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_LB_CODE_CHARGE() {
		return "NOM_LB_CODE_CHARGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_CODE_CHARGE_SELECT Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_LB_CODE_CHARGE_SELECT() {
		return "NOM_LB_CODE_CHARGE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_CODE_CHARGE Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String[] getVAL_LB_CODE_CHARGE() {
		return getLB_CODE_CHARGE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_CODE_CHARGE Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_LB_CODE_CHARGE_SELECT() {
		return getZone(getNOM_LB_CODE_CHARGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_CREANCIER Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	private String[] getLB_CREANCIER() {
		if (LB_CREANCIER == null)
			LB_CREANCIER = initialiseLazyLB();
		return LB_CREANCIER;
	}

	/**
	 * Setter de la liste: LB_CREANCIER Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	private void setLB_CREANCIER(String[] newLB_CREANCIER) {
		LB_CREANCIER = newLB_CREANCIER;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_CREANCIER Date de
	 * cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_LB_CREANCIER() {
		return "NOM_LB_CREANCIER";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_CREANCIER_SELECT Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getNOM_LB_CREANCIER_SELECT() {
		return "NOM_LB_CREANCIER_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_CREANCIER Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String[] getVAL_LB_CREANCIER() {
		return getLB_CREANCIER();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_CREANCIER Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public String getVAL_LB_CREANCIER_SELECT() {
		return getZone(getNOM_LB_CREANCIER_SELECT());
	}

	public AgentNW getAgentCourant() {
		return agentCourant;
	}

	private void setAgentCourant(AgentNW agentCourant) {
		this.agentCourant = agentCourant;
	}

	private Charge getChargeCourante() {
		return chargeCourante;
	}

	private void setChargeCourante(Charge chargeCourante) {
		this.chargeCourante = chargeCourante;
	}

	private Hashtable<String, Creancier> getHashCreanciers() {
		if (hashCreanciers == null)
			hashCreanciers = new Hashtable<String, Creancier>();
		return hashCreanciers;
	}

	private Hashtable<String, Rubrique> getHashRubriques() {
		if (hashRubriques == null)
			hashRubriques = new Hashtable<String, Rubrique>();
		return hashRubriques;
	}

	public ArrayList<Charge> getListeCharges() {
		return listeCharges;
	}

	private void setListeCharges(ArrayList<Charge> listeCharges) {
		this.listeCharges = listeCharges;
	}

	private ArrayList<Creancier> getListeCreancier() {
		return listeCreancier;
	}

	private void setListeCreancier(ArrayList<Creancier> listeCreancier) {
		this.listeCreancier = listeCreancier;
	}

	public ArrayList<Rubrique> getListeRubriques() {
		return listeRubriques;
	}

	private void setListeRubriques(ArrayList<Rubrique> listeRubriques) {
		this.listeRubriques = listeRubriques;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SELECT_RUBRIQUE Date de
	 * cr�ation : (10/08/11 14:21:39)
	 * 
	 */
	public String getNOM_PB_SELECT_RUBRIQUE() {
		return "NOM_PB_SELECT_RUBRIQUE";
	}

	/**
	 * Affiche la bonne liste des code charge en fonction du code de rubrique
	 * 
	 * @param codeRubr
	 * @throws Exception
	 *             RG_AG_CG_C05
	 */
	public void afficheListeCodeCharge(String codeRubr, String codeCharge) throws Exception {
		// RG_AG_CG_C05

		int[] tailles = { 3, 40 };

		showCodeCharge = true;

		switch (Integer.parseInt(codeRubr)) {
		case 2850:
		case 2900:
			String[] champsAcci = { "cdacci", "libacc" };
			setLB_CODE_CHARGE(new FormateListe(tailles, getListeCodesAcci(), champsAcci).getListeFormatee(false));

			if (codeCharge != null) {
				CodeAcci c = (CodeAcci) getHashCodesAcci().get(codeCharge);
				int ligneCode = getListeCodesAcci().indexOf(c);
				addZone(getNOM_LB_CODE_CHARGE_SELECT(), String.valueOf(ligneCode));
				addZone(getNOM_ST_CODE_CHARGE(), c.getLibacc());
			}

			break;
		case 3000:
			int[] taillesMutu = { 3, 25 };
			String padding[] = { "G", "G" };
			String[] champsMutu = { "cdmutu", "limutu" };
			setLB_CODE_CHARGE(new FormateListe(taillesMutu, getListeCodesMutu(), champsMutu, padding, false).getListeFormatee(false));

			if (codeCharge != null) {
				CodeMutu c = (CodeMutu) getHashCodesMutu().get(codeCharge);
				int ligneCode = getListeCodesMutu().indexOf(c);
				addZone(getNOM_LB_CODE_CHARGE_SELECT(), String.valueOf(ligneCode));
				addZone(getNOM_ST_CODE_CHARGE(), c.getLimutu());
			}

			break;

		case 4000:
			int[] taillesCodeLogt = { 3, 30, 10 };
			FormateListe aFormat = new FormateListe(taillesCodeLogt);
			for (int i = 0; i < getListeCodesLogt().size(); i++) {
				CodeLogt c = getListeCodesLogt().get(i);
				String tauxAff = Const.CHAINE_VIDE;
				if (Float.parseFloat(c.getTxsal()) != 0) {
					Float taux = Float.parseFloat(c.getTxsal()) * 10;
					tauxAff = taux.toString();
					if (tauxAff.length() > 5) {
						tauxAff = tauxAff.substring(0, 5);
					}
				}
				String ligne[] = { c.getCdlogt(), c.getLiblog(), tauxAff.equals(Const.CHAINE_VIDE) ? Const.CHAINE_VIDE : tauxAff + "%" };
				aFormat.ajouteLigne(ligne);
			}
			setLB_CODE_CHARGE(aFormat.getListeFormatee(false));

			if (codeCharge != null) {
				CodeLogt c = (CodeLogt) getHashCodesLogt().get(codeCharge);
				int ligneCode = getListeCodesLogt().indexOf(c);
				addZone(getNOM_LB_CODE_CHARGE_SELECT(), String.valueOf(ligneCode));
				addZone(getNOM_ST_CODE_CHARGE(), c.getLiblog());
			}
			break;

		case 4001:
			String[] champs = { "cdlogt", "liblog" };

			setLB_CODE_CHARGE(new FormateListe(tailles, getListeCodesChargeLogt(), champs).getListeFormatee(false));

			if (codeCharge != null) {
				CodeChargeLogt c = (CodeChargeLogt) getHashCodesChargeLogt().get(codeCharge);
				int ligneCode = getListeCodesChargeLogt().indexOf(c);
				addZone(getNOM_LB_CODE_CHARGE_SELECT(), String.valueOf(ligneCode));
				addZone(getNOM_ST_CODE_CHARGE(), c.getLiblog());
			}

			break;
		default:
			showCodeCharge = false;
			break;
		}
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (10/08/11 14:21:39)
	 * 
	 */
	public boolean performPB_SELECT_RUBRIQUE(HttpServletRequest request) throws Exception {

		Rubrique r = getSelectedRubrique();
		getChargeCourante().setCdChar(null);

		initialiseChamp(r);

		return true;
	}

	/**
	 * 
	 * @param r
	 * @throws Exception
	 *             RG_AG_CG_C09 RG_AG_CG_C08 RG_AG_CG_C10 RG_AG_CG_C11
	 */
	private void initialiseChamp(Rubrique r) throws Exception {
		// RG_AG_CG_C09
		// RG_AG_CG_C08
		// RG_AG_CG_C10
		// RG_AG_CG_C11

		showCreancier = false;
		showCodeCharge = false;
		showMatriculeCharge = false;
		showMontant = false;
		matriculeChargeEditable = false;
		matriculeChargeObligatoire = false;
		montantObligatoire = false;

		addZone(getNOM_EF_MAT_CHARGE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_MONTANT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_TAUX(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_MAT_CHARGE(), Const.CHAINE_VIDE);
		if (r != null) {

			afficheListeCodeCharge(r.numRubrique, null);

			if (r.getNumRubrique().equals("2400")) {
				matriculeChargeEditable = true;
				showMatriculeCharge = true;
				return;
			}

			if (r.getNumRubrique().equals("2700")) {
				addZone(getNOM_EF_MAT_CHARGE(), getAgentCourant().getNumIrcafex());
				matriculeChargeEditable = true;
				showMatriculeCharge = true;
				return;
			}

			if (r.getNumRubrique().equals("2800")) {
				addZone(getNOM_EF_MAT_CHARGE(), getAgentCourant().getNumCre());
				matriculeChargeEditable = true;
				showMatriculeCharge = true;
				return;
			}

			if (r.getNumRubrique().equals("2850")) {
				addZone(getNOM_EF_MAT_CHARGE(), getAgentCourant().getNumRuamm());
				showMatriculeCharge = true;
				matriculeChargeEditable = true;
				return;
			}

			if (r.getNumRubrique().equals("2900")) {
				addZone(getNOM_EF_MAT_CHARGE(), getAgentCourant().getNumCafat());
				matriculeChargeEditable = true;
				showMatriculeCharge = true;
				matriculeChargeObligatoire = true;
				return;
			}

			if (r.getNumRubrique().equals("3000")) {
				addZone(getNOM_EF_MAT_CHARGE(), getAgentCourant().getNumMutuelle());
				matriculeChargeEditable = true;
				showMatriculeCharge = true;
				return;
			}

			if (r.getNumRubrique().equals("6000")) {
				matriculeChargeEditable = true;
				showMontant = true;
				montantObligatoire = true;
				showMatriculeCharge = true;
				return;
			}

			if (r.getNumRubrique().equals("7900")) {
				showCreancier = true;
				showMontant = true;
				montantObligatoire = true;
			}

			if (r.getNumRubrique().equals("1050")) {
				addZone(getNOM_EF_MAT_CHARGE(), getAgentCourant().getNumClr());
				matriculeChargeEditable = true;
				showMatriculeCharge = true;
				showMontant = true;
				return;
			}

			if (r.getNumRubrique().equals("6000") || r.getNumRubrique().equals("8797") || r.getNumRubrique().equals("8798")
					|| r.getNumRubrique().equals("8799") || r.getNumRubrique().equals("1030") || r.getNumRubrique().equals("1031")
					|| r.getNumRubrique().equals("1035") || r.getNumRubrique().equals("1036")) {
				showMontant = true;
				montantObligatoire = true;
			}
		}
	}

	private ArrayList<CodeAcci> getListeCodesAcci() {
		return listeCodesAcci;
	}

	private void setListeCodesAcci(ArrayList<CodeAcci> listeCodesAcci) {
		this.listeCodesAcci = listeCodesAcci;
	}

	private ArrayList<CodeChargeLogt> getListeCodesChargeLogt() {
		return listeCodesChargeLogt;
	}

	private void setListeCodesChargeLogt(ArrayList<CodeChargeLogt> listeCodesChargeLogt) {
		this.listeCodesChargeLogt = listeCodesChargeLogt;
	}

	private ArrayList<CodeLogt> getListeCodesLogt() {
		return listeCodesLogt;
	}

	private void setListeCodesLogt(ArrayList<CodeLogt> listeCodesLogt) {
		this.listeCodesLogt = listeCodesLogt;
	}

	private ArrayList<CodeMutu> getListeCodesMutu() {
		return listeCodesMutu;
	}

	private void setListeCodesMutu(ArrayList<CodeMutu> listeCodesMutu) {
		this.listeCodesMutu = listeCodesMutu;
	}

	private Hashtable<String, CodeAcci> getHashCodesAcci() {
		if (hashCodesAcci == null)
			hashCodesAcci = new Hashtable<String, CodeAcci>();
		return hashCodesAcci;
	}

	private Hashtable<String, CodeChargeLogt> getHashCodesChargeLogt() {
		if (hashCodesChargeLogt == null)
			hashCodesChargeLogt = new Hashtable<String, CodeChargeLogt>();
		return hashCodesChargeLogt;
	}

	private Hashtable<String, CodeLogt> getHashCodesLogt() {
		if (hashCodesLogt == null)
			hashCodesLogt = new Hashtable<String, CodeLogt>();
		return hashCodesLogt;
	}

	private Hashtable<String, CodeMutu> getHashCodesMutu() {
		if (hashCodesMutu == null)
			hashCodesMutu = new Hashtable<String, CodeMutu>();
		return hashCodesMutu;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_MATRICULE Date de
	 * cr�ation : (11/08/11 09:43:07)
	 * 
	 */
	public String getNOM_EF_MATRICULE() {
		return "NOM_EF_MATRICULE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_MATRICULE Date de cr�ation : (11/08/11 09:43:07)
	 * 
	 */
	public String getVAL_EF_MATRICULE() {
		return getZone(getNOM_EF_MATRICULE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_MAT_CHARGE Date de
	 * cr�ation : (11/08/11 16:10:45)
	 * 
	 */
	public String getNOM_EF_MAT_CHARGE() {
		return "NOM_EF_MAT_CHARGE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_MAT_CHARGE Date de cr�ation : (11/08/11 16:10:45)
	 * 
	 */
	public String getVAL_EF_MAT_CHARGE() {
		return getZone(getNOM_EF_MAT_CHARGE());
	}

	public String getNomEcran() {
		return "ECR-AG-ELTSAL-CHARGES";
	}

	/**
	 * M�thode appel�e par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de cr�ation : (10/08/11 09:33:52)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_SELECT_RUBRIQUE
			if (testerParametre(request, getNOM_PB_SELECT_RUBRIQUE())) {
				return performPB_SELECT_RUBRIQUE(request);
			}

			// Si clic sur le bouton PB_SELECT_CODE_CHARGE
			if (testerParametre(request, getNOM_PB_SELECT_CODE_CHARGE())) {
				return performPB_SELECT_CODE_CHARGE(request);
			}

			// Si clic sur le bouton PB_AJOUTER
			if (testerParametre(request, getNOM_PB_AJOUTER())) {
				return performPB_AJOUTER(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeCharges().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_CONSULTER
			for (int i = 0; i < getListeCharges().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListeCharges().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}

		}
		// Si TAG INPUT non g�r� par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (18/10/11 11:43:32)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTCharge.jsp";
	}

	public boolean isMatriculeChargeEditable() {
		return matriculeChargeEditable;
	}

	public void setMatriculeChargeEditable(boolean matriculeChargeEditable) {
		this.matriculeChargeEditable = matriculeChargeEditable;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SELECT_CODE_CHARGE Date de
	 * cr�ation : (10/08/11 14:21:39)
	 * 
	 */
	public String getNOM_PB_SELECT_CODE_CHARGE() {
		return "NOM_PB_SELECT_CODE_CHARGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (10/08/11 14:21:39)
	 * 
	 */
	public boolean performPB_SELECT_CODE_CHARGE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_INFO_CODE_CHARGE(), Const.CHAINE_VIDE);
		// R�cup�ration du code charge s�lectionn�
		int indiceCodeCharge = (Services.estNumerique(getVAL_LB_CODE_CHARGE_SELECT()) ? Integer.parseInt(getVAL_LB_CODE_CHARGE_SELECT()) : -1);
		if (indiceCodeCharge == -1 || getListeCodesMutu().size() == 0 || indiceCodeCharge > getListeCodesMutu().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "charge"));
			return false;
		}
		// si rubrique 3000 on affiche � cot� de la liste deroulante le txsal et
		// txpat
		showDonneesMutu = false;
		Rubrique r = getSelectedRubrique();
		if (r != null) {
			if (r.getNumRubrique().equals("3000")) {
				CodeMutu g = (CodeMutu) getListeCodesMutu().get(indiceCodeCharge);
				String txSal = String.valueOf(Double.parseDouble(g.getTxsal()) * 100).length() > 4 ? String.valueOf(
						Double.parseDouble(g.getTxsal()) * 100).substring(0, 4) : String.valueOf(Double.parseDouble(g.getTxsal()) * 100);
				String txPat = String.valueOf(Double.parseDouble(g.getTxpat()) * 100).length() > 4 ? String.valueOf(
						Double.parseDouble(g.getTxpat()) * 100).substring(0, 4) : String.valueOf(Double.parseDouble(g.getTxpat()) * 100);
				addZone(getNOM_ST_INFO_CODE_CHARGE(), "TxSal : " + txSal + "% , TxPat : " + txPat + "%");
				showDonneesMutu = true;
			}
		}
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INFO_CODE_CHARGE
	 * Date de cr�ation : (29/11/11 16:42:44)
	 * 
	 */
	public String getNOM_ST_INFO_CODE_CHARGE() {
		return "NOM_ST_INFO_CODE_CHARGE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_INFO_CODE_CHARGE Date de cr�ation : (29/11/11 16:42:44)
	 * 
	 */
	public String getVAL_ST_INFO_CODE_CHARGE() {
		return getZone(getNOM_ST_INFO_CODE_CHARGE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_RUBRIQUE Date de
	 * cr�ation : (30/08/11 10:25:41)
	 * 
	 */
	public String getNOM_EF_RUBRIQUE() {
		return "NOM_EF_RUBRIQUE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_RUBRIQUE Date de cr�ation : (30/08/11 10:25:41)
	 * 
	 */
	public String getVAL_EF_RUBRIQUE() {
		return getZone(getNOM_EF_RUBRIQUE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_RUBR Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_CODE_RUBR(int i) {
		return "NOM_ST_CODE_RUBR" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_CODE_RUBR Date
	 * de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_CODE_RUBR(int i) {
		return getZone(getNOM_ST_CODE_RUBR(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RUBRIQUE Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_RUBRIQUE(int i) {
		return "NOM_ST_RUBRIQUE" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_RUBRIQUE Date
	 * de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_RUBRIQUE(int i) {
		return getZone(getNOM_ST_RUBRIQUE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MAT_CHARGE Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_MAT_CHARGE(int i) {
		return "NOM_ST_MAT_CHARGE" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_MAT_CHARGE
	 * Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_MAT_CHARGE(int i) {
		return getZone(getNOM_ST_MAT_CHARGE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_CHARGE Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB_CHARGE(int i) {
		return "NOM_ST_LIB_CHARGE" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_LIB_CHARGE
	 * Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB_CHARGE(int i) {
		return getZone(getNOM_ST_LIB_CHARGE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TAUX Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_TAUX(int i) {
		return "NOM_ST_TAUX" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_TAUX Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_TAUX(int i) {
		return getZone(getNOM_ST_TAUX(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MONTANT Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_MONTANT(int i) {
		return "NOM_ST_MONTANT" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_MONTANT Date
	 * de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_MONTANT(int i) {
		return getZone(getNOM_ST_MONTANT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DEBUT Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_DEBUT(int i) {
		return "NOM_ST_DATE_DEBUT" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DATE_DEBUT
	 * Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_DEBUT(int i) {
		return getZone(getNOM_ST_DATE_DEBUT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_FIN Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_FIN(int i) {
		return "NOM_ST_DATE_FIN" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_DATE_FIN Date
	 * de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_FIN(int i) {
		return getZone(getNOM_ST_DATE_FIN(i));
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// R�cup de la charge courante
		Charge chargeCourante = (Charge) getListeCharges().get(indiceEltAModifier);
		setChargeCourante(chargeCourante);

		// init du diplome courant
		if (!initialiseChargeCourante(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION Date de
	 * cr�ation : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_CONSULTER(int i) {
		return "NOM_PB_CONSULTER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_CONSULTER(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		// R�cup de la charge courante
		Charge chargeCourante = (Charge) getListeCharges().get(indiceEltAConsulter);
		setChargeCourante(chargeCourante);

		// init du diplome courant
		if (!initialiseChargeCourante(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CONSULTATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER Date de cr�ation
	 * : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER(int i) {
		return "NOM_PB_SUPPRIMER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// R�cup de la charge courante
		Charge chargeCourante = (Charge) getListeCharges().get(indiceEltASuprimer);
		setChargeCourante(chargeCourante);

		// init du diplome courant
		if (!initialiseChargeCourante(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}
}