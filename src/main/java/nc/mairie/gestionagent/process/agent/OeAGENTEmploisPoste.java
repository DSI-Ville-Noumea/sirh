package nc.mairie.gestionagent.process.agent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeCompetence;
import nc.mairie.gestionagent.pointage.dto.RefPrimeDto;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.agent.DocumentAgent;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.diplome.DiplomeAgent;
import nc.mairie.metier.parametrage.CadreEmploi;
import nc.mairie.metier.parametrage.NatureAvantage;
import nc.mairie.metier.parametrage.SpecialiteDiplome;
import nc.mairie.metier.parametrage.TitreDiplome;
import nc.mairie.metier.parametrage.TypeAvantage;
import nc.mairie.metier.parametrage.TypeDelegation;
import nc.mairie.metier.parametrage.TypeRegIndemn;
import nc.mairie.metier.poste.Activite;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.Budget;
import nc.mairie.metier.poste.Competence;
import nc.mairie.metier.poste.EntiteGeo;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Horaire;
import nc.mairie.metier.poste.Service;
import nc.mairie.metier.poste.StatutFP;
import nc.mairie.metier.poste.TitrePoste;
import nc.mairie.metier.referentiel.TypeCompetence;
import nc.mairie.metier.specificites.AvantageNature;
import nc.mairie.metier.specificites.Delegation;
import nc.mairie.metier.specificites.PrimePointageAff;
import nc.mairie.metier.specificites.PrimePointageFP;
import nc.mairie.metier.specificites.RegimeIndemnitaire;
import nc.mairie.spring.dao.metier.agent.DocumentAgentDao;
import nc.mairie.spring.dao.metier.agent.DocumentDao;
import nc.mairie.spring.dao.metier.diplome.DiplomeAgentDao;
import nc.mairie.spring.dao.metier.parametrage.CadreEmploiDao;
import nc.mairie.spring.dao.metier.parametrage.NatureAvantageDao;
import nc.mairie.spring.dao.metier.parametrage.SpecialiteDiplomeDao;
import nc.mairie.spring.dao.metier.parametrage.TitreDiplomeDao;
import nc.mairie.spring.dao.metier.parametrage.TypeAvantageDao;
import nc.mairie.spring.dao.metier.parametrage.TypeDelegationDao;
import nc.mairie.spring.dao.metier.parametrage.TypeRegIndemnDao;
import nc.mairie.spring.dao.metier.referentiel.TypeCompetenceDao;
import nc.mairie.spring.dao.metier.specificites.AvantageNatureDao;
import nc.mairie.spring.dao.metier.specificites.DelegationDao;
import nc.mairie.spring.dao.metier.specificites.PrimePointageFPDao;
import nc.mairie.spring.dao.metier.specificites.RegIndemnDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.SirhPtgWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Process OeAGENTEmploisPoste Date de création : (03/08/11 17:03:03)
 * 
 */
public class OeAGENTEmploisPoste extends BasicProcess {

	private Logger logger = LoggerFactory.getLogger(OeAGENTEmploisPoste.class);

	private static final long serialVersionUID = 1L;
	private ArrayList<AvantageNature> listeAvantage;
	private ArrayList<Delegation> listeDelegation;
	private ArrayList<RegimeIndemnitaire> listeRegIndemn;
	private ArrayList<PrimePointageFP> listePrimePointageFP;
	private ArrayList<PrimePointageAff> listePrimePointageAff;
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
	private FichePoste remplacement;
	private AgentNW agtRemplacement;
	private TitrePoste titrePosteRemplacement;

	private String titrePoste;
	private Service service;
	private Service direction;
	private Service section;
	private String localisation;
	private FichePoste responsable;
	private String cadreEmploi;
	private String gradeFP;
	private String gradeAgt;
	private String diplomeAgt;

	private FichePoste superieurHierarchique;
	private AgentNW agtResponsable;
	private TitrePoste titrePosteResponsable;

	public String ACTION_IMPRESSION = "Impression d'un contrat.";
	private String focus = null;
	private String urlFichier;

	private PrimePointageFPDao primePointageFPDao;
	private CadreEmploiDao cadreEmploiDao;
	private NatureAvantageDao natureAvantageDao;
	private SpecialiteDiplomeDao specialiteDiplomeDao;
	private TitreDiplomeDao titreDiplomeDao;
	private TypeAvantageDao typeAvantageDao;
	private TypeDelegationDao typeDelegationDao;
	private TypeRegIndemnDao typeRegIndemnDao;
	private AvantageNatureDao avantageNatureDao;
	private DelegationDao delegationDao;
	private RegIndemnDao regIndemnDao;
	private TypeCompetenceDao typeCompetenceDao;
	private DocumentAgentDao lienDocumentAgentDao;
	private DocumentDao documentDao;
	private DiplomeAgentDao diplomeAgentDao;

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
		initialiseDao();

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

		// Mise à jour de la liste des compétences
		if (getTypeCompetenceCourant() == null || getTypeCompetenceCourant().getIdTypeCompetence() == null) {
			setTypeCompetenceCourant(getTypeCompetenceDao().chercherTypeCompetence(1));
			addZone(getNOM_RG_TYPE_COMPETENCE(), getNOM_RB_TYPE_COMPETENCE_S());
		} else {
			if (getVAL_RG_TYPE_COMPETENCE().equals(getNOM_RB_TYPE_COMPETENCE_S()))
				setTypeCompetenceCourant(getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(
						EnumTypeCompetence.SAVOIR.getValue()));
			if (getVAL_RG_TYPE_COMPETENCE().equals(getNOM_RB_TYPE_COMPETENCE_SF()))
				setTypeCompetenceCourant(getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(
						EnumTypeCompetence.SAVOIR_FAIRE.getValue()));
			if (getVAL_RG_TYPE_COMPETENCE().equals(getNOM_RB_TYPE_COMPETENCE_C()))
				setTypeCompetenceCourant(getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(
						EnumTypeCompetence.COMPORTEMENT.getValue()));
		}

		// Si pas d'affectation en cours
		if (getFichePosteCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			ArrayList<Affectation> affActives = Affectation.listerAffectationActiveAvecAgent(getTransaction(),
					getAgentCourant());
			if (affActives.size() == 0) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR083"));
				return;
			} else if (affActives.size() > 1) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR082"));
				return;
			} else {
				setAffectationCourant((Affectation) affActives.get(0));
				setFichePosteCourant(FichePoste.chercherFichePoste(getTransaction(), getAffectationCourant()
						.getIdFichePoste()));
				if (getAffectationCourant().getIdFichePosteSecondaire() != null) {
					setFichePosteSecondaireCourant(FichePoste.chercherFichePoste(getTransaction(),
							getAffectationCourant().getIdFichePosteSecondaire()));
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

		if (getPrimePointageFPDao() == null) {
			setPrimePointageFPDao(new PrimePointageFPDao((SirhDao) context.getBean("sirhDao")));
		}

		if (getCadreEmploiDao() == null) {
			setCadreEmploiDao(new CadreEmploiDao((SirhDao) context.getBean("sirhDao")));
		}

		if (getNatureAvantageDao() == null) {
			setNatureAvantageDao(new NatureAvantageDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getSpecialiteDiplomeDao() == null) {
			setSpecialiteDiplomeDao(new SpecialiteDiplomeDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTitreDiplomeDao() == null) {
			setTitreDiplomeDao(new TitreDiplomeDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeAvantageDao() == null) {
			setTypeAvantageDao(new TypeAvantageDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeDelegationDao() == null) {
			setTypeDelegationDao(new TypeDelegationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeRegIndemnDao() == null) {
			setTypeRegIndemnDao(new TypeRegIndemnDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAvantageNatureDao() == null) {
			setAvantageNatureDao(new AvantageNatureDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDelegationDao() == null) {
			setDelegationDao(new DelegationDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getRegIndemnDao() == null) {
			setRegIndemnDao(new RegIndemnDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getTypeCompetenceDao() == null) {
			setTypeCompetenceDao(new TypeCompetenceDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getLienDocumentAgentDao() == null) {
			setLienDocumentAgentDao(new DocumentAgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDocumentDao() == null) {
			setDocumentDao(new DocumentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getDiplomeAgentDao() == null) {
			setDiplomeAgentDao(new DiplomeAgentDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	private void alimenterFicheDePoste() throws Exception {
		if (getFichePosteCourant() != null) {
			// Recherche des informations à afficher
			setTitrePoste(getFichePosteCourant().getIdTitrePoste() == null ? Const.CHAINE_VIDE : TitrePoste
					.chercherTitrePoste(getTransaction(), getFichePosteCourant().getIdTitrePoste()).getLibTitrePoste());

			setDirection(Service.getDirection(getTransaction(), getFichePosteCourant().getIdServi()));
			Service serv = Service.getDivision(getTransaction(), getFichePosteCourant().getIdServi());
			if (serv == null)
				serv = Service.chercherService(getTransaction(), getFichePosteCourant().getIdServi());
			setService(serv);
			setSection(Service.getSection(getTransaction(), getFichePosteCourant().getIdServi()));

			setLocalisation(getFichePosteCourant().getIdEntiteGeo() == null ? Const.CHAINE_VIDE : EntiteGeo
					.chercherEntiteGeo(getTransaction(), getFichePosteCourant().getIdEntiteGeo()).getLibEntiteGeo());

			String gradeAffichage = Const.CHAINE_VIDE;
			if (getFichePosteCourant().getCodeGrade() != null) {
				Grade g = Grade.chercherGrade(getTransaction(), getFichePosteCourant().getCodeGrade());
				gradeAffichage = g.getGrade();

				GradeGenerique gg = GradeGenerique.chercherGradeGenerique(getTransaction(), g.getCodeGradeGenerique());
				CadreEmploi cadreEmp = null;
				if (gg != null && gg.getIdCadreEmploi() != null) {
					cadreEmp = getCadreEmploiDao().chercherCadreEmploi(Integer.valueOf(gg.getIdCadreEmploi()));
				}
				setCadreEmploi(cadreEmp == null || cadreEmp.getIdCadreEmploi() == null ? Const.CHAINE_VIDE : cadreEmp
						.getLibCadreEmploi());
			}

			setGradeFP(gradeAffichage);

			// Diplome Agent
			try {
				DiplomeAgent dipl = getDiplomeAgentDao().chercherDernierDiplomeAgentAvecAgent(
						Integer.valueOf(getAgentCourant().getIdAgent()));
				TitreDiplome t = getTitreDiplomeDao().chercherTitreDiplome(dipl.getIdTitreDiplome());
				SpecialiteDiplome s = getSpecialiteDiplomeDao()
						.chercherSpecialiteDiplome(dipl.getIdSpecialiteDiplome());
				setDiplomeAgt(t.getLibTitreDiplome() + " - " + s.getLibSpecialiteDiplome());
			} catch (Exception e) {
				// aucun diplome
			}

			// Carriere
			Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), getAgentCourant());
			if (carr != null) {
				Grade grade = Grade.chercherGrade(getTransaction(), carr.getCodeGrade());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					setGradeAgt(Const.CHAINE_VIDE);
				} else {
					setGradeAgt(grade.getLibGrade());
				}
			}

			// Responsable hiérarchique
			if (getFichePosteCourant() != null && getFichePosteCourant().getIdResponsable() != null) {
				setSuperieurHierarchique(FichePoste.chercherFichePoste(getTransaction(), getFichePosteCourant()
						.getIdResponsable()));
				afficheSuperieurHierarchique();
			}

			// Affiche les zones de la page
			alimenterZones();
			// Affiche les activités
			initialiserActivite();
			// Affiche les compétences
			initialiserCompetence();
			// Affiche les spécificités de la fiche de poste
			initialiserSpecificites();

			// affichage du responsable hierarchique et le remplace
			if (getFichePosteCourant() != null && getFichePosteCourant().getIdResponsable() != null) {
				setResponsable(FichePoste.chercherFichePoste(getTransaction(), getFichePosteCourant()
						.getIdResponsable()));
			} else {
				setResponsable(null);
			}
			afficheResponsable();

			if (getFichePosteCourant() != null && getFichePosteCourant().getIdRemplacement() != null) {
				setRemplacement(FichePoste.chercherFichePoste(getTransaction(), getFichePosteCourant()
						.getIdRemplacement()));
			} else {
				setRemplacement(null);
			}
			afficheRemplacement();
		}
	}

	/**
	 * Récupère les spécificités de la fiche de poste.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void initialiserSpecificites() throws Exception {
		setListeAvantage(null);
		setListeDelegation(null);
		setListeRegIndemn(null);
		setListePrimePointageFP(null);
		setListePrimePointageAff(null);

		// Avantages en nature
		if (getListeAvantage().size() == 0) {
			setListeAvantage(getAvantageNatureDao().listerAvantageNatureAvecFP(
					Integer.valueOf(getFichePosteCourant().getIdFichePoste())));
			if (getTransaction().isErreur()) {
				return;
			}
		}
		int indiceAvantage = 0;
		if (getListeAvantage() != null) {
			for (ListIterator<AvantageNature> list = getListeAvantage().listIterator(); list.hasNext();) {
				AvantageNature aAvNat = (AvantageNature) list.next();
				if (aAvNat != null) {
					TypeAvantage typAv = getTypeAvantageDao().chercherTypeAvantage(aAvNat.getIdTypeAvantage());
					NatureAvantage natAv = getNatureAvantageDao().chercherNatureAvantage(aAvNat.getIdNatureAvantage());

					addZone(getNOM_ST_AV_TYPE(indiceAvantage),
							typAv.getLibTypeAvantage().equals(Const.CHAINE_VIDE) ? "&nbsp;" : typAv
									.getLibTypeAvantage());
					addZone(getNOM_ST_AV_MNT(indiceAvantage), aAvNat.getMontant().toString());
					addZone(getNOM_ST_AV_NATURE(indiceAvantage),
							natAv == null ? "&nbsp;" : natAv.getLibNatureAvantage());
				}
				indiceAvantage++;
			}
		}

		// Délégations
		setListeDelegation((ArrayList<Delegation>) VariablesActivite.recuperer(this,
				VariablesActivite.ACTIVITE_LST_DELEGATION));
		if (getListeDelegation().size() == 0) {
			setListeDelegation(getDelegationDao().listerDelegationAvecFP(
					Integer.valueOf(getFichePosteCourant().getIdFichePoste())));
			if (getTransaction().isErreur()) {
				return;
			}
		}
		int indiceDelegation = 0;
		if (getListeDelegation() != null) {
			for (ListIterator<Delegation> list = getListeDelegation().listIterator(); list.hasNext();) {
				Delegation aDel = (Delegation) list.next();
				if (aDel != null) {
					TypeDelegation typDel = getTypeDelegationDao().chercherTypeDelegation(aDel.getIdTypeDelegation());

					addZone(getNOM_ST_DEL_TYPE(indiceDelegation),
							typDel.getLibTypeDelegation().equals(Const.CHAINE_VIDE) ? "&nbsp;" : typDel
									.getLibTypeDelegation());
					addZone(getNOM_ST_DEL_COMMENTAIRE(indiceDelegation),
							aDel.getLibDelegation().equals(Const.CHAINE_VIDE) ? "&nbsp;" : aDel.getLibDelegation());
				}
				indiceDelegation++;
			}
		}

		// Régimes indemnitaires
		if (getListeRegIndemn().size() == 0) {
			setListeRegIndemn(getRegIndemnDao().listerRegimeIndemnitaireAvecFP(
					Integer.valueOf(getFichePosteCourant().getIdFichePoste())));
			if (getTransaction().isErreur()) {
				return;
			}
		}
		int indiceRegime = 0;
		if (getListeRegIndemn() != null) {
			for (ListIterator<RegimeIndemnitaire> list = getListeRegIndemn().listIterator(); list.hasNext();) {
				RegimeIndemnitaire aReg = (RegimeIndemnitaire) list.next();
				if (aReg != null) {
					TypeRegIndemn typReg = getTypeRegIndemnDao().chercherTypeRegIndemn(aReg.getIdTypeRegIndemn());

					addZone(getNOM_ST_REG_TYPE(indiceRegime),
							typReg.getLibTypeRegIndemn().equals(Const.CHAINE_VIDE) ? "&nbsp;" : typReg
									.getLibTypeRegIndemn());
					addZone(getNOM_ST_REG_FORFAIT(indiceRegime), aReg.getForfait().toString());
					addZone(getNOM_ST_REG_NB_PTS(indiceRegime), aReg.getNombrePoints().toString());
				}
				indiceRegime++;
			}
		}

		// Prime pointage
		SirhPtgWSConsumer t = new SirhPtgWSConsumer();
		ArrayList<RefPrimeDto> listeTotale = new ArrayList<RefPrimeDto>();
		if (getListePrimePointageFP().size() == 0) {
			setListePrimePointageFP(getPrimePointageFPDao().listerPrimePointageFP(
					Integer.valueOf(getFichePosteCourant().getIdFichePoste())));
			for (PrimePointageFP primeFP : getListePrimePointageFP()) {
				try {
					RefPrimeDto rubr = t.getPrimeDetail(primeFP.getNumRubrique());
					listeTotale.add(rubr);
				} catch (Exception e) {
					// TODO a supprimer quand les WS de PTG seront en prod
				}
			}
		}
		int indicePrime = 0;
		for (RefPrimeDto list : listeTotale) {
			addZone(getNOM_ST_PP_RUBR(indicePrime), list.getNumRubrique() + " - " + list.getLibelle());
			indicePrime++;
		}

	}

	/**
	 * Récupère les compétences des activités principales et supplémentaires
	 * choisies.
	 * 
	 * @throws Exception
	 */
	private void initialiserCompetence() throws Exception {
		TypeCompetence savoir = getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(
				EnumTypeCompetence.SAVOIR.getValue());
		TypeCompetence savoirFaire = getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(
				EnumTypeCompetence.SAVOIR_FAIRE.getValue());
		TypeCompetence comportement = getTypeCompetenceDao().chercherTypeCompetenceAvecLibelle(
				EnumTypeCompetence.COMPORTEMENT.getValue());

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
			if (savoir.getIdTypeCompetence().toString().equals(c.getIdTypeCompetence())) {
				if (!getListeSavoir().contains(c))
					getListeSavoir().add(c);
			} else if (savoirFaire.getIdTypeCompetence().toString().equals(c.getIdTypeCompetence())) {
				if (!getListeSavoirFaire().contains(c))
					getListeSavoirFaire().add(c);
			} else if (comportement.getIdTypeCompetence().toString().equals(c.getIdTypeCompetence())) {
				if (!getListeComportementPro().contains(c))
					getListeComportementPro().add(c);
			}
		}

		int indiceCompS = 0;
		if (getListeSavoir() != null) {
			for (int i = 0; i < getListeSavoir().size(); i++) {
				Competence co = (Competence) getListeSavoir().get(i);
				addZone(getNOM_ST_LIB_COMP_S(indiceCompS), co.getNomCompetence().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: co.getNomCompetence());
				indiceCompS++;
			}
		}

		int indiceCompSF = 0;
		if (getListeSavoirFaire() != null) {
			for (int i = 0; i < getListeSavoirFaire().size(); i++) {
				Competence co = (Competence) getListeSavoirFaire().get(i);
				addZone(getNOM_ST_LIB_COMP_SF(indiceCompSF), co.getNomCompetence().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: co.getNomCompetence());
				indiceCompSF++;
			}
		}

		int indiceCompPro = 0;
		if (getListeComportementPro() != null) {
			for (int i = 0; i < getListeComportementPro().size(); i++) {
				Competence co = (Competence) getListeComportementPro().get(i);
				addZone(getNOM_ST_LIB_COMP_PRO(indiceCompPro),
						co.getNomCompetence().equals(Const.CHAINE_VIDE) ? "&nbsp;" : co.getNomCompetence());
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
				addZone(getNOM_ST_LIB_ACTI(indiceActi), acti.getNomActivite().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: acti.getNomActivite());
				indiceActi++;
			}
		}
	}

	public void alimenterZones() throws Exception {
		addZone(getNOM_ST_BUDGET(), getFichePosteCourant().getIdBudget() == null ? Const.CHAINE_VIDE : Budget
				.chercherBudget(getTransaction(), getFichePosteCourant().getIdBudget()).getLibBudget());
		addZone(getNOM_ST_ANNEE(), getFichePosteCourant().getAnneeCreation());
		addZone(getNOM_ST_NUMERO(), getFichePosteCourant().getNumFP());
		addZone(getNOM_ST_NFA(), getFichePosteCourant().getNFA());
		addZone(getNOM_ST_OPI(), getFichePosteCourant().getOPI());
		addZone(getNOM_ST_REGLEMENTAIRE(),
				Horaire.chercherHoraire(getTransaction(), getFichePosteCourant().getIdCdthorReg()).getLibHor());
		addZone(getNOM_ST_POURCENT_BUDGETE(),
				Horaire.chercherHoraire(getTransaction(), getFichePosteCourant().getIdCdthorBud()).getLibHor());
		addZone(getNOM_ST_ACT_INACT(), getFichePosteCourant().getIdStatutFP() == null ? Const.CHAINE_VIDE : StatutFP
				.chercherStatutFP(getTransaction(), getFichePosteCourant().getIdStatutFP()).getLibStatutFP());

		addZone(getNOM_ST_TITRE(), getTitrePoste());
		addZone(getNOM_ST_DIRECTION(), getDirection() == null ? Const.CHAINE_VIDE : getDirection().getLibService());
		addZone(getNOM_ST_SERVICE(), getService() == null ? Const.CHAINE_VIDE : getService().getLibService());
		addZone(getNOM_ST_SECTION(), getSection() == null ? Const.CHAINE_VIDE : getSection().getLibService());

		addZone(getNOM_ST_LOCALISATION(), getLocalisation());
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
	 * Affiche la fiche de poste "Responsable"
	 */
	private void afficheSuperieurHierarchique() {
		if (getFichePosteCourant().getIdResponsable() != null) {
			if (getAgtResponsable() != null) {
				addZone(getNOM_ST_SUPERIEUR_HIERARCHIQUE(), getAgtResponsable().getNomAgent() + " "
						+ getAgtResponsable().getPrenomAgent() + " (" + getAgtResponsable().getNoMatricule() + ") - "
						+ getTitrePosteResponsable().getLibTitrePoste());
			} else {
				addZone(getNOM_ST_SUPERIEUR_HIERARCHIQUE(), "Cette fiche de poste ("
						+ getTitrePosteResponsable().getLibTitrePoste() + ") n'est pas affectée");
			}
		} else {
			addZone(getNOM_ST_SUPERIEUR_HIERARCHIQUE(), Const.CHAINE_VIDE);
		}
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
	 * Retourne pour la JSP le nom de la zone statique : ST_TPS_TRAVAIL_AGT Date
	 * de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getNOM_ST_SUPERIEUR_HIERARCHIQUE() {
		return "NOM_ST_SUPERIEUR_HIERARCHIQUE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_TPS_TRAVAIL_AGT Date de création : (03/08/11 17:03:03)
	 * 
	 */
	public String getVAL_ST_SUPERIEUR_HIERARCHIQUE() {
		return getZone(getNOM_ST_SUPERIEUR_HIERARCHIQUE());
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
	public ArrayList<PrimePointageFP> getListePrimePointageFP() {
		if (listePrimePointageFP == null)
			listePrimePointageFP = new ArrayList<PrimePointageFP>();
		return listePrimePointageFP;
	}

	/**
	 * Met à jour la liste des RegimeIndemnitaire.
	 * 
	 * @param newListePrimePointage
	 *            Nouvelle liste des RegimeIndemnitaire
	 */
	private void setListePrimePointageFP(ArrayList<PrimePointageFP> newListePrimePointage) {
		this.listePrimePointageFP = newListePrimePointage;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire.
	 * 
	 * @return listePrimePointage
	 */
	public ArrayList<PrimePointageAff> getListePrimePointageAff() {
		if (listePrimePointageAff == null)
			listePrimePointageAff = new ArrayList<PrimePointageAff>();
		return listePrimePointageAff;
	}

	/**
	 * Met à jour la liste des RegimeIndemnitaire.
	 * 
	 * @param newListePrimePointage
	 *            Nouvelle liste des RegimeIndemnitaire
	 */
	private void setListePrimePointageAff(ArrayList<PrimePointageAff> newListePrimePointage) {
		this.listePrimePointageAff = newListePrimePointage;
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

	private FichePoste getResponsable() {
		return responsable;
	}

	/**
	 * Setter du responsable.
	 * 
	 * @param responsable
	 *            responsable à définir
	 */
	private void setResponsable(FichePoste responsable) {
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
		try {
			getDocumentDao().chercherDocumentByContainsNom("FP_" + idFichePoste);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	private boolean imprimeModele(HttpServletRequest request) throws Exception {
		// on verifie que les repertoires existent
		verifieRepertoire("FP");

		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
		String destinationFDP = "FP/FP_" + getFichePosteCourant().getIdFichePoste() + ".doc";
		// si le fichier existe alors on supprime l'entrée où il y a le fichier
		if (verifieExistFichier(getFichePosteCourant().getIdFichePoste())) {
			Document d = getDocumentDao().chercherDocumentByContainsNom(
					"FP_" + getFichePosteCourant().getIdFichePoste());
			DocumentAgent l = getLienDocumentAgentDao().chercherDocumentAgent(
					Integer.valueOf(getAgentCourant().getIdAgent()), d.getIdDocument());
			String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ROOT");
			File f = new File(repertoireStockage + d.getLienDocument());
			if (f.exists()) {
				f.delete();
			}
			getLienDocumentAgentDao().supprimerDocumentAgent(l.getIdAgent(), l.getIdDocument());
			getDocumentDao().supprimerDocument(d.getIdDocument());
		}

		try {
			byte[] fileAsBytes = getFDPReportAsByteArray(getFichePosteCourant().getIdFichePoste());

			if (!saveFileToRemoteFileSystem(fileAsBytes, repPartage, destinationFDP)) {
				// "ERR185",
				// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
				return false;
			}

			// Tout s'est bien passé
			// on crée le document en base de données
			Document d = new Document();
			d.setIdTypeDocument(1);
			d.setLienDocument(destinationFDP);
			d.setNomDocument("FP_" + getFichePosteCourant().getIdFichePoste() + ".doc");
			d.setDateDocument(new Date());
			d.setCommentaire("Document généré par l'application");
			Integer id = getDocumentDao().creerDocument(d.getClasseDocument(), d.getNomDocument(), d.getLienDocument(),
					d.getDateDocument(), d.getCommentaire(), d.getIdTypeDocument(), d.getNomOriginal());

			DocumentAgent lda = new DocumentAgent();
			lda.setIdAgent(Integer.valueOf(getAgentCourant().getIdAgent()));
			lda.setIdDocument(id);
			getLienDocumentAgentDao().creerDocumentAgent(lda.getIdAgent(), lda.getIdDocument());

			if (getTransaction().isErreur())
				return false;

			destinationFDP = destinationFDP.substring(destinationFDP.lastIndexOf("/"), destinationFDP.length());
			String repertoireStockage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_LECTURE");
			setURLFichier(getScriptOuverture(repertoireStockage + "FP" + destinationFDP));

			commitTransaction();

		} catch (Exception e) {
			// "ERR185",
			// "Une erreur est survenue dans la génération des documents. Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR185"));
			return false;
		}

		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		return true;
	}

	private void setURLFichier(String scriptOuverture) {
		urlFichier = scriptOuverture;
	}

	public String getScriptOuverture(String cheminFichier) throws Exception {
		StringBuffer scriptOuvPDF = new StringBuffer("<script language=\"JavaScript\" type=\"text/javascript\">");
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

	public FichePoste getSuperieurHierarchique() {
		return superieurHierarchique;
	}

	public void setSuperieurHierarchique(FichePoste superieurHierarchique) throws Exception {
		this.superieurHierarchique = superieurHierarchique;
		if (superieurHierarchique != null) {
			setAgtResponsable(AgentNW.chercherAgentAffecteFichePoste(getTransaction(), getSuperieurHierarchique()
					.getIdFichePoste()));
			setTitrePosteResponsable(TitrePoste.chercherTitrePoste(getTransaction(), getSuperieurHierarchique()
					.getIdTitrePoste()));
		} else {
			setAgtResponsable(null);
			setTitrePosteResponsable(null);
		}
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

	public PrimePointageFPDao getPrimePointageFPDao() {
		return primePointageFPDao;
	}

	public void setPrimePointageFPDao(PrimePointageFPDao primePointageFPDao) {
		this.primePointageFPDao = primePointageFPDao;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_REMPLACEMENT Date de
	 * création : (11/10/11 10:23:53)
	 */
	public String getNOM_ST_REMPLACEMENT() {
		return "NOM_ST_REMPLACEMENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_REMPLACEMENT
	 * Date de création : (11/10/11 10:23:53)
	 */
	public String getVAL_ST_REMPLACEMENT() {
		return getZone(getNOM_ST_REMPLACEMENT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INFO_REMP Date de
	 * création : (29/11/11 16:42:44)
	 */
	public String getNOM_ST_INFO_REMP() {
		return "NOM_ST_INFO_REMP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INFO_REMP Date
	 * de création : (29/11/11 16:42:44)
	 */
	public String getVAL_ST_INFO_REMP() {
		return getZone(getNOM_ST_INFO_REMP());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INFO_RESP Date de
	 * création : (29/11/11 16:42:44)
	 */
	public String getNOM_ST_INFO_RESP() {
		return "NOM_ST_INFO_RESP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INFO_RESP Date
	 * de création : (29/11/11 16:42:44)
	 */
	public String getVAL_ST_INFO_RESP() {
		return getZone(getNOM_ST_INFO_RESP());
	}

	/**
	 * Affiche la fiche de poste "Responsable"
	 */
	private void afficheResponsable() {
		if (getResponsable() != null) {
			addZone(getNOM_ST_RESPONSABLE(), getTitrePosteResponsable().getLibTitrePoste() + " ("
					+ getResponsable().getNumFP() + ")");
			if (getAgtResponsable() != null) {
				addZone(getNOM_ST_INFO_RESP(), getAgtResponsable().getNomAgent() + " "
						+ getAgtResponsable().getPrenomAgent() + " (" + getAgtResponsable().getNoMatricule() + ")");
			} else {
				addZone(getNOM_ST_INFO_RESP(), "Cette fiche de poste n'est pas affectée");
			}
		} else {
			addZone(getNOM_ST_RESPONSABLE(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_INFO_RESP(), Const.CHAINE_VIDE);
		}
	}

	/**
	 * Affiche la fiche de poste "Remplacement"
	 */
	private void afficheRemplacement() {
		if (getRemplacement() != null) {
			addZone(getNOM_ST_REMPLACEMENT(), getTitrePosteRemplacement().getLibTitrePoste() + " ("
					+ getRemplacement().getNumFP() + ")");
			if (getAgtRemplacement() != null) {
				addZone(getNOM_ST_INFO_REMP(), getAgtRemplacement().getNomAgent() + " "
						+ getAgtRemplacement().getPrenomAgent() + " (" + getAgtRemplacement().getNoMatricule() + ")");
			} else {
				addZone(getNOM_ST_INFO_REMP(), "Cette fiche de poste n'est pas affectée");
			}
		} else {
			addZone(getNOM_ST_REMPLACEMENT(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_INFO_REMP(), Const.CHAINE_VIDE);
		}
	}

	/**
	 * Getter de la FichePoste Remplacement.
	 * 
	 * @return FichePoste
	 */
	public FichePoste getRemplacement() {
		return remplacement;
	}

	/**
	 * Setter de la FichePoste Remplacement.
	 * 
	 * @param remp
	 */
	public void setRemplacement(FichePoste remp) throws Exception {
		this.remplacement = remp;
		if (remp != null) {
			setAgtRemplacement(AgentNW.chercherAgentAffecteFichePoste(getTransaction(), getRemplacement()
					.getIdFichePoste()));
			setTitrePosteRemplacement(TitrePoste.chercherTitrePoste(getTransaction(), getRemplacement()
					.getIdTitrePoste()));
		} else {
			setAgtRemplacement(null);
			setTitrePosteRemplacement(null);
		}
	}

	/**
	 * Getter de l'agent responsable.
	 * 
	 * @return agtResponsable
	 */
	private AgentNW getAgtResponsable() {
		return agtResponsable;
	}

	/**
	 * Setter de l'agent responsable.
	 * 
	 * @param agtResponsable
	 */
	private void setAgtResponsable(AgentNW agtResponsable) {
		this.agtResponsable = agtResponsable;
	}

	/**
	 * Getter de l'agent remplacement.
	 * 
	 * @return agtRemplacement
	 */
	private AgentNW getAgtRemplacement() {
		return agtRemplacement;
	}

	/**
	 * Setter de l'agent remplacement.
	 * 
	 * @param agtRemplacement
	 */
	private void setAgtRemplacement(AgentNW agtRemplacement) {
		this.agtRemplacement = agtRemplacement;
	}

	/**
	 * Getter du TitrePoste Remplacement.
	 * 
	 * @return titrePosteRemplacement
	 */
	private TitrePoste getTitrePosteRemplacement() {
		return titrePosteRemplacement;
	}

	/**
	 * Setter du TitrePoste Remplacement.
	 * 
	 * @param titrePosteRemplacement
	 */
	private void setTitrePosteRemplacement(TitrePoste titrePosteRemplacement) {
		this.titrePosteRemplacement = titrePosteRemplacement;
	}

	/**
	 * Getter du TitrePoste Responsable.
	 * 
	 * @return titrePosteResponsable
	 */
	private TitrePoste getTitrePosteResponsable() {
		return titrePosteResponsable;
	}

	/**
	 * Setter du TitrePoste Responsable.
	 * 
	 * @param titrePosteResponsable
	 */
	private void setTitrePosteResponsable(TitrePoste titrePosteResponsable) {
		this.titrePosteResponsable = titrePosteResponsable;
	}

	private byte[] getFDPReportAsByteArray(String idFichePoste) throws Exception {

		ClientResponse response = createAndFireRequest(idFichePoste);

		return readResponseAsByteArray(response);
	}

	public ClientResponse createAndFireRequest(String idFichePoste) {
		String urlWSArretes = (String) ServletAgent.getMesParametres().get("SIRH_WS_URL_FDP_SIRH") + "?idFichePoste="
				+ idFichePoste;

		Client client = Client.create();

		WebResource webResource = client.resource(urlWSArretes);

		ClientResponse response = webResource.get(ClientResponse.class);

		return response;
	}

	public byte[] readResponseAsByteArray(ClientResponse response) throws Exception {

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new Exception(String.format("An error occured ", response.getStatus()));
		}

		byte[] reponseData = null;
		File reportFile = null;

		try {
			reportFile = response.getEntity(File.class);
			reponseData = IOUtils.toByteArray(new FileInputStream(reportFile));
		} catch (Exception e) {
			throw new Exception("An error occured while reading the downloaded report.", e);
		} finally {
			if (reportFile != null && reportFile.exists())
				reportFile.delete();
		}

		return reponseData;
	}

	public boolean saveFileToRemoteFileSystem(byte[] fileAsBytes, String chemin, String filename) throws Exception {

		BufferedOutputStream bos = null;
		FileObject docFile = null;

		try {
			FileSystemManager fsManager = VFS.getManager();
			docFile = fsManager.resolveFile(String.format("%s", chemin + filename));
			bos = new BufferedOutputStream(docFile.getContent().getOutputStream());
			IOUtils.write(fileAsBytes, bos);
			IOUtils.closeQuietly(bos);

			if (docFile != null) {
				try {
					docFile.close();
				} catch (FileSystemException e) {
					// ignore the exception
				}
			}
		} catch (Exception e) {
			logger.error(String.format("An error occured while writing the report file to the following path  : "
					+ chemin + filename + " : " + e));
			return false;
		}
		return true;
	}

	public CadreEmploiDao getCadreEmploiDao() {
		return cadreEmploiDao;
	}

	public void setCadreEmploiDao(CadreEmploiDao cadreEmploiDao) {
		this.cadreEmploiDao = cadreEmploiDao;
	}

	public NatureAvantageDao getNatureAvantageDao() {
		return natureAvantageDao;
	}

	public void setNatureAvantageDao(NatureAvantageDao natureAvantageDao) {
		this.natureAvantageDao = natureAvantageDao;
	}

	public SpecialiteDiplomeDao getSpecialiteDiplomeDao() {
		return specialiteDiplomeDao;
	}

	public void setSpecialiteDiplomeDao(SpecialiteDiplomeDao specialiteDiplomeDao) {
		this.specialiteDiplomeDao = specialiteDiplomeDao;
	}

	public TitreDiplomeDao getTitreDiplomeDao() {
		return titreDiplomeDao;
	}

	public void setTitreDiplomeDao(TitreDiplomeDao titreDiplomeDao) {
		this.titreDiplomeDao = titreDiplomeDao;
	}

	public TypeAvantageDao getTypeAvantageDao() {
		return typeAvantageDao;
	}

	public void setTypeAvantageDao(TypeAvantageDao typeAvantageDao) {
		this.typeAvantageDao = typeAvantageDao;
	}

	public TypeDelegationDao getTypeDelegationDao() {
		return typeDelegationDao;
	}

	public void setTypeDelegationDao(TypeDelegationDao typeDelegationDao) {
		this.typeDelegationDao = typeDelegationDao;
	}

	public TypeRegIndemnDao getTypeRegIndemnDao() {
		return typeRegIndemnDao;
	}

	public void setTypeRegIndemnDao(TypeRegIndemnDao typeRegIndemnDao) {
		this.typeRegIndemnDao = typeRegIndemnDao;
	}

	public AvantageNatureDao getAvantageNatureDao() {
		return avantageNatureDao;
	}

	public void setAvantageNatureDao(AvantageNatureDao avantageNatureDao) {
		this.avantageNatureDao = avantageNatureDao;
	}

	public DelegationDao getDelegationDao() {
		return delegationDao;
	}

	public void setDelegationDao(DelegationDao delegationDao) {
		this.delegationDao = delegationDao;
	}

	public RegIndemnDao getRegIndemnDao() {
		return regIndemnDao;
	}

	public void setRegIndemnDao(RegIndemnDao regIndemnDao) {
		this.regIndemnDao = regIndemnDao;
	}

	public TypeCompetenceDao getTypeCompetenceDao() {
		return typeCompetenceDao;
	}

	public void setTypeCompetenceDao(TypeCompetenceDao typeCompetenceDao) {
		this.typeCompetenceDao = typeCompetenceDao;
	}

	public DocumentAgentDao getLienDocumentAgentDao() {
		return lienDocumentAgentDao;
	}

	public void setLienDocumentAgentDao(DocumentAgentDao lienDocumentAgentDao) {
		this.lienDocumentAgentDao = lienDocumentAgentDao;
	}

	public DocumentDao getDocumentDao() {
		return documentDao;
	}

	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}

	public DiplomeAgentDao getDiplomeAgentDao() {
		return diplomeAgentDao;
	}

	public void setDiplomeAgentDao(DiplomeAgentDao diplomeAgentDao) {
		this.diplomeAgentDao = diplomeAgentDao;
	}
}
