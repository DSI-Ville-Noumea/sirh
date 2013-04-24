package nc.mairie.gestionagent.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeCompetence;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.agent.LienDocumentAgent;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.carriere.FiliereGrade;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.diplome.DiplomeAgent;
import nc.mairie.metier.parametrage.CadreEmploi;
import nc.mairie.metier.parametrage.DiplomeGenerique;
import nc.mairie.metier.parametrage.NatureAvantage;
import nc.mairie.metier.parametrage.SpecialiteDiplomeNW;
import nc.mairie.metier.parametrage.TitreDiplome;
import nc.mairie.metier.parametrage.TypeAvantage;
import nc.mairie.metier.parametrage.TypeDelegation;
import nc.mairie.metier.parametrage.TypeRegIndemn;
import nc.mairie.metier.poste.Activite;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.Budget;
import nc.mairie.metier.poste.Competence;
import nc.mairie.metier.poste.DiplomeFP;
import nc.mairie.metier.poste.EntiteGeo;
import nc.mairie.metier.poste.FicheEmploi;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Horaire;
import nc.mairie.metier.poste.NiveauEtudeFP;
import nc.mairie.metier.poste.Service;
import nc.mairie.metier.poste.StatutFP;
import nc.mairie.metier.poste.TitrePoste;
import nc.mairie.metier.referentiel.NiveauEtude;
import nc.mairie.metier.referentiel.TypeCompetence;
import nc.mairie.metier.specificites.AvantageNature;
import nc.mairie.metier.specificites.Delegation;
import nc.mairie.metier.specificites.RegimeIndemnitaire;
import nc.mairie.metier.specificites.Rubrique;
import nc.mairie.spring.dao.metier.specificites.PrimePointageDao;
import nc.mairie.spring.dao.metier.specificites.PrimePointageFPDao;
import nc.mairie.spring.domain.metier.specificites.PrimePointage;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.springframework.context.ApplicationContext;

/**
 * Process OeAGENTEmploisPoste Date de création : (03/08/11 17:03:03)
 * 
 */
public class OeAGENTEmploisPoste extends nc.mairie.technique.BasicProcess {

	private ArrayList<AvantageNature> listeAvantage;
	private ArrayList<Delegation> listeDelegation;
	private ArrayList<RegimeIndemnitaire> listeRegIndemn;
	private ArrayList<PrimePointage> listePrimePointage;
	private ArrayList<Competence> listeSavoir;
	private ArrayList<Competence> listeSavoirFaire;
	private ArrayList<Competence> listeComportementPro;
	private ArrayList<Activite> listeActivite;
	private ArrayList<Competence> listeCompetence;

	private AgentNW agentCourant;
	private FichePoste fichePosteCourant;
	private FichePoste fichePosteSecondaireCourant;
	private Affectation affectationCourant;
	private TypeCompetence typeCompetenceCourant;

	private String titrePoste;
	private Service service;
	private Service direction;
	private Service section;
	private String localisation;
	private String responsable;
	private String cadreEmploi;
	private String gradeFP;
	private String gradeAgt;
	private String listeDiplomeGenFP = Const.CHAINE_VIDE;
	private String diplomeAgt;

	public String ACTION_IMPRESSION = "Impression d'un contrat.";
	private String focus = null;
	private String urlFichier;

	private PrimePointageDao primePointageDao;
	private PrimePointageFPDao primePointageFPDao;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		}

		// Vérification des droits d'accès.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
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
		initialiseDao();

		// Mise à jour de la liste des compétences
		if (getTypeCompetenceCourant() == null || getTypeCompetenceCourant().getIdTypeCompetence() == null) {
			setTypeCompetenceCourant(TypeCompetence.chercherTypeCompetence(getTransaction(), "1"));
			addZone(getNOM_RG_TYPE_COMPETENCE(), getNOM_RB_TYPE_COMPETENCE_S());
		} else {
			if (getVAL_RG_TYPE_COMPETENCE().equals(getNOM_RB_TYPE_COMPETENCE_S()))
				setTypeCompetenceCourant(TypeCompetence.chercherTypeCompetenceAvecLibelle(getTransaction(), EnumTypeCompetence.SAVOIR.getValue()));
			if (getVAL_RG_TYPE_COMPETENCE().equals(getNOM_RB_TYPE_COMPETENCE_SF()))
				setTypeCompetenceCourant(TypeCompetence.chercherTypeCompetenceAvecLibelle(getTransaction(),
						EnumTypeCompetence.SAVOIR_FAIRE.getValue()));
			if (getVAL_RG_TYPE_COMPETENCE().equals(getNOM_RB_TYPE_COMPETENCE_C()))
				setTypeCompetenceCourant(TypeCompetence.chercherTypeCompetenceAvecLibelle(getTransaction(),
						EnumTypeCompetence.COMPORTEMENT.getValue()));
		}

		// Si pas d'affectation en cours
		if (getFichePosteCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			ArrayList<Affectation> affActives = Affectation.listerAffectationActiveAvecAgent(getTransaction(), getAgentCourant());
			if (affActives.size() == 0) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR083"));
				return;
			} else if (affActives.size() > 1) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR082"));
				return;
			} else {
				setAffectationCourant((Affectation) affActives.get(0));
				setFichePosteCourant(FichePoste.chercherFichePoste(getTransaction(), getAffectationCourant().getIdFichePoste()));
				if (getAffectationCourant().getIdFichePosteSecondaire() != null) {
					setFichePosteSecondaireCourant(FichePoste.chercherFichePoste(getTransaction(), getAffectationCourant()
							.getIdFichePosteSecondaire()));
				}
				alimenterFicheDePoste();
			}
		} else {
			alimenterFicheDePoste();
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getPrimePointageDao() == null) {
			setPrimePointageDao((PrimePointageDao) context.getBean("primePointageDao"));
		}
		if (getPrimePointageFPDao() == null) {
			setPrimePointageFPDao((PrimePointageFPDao) context.getBean("primePointageFPDao"));
		}
	}

	private void alimenterFicheDePoste() throws Exception {
		if (getFichePosteCourant() != null) {
			// Recherche des informations à afficher
			setTitrePoste(getFichePosteCourant().getIdTitrePoste() == null ? Const.CHAINE_VIDE : TitrePoste.chercherTitrePoste(getTransaction(),
					getFichePosteCourant().getIdTitrePoste()).getLibTitrePoste());

			setDirection(Service.getDirection(getTransaction(), getFichePosteCourant().getIdServi()));
			Service serv = Service.getDivision(getTransaction(), getFichePosteCourant().getIdServi());
			if (serv == null)
				serv = Service.chercherService(getTransaction(), getFichePosteCourant().getIdServi());
			setService(serv);
			setSection(Service.getSection(getTransaction(), getFichePosteCourant().getIdServi()));

			setLocalisation(getFichePosteCourant().getIdEntiteGeo() == null ? Const.CHAINE_VIDE : EntiteGeo.chercherEntiteGeo(getTransaction(),
					getFichePosteCourant().getIdEntiteGeo()).getLibEntiteGeo());
			if (getFichePosteCourant().getIdResponsable() != null) {
				FichePoste fpResponsable = FichePoste.chercherFichePoste(getTransaction(), getFichePosteCourant().getIdResponsable());
				setResponsable(fpResponsable.getIdTitrePoste() == null ? Const.CHAINE_VIDE : TitrePoste.chercherTitrePoste(getTransaction(),
						fpResponsable.getIdTitrePoste()).getLibTitrePoste());
			}
			String gradeAffichage = Const.CHAINE_VIDE;
			if (getFichePosteCourant().getCodeGrade() != null) {
				Grade g = Grade.chercherGrade(getTransaction(), getFichePosteCourant().getCodeGrade());
				gradeAffichage = g.getGrade();

				GradeGenerique gg = GradeGenerique.chercherGradeGenerique(getTransaction(), g.getCodeGradeGenerique());
				CadreEmploi cadreEmp = null;
				if (gg != null && gg.getIdCadreEmploi() != null) {
					cadreEmp = CadreEmploi.chercherCadreEmploi(getTransaction(), gg.getIdCadreEmploi());
				}
				setCadreEmploi(cadreEmp == null || cadreEmp.getIdCadreEmploi() == null ? Const.CHAINE_VIDE : cadreEmp.getLibCadreEmploi());
			}

			setGradeFP(gradeAffichage);

			// Liste Diplomes FP
			setListeDiplomeGenFP(null);
			ArrayList<DiplomeGenerique> dg = DiplomeGenerique.listerDiplomeGeneriqueAvecFP(getTransaction(), getFichePosteCourant());
			for (DiplomeGenerique d : dg) {
				if (getListeDiplomeGenFP() == null)
					setListeDiplomeGenFP(d.getLibDiplomeGenerique());
				else
					setListeDiplomeGenFP(getListeDiplomeGenFP().concat(", " + d.getLibDiplomeGenerique()));
			}

			// Diplome Agent
			DiplomeAgent dipl = DiplomeAgent.chercherDernierDiplomeAgentAvecAgent(getTransaction(), getAgentCourant());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			} else {
				TitreDiplome t = TitreDiplome.chercherTitreDiplome(getTransaction(), dipl.getIdTitreDiplome());
				SpecialiteDiplomeNW s = SpecialiteDiplomeNW.chercherSpecialiteDiplomeNW(getTransaction(), dipl.getIdSpecialiteDiplome());
				setDiplomeAgt(t.getLibTitreDiplome() + " - " + s.getLibSpeDiplome());
			}

			// Carriere
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), getAgentCourant().getIdAgent());
			if (carr != null) {
				Grade grade = Grade.chercherGrade(getTransaction(), carr.getCodeGrade());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					setGradeAgt(Const.CHAINE_VIDE);
				} else {
					setGradeAgt(grade.getLibGrade());
				}
			}

			// Affiche les zones de la page
			alimenterZones();
			// Affiche les activités
			initialiserActivite();
			// Affiche les compétences
			initialiserCompetence();
			// Affiche les spécificités de la fiche de poste
			initialiserSpecificites();
		}
	}

	/**
	 * Récupère les spécificités de la fiche de poste.
	 * 
	 * @throws Exception
	 */
	private void initialiserSpecificites() throws Exception {
		setListeAvantage(null);
		setListeDelegation(null);
		setListeRegIndemn(null);
		setListePrimePointage(null);

		// Avantages en nature
		if (getListeAvantage().size() == 0) {
			setListeAvantage(AvantageNature.listerAvantageNatureAvecFP(getTransaction(), getFichePosteCourant().getIdFichePoste()));
			if (getTransaction().isErreur()) {
				return;
			}
		}
		int indiceAvantage = 0;
		if (getListeAvantage() != null) {
			for (ListIterator<AvantageNature> list = getListeAvantage().listIterator(); list.hasNext();) {
				AvantageNature aAvNat = (AvantageNature) list.next();
				if (aAvNat != null) {
					TypeAvantage typAv = TypeAvantage.chercherTypeAvantage(getTransaction(), aAvNat.getIdTypeAvantage());
					NatureAvantage natAv = NatureAvantage.chercherNatureAvantage(getTransaction(), aAvNat.getIdNatureAvantage());

					addZone(getNOM_ST_AV_TYPE(indiceAvantage),
							typAv.getLibTypeAvantage().equals(Const.CHAINE_VIDE) ? "&nbsp;" : typAv.getLibTypeAvantage());
					addZone(getNOM_ST_AV_MNT(indiceAvantage), aAvNat.getMontant());
					addZone(getNOM_ST_AV_NATURE(indiceAvantage), natAv == null ? "&nbsp;" : natAv.getLibNatureAvantage());
				}
				indiceAvantage++;
			}
		}

		// Délégations
		setListeDelegation((ArrayList<Delegation>) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_LST_DELEGATION));
		if (getListeDelegation().size() == 0) {
			setListeDelegation(Delegation.listerDelegationAvecFP(getTransaction(), getFichePosteCourant().getIdFichePoste()));
			if (getTransaction().isErreur()) {
				return;
			}
		}
		int indiceDelegation = 0;
		if (getListeDelegation() != null) {
			for (ListIterator<Delegation> list = getListeDelegation().listIterator(); list.hasNext();) {
				Delegation aDel = (Delegation) list.next();
				if (aDel != null) {
					TypeDelegation typDel = TypeDelegation.chercherTypeDelegation(getTransaction(), aDel.getIdTypeDelegation());

					addZone(getNOM_ST_DEL_TYPE(indiceDelegation),
							typDel.getLibTypeDelegation().equals(Const.CHAINE_VIDE) ? "&nbsp;" : typDel.getLibTypeDelegation());
					addZone(getNOM_ST_DEL_COMMENTAIRE(indiceDelegation),
							aDel.getLibDelegation().equals(Const.CHAINE_VIDE) ? "&nbsp;" : aDel.getLibDelegation());
				}
				indiceDelegation++;
			}
		}

		// Régimes indemnitaires
		if (getListeRegIndemn().size() == 0) {
			setListeRegIndemn(RegimeIndemnitaire.listerRegimeIndemnitaireAvecFP(getTransaction(), getFichePosteCourant().getIdFichePoste()));
			if (getTransaction().isErreur()) {
				return;
			}
		}
		int indiceRegime = 0;
		if (getListeRegIndemn() != null) {
			for (ListIterator<RegimeIndemnitaire> list = getListeRegIndemn().listIterator(); list.hasNext();) {
				RegimeIndemnitaire aReg = (RegimeIndemnitaire) list.next();
				if (aReg != null) {
					TypeRegIndemn typReg = TypeRegIndemn.chercherTypeRegIndemn(getTransaction(), aReg.getIdTypeRegIndemn());

					addZone(getNOM_ST_REG_TYPE(indiceRegime),
							typReg.getLibTypeRegIndemn().equals(Const.CHAINE_VIDE) ? "&nbsp;" : typReg.getLibTypeRegIndemn());
					addZone(getNOM_ST_REG_FORFAIT(indiceRegime), aReg.getForfait());
					addZone(getNOM_ST_REG_NB_PTS(indiceRegime), aReg.getNombrePoints());
				}
				indiceRegime++;
			}
		}

		// Prime pointage
		if (getListePrimePointage().size() == 0) {
			setListePrimePointage(getPrimePointageDao().listerPrimePointageAvecFP(Integer.valueOf(getFichePosteCourant().getIdFichePoste())));
		}
		int indicePrime = 0;
		if (getListePrimePointage() != null) {
			for (ListIterator<PrimePointage> list = getListePrimePointage().listIterator(); list.hasNext();) {
				PrimePointage prime = list.next();
				if (prime != null) {
					Rubrique rubr = Rubrique.chercherRubrique(getTransaction(), prime.getIdRubrique().toString());
					addZone(getNOM_ST_PP_RUBR(indicePrime), rubr.getNumRubrique() + " - " + rubr.getLibRubrique());
				}
				indicePrime++;
			}
		}
	}

	/**
	 * Récupère les compétences des activités principales et supplémentaires
	 * choisies.
	 * 
	 * @throws Exception
	 */
	private void initialiserCompetence() throws Exception {
		TypeCompetence savoir = TypeCompetence.chercherTypeCompetenceAvecLibelle(getTransaction(), EnumTypeCompetence.SAVOIR.getValue());
		if (getTransaction().isErreur()) {
			getTransaction().declarerErreur(getTransaction().traiterErreur());
		}
		TypeCompetence savoirFaire = TypeCompetence.chercherTypeCompetenceAvecLibelle(getTransaction(), EnumTypeCompetence.SAVOIR_FAIRE.getValue());
		if (getTransaction().isErreur()) {
			getTransaction().declarerErreur(getTransaction().traiterErreur());
		}
		TypeCompetence comportement = TypeCompetence.chercherTypeCompetenceAvecLibelle(getTransaction(), EnumTypeCompetence.COMPORTEMENT.getValue());
		if (getTransaction().isErreur()) {
			getTransaction().declarerErreur(getTransaction().traiterErreur());
		}

		// Compétences
		ArrayList<Competence> comp = Competence.listerCompetenceAvecFP(getTransaction(), getFichePosteCourant());
		if (comp != null) {
			setListeCompetence(comp);
		} else {
			setListeCompetence(null);
		}

		getListeSavoir().clear();
		getListeSavoirFaire().clear();
		getListeComportementPro().clear();

		for (int j = 0; j < getListeCompetence().size(); j++) {
			Competence c = (Competence) getListeCompetence().get(j);
			if (savoir.getIdTypeCompetence().equals(c.getIdTypeCompetence())) {
				if (!getListeSavoir().contains(c))
					getListeSavoir().add(c);
			} else if (savoirFaire.getIdTypeCompetence().equals(c.getIdTypeCompetence())) {
				if (!getListeSavoirFaire().contains(c))
					getListeSavoirFaire().add(c);
			} else if (comportement.getIdTypeCompetence().equals(c.getIdTypeCompetence())) {
				if (!getListeComportementPro().contains(c))
					getListeComportementPro().add(c);
			}
		}

		int indiceCompS = 0;
		if (getListeSavoir() != null) {
			for (int i = 0; i < getListeSavoir().size(); i++) {
				Competence co = (Competence) getListeSavoir().get(i);
				addZone(getNOM_ST_LIB_COMP_S(indiceCompS), co.getNomCompetence().equals(Const.CHAINE_VIDE) ? "&nbsp;" : co.getNomCompetence());
				indiceCompS++;
			}
		}

		int indiceCompSF = 0;
		if (getListeSavoirFaire() != null) {
			for (int i = 0; i < getListeSavoirFaire().size(); i++) {
				Competence co = (Competence) getListeSavoirFaire().get(i);
				addZone(getNOM_ST_LIB_COMP_SF(indiceCompSF), co.getNomCompetence().equals(Const.CHAINE_VIDE) ? "&nbsp;" : co.getNomCompetence());
				indiceCompSF++;
			}
		}

		int indiceCompPro = 0;
		if (getListeComportementPro() != null) {
			for (int i = 0; i < getListeComportementPro().size(); i++) {
				Competence co = (Competence) getListeComportementPro().get(i);
				addZone(getNOM_ST_LIB_COMP_PRO(indiceCompPro), co.getNomCompetence().equals(Const.CHAINE_VIDE) ? "&nbsp;" : co.getNomCompetence());
				indiceCompPro++;
			}
		}
	}

	/**
	 * Récupère les compétences des activités principales et supplémentaires
	 * choisies.
	 * 
	 * @throws Exception
	 */
	private void initialiserActivite() throws Exception {
		// Activités
		setListeActivite(Activite.listerToutesActiviteAvecFP(getTransaction(), getFichePosteCourant()));
		int indiceActi = 0;
		if (getListeActivite() != null) {
			for (int i = 0; i < getListeActivite().size(); i++) {
				Activite acti = (Activite) getListeActivite().get(i);
				addZone(getNOM_ST_LIB_ACTI(indiceActi), acti.getNomActivite().equals(Const.CHAINE_VIDE) ? "&nbsp;" : acti.getNomActivite());
				indiceActi++;
			}
		}
	}

	public void alimenterZones() throws Exception {
		addZone(getNOM_ST_BUDGET(),
				getFichePosteCourant().getIdBudget() == null ? Const.CHAINE_VIDE : Budget.chercherBudget(getTransaction(),
						getFichePosteCourant().getIdBudget()).getLibBudget());
		addZone(getNOM_ST_ANNEE(), getFichePosteCourant().getAnneeCreation());
		addZone(getNOM_ST_NUMERO(), getFichePosteCourant().getNumFP());
		addZone(getNOM_ST_NFA(), getFichePosteCourant().getNFA());
		addZone(getNOM_ST_OPI(), getFichePosteCourant().getOPI());
		addZone(getNOM_ST_REGLEMENTAIRE(), Horaire.chercherHoraire(getTransaction(), getFichePosteCourant().getIdCdthorReg()).getLibHor());
		addZone(getNOM_ST_POURCENT_BUDGETE(), Horaire.chercherHoraire(getTransaction(), getFichePosteCourant().getIdCdthorBud()).getLibHor());
		addZone(getNOM_ST_ACT_INACT(),
				getFichePosteCourant().getIdStatutFP() == null ? Const.CHAINE_VIDE : StatutFP.chercherStatutFP(getTransaction(),
						getFichePosteCourant().getIdStatutFP()).getLibStatutFP());

		addZone(getNOM_ST_TITRE(), getTitrePoste());
		addZone(getNOM_ST_DIRECTION(), getDirection() == null ? Const.CHAINE_VIDE : getDirection().getLibService());
		addZone(getNOM_ST_SERVICE(), getService() == null ? Const.CHAINE_VIDE : getService().getLibService());
		addZone(getNOM_ST_SECTION(), getSection() == null ? Const.CHAINE_VIDE : getSection().getLibService());

		addZone(getNOM_ST_LOCALISATION(), getLocalisation());
		addZone(getNOM_ST_ETUDE(), getListeDiplomeGenFP());
		addZone(getNOM_ST_RESPONSABLE(), getResponsable());
		addZone(getNOM_ST_GRADE(), getGradeFP());
		addZone(getNOM_ST_CADRE_EMPLOI(), getCadreEmploi());
		addZone(getNOM_ST_MISSION(), getFichePosteCourant().getMissions());

		addZone(getNOM_ST_TITULAIRE(), getAgentCourant().getNomAgent() + " " + getAgentCourant().getPrenomAgent());

		addZone(getNOM_ST_GRADE_AGT(), getGradeAgt());
		addZone(getNOM_ST_ETUDE_AGT(), getDiplomeAgt());
		addZone(getNOM_ST_TPS_TRAVAIL_AGT(), getAffectationCourant().getTempsTravail() + " %");
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {
		// Si clic sur le bouton PB_IMPRIMER
		if (testerParametre(request, getNOM_PB_IMPRIMER())) {
			return performPB_IMPRIMER(request);
		}
		// Si clic sur le bouton PB_ANNULER
		if (testerParametre(request, getNOM_PB_ANNULER())) {
			return performPB_ANNULER(request);
		}
		// Si clic sur le bouton PB_VALIDER_IMPRIMER
		if (testerParametre(request, getNOM_PB_VALIDER_IMPRIMER())) {
			return performPB_VALIDER_IMPRIMER(request);
		}
		// Si clic sur le bouton PB_VOIR_AUTRE_FP
		if (testerParametre(request, getNOM_PB_VOIR_AUTRE_FP())) {
			return performPB_VOIR_AUTRE_FP(request);
		}
		// Si clic sur le bouton PB_CHANGER_TYPE
		if (testerParametre(request, getNOM_PB_CHANGER_TYPE())) {
			return performPB_CHANGER_TYPE(request);
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION())) {
			setStatut(STATUT_PROCESS_APPELANT);
		}
		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	public boolean performPB_VALIDER_IMPRIMER(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}
		imprimeModele(request);

		return true;
	}

	public boolean performPB_VOIR_AUTRE_FP(HttpServletRequest request) throws Exception {
		FichePoste origine = getFichePosteCourant();
		setFichePosteCourant(getFichePosteSecondaireCourant());
		setFichePosteSecondaireCourant(origine);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeAGENTEmploisPoste. Date de création : (03/08/11
	 * 17:03:03)
	 * 
	 */
	public OeAGENTEmploisPoste() {
		super();
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACT_INACT Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_ACT_INACT() {
		return "NOM_ST_ACT_INACT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACT_INACT Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_ACT_INACT() {
		return getZone(getNOM_ST_ACT_INACT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ANNEE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_ANNEE() {
		return "NOM_ST_ANNEE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ANNEE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_ANNEE() {
		return getZone(getNOM_ST_ANNEE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_BUDGET Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_BUDGET() {
		return "NOM_ST_BUDGET";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_BUDGET Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_BUDGET() {
		return getZone(getNOM_ST_BUDGET());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CADRE_EMPLOI Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_CADRE_EMPLOI() {
		return "NOM_ST_CADRE_EMPLOI";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CADRE_EMPLOI
	 * Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_CADRE_EMPLOI() {
		return getZone(getNOM_ST_CADRE_EMPLOI());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DIRECTION Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_DIRECTION() {
		return "NOM_ST_DIRECTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DIRECTION Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_DIRECTION() {
		return getZone(getNOM_ST_DIRECTION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ETUDE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_ETUDE() {
		return "NOM_ST_ETUDE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ETUDE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_ETUDE() {
		return getZone(getNOM_ST_ETUDE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ETUDE_AGT Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_ETUDE_AGT() {
		return "NOM_ST_ETUDE_AGT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ETUDE_AGT Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_ETUDE_AGT() {
		return getZone(getNOM_ST_ETUDE_AGT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_GRADE() {
		return "NOM_ST_GRADE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_GRADE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_GRADE() {
		return getZone(getNOM_ST_GRADE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE_AGT Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_GRADE_AGT() {
		return "NOM_ST_GRADE_AGT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_GRADE_AGT Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_GRADE_AGT() {
		return getZone(getNOM_ST_GRADE_AGT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LOCALISATION Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_LOCALISATION() {
		return "NOM_ST_LOCALISATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LOCALISATION
	 * Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_LOCALISATION() {
		return getZone(getNOM_ST_LOCALISATION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_MISSION Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_MISSION() {
		return "NOM_ST_MISSION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MISSION Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_MISSION() {
		return getZone(getNOM_ST_MISSION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NFA Date de création
	 * : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_NFA() {
		return "NOM_ST_NFA";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NFA Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_NFA() {
		return getZone(getNOM_ST_NFA());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUMERO Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_NUMERO() {
		return "NOM_ST_NUMERO";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NUMERO Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_NUMERO() {
		return getZone(getNOM_ST_NUMERO());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_OPI Date de création
	 * : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_OPI() {
		return "NOM_ST_OPI";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_OPI Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_OPI() {
		return getZone(getNOM_ST_OPI());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_POURCENT_BUDGETE
	 * Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_POURCENT_BUDGETE() {
		return "NOM_ST_POURCENT_BUDGETE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_POURCENT_BUDGETE Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_POURCENT_BUDGETE() {
		return getZone(getNOM_ST_POURCENT_BUDGETE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_REGLEMENTAIRE Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_REGLEMENTAIRE() {
		return "NOM_ST_REGLEMENTAIRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_REGLEMENTAIRE
	 * Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_REGLEMENTAIRE() {
		return getZone(getNOM_ST_REGLEMENTAIRE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RESPONSABLE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_RESPONSABLE() {
		return "NOM_ST_RESPONSABLE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_RESPONSABLE
	 * Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_RESPONSABLE() {
		return getZone(getNOM_ST_RESPONSABLE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_SERVICE() {
		return "NOM_ST_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SERVICE Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_SERVICE() {
		return getZone(getNOM_ST_SERVICE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TITRE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_TITRE() {
		return "NOM_ST_TITRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TITRE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_TITRE() {
		return getZone(getNOM_ST_TITRE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TITULAIRE Date de
	 * création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_TITULAIRE() {
		return "NOM_ST_TITULAIRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TITULAIRE Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_TITULAIRE() {
		return getZone(getNOM_ST_TITULAIRE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TPS_TRAVAIL_AGT Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_TPS_TRAVAIL_AGT() {
		return "NOM_ST_TPS_TRAVAIL_AGT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_TPS_TRAVAIL_AGT Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_TPS_TRAVAIL_AGT() {
		return getZone(getNOM_ST_TPS_TRAVAIL_AGT());
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
	 * Met à jour l'agent courant
	 * 
	 * @param agentCourant
	 */
	private void setAgentCourant(AgentNW agentCourant) {
		this.agentCourant = agentCourant;
	}

	/**
	 * Retourne la fiche de poste courante.
	 * 
	 * @return fichePosteCourant
	 */
	public FichePoste getFichePosteCourant() {
		return fichePosteCourant;
	}

	/**
	 * Met à jour la fiche de poste courante.
	 * 
	 * @param fichePosteCourant
	 */
	private void setFichePosteCourant(FichePoste fichePosteCourant) {
		this.fichePosteCourant = fichePosteCourant;
	}

	/**
	 * Retourne la fiche de poste courante.
	 * 
	 * @return fichePosteSecondaireCourant
	 */
	public FichePoste getFichePosteSecondaireCourant() {
		return fichePosteSecondaireCourant;
	}

	/**
	 * Met à jour la fiche de poste courante.
	 * 
	 * @param fichePosteCourant
	 */
	private void setFichePosteSecondaireCourant(FichePoste fichePosteSecondaireCourant) {
		this.fichePosteSecondaireCourant = fichePosteSecondaireCourant;
	}

	/**
	 * Retourne la liste des activités.
	 * 
	 * @return listeActivite
	 */
	public ArrayList<Activite> getListeActivite() {
		if (listeActivite == null)
			listeActivite = new ArrayList<Activite>();
		return listeActivite;
	}

	/**
	 * Retourne la liste des activités principales sous forme d'une chaîne de
	 * caractères.
	 * 
	 * @return listeActivitePrinc
	 */
	/*
	 * private String getListeActiviteIntoString() throws Exception { String
	 * result = Const.CHAINE_VIDE; for (Activite actiPrinc : getListeActivite())
	 * { if (result.length() == 0) result = actiPrinc.getNomActivite(); else
	 * result = result.concat(", " + actiPrinc.getNomActivite()); } return
	 * result; }
	 */

	/**
	 * Met à jour la liste des activités principales.
	 * 
	 * @param listeActivitePrinc
	 *            Nouvelle liste des activités
	 */
	private void setListeActivite(ArrayList<Activite> listeActivite) {
		this.listeActivite = listeActivite;
	}

	/**
	 * Retourne la liste des comportements professionels.
	 * 
	 * @return listeComportementPro
	 */
	public ArrayList<Competence> getListeComportementPro() {
		if (listeComportementPro == null)
			listeComportementPro = new ArrayList<Competence>();
		return listeComportementPro;
	}

	/**
	 * Retourne la liste des Savoir.
	 * 
	 * @return listeSavoir
	 */
	public ArrayList<Competence> getListeSavoir() {
		if (listeSavoir == null)
			listeSavoir = new ArrayList<Competence>();
		return listeSavoir;
	}

	/**
	 * Retourne la liste des Savoir-Faire.
	 * 
	 * @return listeSavoirFaire
	 */
	public ArrayList<Competence> getListeSavoirFaire() {
		if (listeSavoirFaire == null)
			listeSavoirFaire = new ArrayList<Competence>();
		return listeSavoirFaire;
	}

	/**
	 * Retourne la liste des AvantageNature.
	 * 
	 * @return listeAvantage
	 */
	public ArrayList<AvantageNature> getListeAvantage() {
		if (listeAvantage == null)
			listeAvantage = new ArrayList<AvantageNature>();
		return listeAvantage;
	}

	/**
	 * Retourne la liste des Delegation.
	 * 
	 * @return listeDelegation
	 */
	public ArrayList<Delegation> getListeDelegation() {
		if (listeDelegation == null)
			listeDelegation = new ArrayList<Delegation>();
		return listeDelegation;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire.
	 * 
	 * @return listeRegIndemn
	 */
	public ArrayList<RegimeIndemnitaire> getListeRegIndemn() {
		if (listeRegIndemn == null)
			listeRegIndemn = new ArrayList<RegimeIndemnitaire>();
		return listeRegIndemn;
	}

	/**
	 * Met à jour la liste des RegimeIndemnitaire.
	 * 
	 * @param newListeRegIndemn
	 *            Nouvelle liste des RegimeIndemnitaire
	 */
	private void setListeRegIndemn(ArrayList<RegimeIndemnitaire> newListeRegIndemn) {
		this.listeRegIndemn = newListeRegIndemn;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire.
	 * 
	 * @return listePrimePointage
	 */
	public ArrayList<PrimePointage> getListePrimePointage() {
		if (listePrimePointage == null)
			listePrimePointage = new ArrayList<PrimePointage>();
		return listePrimePointage;
	}

	/**
	 * Met à jour la liste des RegimeIndemnitaire.
	 * 
	 * @param newListePrimePointage
	 *            Nouvelle liste des RegimeIndemnitaire
	 */
	private void setListePrimePointage(ArrayList<PrimePointage> newListePrimePointage) {
		this.listePrimePointage = newListePrimePointage;
	}

	/**
	 * Met à jour la liste des Delegation.
	 * 
	 * @param newListeDelegation
	 *            Nouvelle liste des Delegation
	 */
	private void setListeDelegation(ArrayList<Delegation> newListeDelegation) {
		this.listeDelegation = newListeDelegation;
	}

	/**
	 * Met à jour la liste des AvantageNature.
	 * 
	 * @param newListeAvantage
	 *            Nouvelle liste des AvantageNature
	 */
	private void setListeAvantage(ArrayList<AvantageNature> newListeAvantage) {
		this.listeAvantage = newListeAvantage;
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
	 * Met à jour l'affectation courante.
	 * 
	 * @param affectationCourant
	 *            Nouvelle affectation courante
	 */
	private void setAffectationCourant(Affectation affectationCourant) {
		this.affectationCourant = affectationCourant;
	}

	private Service getService() {
		return service;
	}

	/**
	 * Setter du Service.
	 * 
	 * @param service
	 */
	private void setService(Service service) {
		this.service = service;
	}

	/**
	 * Getter du Titre du poste.
	 */
	private String getTitrePoste() {
		return titrePoste;
	}

	/**
	 * Setter du Titre du poste.
	 * 
	 * @param titrePoste
	 */
	private void setTitrePoste(String titrePoste) {
		this.titrePoste = titrePoste;
	}

	private Service getDirection() {
		return direction;
	}

	/**
	 * Setter de la direction du service.
	 * 
	 * @param direction
	 */
	private void setDirection(Service direction) {
		this.direction = direction;
	}

	private String getLocalisation() {
		return localisation;
	}

	/**
	 * Setter de la localisation.
	 * 
	 * @param localisation
	 */
	private void setLocalisation(String localisation) {
		this.localisation = localisation;
	}

	private String getResponsable() {
		return responsable;
	}

	/**
	 * Setter du responsable.
	 * 
	 * @param responsable
	 *            responsable à définir
	 */
	private void setResponsable(String responsable) {
		this.responsable = responsable;
	}

	private String getCadreEmploi() {
		return cadreEmploi;
	}

	/**
	 * Setter du cadreEmploi.
	 * 
	 * @param cadreEmploi
	 */
	private void setCadreEmploi(String cadreEmploi) {
		this.cadreEmploi = cadreEmploi;
	}

	private String getGradeFP() {
		return gradeFP;
	}

	/**
	 * Setter du garde de la fiche de poste.
	 * 
	 * @param gradeFP
	 */
	private void setGradeFP(String gradeFP) {
		this.gradeFP = gradeFP;
	}

	/**
	 * Setter de la liste des Diplomes.
	 * 
	 * @param listeDiplomeGenFP
	 */
	private void setListeDiplomeGenFP(String listeDiplomeGenFP) {
		this.listeDiplomeGenFP = listeDiplomeGenFP;
	}

	/**
	 * Getter de la liste des Diplomes.
	 * 
	 * @return listeDiplomeGenFP
	 */
	private String getListeDiplomeGenFP() {
		return listeDiplomeGenFP;
	}

	/**
	 * @param diplomeAgt
	 *            diplomeAgt à définir
	 */
	private void setDiplomeAgt(String diplomeAgt) {
		this.diplomeAgt = diplomeAgt;
	}

	public String getNomEcran() {
		return "ECR-AG-EMPLOIS-POSTE";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VOIR_AUTRE_FP Date de
	 * création : (04/11/11 10:59:29)
	 * 
	 * 
	 */
	public String getNOM_PB_VOIR_AUTRE_FP() {
		return "NOM_PB_VOIR_AUTRE_FP";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER Date de création :
	 * (04/11/11 10:59:29)
	 * 
	 * 
	 */
	public String getNOM_PB_IMPRIMER() {
		return "NOM_PB_IMPRIMER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER Date de création :
	 * (04/11/11 10:59:29)
	 * 
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER Date de création :
	 * (04/11/11 10:59:29)
	 * 
	 * 
	 */
	public String getNOM_PB_VALIDER_IMPRIMER() {
		return "NOM_PB_VALIDER_IMPRIMER";
	}

	private boolean performPB_IMPRIMER(HttpServletRequest request) throws Exception {
		// Récup de la fiche de poste courante
		FichePoste fp = getFichePosteCourant();

		// on verifie si il existe dejà un fichier pour ce contrat dans la BD
		if (verifieExistFichier(fp.getIdFichePoste())) {
			addZone(getNOM_ST_WARNING(),
					"Attention un fichier existe déjà pour cette fiche de poste. Etes-vous sûr de vouloir écraser la version précédente ?");
			// On nomme l'action
			addZone(getNOM_ST_ACTION(), ACTION_IMPRESSION);
			setFocus(getNOM_PB_VALIDER_IMPRIMER());
		} else {
			imprimeModele(request);
		}
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean verifieExistFichier(String idFichePoste) throws Exception {
		// on regarde si le fichier existe
		Document.chercherDocumentByContainsNom(getTransaction(), "FP_" + idFichePoste);
		if (getTransaction().isErreur()) {
			getTransaction().traiterErreur();
			return false;
		}

		return true;
	}

	private boolean imprimeModele(HttpServletRequest request) throws Exception {
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
		String destination = "FP/FP_" + getFichePosteCourant().getIdFichePoste() + ".xml";
		// si le fichier existe alors on supprime l'entrée où il y a le fichier
		// f
		if (verifieExistFichier(getFichePosteCourant().getIdFichePoste())) {
			Document d = Document.chercherDocumentByContainsNom(getTransaction(), "FP_" + getFichePosteCourant().getIdFichePoste());
			LienDocumentAgent l = LienDocumentAgent.chercherLienDocumentAgent(getTransaction(), getAgentCourant().getIdAgent(), d.getIdDocument());
			String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
			File f = new File(repertoireStockage + d.getLienDocument());
			if (f.exists()) {
				f.delete();
			}
			l.supprimerLienDocumentAgent(getTransaction());
			d.supprimerDocument(getTransaction());
		}

		String modele = "ModeleFP.xml";
		String repModeles = (String) ServletAgent.getMesParametres().get("REPERTOIRE_MODELES_FICHEPOSTE");

		// Tout s'est bien passé
		// on crée le document en base de données
		Document d = new Document();
		d.setIdTypeDocument("1");
		d.setLienDocument(destination);
		d.setNomDocument("FP_" + getFichePosteCourant().getIdFichePoste() + ".xml");
		d.setDateDocument(new SimpleDateFormat("dd/MM/yyyy").format(new Date()).toString());
		d.setCommentaire("Document généré par l'application");
		d.creerDocument(getTransaction());

		LienDocumentAgent lda = new LienDocumentAgent();
		lda.setIdAgent(getAgentCourant().getIdAgent());
		lda.setIdDocument(d.getIdDocument());
		lda.creerLienDocumentAgent(getTransaction());

		if (getTransaction().isErreur())
			return false;

		creerModeleDocument(repModeles + modele, repPartage + destination, getFichePosteCourant().getIdFichePoste());

		commitTransaction();

		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		return true;
	}

	private void creerModeleDocument(String modele, String destination, String idFichePoste) throws Exception {
		// on verifie que les repertoires existent
		verifieRepertoire("FP");

		FileSystemManager fsManager = VFS.getManager();
		// LECTURE
		FileObject fo = fsManager.resolveFile(modele);
		InputStream is = fo.getContent().getInputStream();
		InputStreamReader inR = new InputStreamReader(is, "UTF8");
		BufferedReader in = new BufferedReader(inR);

		// ECRITURE
		FileObject destinationFile = fsManager.resolveFile(destination);
		destinationFile.createFile();
		OutputStream os = destinationFile.getContent().getOutputStream();
		OutputStreamWriter ouw = new OutputStreamWriter(os, "UTF8");
		BufferedWriter out = new BufferedWriter(ouw);

		String ligne;
		FichePoste fp = FichePoste.chercherFichePoste(getTransaction(), idFichePoste);

		// requete necessaire
		TitrePoste tp = TitrePoste.chercherTitrePoste(getTransaction(), fp.getIdTitrePoste());
		Grade g = Grade.chercherGrade(getTransaction(), fp.getCodeGrade());
		GradeGenerique gg = GradeGenerique.chercherGradeGenerique(getTransaction(), g.getCodeGradeGenerique());
		FiliereGrade fi = null;
		CadreEmploi cadreEmp = null;
		if (gg != null && gg.getIdCadreEmploi() != null) {
			cadreEmp = CadreEmploi.chercherCadreEmploi(getTransaction(), gg.getIdCadreEmploi());
			if (gg.getCdfili() != null) {
				fi = FiliereGrade.chercherFiliereGrade(getTransaction(), gg.getCdfili());
			}
		}
		EntiteGeo eg = EntiteGeo.chercherEntiteGeo(getTransaction(), fp.getIdEntiteGeo());
		Service s = Service.chercherService(getTransaction(), fp.getIdServi());

		// partie concernant le statut
		String statutFP = Const.CHAINE_VIDE;
		StatutFP statut = StatutFP.chercherStatutFP(getTransaction(), fp.getIdStatutFP());
		statutFP = statut.getLibStatutFP();

		// partie concernant le service
		String lieuPoste = eg.getLibEntiteGeo();
		String libService = s.getLibService();

		// partie concernant le grade,cadre emploi...
		String grade = g.getGrade();
		String categorie = gg.getCodCadre();
		String filiere = Const.CHAINE_VIDE;
		if (fi != null && fi.getLibFiliere() != null) {
			filiere = fi.getLibFiliere();
		}

		String cadreEmploiAffiche = Const.CHAINE_VIDE;
		if (cadreEmp != null && cadreEmp.getIdCadreEmploi() != null) {
			cadreEmploiAffiche = cadreEmp.getLibCadreEmploi();
		}

		NiveauEtudeFP nivEtuFP = NiveauEtudeFP.chercherNiveauEtudeAvecFP(getTransaction(), fp.getIdFichePoste());
		String niveauEtude = Const.CHAINE_VIDE;
		if (nivEtuFP != null && nivEtuFP.getIdNiveauEtude() != null) {
			NiveauEtude nivEtu = NiveauEtude.chercherNiveauEtude(getTransaction(), nivEtuFP.getIdNiveauEtude());
			niveauEtude = nivEtu.getLibNiveauEtude();
		}

		DiplomeFP dipFP = DiplomeFP.chercherDiplomeAvecFP(getTransaction(), fp.getIdFichePoste());
		String diplome = Const.CHAINE_VIDE;
		if (dipFP != null && dipFP.getIdDiplomeGenerique() != null) {
			DiplomeGenerique dip = DiplomeGenerique.chercherDiplomeGenerique(getTransaction(), dipFP.getIdDiplomeGenerique());
			diplome = dip.getLibDiplomeGenerique();
		}

		// partie concernant l'emploi
		String emploiPrimaire = Const.CHAINE_VIDE;
		FicheEmploi ficheEmploiPrimaire = FicheEmploi.chercherFicheEmploiAvecFichePoste(getTransaction(), getFichePosteCourant(), true);
		if (ficheEmploiPrimaire != null && ficheEmploiPrimaire.getRefMairie() != null) {
			emploiPrimaire = ficheEmploiPrimaire.getRefMairie();
		}
		String emploiSecondaire = Const.CHAINE_VIDE;
		FicheEmploi ficheEmploiSecondaire = FicheEmploi.chercherFicheEmploiAvecFichePoste(getTransaction(), getFichePosteCourant(), false);
		if (ficheEmploiSecondaire != null && ficheEmploiSecondaire.getRefMairie() != null) {
			emploiSecondaire = ficheEmploiSecondaire.getRefMairie();
		}
		String budget = fp.getIdBudget() == null ? Const.CHAINE_VIDE : Budget.chercherBudget(getTransaction(), fp.getIdBudget()).getLibBudget();

		// partie concernant le temps de travail du poste
		String reglementaire = Horaire.chercherHoraire(getTransaction(), fp.getIdCdthorReg()).getLibHor();
		String budgete = Horaire.chercherHoraire(getTransaction(), fp.getIdCdthorBud()).getLibHor();

		// partie concernant le poste
		// titulaire
		ArrayList<Affectation> affActives = Affectation.listerAffectationActiveAvecAgent(getTransaction(), getAgentCourant());
		AgentNW agent = null;
		Affectation affAgent = null;
		if (affActives.size() == 1) {
			affAgent = affActives.get(0);
			agent = AgentNW.chercherAgent(getTransaction(), affActives.get(0).getIdAgent());
		}

		String titulaireMatr = Const.CHAINE_VIDE;
		String titulaireNom = "Poste vacant.";
		if (agent != null) {
			String prenomTitulaire = agent.getPrenomAgent().toLowerCase();
			String premLettreTitulaire = prenomTitulaire.substring(0, 1).toUpperCase();
			String restePrenomTitulaire = prenomTitulaire.substring(1, prenomTitulaire.length()).toLowerCase();
			prenomTitulaire = premLettreTitulaire + restePrenomTitulaire;
			String nomTitulaire = agent.getNomAgent().toUpperCase();
			titulaireNom = prenomTitulaire + " " + nomTitulaire;
			titulaireMatr = agent.getNoMatricule();
		}
		String dateAff = Const.CHAINE_VIDE;
		if (affAgent != null) {
			dateAff = affAgent.getDateDebutAff() == null ? Const.CHAINE_VIDE : "Affecté depuis le " + affAgent.getDateDebutAff();
		}
		// responsable hierarchique
		String respFP = Const.CHAINE_VIDE;
		String respTitreFP = Const.CHAINE_VIDE;
		String respMatr = Const.CHAINE_VIDE;
		String respNom = Const.CHAINE_VIDE;
		if (fp.getIdResponsable() != null) {
			FichePoste fpResponsable = FichePoste.chercherFichePoste(getTransaction(), fp.getIdResponsable());
			TitrePoste tpResponsable = TitrePoste.chercherTitrePoste(getTransaction(), fpResponsable.getIdTitrePoste());
			Affectation affResponsable = Affectation.chercherAffectationAvecFP(getTransaction(), fp.getIdResponsable());
			if (affResponsable != null && affResponsable.getIdAgent() != null) {
				AgentNW agentResponsable = AgentNW.chercherAgent(getTransaction(), affResponsable.getIdAgent());
				respMatr = agentResponsable.getNoMatricule();
				String prenomResponsable = agentResponsable.getPrenomAgent().toLowerCase();
				String premLettreResponsable = prenomResponsable.substring(0, 1).toUpperCase();
				String restePrenomResponsable = prenomResponsable.substring(1, prenomResponsable.length()).toLowerCase();
				prenomResponsable = premLettreResponsable + restePrenomResponsable;
				String nom = agentResponsable.getNomAgent().toUpperCase();
				respNom = prenomResponsable + " " + nom;
			}
			respFP = fpResponsable.getNumFP();
			respTitreFP = tpResponsable.getLibTitrePoste();

		}
		// FDP remplacée
		String rempFP = Const.CHAINE_VIDE;
		String rempTitreFP = Const.CHAINE_VIDE;
		String rempMatr = Const.CHAINE_VIDE;
		String rempNom = Const.CHAINE_VIDE;
		if (fp.getIdRemplacement() != null) {
			FichePoste fpRemplacement = FichePoste.chercherFichePoste(getTransaction(), fp.getIdRemplacement());
			TitrePoste tpRemplacement = TitrePoste.chercherTitrePoste(getTransaction(), fpRemplacement.getIdTitrePoste());
			Affectation affRemplacement = Affectation.chercherAffectationAvecFP(getTransaction(), fp.getIdRemplacement());
			if (affRemplacement != null && affRemplacement.getIdAgent() != null) {
				AgentNW agentRemplacement = AgentNW.chercherAgent(getTransaction(), affRemplacement.getIdAgent());
				rempMatr = agentRemplacement.getNoMatricule();
				String prenomRemplacement = agentRemplacement.getPrenomAgent().toLowerCase();
				String premLettreRemplacement = prenomRemplacement.substring(0, 1).toUpperCase();
				String restePrenomRemplacement = prenomRemplacement.substring(1, prenomRemplacement.length()).toLowerCase();
				prenomRemplacement = premLettreRemplacement + restePrenomRemplacement;
				String nom = agentRemplacement.getNomAgent().toUpperCase();
				rempNom = prenomRemplacement + " " + nom;
			}
			rempFP = fpRemplacement.getNumFP();
			rempTitreFP = tpRemplacement.getLibTitrePoste();
		}
		String titrePoste = tp.getLibTitrePoste();

		// partie concernant la mission
		String missions = fp.getMissions();

		// partie concernant les activites
		String activites = Const.CHAINE_VIDE;
		ArrayList<Activite> lActi = Activite.listerActiviteAvecFP(getTransaction(), fp);
		for (Activite acti : lActi) {
			activites += acti.getNomActivite() + "<w:br />";
		}
		if (activites.length() > 8) {
			activites = activites.substring(0, activites.length() - 8);
		}

		// partie concernant les competences
		String competences = Const.CHAINE_VIDE;
		ArrayList<Competence> lComp = Competence.listerCompetenceAvecFP(getTransaction(), fp);
		for (Competence comp : lComp) {
			TypeCompetence tc = TypeCompetence.chercherTypeCompetence(getTransaction(), comp.getIdTypeCompetence());
			competences += comp.getNomCompetence() + " (" + tc.getLibTypeCompetence() + ")<w:br />";
		}
		if (competences.length() > 8) {
			competences = competences.substring(0, competences.length() - 8);
		}

		// Partie concernant les avantages nature
		String natureAvantage = Const.CHAINE_VIDE;
		String libelleAvantage = Const.CHAINE_VIDE;
		String montantAvantage = Const.CHAINE_VIDE;
		// Partie concernant les delegations
		String typeDelegation = Const.CHAINE_VIDE;
		String libelleDelegation = Const.CHAINE_VIDE;
		// Partie concernant les regimes indemnitaires
		String typeRegimeIndemnitaire = Const.CHAINE_VIDE;
		String rubriqueRegimeIndemnitaire = Const.CHAINE_VIDE;
		String montantRegimeIndemnitaire = Const.CHAINE_VIDE;
		String nbPointsRegimeIndemnitaire = Const.CHAINE_VIDE;

		// tant qu'il y a des lignes
		while ((ligne = in.readLine()) != null) {
			// je fais mon traitement
			// statut
			ligne = StringUtils.replace(ligne, "$_STATUT", statutFP);
			// service
			ligne = StringUtils.replace(ligne, "$_LIEU_POSTE", lieuPoste);
			ligne = StringUtils.replace(ligne, "$_SERVICE", libService.replace("&", "et"));
			// cadre emploi,grade..
			ligne = StringUtils.replace(ligne, "$_GRADE_POSTE", grade);
			ligne = StringUtils.replace(ligne, "$_CATEGORIE_POSTE", categorie);
			ligne = StringUtils.replace(ligne, "$_FILIERE_POSTE", filiere);
			ligne = StringUtils.replace(ligne, "$_CADRE_EMPLOI", cadreEmploiAffiche);
			ligne = StringUtils.replace(ligne, "$_NIVEAU_ETUDE", niveauEtude);
			ligne = StringUtils.replace(ligne, "$_DIPLOME", diplome);
			// emploi
			ligne = StringUtils.replace(ligne, "$_FE_PRIMAIRE", emploiPrimaire);
			ligne = StringUtils.replace(ligne, "$_FE_SECONDAIRE", emploiSecondaire);
			ligne = StringUtils.replace(ligne, "$_BUDGET_POSTE", budget);
			ligne = StringUtils.replace(ligne, "$_ANNEE", fp.getAnneeCreation());
			ligne = StringUtils.replace(ligne, "$_NFA", fp.getNFA());
			ligne = StringUtils.replace(ligne, "$_OPI", fp.getOPI() == null ? Const.CHAINE_VIDE : fp.getOPI());
			// temps travail
			ligne = StringUtils.replace(ligne, "$_REGLEMENTAIRE", reglementaire);
			ligne = StringUtils.replace(ligne, "$_BUDGETE", budgete);
			// poste
			ligne = StringUtils.replace(ligne, "$_TITRE_POSTE", titrePoste);
			ligne = StringUtils.replace(ligne, "$_RESP_FP", respFP);
			ligne = StringUtils.replace(ligne, "$_RESP_TITRE_FP", respTitreFP);
			ligne = StringUtils.replace(ligne, "$_RESP_MATR", respMatr);
			ligne = StringUtils.replace(ligne, "$_RESP_NOM", respNom);
			ligne = StringUtils.replace(ligne, "$_REMP_FP", rempFP);
			ligne = StringUtils.replace(ligne, "$_REMP_TITRE_FP", rempTitreFP);
			ligne = StringUtils.replace(ligne, "$_REMP_MATR", rempMatr);
			ligne = StringUtils.replace(ligne, "$_REMP_NOM", rempNom);
			ligne = StringUtils.replace(ligne, "$_TITU_MATR", titulaireMatr);
			ligne = StringUtils.replace(ligne, "$_TITU_NOM", titulaireNom);
			ligne = StringUtils.replace(ligne, "$_DATE_AFF", dateAff);
			// mission
			ligne = StringUtils.replace(ligne, "$_MISSION", missions);
			// activites
			ligne = StringUtils.replace(ligne, "$_ACTIVITE", activites);
			// competences
			ligne = StringUtils.replace(ligne, "$_COMPETENCE", competences);
			// specificites
			ligne = StringUtils.replace(ligne, "$_NATURE_AV", natureAvantage);
			ligne = StringUtils.replace(ligne, "$_LIB_AV", libelleAvantage);
			ligne = StringUtils.replace(ligne, "$_MNT_AV", montantAvantage);
			ligne = StringUtils.replace(ligne, "$_TYPE_DEL", typeDelegation);
			ligne = StringUtils.replace(ligne, "$_LIB_DEL", libelleDelegation);
			ligne = StringUtils.replace(ligne, "$_TYPE_REG", typeRegimeIndemnitaire);
			ligne = StringUtils.replace(ligne, "$_RUBR_REG", rubriqueRegimeIndemnitaire);
			ligne = StringUtils.replace(ligne, "$_MNT_REG", montantRegimeIndemnitaire);
			ligne = StringUtils.replace(ligne, "$_PTS_REG", nbPointsRegimeIndemnitaire);

			ligne = StringUtils.replace(ligne, "$_NUMERO_FP", fp.getNumFP());

			out.write(ligne);
		}

		// FERMETURE DES FLUX
		in.close();
		inR.close();
		is.close();
		fo.close();

		out.close();
		ouw.close();
		os.close();
		destinationFile.close();

		destination = destination.substring(destination.lastIndexOf("/"), destination.length());
		String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_LECTURE");
		setURLFichier(getScriptOuverture(repertoireStockage + "FP" + destination));

	}

	private void setURLFichier(String scriptOuverture) {
		urlFichier = scriptOuverture;
	}

	public String getScriptOuverture(String cheminFichier) throws Exception {
		StringBuffer scriptOuvPDF = new StringBuffer("<script type=\"text/javascript\">");
		scriptOuvPDF.append("window.open('" + cheminFichier + "');");
		scriptOuvPDF.append("</script>");
		return scriptOuvPDF.toString();
	}

	public String getUrlFichier() {
		String res = urlFichier;
		setURLFichier(null);
		if (res == null) {
			return Const.CHAINE_VIDE;
		} else {
			return res;
		}
	}

	private void verifieRepertoire(String codTypeDoc) {
		// on verifie déjà que le repertoire source existe
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");

		File dossierParent = new File(repPartage);
		if (!dossierParent.exists()) {
			dossierParent.mkdir();
		}
		File ssDossier = new File(repPartage + codTypeDoc + "/");
		if (!ssDossier.exists()) {
			ssDossier.mkdir();
		}
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_WARNING Date de création : (16/05/11 09:36:20)
	 * 
	 */
	public String getVAL_ST_WARNING() {
		return getZone(getNOM_ST_WARNING());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_WARNING Date de
	 * création : (16/05/11 09:36:20)
	 * 
	 */
	public String getNOM_ST_WARNING() {
		return "NOM_ST_WARNING";
	}

	/**
	 * @return Renvoie focus.
	 */
	public String getFocus() {
		return focus;
	}

	/**
	 * Met à jour le focus de la JSP.
	 * 
	 * @param focus
	 */
	private void setFocus(String focus) {
		this.focus = focus;
	}

	private String getDiplomeAgt() {
		return diplomeAgt;
	}

	private String getGradeAgt() {
		return gradeAgt;
	}

	private void setGradeAgt(String gradeAgt) {
		this.gradeAgt = gradeAgt;
	}

	private Service getSection() {
		return section;
	}

	private void setSection(Service section) {
		this.section = section;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (08/11/11 16:35:28)
	 * 
	 */
	public String getJSP() {
		return "OeAGENTEmploisPoste.jsp";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SECTION Date de
	 * création : (08/11/11 16:35:28)
	 * 
	 */
	public String getNOM_ST_SECTION() {
		return "NOM_ST_SECTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SECTION Date
	 * de création : (08/11/11 16:35:28)
	 * 
	 */
	public String getVAL_ST_SECTION() {
		return getZone(getNOM_ST_SECTION());
	}

	public ArrayList<Competence> getListeCompetence() {
		if (listeCompetence == null)
			listeCompetence = new ArrayList<Competence>();
		return listeCompetence;
	}

	private void setListeCompetence(ArrayList<Competence> listeCompetence) {
		this.listeCompetence = listeCompetence;
	}

	public TypeCompetence getTypeCompetenceCourant() {
		return typeCompetenceCourant;
	}

	private void setTypeCompetenceCourant(TypeCompetence typeCompetenceCourant) {
		this.typeCompetenceCourant = typeCompetenceCourant;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CHANGER_TYPE Date de
	 * création : (04/07/11 13:57:35)
	 * 
	 */
	public String getNOM_PB_CHANGER_TYPE() {
		return "NOM_PB_CHANGER_TYPE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (04/07/11 13:57:35)
	 * 
	 */
	public boolean performPB_CHANGER_TYPE(HttpServletRequest request) throws Exception {
		setFocus(getNOM_PB_IMPRIMER());
		return true;
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP :
	 * RG_TYPE_COMPETENCE Date de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_RG_TYPE_COMPETENCE() {
		return "NOM_RG_TYPE_COMPETENCE";
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP :
	 * RG_TYPE_COMPETENCE Date de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getVAL_RG_TYPE_COMPETENCE() {
		return getZone(getNOM_RG_TYPE_COMPETENCE());
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TYPE_COMPETENCE_C Date
	 * de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_RB_TYPE_COMPETENCE_C() {
		return "NOM_RB_TYPE_COMPETENCE_C";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TYPE_COMPETENCE_S Date
	 * de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_RB_TYPE_COMPETENCE_S() {
		return "NOM_RB_TYPE_COMPETENCE_S";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_TYPE_COMPETENCE_SF Date
	 * de création : (01/07/11 10:39:25)
	 * 
	 */
	public String getNOM_RB_TYPE_COMPETENCE_SF() {
		return "NOM_RB_TYPE_COMPETENCE_SF";
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_LIB_ACTI Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB_ACTI(int i) {
		return "NOM_ST_LIB_ACTI" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_ACTI Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB_ACTI(int i) {
		return getZone(getNOM_ST_LIB_ACTI(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_LIB_COMP_SF
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB_COMP_SF(int i) {
		return "NOM_ST_LIB_COMP_SF" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_COMPSF
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB_COMP_SF(int i) {
		return getZone(getNOM_ST_LIB_COMP_SF(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_LIB_COMP_S
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB_COMP_S(int i) {
		return "NOM_ST_LIB_COMP_S" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_COMPS Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB_COMP_S(int i) {
		return getZone(getNOM_ST_LIB_COMP_S(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_LIB_COMP_PRO
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB_COMP_PRO(int i) {
		return "NOM_ST_LIB_COMP_PRO" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_COMPPRO
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB_COMP_PRO(int i) {
		return getZone(getNOM_ST_LIB_COMP_PRO(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_AV_TYPE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_AV_TYPE(int i) {
		return "NOM_ST_AV_TYPE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_AV_TYPE(int i) {
		return getZone(getNOM_ST_AV_TYPE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_AV_MNT Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_AV_MNT(int i) {
		return "NOM_ST_AV_MNT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_AV_MNT(int i) {
		return getZone(getNOM_ST_AV_MNT(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_AV_NATURE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_AV_NATURE(int i) {
		return "NOM_ST_AV_NATURE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_AV_NATURE(int i) {
		return getZone(getNOM_ST_AV_NATURE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_DEL_TYPE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DEL_TYPE(int i) {
		return "NOM_ST_DEL_TYPE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DEL_TYPE(int i) {
		return getZone(getNOM_ST_DEL_TYPE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique :
	 * ST_DEL_COMMENTAIRE Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DEL_COMMENTAIRE(int i) {
		return "NOM_ST_DEL_COMMENTAIRE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DEL_COMMENTAIRE(int i) {
		return getZone(getNOM_ST_DEL_COMMENTAIRE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_REG_TYPE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_REG_TYPE(int i) {
		return "NOM_ST_REG_TYPE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_REG_TYPE(int i) {
		return getZone(getNOM_ST_REG_TYPE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_REG_FORFAIT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_REG_FORFAIT(int i) {
		return "NOM_ST_REG_FORFAIT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_REG_FORFAIT(int i) {
		return getZone(getNOM_ST_REG_FORFAIT(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_REG_NB_PTS
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_REG_NB_PTS(int i) {
		return "NOM_ST_REG_NB_PTS" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_REG_NB_PTS(int i) {
		return getZone(getNOM_ST_REG_NB_PTS(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_PP_RUBR Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_PP_RUBR(int i) {
		return "NOM_ST_PP_RUBR" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_PP_RUBR(int i) {
		return getZone(getNOM_ST_PP_RUBR(i));
	}

	public PrimePointageDao getPrimePointageDao() {
		return primePointageDao;
	}

	public void setPrimePointageDao(PrimePointageDao primePointageDao) {
		this.primePointageDao = primePointageDao;
	}

	public PrimePointageFPDao getPrimePointageFPDao() {
		return primePointageFPDao;
	}

	public void setPrimePointageFPDao(PrimePointageFPDao primePointageFPDao) {
		this.primePointageFPDao = primePointageFPDao;
	}

}
