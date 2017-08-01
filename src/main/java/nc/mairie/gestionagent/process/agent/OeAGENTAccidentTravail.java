package nc.mairie.gestionagent.process.agent;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.oreilly.servlet.MultipartRequest;

import flexjson.JSONSerializer;
import nc.mairie.comparator.DemandeDtoDateDeclarationComparator;
import nc.mairie.enums.EnumEtatAbsence;
import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.enums.EnumTypeGroupeAbsence;
import nc.mairie.gestionagent.absence.dto.DemandeDto;
import nc.mairie.gestionagent.absence.dto.PieceJointeDto;
import nc.mairie.gestionagent.absence.dto.RefTypeDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.MSDateTransformer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.noumea.spring.service.AbsService;
import nc.noumea.spring.service.IAbsService;
import nc.noumea.spring.service.IRadiService;
import nc.noumea.spring.service.RadiService;

/**
 * Process OeAGENTAccidentTravail Date de création : (30/06/11 13:56:32)
 * 
 */
public class OeAGENTAccidentTravail extends BasicProcess {

	/**
	 * 
	 */
	private static final long				serialVersionUID			= 1L;
	public static final int					STATUT_RECHERCHER_AGENT		= 1;

	private Logger							logger						= LoggerFactory.getLogger(OeAGENTAccidentTravail.class);

	private SimpleDateFormat				sdf							= new SimpleDateFormat("dd/MM/yyyy");

	private String[]						LB_SIEGE_LESION;
	private String[]						LB_AVIS_COMMISSION;
	private String[]						LB_TYPE_AT;
	private String[]						LB_TYPE_MP;
	private String[]						LB_AT_REFERENCE;

	private Agent							agentCourant;
	private DemandeDto						demandeCourant;
	private PieceJointeDto					documentCourant;

	private ArrayList<RefTypeDto>			listeTypeAT;
	private ArrayList<RefTypeDto>			listeTypeMP;
	private ArrayList<RefTypeDto>			listeSiegeLesion;
	private ArrayList<DemandeDto>			listeATReference;
	private ArrayList<DemandeDto>			listeAT_MP;

	private Hashtable<Integer, RefTypeDto>	hashTypeAT;
	private Hashtable<Integer, RefTypeDto>	hashSiegeLesion;
	private Hashtable<Integer, RefTypeDto>	hashTypeMP;
	private Hashtable<Integer, DemandeDto>	hashATReference;

	public String							ACTION_CONSULTATION			= "Consultation d'une fiche";
	private String							ACTION_MODIFICATION			= "Modification d'une fiche";

	public String							ACTION_DOCUMENT				= "Documents d'une fiche";
	public String							ACTION_DOCUMENT_SUPPRESSION	= "Suppression d'un document d'une fiche";
	public String							ACTION_DOCUMENT_CREATION	= "Création d'un document d'une fiche";
	public boolean							isImporting					= false;
	public MultipartRequest					multi						= null;
	public File								fichierUpload				= null;

	private IAbsService						absService;
	private IRadiService					radiService;
	private AgentDao						agentDao;

	/**
	 * Constructeur du process OeAGENTAccidentTravail. Date de création :
	 * (30/06/11 13:56:32)
	 * 
	 */
	public OeAGENTAccidentTravail() {
		super();
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (30/06/11 14:19:10)
	 * 
	 * RG_AG_AT_C01 RG_AG_AT_C02
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setDemandeCourant(null);
			multi = null;
			isImporting = false;
		}

		// Vérification des droits d'acces.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a
			// cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();

		// Si hashtable des types d'accident de travail vide
		// RG_AG_AT_C01
		if (getHashTypeAT().size() == 0) {
			setListeTypeAT((ArrayList<RefTypeDto>) getAbsService().getRefTypeAccidentTravail());

			if (getListeTypeAT().size() != 0) {
				int tailles[] = { 40 };
				String padding[] = { "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for (ListIterator<RefTypeDto> list = getListeTypeAT().listIterator(); list.hasNext();) {
					RefTypeDto m = (RefTypeDto) list.next();
					String ligne[] = { m.getLibelle() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_TYPE_AT(aFormat.getListeFormatee(true));
			} else {
				setLB_TYPE_AT(null);
			}

			// remplissage de la hashTable
			for (int i = 0; i < listeTypeAT.size(); i++) {
				RefTypeDto t = (RefTypeDto) listeTypeAT.get(i);
				getHashTypeAT().put(t.getIdRefType(), t);
			}
		}

		// Si hashtable des types de maladie pro vide
		// RG_AG_AT_C01
		if (getHashTypeMP().size() == 0) {
			setListeTypeMP((ArrayList<RefTypeDto>) getAbsService().getRefTypeMaladiePro());

			if (getListeTypeMP().size() != 0) {
				int tailles[] = { 40 };
				String padding[] = { "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for (ListIterator<RefTypeDto> list = getListeTypeMP().listIterator(); list.hasNext();) {
					RefTypeDto m = (RefTypeDto) list.next();
					String ligne[] = { m.getLibelle() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_TYPE_MP(aFormat.getListeFormatee(true));
			} else {
				setLB_TYPE_MP(null);
			}

			// remplissage de la hashTable
			for (int i = 0; i < listeTypeMP.size(); i++) {
				RefTypeDto t = (RefTypeDto) listeTypeMP.get(i);
				getHashTypeMP().put(t.getIdRefType(), t);
			}
		}

		// Si hashtable des sieges de lésion vide
		// RG_AG_AT_C02

		if (getHashSiegeLesion().size() == 0) {
			setListeSiegeLesion((ArrayList<RefTypeDto>) getAbsService().getRefTypeSiegeLesion());

			if (getListeSiegeLesion().size() != 0) {
				int tailles[] = { 40 };
				String padding[] = { "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for (ListIterator<RefTypeDto> list = getListeSiegeLesion().listIterator(); list.hasNext();) {
					RefTypeDto m = (RefTypeDto) list.next();
					String ligne[] = { m.getLibelle() };

					aFormat.ajouteLigne(ligne);
				}
				setLB_SIEGE_LESION(aFormat.getListeFormatee(true));
			} else {
				setLB_SIEGE_LESION(null);
			}

			// remplissage de la hashTable
			for (int i = 0; i < listeSiegeLesion.size(); i++) {
				RefTypeDto s = (RefTypeDto) listeSiegeLesion.get(i);
				getHashSiegeLesion().put(s.getIdRefType(), s);
			}
		}

		// avis commission
		if (null == getLB_AVIS_COMMISSION() || 2 > getLB_AVIS_COMMISSION().length) {
			int tailles[] = { 40 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			String ligneAccepte[] = { "Accepté" };
			aFormat.ajouteLigne(ligneAccepte);
			String ligneRefus[] = { "Refus" };
			aFormat.ajouteLigne(ligneRefus);
			setLB_AVIS_COMMISSION(aFormat.getListeFormatee(true));
		}

		// Si agentCourant vide
		if (getAgentCourant() == null || MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			Agent aAgent = (Agent) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				initialiseListeAT_MP(request);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}

		// AT de référence
		if (getHashATReference().size() == 0) {

			List<DemandeDto> listeATReference = absService.getListeDemandesAgent(getAgentCourant().getIdAgent(), "TOUTES", null, null,
					null, Arrays.asList(EnumEtatAbsence.VALIDEE.getCode(), EnumEtatAbsence.PRISE.getCode()).toString().replace("[", "")
							.replace("]", "").replace(" ", ""),
					EnumTypeAbsence.MALADIES_ACCIDENT_TRAVAIL.getCode(), EnumTypeGroupeAbsence.MALADIES.getValue());

			if (null != listeATReference) {
				int tailles[] = { 40 };
				String padding[] = { "G" };
				FormateListe aFormat = new FormateListe(tailles, padding, false);
				for (DemandeDto atReference : listeATReference) {
					if (null != atReference.getTypeSiegeLesion()) {
						RefTypeDto s = getHashSiegeLesion().get(atReference.getTypeSiegeLesion().getIdRefType());
						String ligne[] = { sdf.format(atReference.getDateDeclaration()) + " - " + s.getLibelle() };
						aFormat.ajouteLigne(ligne);
					}
					getHashATReference().put(atReference.getIdDemande(), atReference);
				}
				setLB_AT_REFERENCE(aFormat.getListeFormatee(true));
				setListeATReference((ArrayList<DemandeDto>) listeATReference);
			}
		}
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAbsService() == null) {
			setAbsService((AbsService) context.getBean("absService"));
		}
		if (getRadiService() == null) {
			setRadiService((RadiService) context.getBean("radiService"));
		}
	}

	/**
	 * Initialisation de la liste des accidents du travail et maladie pro de
	 * l'agent courant Date de création : 12/11/2015
	 */
	private void initialiseListeAT_MP(HttpServletRequest request) throws Exception {
		// Recherche des accidents du travail de l'agent
		ArrayList<DemandeDto> listeATAnnulee = (ArrayList<DemandeDto>) absService.getListeDemandes(null, null,
				EnumEtatAbsence.ANNULEE.getCode().toString(), EnumTypeAbsence.MALADIES_ACCIDENT_TRAVAIL.getCode(), getAgentCourant().getIdAgent(),
				EnumTypeGroupeAbsence.MALADIES.getValue(), false, null);
		
		ArrayList<DemandeDto> listeATValidee = (ArrayList<DemandeDto>) absService.getListeDemandes(null, null,
				EnumEtatAbsence.VALIDEE.getCode().toString(), EnumTypeAbsence.MALADIES_ACCIDENT_TRAVAIL.getCode(), getAgentCourant().getIdAgent(),
				EnumTypeGroupeAbsence.MALADIES.getValue(), false, null);

		ArrayList<DemandeDto> listeATPrise = (ArrayList<DemandeDto>) absService.getListeDemandes(null, null,
				EnumEtatAbsence.PRISE.getCode().toString(), EnumTypeAbsence.MALADIES_ACCIDENT_TRAVAIL.getCode(), getAgentCourant().getIdAgent(),
				EnumTypeGroupeAbsence.MALADIES.getValue(), false, null);

		ArrayList<DemandeDto> listeRechuteValidee = (ArrayList<DemandeDto>) absService.getListeDemandes(null, null,
				EnumEtatAbsence.VALIDEE.getCode().toString(), EnumTypeAbsence.MALADIES_RECHUTE.getCode(), getAgentCourant().getIdAgent(),
				EnumTypeGroupeAbsence.MALADIES.getValue(), false, null);

		ArrayList<DemandeDto> listeRechutePrise = (ArrayList<DemandeDto>) absService.getListeDemandes(null, null,
				EnumEtatAbsence.PRISE.getCode().toString(), EnumTypeAbsence.MALADIES_RECHUTE.getCode(), getAgentCourant().getIdAgent(),
				EnumTypeGroupeAbsence.MALADIES.getValue(), false, null);

		ArrayList<DemandeDto> listeMPValidee = (ArrayList<DemandeDto>) absService.getListeDemandes(null, null,
				EnumEtatAbsence.VALIDEE.getCode().toString(), EnumTypeAbsence.MALADIES_PROFESSIONNELLE.getCode(), getAgentCourant().getIdAgent(),
				EnumTypeGroupeAbsence.MALADIES.getValue(), false, null);

		ArrayList<DemandeDto> listeMPPrise = (ArrayList<DemandeDto>) absService.getListeDemandes(null, null,
				EnumEtatAbsence.PRISE.getCode().toString(), EnumTypeAbsence.MALADIES_PROFESSIONNELLE.getCode(), getAgentCourant().getIdAgent(),
				EnumTypeGroupeAbsence.MALADIES.getValue(), false, null);

		// #40735 : Les prolongations d'AT ne doivent pas apparaître dans la liste
		ArrayList<DemandeDto> listeAllATSansProlongation = new ArrayList<>();
		for (DemandeDto demande : listeATAnnulee) {
			if (!demande.isProlongation())
				listeAllATSansProlongation.add(demande);
		}
		for (DemandeDto demande : listeATPrise) {
			if (!demande.isProlongation())
				listeAllATSansProlongation.add(demande);
		}
		for (DemandeDto demande : listeATValidee) {
			if (!demande.isProlongation())
				listeAllATSansProlongation.add(demande);
		}

		ArrayList<DemandeDto> listeAT_MP = new ArrayList<DemandeDto>();
		listeAT_MP.addAll(listeAllATSansProlongation);
		listeAT_MP.addAll(listeRechuteValidee);
		listeAT_MP.addAll(listeRechutePrise);
		listeAT_MP.addAll(listeMPValidee);
		listeAT_MP.addAll(listeMPPrise);

		Collections.sort(listeAT_MP, new DemandeDtoDateDeclarationComparator());
		setListeAT_MP(listeAT_MP);

		int indiceAcc = 0;
		if (getListeAT_MP() != null) {
			for (int i = 0; i < getListeAT_MP().size(); i++) {
				DemandeDto demande = (DemandeDto) getListeAT_MP().get(i);

				if ((demande.getIdTypeDemande().equals(EnumTypeAbsence.MALADIES_ACCIDENT_TRAVAIL.getCode())
						|| demande.getIdTypeDemande().equals(EnumTypeAbsence.MALADIES_RECHUTE.getCode())) && null != demande.getTypeAccidentTravail()
						&& null != demande.getTypeAccidentTravail().getIdRefType()) {
					RefTypeDto t = (RefTypeDto) getHashTypeAT().get(demande.getTypeAccidentTravail().getIdRefType());
					addZone(getNOM_ST_TYPE(indiceAcc), t.getLibelle().equals(Const.CHAINE_VIDE) ? "&nbsp;" : t.getLibelle());
				}

				if (demande.getTypeSaisi().isSiegeLesion()) {
					RefTypeDto s = null != demande.getTypeSiegeLesion()
							? (RefTypeDto) getHashSiegeLesion().get(demande.getTypeSiegeLesion().getIdRefType()) : null;
					addZone(getNOM_ST_SIEGE(indiceAcc), null == s || s.getLibelle().equals(Const.CHAINE_VIDE) ? "&nbsp;" : s.getLibelle());
				}

				if (demande.getTypeSaisi().isMaladiePro()) {
					RefTypeDto t = (RefTypeDto) getHashTypeMP().get(demande.getTypeMaladiePro().getIdRefType());
					addZone(getNOM_ST_TYPE(indiceAcc), t.getLibelle().equals(Const.CHAINE_VIDE) ? "&nbsp;" : t.getLibelle());
				}

				// #39863 : Affichage des AT Annulés
				String typeDemande = absService.getTypeDemande(demande);
				if (demande.getIdRefEtat().equals(EnumEtatAbsence.ANNULEE.getCode()))
					typeDemande += " (Refusé)";
				
				addZone(getNOM_ST_AT_MP(indiceAcc), typeDemande);
				addZone(getNOM_ST_DATE(indiceAcc), null == demande.getDateDeclaration() ? "" : sdf.format(demande.getDateDeclaration()));
				addZone(getNOM_ST_DATE_DEBUT(indiceAcc), null == demande.getDateDebut() ? "" : sdf.format(demande.getDateDebut()));
				addZone(getNOM_ST_DATE_FIN(indiceAcc), null == demande.getDateFin() ? "" : sdf.format(demande.getDateFin()));
				addZone(getNOM_ST_RECHUTE(indiceAcc), demande.getIdTypeDemande().equals(EnumTypeAbsence.MALADIES_RECHUTE.getCode()) ? "X" : "&nbsp;");
				addZone(getNOM_ST_NB_JOURS(indiceAcc), demande.getNombreITT() == null ? "&nbsp;" : demande.getNombreITT().toString());
				addZone(getNOM_ST_NB_DOC(indiceAcc), null == demande.getPiecesJointes() || demande.getPiecesJointes().size() == 0 ? "&nbsp;"
						: String.valueOf(demande.getPiecesJointes().size()));

				indiceAcc++;
			}
		}
	}

	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION())) {
			setStatut(STATUT_PROCESS_APPELANT);
		} else {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
			setStatut(STATUT_MEME_PROCESS);
		}
		return true;
	}

	/**
	 * Initialise les zones de saisie du formulaire de modification d'un
	 * accident de travail Date de création : 11/07/01
	 */
	private boolean initialiseATCourant(HttpServletRequest request) throws Exception {

		addZone(getNOM_ST_TYPE_DEMANDE(), getDemandeCourant().getLibelleTypeDemande());

		if (getDemandeCourant().getIdTypeDemande().equals(EnumTypeAbsence.MALADIES_ACCIDENT_TRAVAIL.getCode())
				|| getDemandeCourant().getIdTypeDemande().equals(EnumTypeAbsence.MALADIES_RECHUTE.getCode())) {

			addZone(getNOM_RG_TYPE_AT_MP(), getNOM_RB_AT());

			if (null != getDemandeCourant().getTypeAccidentTravail() && null != getDemandeCourant().getTypeAccidentTravail().getIdRefType()) {
				RefTypeDto typeAT = (RefTypeDto) getHashTypeAT().get(getDemandeCourant().getTypeAccidentTravail().getIdRefType());
				int ligneType = getListeTypeAT().indexOf(typeAT);
				addZone(getNOM_LB_TYPE_SELECT(), String.valueOf(ligneType + 1));
				addZone(getNOM_ST_TYPE(), typeAT.getLibelle());
			} else {
				addZone(getNOM_LB_TYPE_SELECT(), String.valueOf(0));
				addZone(getNOM_ST_TYPE(), "");
			}
		}

		if (getDemandeCourant().getTypeSaisi().isSiegeLesion() && null != getDemandeCourant().getTypeSiegeLesion()) {
			RefTypeDto siegeLesion = (RefTypeDto) getHashSiegeLesion().get(getDemandeCourant().getTypeSiegeLesion().getIdRefType());
			int ligneSiege = getListeSiegeLesion().indexOf(siegeLesion);
			addZone(getNOM_LB_SIEGE_LESION_SELECT(), String.valueOf(ligneSiege + 1));
			addZone(getNOM_ST_SIEGE_LESION(), siegeLesion.getLibelle());
		} else {
			addZone(getNOM_LB_SIEGE_LESION_SELECT(), String.valueOf(0));
			addZone(getNOM_ST_SIEGE_LESION(), "");
		}

		if (getDemandeCourant().getTypeSaisi().isAtReference() && null != getDemandeCourant().getAccidentTravailReference()
				&& null != getDemandeCourant().getAccidentTravailReference().getIdDemande()) {
			DemandeDto atReference = (DemandeDto) getHashATReference().get(getDemandeCourant().getAccidentTravailReference().getIdDemande());
			int ligneAtReference = getListeATReference().indexOf(atReference);
			addZone(getNOM_LB_AT_REFERENCE_SELECT(), String.valueOf(ligneAtReference + 1));
			RefTypeDto s = getHashSiegeLesion().get(atReference.getTypeSiegeLesion().getIdRefType());
			addZone(getNOM_LB_AT_REFERENCE(), sdf.format(atReference.getDateDeclaration()) + " - " + s.getLibelle());
		}

		if (getDemandeCourant().getTypeSaisi().isMaladiePro() && null != getDemandeCourant().getTypeMaladiePro()) {
			RefTypeDto typeMP = (RefTypeDto) getHashTypeMP().get(getDemandeCourant().getTypeMaladiePro().getIdRefType());

			int ligneType = getListeTypeMP().indexOf(typeMP);
			addZone(getNOM_LB_TYPE_SELECT(), String.valueOf(ligneType + 1));
			addZone(getNOM_ST_TYPE(), typeMP.getLibelle());
		}

		if (getDemandeCourant().getIdTypeDemande().equals(EnumTypeAbsence.MALADIES_PROFESSIONNELLE.getCode())) {
			addZone(getNOM_EF_DATE_COMMISSION_APTITUDE(),
					null == getDemandeCourant().getDateCommissionAptitude() ? "" : sdf.format(getDemandeCourant().getDateCommissionAptitude()));
			addZone(getNOM_EF_DATE_TRANSMISSION_CAFAT(),
					null == getDemandeCourant().getDateTransmissionCafat() ? "" : sdf.format(getDemandeCourant().getDateTransmissionCafat()));
			addZone(getNOM_EF_DATE_DECISION_CAFAT(),
					null == getDemandeCourant().getDateDecisionCafat() ? "" : sdf.format(getDemandeCourant().getDateDecisionCafat()));
			addZone(getNOM_EF_TAUX_CAFAT(), null != getDemandeCourant().getTauxCafat() ? getDemandeCourant().getTauxCafat().toString() : "");
		}

		addZone(getNOM_LB_AVIS_COMMISSION(),
				(null != getDemandeCourant().getAvisCommissionAptitude() && getDemandeCourant().getAvisCommissionAptitude()) ? "Accepté"
						: (null != getDemandeCourant().getAvisCommissionAptitude() && !getDemandeCourant().getAvisCommissionAptitude() ? "Refus"
								: ""));
		Boolean avisCommission = getDemandeCourant().getAvisCommissionAptitude();
		String avisCommissionLB = null;
		if (null == avisCommission) {
			avisCommissionLB = "-1";
		} else if (avisCommission) {
			avisCommissionLB = "1";
		} else {
			avisCommissionLB = "2";
		}

		addZone(getNOM_LB_AVIS_COMMISSION_SELECT(), avisCommissionLB);

		// Alim zones
		if (getDemandeCourant().getTypeSaisi().isDateDeclaration()) {
			addZone(getNOM_EF_DATE(),
					getDemandeCourant().getDateDeclaration() == null ? Const.CHAINE_VIDE : sdf.format(getDemandeCourant().getDateDeclaration()));
		}
		if (getDemandeCourant().getTypeSaisi().isCalendarDateDebut()) {
			addZone(getNOM_EF_DATE_DEBUT(),
					getDemandeCourant().getDateDebut() == null ? Const.CHAINE_VIDE : sdf.format(getDemandeCourant().getDateDebut()));
		}
		if (getDemandeCourant().getTypeSaisi().isCalendarDateFin()) {
			addZone(getNOM_EF_DATE_FIN(),
					getDemandeCourant().getDateFin() == null ? Const.CHAINE_VIDE : sdf.format(getDemandeCourant().getDateFin()));
		}
		if (getDemandeCourant().getTypeSaisi().isNombreITT()) {
			addZone(getNOM_EF_NB_JOUR_ITT(),
					getDemandeCourant().getNombreITT() == null ? Const.CHAINE_VIDE : getDemandeCourant().getNombreITT().toString());
		}
		if (getDemandeCourant().getTypeSaisi().isPrescripteur()) {
			addZone(getNOM_EF_PRESCRIPTEUR(),
					getDemandeCourant().getPrescripteur() == null ? Const.CHAINE_VIDE : getDemandeCourant().getPrescripteur().toString());
		}
		if (getDemandeCourant().getTypeSaisi().isMotif()) {
			addZone(getNOM_EF_MOTIF(), getDemandeCourant().getCommentaire() == null ? Const.CHAINE_VIDE : getDemandeCourant().getCommentaire());
		}

		return true;
	}

	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/*
	 * RG_AG_AT_A02
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {

		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		// Vérification de la validité du formulaire
		if (!performControlerChamps(request)) {
			return false;
		}

		if (getDemandeCourant().getIdTypeDemande().equals(EnumTypeAbsence.MALADIES_ACCIDENT_TRAVAIL.getCode())
				|| getDemandeCourant().getIdTypeDemande().equals(EnumTypeAbsence.MALADIES_RECHUTE.getCode())) {

			int numLigneType = (Services.estNumerique(getZone(getNOM_LB_TYPE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_TYPE_SELECT())) : -1);
			int numLigneSiege = (Services.estNumerique(getZone(getNOM_LB_SIEGE_LESION_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_SIEGE_LESION_SELECT())) : -1);

			if (numLigneType == -1 || numLigneType == 0 || getListeTypeAT().size() == 0 || numLigneType > getListeTypeAT().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "types"));
				return false;
			}

			if (numLigneSiege == -1 || numLigneSiege == 0 || getListeSiegeLesion().size() == 0 || numLigneSiege > getListeSiegeLesion().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "siege des lésions"));
				return false;
			}
			RefTypeDto typeAt = (RefTypeDto) getListeTypeAT().get(numLigneType - 1);
			RefTypeDto typeSiege = (RefTypeDto) getListeSiegeLesion().get(numLigneSiege - 1);
			
			getDemandeCourant().setTypeAccidentTravail(typeAt);
			getDemandeCourant().setTypeSiegeLesion(typeSiege);
			
			if (!Services.estUneDate(getZone(getNOM_EF_DATE()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de déclaration"));
				return false;
			}
			getDemandeCourant().setDateDeclaration(sdf.parse(getZone(getNOM_EF_DATE())));
		}

		if (getDemandeCourant().getIdTypeDemande().equals(EnumTypeAbsence.MALADIES_PROFESSIONNELLE.getCode())) {

			String dateTransmissionCafat = getZone(getNOM_EF_DATE_TRANSMISSION_CAFAT()).equals(Const.CHAINE_VIDE) ? null
					: Services.formateDate(getZone(getNOM_EF_DATE_TRANSMISSION_CAFAT()));

			String dateDecisionCafat = getZone(getNOM_EF_DATE_DECISION_CAFAT()).equals(Const.CHAINE_VIDE) ? null
					: Services.formateDate(getZone(getNOM_EF_DATE_DECISION_CAFAT()));

			String dateTransmissionAptitude = getZone(getNOM_EF_DATE_COMMISSION_APTITUDE()).equals(Const.CHAINE_VIDE) ? null
					: Services.formateDate(getZone(getNOM_EF_DATE_COMMISSION_APTITUDE()));

			String tauxPrisEnChargeCafat = getZone(getNOM_EF_TAUX_CAFAT());

			getDemandeCourant().setDateTransmissionCafat(
					null != dateTransmissionCafat && !"".equals(dateTransmissionCafat) ? sdf.parse(dateTransmissionCafat) : null);
			getDemandeCourant()
					.setDateDecisionCafat(null != dateDecisionCafat && !"".equals(dateDecisionCafat) ? sdf.parse(dateDecisionCafat) : null);
			getDemandeCourant().setDateCommissionAptitude(
					null != dateTransmissionAptitude && !"".equals(dateTransmissionAptitude) ? sdf.parse(dateTransmissionAptitude) : null);
			getDemandeCourant()
					.setTauxCafat(null != tauxPrisEnChargeCafat && !"".equals(tauxPrisEnChargeCafat) ? new Double(tauxPrisEnChargeCafat) : null);
		}

		// commentaire
		String commentaire = getZone(getNOM_EF_MOTIF()).equals(Const.CHAINE_VIDE) ? null : getZone(getNOM_EF_MOTIF());
		getDemandeCourant().setCommentaire(commentaire);

		// avis commission
		Integer numLigneAvisCommission = (Services.estNumerique(getZone(getNOM_LB_AVIS_COMMISSION_SELECT()))
				? Integer.parseInt(getZone(getNOM_LB_AVIS_COMMISSION_SELECT())) : -1);
		Boolean avisCommission = numLigneAvisCommission == 2 ? Boolean.FALSE : (numLigneAvisCommission == 1 ? Boolean.TRUE : null);
		getDemandeCourant().setAvisCommissionAptitude(avisCommission);

		getDemandeCourant().setFromHSCT(true);

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(getDemandeCourant());

		ReturnMessageDto srm = absService.saveDemande(getAgentConnecte(request).getIdAgent(), json);

		if (srm.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : srm.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur("ERREUR : " + err);
			return false;
		}
		if (srm.getInfos().size() > 0) {
			String info = Const.CHAINE_VIDE;
			for (String erreur : srm.getInfos()) {
				info += " " + erreur;
			}
			getTransaction().declarerErreur(info);
		}

		// On a fini l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Tout s'est bien passé
		initialiseListeAT_MP(request);

		return true;
	}

	private Agent getAgentConnecte(HttpServletRequest request) throws Exception {
		Agent agent = null;

		UserAppli uUser = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		// on fait la correspondance entre le login et l'agent via RADI
		LightUserDto user = getRadiService().getAgentCompteADByLogin(uUser.getUserName());
		if (user == null) {
			getTransaction().traiterErreur();
			// "Votre login ne nous permet pas de trouver votre identifiant.
			// Merci de contacter le responsable du projet."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
			return null;
		} else {
			if (user != null && user.getEmployeeNumber() != null && user.getEmployeeNumber() != 0) {
				try {
					agent = getAgentDao().chercherAgentParMatricule(getRadiService().getNomatrWithEmployeeNumber(user.getEmployeeNumber()));
				} catch (Exception e) {
					// "Votre login ne nous permet pas de trouver votre
					// identifiant. Merci de contacter le responsable du
					// projet."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR183"));
					return null;
				}
			}
		}

		return agent;
	}

	/**
	 * Vérifie les regles de gestion de saisie (champs obligatoires, ...) du
	 * formulaire d'accident du travail
	 * 
	 * @param request
	 * @return true si les regles de gestion sont respectées. false sinon.
	 * @throws Exception
	 *             RG_AG_AT_A01
	 */
	public boolean performControlerChamps(HttpServletRequest request) throws Exception {

		// taux Cafat
		if (getDemandeCourant().getIdTypeDemande().equals(EnumTypeAbsence.MALADIES_PROFESSIONNELLE.getCode())) {
			if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_TAUX_CAFAT())) && !Services.estFloat(getZone(getNOM_EF_TAUX_CAFAT()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "taux de pris en charge Cafat"));
				return false;
			}

			if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_TRANSMISSION_CAFAT()))
					&& !Services.estUneDate(getZone(getNOM_EF_DATE_TRANSMISSION_CAFAT()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "date de transmission Cafat"));
				return false;
			}

			if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_DECISION_CAFAT()))
					&& !Services.estUneDate(getZone(getNOM_EF_DATE_DECISION_CAFAT()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "date de décision Cafat"));
				return false;
			}

			if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_COMMISSION_APTITUDE()))
					&& !Services.estUneDate(getZone(getNOM_EF_DATE_COMMISSION_APTITUDE()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "date de transmission à la commission d'aptitude"));
				return false;
			}
		}

		return true;
	}

	public String getNomEcran() {
		return "ECR-AG-HSCT-ACCTRAVAIL";
	}

	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup de l'AT courant
		setDemandeCourant((DemandeDto) getListeAT_MP().get(indiceEltAModifier));

		// init du diplome courant
		if (!initialiseATCourant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_CONSULTER(int i) {
		return "NOM_PB_CONSULTER" + i;
	}

	public boolean performPB_CONSULTER(HttpServletRequest request, int indiceEltAConsulter) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup de l'AT courant
		setDemandeCourant((DemandeDto) getListeAT_MP().get(indiceEltAConsulter));

		// init
		if (!initialiseATCourant(request))
			return false;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CONSULTATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_DOCUMENT(int i) {
		return "NOM_PB_DOCUMENT" + i;
	}

	public boolean performPB_DOCUMENT(HttpServletRequest request, int indiceEltDocument) throws Exception {

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup de l'AT courant
		setDemandeCourant((DemandeDto) getListeAT_MP().get(indiceEltDocument));

		// init des documents AT de l'agent
		initialiseListeDocuments(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);
		addZone(getNOM_ST_NOM_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMENTAIRE_DOC(), Const.CHAINE_VIDE);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private void initialiseListeDocuments(HttpServletRequest request) throws Exception {
		// Recherche des documents de l'agent
		int indiceActeVM = 0;
		if (getDemandeCourant().getPiecesJointes() != null) {
			for (int i = 0; i < getDemandeCourant().getPiecesJointes().size(); i++) {
				PieceJointeDto doc = (PieceJointeDto) getDemandeCourant().getPiecesJointes().get(i);

				addZone(getNOM_ST_NOM_DOC(indiceActeVM), doc.getTitre().equals(Const.CHAINE_VIDE) ? "&nbsp;" : doc.getTitre());
				addZone(getNOM_ST_COMMENTAIRE(indiceActeVM),
						null == doc.getCommentaire() || doc.getCommentaire().equals(Const.CHAINE_VIDE) ? "&nbsp;" : doc.getCommentaire());
				addZone(getNOM_ST_DATE_DOC(indiceActeVM), null == doc.getDateModification() ? "&nbsp;" : sdf.format(doc.getDateModification()));

				indiceActeVM++;
			}
		}
	}

	public boolean performPB_CREER_DOC(HttpServletRequest request) throws Exception {

		// init des documents courant
		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);

		isImporting = true;

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT_CREATION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public boolean performPB_SUPPRIMER_DOC(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// Récup du Diplome courant
		PieceJointeDto doc = (PieceJointeDto) getDemandeCourant().getPiecesJointes().get(indiceEltASuprimer);

		// init des documents courant
		if (!initialiseDocumentSuppression(request, doc))
			return false;

		setDocumentCourant(doc);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT_SUPPRESSION);

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean initialiseDocumentSuppression(HttpServletRequest request, PieceJointeDto doc) throws Exception {

		// Alim zones
		addZone(getNOM_ST_NOM_DOC(), doc.getTitre());

		return true;
	}

	private boolean performPB_VALIDER_DOCUMENT_SUPPRESSION(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		getDemandeCourant().getPiecesJointes().remove(getDocumentCourant());

		getDemandeCourant().setFromHSCT(true);

		String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(getDemandeCourant());

		ReturnMessageDto srm = absService.saveDemande(getAgentConnecte(request).getIdAgent(), json);

		if (srm.getErrors().size() > 0) {
			String err = Const.CHAINE_VIDE;
			for (String erreur : srm.getErrors()) {
				err += " " + erreur;
			}
			getTransaction().declarerErreur("ERREUR : " + err);
			return false;
		}
		if (srm.getInfos().size() > 0) {
			String info = Const.CHAINE_VIDE;
			for (String erreur : srm.getInfos()) {
				info += " " + erreur;
			}
			getTransaction().declarerErreur(info);
		}
		logger.debug("Document supprime de la demande");

		// Alim zones
		addZone(getNOM_ST_NOM_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_DATE_DOC(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMENTAIRE_DOC(), Const.CHAINE_VIDE);

		initialiseListeDocuments(request);
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);

		return true;
	}

	public boolean performPB_VALIDER_DOCUMENT_CREATION(HttpServletRequest request) throws Exception {
		// on sauvegarde le nom du fichier parcourir
		if (multi.getFile(getNOM_EF_LIENDOCUMENT()) != null) {
			fichierUpload = multi.getFile(getNOM_EF_LIENDOCUMENT());
		}
		// Controle des champs
		if (!performControlerSaisieDocument(request))
			return false;

		if (!creeDocument(request, getDemandeCourant())) {
			return false;
		}

		addZone(getNOM_ST_WARNING(), Const.CHAINE_VIDE);

		initialiseListeAT_MP(request);

		for (DemandeDto demande : getListeAT_MP()) {
			if (demande.getIdDemande().equals(getDemandeCourant().getIdDemande())) {
				setDemandeCourant(demande);
				break;
			}
		}

		initialiseListeDocuments(request);
		addZone(getNOM_ST_ACTION(), ACTION_DOCUMENT);

		return true;
	}

	private boolean creeDocument(HttpServletRequest request, DemandeDto demande) throws Exception {
		// on recupere le fichier mis dans le repertoire temporaire
		if (fichierUpload == null) {
			getTransaction().declarerErreur("Err : le nom de fichier est incorrect");
			return false;
		}

		// on sauvegarde le nom du fichier parcourir
		if (multi != null) {
			if (multi.getFile(getNOM_EF_LIENDOCUMENT()) != null) {
				File file = multi.getFile(getNOM_EF_LIENDOCUMENT());

				DemandeDto demandeDto = getDemandeCourant();
				demandeDto.setFromHSCT(true);

				FileInputStream in = new FileInputStream(file);
				try {
					byte[] byteBuffer = new byte[in.available()];
					in.read(byteBuffer);
					PieceJointeDto pj = new PieceJointeDto();
					pj.setbFile(byteBuffer);
					pj.setTypeFile(new MimetypesFileTypeMap().getContentType(file));

					String commentaire = getZone(getNOM_EF_COMMENTAIRE());
					if (null != commentaire && !"".equals(commentaire)) {
						pj.setCommentaire(commentaire);
					}

					demandeDto.getPiecesJointes().add(pj);
				} finally {
					in.close();
				}

				String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(demandeDto);
				ReturnMessageDto srm = absService.saveDemande(getAgentConnecte(request).getIdAgent(), json);

				if (srm.getErrors().size() > 0) {
					String err = Const.CHAINE_VIDE;
					for (String erreur : srm.getErrors()) {
						err += " " + erreur;
					}
					getTransaction().declarerErreur("ERREUR : " + err);
					return false;
				}

				// si pas d'erreur
				// on met a jour la demande courante
				setDemandeCourant(demandeDto);

				if (srm.getInfos().size() > 0) {
					String info = Const.CHAINE_VIDE;
					for (String erreur : srm.getInfos()) {
						info += " " + erreur;
					}
					getTransaction().declarerErreur(info);
				}
				logger.debug("Document ajouté a la demande");
			}
		}

		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);

		// on supprime le fichier temporaire
		fichierUpload.delete();
		isImporting = false;
		fichierUpload = null;

		return true;
	}

	private boolean performControlerSaisieDocument(HttpServletRequest request) throws Exception {
		addZone(getNOM_EF_LIENDOCUMENT(), fichierUpload != null ? fichierUpload.getPath() : Const.CHAINE_VIDE);
		addZone(getNOM_EF_COMMENTAIRE(), multi.getParameter(getNOM_EF_COMMENTAIRE()));

		boolean result = true;
		// parcourir
		if (fichierUpload == null || fichierUpload.getPath().equals(Const.CHAINE_VIDE)) {
			// ERR002:La zone parcourir est obligatoire.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "parcourir"));
			result &= false;
		}
		return result;
	}

	public boolean testerParametre(HttpServletRequest request, String param) {
		return (request.getParameter(param) != null || request.getParameter(param + ".x") != null
				|| (multi != null && multi.getParameter(param) != null));
	}

	public boolean recupererPreControles(HttpServletRequest request) throws Exception {
		String type = request.getHeader("Content-Type");
		String repTemp = (String) ServletAgent.getMesParametres().get("REPERTOIRE_TEMP");
		@SuppressWarnings("unused")
		String JSP = null;
		multi = null;

		if (type != null && type.indexOf("multipart/form-data") != -1) {
			request.setCharacterEncoding("UTF-8");
			multi = new MultipartRequest(request, repTemp, 10 * 1024 * 1024, "UTF-8");
			JSP = multi.getParameter("JSP");
		} else {
			JSP = request.getParameter("JSP");
		}
		return true;
	}

	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		String JSP = null;
		if (null == request.getParameter("JSP")) {
			if (multi != null) {
				JSP = multi.getParameter("JSP");
			}
		} else {
			JSP = request.getParameter("JSP");
		}

		// Si on arrive de la JSP alors on traite le get
		if (JSP != null && JSP.equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeAT_MP().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_CONSULTER
			for (int i = 0; i < getListeAT_MP().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
			}

			// Si clic sur le bouton PB_DOCUMENT
			for (int i = 0; i < getListeAT_MP().size(); i++) {
				if (testerParametre(request, getNOM_PB_DOCUMENT(i))) {
					return performPB_DOCUMENT(request, i);
				}
			}

			// Si clic sur le bouton PB_CREER_DOC
			if (testerParametre(request, getNOM_PB_CREER_DOC())) {
				return performPB_CREER_DOC(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_DOC
			for (int i = 0; i < getDemandeCourant().getPiecesJointes().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_DOC(i))) {
					return performPB_SUPPRIMER_DOC(request, i);
				}
			}

			// Si clic sur le bouton PB_VALIDER_DOCUMENT_SUPPRESSION
			if (testerParametre(request, getNOM_PB_VALIDER_DOCUMENT_SUPPRESSION())) {
				return performPB_VALIDER_DOCUMENT_SUPPRESSION(request);
			}

			// Si clic sur le bouton PB_VALIDER_DOCUMENT_CREATION
			if (testerParametre(request, getNOM_PB_VALIDER_DOCUMENT_CREATION())) {
				return performPB_VALIDER_DOCUMENT_CREATION(request);
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getJSP() {
		return "OeAGENTAccidentTravail.jsp";
	}

	public String getNOM_ST_DATE(int i) {
		return "NOM_ST_DATE" + i;
	}

	public String getVAL_ST_DATE(int i) {
		return getZone(getNOM_ST_DATE(i));
	}

	public String getNOM_ST_DATE_DEBUT(int i) {
		return "NOM_ST_DATE_DEBUT" + i;
	}

	public String getVAL_ST_DATE_DEBUT(int i) {
		return getZone(getNOM_ST_DATE_DEBUT(i));
	}

	public String getNOM_ST_DATE_FIN(int i) {
		return "NOM_ST_DATE_FIN" + i;
	}

	public String getVAL_ST_DATE_FIN(int i) {
		return getZone(getNOM_ST_DATE_FIN(i));
	}

	public String getNOM_ST_AT_MP(int i) {
		return "NOM_ST_AT_MP" + i;
	}

	public String getVAL_ST_AT_MP(int i) {
		return getZone(getNOM_ST_AT_MP(i));
	}

	public String getNOM_ST_RECHUTE(int i) {
		return "NOM_ST_RECHUTE" + i;
	}

	public String getVAL_ST_RECHUTE(int i) {
		return getZone(getNOM_ST_RECHUTE(i));
	}

	public String getNOM_ST_NB_JOURS(int i) {
		return "NOM_ST_NB_JOURS" + i;
	}

	public String getVAL_ST_NB_JOURS(int i) {
		return getZone(getNOM_ST_NB_JOURS(i));
	}

	public String getNOM_ST_TYPE(int i) {
		return "NOM_ST_TYPE" + i;
	}

	public String getVAL_ST_TYPE(int i) {
		return getZone(getNOM_ST_TYPE(i));
	}

	public String getNOM_ST_SIEGE(int i) {
		return "NOM_ST_SIEGE" + i;
	}

	public String getVAL_ST_SIEGE(int i) {
		return getZone(getNOM_ST_SIEGE(i));
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	public String getNOM_ST_NOM_ORI_DOC(int i) {
		return "NOM_ST_NOM_ORI_DOC" + i;
	}

	public String getVAL_ST_NOM_ORI_DOC(int i) {
		return getZone(getNOM_ST_NOM_ORI_DOC(i));
	}

	public String getNOM_ST_NOM_DOC(int i) {
		return "NOM_ST_NOM_DOC" + i;
	}

	public String getVAL_ST_NOM_DOC(int i) {
		return getZone(getNOM_ST_NOM_DOC(i));
	}

	public String getNOM_ST_TYPE_DOC(int i) {
		return "NOM_ST_TYPE_DOC" + i;
	}

	public String getVAL_ST_TYPE_DOC(int i) {
		return getZone(getNOM_ST_TYPE_DOC(i));
	}

	public String getNOM_ST_DATE_DOC(int i) {
		return "NOM_ST_DATE_DOC" + i;
	}

	public String getVAL_ST_DATE_DOC(int i) {
		return getZone(getNOM_ST_DATE_DOC(i));
	}

	public String getNOM_ST_COMMENTAIRE(int i) {
		return "NOM_ST_COMMENTAIRE" + i;
	}

	public String getVAL_ST_COMMENTAIRE(int i) {
		return getZone(getNOM_ST_COMMENTAIRE(i));
	}

	public String getNOM_ST_NB_DOC(int i) {
		return "NOM_ST_NB_DOC" + i;
	}

	public String getVAL_ST_NB_DOC(int i) {
		return getZone(getNOM_ST_NB_DOC(i));
	}

	public String getNOM_PB_CREER_DOC() {
		return "NOM_PB_CREER_DOC";
	}

	public String getNOM_PB_CONSULTER_DOC(int i) {
		return "NOM_PB_CONSULTER_DOC" + i;
	}

	public String getNOM_PB_SUPPRIMER_DOC(int i) {
		return "NOM_PB_SUPPRIMER_DOC" + i;
	}

	public String getVAL_ST_NOM_DOC() {
		return getZone(getNOM_ST_NOM_DOC());
	}

	public String getNOM_ST_NOM_DOC() {
		return "NOM_ST_NOM_DOC";
	}

	public String getVAL_ST_DATE_DOC() {
		return getZone(getNOM_ST_DATE_DOC());
	}

	public String getNOM_ST_DATE_DOC() {
		return "NOM_ST_DATE_DOC";
	}

	public String getVAL_ST_COMMENTAIRE_DOC() {
		return getZone(getNOM_ST_COMMENTAIRE_DOC());
	}

	public String getNOM_ST_COMMENTAIRE_DOC() {
		return "NOM_ST_COMMENTAIRE_DOC";
	}

	public String getNOM_PB_VALIDER_DOCUMENT_SUPPRESSION() {
		return "NOM_PB_VALIDER_DOCUMENT_SUPPRESSION";
	}

	public String getNOM_EF_COMMENTAIRE() {
		return "NOM_EF_COMMENTAIRE";
	}

	public String getVAL_EF_COMMENTAIRE() {
		return getZone(getNOM_EF_COMMENTAIRE());
	}

	public String getNOM_EF_LIENDOCUMENT() {
		return "NOM_EF_LIENDOCUMENT";
	}

	public String getVAL_EF_LIENDOCUMENT() {
		return getZone(getNOM_EF_LIENDOCUMENT());
	}

	public String getNOM_PB_VALIDER_DOCUMENT_CREATION() {
		return "NOM_PB_VALIDER_DOCUMENT_CREATION";
	}

	public String getNOM_ST_WARNING() {
		return "NOM_ST_WARNING";
	}

	public String getVAL_ST_WARNING() {
		return getZone(getNOM_ST_WARNING());
	}

	public String getVAL_ST_NOM_ORI_DOC() {
		return getZone(getNOM_ST_NOM_ORI_DOC());
	}

	public String getNOM_ST_NOM_ORI_DOC() {
		return "NOM_ST_NOM_ORI_DOC";
	}

	public ArrayList<RefTypeDto> getListeTypeMP() {
		return listeTypeMP;
	}

	public void setListeTypeMP(ArrayList<RefTypeDto> listeTypeMP) {
		this.listeTypeMP = listeTypeMP;
	}

	public ArrayList<DemandeDto> getListeAT_MP() {

		if (null == listeAT_MP) {
			return new ArrayList<DemandeDto>();
		}

		return listeAT_MP;
	}

	public void setListeAT_MP(ArrayList<DemandeDto> listeAT_MP) {
		this.listeAT_MP = listeAT_MP;
	}

	public String getNOM_ST_DATE() {
		return "NOM_ST_DATE";
	}

	public String getVAL_ST_DATE() {
		return getZone(getNOM_ST_DATE());
	}

	public String getNOM_ST_NB_JOUR_ITT() {
		return "NOM_ST_NB_JOUR_ITT";
	}

	public String getVAL_ST_NB_JOUR_ITT() {
		return getZone(getNOM_ST_NB_JOUR_ITT());
	}

	public String getNOM_ST_SIEGE_LESION() {
		return "NOM_ST_SIEGE_LESION";
	}

	public String getVAL_ST_SIEGE_LESION() {
		return getZone(getNOM_ST_SIEGE_LESION());
	}

	public String getNOM_ST_TYPE() {
		return "NOM_ST_TYPE";
	}

	public String getVAL_ST_TYPE() {
		return getZone(getNOM_ST_TYPE());
	}

	public String getNOM_EF_DATE() {
		return "NOM_EF_DATE";
	}

	public String getVAL_EF_DATE() {
		return getZone(getNOM_EF_DATE());
	}

	public String getNOM_EF_NB_JOUR_ITT() {
		return "NOM_EF_NB_JOUR_ITT";
	}

	public String getVAL_EF_NB_JOUR_ITT() {
		return getZone(getNOM_EF_NB_JOUR_ITT());
	}

	public String getNOM_EF_TAUX_CAFAT() {
		return "NOM_EF_TAUX_CAFAT";
	}

	public String getVAL_EF_TAUX_CAFAT() {
		return getZone(getNOM_EF_TAUX_CAFAT());
	}

	private String[] getLB_SIEGE_LESION() {
		if (LB_SIEGE_LESION == null)
			LB_SIEGE_LESION = initialiseLazyLB();
		return LB_SIEGE_LESION;
	}

	private void setLB_SIEGE_LESION(String[] newLB_SIEGE_LESION) {
		LB_SIEGE_LESION = newLB_SIEGE_LESION;
	}

	public String getNOM_LB_SIEGE_LESION() {
		return "NOM_LB_SIEGE_LESION";
	}

	public String getNOM_LB_SIEGE_LESION_SELECT() {
		return "NOM_LB_SIEGE_LESION_SELECT";
	}

	public String[] getVAL_LB_SIEGE_LESION() {
		return getLB_SIEGE_LESION();
	}

	public String getVAL_LB_SIEGE_LESION_SELECT() {
		return getZone(getNOM_LB_SIEGE_LESION_SELECT());
	}

	public String[] getLB_AVIS_COMMISSION() {
		if (LB_AVIS_COMMISSION == null)
			LB_AVIS_COMMISSION = initialiseLazyLB();
		return LB_AVIS_COMMISSION;
	}

	private void setLB_AVIS_COMMISSION(String[] newLB_AVIS_COMMISSION) {
		LB_AVIS_COMMISSION = newLB_AVIS_COMMISSION;
	}

	public String getNOM_LB_AVIS_COMMISSION() {
		return "NOM_LB_AVIS_COMMISSION";
	}

	public String getNOM_LB_AVIS_COMMISSION_SELECT() {
		return "NOM_LB_AVIS_COMMISSION_SELECT";
	}

	public String getVAL_LB_AVIS_COMMISSION() {
		return getZone(getNOM_LB_AVIS_COMMISSION());
	}

	public String getVAL_LB_AVIS_COMMISSION_SELECT() {
		return getZone(getNOM_LB_AVIS_COMMISSION_SELECT());
	}

	private String[] getLB_TYPE_AT() {
		if (LB_TYPE_AT == null)
			LB_TYPE_AT = initialiseLazyLB();
		return LB_TYPE_AT;
	}

	private void setLB_TYPE_AT(String[] newLB_TYPE_AT) {
		LB_TYPE_AT = newLB_TYPE_AT;
	}

	public String[] getVAL_LB_TYPE_AT() {
		return getLB_TYPE_AT();
	}

	private String[] getLB_TYPE_MP() {
		if (LB_TYPE_MP == null)
			LB_TYPE_MP = initialiseLazyLB();
		return LB_TYPE_MP;
	}

	private void setLB_TYPE_MP(String[] newLB_TYPE_MP) {
		LB_TYPE_MP = newLB_TYPE_MP;
	}

	public String getNOM_LB_TYPE() {
		return "NOM_LB_TYPE";
	}

	public String[] getVAL_LB_TYPE_MP() {
		return getLB_TYPE_MP();
	}

	public String getNOM_LB_TYPE_SELECT() {
		return "NOM_LB_TYPE_SELECT";
	}

	public String getVAL_LB_TYPE_SELECT() {
		return getZone(getNOM_LB_TYPE_SELECT());
	}

	public String[] getLB_AT_REFERENCE() {
		if (LB_AT_REFERENCE == null)
			LB_AT_REFERENCE = initialiseLazyLB();
		return LB_AT_REFERENCE;
	}

	private void setLB_AT_REFERENCE(String[] newLB_AT_REFERENCE) {
		LB_AT_REFERENCE = newLB_AT_REFERENCE;
	}

	public String getNOM_LB_AT_REFERENCE() {
		return "NOM_LB_AT_REFERENCE";
	}

	public String getVAL_LB_AT_REFERENCE() {
		return getZone(getNOM_LB_AT_REFERENCE());
	}

	public String getNOM_LB_AT_REFERENCE_SELECT() {
		return "NOM_LB_AT_REFERENCE_SELECT";
	}

	public String getVAL_LB_AT_REFERENCE_SELECT() {
		return getZone(getNOM_LB_AT_REFERENCE_SELECT());
	}

	public String getNOM_RG_TYPE_AT_MP() {
		return "NOM_RG_TYPE_AT_MP";
	}

	public String getNOM_RB_AT() {
		return "NOM_RB_AT";
	}

	public String getNOM_RB_MP() {
		return "NOM_RB_MP";
	}

	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	public String getNOM_ST_TYPE_DEMANDE() {
		return "NOM_ST_TYPE_DEMANDE";
	}

	public String getVAL_ST_TYPE_DEMANDE() {
		return getZone(getNOM_ST_TYPE_DEMANDE());
	}

	public String getNOM_PB_SELECT_AT() {
		return "NOM_PB_SELECT_AT";
	}

	public String getNOM_PB_SELECT_RECHUTE() {
		return "NOM_PB_SELECT_RECHUTE";
	}

	public String getNOM_PB_SELECT_MP() {
		return "NOM_PB_SELECT_MP";
	}

	public DemandeDto getDemandeCourant() {
		return demandeCourant;
	}

	public void setDemandeCourant(DemandeDto demandeCourant) {
		this.demandeCourant = demandeCourant;
	}

	public Agent getAgentCourant() {
		return agentCourant;
	}

	private void setAgentCourant(Agent agentCourant) {
		this.agentCourant = agentCourant;
	}

	private Hashtable<Integer, RefTypeDto> getHashSiegeLesion() {
		if (hashSiegeLesion == null) {
			hashSiegeLesion = new Hashtable<Integer, RefTypeDto>();
		}
		return hashSiegeLesion;
	}

	private Hashtable<Integer, RefTypeDto> getHashTypeAT() {
		if (hashTypeAT == null) {
			hashTypeAT = new Hashtable<Integer, RefTypeDto>();
		}
		return hashTypeAT;
	}

	private Hashtable<Integer, DemandeDto> getHashATReference() {
		if (hashATReference == null) {
			hashATReference = new Hashtable<Integer, DemandeDto>();
		}
		return hashATReference;
	}

	public ArrayList<DemandeDto> getListeATReference() {
		return listeATReference;
	}

	private void setListeATReference(ArrayList<DemandeDto> listeATReference) {
		this.listeATReference = listeATReference;
	}

	public Hashtable<Integer, RefTypeDto> getHashTypeMP() {
		if (hashTypeMP == null) {
			hashTypeMP = new Hashtable<Integer, RefTypeDto>();
		}
		return hashTypeMP;
	}

	public void setHashTypeMP(Hashtable<Integer, RefTypeDto> hashTypeMP) {
		this.hashTypeMP = hashTypeMP;
	}

	private ArrayList<RefTypeDto> getListeSiegeLesion() {
		return listeSiegeLesion;
	}

	private void setListeSiegeLesion(ArrayList<RefTypeDto> listeSiegeLesion) {
		this.listeSiegeLesion = listeSiegeLesion;
	}

	private ArrayList<RefTypeDto> getListeTypeAT() {
		return listeTypeAT;
	}

	private void setListeTypeAT(ArrayList<RefTypeDto> listeTypeAT) {
		this.listeTypeAT = listeTypeAT;
	}

	public String getNOM_ST_DATE_INITIALE() {
		return "NOM_ST_DATE_INITIALE";
	}

	public String getVAL_ST_DATE_INITIALE() {
		return getZone(getNOM_ST_DATE_INITIALE());
	}

	public String getNOM_EF_DATE_FIN() {
		return "NOM_EF_DATE_FIN";
	}

	public String getVAL_EF_DATE_FIN() {
		return getZone(getNOM_EF_DATE_FIN());
	}

	public String getNOM_EF_DATE_DEBUT() {
		return "NOM_EF_DATE_DEBUT";
	}

	public String getVAL_EF_DATE_DEBUT() {
		return getZone(getNOM_EF_DATE_DEBUT());
	}

	public String getNOM_EF_PRESCRIPTEUR() {
		return "NOM_EF_PRESCRIPTEUR";
	}

	public String getVAL_EF_PRESCRIPTEUR() {
		return getZone(getNOM_EF_PRESCRIPTEUR());
	}

	public String getNOM_EF_MOTIF() {
		return "NOM_EF_MOTIF";
	}

	public String getVAL_EF_MOTIF() {
		return getZone(getNOM_EF_MOTIF());
	}

	public String getNOM_EF_DATE_TRANSMISSION_CAFAT() {
		return "NOM_EF_DATE_TRANSMISSION_CAFAT";
	}

	public String getVAL_EF_DATE_TRANSMISSION_CAFAT() {
		return getZone(getNOM_EF_DATE_TRANSMISSION_CAFAT());
	}

	public String getNOM_EF_DATE_DECISION_CAFAT() {
		return "NOM_EF_DATE_DECISION_CAFAT";
	}

	public String getVAL_EF_DATE_DECISION_CAFAT() {
		return getZone(getNOM_EF_DATE_DECISION_CAFAT());
	}

	public String getNOM_EF_DATE_COMMISSION_APTITUDE() {
		return "NOM_EF_DATE_COMMISSION_APTITUDE";
	}

	public String getVAL_EF_DATE_COMMISSION_APTITUDE() {
		return getZone(getNOM_EF_DATE_COMMISSION_APTITUDE());
	}

	public IAbsService getAbsService() {
		return absService;
	}

	public void setAbsService(IAbsService absService) {
		this.absService = absService;
	}

	public IRadiService getRadiService() {
		return radiService;
	}

	public void setRadiService(IRadiService radiService) {
		this.radiService = radiService;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public PieceJointeDto getDocumentCourant() {
		return documentCourant;
	}

	public void setDocumentCourant(PieceJointeDto documentCourant) {
		this.documentCourant = documentCourant;
	}
}
