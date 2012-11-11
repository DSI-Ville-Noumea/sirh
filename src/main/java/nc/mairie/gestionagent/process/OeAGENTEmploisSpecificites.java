package nc.mairie.gestionagent.process;

import java.util.ArrayList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.parametrage.NatureAvantage;
import nc.mairie.metier.parametrage.TypeAvantage;
import nc.mairie.metier.parametrage.TypeDelegation;
import nc.mairie.metier.parametrage.TypeRegIndemn;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.specificites.AvantageNature;
import nc.mairie.metier.specificites.AvantageNatureAFF;
import nc.mairie.metier.specificites.Delegation;
import nc.mairie.metier.specificites.DelegationAFF;
import nc.mairie.metier.specificites.RegIndemnAFF;
import nc.mairie.metier.specificites.RegimeIndemnitaire;
import nc.mairie.metier.specificites.Rubrique;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

/**
 * Process OeAGENTEmploisSpecificites Date de cr�ation : (16/08/11 15:48:02)
 * 
 */
public class OeAGENTEmploisSpecificites extends nc.mairie.technique.BasicProcess {

	private final String ACTION_AJOUTER = "Ajouter";
	public final String ACTION_SUPPRIMER = "Supprimer";
	public final String SPEC_AVANTAGE_NATURE = "avantage en nature";
	public final String SPEC_DELEGATION = "d�l�gation";
	public final String SPEC_REG_INDEMN = "r�gime indemnitaire";

	private String[] LB_NATURE_AVANTAGE;
	private String[] LB_RUBRIQUE_AVANTAGE;
	private String[] LB_RUBRIQUE_REGIME;
	private String[] LB_TYPE_AVANTAGE;
	private String[] LB_TYPE_DELEGATION;
	private String[] LB_TYPE_REGIME;

	private ArrayList listeAvantageAFF;
	private ArrayList listeAvantageFP;
	private ArrayList listeAvantageAAjouter;
	private ArrayList listeAvantageASupprimer;
	private ArrayList listeDelegationAFF;
	private ArrayList listeDelegationFP;
	private ArrayList listeDelegationAAjouter;
	private ArrayList listeDelegationASupprimer;
	private ArrayList listeRegimeAFF;
	private ArrayList listeRegimeFP;
	private ArrayList listeRegimeAAjouter;
	private ArrayList listeRegimeASupprimer;

	private ArrayList listeTypeAvantage;
	private ArrayList listeNatureAvantage;
	private ArrayList listeRubrique;
	private ArrayList listeTypeDelegation;
	private ArrayList listeTypeRegIndemn;

	private AgentNW agentCourant;
	private FichePoste fichePosteCourant;
	private FichePoste fichePosteSecondaireCourant;
	private Affectation affectationCourant;

	public String focus = null;

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (16/08/11 15:48:02)
	 * 
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

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			AgentNW aAgent = (AgentNW) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
			} else {
				// ERR004 : "Vous devez d'abord rechercher un agent."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}

		// Si pas d'affectation en cours
		if (getFichePosteCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			ArrayList affActives = Affectation.listerAffectationActiveAvecAgent(getTransaction(), getAgentCourant());
			if (affActives.size() == 1) {
				setAffectationCourant((Affectation) affActives.get(0));
				// Recherche des informations � afficher
				setFichePosteCourant(FichePoste.chercherFichePoste(getTransaction(), getAffectationCourant().getIdFichePoste()));
				if (getAffectationCourant().getIdFichePosteSecondaire() != null) {
					setFichePosteSecondaireCourant(FichePoste.chercherFichePoste(getTransaction(), getAffectationCourant()
							.getIdFichePosteSecondaire()));
				}
			} else if (affActives.size() == 0) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR083"));
				return;
			} else if (affActives.size() > 1) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR082"));
				return;
			}
		}

		initialiseListeDeroulante();
		initialiseListeSpecificites();
		if (getVAL_RG_SPECIFICITE() == null || getVAL_RG_SPECIFICITE().length() == 0) {
			addZone(getNOM_RG_SPECIFICITE(), getNOM_RB_SPECIFICITE_AN());
			addZone(getNOM_ST_SPECIFICITE(), SPEC_AVANTAGE_NATURE);
		}
	}

	/**
	 * Initialise les listes de sp�cificit�s. Date de cr�ation : (28/07/11)
	 * 
	 * @throws Exception
	 */
	private void initialiseListeSpecificites() throws Exception {
		// Avantages en nature
		if (getListeAvantageFP() == null && getFichePosteCourant() != null && getFichePosteCourant().getIdFichePoste() != null) {
			setListeAvantageFP(AvantageNature.listerAvantageNatureAvecFP(getTransaction(), getFichePosteCourant().getIdFichePoste()));
			if (getFichePosteSecondaireCourant() != null) {
				getListeAvantageFP().addAll(
						AvantageNature.listerAvantageNatureAvecFP(getTransaction(), getFichePosteSecondaireCourant().getIdFichePoste()));
			}
		}
		if (getListeAvantageAFF() == null && getFichePosteCourant() != null && getFichePosteCourant().getIdFichePoste() != null) {
			setListeAvantageAFF(AvantageNature.listerAvantageNatureAvecAFF(getTransaction(), getAffectationCourant().getIdAffectation()));
		}
		int indiceAvNat = 0;
		if (getListeAvantageFP() != null && getListeAvantageFP().size() != 0) {
			for (int i = 0; i < getListeAvantageFP().size(); i++) {
				AvantageNature aAvNat = (AvantageNature) getListeAvantageFP().get(i);
				if (aAvNat != null) {
					TypeAvantage typAv = TypeAvantage.chercherTypeAvantage(getTransaction(), aAvNat.getIdTypeAvantage());
					NatureAvantage natAv = aAvNat.getIdNatureAvantage() == null ? null : NatureAvantage.chercherNatureAvantage(getTransaction(),
							aAvNat.getIdNatureAvantage());
					Rubrique rubr = aAvNat.getNumRubrique() == null ? null : Rubrique.chercherRubrique(getTransaction(), aAvNat.getNumRubrique());
					if (getListeAvantageAFF().contains(aAvNat))
						addZone(getNOM_CK_AVANTAGE(indiceAvNat), getCHECKED_ON());
					addZone(getNOM_ST_LST_AVANTAGE_TYPE(indiceAvNat), typAv.getLibTypeAvantage());
					addZone(getNOM_ST_LST_AVANTAGE_MONTANT(indiceAvNat), aAvNat.getMontant());
					if (natAv != null && natAv.getIdNatureAvantage() != null)
						addZone(getNOM_ST_LST_AVANTAGE_NATURE(indiceAvNat), natAv.getLibNatureAvantage());
					if (rubr != null && rubr.getNumRubrique() != null)
						addZone(getNOM_ST_LST_AVANTAGE_RUBRIQUE(indiceAvNat), rubr.getLibRubrique());
					indiceAvNat++;
				}
			}
		}
		if (getListeAvantageAFF() != null && getListeAvantageAFF().size() != 0) {
			for (int j = 0; j < getListeAvantageAFF().size(); j++) {
				AvantageNature aAvNat = (AvantageNature) getListeAvantageAFF().get(j);
				if (aAvNat != null && !getListeAvantageFP().contains(aAvNat)) {
					TypeAvantage typAv = TypeAvantage.chercherTypeAvantage(getTransaction(), aAvNat.getIdTypeAvantage());
					NatureAvantage natAv = aAvNat.getIdNatureAvantage() == null ? null : NatureAvantage.chercherNatureAvantage(getTransaction(),
							aAvNat.getIdNatureAvantage());
					Rubrique rubr = aAvNat.getNumRubrique() == null ? null : Rubrique.chercherRubrique(getTransaction(), aAvNat.getNumRubrique());
					addZone(getNOM_ST_LST_AVANTAGE_TYPE(indiceAvNat), typAv.getLibTypeAvantage());
					addZone(getNOM_ST_LST_AVANTAGE_MONTANT(indiceAvNat), aAvNat.getMontant());
					if (natAv != null && natAv.getIdNatureAvantage() != null)
						addZone(getNOM_ST_LST_AVANTAGE_NATURE(indiceAvNat), natAv.getLibNatureAvantage());
					if (rubr != null && rubr.getNumRubrique() != null)
						addZone(getNOM_ST_LST_AVANTAGE_RUBRIQUE(indiceAvNat), rubr.getLibRubrique());
					indiceAvNat++;
				}
			}
		}
		if (getListeAvantageAAjouter() != null && getListeAvantageAAjouter().size() != 0) {
			for (int k = 0; k < getListeAvantageAAjouter().size(); k++) {
				AvantageNature aAvNat = (AvantageNature) getListeAvantageAAjouter().get(k);
				if (aAvNat != null) {
					TypeAvantage typAv = TypeAvantage.chercherTypeAvantage(getTransaction(), aAvNat.getIdTypeAvantage());
					NatureAvantage natAv = aAvNat.getIdNatureAvantage() == null ? null : NatureAvantage.chercherNatureAvantage(getTransaction(),
							aAvNat.getIdNatureAvantage());
					Rubrique rubr = aAvNat.getNumRubrique() == null ? null : Rubrique.chercherRubrique(getTransaction(), aAvNat.getNumRubrique());
					addZone(getNOM_ST_LST_AVANTAGE_TYPE(indiceAvNat), typAv.getLibTypeAvantage());
					addZone(getNOM_ST_LST_AVANTAGE_MONTANT(indiceAvNat), aAvNat.getMontant());
					if (natAv != null && natAv.getIdNatureAvantage() != null)
						addZone(getNOM_ST_LST_AVANTAGE_NATURE(indiceAvNat), natAv.getLibNatureAvantage());
					if (rubr != null && rubr.getNumRubrique() != null)
						addZone(getNOM_ST_LST_AVANTAGE_RUBRIQUE(indiceAvNat), rubr.getLibRubrique());
					indiceAvNat++;
				}
			}
		}

		// D�l�gations
		if (getListeDelegationFP() == null && getFichePosteCourant() != null && getFichePosteCourant().getIdFichePoste() != null) {
			setListeDelegationFP(Delegation.listerDelegationAvecFP(getTransaction(), getFichePosteCourant().getIdFichePoste()));
			if (getFichePosteSecondaireCourant() != null) {
				getListeDelegationFP()
						.addAll(Delegation.listerDelegationAvecFP(getTransaction(), getFichePosteSecondaireCourant().getIdFichePoste()));
			}
		}
		if (getListeDelegationAFF() == null && getFichePosteCourant() != null && getFichePosteCourant().getIdFichePoste() != null) {
			setListeDelegationAFF(Delegation.listerDelegationAvecAFF(getTransaction(), getAffectationCourant().getIdAffectation()));
		}
		int indiceDel = 0;
		if (getListeDelegationFP() != null && getListeDelegationFP().size() != 0) {
			for (int i = 0; i < getListeDelegationFP().size(); i++) {
				Delegation aDel = (Delegation) getListeDelegationFP().get(i);
				if (aDel != null) {
					TypeDelegation typDel = TypeDelegation.chercherTypeDelegation(getTransaction(), aDel.getIdTypeDelegation());
					if (getListeDelegationAFF().contains(aDel))
						addZone(getNOM_CK_DELEGATION(indiceDel), getCHECKED_ON());
					addZone(getNOM_ST_LST_DELEGATION_TYPE(indiceDel), typDel.getLibTypeDelegation());
					addZone(getNOM_ST_LST_DELEGATION_COMMENT(indiceDel), aDel.getLibDelegation());
					indiceDel++;
				}
			}
		}
		if (getListeDelegationAFF() != null && getListeDelegationAFF().size() != 0) {
			for (int j = 0; j < getListeDelegationAFF().size(); j++) {
				Delegation aDel = (Delegation) getListeDelegationAFF().get(j);
				if (aDel != null && !getListeDelegationFP().contains(aDel)) {
					TypeDelegation typDel = TypeDelegation.chercherTypeDelegation(getTransaction(), aDel.getIdTypeDelegation());
					addZone(getNOM_ST_LST_DELEGATION_TYPE(indiceDel), typDel.getLibTypeDelegation());
					addZone(getNOM_ST_LST_DELEGATION_COMMENT(indiceDel), aDel.getLibDelegation());
					indiceDel++;
				}
			}
		}
		if (getListeDelegationAAjouter() != null && getListeDelegationAAjouter().size() != 0) {
			for (int k = 0; k < getListeDelegationAAjouter().size(); k++) {
				Delegation aDel = (Delegation) getListeDelegationAAjouter().get(k);
				if (aDel != null) {
					TypeDelegation typDel = TypeDelegation.chercherTypeDelegation(getTransaction(), aDel.getIdTypeDelegation());
					addZone(getNOM_ST_LST_DELEGATION_TYPE(indiceDel), typDel.getLibTypeDelegation());
					addZone(getNOM_ST_LST_DELEGATION_COMMENT(indiceDel), aDel.getLibDelegation());
					indiceDel++;
				}
			}
		}

		// R�gimes indemnitaires
		if (getListeRegimeFP() == null && getFichePosteCourant() != null && getFichePosteCourant().getIdFichePoste() != null) {
			setListeRegimeFP(RegimeIndemnitaire.listerRegimeIndemnitaireAvecFP(getTransaction(), getFichePosteCourant().getIdFichePoste()));
			if (getFichePosteSecondaireCourant() != null) {
				getListeRegimeFP().addAll(
						RegimeIndemnitaire.listerRegimeIndemnitaireAvecFP(getTransaction(), getFichePosteSecondaireCourant().getIdFichePoste()));
			}
		}
		if (getListeRegimeAFF() == null && getFichePosteCourant() != null && getFichePosteCourant().getIdFichePoste() != null) {
			setListeRegimeAFF(RegimeIndemnitaire.listerRegimeIndemnitaireAvecAFF(getTransaction(), getAffectationCourant().getIdAffectation()));
		}
		int indiceReg = 0;
		if (getListeRegimeFP() != null && getListeRegimeFP().size() != 0) {
			for (int i = 0; i < getListeRegimeFP().size(); i++) {
				RegimeIndemnitaire aReg = (RegimeIndemnitaire) getListeRegimeFP().get(i);
				if (aReg != null) {
					TypeRegIndemn typReg = TypeRegIndemn.chercherTypeRegIndemn(getTransaction(), aReg.getIdTypeRegIndemn());
					Rubrique rubr = aReg.getNumRubrique() == null ? null : Rubrique.chercherRubrique(getTransaction(), aReg.getNumRubrique());
					if (getListeRegimeAFF().contains(aReg))
						addZone(getNOM_CK_REGINDEMN(indiceReg), getCHECKED_ON());
					addZone(getNOM_ST_LST_REGINDEMN_TYPE(indiceReg), typReg.getLibTypeRegIndemn());
					addZone(getNOM_ST_LST_REGINDEMN_FORFAIT(indiceReg), aReg.getForfait());
					addZone(getNOM_ST_LST_REGINDEMN_NB_POINTS(indiceReg), aReg.getNombrePoints());
					if (rubr != null && rubr.getNumRubrique() != null)
						addZone(getNOM_ST_LST_REGINDEMN_RUBRIQUE(indiceReg), rubr.getLibRubrique());
					indiceReg++;
				}
			}
		}
		if (getListeRegimeAFF() != null && getListeRegimeAFF().size() != 0) {
			for (int j = 0; j < getListeRegimeAFF().size(); j++) {
				RegimeIndemnitaire aReg = (RegimeIndemnitaire) getListeRegimeAFF().get(j);
				if (aReg != null && !getListeRegimeFP().contains(aReg)) {
					TypeRegIndemn typReg = TypeRegIndemn.chercherTypeRegIndemn(getTransaction(), aReg.getIdTypeRegIndemn());
					Rubrique rubr = aReg.getNumRubrique() == null ? null : Rubrique.chercherRubrique(getTransaction(), aReg.getNumRubrique());
					addZone(getNOM_ST_LST_REGINDEMN_TYPE(indiceReg), typReg.getLibTypeRegIndemn());
					addZone(getNOM_ST_LST_REGINDEMN_FORFAIT(indiceReg), aReg.getForfait());
					addZone(getNOM_ST_LST_REGINDEMN_NB_POINTS(indiceReg), aReg.getNombrePoints());
					if (rubr != null && rubr.getNumRubrique() != null)
						addZone(getNOM_ST_LST_REGINDEMN_RUBRIQUE(indiceReg), rubr.getLibRubrique());
					indiceReg++;
				}
			}
		}
		if (getListeRegimeAAjouter() != null && getListeRegimeAAjouter().size() != 0) {
			for (int k = 0; k < getListeRegimeAAjouter().size(); k++) {
				RegimeIndemnitaire aReg = (RegimeIndemnitaire) getListeRegimeAAjouter().get(k);
				if (aReg != null) {
					TypeRegIndemn typReg = TypeRegIndemn.chercherTypeRegIndemn(getTransaction(), aReg.getIdTypeRegIndemn());
					Rubrique rubr = aReg.getNumRubrique() == null ? null : Rubrique.chercherRubrique(getTransaction(), aReg.getNumRubrique());
					addZone(getNOM_ST_LST_REGINDEMN_TYPE(indiceReg), typReg.getLibTypeRegIndemn());
					addZone(getNOM_ST_LST_REGINDEMN_FORFAIT(indiceReg), aReg.getForfait());
					addZone(getNOM_ST_LST_REGINDEMN_NB_POINTS(indiceReg), aReg.getNombrePoints());
					if (rubr != null && rubr.getNumRubrique() != null)
						addZone(getNOM_ST_LST_REGINDEMN_RUBRIQUE(indiceReg), rubr.getLibRubrique());
					indiceReg++;
				}
			}
		}
	}

	/**
	 * Initialise les listes d�roulantes de l'�cran. Date de cr�ation :
	 * (28/07/11)
	 * 
	 * @throws Exception
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste type avantage vide alors affectation
		if (getLB_TYPE_AVANTAGE() == LBVide) {
			ArrayList typeAvantage = TypeAvantage.listerTypeAvantage(getTransaction());
			setListeTypeAvantage(typeAvantage);

			int[] tailles = { 50 };
			String[] champs = { "libTypeAvantage" };
			setLB_TYPE_AVANTAGE(new FormateListe(tailles, typeAvantage, champs).getListeFormatee());
		}

		// Si liste nature avantage vide alors affectation
		if (getLB_NATURE_AVANTAGE() == LBVide) {
			ArrayList natureAvantage = NatureAvantage.listerNatureAvantage(getTransaction());
			NatureAvantage natAvVide = new NatureAvantage();
			natureAvantage.add(0, natAvVide);
			setListeNatureAvantage(natureAvantage);

			int[] tailles = { 50 };
			String[] champs = { "libNatureAvantage" };
			setLB_NATURE_AVANTAGE(new FormateListe(tailles, natureAvantage, champs).getListeFormatee());
		}

		// Si liste rubrique vide alors affectation
		if (getLB_RUBRIQUE_AVANTAGE() == LBVide || getLB_RUBRIQUE_REGIME() == LBVide) {
			ArrayList rubrique = Rubrique.listerRubrique7000(getTransaction());
			setListeRubrique(rubrique);

			if (getListeRubrique() != null && getListeRubrique().size() != 0) {
				int taillesRub[] = { 68 };
				FormateListe aFormatRub = new FormateListe(taillesRub);
				for (ListIterator list = getListeRubrique().listIterator(); list.hasNext();) {
					Rubrique aRub = (Rubrique) list.next();
					if (aRub != null) {
						String ligne[] = { aRub.getNumRubrique() + " - " + aRub.getLibRubrique() };
						aFormatRub.ajouteLigne(ligne);
					}
				}
				setLB_RUBRIQUE_AVANTAGE(aFormatRub.getListeFormatee(true));
				setLB_RUBRIQUE_REGIME(aFormatRub.getListeFormatee(true));
			} else {
				setLB_RUBRIQUE_AVANTAGE(null);
				setLB_RUBRIQUE_REGIME(null);
			}
		}

		// Si liste type d�l�gation vide alors affectation
		if (getLB_TYPE_DELEGATION() == LBVide) {
			ArrayList typeDelegation = TypeDelegation.listerTypeDelegation(getTransaction());
			setListeTypeDelegation(typeDelegation);

			int[] tailles = { 30 };
			String[] champs = { "libTypeDelegation" };
			setLB_TYPE_DELEGATION(new FormateListe(tailles, typeDelegation, champs).getListeFormatee());
		}

		// Si liste type r�gime vide alors affectation
		if (getLB_TYPE_REGIME() == LBVide) {
			ArrayList typeRegime = TypeRegIndemn.listerTypeRegIndemn(getTransaction());
			setListeTypeRegIndemn(typeRegime);
			int[] tailles = { 20 };
			String[] champs = { "libTypeRegIndemn" };
			setLB_TYPE_REGIME(new FormateListe(tailles, typeRegime, champs).getListeFormatee());
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_AVANTAGE Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_AJOUTER_AVANTAGE() {
		return "NOM_PB_AJOUTER_AVANTAGE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_AJOUTER_AVANTAGE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), ACTION_AJOUTER);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_DELEGATION Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_AJOUTER_DELEGATION() {
		return "NOM_PB_AJOUTER_DELEGATION";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_AJOUTER_DELEGATION(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), ACTION_AJOUTER);
		addZone(getNOM_ST_SPECIFICITE(), SPEC_DELEGATION);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_REGIME Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_AJOUTER_REGIME() {
		return "NOM_PB_AJOUTER_REGIME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_AJOUTER_REGIME(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), ACTION_AJOUTER);
		addZone(getNOM_ST_SPECIFICITE(), SPEC_REG_INDEMN);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de cr�ation :
	 * (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CHANGER_SPECIFICITE Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_CHANGER_SPECIFICITE() {
		return "NOM_PB_CHANGER_SPECIFICITE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_CHANGER_SPECIFICITE(HttpServletRequest request) throws Exception {

		if (getVAL_ST_SPECIFICITE().equals(SPEC_AVANTAGE_NATURE) && getListeAvantageFP() != null) {
			for (int i = 0; i < getListeAvantageFP().size(); i++) {
				if (getVAL_CK_AVANTAGE(i).equals(getCHECKED_ON()) && !getListeAvantageAFF().contains(getListeAvantageFP().get(i)))
					getListeAvantageAFF().add(getListeAvantageFP().get(i));
				if (getVAL_CK_AVANTAGE(i).equals(getCHECKED_OFF()) && getListeAvantageAFF().contains(getListeAvantageFP().get(i)))
					getListeAvantageAFF().remove(getListeAvantageFP().get(i));
			}
		}
		if (getVAL_ST_SPECIFICITE().equals(SPEC_DELEGATION) && getListeDelegationFP() != null) {
			for (int i = 0; i < getListeDelegationFP().size(); i++) {
				if (getVAL_CK_DELEGATION(i).equals(getCHECKED_ON()) && !getListeDelegationAFF().contains(getListeDelegationFP().get(i)))
					getListeDelegationAFF().add(getListeDelegationFP().get(i));
				if (getVAL_CK_DELEGATION(i).equals(getCHECKED_OFF()) && getListeDelegationAFF().contains(getListeDelegationFP().get(i)))
					getListeDelegationAFF().remove(getListeDelegationFP().get(i));
			}
		}
		if (getVAL_ST_SPECIFICITE().equals(SPEC_REG_INDEMN) && getListeRegimeFP() != null) {
			for (int i = 0; i < getListeRegimeFP().size(); i++) {
				if (getVAL_CK_REGINDEMN(i).equals(getCHECKED_ON()) && !getListeRegimeAFF().contains(getListeRegimeFP().get(i)))
					getListeRegimeAFF().add(getListeRegimeFP().get(i));
				if (getVAL_CK_REGINDEMN(i).equals(getCHECKED_OFF()) && getListeRegimeAFF().contains(getListeRegimeFP().get(i)))
					getListeRegimeAFF().remove(getListeRegimeFP().get(i));
			}
		}

		if (getVAL_RG_SPECIFICITE().equals(getNOM_RB_SPECIFICITE_AN()))
			addZone(getNOM_ST_SPECIFICITE(), SPEC_AVANTAGE_NATURE);
		else if (getVAL_RG_SPECIFICITE().equals(getNOM_RB_SPECIFICITE_D()))
			addZone(getNOM_ST_SPECIFICITE(), SPEC_DELEGATION);
		else if (getVAL_RG_SPECIFICITE().equals(getNOM_RB_SPECIFICITE_RI()))
			addZone(getNOM_ST_SPECIFICITE(), SPEC_REG_INDEMN);

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_AVANTAGE Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_AVANTAGE(int i) {
		return "NOM_PB_SUPPRIMER_AVANTAGE" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_AVANTAGE(HttpServletRequest request, int indiceEltASupprimer) throws Exception {
		// Calcul du nombre d'Avantages en nature s�lectionn�s par l'utilisateur
		// parmi ceux issus de la fiche de poste
		int nbAvNatFPSelected = 0;
		for (int i = 0; i < getListeAvantageAFF().size(); i++) {
			if (getListeAvantageFP().contains(getListeAvantageAFF().get(i)))
				nbAvNatFPSelected++;
		}
		// Si la sp�cificit� � supprimer est d�j� en base
		if (indiceEltASupprimer - getListeAvantageFP().size() + nbAvNatFPSelected < getListeAvantageAFF().size()) {
			AvantageNature avNatASupprimer = (AvantageNature) getListeAvantageAFF().get(
					indiceEltASupprimer - getListeAvantageFP().size() + nbAvNatFPSelected);
			if (avNatASupprimer != null) {
				getListeAvantageAFF().remove(avNatASupprimer);
				getListeAvantageASupprimer().add(avNatASupprimer);
			}

		}
		// Si la sp�cificit� � supprimer n'est pas encore en base mais vient
		// d'�tre ajout�e par l'utilisateur
		else {
			AvantageNature avNatASupprimer = (AvantageNature) getListeAvantageAAjouter().get(
					indiceEltASupprimer - getListeAvantageFP().size() - getListeAvantageAFF().size() + nbAvNatFPSelected);
			if (avNatASupprimer != null) {
				getListeAvantageAAjouter().remove(avNatASupprimer);
			}
		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_DELEGATION Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_DELEGATION(int i) {
		return "NOM_PB_SUPPRIMER_DELEGATION" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_DELEGATION(HttpServletRequest request, int indiceEltASupprimer) throws Exception {
		// Calcul du nombre de Delegation s�lectionn�es par l'utilisateur parmi
		// ceux issus de la fiche de poste
		int nbDelFPSelected = 0;
		for (int i = 0; i < getListeDelegationAFF().size(); i++) {
			if (getListeDelegationFP().contains(getListeDelegationAFF().get(i)))
				nbDelFPSelected++;
		}
		// Si la sp�cificit� � supprimer est d�j� en base
		if (indiceEltASupprimer - getListeDelegationFP().size() + nbDelFPSelected < getListeDelegationAFF().size()) {
			Delegation delASupprimer = (Delegation) getListeDelegationAFF()
					.get(indiceEltASupprimer - getListeDelegationFP().size() + nbDelFPSelected);
			if (delASupprimer != null) {
				getListeDelegationAFF().remove(delASupprimer);
				getListeDelegationASupprimer().add(delASupprimer);
			}

		}
		// Si la sp�cificit� � supprimer n'est pas encore en base mais vient
		// d'�tre ajout�e par l'utilisateur
		else {
			Delegation delASupprimer = (Delegation) getListeDelegationAAjouter().get(
					indiceEltASupprimer - getListeDelegationFP().size() - getListeDelegationAFF().size() + nbDelFPSelected);
			if (delASupprimer != null) {
				getListeDelegationAAjouter().remove(delASupprimer);
			}
		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_REGIME Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_REGIME(int i) {
		return "NOM_PB_SUPPRIMER_REGIME" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_SUPPRIMER_REGIME(HttpServletRequest request, int indiceEltASupprimer) throws Exception {
		// Calcul du nombre de RegimeIndemnitaire s�lectionn�s par l'utilisateur
		// parmi ceux issus de la fiche de poste
		int nbRegIndemnFPSelected = 0;
		for (int i = 0; i < getListeRegimeAFF().size(); i++) {
			if (getListeRegimeFP().contains(getListeRegimeAFF().get(i)))
				nbRegIndemnFPSelected++;
		}
		// Si la sp�cificit� � supprimer est d�j� en base
		if (indiceEltASupprimer - getListeRegimeFP().size() + nbRegIndemnFPSelected < getListeRegimeAFF().size()) {
			RegimeIndemnitaire regIndemnASupprimer = (RegimeIndemnitaire) getListeRegimeAFF().get(
					indiceEltASupprimer - getListeRegimeFP().size() + nbRegIndemnFPSelected);
			if (regIndemnASupprimer != null) {
				getListeRegimeAFF().remove(regIndemnASupprimer);
				getListeRegimeASupprimer().add(regIndemnASupprimer);
			}

		}
		// Si la sp�cificit� � supprimer n'est pas encore en base mais vient
		// d'�tre ajout�e par l'utilisateur
		else {
			RegimeIndemnitaire regIndemnASupprimer = (RegimeIndemnitaire) getListeRegimeAAjouter().get(
					indiceEltASupprimer - getListeRegimeFP().size() - getListeRegimeAFF().size() + nbRegIndemnFPSelected);
			if (regIndemnASupprimer != null) {
				getListeRegimeAAjouter().remove(regIndemnASupprimer);
			}
		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de cr�ation :
	 * (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * TODO A completer quand la MOA aura fourni plus d'informations. Cr��e une
	 * ligne de prime ou de charge selon le type de Rubrique de la sp�cificit�.
	 * 
	 * @param obj
	 *            Une sp�cificit� (AvantageNature, Delegation ou
	 *            RegimeIndemnitaire)
	 * @throws Exception
	 */
	/*
	 * private void ajouterEltSalaire(Object obj, HttpServletRequest request)
	 * throws Exception { UserAppli user = (UserAppli)
	 * VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
	 * 
	 * if (obj.getClass().equals(AvantageNature.class)) { AvantageNature avNat =
	 * (AvantageNature) obj; Rubrique rubr =
	 * Rubrique.chercherRubrique(getTransaction(), avNat.getNumRubrique()); if
	 * (rubr != null) { if
	 * (rubr.getTypeRubrique().equals(EnumTypeRubrique.CHARGE.getValue())) {
	 * Charge charge = new Charge(getAgentCourant().getNoMatricule(),
	 * rubr.getNumRubrique(), Services.dateDuJour());
	 * charge.creerCharge(getTransaction(), user); ChargeAgent chargeAgent = new
	 * ChargeAgent(getAgentCourant().getIdAgent(),
	 * getAgentCourant().getNoMatricule(), rubr.getNumRubrique(),
	 * Services.dateDuJour()); chargeAgent.creerChargeAgent(getTransaction()); }
	 * else if
	 * (rubr.getTypeRubrique().equals(EnumTypeRubrique.PRIME.getValue())) {
	 * Prime prime = new Prime(getAgentCourant().getNoMatricule(),
	 * rubr.getNumRubrique(), Services.dateDuJour());
	 * prime.creerPrime(getTransaction(), user); PrimeAgent primeAgent = new
	 * PrimeAgent(getAgentCourant().getIdAgent(),
	 * getAgentCourant().getNoMatricule(), rubr.getNumRubrique(),
	 * Services.dateDuJour()); primeAgent.creerPrimeAgent(getTransaction()); } }
	 * } else if (obj.getClass().equals(RegimeIndemnitaire.class)) {
	 * RegimeIndemnitaire regIndemn = (RegimeIndemnitaire) obj; Rubrique rubr =
	 * Rubrique.chercherRubrique(getTransaction(), regIndemn.getNumRubrique());
	 * if (rubr != null) { if
	 * (rubr.getTypeRubrique().equals(EnumTypeRubrique.CHARGE.getValue())) {
	 * Charge charge = new Charge(getAgentCourant().getNoMatricule(),
	 * rubr.getNumRubrique(), Services.dateDuJour());
	 * charge.creerCharge(getTransaction(), user); ChargeAgent chargeAgent = new
	 * ChargeAgent(getAgentCourant().getIdAgent(),
	 * getAgentCourant().getNoMatricule(), rubr.getNumRubrique(),
	 * Services.dateDuJour()); chargeAgent.creerChargeAgent(getTransaction()); }
	 * else if
	 * (rubr.getTypeRubrique().equals(EnumTypeRubrique.PRIME.getValue())) {
	 * Prime prime = new Prime(getAgentCourant().getNoMatricule(),
	 * rubr.getNumRubrique(), Services.dateDuJour());
	 * prime.creerPrime(getTransaction(), user); PrimeAgent primeAgent = new
	 * PrimeAgent(getAgentCourant().getIdAgent(),
	 * getAgentCourant().getNoMatricule(), rubr.getNumRubrique(),
	 * Services.dateDuJour()); primeAgent.creerPrimeAgent(getTransaction()); } }
	 * } }
	 */

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {

		// Sauvegarde des nouveaux avantages nature et suppression des anciens
		for (int i = 0; i < getListeAvantageFP().size(); i++) {
			if (getVAL_CK_AVANTAGE(i).equals(getCHECKED_ON()) && !getListeAvantageAFF().contains(getListeAvantageFP().get(i))) {
				AvantageNature avNat = (AvantageNature) getListeAvantageFP().get(i);
				AvantageNatureAFF avNatAFF = new AvantageNatureAFF(getAffectationCourant().getIdAffectation(), avNat.getIdAvantage());
				avNatAFF.creerAvantageNatureAFF(getTransaction());
				if (getTransaction().isErreur()) {
					getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins un avantage en nature n'a pu �tre cr��.");
					return false;
				}
				getListeAvantageAFF().add(getListeAvantageFP().get(i));
			}
			if (getVAL_CK_AVANTAGE(i).equals(getCHECKED_OFF()) && getListeAvantageAFF().contains(getListeAvantageFP().get(i))) {
				AvantageNature avNat = (AvantageNature) getListeAvantageFP().get(i);
				AvantageNatureAFF avNatAFF = AvantageNatureAFF.chercherAvantageNatureAFF(getTransaction(),
						getAffectationCourant().getIdAffectation(), avNat.getIdAvantage());
				avNatAFF.supprimerAvantageNatureAFF(getTransaction());
				if (getTransaction().isErreur()) {
					getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins un avantage en nature n'a pu �tre supprim�.");
					return false;
				}
				getListeAvantageAFF().remove(getListeAvantageFP().get(i));
			}
		}
		for (int i = 0; i < getListeAvantageAAjouter().size(); i++) {
			AvantageNature avNat = (AvantageNature) getListeAvantageAAjouter().get(i);
			avNat.creerAvantageNature(getTransaction());
			AvantageNatureAFF avNatAFF = new AvantageNatureAFF(getAffectationCourant().getIdAffectation(), avNat.getIdAvantage());
			avNatAFF.creerAvantageNatureAFF(getTransaction());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins un avantage en nature n'a pu �tre cr��.");
				return false;
			}
		}
		for (int i = 0; i < getListeAvantageASupprimer().size(); i++) {
			AvantageNature avNat = (AvantageNature) getListeAvantageASupprimer().get(i);
			AvantageNatureAFF avNatAFF = AvantageNatureAFF.chercherAvantageNatureAFF(getTransaction(), getAffectationCourant().getIdAffectation(),
					avNat.getIdAvantage());
			avNatAFF.supprimerAvantageNatureAFF(getTransaction());
			avNat.supprimerAvantageNature(getTransaction());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins un avantage en nature n'a pu �tre supprim�.");
				return false;
			}
		}

		// Sauvegarde des nouvelles Delegation et suppression des anciennes
		for (int i = 0; i < getListeDelegationFP().size(); i++) {
			if (getVAL_CK_DELEGATION(i).equals(getCHECKED_ON()) && !getListeDelegationAFF().contains(getListeDelegationFP().get(i))) {
				Delegation del = (Delegation) getListeDelegationFP().get(i);
				DelegationAFF delAFF = new DelegationAFF(getAffectationCourant().getIdAffectation(), del.getIdDelegation());
				delAFF.creerDelegationAFF(getTransaction());
				if (getTransaction().isErreur()) {
					getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins une Delegation n'a pu �tre cr��e.");
					return false;
				}
				getListeDelegationAFF().add(getListeDelegationFP().get(i));
			}
			if (getVAL_CK_DELEGATION(i).equals(getCHECKED_OFF()) && getListeDelegationAFF().contains(getListeDelegationFP().get(i))) {
				Delegation del = (Delegation) getListeDelegationFP().get(i);
				DelegationAFF delAFF = DelegationAFF.chercherDelegationAFF(getTransaction(), getAffectationCourant().getIdAffectation(),
						del.getIdDelegation());
				delAFF.supprimerDelegationAFF(getTransaction());
				if (getTransaction().isErreur()) {
					getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins une Delegation n'a pu �tre supprim�e.");
					return false;
				}
				getListeDelegationAFF().remove(getListeDelegationFP().get(i));
			}
		}
		for (int i = 0; i < getListeDelegationAAjouter().size(); i++) {
			Delegation deleg = (Delegation) getListeDelegationAAjouter().get(i);
			deleg.creerDelegation(getTransaction());
			DelegationAFF delAFF = new DelegationAFF(getAffectationCourant().getIdAffectation(), deleg.getIdDelegation());
			delAFF.creerDelegationAFF(getTransaction());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins une Delegation n'a pu �tre cr��e.");
				return false;
			}
		}
		for (int i = 0; i < getListeDelegationASupprimer().size(); i++) {
			Delegation deleg = (Delegation) getListeDelegationASupprimer().get(i);
			DelegationAFF delAFF = DelegationAFF.chercherDelegationAFF(getTransaction(), getAffectationCourant().getIdAffectation(),
					deleg.getIdDelegation());
			delAFF.supprimerDelegationAFF(getTransaction());
			deleg.supprimerDelegation(getTransaction());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins une Delegation n'a pu �tre supprim�e.");
				return false;
			}
		}

		// Sauvegarde des nouveaux RegimeIndemnitaire et suppression des anciens
		for (int i = 0; i < getListeRegimeFP().size(); i++) {
			if (getVAL_CK_REGINDEMN(i).equals(getCHECKED_ON()) && !getListeRegimeAFF().contains(getListeRegimeFP().get(i))) {
				RegimeIndemnitaire regIndemn = (RegimeIndemnitaire) getListeRegimeFP().get(i);
				RegIndemnAFF riAFF = new RegIndemnAFF(getAffectationCourant().getIdAffectation(), regIndemn.getIdRegIndemn());
				riAFF.creerRegIndemnAFF(getTransaction());
				if (getTransaction().isErreur()) {
					getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins un RegimeIndemnitaire n'a pu �tre cr��.");
					return false;
				}
				getListeRegimeAFF().add(getListeRegimeFP().get(i));
			}
			if (getVAL_CK_REGINDEMN(i).equals(getCHECKED_OFF()) && getListeRegimeAFF().contains(getListeRegimeFP().get(i))) {
				RegimeIndemnitaire regIndemn = (RegimeIndemnitaire) getListeRegimeFP().get(i);
				RegIndemnAFF riAFF = RegIndemnAFF.chercherRegIndemnAFF(getTransaction(), getAffectationCourant().getIdAffectation(),
						regIndemn.getIdRegIndemn());
				riAFF.supprimerRegIndemnAFF(getTransaction());
				if (getTransaction().isErreur()) {
					getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins un RegimeIndemnitaire n'a pu �tre supprim�.");
					return false;
				}
				getListeRegimeAFF().remove(getListeRegimeFP().get(i));
			}
		}
		for (int i = 0; i < getListeRegimeAAjouter().size(); i++) {
			RegimeIndemnitaire regIndemn = (RegimeIndemnitaire) getListeRegimeAAjouter().get(i);
			regIndemn.creerRegimeIndemnitaire(getTransaction());
			RegIndemnAFF riAFF = new RegIndemnAFF(getAffectationCourant().getIdAffectation(), regIndemn.getIdRegIndemn());
			riAFF.creerRegIndemnAFF(getTransaction());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins un RegimeIndemnitaire n'a pu �tre cr��.");
				return false;
			}
		}
		for (int i = 0; i < getListeRegimeASupprimer().size(); i++) {
			RegimeIndemnitaire regIndemn = (RegimeIndemnitaire) getListeRegimeASupprimer().get(i);
			RegIndemnAFF riAFF = RegIndemnAFF.chercherRegIndemnAFF(getTransaction(), getAffectationCourant().getIdAffectation(),
					regIndemn.getIdRegIndemn());
			riAFF.supprimerRegIndemnAFF(getTransaction());
			regIndemn.supprimerRegimeIndemnitaire(getTransaction());
			if (getTransaction().isErreur()) {
				getTransaction().declarerErreur(getTransaction().traiterErreur() + " Au moins un RegimeIndemnitaire n'a pu �tre supprim�.");
				return false;
			}
		}

		// COMMIT
		commitTransaction();

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_AJOUT Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_PB_VALIDER_AJOUT() {
		return "NOM_PB_VALIDER_AJOUT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_VALIDER_AJOUT(HttpServletRequest request) throws Exception {
		if (getVAL_ST_SPECIFICITE().equals(SPEC_AVANTAGE_NATURE)) {
			// Contr�le des champs
			if (!performControlerSaisieAvNat(request))
				return false;

			// Alimentation de l'objet
			AvantageNature avNat = new AvantageNature();

			avNat.setMontant(getVAL_EF_MONTANT_AVANTAGE());

			int indiceTypeAvantage = (Services.estNumerique(getVAL_LB_TYPE_AVANTAGE_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_AVANTAGE_SELECT())
					: -1);
			avNat.setIdTypeAvantage(((TypeAvantage) getListeTypeAvantage().get(indiceTypeAvantage)).getIdTypeAvantage());
			int indiceNatAvantage = (Services.estNumerique(getVAL_LB_NATURE_AVANTAGE_SELECT()) ? Integer.parseInt(getVAL_LB_NATURE_AVANTAGE_SELECT())
					: -1);
			avNat.setIdNatureAvantage(((NatureAvantage) getListeNatureAvantage().get(indiceNatAvantage)).getIdNatureAvantage());
			int indiceRubAvantage = (Services.estNumerique(getVAL_LB_RUBRIQUE_AVANTAGE_SELECT()) ? Integer
					.parseInt(getVAL_LB_RUBRIQUE_AVANTAGE_SELECT()) : -1);
			if (indiceRubAvantage > 0)
				avNat.setNumRubrique(((Rubrique) getListeRubrique().get(indiceRubAvantage - 1)).getNumRubrique());

			if (getListeAvantageAFF() == null)
				setListeAvantageAFF(new ArrayList());

			if (!getListeAvantageAFF().contains(avNat) && !getListeAvantageFP().contains(avNat) && !getListeAvantageAAjouter().contains(avNat)) {
				if (getListeAvantageASupprimer().contains(avNat)) {
					getListeAvantageASupprimer().remove(avNat);
					getListeAvantageAFF().add(avNat);
				} else {
					getListeAvantageAAjouter().add(avNat);
				}
			}
			// R�initialisation des champs de saisie
			viderAvantageNature();
		} else if (getVAL_ST_SPECIFICITE().equals(SPEC_DELEGATION)) {
			// Contr�le des champs
			if (!performControlerSaisieDel(request))
				return false;

			// Alimentation de l'objet
			Delegation deleg = new Delegation();

			deleg.setLibDelegation(getVAL_EF_COMMENT_DELEGATION());

			int indiceTypeDelegation = (Services.estNumerique(getVAL_LB_TYPE_DELEGATION_SELECT()) ? Integer
					.parseInt(getVAL_LB_TYPE_DELEGATION_SELECT()) : -1);
			deleg.setIdTypeDelegation(((TypeDelegation) getListeTypeDelegation().get(indiceTypeDelegation)).getIdTypeDelegation());

			if (getListeDelegationAFF() == null)
				setListeDelegationAFF(new ArrayList());

			if (!getListeDelegationAFF().contains(deleg) && !getListeDelegationFP().contains(deleg) && !getListeDelegationAAjouter().contains(deleg)) {
				if (getListeDelegationASupprimer().contains(deleg)) {
					getListeDelegationASupprimer().remove(deleg);
					getListeDelegationAFF().add(deleg);
				} else {
					getListeDelegationAAjouter().add(deleg);
				}
			}
			// R�initialisation des champs de saisie
			viderDelegation();
		} else if (getVAL_ST_SPECIFICITE().equals(SPEC_REG_INDEMN)) {
			// Contr�le des champs
			if (!performControlerSaisieRegIndemn(request))
				return false;

			// Alimentation de l'objet
			RegimeIndemnitaire regIndemn = new RegimeIndemnitaire();

			regIndemn.setForfait(getVAL_EF_FORFAIT_REGIME());
			regIndemn.setNombrePoints(getVAL_EF_NB_POINTS_REGIME());

			int indiceRegIndemn = (Services.estNumerique(getVAL_LB_TYPE_REGIME_SELECT()) ? Integer.parseInt(getVAL_LB_TYPE_REGIME_SELECT()) : -1);
			regIndemn.setIdTypeRegIndemn(((TypeRegIndemn) getListeTypeRegIndemn().get(indiceRegIndemn)).getIdTypeRegIndemn());
			int indiceRub = (Services.estNumerique(getVAL_LB_RUBRIQUE_REGIME_SELECT()) ? Integer.parseInt(getVAL_LB_RUBRIQUE_REGIME_SELECT()) : -1);
			if (indiceRub > 0)
				regIndemn.setNumRubrique(((Rubrique) getListeRubrique().get(indiceRub - 1)).getNumRubrique());

			if (getListeRegimeAFF() == null)
				setListeRegimeAFF(new ArrayList());

			if (!getListeRegimeAFF().contains(regIndemn) && !getListeRegimeFP().contains(regIndemn) && !getListeRegimeAAjouter().contains(regIndemn)) {
				if (getListeRegimeASupprimer().contains(regIndemn)) {
					getListeRegimeASupprimer().remove(regIndemn);
					getListeRegimeAFF().add(regIndemn);
				} else {
					getListeRegimeAAjouter().add(regIndemn);
				}
			}

			// R�initialisation des champs de saisie
			viderRegIndemn();
		}

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACTION Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SPECIFICITE Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_ST_SPECIFICITE() {
		return "NOM_ST_SPECIFICITE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_SPECIFICITE
	 * Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_ST_SPECIFICITE() {
		return getZone(getNOM_ST_SPECIFICITE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_COMMENT_DELEGATION
	 * Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_EF_COMMENT_DELEGATION() {
		return "NOM_EF_COMMENT_DELEGATION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_COMMENT_DELEGATION Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_EF_COMMENT_DELEGATION() {
		return getZone(getNOM_EF_COMMENT_DELEGATION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_FORFAIT_REGIME Date
	 * de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_EF_FORFAIT_REGIME() {
		return "NOM_EF_FORFAIT_REGIME";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_FORFAIT_REGIME Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_EF_FORFAIT_REGIME() {
		return getZone(getNOM_EF_FORFAIT_REGIME());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_MONTANT_AVANTAGE
	 * Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_EF_MONTANT_AVANTAGE() {
		return "NOM_EF_MONTANT_AVANTAGE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_MONTANT_AVANTAGE Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_EF_MONTANT_AVANTAGE() {
		return getZone(getNOM_EF_MONTANT_AVANTAGE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NB_POINTS_REGIME
	 * Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_EF_NB_POINTS_REGIME() {
		return "NOM_EF_NB_POINTS_REGIME";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_NB_POINTS_REGIME Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_EF_NB_POINTS_REGIME() {
		return getZone(getNOM_EF_NB_POINTS_REGIME());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_NATURE_AVANTAGE Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_NATURE_AVANTAGE() {
		if (LB_NATURE_AVANTAGE == null)
			LB_NATURE_AVANTAGE = initialiseLazyLB();
		return LB_NATURE_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_NATURE_AVANTAGE Date de cr�ation : (16/08/11
	 * 15:48:02)
	 * 
	 */
	private void setLB_NATURE_AVANTAGE(String[] newLB_NATURE_AVANTAGE) {
		LB_NATURE_AVANTAGE = newLB_NATURE_AVANTAGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_NATURE_AVANTAGE Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_NATURE_AVANTAGE() {
		return "NOM_LB_NATURE_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_NATURE_AVANTAGE_SELECT Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_NATURE_AVANTAGE_SELECT() {
		return "NOM_LB_NATURE_AVANTAGE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_NATURE_AVANTAGE Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_NATURE_AVANTAGE() {
		return getLB_NATURE_AVANTAGE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_NATURE_AVANTAGE Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_LB_NATURE_AVANTAGE_SELECT() {
		return getZone(getNOM_LB_NATURE_AVANTAGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_RUBRIQUE_AVANTAGE Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_RUBRIQUE_AVANTAGE() {
		if (LB_RUBRIQUE_AVANTAGE == null)
			LB_RUBRIQUE_AVANTAGE = initialiseLazyLB();
		return LB_RUBRIQUE_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_RUBRIQUE_AVANTAGE Date de cr�ation : (16/08/11
	 * 15:48:02)
	 * 
	 */
	private void setLB_RUBRIQUE_AVANTAGE(String[] newLB_RUBRIQUE_AVANTAGE) {
		LB_RUBRIQUE_AVANTAGE = newLB_RUBRIQUE_AVANTAGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_RUBRIQUE_AVANTAGE Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_AVANTAGE() {
		return "NOM_LB_RUBRIQUE_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_RUBRIQUE_AVANTAGE_SELECT Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_AVANTAGE_SELECT() {
		return "NOM_LB_RUBRIQUE_AVANTAGE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_RUBRIQUE_AVANTAGE Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_RUBRIQUE_AVANTAGE() {
		return getLB_RUBRIQUE_AVANTAGE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_RUBRIQUE_AVANTAGE Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_LB_RUBRIQUE_AVANTAGE_SELECT() {
		return getZone(getNOM_LB_RUBRIQUE_AVANTAGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_RUBRIQUE_REGIME Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_RUBRIQUE_REGIME() {
		if (LB_RUBRIQUE_REGIME == null)
			LB_RUBRIQUE_REGIME = initialiseLazyLB();
		return LB_RUBRIQUE_REGIME;
	}

	/**
	 * Setter de la liste: LB_RUBRIQUE_REGIME Date de cr�ation : (16/08/11
	 * 15:48:02)
	 * 
	 */
	private void setLB_RUBRIQUE_REGIME(String[] newLB_RUBRIQUE_REGIME) {
		LB_RUBRIQUE_REGIME = newLB_RUBRIQUE_REGIME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_RUBRIQUE_REGIME Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_REGIME() {
		return "NOM_LB_RUBRIQUE_REGIME";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_RUBRIQUE_REGIME_SELECT Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_RUBRIQUE_REGIME_SELECT() {
		return "NOM_LB_RUBRIQUE_REGIME_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_RUBRIQUE_REGIME Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_RUBRIQUE_REGIME() {
		return getLB_RUBRIQUE_REGIME();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_RUBRIQUE_REGIME Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_LB_RUBRIQUE_REGIME_SELECT() {
		return getZone(getNOM_LB_RUBRIQUE_REGIME_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_AVANTAGE Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_TYPE_AVANTAGE() {
		if (LB_TYPE_AVANTAGE == null)
			LB_TYPE_AVANTAGE = initialiseLazyLB();
		return LB_TYPE_AVANTAGE;
	}

	/**
	 * Setter de la liste: LB_TYPE_AVANTAGE Date de cr�ation : (16/08/11
	 * 15:48:02)
	 * 
	 */
	private void setLB_TYPE_AVANTAGE(String[] newLB_TYPE_AVANTAGE) {
		LB_TYPE_AVANTAGE = newLB_TYPE_AVANTAGE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_AVANTAGE Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_TYPE_AVANTAGE() {
		return "NOM_LB_TYPE_AVANTAGE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_TYPE_AVANTAGE_SELECT Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_TYPE_AVANTAGE_SELECT() {
		return "NOM_LB_TYPE_AVANTAGE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_TYPE_AVANTAGE Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_TYPE_AVANTAGE() {
		return getLB_TYPE_AVANTAGE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_TYPE_AVANTAGE Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_LB_TYPE_AVANTAGE_SELECT() {
		return getZone(getNOM_LB_TYPE_AVANTAGE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_DELEGATION Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_TYPE_DELEGATION() {
		if (LB_TYPE_DELEGATION == null)
			LB_TYPE_DELEGATION = initialiseLazyLB();
		return LB_TYPE_DELEGATION;
	}

	/**
	 * Setter de la liste: LB_TYPE_DELEGATION Date de cr�ation : (16/08/11
	 * 15:48:02)
	 * 
	 */
	private void setLB_TYPE_DELEGATION(String[] newLB_TYPE_DELEGATION) {
		LB_TYPE_DELEGATION = newLB_TYPE_DELEGATION;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_DELEGATION Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_TYPE_DELEGATION() {
		return "NOM_LB_TYPE_DELEGATION";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_TYPE_DELEGATION_SELECT Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_TYPE_DELEGATION_SELECT() {
		return "NOM_LB_TYPE_DELEGATION_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_TYPE_DELEGATION Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_TYPE_DELEGATION() {
		return getLB_TYPE_DELEGATION();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_TYPE_DELEGATION Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_LB_TYPE_DELEGATION_SELECT() {
		return getZone(getNOM_LB_TYPE_DELEGATION_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TYPE_REGIME Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	private String[] getLB_TYPE_REGIME() {
		if (LB_TYPE_REGIME == null)
			LB_TYPE_REGIME = initialiseLazyLB();
		return LB_TYPE_REGIME;
	}

	/**
	 * Setter de la liste: LB_TYPE_REGIME Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	private void setLB_TYPE_REGIME(String[] newLB_TYPE_REGIME) {
		LB_TYPE_REGIME = newLB_TYPE_REGIME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE_REGIME Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_TYPE_REGIME() {
		return "NOM_LB_TYPE_REGIME";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_TYPE_REGIME_SELECT Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_LB_TYPE_REGIME_SELECT() {
		return "NOM_LB_TYPE_REGIME_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_TYPE_REGIME Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String[] getVAL_LB_TYPE_REGIME() {
		return getLB_TYPE_REGIME();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_TYPE_REGIME Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_LB_TYPE_REGIME_SELECT() {
		return getZone(getNOM_LB_TYPE_REGIME_SELECT());
	}

	/**
	 * Retourne le nom du groupe de radio boutons coch� pour la JSP :
	 * RG_SPECIFICITE Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_RG_SPECIFICITE() {
		return "NOM_RG_SPECIFICITE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coch� dans la JSP :
	 * RG_SPECIFICITE Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getVAL_RG_SPECIFICITE() {
		return getZone(getNOM_RG_SPECIFICITE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SPECIFICITE_AN Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_RB_SPECIFICITE_AN() {
		return "NOM_RB_SPECIFICITE_AN";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SPECIFICITE_D Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_RB_SPECIFICITE_D() {
		return "NOM_RB_SPECIFICITE_D";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SPECIFICITE_RI Date de
	 * cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public String getNOM_RB_SPECIFICITE_RI() {
		return "NOM_RB_SPECIFICITE_RI";
	}

	/**
	 * @return Renvoie focus.
	 */
	public String getFocus() {
		if (focus == null) {
			focus = getDefaultFocus();
		}
		return focus;
	}

	public String getDefaultFocus() {
		return getNOM_PB_VALIDER();
	}

	/**
	 * @param focus
	 *            focus � d�finir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne la liste des TypeDelegation.
	 * 
	 * @return listeTypeDelegation
	 */
	private ArrayList getListeTypeDelegation() {
		return listeTypeDelegation;
	}

	/**
	 * Met � jour la liste des TypeDelegation.
	 * 
	 * @param listeTypeDelegation
	 */
	private void setListeTypeDelegation(ArrayList listeTypeDelegation) {
		this.listeTypeDelegation = listeTypeDelegation;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire.
	 * 
	 * @return listeTypeRegIndemn
	 */
	private ArrayList getListeTypeRegIndemn() {
		return listeTypeRegIndemn;
	}

	/**
	 * Met � jour la liste des RegimeIndemnitaire.
	 * 
	 * @param listeTypeRegIndemn
	 */
	private void setListeTypeRegIndemn(ArrayList listeTypeRegIndemn) {
		this.listeTypeRegIndemn = listeTypeRegIndemn;
	}

	/**
	 * Retourne la liste des types d'avantage en nature.
	 * 
	 * @return listeTypeAvantage
	 */
	private ArrayList getListeTypeAvantage() {
		return listeTypeAvantage;
	}

	/**
	 * Met � jour la liste des types d'avantage en nature.
	 * 
	 * @param listeTypeAvantage
	 */
	private void setListeTypeAvantage(ArrayList listeTypeAvantage) {
		this.listeTypeAvantage = listeTypeAvantage;
	}

	/**
	 * Retourne la liste des avantages en nature � ajouter.
	 * 
	 * @return listeAvantageAAjouter
	 */
	public ArrayList getListeAvantageAAjouter() {
		if (listeAvantageAAjouter == null)
			listeAvantageAAjouter = new ArrayList();
		return listeAvantageAAjouter;
	}

	/**
	 * Retourne la liste des avantages en nature � supprimer.
	 * 
	 * @return listeAvantageASupprimer
	 */
	private ArrayList getListeAvantageASupprimer() {
		if (listeAvantageASupprimer == null)
			listeAvantageASupprimer = new ArrayList();
		return listeAvantageASupprimer;
	}

	/**
	 * Retourne la liste des d�l�gations � ajouter.
	 * 
	 * @return listeDelegationAAjouter
	 */
	public ArrayList getListeDelegationAAjouter() {
		if (listeDelegationAAjouter == null)
			listeDelegationAAjouter = new ArrayList();
		return listeDelegationAAjouter;
	}

	/**
	 * Retourne la liste des d�l�gations � supprimer.
	 * 
	 * @return listeDelegationASupprimer
	 */
	private ArrayList getListeDelegationASupprimer() {
		if (listeDelegationASupprimer == null)
			listeDelegationASupprimer = new ArrayList();
		return listeDelegationASupprimer;
	}

	/**
	 * Retourne la liste des r�gimes indemnitaires � ajouter.
	 * 
	 * @return listeRegimeAAjouter
	 */
	public ArrayList getListeRegimeAAjouter() {
		if (listeRegimeAAjouter == null)
			listeRegimeAAjouter = new ArrayList();
		return listeRegimeAAjouter;
	}

	/**
	 * Retourne la liste des r�gimes indemnitaires � supprimer.
	 * 
	 * @return listeRegimeASupprimer
	 */
	private ArrayList getListeRegimeASupprimer() {
		if (listeRegimeASupprimer == null)
			listeRegimeASupprimer = new ArrayList();
		return listeRegimeASupprimer;
	}

	/**
	 * Retourne la liste des natures d'avantage en nature.
	 * 
	 * @return listeNatureAvantage
	 */
	private ArrayList getListeNatureAvantage() {
		return listeNatureAvantage;
	}

	/**
	 * Retourne la liste des rubriques.
	 * 
	 * @return listeRubrique
	 */
	private ArrayList getListeRubrique() {
		return listeRubrique;
	}

	/**
	 * Met � jour la liste des natures d'avantage en nature.
	 * 
	 * @param listeNatureAvantage
	 */
	private void setListeNatureAvantage(ArrayList listeNatureAvantage) {
		this.listeNatureAvantage = listeNatureAvantage;
	}

	/**
	 * Met � jour la liste des rubriques.
	 * 
	 * @param listeRubrique
	 */
	private void setListeRubrique(ArrayList listeRubrique) {
		this.listeRubrique = listeRubrique;
	}

	/**
	 * Retourne la liste des AvantageNature de l'affectation.
	 * 
	 * @return listeAvantageAFF
	 */
	public ArrayList getListeAvantageAFF() {
		return listeAvantageAFF;
	}

	/**
	 * Met � jour la liste des AvantageNature de l'affectation.
	 * 
	 * @param listeAvantageAFF
	 */
	private void setListeAvantageAFF(ArrayList listeAvantageAFF) {
		this.listeAvantageAFF = listeAvantageAFF;
	}

	/**
	 * Retourne la liste des Delegation de l'affectation.
	 * 
	 * @return listeDelegationAFF
	 */
	public ArrayList getListeDelegationAFF() {
		return listeDelegationAFF;
	}

	/**
	 * Met � jour la liste des Delegation de l'affectation.
	 * 
	 * @param listeDelegationAFF
	 */
	private void setListeDelegationAFF(ArrayList listeDelegationAFF) {
		this.listeDelegationAFF = listeDelegationAFF;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire de l'affectation.
	 * 
	 * @return listeRegimeAFF
	 */
	public ArrayList getListeRegimeAFF() {
		return listeRegimeAFF;
	}

	/**
	 * Met � jour la liste des RegimeIndemnitaire de l'affectation.
	 * 
	 * @param listeRegimeAFF
	 */
	private void setListeRegimeAFF(ArrayList listeRegimeAFF) {
		this.listeRegimeAFF = listeRegimeAFF;
	}

	/**
	 * Contr�le les zones saisies d'un avantage en nature. Date de cr�ation :
	 * (28/07/11)
	 */
	private boolean performControlerSaisieAvNat(HttpServletRequest request) throws Exception {

		// **************************
		// Verification Type avantage
		// **************************
		if (getVAL_LB_TYPE_AVANTAGE_SELECT().length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Type avantage"));
			setFocus(getNOM_LB_TYPE_AVANTAGE());
			return false;
		}

		// ****************************************
		// Verification Montant OU Nature renseign�
		// ****************************************
		if (getVAL_EF_MONTANT_AVANTAGE().length() == 0
				&& ((NatureAvantage) getListeNatureAvantage().get(Integer.parseInt(getVAL_LB_NATURE_AVANTAGE_SELECT()))).getIdNatureAvantage() == null) {
			// "ERR979","Au moins une des 2 zones suivantes doit �tre renseign�e : @ ou @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR979", "Nature avantage", "Montant"));
			setFocus(getNOM_LB_NATURE_AVANTAGE());
			return false;
		}

		// ********************
		// Verification Montant
		// ********************
		if (getVAL_EF_MONTANT_AVANTAGE().length() != 0 && !Services.estNumerique(getVAL_EF_MONTANT_AVANTAGE())) {
			// "ERR992","La zone @ doit �tre num�rique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Montant"));
			setFocus(getNOM_EF_MONTANT_AVANTAGE());
			return false;
		}

		return true;
	}

	/**
	 * Contr�le les zones saisies d'une d�l�gation. Date de cr�ation :
	 * (29/07/11)
	 */
	private boolean performControlerSaisieDel(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * Contr�le les zones saisies d'un r�gime indemnitaire. Date de cr�ation :
	 * (29/07/11)
	 */
	private boolean performControlerSaisieRegIndemn(HttpServletRequest request) throws Exception {

		// *******************************************
		// Verification Forfait OU Nb points renseign�
		// *******************************************
		if (getVAL_EF_FORFAIT_REGIME().length() == 0 && getVAL_EF_NB_POINTS_REGIME().length() == 0) {
			// "ERR979","Au moins une des 2 zones suivantes doit �tre renseign�e : @ ou @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR979", "Forfait", "Nb points"));
			setFocus(getNOM_EF_FORFAIT_REGIME());
			return false;
		}

		// ********************
		// Verification Forfait
		// ********************
		if (getVAL_EF_FORFAIT_REGIME().length() != 0 && !Services.estNumerique(getVAL_EF_FORFAIT_REGIME())) {
			// "ERR992","La zone @ doit �tre num�rique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Forfait"));
			setFocus(getNOM_EF_FORFAIT_REGIME());
			return false;
		}

		// **********************
		// Verification Nb points
		// **********************
		if (getVAL_EF_NB_POINTS_REGIME().length() != 0 && !Services.estNumerique(getVAL_EF_NB_POINTS_REGIME())) {
			// "ERR992","La zone @ doit �tre num�rique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "Nb points"));
			setFocus(getNOM_EF_NB_POINTS_REGIME());
			return false;
		}

		return true;
	}

	/**
	 * Retourne la fiche de poste courante.
	 * 
	 * @return FichePoste
	 */
	private FichePoste getFichePosteCourant() {
		return fichePosteCourant;
	}

	/**
	 * Met � jour la fiche de poste courante.
	 * 
	 * @param fichePosteCourant
	 */
	private void setFichePosteCourant(FichePoste fichePosteCourant) {
		this.fichePosteCourant = fichePosteCourant;
	}

	/**
	 * Retourne la fiche de poste secondaire courante.
	 * 
	 * @return fichePosteSecondaireCourant
	 */
	private FichePoste getFichePosteSecondaireCourant() {
		return fichePosteSecondaireCourant;
	}

	/**
	 * Met � jour la fiche de poste secondaire courante.
	 * 
	 * @param fichePosteSecondaireCourant
	 */
	private void setFichePosteSecondaireCourant(FichePoste fichePosteSecondaireCourant) {
		this.fichePosteSecondaireCourant = fichePosteSecondaireCourant;
	}

	/**
	 * Retourne l'agent courant.
	 * 
	 * @return agentCourant
	 */
	public AgentNW getAgentCourant() {
		return agentCourant;
	}

	/**
	 * Met � jour l'agent courant
	 * 
	 * @param agentCourant
	 */
	private void setAgentCourant(AgentNW agentCourant) {
		this.agentCourant = agentCourant;
	}

	/**
	 * Retourne l'affectation courante.
	 * 
	 * @return affectationCourant
	 */
	private Affectation getAffectationCourant() {
		return affectationCourant;
	}

	/**
	 * Met � jour l'affectation courante.
	 * 
	 * @param affectationCourant
	 *            Nouvelle affectation courante
	 */
	private void setAffectationCourant(Affectation affectationCourant) {
		this.affectationCourant = affectationCourant;
	}

	/**
	 * Retourne le nom de la case � cocher s�lectionn�e pour la JSP :
	 * CK_AVANTAGE Date de cr�ation : (17/08/11 14:39:21)
	 * 
	 */
	public String getNOM_CK_AVANTAGE(int i) {
		return "NOM_CK_AVANTAGE" + i;
	}

	/**
	 * Retourne la valeur de la case � cocher � afficher par la JSP pour la case
	 * � cocher : CK_AVANTAGE Date de cr�ation : (17/08/11 14:39:21)
	 * 
	 */
	public String getVAL_CK_AVANTAGE(int i) {
		return getZone(getNOM_CK_AVANTAGE(i));
	}

	/**
	 * M�thode appel�e par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de cr�ation : (16/08/11 15:48:02)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_AJOUTER_AVANTAGE
			if (testerParametre(request, getNOM_PB_AJOUTER_AVANTAGE())) {
				return performPB_AJOUTER_AVANTAGE(request);
			}

			// Si clic sur le bouton PB_AJOUTER_DELEGATION
			if (testerParametre(request, getNOM_PB_AJOUTER_DELEGATION())) {
				return performPB_AJOUTER_DELEGATION(request);
			}

			// Si clic sur le bouton PB_AJOUTER_REGIME
			if (testerParametre(request, getNOM_PB_AJOUTER_REGIME())) {
				return performPB_AJOUTER_REGIME(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_CHANGER_SPECIFICITE
			if (testerParametre(request, getNOM_PB_CHANGER_SPECIFICITE())) {
				return performPB_CHANGER_SPECIFICITE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_AVANTAGE
			for (int i = getListeAvantageFP().size(); i < getListeAvantageFP().size() + getListeAvantageAFF().size()
					+ getListeAvantageAAjouter().size() - getListeAvantageASupprimer().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_AVANTAGE(i))) {
					return performPB_SUPPRIMER_AVANTAGE(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_DELEGATION
			for (int i = getListeDelegationFP().size(); i < getListeDelegationFP().size() + getListeDelegationAFF().size()
					+ getListeDelegationAAjouter().size() - getListeDelegationASupprimer().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_DELEGATION(i))) {
					return performPB_SUPPRIMER_DELEGATION(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER_REGIME
			for (int i = getListeRegimeFP().size(); i < getListeRegimeFP().size() + getListeRegimeAFF().size() + getListeRegimeAAjouter().size()
					- getListeRegimeASupprimer().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_REGIME(i))) {
					return performPB_SUPPRIMER_REGIME(request, i);
				}
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_VALIDER_AJOUT
			if (testerParametre(request, getNOM_PB_VALIDER_AJOUT())) {
				return performPB_VALIDER_AJOUT(request);
			}

		}
		// Si TAG INPUT non g�r� par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeAGENTEmploisSpecificites. Date de cr�ation :
	 * (18/08/11 10:21:15)
	 * 
	 */
	public OeAGENTEmploisSpecificites() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTEmploisSpecificites.jsp";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LST_AVANTAGE_MONTANT
	 * Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_AVANTAGE_MONTANT(int i) {
		return "NOM_ST_LST_AVANTAGE_MONTANT" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_LST_AVANTAGE_MONTANT Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_AVANTAGE_MONTANT(int i) {
		return getZone(getNOM_ST_LST_AVANTAGE_MONTANT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LST_AVANTAGE_NATURE
	 * Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_AVANTAGE_NATURE(int i) {
		return "NOM_ST_LST_AVANTAGE_NATURE" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_LST_AVANTAGE_NATURE Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_AVANTAGE_NATURE(int i) {
		return getZone(getNOM_ST_LST_AVANTAGE_NATURE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LST_AVANTAGE_TYPE
	 * Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_AVANTAGE_TYPE(int i) {
		return "NOM_ST_LST_AVANTAGE_TYPE" + i;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_LST_AVANTAGE_RUBRIQUE Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_AVANTAGE_RUBRIQUE(int i) {
		return "NOM_ST_LST_AVANTAGE_RUBRIQUE" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_LST_AVANTAGE_TYPE Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_AVANTAGE_TYPE(int i) {
		return getZone(getNOM_ST_LST_AVANTAGE_TYPE(i));
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_LST_AVANTAGE_RUBRIQUE Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_AVANTAGE_RUBRIQUE(int i) {
		return getZone(getNOM_ST_LST_AVANTAGE_RUBRIQUE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_LST_DELEGATION_COMMENT Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_DELEGATION_COMMENT(int i) {
		return "NOM_ST_LST_DELEGATION_COMMENT" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_LST_DELEGATION_COMMENT Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_DELEGATION_COMMENT(int i) {
		return getZone(getNOM_ST_LST_DELEGATION_COMMENT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LST_DELEGATION_TYPE
	 * Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_DELEGATION_TYPE(int i) {
		return "NOM_ST_LST_DELEGATION_TYPE" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_LST_DELEGATION_TYPE Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_DELEGATION_TYPE(int i) {
		return getZone(getNOM_ST_LST_DELEGATION_TYPE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_LST_REGINDEMN_FORFAIT Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_REGINDEMN_FORFAIT(int i) {
		return "NOM_ST_LST_REGINDEMN_FORFAIT" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_LST_REGINDEMN_FORFAIT Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_REGINDEMN_FORFAIT(int i) {
		return getZone(getNOM_ST_LST_REGINDEMN_FORFAIT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_LST_REGINDEMN_NB_POINTS Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_REGINDEMN_NB_POINTS(int i) {
		return "NOM_ST_LST_REGINDEMN_NB_POINTS" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_LST_REGINDEMN_NB_POINTS Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_REGINDEMN_NB_POINTS(int i) {
		return getZone(getNOM_ST_LST_REGINDEMN_NB_POINTS(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LST_REGINDEMN_TYPE
	 * Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_REGINDEMN_TYPE(int i) {
		return "NOM_ST_LST_REGINDEMN_TYPE" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_LST_REGINDEMN_TYPE Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_REGINDEMN_TYPE(int i) {
		return getZone(getNOM_ST_LST_REGINDEMN_TYPE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique :
	 * ST_LST_REGINDEMN_RUBRIQUE Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LST_REGINDEMN_RUBRIQUE(int i) {
		return "NOM_ST_LST_REGINDEMN_RUBRIQUE" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone :
	 * ST_LST_REGINDEMN_RUBRIQUE Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LST_REGINDEMN_RUBRIQUE(int i) {
		return getZone(getNOM_ST_LST_REGINDEMN_RUBRIQUE(i));
	}

	/**
	 * Retourne le nom de la case � cocher s�lectionn�e pour la JSP :
	 * CK_DELEGATION Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_CK_DELEGATION(int i) {
		return "NOM_CK_DELEGATION" + i;
	}

	/**
	 * Retourne la valeur de la case � cocher � afficher par la JSP pour la case
	 * � cocher : CK_DELEGATION Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_CK_DELEGATION(int i) {
		return getZone(getNOM_CK_DELEGATION(i));
	}

	/**
	 * Retourne le nom de la case � cocher s�lectionn�e pour la JSP :
	 * CK_REGINDEMN Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_CK_REGINDEMN(int i) {
		return "NOM_CK_REGINDEMN" + i;
	}

	/**
	 * Retourne la valeur de la case � cocher � afficher par la JSP pour la case
	 * � cocher : CK_REGINDEMN Date de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_CK_REGINDEMN(int i) {
		return getZone(getNOM_CK_REGINDEMN(i));
	}

	/**
	 * Retourne la liste des AvantageNature de la fiche de poste.
	 * 
	 * @return listeAvantageFP
	 */
	public ArrayList getListeAvantageFP() {
		return listeAvantageFP;
	}

	/**
	 * Met � jour la liste des AvantageNature de la fiche de poste.
	 * 
	 * @param listeAvantageFP
	 *            listeAvantageFP � d�finir
	 */
	private void setListeAvantageFP(ArrayList listeAvantageFP) {
		this.listeAvantageFP = listeAvantageFP;
	}

	/**
	 * Retourne la liste des Delegation de la fiche de poste.
	 * 
	 * @return listeDelegationFP
	 */
	public ArrayList getListeDelegationFP() {
		return listeDelegationFP;
	}

	/**
	 * Met � jour la liste des Delegation de la fiche de poste.
	 * 
	 * @param listeDelegationFP
	 *            listeDelegationFP � d�finir
	 */
	private void setListeDelegationFP(ArrayList listeDelegationFP) {
		this.listeDelegationFP = listeDelegationFP;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire de la fiche de poste.
	 * 
	 * @return listeRegimeFP
	 */
	public ArrayList getListeRegimeFP() {
		return listeRegimeFP;
	}

	/**
	 * Met � jour la liste des RegimeIndemnitaire de la fiche de poste.
	 * 
	 * @param listeRegimeFP
	 *            listeRegimeFP � d�finir
	 */
	private void setListeRegimeFP(ArrayList listeRegimeFP) {
		this.listeRegimeFP = listeRegimeFP;
	}

	/**
	 * Vide les champs de saisie de l'avantage en nature.
	 * 
	 * @throws Exception
	 */
	private void viderAvantageNature() throws Exception {
		addZone(getNOM_LB_TYPE_AVANTAGE_SELECT(), "0");
		addZone(getNOM_LB_NATURE_AVANTAGE_SELECT(), "0");
		addZone(getNOM_EF_MONTANT_AVANTAGE(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_RUBRIQUE_AVANTAGE_SELECT(), "0");
	}

	/**
	 * Vide les champs de saisie de l'avantage en nature.
	 * 
	 * @throws Exception
	 */
	private void viderDelegation() throws Exception {
		addZone(getNOM_LB_TYPE_DELEGATION_SELECT(), "0");
		addZone(getNOM_EF_COMMENT_DELEGATION(), Const.CHAINE_VIDE);
	}

	/**
	 * Vide les champs de saisie de l'avantage en nature.
	 * 
	 * @throws Exception
	 */
	private void viderRegIndemn() throws Exception {
		addZone(getNOM_LB_TYPE_REGIME_SELECT(), "0");
		addZone(getNOM_EF_FORFAIT_REGIME(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NB_POINTS_REGIME(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_RUBRIQUE_AVANTAGE_SELECT(), "0");
	}

	public String getNomEcran() {
		return "ECR-AG-EMPLOIS-SPECIFICITES";
	}
}