package nc.mairie.gestionagent.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumStatutFichePoste;
import nc.mairie.enums.EnumTypeCompetence;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.agent.Contrat;
import nc.mairie.metier.agent.Document;
import nc.mairie.metier.agent.LienDocumentAgent;
import nc.mairie.metier.carriere.FiliereGrade;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.carriere.GradeGenerique;
import nc.mairie.metier.parametrage.CadreEmploi;
import nc.mairie.metier.parametrage.DiplomeGenerique;
import nc.mairie.metier.parametrage.NatureAvantage;
import nc.mairie.metier.parametrage.TypeAvantage;
import nc.mairie.metier.parametrage.TypeDelegation;
import nc.mairie.metier.parametrage.TypeRegIndemn;
import nc.mairie.metier.poste.Activite;
import nc.mairie.metier.poste.ActiviteFP;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.Budget;
import nc.mairie.metier.poste.Competence;
import nc.mairie.metier.poste.CompetenceFP;
import nc.mairie.metier.poste.DiplomeFP;
import nc.mairie.metier.poste.EntiteGeo;
import nc.mairie.metier.poste.FEFP;
import nc.mairie.metier.poste.FicheEmploi;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Horaire;
import nc.mairie.metier.poste.NFA;
import nc.mairie.metier.poste.NiveauEtudeFP;
import nc.mairie.metier.poste.Service;
import nc.mairie.metier.poste.StatutFP;
import nc.mairie.metier.poste.TitrePoste;
import nc.mairie.metier.referentiel.NiveauEtude;
import nc.mairie.metier.referentiel.TypeCompetence;
import nc.mairie.metier.referentiel.TypeContrat;
import nc.mairie.metier.specificites.AvantageNature;
import nc.mairie.metier.specificites.AvantageNatureFP;
import nc.mairie.metier.specificites.Delegation;
import nc.mairie.metier.specificites.DelegationFP;
import nc.mairie.metier.specificites.RegIndemFP;
import nc.mairie.metier.specificites.RegIndemnAFF;
import nc.mairie.metier.specificites.RegimeIndemnitaire;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableActivite;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.TreeHierarchy;
import nc.mairie.utils.VariablesActivite;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Process OePOSTEFichePoste Date de création : (07/07/11 10:59:29)
 * 
 * 
 */
public class OePOSTEFichePoste extends nc.mairie.technique.BasicProcess {
	public static final int STATUT_RECHERCHE = 1;
	public static final int STATUT_A_DUPLIQUER = 2;
	public static final int STATUT_DUPLIQUER = 3;
	public static final int STATUT_SPECIFICITES = 5;
	public static final int STATUT_ACTI_PRINC = 6;
	public static final int STATUT_EMPLOI_PRIMAIRE = 7;
	public static final int STATUT_EMPLOI_SECONDAIRE = 8;
	public static final int STATUT_RESPONSABLE = 10;
	public static final int STATUT_RECHERCHE_AVANCEE = 11;
	public static final int STATUT_REMPLACEMENT = 12;
	public static final int STATUT_COMPETENCE = 13;

	public String ACTION_RECHERCHE = "Recherche.";
	public String ACTION_CREATION = "Création.";
	public String ACTION_DUPLICATION = "Duplication.";
	public String ACTION_MODIFICATION = "Modification.";
	public String ACTION_IMPRESSION = "Impression.";

	private String[] LB_TITRE_POSTE;
	private String[] LB_GRADE;
	private String[] LB_LOC;
	private String[] LB_BUDGET;
	private String[] LB_BUDGETE;
	private String[] LB_REGLEMENTAIRE;
	private String[] LB_STATUT;
	private String[] LB_DIPLOME;
	private String[] LB_DIPLOME_MULTI;
	private String[] LB_NIVEAU_ETUDE;

	// nouvelle liste suite remaniement fdp/activites
	private ArrayList listeToutesActi;
	// activites de la fiche emploi primaire
	private ArrayList listeActiFEP;
	// activites de la fiche emploi secondaire
	private ArrayList listeActiFES;
	// activites de la fiche poste
	private ArrayList listeActiFP;
	// activites de la fiche poste ajouté
	private ArrayList listeAjoutActiFP;

	// nouvelle liste suite remaniement fdp/compétences
	private ArrayList listeToutesComp;
	// competences de la fiche emploi primaire
	private ArrayList listeCompFEP;
	// competences de la fiche emploi secondaire
	private ArrayList listeCompFES;
	// competences de la fiche poste
	private ArrayList listeCompFP;
	// competences de la fiche poste ajouté
	private ArrayList listeAjoutCompFP;

	// Nouvelle gestion des niveau etude
	private ArrayList listeTousNiveau;
	// niveau etude de la fiche poste
	private ArrayList listeNiveauFP;

	// Nouvelle gestion des diplomes
	private ArrayList listeTousDiplomes;
	// diplomes de la fiche poste
	private ArrayList listeDiplomeFP;

	// pour les liste deroulante
	private ArrayList listeNiveauEtude;
	private ArrayList listeDiplome;

	private ArrayList listeBudget;
	private ArrayList listeStatut;
	private ArrayList listeTitre;
	private ArrayList listeGrade;
	private ArrayList listeLocalisation;
	private ArrayList listeAvantage;
	private ArrayList listeAvantageAAjouter;
	private ArrayList listeAvantageASupprimer;
	private ArrayList listeDelegation;
	private ArrayList listeDelegationAAjouter;
	private ArrayList listeDelegationASupprimer;
	private ArrayList listeRegime;
	private ArrayList listeRegimeAAjouter;
	private ArrayList listeRegimeASupprimer;
	private ArrayList listeServices;
	private ArrayList listeHoraire;
	private String observation;
	private String mission;

	private boolean afficherListeGrade = false;
	private boolean afficherListeNivEt = false;
	private boolean afficherListeDiplome = false;
	private boolean fpCouranteAffectee = false;

	public Hashtable<String, TreeHierarchy> hTree = null;
	public Hashtable<String, TypeAvantage> hashtypAv = null;
	public Hashtable<String, NatureAvantage> hashNatAv = null;
	public Hashtable<String, TypeDelegation> hashTypDel = null;
	public Hashtable<String, TypeRegIndemn> hashTypRegIndemn = null;

	private Hashtable<String, String> hashOrigineActivite;
	private Hashtable<String, String> hashOrigineCompetence;

	private FichePoste fichePosteCourante;

	private Affectation affectationCourante;
	private AgentNW agentCourant;
	private Contrat contratCourant;
	private TypeContrat typeContratCourant;

	private FicheEmploi emploiPrimaire;
	private FicheEmploi emploiSecondaire;

	private FichePoste responsable;
	private AgentNW agtResponsable;
	private TitrePoste titrePosteResponsable;

	private FichePoste remplacement;
	private AgentNW agtRemplacement;
	private TitrePoste titrePosteRemplacement;

	private Service service;
	public String focus = null;
	private String urlFichier;

	private String messageInf = Const.CHAINE_VIDE;
	public boolean responsableObligatoire = false;
	private boolean changementFEAutorise = true;
	public boolean estFDPInactive = false;

	private Logger logger = LoggerFactory.getLogger(OePOSTEFichePoste.class);

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (25/07/11 14:53:21)
	 * 
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		messageInf = Const.CHAINE_VIDE;

		// ----------------------------------//
		// Vérification des droits d'accès. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		// ---------------------------//
		// Initialisation de la page.//
		// ---------------------------//
		initialiseListeDeroulante();
		initialiseListeService();

		if (etatStatut() == STATUT_RECHERCHE) {
			afficheFicheCourante();
		}

		if (etatStatut() == STATUT_ACTI_PRINC) {
			initialiseActivites();
		}

		if (etatStatut() == STATUT_COMPETENCE) {
			initialiseCompetence();
		}

		if (etatStatut() == STATUT_DUPLIQUER) {
			addZone(getNOM_ST_NUMERO(), getFichePosteCourante().getNumFP());
		}

		if (etatStatut() == STATUT_A_DUPLIQUER) {
			addZone(getNOM_ST_INFO_FP(), Const.CHAINE_VIDE);
			addZone(getNOM_EF_RECHERCHE(), Const.CHAINE_VIDE);
			afficheFicheCourante();
		}

		// Récupération de la fiche de poste en session
		if (etatStatut() == STATUT_RECHERCHE_AVANCEE) {
			FichePoste fpRechAvancee = (FichePoste) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
			if (fpRechAvancee != null) {
				viderFichePoste();
				viderObjetsFichePoste();
				setFichePosteCourante(fpRechAvancee);
				addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
			} else {
				setFichePosteCourante(new FichePoste());
			}
			afficheFicheCourante();
			return;
		}

		if (etatStatut() == STATUT_RESPONSABLE) {
			// Responsable hiérarchique
			if ((FichePoste) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_POSTE) != null) {
				setResponsable((FichePoste) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_POSTE));
			} else {
				if (getFichePosteCourante() != null && getFichePosteCourante().getIdResponsable() != null) {
					setResponsable(FichePoste.chercherFichePoste(getTransaction(), getFichePosteCourante().getIdResponsable()));
				} else {
					setResponsable(null);
				}
			}
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
			afficheResponsable();
			return;
		} else {
			if (getResponsable() == null && getFichePosteCourante() != null && getFichePosteCourante().getIdResponsable() != null) {
				setResponsable(FichePoste.chercherFichePoste(getTransaction(), getFichePosteCourante().getIdResponsable()));
				afficheResponsable();
			}
		}

		if (etatStatut() == STATUT_REMPLACEMENT) {
			// Fiche de poste remplacée
			if ((FichePoste) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_POSTE) != null) {
				setRemplacement((FichePoste) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_POSTE));
			} else {
				if (getFichePosteCourante() != null && getFichePosteCourante().getIdRemplacement() != null) {
					setRemplacement(FichePoste.chercherFichePoste(getTransaction(), getFichePosteCourante().getIdRemplacement()));
				} else {
					setRemplacement(null);
				}
			}
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_POSTE);
			afficheRemplacement();
			return;
		} else {
			if (getRemplacement() == null && getFichePosteCourante() != null && getFichePosteCourante().getIdRemplacement() != null) {
				setRemplacement(FichePoste.chercherFichePoste(getTransaction(), getFichePosteCourante().getIdRemplacement()));
				afficheRemplacement();
			}
		}

		if (etatStatut() == STATUT_EMPLOI_SECONDAIRE) {
			FicheEmploi fes = (FicheEmploi) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI);
			if (fes == null) {
				if (getEmploiSecondaire() == null) {
					setEmploiSecondaire(fes);
				}
			} else {
				setEmploiSecondaire(fes);
			}
			afficheFES();
			initialiseMission();
			initialiseInfoEmploi();
			// si changement de FES, on ajoute la mission à la mission actuelle
			if (getEmploiSecondaire() != null) {
				if (!getMission().toUpperCase().contains(getEmploiSecondaire().getDefinitionEmploi().toUpperCase())) {
					setMission(getMission() + " " + getEmploiSecondaire().getDefinitionEmploi());
				}
			}

			addZone(getNOM_EF_MISSIONS(), getMission() == null ? Const.CHAINE_VIDE : getMission());
			return;
		} else {
			if (getEmploiSecondaire() != null) {
				afficheFES();
			}
		}

		if (etatStatut() == STATUT_EMPLOI_PRIMAIRE) {
			FicheEmploi fep = (FicheEmploi) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI);
			if (fep == null) {
				if (getEmploiPrimaire() == null) {
					setEmploiPrimaire(fep);
				}
			} else {
				setEmploiPrimaire(fep);
			}
			afficheFEP();
			initialiseMission();
			initialiseInfoEmploi();
			// si changement de FES, on ajoute la mission à la mission actuelle
			if (getEmploiPrimaire() != null) {
				if (!getMission().toUpperCase().contains(getEmploiPrimaire().getDefinitionEmploi().toUpperCase())) {
					setMission(getMission() + " " + getEmploiPrimaire().getDefinitionEmploi());
				}
			}

			addZone(getNOM_EF_MISSIONS(), getMission() == null ? Const.CHAINE_VIDE : getMission());
			return;
		} else {
			if (getEmploiPrimaire() == null && getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {
				setEmploiPrimaire(FicheEmploi.chercherFicheEmploiAvecFichePoste(getTransaction(), getFichePosteCourante(), true));
				afficheFEP();
			}
		}

		if (etatStatut() == STATUT_SPECIFICITES) {
			// Affiche les spécificités de la fiche de poste
			initialiseSpecificites();
			afficheSpecificites();
			return;
		}

		// Init à l'action Recherche lors du premier accès.
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION())) {
			addZone(getNOM_ST_ACTION(), ACTION_RECHERCHE);
			setFocus(getNOM_EF_RECHERCHE());
		}
	}

	/**
	 * Initialise les zones de la fiche poste courante.
	 * 
	 * @throws Exception
	 */
	private void afficheFicheCourante() throws Exception {

		// FICHE POSTE
		if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {
			// si FDP inactive alors on rend les champs disabled
			StatutFP statut = StatutFP.chercherStatutFP(getTransaction(), getFichePosteCourante().getIdStatutFP());
			if (statut.getLibStatutFP().equals(EnumStatutFichePoste.INACTIVE.getLibLong())) {
				estFDPInactive = true;
			} else {
				estFDPInactive = false;
			}

			// SERVICE
			if (getService() != null) {
				addZone(getNOM_EF_SERVICE(), getService().getSigleService());
				addZone(getNOM_EF_CODESERVICE(), getService().getCodService());
				String infoService = getService().getCodService() + " - " + getService().getLibService().replace("\'", " ");
				addZone(getNOM_ST_INFO_SERVICE(), infoService);
			}

			// FICHE EMPLOI PRIMAIRE
			afficheFEP();

			// FICHE EMPLOI SECONDAIRE
			afficheFES();

			// OBSERVATION
			initialiseObservation();

			// MISSION
			initialiseMission();

			// INFOS
			afficheInfosAffectationFP();
			addZone(getNOM_EF_ANNEE(), getFichePosteCourante().getAnneeCreation());
			addZone(getNOM_ST_NUMERO(), getFichePosteCourante().getNumFP());
			addZone(getNOM_EF_DATE_DEBUT_VALIDITE(), getFichePosteCourante().getDateDebutValiditeFP());
			addZone(getNOM_EF_DATE_FIN_VALIDITE(), getFichePosteCourante().getDateFinValiditeFP());
			addZone(getNOM_EF_DATE_DEBUT_APPLI_SERV(), getFichePosteCourante().getDateDebAppliService());
			addZone(getNOM_EF_NFA(), getFichePosteCourante().getNFA());
			addZone(getNOM_EF_OPI(), getFichePosteCourante().getOPI());
			if (getFichePosteCourante().getCodeGrade() != null) {
				Grade g = Grade.chercherGrade(getTransaction(), getFichePosteCourante().getCodeGrade());
				GradeGenerique gg = GradeGenerique.chercherGradeGenerique(getTransaction(), g.getCodeGradeGenerique());
				addZone(getNOM_EF_GRADE(), g.getGrade());
				// on récupère la categorie et la filiere de ce grade
				if (gg.getCodCadre() != null && (!gg.getCodCadre().equals(Const.CHAINE_VIDE))) {
					String info = "Cat : " + gg.getCodCadre();

					if (gg.getIdCadreEmploi() != null) {
						if (gg.getCdfili() != null) {
							FiliereGrade fi = FiliereGrade.chercherFiliereGrade(getTransaction(), gg.getCdfili());
							if (fi == null || getTransaction().isErreur()) {
								getTransaction().traiterErreur();
							} else {
								info += " , filière : " + fi.getLibFiliere();
							}
						}
					}
					addZone(getNOM_ST_INFO_GRADE(), info);
				}
			}
			addZone(getNOM_EF_CODE_GRADE(), getFichePosteCourante().getCodeGrade());

			if (getListeStatut() != null)
				for (int i = 0; i < getListeStatut().size(); i++) {
					StatutFP s = (StatutFP) getListeStatut().get(i);
					if (s.getIdStatutFP().equals(getFichePosteCourante().getIdStatutFP())) {
						addZone(getNOM_LB_STATUT_SELECT(), String.valueOf(i));
						initialiseChampObligatoire(s);
						break;
					}
				}

			if (getListeBudget() != null)
				for (int i = 0; i < getListeBudget().size(); i++) {
					Budget b = (Budget) getListeBudget().get(i);
					if (b.getIdBudget().equals(getFichePosteCourante().getIdBudget())) {
						addZone(getNOM_LB_BUDGET_SELECT(), String.valueOf(i));
						break;
					}
				}

			if (getListeHoraire() != null) {
				for (int i = 0; i < getListeHoraire().size(); i++) {
					Horaire h = (Horaire) getListeHoraire().get(i);
					if (h.getCdtHor().equals(getFichePosteCourante().getIdCdthorReg())) {
						addZone(getNOM_LB_REGLEMENTAIRE_SELECT(), String.valueOf(i));
						break;
					}
				}

				for (int i = 0; i < getListeHoraire().size(); i++) {
					Horaire h = (Horaire) getListeHoraire().get(i);
					if (h.getCdtHor().equals(getFichePosteCourante().getIdCdthorBud())) {
						addZone(getNOM_LB_BUDGETE_SELECT(), String.valueOf(i));
						break;
					}
				}
			}

			if (getListeLocalisation() != null)
				for (int i = 0; i < getListeLocalisation().size(); i++) {
					EntiteGeo eg = (EntiteGeo) getListeLocalisation().get(i);
					if (eg.getIdEntiteGeo().equals(getFichePosteCourante().getIdEntiteGeo())) {
						addZone(getNOM_LB_LOC_SELECT(), String.valueOf(i + 1));
						break;
					}
				}

			if (getListeTitre() != null)
				for (int i = 0; i < getListeTitre().size(); i++) {
					TitrePoste tp = (TitrePoste) getListeTitre().get(i);
					if (tp.getIdTitrePoste().equals(getFichePosteCourante().getIdTitrePoste())) {
						addZone(getNOM_EF_TITRE_POSTE(), tp.getLibTitrePoste());
						break;
					}
				}

			afficheResponsable();
			afficheRemplacement();

			// Spécificités
			initialiseSpecificites();
			afficheSpecificites();
		}
	}

	/**
	 * Affiche la FicheEmploi secondaire.
	 */
	private void afficheFES() throws Exception {
		addZone(getNOM_ST_EMPLOI_SECONDAIRE(), getEmploiSecondaire() == null ? Const.CHAINE_VIDE : getEmploiSecondaire().getRefMairie());
	}

	/**
	 * Affiche la FicheEmploi primaire.
	 */
	private void afficheFEP() throws Exception {
		if (getEmploiPrimaire() != null) {
			addZone(getNOM_ST_EMPLOI_PRIMAIRE(), getEmploiPrimaire().getRefMairie());
		} else
			addZone(getNOM_ST_EMPLOI_PRIMAIRE(), Const.CHAINE_VIDE);
	}

	/**
	 * Affiche la fiche de poste "Responsable"
	 */
	private void afficheResponsable() {
		if (getResponsable() != null) {
			addZone(getNOM_ST_RESPONSABLE(), getResponsable().getNumFP());
			if (getAgtResponsable() != null) {
				addZone(getNOM_ST_INFO_RESP(), getAgtResponsable().getNomAgent() + " " + getAgtResponsable().getPrenomAgent() + " ("
						+ getAgtResponsable().getNoMatricule() + ") - " + getTitrePosteResponsable().getLibTitrePoste());
			} else {
				addZone(getNOM_ST_INFO_RESP(), "Cette fiche de poste (" + getTitrePosteResponsable().getLibTitrePoste() + ") n'est pas affectée");
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
			addZone(getNOM_ST_REMPLACEMENT(), getRemplacement().getNumFP());
			if (getAgtRemplacement() != null) {
				addZone(getNOM_ST_INFO_REMP(), getAgtRemplacement().getNomAgent() + " " + getAgtRemplacement().getPrenomAgent() + " ("
						+ getAgtRemplacement().getNoMatricule() + ") - " + getTitrePosteRemplacement().getLibTitrePoste());
			} else {
				addZone(getNOM_ST_INFO_REMP(), "Cette fiche de poste (" + getTitrePosteRemplacement().getLibTitrePoste() + ") n'est pas affectée");
			}
		} else {
			addZone(getNOM_ST_REMPLACEMENT(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_INFO_REMP(), Const.CHAINE_VIDE);
		}
	}

	/**
	 * Affiche infos affectation FichePoste.
	 */
	private void afficheInfosAffectationFP() {
		if (getFichePosteCourante() != null && getAgentCourant() != null && getAffectationCourante() != null) {
			String chaine = "Cette fiche de poste est affectée à l'agent " + getAgentCourant().getNomAgent() + " "
					+ getAgentCourant().getPrenomAgent() + " (" + getAgentCourant().getNoMatricule() + ") depuis le "
					+ getAffectationCourante().getDateDebutAff();
			if (getContratCourant() != null && getContratCourant().getIdContrat() != null) {
				chaine += " (" + getTypeContratCourant().getLibTypeContrat() + " depuis le " + getContratCourant().getDateDebut();
				if (getContratCourant().getDateFin() != null) {
					chaine += " jusqu'au " + getContratCourant().getDateFin() + ")";
				} else
					chaine += ")";
			}

			addZone(getNOM_ST_INFO_FP(), chaine);
		} else {
			addZone(getNOM_ST_INFO_FP(), Const.CHAINE_VIDE);
		}
	}

	/**
	 * Initialise la liste des services.
	 * 
	 * @throws Exception
	 *             RG_PE_FP_C03
	 */
	private void initialiseListeService() throws Exception {
		// Si la liste des services est nulle
		if (getListeServices() == null || getListeServices().size() == 0) {
			ArrayList services = Service.listerServiceActif(getTransaction());
			setListeServices(services);

			// Tri par codeservice
			Collections.sort(getListeServices(), new Comparator<Object>() {
				public int compare(Object o1, Object o2) {
					Service s1 = (Service) o1;
					Service s2 = (Service) o2;
					return (s1.getCodService().compareTo(s2.getCodService()));
				}
			});

			// alim de la hTree
			// RG_PE_FP_C03
			hTree = new Hashtable<String, TreeHierarchy>();
			TreeHierarchy parent = null;
			for (int i = 0; i < getListeServices().size(); i++) {
				Service serv = (Service) getListeServices().get(i);

				if (Const.CHAINE_VIDE.equals(serv.getCodService()))
					continue;

				// recherche du nfa
				String nfa = NFA.chercherNFAByCodeService(getTransaction(), serv.getCodService()).getNFA();
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					nfa = null;
				}

				// recherche du supérieur
				String codeService = serv.getCodService();
				while (codeService.endsWith("A")) {
					codeService = codeService.substring(0, codeService.length() - 1);
				}
				codeService = codeService.substring(0, codeService.length() - 1);
				codeService = Services.rpad(codeService, 4, "A");

				parent = hTree.get(codeService);
				int indexParent = (parent == null ? 0 : parent.getIndex());
				hTree.put(serv.getCodService(), new TreeHierarchy(serv, i, indexParent, nfa));

			}
		}
	}

	/**
	 * Initialise les listes déroulantes de l'écran.
	 * 
	 * @throws Exception
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste type budget vide alors affectation
		if (getLB_BUDGET() == LBVide) {
			ArrayList budget = Budget.listerBudget(getTransaction());
			setListeBudget(budget);

			int[] tailles = { 20 };
			String[] champs = { "libBudget" };
			setLB_BUDGET(new FormateListe(tailles, budget, champs).getListeFormatee());
		}

		// Si liste statut vide alors affectation
		if (getLB_STATUT() == LBVide) {
			ArrayList statut = StatutFP.listerStatutFP(getTransaction());
			setListeStatut(statut);

			int[] tailles = { 20 };
			String[] champs = { "libStatutFP" };
			setLB_STATUT(new FormateListe(tailles, statut, champs).getListeFormatee());
			addZone(getNOM_LB_STATUT_SELECT(), Const.ZERO);
		}

		// Si liste localisation vide alors affectation
		if (getLB_LOC() == LBVide) {
			ArrayList loc = EntiteGeo.listerEntiteGeo(getTransaction());
			setListeLocalisation(loc);

			int[] tailles = { 100 };
			String[] champs = { "libEntiteGeo" };
			setLB_LOC(new FormateListe(tailles, loc, champs).getListeFormatee(true));
		}

		// Si liste titre poste vide alors affectation
		if (getLB_TITRE_POSTE() == LBVide) {
			ArrayList titre = TitrePoste.listerTitrePoste(getTransaction());
			setListeTitre(titre);

			int[] tailles = { 100 };
			String[] champs = { "libTitrePoste" };
			setLB_TITRE_POSTE(new FormateListe(tailles, titre, champs).getListeFormatee());
		}

		// Si liste grade vide alors affectation
		if (getLB_GRADE() == LBVide) {
			ArrayList grade = Grade.listerGradeInitialActif(getTransaction());
			setListeGrade(grade);

			int[] tailles = { 100 };
			String[] champs = { "grade" };
			setLB_GRADE(new FormateListe(tailles, grade, champs).getListeFormatee(true));
		}

		// Si liste niveau etude vide alors affectation
		if (getLB_NIVEAU_ETUDE() == LBVide) {
			ArrayList niveau = NiveauEtude.listerNiveauEtude(getTransaction());
			setListeNiveauEtude(niveau);

			int[] tailles = { 15 };
			String[] champs = { "libNiveauEtude" };
			setLB_NIVEAU_ETUDE(new FormateListe(tailles, niveau, champs).getListeFormatee(true));
		}

		// Si liste diplomes vide alors affectation
		if (getLB_DIPLOME() == LBVide) {
			ArrayList dipl = DiplomeGenerique.listerDiplomeGenerique(getTransaction());
			setListeDiplome(dipl);

			int[] tailles = { 100 };
			String[] champs = { "libDiplomeGenerique" };
			setLB_DIPLOME(new FormateListe(tailles, dipl, champs).getListeFormatee(true));
		}

		// Si liste diplomes vide alors affectation
		if (getLB_REGLEMENTAIRE() == LBVide) {
			ArrayList hor = Horaire.listerHoraire(getTransaction());
			setListeHoraire(hor);

			int[] tailles = { 100 };
			String[] champs = { "libHor" };
			setLB_REGLEMENTAIRE(new FormateListe(tailles, hor, champs).getListeFormatee());
		}

		// Si liste diplomes vide alors affectation
		if (getLB_BUDGETE() == LBVide) {
			int[] tailles = { 100 };
			String[] champs = { "libHor" };
			setLB_BUDGETE(new FormateListe(tailles, getListeHoraire(), champs).getListeFormatee());
		}
	}

	/**
	 * Récupère les compétences choisies.
	 * 
	 * @throws Exception
	 */
	private void initialiseCompetence() throws Exception {

		// on fait une liste de toutes les competences
		setListeToutesComp(new ArrayList());
		boolean trouve = false;
		if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {
			// on recupere les competences de la FDP
			setListeCompFP(CompetenceFP.listerCompetenceFPAvecFP(getTransaction(), getFichePosteCourante().getIdFichePoste()));
			for (int i = 0; i < getListeCompFP().size(); i++) {
				trouve = false;
				CompetenceFP compFP = (CompetenceFP) getListeCompFP().get(i);
				Competence competence = Competence.chercherCompetence(getTransaction(), compFP.getIdCompetence());
				for (int j = 0; j < getListeToutesComp().size(); j++) {
					Competence tteComp = (Competence) getListeToutesComp().get(j);
					if (tteComp.getIdCompetence().equals(competence.getIdCompetence())) {
						trouve = true;
						break;
					}
				}
				if (!trouve) {
					getListeToutesComp().add(competence);
					getHashOrigineCompetence().put(competence.getIdCompetence(), "FDP");
				}
			}
		} else {
			setListeCompFP(new ArrayList());
		}

		// on recupere les competences des differentes FE
		trouve = false;
		if (getEmploiPrimaire() != null && getEmploiPrimaire().getIdFicheEmploi() != null) {
			setListeCompFEP(Competence.listerCompetenceAvecFE(getTransaction(), getEmploiPrimaire()));
			for (int i = 0; i < getListeCompFEP().size(); i++) {
				trouve = false;
				Competence compFP = (Competence) getListeCompFEP().get(i);
				for (int j = 0; j < getListeToutesComp().size(); j++) {
					Competence tteComp = (Competence) getListeToutesComp().get(j);
					if (tteComp.getIdCompetence().equals(compFP.getIdCompetence())) {
						trouve = true;
						break;
					}
				}
				if (!trouve) {
					getListeToutesComp().add(compFP);
					getHashOrigineCompetence().put(compFP.getIdCompetence(), getEmploiPrimaire().getRefMairie());
				}
			}
		} else {
			setListeCompFEP(new ArrayList());
		}

		if (getEmploiSecondaire() != null && getEmploiSecondaire().getIdFicheEmploi() != null) {
			trouve = false;
			setListeCompFES(Competence.listerCompetenceAvecFE(getTransaction(), getEmploiSecondaire()));
			for (int i = 0; i < getListeCompFES().size(); i++) {
				trouve = false;
				Competence compFP = (Competence) getListeCompFES().get(i);
				for (int j = 0; j < getListeToutesComp().size(); j++) {
					Competence tteActi = (Competence) getListeToutesComp().get(j);
					if (tteActi.getIdCompetence().equals(compFP.getIdCompetence())) {
						trouve = true;
						break;
					}
				}
				if (!trouve) {
					getListeToutesComp().add(compFP);
					getHashOrigineCompetence().put(compFP.getIdCompetence(), getEmploiSecondaire().getRefMairie());
				}
			}

		} else {
			setListeCompFES(new ArrayList());
		}

		// on recupere les activites selectionnées dans l'ecran de selection
		ArrayList listeCompSelect = (ArrayList) VariablesActivite.recuperer(this, "COMPETENCE");

		if (listeCompSelect != null && listeCompSelect.size() != 0) {
			if (getListeAjoutCompFP() != null) {
				getListeAjoutCompFP().addAll(listeCompSelect);
			} else {
				setListeAjoutCompFP(listeCompSelect);
			}
			for (int i = 0; i < getListeAjoutCompFP().size(); i++) {
				Competence c = (Competence) getListeAjoutCompFP().get(i);
				if (c != null) {
					if (getListeToutesComp() == null)
						setListeToutesComp(new ArrayList());
					if (!getListeToutesComp().contains(c)) {
						getListeToutesComp().add(c);
						getHashOrigineCompetence().put(c.getIdCompetence(), "FDP");
					}
				}
			}

		} else {
			setListeAjoutCompFP(new ArrayList());
		}

		// Si liste competences vide alors initialisation.
		boolean dejaCoche = false;
		for (int i = 0; i < getListeToutesComp().size(); i++) {
			dejaCoche = false;
			Competence competence = (Competence) getListeToutesComp().get(i);
			TypeCompetence typeComp = TypeCompetence.chercherTypeCompetence(getTransaction(), competence.getIdTypeCompetence());
			String origineComp = (String) getHashOrigineCompetence().get(competence.getIdCompetence());

			if (competence != null) {
				addZone(getNOM_ST_ID_COMP(i), competence.getIdCompetence());
				addZone(getNOM_ST_LIB_COMP(i), competence.getNomCompetence());
				addZone(getNOM_ST_TYPE_COMP(i), typeComp.getLibTypeCompetence());
				addZone(getNOM_ST_LIB_ORIGINE_COMP(i), origineComp);
				addZone(getNOM_CK_SELECT_LIGNE_COMP(i), getCHECKED_OFF());

				if (getListeCompFP() != null) {
					// si la competence fait partie de la liste des competences
					// de la FDP
					for (int j = 0; j < getListeCompFP().size(); j++) {
						CompetenceFP compFP = (CompetenceFP) getListeCompFP().get(j);
						Competence competenceFP = Competence.chercherCompetence(getTransaction(), compFP.getIdCompetence());
						if (competenceFP.equals(competence)) {
							addZone(getNOM_CK_SELECT_LIGNE_COMP(i), getCHECKED_ON());
							dejaCoche = true;
							break;
						} else {
							if (!dejaCoche) {
								addZone(getNOM_CK_SELECT_LIGNE_COMP(i), getCHECKED_OFF());
							}
						}
					}
				} else {
					addZone(getNOM_CK_SELECT_LIGNE_COMP(i), getCHECKED_OFF());
				}
				if (getListeAjoutCompFP() != null) {
					// si la competence fait partie de la liste des competences
					// ajoutées à la FDP
					for (int j = 0; j < getListeAjoutCompFP().size(); j++) {
						Competence competenceFP = (Competence) getListeAjoutCompFP().get(j);
						if (competenceFP.equals(competence)) {
							addZone(getNOM_CK_SELECT_LIGNE_COMP(i), getCHECKED_ON());
							dejaCoche = true;
							break;
						} else {
							if (!dejaCoche) {
								addZone(getNOM_CK_SELECT_LIGNE_COMP(i), getCHECKED_OFF());
							}
						}
					}

				} else {
					addZone(getNOM_CK_SELECT_LIGNE_COMP(i), getCHECKED_OFF());
				}

			}
		}

		VariablesActivite.enlever(this, "COMPETENCE");
		VariablesActivite.enlever(this, "LISTECOMPETENCESAVOIR");
		VariablesActivite.enlever(this, "LISTECOMPETENCESAVOIRFAIRE");
		VariablesActivite.enlever(this, "LISTECOMPETENCECOMPORTEMENT");
	}

	/**
	 * Récupère les spécificités de la fiche de poste.
	 * 
	 * @throws Exception
	 */
	private void initialiseSpecificites() throws Exception {
		// Avantages en nature
		setListeAvantage((ArrayList) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_LST_AV_NATURE));
		if (getListeAvantage() != null && getListeAvantage().size() > 0) {
			setListeAvantageAAjouter((ArrayList) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_LST_AV_NATURE_A_AJOUT));
			setListeAvantageASupprimer((ArrayList) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_LST_AV_NATURE_A_SUPPR));
		} else if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {
			setListeAvantage(AvantageNature.listerAvantageNatureAvecFP(getTransaction(), getFichePosteCourante().getIdFichePoste()));
		}
		if (getListeAvantage() != null) {
			for (ListIterator list = getListeAvantage().listIterator(); list.hasNext();) {
				AvantageNature aAvNat = (AvantageNature) list.next();
				if (aAvNat != null) {
					TypeAvantage typAv = TypeAvantage.chercherTypeAvantage(getTransaction(), aAvNat.getIdTypeAvantage());
					NatureAvantage natAv = aAvNat.getIdNatureAvantage() != null ? NatureAvantage.chercherNatureAvantage(getTransaction(),
							aAvNat.getIdNatureAvantage()) : null;
					getHashtypAv().put(typAv.getIdTypeAvantage(), typAv);
					getHashNatAv().put(natAv.getIdNatureAvantage(), natAv);
				}
			}
		}

		// Délégations
		setListeDelegation((ArrayList) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_LST_DELEGATION));
		if (getListeDelegation() != null && getListeDelegation().size() > 0) {
			setListeDelegationAAjouter((ArrayList) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_LST_DELEGATION_A_AJOUT));
			setListeDelegationASupprimer((ArrayList) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_LST_DELEGATION_A_SUPPR));
		} else if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {
			setListeDelegation(Delegation.listerDelegationAvecFP(getTransaction(), getFichePosteCourante().getIdFichePoste()));
		}
		if (getListeDelegation() != null) {
			for (ListIterator list = getListeDelegation().listIterator(); list.hasNext();) {
				Delegation aDel = (Delegation) list.next();
				if (aDel != null) {
					TypeDelegation typDel = TypeDelegation.chercherTypeDelegation(getTransaction(), aDel.getIdTypeDelegation());
					getHashTypDel().put(typDel.getIdTypeDelegation(), typDel);
				}
			}
		}

		// Régimes indemnitaires
		setListeRegime((ArrayList) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_LST_REG_INDEMN));
		if (getListeRegime() != null && getListeRegime().size() > 0) {
			setListeRegimeAAjouter((ArrayList) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_LST_REG_INDEMN_A_AJOUT));
			setListeRegimeASupprimer((ArrayList) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_LST_REG_INDEMN_A_SUPPR));
		} else if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {
			setListeRegime(RegimeIndemnitaire.listerRegimeIndemnitaireAvecFP(getTransaction(), getFichePosteCourante().getIdFichePoste()));
		}
		if (getListeRegime() != null) {
			for (ListIterator list = getListeRegime().listIterator(); list.hasNext();) {
				RegimeIndemnitaire aReg = (RegimeIndemnitaire) list.next();
				if (aReg != null) {
					TypeRegIndemn typReg = TypeRegIndemn.chercherTypeRegIndemn(getTransaction(), aReg.getIdTypeRegIndemn());
					getHashTypRegIndemn().put(typReg.getIdTypeRegIndemn(), typReg);
				}
			}
		}
	}

	/**
	 * Affiche les spécificités de la fiche de poste.
	 * 
	 * @throws Exception
	 */
	private void afficheSpecificites() throws Exception {
		// Avantages en nature
		int indiceAvantage = 0;
		if (getListeAvantage() != null) {
			for (ListIterator list = getListeAvantage().listIterator(); list.hasNext();) {
				AvantageNature aAvNat = (AvantageNature) list.next();
				if (aAvNat != null) {

					addZone(getNOM_ST_AV_TYPE(indiceAvantage),
							getHashtypAv().get(aAvNat.getIdTypeAvantage()).getLibTypeAvantage().equals(Const.CHAINE_VIDE) ? "&nbsp;" : getHashtypAv()
									.get(aAvNat.getIdTypeAvantage()).getLibTypeAvantage());
					addZone(getNOM_ST_AV_MNT(indiceAvantage), aAvNat.getMontant());
					addZone(getNOM_ST_AV_NATURE(indiceAvantage),
							aAvNat.getIdNatureAvantage() == null ? "&nbsp;" : getHashNatAv().get(aAvNat.getIdNatureAvantage()).getLibNatureAvantage());
				}
				indiceAvantage++;
			}
		}

		// Délégations
		int indiceDelegation = 0;
		if (getListeDelegation() != null) {
			for (ListIterator list = getListeDelegation().listIterator(); list.hasNext();) {
				Delegation aDel = (Delegation) list.next();
				if (aDel != null) {

					addZone(getNOM_ST_DEL_TYPE(indiceDelegation),
							getHashTypDel().get(aDel.getIdTypeDelegation()).getLibTypeDelegation().equals(Const.CHAINE_VIDE) ? "&nbsp;"
									: getHashTypDel().get(aDel.getIdTypeDelegation()).getLibTypeDelegation());
					addZone(getNOM_ST_DEL_COMMENTAIRE(indiceDelegation),
							aDel.getLibDelegation().equals(Const.CHAINE_VIDE) ? "&nbsp;" : aDel.getLibDelegation());
				}
				indiceDelegation++;
			}
		}

		// Régimes indemnitaires
		int indiceRegime = 0;
		if (getListeRegime() != null) {
			for (ListIterator list = getListeRegime().listIterator(); list.hasNext();) {
				RegimeIndemnitaire aReg = (RegimeIndemnitaire) list.next();
				if (aReg != null) {

					addZone(getNOM_ST_REG_TYPE(indiceRegime),
							getHashTypRegIndemn().get(aReg.getIdTypeRegIndemn()).getLibTypeRegIndemn().equals(Const.CHAINE_VIDE) ? "&nbsp;"
									: getHashTypRegIndemn().get(aReg.getIdTypeRegIndemn()).getLibTypeRegIndemn());
					addZone(getNOM_ST_REG_FORFAIT(indiceRegime), aReg.getForfait());
					addZone(getNOM_ST_REG_NB_PTS(indiceRegime), aReg.getNombrePoints());
				}
				indiceRegime++;
			}
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (07/07/11 10:59:29)
	 * 
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * Vide les champs du formulaire.
	 * 
	 * @throws Exception
	 */
	private void viderFichePoste() throws Exception {

		addZone(getNOM_EF_ANNEE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_MISSIONS(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_OBSERVATION(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_RECHERCHE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SERVICE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_TITRE_POSTE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NIVEAU_ETUDE_MULTI(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_FIN_VALIDITE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_DEBUT_VALIDITE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_DEBUT_APPLI_SERV(), Const.CHAINE_VIDE);

		addZone(getNOM_ST_INFO_FP(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_EMPLOI_PRIMAIRE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_EMPLOI_SECONDAIRE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_NFA(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_NUMERO(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_OPI(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_RESPONSABLE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_GRADE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_CODE_GRADE(), Const.CHAINE_VIDE);

		addZone(getNOM_LB_STATUT_SELECT(), "0");
		addZone(getNOM_LB_BUDGET_SELECT(), "0");
		addZone(getNOM_LB_BUDGETE_SELECT(), "0");
		addZone(getNOM_LB_REGLEMENTAIRE_SELECT(), "0");
		addZone(getNOM_LB_NIVEAU_ETUDE_SELECT(), "0");
		addZone(getNOM_LB_DIPLOME_SELECT(), "0");
		addZone(getNOM_LB_LOC_SELECT(), "0");
		addZone(getNOM_LB_TITRE_POSTE_SELECT(), "0");
		addZone(getNOM_LB_GRADE_SELECT(), "0");
		addZone(getNOM_ST_INFO_GRADE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_INFO_SERVICE(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_INFO_RESP(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_INFO_REMP(), Const.CHAINE_VIDE);

		setLB_NIVEAU_ETUDE(null);
		setLB_DIPLOME_MULTI(null);
	}

	/**
	 * Efface tous les objets liés à la fiche emploi courante.
	 */
	private void viderObjetsFichePoste() throws Exception {

		responsableObligatoire = true;

		setFichePosteCourante(null);
		setFpCouranteAffectee(false);

		setEmploiPrimaire(null);
		setEmploiSecondaire(null);

		setAgentCourant(null);
		setAffectationCourante(null);
		setContratCourant(null);
		setTypeContratCourant(null);
		setResponsable(null);
		setAgtResponsable(null);
		setTitrePosteResponsable(null);
		setRemplacement(null);
		setAgtRemplacement(null);
		setTitrePosteRemplacement(null);

		setListeTousNiveau(null);
		setListeTousDiplomes(null);

		setListeAjoutActiFP(null);
		setListeAjoutCompFP(null);

		setListeAvantage(null);
		setListeDelegation(null);
		setListeRegime(null);

		setListeTousNiveau(new ArrayList());
		setListeTousDiplomes(new ArrayList());

		setListeNiveauFP(new ArrayList());

		setListeDiplomeFP(new ArrayList());

		setListeActiFP(new ArrayList());
		setListeActiFES(new ArrayList());
		setListeActiFEP(new ArrayList());
		setListeAjoutActiFP(new ArrayList());

		setListeCompFP(new ArrayList());
		setListeCompFES(new ArrayList());
		setListeCompFEP(new ArrayList());
		setListeAjoutCompFP(new ArrayList());
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (07/07/11 10:59:29)
	 * 
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		VariableActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_POSTE);

		viderFichePoste();
		viderObjetsFichePoste();

		setAfficherListeGrade(false);

		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER Date de création :
	 * (07/07/11 10:59:29)
	 * 
	 * 
	 */
	public String getNOM_PB_CREER() {
		return "NOM_PB_CREER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER Date de création :
	 * (07/07/11 10:59:29)
	 * 
	 * 
	 */
	public String getNOM_PB_IMPRIMER() {
		return "NOM_PB_IMPRIMER";
	}

	/**
	 * Contrôle les zones saisies Date de création : (27/06/11 14:50:00)
	 * RG_PE_FP_A01
	 */
	private boolean performControlerSaisie(HttpServletRequest request) throws Exception {
		// RG_PE_FP_A01
		// **********************
		// Verification Année
		// **********************
		if (getZone(getNOM_EF_ANNEE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "année"));
			setFocus(getNOM_EF_ANNEE());
			return false;
		} else if (getZone(getNOM_EF_ANNEE()).length() != 4) {
			// "ERR118","L'année doit être saisie avec 4 chiffres."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR118"));
			setFocus(getNOM_EF_ANNEE());
			return false;
		} else if (!Services.estNumerique(getZone(getNOM_EF_ANNEE()))) {
			// "ERR992","La zone @ doit être numérique."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "année"));
			setFocus(getNOM_EF_ANNEE());
			return false;
		}

		// Verification grade generique
		if (getZone(getNOM_EF_GRADE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "grade"));
			setFocus(getNOM_EF_GRADE());
			return false;
		}

		// **********************
		// Verification Niveau Etudes
		// **********************
		if (getListeTousNiveau().size() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Niveau d'étude"));
			setFocus(getNOM_LB_NIVEAU_ETUDE());
			return false;
		}
		if (getListeTousNiveau().size() > 1) {
			// "ERR110", "La liste @ ne doit contenir qu'un seul élément."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR110", "Niveau d'étude"));
			setFocus(getNOM_LB_NIVEAU_ETUDE());
			return false;
		}

		// **********************
		// Verification Diplome
		// **********************
		if (getListeTousDiplomes().size() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "diplômes"));
			setFocus(getNOM_LB_DIPLOME_MULTI());
			return false;
		}
		if (getListeTousDiplomes().size() > 1) {
			// "ERR110", "La liste @ ne doit contenir qu'un seul élément."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR110", "Diplômes"));
			setFocus(getNOM_LB_DIPLOME_MULTI());
			return false;
		}

		// **********************
		// Verification Service
		// **********************
		if (getZone(getNOM_EF_SERVICE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "service"));
			setFocus(getNOM_EF_SERVICE());
			return false;
		}

		// **********************
		// Verification Date debut application service
		// **********************
		if ((Const.CHAINE_VIDE).equals(getVAL_EF_DATE_DEBUT_APPLI_SERV())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Date application"));
			setFocus(getNOM_EF_DATE_DEBUT_APPLI_SERV());
			return false;
		}

		if (!Services.estUneDate(getVAL_EF_DATE_DEBUT_APPLI_SERV())) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "application"));
			setFocus(getNOM_EF_DATE_DEBUT_APPLI_SERV());
			return false;
		}

		// **********************
		// Verification NFA
		// **********************
		if (getZone(getNOM_EF_NFA()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "NFA"));
			setFocus(getNOM_EF_NFA());
			return false;
		}

		// **********************
		// Verification Responsable hiérarchique
		// **********************
		if (responsableObligatoire && getVAL_ST_RESPONSABLE().length() == 0 && !getVAL_EF_TITRE_POSTE().equals(Const.TITRE_POSTE_MAIRE)) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "responsable hiérarchique"));
			setFocus(getNOM_ST_RESPONSABLE());
			return false;
		}

		// **********************
		// Verification Missions
		// **********************
		if (getVAL_EF_MISSIONS().length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "missions"));
			setFocus(getNOM_PB_RECHERCHER());
			return false;
		}
		if (getVAL_EF_MISSIONS().length() > 2000) {
			// "ERR119", "La mission ne doit pas dépasser 2000 caractères."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR119"));
			setFocus(getNOM_PB_RECHERCHER());
			return false;
		}

		// **********************
		// Verification activites
		// **********************
		boolean auMoinsUneligneSelect = false;
		for (int i = 0; i < getListeToutesActi().size(); i++) {
			// si la ligne est cochée
			if (getVAL_CK_SELECT_LIGNE_ACTI(i).equals(getCHECKED_ON())) {
				auMoinsUneligneSelect = true;
				break;
			}
		}
		if (!auMoinsUneligneSelect) {
			// "ERR008", Aucun élément n'est sélectionné dans la liste des @.
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR008", "activités"));
			return false;
		}

		// **********************
		// Verification reglementaire et budgete
		// **********************
		// si statut = "validée" alors on ne peut avoir "indeterminé" dans
		// reglementaire et budgete
		// Récupération Statut de la fiche
		int numLigneStatut = (Services.estNumerique(getZone(getNOM_LB_STATUT_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_STATUT_SELECT())) : -1);
		if (numLigneStatut == -1 || getListeStatut().size() == 0 || numLigneStatut > getListeStatut().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "statuts"));
			return false;
		}

		return true;
	}

	/**
	 * Contrôle les Règles de gestion. RG_PE_FP_C02
	 */
	private boolean performControlerRG(HttpServletRequest request) throws Exception {

		/*
		 * Vérification RG responsable hiérarchique != fiche courante && fiche
		 * de poste remplacée != fiche courante
		 */
		if (getFichePosteCourante() != null && getFichePosteCourante().getNumFP() != null
				&& !getFichePosteCourante().getNumFP().equals(Const.CHAINE_VIDE)) {
			if (getVAL_ST_RESPONSABLE().equals(getFichePosteCourante().getNumFP())) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR116"));
				return false;
			}
			if (getVAL_ST_REMPLACEMENT().equals(getFichePosteCourante().getNumFP())) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR117", "remplacée"));
				return false;
			}
		}

		// *********************** //
		// Verification statut //
		// *********************** //
		// RG_PE_FP_C02
		if (getFichePosteCourante() != null) {

			// Récupération Statut de la fiche
			int numLigneStatut = (Services.estNumerique(getZone(getNOM_LB_STATUT_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_STATUT_SELECT()))
					: -1);

			if (numLigneStatut == -1 || getListeStatut().size() == 0 || numLigneStatut > getListeStatut().size()) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "statuts"));
				return false;
			}

			StatutFP statutCourant = (StatutFP) getListeStatut().get(numLigneStatut);
			StatutFP statutPrecedant = null;

			if (getFichePosteCourante().getIdStatutFP() != null) {
				statutPrecedant = StatutFP.chercherStatutFP(getTransaction(), getFichePosteCourante().getIdStatutFP());
				if (!statutCourant.getIdStatutFP().equals(statutPrecedant.getIdStatutFP())) {

					// Passage au statut inactif impossible si la fiche est
					// affectée ou est utilisée comme responsable hiérarchique.
					if (EnumStatutFichePoste.INACTIVE.getLibLong().equals(statutCourant.getLibStatutFP())) {
						if (estFpCouranteAffectee()) {
							// "ERR114",
							// "Cette fiche de poste ne peut être inactive car elle est affectée à un agent."
							getTransaction().declarerErreur(MessageUtils.getMessage("ERR114"));
							return false;
						}
						// SUPPRESSION DE CETTE REGLE SUITE JIRA SIRH-122
						/*
						 * if (getFichePosteCourante().estRespHierarchique(
						 * getTransaction())) { // "ERR115", //
						 * "Cette fiche de poste ne peut être inactive car elle est utilisée comme responsable hiérarchique."
						 * getTransaction().declarerErreur(
						 * MessageUtils.getMessage("ERR115")); return false; }
						 */
					}

					if (EnumStatutFichePoste.EN_CREATION.getLibLong().equals(statutCourant.getLibStatutFP())) {
						// "ERR123", "Le statut ne peut repasser à @."
						getTransaction().declarerErreur(MessageUtils.getMessage("ERR123", "'En création'"));
						return false;
					}

					if (EnumStatutFichePoste.INACTIVE.getLibLong().equals(statutCourant.getLibStatutFP())
							&& !EnumStatutFichePoste.VALIDEE.getLibLong().equals(statutPrecedant.getLibStatutFP())) {
						// "ERR124",
						// "Le statut ne peut passer à @ s'il n'est pas @."
						getTransaction().declarerErreur(
								MessageUtils.getMessage("ERR124", "'" + EnumStatutFichePoste.INACTIVE.getLibLong() + "'", "'"
										+ EnumStatutFichePoste.VALIDEE.getLibLong() + "'"));
						return false;
					}

					if (EnumStatutFichePoste.EN_MODIFICATION.getLibLong().equals(statutCourant.getLibStatutFP())
							&& !EnumStatutFichePoste.VALIDEE.getLibLong().equals(statutPrecedant.getLibStatutFP())) {
						// "ERR124",
						// "Le statut ne peut passer à @ s'il n'est pas @."
						getTransaction().declarerErreur(
								MessageUtils.getMessage("ERR124", "'" + EnumStatutFichePoste.EN_MODIFICATION.getLibLong() + "'", "'"
										+ EnumStatutFichePoste.VALIDEE.getLibLong() + "'"));
						return false;
					}

				}
			}
		}

		return true;
	}

	/**
	 * Alimente l'objet FicheEmploi avec les champs de saisie du formulaire.
	 * Retourne true ou false Date de création : (27/06/11 15:34:00)
	 */
	private boolean alimenterFichePoste(HttpServletRequest request) throws Exception {

		// récupération des informations remplies dans les zones de saisie
		String annee = getVAL_EF_ANNEE();
		String dateFinValidite = getVAL_EF_DATE_FIN_VALIDITE();
		String dateDebutValidite = getVAL_EF_DATE_DEBUT_VALIDITE();
		String dateDebutAppliServ = getVAL_EF_DATE_DEBUT_APPLI_SERV();
		String opi = getVAL_EF_OPI().length() == 0 ? null : getVAL_EF_OPI();
		String observation = getVAL_EF_OBSERVATION();
		String nfa = getVAL_EF_NFA();
		String missions = getVAL_EF_MISSIONS();
		String codServ = getVAL_EF_CODESERVICE();
		String grade = getVAL_EF_CODE_GRADE();

		// récupération du titre de poste et vérification de son existence.
		String idTitre = Const.CHAINE_VIDE;
		for (int i = 0; i < getListeTitre().size(); i++) {
			TitrePoste titre = (TitrePoste) getListeTitre().get(i);
			if (titre.getLibTitrePoste().equals(getVAL_EF_TITRE_POSTE())) {
				idTitre = titre.getIdTitrePoste();
				break;
			}
		}
		if (idTitre.length() == 0) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "titre de poste"));
			return false;
		}

		// Statut de la fiche
		int numLigneStatut = (Services.estNumerique(getZone(getNOM_LB_STATUT_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_STATUT_SELECT())) : -1);

		if (numLigneStatut == -1 || getListeStatut().size() == 0 || numLigneStatut > getListeStatut().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "statuts"));
			return false;
		}

		StatutFP statut = (StatutFP) getListeStatut().get(numLigneStatut);

		// Budget
		int numLigneBudget = (Services.estNumerique(getZone(getNOM_LB_BUDGET_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_BUDGET_SELECT())) : -1);

		if (numLigneBudget == -1 || getListeBudget().size() == 0 || numLigneBudget > getListeBudget().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "budgets"));
			return false;
		}

		Budget budget = (Budget) getListeBudget().get(numLigneBudget);

		// Lieu
		int numLigneLoc = (Services.estNumerique(getZone(getNOM_LB_LOC_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_LOC_SELECT())) : -1);

		if (numLigneLoc == -1 || getListeLocalisation().size() == 0 || numLigneLoc > getListeLocalisation().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "localisations"));
			return false;
		}

		EntiteGeo lieu = (EntiteGeo) getListeLocalisation().get(numLigneLoc - 1);

		int numLigneBudgete = (Services.estNumerique(getZone(getNOM_LB_BUDGETE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_BUDGETE_SELECT()))
				: -1);

		if (numLigneBudgete == -1 || getListeHoraire().size() == 0 || numLigneBudgete > getListeHoraire().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "budgété"));
			return false;
		}

		Horaire budgete = (Horaire) getListeHoraire().get(numLigneBudgete);

		int numLigneReglementaire = (Services.estNumerique(getZone(getNOM_LB_REGLEMENTAIRE_SELECT())) ? Integer
				.parseInt(getZone(getNOM_LB_REGLEMENTAIRE_SELECT())) : -1);

		if (numLigneReglementaire == -1 || getListeHoraire().size() == 0 || numLigneReglementaire > getListeHoraire().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "réglementaire"));
			return false;
		}

		Horaire reglementaire = (Horaire) getListeHoraire().get(numLigneReglementaire);

		getFichePosteCourante().setAnneeCreation(annee);
		getFichePosteCourante().setDateFinValiditeFP(dateFinValidite);
		getFichePosteCourante().setDateDebutValiditeFP(dateDebutValidite);
		getFichePosteCourante().setObservation(observation);
		getFichePosteCourante().setMissions(missions);
		getFichePosteCourante().setIdStatutFP(statut.getIdStatutFP());
		getFichePosteCourante().setIdBudget(budget.getIdBudget());
		getFichePosteCourante().setOPI(opi);
		getFichePosteCourante().setNFA(nfa);
		getFichePosteCourante().setIdEntiteGeo(lieu.getIdEntiteGeo());
		getFichePosteCourante().setIdTitrePoste(idTitre);
		getFichePosteCourante().setCodeGrade(grade);
		getFichePosteCourante().setIdServi(codServ);
		getFichePosteCourante().setIdCdthorBud(budgete.getCdtHor());
		getFichePosteCourante().setIdCdthorReg(reglementaire.getCdtHor());
		getFichePosteCourante().setDateDebAppliService(dateDebutAppliServ);

		if (getFichePosteCourante().getIdStatutFP().equals(EnumStatutFichePoste.INACTIVE.getId())) {
			getFichePosteCourante().setIdResponsable(null);
		} else {
			if (getResponsable() != null)
				getFichePosteCourante().setIdResponsable(getResponsable().getIdFichePoste());
		}

		if (getRemplacement() != null)
			getFichePosteCourante().setIdRemplacement(getRemplacement().getIdFichePoste());

		return true;
	}

	private boolean saveJoin(HttpServletRequest request) throws Exception {
		// Sauvegarde des fiche emploi primaire et secondaire
		FicheEmploi emploiPrimaire = FicheEmploi.chercherFicheEmploiAvecFichePoste(getTransaction(), getFichePosteCourante(), true);
		if (emploiPrimaire == null) {
			FEFP fefpPrimaire = new FEFP(getFichePosteCourante().getIdFichePoste(), getEmploiPrimaire().getIdFicheEmploi(), true);
			fefpPrimaire.creerFEFP(getTransaction());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR976", "La liaison FicheEmploi-FichePoste"));
				return false;
			}
		} else {
			FEFP ancienLien = FEFP.chercherFEFPAvecNumFPPrimaire(getTransaction(), getFichePosteCourante().getIdFichePoste(), true);
			ancienLien.setFePrimaire(true);
			ancienLien.setIdFicheEmploi(getEmploiPrimaire().getIdFicheEmploi());
			ancienLien.modifierFEFP(getTransaction());
		}

		if (getEmploiSecondaire() != null) {
			FicheEmploi emploiSecondaire = FicheEmploi.chercherFicheEmploiAvecFichePoste(getTransaction(), getFichePosteCourante(), false);
			if (emploiSecondaire == null) {
				FEFP fefpSecondaire = new FEFP(getFichePosteCourante().getIdFichePoste(), getEmploiSecondaire().getIdFicheEmploi(), false);
				fefpSecondaire.creerFEFP(getTransaction());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR976", "La liaison FicheEmploi-FichePoste"));
					return false;
				}
			} else {
				// on modifie le lien avec le num FE secondaire
				FEFP ancienLien = FEFP.chercherFEFPAvecNumFPPrimaire(getTransaction(), getFichePosteCourante().getIdFichePoste(), false);
				ancienLien.setFePrimaire(false);
				ancienLien.setIdFicheEmploi(getEmploiSecondaire().getIdFicheEmploi());
				ancienLien.modifierFEFP(getTransaction());
			}
		} else {
			// on supprime le lien eventuel
			FicheEmploi emploiSecondaire = FicheEmploi.chercherFicheEmploiAvecFichePoste(getTransaction(), getFichePosteCourante(), false);
			if (emploiSecondaire != null) {
				// on modifie le lien avec le num FE secondaire
				FEFP ancienLien = FEFP.chercherFEFPAvecNumFPPrimaire(getTransaction(), getFichePosteCourante().getIdFichePoste(), false);
				ancienLien.supprimerFEFP(getTransaction());
			}
		}
		// on supprime tous les diplome de la FDP
		ArrayList diplomeFPExistant = DiplomeFP.listerDiplomeFPAvecFP(getTransaction(), getFichePosteCourante());
		if (diplomeFPExistant != null && diplomeFPExistant.size() > 0) {
			for (int i = 0; i < diplomeFPExistant.size(); i++) {
				DiplomeFP diplomeFP = (DiplomeFP) diplomeFPExistant.get(i);
				diplomeFP.supprimerDiplomeFP(getTransaction());
			}
		}
		// on ajoute le diplome dela FDP
		DiplomeGenerique diplomeAAjouter = (DiplomeGenerique) getListeTousDiplomes().get(0);
		DiplomeFP diplomeFP = new DiplomeFP(getFichePosteCourante().getIdFichePoste(), diplomeAAjouter.getIdDiplomeGenerique());
		diplomeFP.creerDiplomeFP(getTransaction());

		// on supprime tous les niveau etude de la FDP
		ArrayList niveauFPExistant = NiveauEtudeFP.listerNiveauEtudeFPAvecFP(getTransaction(), getFichePosteCourante());
		if (niveauFPExistant != null && niveauFPExistant.size() > 0) {
			for (int i = 0; i < niveauFPExistant.size(); i++) {
				NiveauEtudeFP niveauFP = (NiveauEtudeFP) niveauFPExistant.get(i);
				niveauFP.supprimerNiveauEtudeFP(getTransaction());
			}
		}
		// on ajoute le niveau etude dela FDP
		NiveauEtude niveauAAjouter = (NiveauEtude) getListeTousNiveau().get(0);
		NiveauEtudeFP niveauFP = new NiveauEtudeFP(getFichePosteCourante().getIdFichePoste(), niveauAAjouter.getIdNiveauEtude());
		niveauFP.creerNiveauEtudeFP(getTransaction());

		// nouvelle gestion des activites
		boolean auMoinsUneligneSelect = false;
		Activite acti = null;
		for (int i = 0; i < getListeToutesActi().size(); i++) {
			// on recupère la ligne concernée
			acti = (Activite) getListeToutesActi().get(i);
			// si la ligne est cochée
			if (getVAL_CK_SELECT_LIGNE_ACTI(i).equals(getCHECKED_ON())) {
				auMoinsUneligneSelect = true;
				// on regarde de quelle liste elle faisait partie
				for (int j = 0; j < getListeActiFEP().size(); j++) {
					Activite actiFP = (Activite) getListeActiFEP().get(j);
					if (acti.getIdActivite().equals(actiFP.getIdActivite())) {
						ActiviteFP actFP = ActiviteFP.chercherActiviteFP(getTransaction(), getFichePosteCourante().getIdFichePoste(),
								acti.getIdActivite());
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
							actFP = new ActiviteFP(getFichePosteCourante().getIdFichePoste(), acti.getIdActivite(), true);
							actFP.creerActiviteFP(getTransaction());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
								getTransaction().declarerErreur(MessageUtils.getMessage("ERR976", "Activite (" + acti.getNomActivite() + ")"));
								return false;
							}
							break;
						} else {
							actFP.setActivitePrincipale(true);
							actFP.modifierActiviteFP(getTransaction());
						}
					}
				}
				for (int j = 0; j < getListeActiFES().size(); j++) {
					Activite actiFP = (Activite) getListeActiFES().get(j);
					if (acti.getIdActivite().equals(actiFP.getIdActivite())) {
						ActiviteFP actFP = ActiviteFP.chercherActiviteFP(getTransaction(), getFichePosteCourante().getIdFichePoste(),
								acti.getIdActivite());
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
							actFP = new ActiviteFP(getFichePosteCourante().getIdFichePoste(), acti.getIdActivite(), true);
							actFP.creerActiviteFP(getTransaction());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
								getTransaction().declarerErreur(MessageUtils.getMessage("ERR976", "Activite (" + acti.getNomActivite() + ")"));
								return false;
							}
							break;
						} else {
							actFP.setActivitePrincipale(true);
							actFP.modifierActiviteFP(getTransaction());
						}
					}
				}
				if (getListeActiFP() != null) {
					for (int j = 0; j < getListeActiFP().size(); j++) {
						ActiviteFP actiFP = (ActiviteFP) getListeActiFP().get(j);
						if (acti.getIdActivite().equals(actiFP.getIdActivite())) {
							ActiviteFP actFP = ActiviteFP.chercherActiviteFP(getTransaction(), getFichePosteCourante().getIdFichePoste(),
									acti.getIdActivite());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
								actFP = new ActiviteFP(getFichePosteCourante().getIdFichePoste(), acti.getIdActivite(), false);
								actFP.creerActiviteFP(getTransaction());
								if (getTransaction().isErreur()) {
									getTransaction().traiterErreur();
									getTransaction().declarerErreur(MessageUtils.getMessage("ERR976", "Activite (" + acti.getNomActivite() + ")"));
									return false;
								}
								break;
							} else {
								actFP.setActivitePrincipale(false);
								actFP.modifierActiviteFP(getTransaction());
							}
						}
					}

				}
				if (getListeAjoutActiFP() != null) {
					for (int j = 0; j < getListeAjoutActiFP().size(); j++) {
						Activite actiFP = (Activite) getListeAjoutActiFP().get(j);
						if (acti.getIdActivite().equals(actiFP.getIdActivite())) {
							ActiviteFP actFP = ActiviteFP.chercherActiviteFP(getTransaction(), getFichePosteCourante().getIdFichePoste(),
									acti.getIdActivite());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
								actFP = new ActiviteFP(getFichePosteCourante().getIdFichePoste(), acti.getIdActivite(), false);
								actFP.creerActiviteFP(getTransaction());
								if (getTransaction().isErreur()) {
									getTransaction().traiterErreur();
									getTransaction().declarerErreur(MessageUtils.getMessage("ERR976", "Activite (" + acti.getNomActivite() + ")"));
									return false;
								}
								break;
							} else {
								actFP.setActivitePrincipale(false);
								actFP.modifierActiviteFP(getTransaction());
							}
						}
					}

				}

			} else {
				ActiviteFP actFP = ActiviteFP.chercherActiviteFP(getTransaction(), getFichePosteCourante().getIdFichePoste(), acti.getIdActivite());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				} else {
					actFP.supprimerActiviteFP(getTransaction());
				}

			}
		}
		if (!auMoinsUneligneSelect) {
			// "ERR008", Aucun élément n'est sélectionné dans la liste des @.
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR008", "activités"));
			return false;
		}

		// nouvelle gestion des activites
		Competence comp = null;
		for (int i = 0; i < getListeToutesComp().size(); i++) {
			// on recupère la ligne concernée
			comp = (Competence) getListeToutesComp().get(i);
			// si la ligne est cochée
			if (getVAL_CK_SELECT_LIGNE_COMP(i).equals(getCHECKED_ON())) {
				// on regarde de quelle liste elle faisait partie
				for (int j = 0; j < getListeCompFEP().size(); j++) {
					Competence compFP = (Competence) getListeCompFEP().get(j);
					if (comp.getIdCompetence().equals(compFP.getIdCompetence())) {
						CompetenceFP comFP = CompetenceFP.chercherCompetenceFP(getTransaction(), getFichePosteCourante().getIdFichePoste(),
								comp.getIdCompetence());
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
							comFP = new CompetenceFP(getFichePosteCourante().getIdFichePoste(), comp.getIdCompetence());
							comFP.creerCompetenceFP(getTransaction());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
								getTransaction().declarerErreur(MessageUtils.getMessage("ERR976", "Compétence (" + comp.getNomCompetence() + ")"));
								return false;
							}
							break;
						} else {
							comFP.modifierCompetenceFP(getTransaction());
						}
					}
				}
				for (int j = 0; j < getListeCompFES().size(); j++) {
					Competence compFP = (Competence) getListeCompFES().get(j);
					if (comp.getIdCompetence().equals(compFP.getIdCompetence())) {
						CompetenceFP comFP = CompetenceFP.chercherCompetenceFP(getTransaction(), getFichePosteCourante().getIdFichePoste(),
								comp.getIdCompetence());
						if (getTransaction().isErreur()) {
							getTransaction().traiterErreur();
							comFP = new CompetenceFP(getFichePosteCourante().getIdFichePoste(), comp.getIdCompetence());
							comFP.creerCompetenceFP(getTransaction());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
								getTransaction().declarerErreur(MessageUtils.getMessage("ERR976", "Compétence (" + comp.getNomCompetence() + ")"));
								return false;
							}
							break;
						} else {
							comFP.modifierCompetenceFP(getTransaction());
						}
					}
				}
				if (getListeCompFP() != null) {
					for (int j = 0; j < getListeCompFP().size(); j++) {
						CompetenceFP compFP = (CompetenceFP) getListeCompFP().get(j);
						if (comp.getIdCompetence().equals(compFP.getIdCompetence())) {
							CompetenceFP comFP = CompetenceFP.chercherCompetenceFP(getTransaction(), getFichePosteCourante().getIdFichePoste(),
									comp.getIdCompetence());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
								comFP = new CompetenceFP(getFichePosteCourante().getIdFichePoste(), comp.getIdCompetence());
								comFP.creerCompetenceFP(getTransaction());
								if (getTransaction().isErreur()) {
									getTransaction().traiterErreur();
									getTransaction()
											.declarerErreur(MessageUtils.getMessage("ERR976", "Compétence (" + comp.getNomCompetence() + ")"));
									return false;
								}
								break;
							} else {
								comFP.modifierCompetenceFP(getTransaction());
							}
						}
					}

				}
				if (getListeAjoutCompFP() != null) {
					for (int j = 0; j < getListeAjoutCompFP().size(); j++) {
						Competence compFP = (Competence) getListeAjoutCompFP().get(j);
						if (comp.getIdCompetence().equals(compFP.getIdCompetence())) {
							CompetenceFP comFP = CompetenceFP.chercherCompetenceFP(getTransaction(), getFichePosteCourante().getIdFichePoste(),
									comp.getIdCompetence());
							if (getTransaction().isErreur()) {
								getTransaction().traiterErreur();
								comFP = new CompetenceFP(getFichePosteCourante().getIdFichePoste(), comp.getIdCompetence());
								comFP.creerCompetenceFP(getTransaction());
								if (getTransaction().isErreur()) {
									getTransaction().traiterErreur();
									getTransaction()
											.declarerErreur(MessageUtils.getMessage("ERR976", "Compétence (" + comp.getNomCompetence() + ")"));
									return false;
								}
								break;
							} else {
								comFP.modifierCompetenceFP(getTransaction());
							}
						}
					}

				}

			} else {
				CompetenceFP comFP = CompetenceFP.chercherCompetenceFP(getTransaction(), getFichePosteCourante().getIdFichePoste(),
						comp.getIdCompetence());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				} else {
					comFP.supprimerCompetenceFP(getTransaction());
				}

			}
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (07/07/11 10:59:29)
	 * 
	 * RG_PE_FP_A02
	 */

	public boolean performPB_CREER(HttpServletRequest request) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);

		// Contrôle des champs
		if (!performControlerSaisie(request))
			return false;

		// Contrôle des RGs
		if (!performControlerRG(request))
			return false;

		// Alimentation de la fiche de poste
		if (!alimenterFichePoste(request))
			return false;

		// Création de la fiche emploi
		if (getFichePosteCourante().getIdFichePoste() == null) {
			getFichePosteCourante().creerFichePoste(getTransaction(), user);
			if (getVAL_ST_ACTION().equals(ACTION_CREATION)) {
				// Fiche poste créée
				messageInf = MessageUtils.getMessage("INF103", getFichePosteCourante().getNumFP());
				// pour reinitialiser la fenetre
				setStatut(STATUT_RECHERCHE);
				addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
			} else {
				// Fiche poste dupliquée
				messageInf = MessageUtils.getMessage("INF104", getFichePosteCourante().getNumFP());
				setStatut(STATUT_DUPLIQUER);
			}
		} else {
			getFichePosteCourante().modifierFichePoste(getTransaction(), user);
			if (getVAL_ST_ACTION().equals(ACTION_IMPRESSION)) {
				// Fiche poste imprimée
				messageInf = MessageUtils.getMessage("INF111", getFichePosteCourante().getNumFP());
			} else {
				// Fiche poste modifiée
				messageInf = MessageUtils.getMessage("INF106", getFichePosteCourante().getNumFP());
			}
		}

		if (getTransaction().isErreur()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR111"));
			return false;
		}

		if (!saveJoin(request))
			return false;

		if (!getTransaction().isErreur()) {

			// Sauvegarde des nouveaux avantages nature et suppression des
			// anciens
			for (int i = 0; i < getListeAvantageAAjouter().size(); i++) {
				AvantageNature avNat = (AvantageNature) getListeAvantageAAjouter().get(i);
				avNat.creerAvantageNature(getTransaction());
				AvantageNatureFP avNatFP = new AvantageNatureFP(getFichePosteCourante().getIdFichePoste(), avNat.getIdAvantage());
				avNatFP.creerAvantageNatureFP(getTransaction());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					getTransaction().declarerErreur(" Au moins un avantage en nature n'a pu être créé.");
					return false;
				}
			}
			for (int i = 0; i < getListeAvantageASupprimer().size(); i++) {
				AvantageNature avNat = (AvantageNature) getListeAvantageASupprimer().get(i);
				AvantageNatureFP avNatFP = new AvantageNatureFP(getFichePosteCourante().getIdFichePoste(), avNat.getIdAvantage());
				avNatFP.supprimerAvantageNatureFP(getTransaction());
				avNat.supprimerAvantageNature(getTransaction());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					getTransaction().declarerErreur(" Au moins un avantage en nature n'a pu être supprimé.");
					return false;
				}
			}

			// Sauvegarde des nouvelles Delegation et suppression des anciennes
			for (int i = 0; i < getListeDelegationAAjouter().size(); i++) {
				Delegation deleg = (Delegation) getListeDelegationAAjouter().get(i);
				deleg.creerDelegation(getTransaction());
				DelegationFP delFP = new DelegationFP(getFichePosteCourante().getIdFichePoste(), deleg.getIdDelegation());
				delFP.creerDelegationFP(getTransaction());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					getTransaction().declarerErreur(" Au moins une Delegation n'a pu être créée.");
					return false;
				}
			}
			for (int i = 0; i < getListeDelegationASupprimer().size(); i++) {
				Delegation deleg = (Delegation) getListeDelegationASupprimer().get(i);
				DelegationFP delFP = new DelegationFP(getFichePosteCourante().getIdFichePoste(), deleg.getIdDelegation());
				delFP.supprimerDelegationFP(getTransaction());
				deleg.supprimerDelegation(getTransaction());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					getTransaction().declarerErreur(" Au moins une Delegation n'a pu être supprimée.");
					return false;
				}
			}

			// Sauvegarde des nouveaux RegimeIndemnitaire et suppression des
			// anciens
			for (int i = 0; i < getListeRegimeAAjouter().size(); i++) {
				RegimeIndemnitaire regIndemn = (RegimeIndemnitaire) getListeRegimeAAjouter().get(i);
				regIndemn.creerRegimeIndemnitaire(getTransaction());
				RegIndemFP riFP = new RegIndemFP(getFichePosteCourante().getIdFichePoste(), regIndemn.getIdRegIndemn());
				riFP.creerRegIndemFP(getTransaction());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					getTransaction().declarerErreur(" Au moins un RegimeIndemnitaire n'a pu être créé.");
					return false;
				}
			}
			for (int i = 0; i < getListeRegimeASupprimer().size(); i++) {
				RegimeIndemnitaire ri = (RegimeIndemnitaire) getListeRegimeASupprimer().get(i);
				RegIndemFP riFP = new RegIndemFP(getFichePosteCourante().getIdFichePoste(), ri.getIdRegIndemn());
				riFP.supprimerRegIndemFP(getTransaction());
				if (!(RegIndemnAFF.listerRegIndemnAFFAvecRI(getTransaction(), ri).size() > 0))
					ri.supprimerRegimeIndemnitaire(getTransaction());
				if (getTransaction().isErreur()) {
					getTransaction().declarerErreur("Au moins un RegimeIndemnitaire n'a pu être supprimé.");
					return false;
				}
			}

			// COMMIT
			commitTransaction();

			setListeAjoutActiFP(null);
			initialiseActivites();

			setListeAjoutCompFP(null);
			initialiseCompetence();

			// Suppression des listes de spécificités en session
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_AV_NATURE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_AV_NATURE_A_AJOUT);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_AV_NATURE_A_SUPPR);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_DELEGATION);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_DELEGATION_A_AJOUT);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_DELEGATION_A_SUPPR);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_REG_INDEMN);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_REG_INDEMN_A_AJOUT);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_LST_REG_INDEMN_A_SUPPR);

			// si la FDP est affectée à un agent, alors on sauvegarde la fiche
			// de poste
			// RG_PE_FP_A02
			if (estFpCouranteAffectee()) {
				if (!sauvegardeFDP()) {
					return false;
				}
			}

		} else {
			return false;
		}

		majChangementFEAutorise();

		if (!messageInf.equals(Const.CHAINE_VIDE)) {
			addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
			getTransaction().declarerErreur(messageInf);
		}

		// appel WS mise à jour Abre FDP
		if (!miseAJourArbreFDP()) {
			// "ERR970",
			// "Une erreur est survenue lors de la mise à jour de l'arbre des Fiche de poste. Merci de contacter le responsable du projet car celà engendre un soucis sur le Kiosque RH."
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR970"));
			messageInf = "";
			return false;
		}
		return true;
	}

	private boolean miseAJourArbreFDP() {

		String urlWSArbreFDP = (String) ServletAgent.getMesParametres().get("SIRH_WS_URL_ARBRE_FDP");
		boolean response = true;

		try {
			URL url = new URL(urlWSArbreFDP);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			try {
				if (conn.getResponseCode() != 200) {
					response = false;
					logger.error("Failed Arbre service : HTTP error code : " + conn.getResponseCode());
				}
			} catch (Exception e) {
				response = false;
				logger.error("Erreur dans la connexion à l'url des WS SIRH", e);
			}
		} catch (Exception e) {
			logger.error("Erreur dans la connexion à l'url des WS SIRH", e);
		}

		return response;

	}

	private boolean sauvegardeFDP() throws Exception {
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
		String dateJour = new SimpleDateFormat("ddMMyyyy-hhmm").format(new Date()).toString();
		String destination = "SauvegardeFDP/SauvFP_" + getFichePosteCourante().getIdFichePoste() + "_" + dateJour + ".xml";

		String modele = "ModeleFP.xml";
		String repModeles = (String) ServletAgent.getMesParametres().get("REPERTOIRE_MODELES_FICHEPOSTE");

		creerModeleDocumentFP("SauvegardeFDP", repModeles + modele, repPartage + destination, getFichePosteCourante().getIdFichePoste());

		// Tout s'est bien passé
		// on crée le document en base de données
		Document d = new Document();
		d.setIdTypeDocument("1");
		d.setLienDocument(destination);
		d.setNomDocument("SauvFP_" + getFichePosteCourante().getIdFichePoste() + "_" + dateJour + ".xml");
		d.setDateDocument(new SimpleDateFormat("dd/MM/yyyy").format(new Date()).toString());
		d.setCommentaire("Sauvegarde automatique lors modification FDP.");
		d.creerDocument(getTransaction());

		LienDocumentAgent lda = new LienDocumentAgent();
		lda.setIdAgent(getAgentCourant().getIdAgent());
		lda.setIdDocument(d.getIdDocument());
		lda.creerLienDocumentAgent(getTransaction());

		if (getTransaction().isErreur())
			return false;

		commitTransaction();
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER Date de création
	 * : (07/07/11 11:20:20)
	 * 
	 * 
	 */
	public String getNOM_PB_RECHERCHER() {
		return "NOM_PB_RECHERCHER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (07/07/11 11:20:20)
	 * 
	 * 
	 */
	public boolean performPB_RECHERCHER(HttpServletRequest request) throws Exception {

		// Mise à jour de l'action menée
		addZone(getNOM_ST_ACTION(), ACTION_RECHERCHE);

		// Recherche de la fiche de poste
		if (getVAL_EF_RECHERCHE() != null && !getVAL_EF_RECHERCHE().equals(Const.CHAINE_VIDE)) {
			FichePoste fiche = FichePoste.chercherFichePosteAvecNumeroFP(getTransaction(), getVAL_EF_RECHERCHE());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR125", "la fiche de poste " + getVAL_EF_RECHERCHE()));
				return false;
			}
			if (fiche != null) {
				viderFichePoste();
				viderObjetsFichePoste();
				addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);
				setFichePosteCourante(fiche);
			} else {
				setStatut(STATUT_RECHERCHE, true, MessageUtils.getMessage("ERR008"));
				return false;
			}
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR982"));
			return false;
		}

		setStatut(STATUT_RECHERCHE);
		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_RECHERCHE Date de
	 * création : (07/07/11 11:20:20)
	 * 
	 * 
	 */
	public String getNOM_EF_RECHERCHE() {
		return "NOM_EF_RECHERCHE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_RECHERCHE Date de création : (07/07/11 11:20:20)
	 * 
	 * 
	 */
	public String getVAL_EF_RECHERCHE() {
		return getZone(getNOM_EF_RECHERCHE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NFA Date de création
	 * : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_EF_NFA() {
		return "NOM_EF_NFA";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NFA Date de
	 * création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getVAL_EF_NFA() {
		return getZone(getNOM_EF_NFA());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUMERO Date de
	 * création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_ST_NUMERO() {
		return "NOM_ST_NUMERO";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NUMERO Date de
	 * création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getVAL_ST_NUMERO() {
		return getZone(getNOM_ST_NUMERO());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_BUDGET Date de création :
	 * (07/07/11 13:23:11)
	 * 
	 * 
	 */
	private String[] getLB_BUDGET() {
		if (LB_BUDGET == null)
			LB_BUDGET = initialiseLazyLB();
		return LB_BUDGET;
	}

	/**
	 * Setter de la liste: LB_BUDGET Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	private void setLB_BUDGET(String[] newLB_BUDGET) {
		LB_BUDGET = newLB_BUDGET;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_BUDGET Date de création :
	 * (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_LB_BUDGET() {
		return "NOM_LB_BUDGET";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_BUDGET_SELECT Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_LB_BUDGET_SELECT() {
		return "NOM_LB_BUDGET_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_BUDGET Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String[] getVAL_LB_BUDGET() {
		return getLB_BUDGET();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_BUDGET Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getVAL_LB_BUDGET_SELECT() {
		return getZone(getNOM_LB_BUDGET_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_BUDGETE Date de création
	 * : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	private String[] getLB_BUDGETE() {
		if (LB_BUDGETE == null)
			LB_BUDGETE = initialiseLazyLB();
		return LB_BUDGETE;
	}

	/**
	 * Setter de la liste: LB_BUDGETE Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	private void setLB_BUDGETE(String[] newLB_BUDGETE) {
		LB_BUDGETE = newLB_BUDGETE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_BUDGETE Date de création
	 * : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_LB_BUDGETE() {
		return "NOM_LB_BUDGETE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_BUDGETE_SELECT Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_LB_BUDGETE_SELECT() {
		return "NOM_LB_BUDGETE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_BUDGETE Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String[] getVAL_LB_BUDGETE() {
		return getLB_BUDGETE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_BUDGETE Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getVAL_LB_BUDGETE_SELECT() {
		return getZone(getNOM_LB_BUDGETE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_REGLEMENTAIRE Date de
	 * création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	private String[] getLB_REGLEMENTAIRE() {
		if (LB_REGLEMENTAIRE == null)
			LB_REGLEMENTAIRE = initialiseLazyLB();
		return LB_REGLEMENTAIRE;
	}

	/**
	 * Setter de la liste: LB_REGLEMENTAIRE Date de création : (07/07/11
	 * 13:23:11)
	 * 
	 * 
	 */
	private void setLB_REGLEMENTAIRE(String[] newLB_REGLEMENTAIRE) {
		LB_REGLEMENTAIRE = newLB_REGLEMENTAIRE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_REGLEMENTAIRE Date de
	 * création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_LB_REGLEMENTAIRE() {
		return "NOM_LB_REGLEMENTAIRE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_REGLEMENTAIRE_SELECT Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_LB_REGLEMENTAIRE_SELECT() {
		return "NOM_LB_REGLEMENTAIRE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_REGLEMENTAIRE Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String[] getVAL_LB_REGLEMENTAIRE() {
		return getLB_REGLEMENTAIRE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_REGLEMENTAIRE Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getVAL_LB_REGLEMENTAIRE_SELECT() {
		return getZone(getNOM_LB_REGLEMENTAIRE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_STATUT Date de création :
	 * (07/07/11 13:23:11)
	 * 
	 * 
	 */
	private String[] getLB_STATUT() {
		if (LB_STATUT == null)
			LB_STATUT = initialiseLazyLB();
		return LB_STATUT;
	}

	/**
	 * Setter de la liste: LB_STATUT Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	private void setLB_STATUT(String[] newLB_STATUT) {
		LB_STATUT = newLB_STATUT;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_STATUT Date de création :
	 * (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_LB_STATUT() {
		return "NOM_LB_STATUT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_STATUT_SELECT Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getNOM_LB_STATUT_SELECT() {
		return "NOM_LB_STATUT_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_STATUT Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String[] getVAL_LB_STATUT() {
		return getLB_STATUT();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_STATUT Date de création : (07/07/11 13:23:11)
	 * 
	 * 
	 */
	public String getVAL_LB_STATUT_SELECT() {
		return getZone(getNOM_LB_STATUT_SELECT());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ANNEE Date de
	 * création : (07/07/11 14:19:57)
	 * 
	 * 
	 */
	public String getNOM_EF_ANNEE() {
		return "NOM_EF_ANNEE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_ANNEE Date de création : (07/07/11 14:19:57)
	 * 
	 * 
	 */
	public String getVAL_EF_ANNEE() {
		return getZone(getNOM_EF_ANNEE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_GRADE Date de
	 * création : (07/07/11 14:19:57)
	 * 
	 * 
	 */
	public String getNOM_EF_GRADE() {
		return "NOM_EF_GRADE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_GRADE Date de création : (07/07/11 14:19:57)
	 * 
	 * 
	 */
	public String getVAL_EF_GRADE() {
		return getZone(getNOM_EF_GRADE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_GRADE Date de
	 * création : (07/07/11 14:19:57)
	 * 
	 * 
	 */
	public String getNOM_EF_CODE_GRADE() {
		return "NOM_EF_CODE_GRADE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_GRADE Date de création : (07/07/11 14:19:57)
	 * 
	 * 
	 */
	public String getVAL_EF_CODE_GRADE() {
		return getZone(getNOM_EF_CODE_GRADE());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_DIPLOME Date de création
	 * : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	private String[] getLB_DIPLOME() {
		if (LB_DIPLOME == null)
			LB_DIPLOME = initialiseLazyLB();
		return LB_DIPLOME;
	}

	/**
	 * Setter de la liste: LB_DIPLOME Date de création : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	private void setLB_DIPLOME(String[] newLB_DIPLOME) {
		LB_DIPLOME = newLB_DIPLOME;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_DIPLOME Date de création
	 * : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getNOM_LB_DIPLOME() {
		return "NOM_LB_DIPLOME";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_DIPLOME_SELECT Date de création : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getNOM_LB_DIPLOME_SELECT() {
		return "NOM_LB_DIPLOME_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_DIPLOME Date de création : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String[] getVAL_LB_DIPLOME() {
		return getLB_DIPLOME();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_DIPLOME Date de création : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getVAL_LB_DIPLOME_SELECT() {
		return getZone(getNOM_LB_DIPLOME_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_NIVEAU_ETUDE Date de
	 * création : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	private String[] getLB_NIVEAU_ETUDE() {
		if (LB_NIVEAU_ETUDE == null)
			LB_NIVEAU_ETUDE = initialiseLazyLB();
		return LB_NIVEAU_ETUDE;
	}

	/**
	 * Setter de la liste: LB_NIVEAU_ETUDE Date de création : (08/07/11
	 * 09:13:07)
	 * 
	 * 
	 */
	private void setLB_NIVEAU_ETUDE(String[] newLB_NIVEAU_ETUDE) {
		LB_NIVEAU_ETUDE = newLB_NIVEAU_ETUDE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_NIVEAU_ETUDE Date de
	 * création : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getNOM_LB_NIVEAU_ETUDE() {
		return "NOM_LB_NIVEAU_ETUDE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_NIVEAU_ETUDE_SELECT Date de création : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getNOM_LB_NIVEAU_ETUDE_SELECT() {
		return "NOM_LB_NIVEAU_ETUDE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_NIVEAU_ETUDE Date de création : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String[] getVAL_LB_NIVEAU_ETUDE() {
		return getLB_NIVEAU_ETUDE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_NIVEAU_ETUDE Date de création : (08/07/11 09:13:07)
	 * 
	 * 
	 */
	public String getVAL_LB_NIVEAU_ETUDE_SELECT() {
		return getZone(getNOM_LB_NIVEAU_ETUDE_SELECT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_GRADE Date de
	 * création : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public String getNOM_PB_AJOUTER_GRADE() {
		return "NOM_PB_AJOUTER_GRADE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public boolean performPB_AJOUTER_GRADE(HttpServletRequest request) throws Exception {
		// Récupération du grade à ajouter
		int indiceGrade = (Services.estNumerique(getVAL_LB_GRADE_SELECT()) ? Integer.parseInt(getVAL_LB_GRADE_SELECT()) : -1);
		if (indiceGrade == -1 || indiceGrade == 0 || getListeGrade().size() == 0 || indiceGrade > getListeGrade().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "grade"));
			return false;
		}

		if (indiceGrade != -1) {
			Grade g = (Grade) getListeGrade().get(indiceGrade - 1);
			Grade gr = Grade.chercherGradeByGradeInitial(getTransaction(), g.getGrade());
			addZone(getNOM_EF_GRADE(), gr.getGrade());
			addZone(getNOM_EF_CODE_GRADE(), gr.getCodeGrade());

			GradeGenerique gg = GradeGenerique.chercherGradeGenerique(getTransaction(), gr.getCodeGradeGenerique());
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			}
			// on récupère la categorie et la filiere de ce grade
			String info = "Cat : " + gg.getCodCadre();
			if (gg != null && gg.getCdfili() != null) {
				FiliereGrade fi = FiliereGrade.chercherFiliereGrade(getTransaction(), gg.getCdfili());
				info += " , filière : " + fi.getLibFiliere();
			}
			addZone(getNOM_ST_INFO_GRADE(), info);

		}
		setAfficherListeGrade(false);

		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_DIPLOME Date de
	 * création : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public String getNOM_PB_AJOUTER_DIPLOME() {
		return "NOM_PB_AJOUTER_DIPLOME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public boolean performPB_AJOUTER_DIPLOME(HttpServletRequest request) throws Exception {
		// Récupération du diplome à ajouter
		int indiceDipl = (Services.estNumerique(getVAL_LB_DIPLOME_SELECT()) ? Integer.parseInt(getVAL_LB_DIPLOME_SELECT()) : -1);
		if (indiceDipl == -1 || getListeDiplome().size() == 0 || indiceDipl > getListeDiplome().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Diplomes"));
			return false;
		}

		if (indiceDipl > 0) {
			DiplomeGenerique d = (DiplomeGenerique) getListeDiplome().get(indiceDipl - 1);

			if (d != null) {
				if (getListeTousDiplomes() == null)
					setListeTousDiplomes(new ArrayList());

				if (!getListeTousDiplomes().contains(d)) {
					getListeTousDiplomes().add(d);

					int[] tailles = { 100 };
					String[] champsDG = { "libDiplomeGenerique" };
					setLB_DIPLOME_MULTI(new FormateListe(tailles, getListeTousDiplomes(), champsDG).getListeFormatee());
				}
			}
		}
		setAfficherListeDiplome(false);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_NIVEAU_ETUDE Date de
	 * création : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public String getNOM_PB_AJOUTER_NIVEAU_ETUDE() {
		return "NOM_PB_AJOUTER_NIVEAU_ETUDE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public boolean performPB_AJOUTER_NIVEAU_ETUDE(HttpServletRequest request) throws Exception {
		// Récupération du niveau d'étude à ajouter
		int indiceNiv = (Services.estNumerique(getVAL_LB_NIVEAU_ETUDE_SELECT()) ? Integer.parseInt(getVAL_LB_NIVEAU_ETUDE_SELECT()) : -1);
		if (indiceNiv == -1 || getListeNiveauEtude().size() == 0 || indiceNiv > getListeNiveauEtude().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "Niveaux d'étude"));
			return false;
		}

		if (indiceNiv > 0) {
			NiveauEtude n = (NiveauEtude) getListeNiveauEtude().get(indiceNiv - 1);

			if (n != null) {
				if (getListeTousNiveau() == null)
					setListeTousNiveau(new ArrayList());

				if (!getListeTousNiveau().contains(n)) {
					getListeTousNiveau().add(n);
				}

				String nivEtMulti = Const.CHAINE_VIDE;
				if (getListeTousNiveau() != null) {
					for (int i = 0; i < getListeTousNiveau().size(); i++) {
						NiveauEtude nivEt = (NiveauEtude) getListeTousNiveau().get(i);
						nivEtMulti += nivEt.getLibNiveauEtude() + ", ";
					}
				}
				addZone(getNOM_EF_NIVEAU_ETUDE_MULTI(), nivEtMulti.length() > 0 ? nivEtMulti.substring(0, nivEtMulti.length() - 2) : nivEtMulti);
			}
		}
		setAfficherListeNivEt(false);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_DIPLOME Date de
	 * création : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_DIPLOME() {
		return "NOM_PB_SUPPRIMER_DIPLOME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public boolean performPB_SUPPRIMER_DIPLOME(HttpServletRequest request) throws Exception {

		// Suppression du dernier diplome de la liste
		if (getListeTousDiplomes() != null && getListeTousDiplomes().size() != 0) {
			DiplomeGenerique dip = (DiplomeGenerique) getListeTousDiplomes().get(getListeTousDiplomes().size() - 1);
			getListeTousDiplomes().remove(dip);

			// Rafraichissement de la liste
			int[] tailles = { 100 };
			String[] champsDG = { "libDiplomeGenerique" };
			setLB_DIPLOME_MULTI(new FormateListe(tailles, getListeTousDiplomes(), champsDG).getListeFormatee());

		}
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_NIVEAU_ETUDE Date
	 * de création : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_NIVEAU_ETUDE() {
		return "NOM_PB_SUPPRIMER_NIVEAU_ETUDE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (08/07/11 09:21:06)
	 * 
	 * 
	 */
	public boolean performPB_SUPPRIMER_NIVEAU_ETUDE(HttpServletRequest request) throws Exception {

		// Suppression du dernier niveau d'étude de la liste
		if (getListeTousNiveau() != null && getListeTousNiveau().size() != 0) {
			NiveauEtude niv = (NiveauEtude) getListeTousNiveau().get(getListeTousNiveau().size() - 1);
			getListeTousNiveau().remove(niv);

			// Rafraichissement de la liste
			String nivEtMulti = Const.CHAINE_VIDE;
			if (getListeTousNiveau() != null) {
				for (int i = 0; i < getListeTousNiveau().size(); i++) {
					NiveauEtude nivEt = (NiveauEtude) getListeTousNiveau().get(i);
					nivEtMulti += nivEt.getLibNiveauEtude() + ", ";
				}
			}
			addZone(getNOM_EF_NIVEAU_ETUDE_MULTI(), nivEtMulti.length() > 0 ? nivEtMulti.substring(0, nivEtMulti.length() - 2) : nivEtMulti);

		}

		return true;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_GRADE Date de création :
	 * (11/07/11 11:08:04)
	 * 
	 * 
	 */
	private String[] getLB_GRADE() {
		if (LB_GRADE == null)
			LB_GRADE = initialiseLazyLB();
		return LB_GRADE;
	}

	/**
	 * Setter de la liste: LB_GRADE Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	private void setLB_GRADE(String[] newLB_GRADE) {
		LB_GRADE = newLB_GRADE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_GRADE Date de création :
	 * (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String getNOM_LB_GRADE() {
		return "NOM_LB_GRADE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_GRADE_SELECT Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String getNOM_LB_GRADE_SELECT() {
		return "NOM_LB_GRADE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_GRADE Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String[] getVAL_LB_GRADE() {
		return getLB_GRADE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_GRADE Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String getVAL_LB_GRADE_SELECT() {
		return getZone(getNOM_LB_GRADE_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_LOC Date de création :
	 * (11/07/11 11:08:04)
	 * 
	 * 
	 */
	private String[] getLB_LOC() {
		if (LB_LOC == null)
			LB_LOC = initialiseLazyLB();
		return LB_LOC;
	}

	/**
	 * Setter de la liste: LB_LOC Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	private void setLB_LOC(String[] newLB_LOC) {
		LB_LOC = newLB_LOC;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_LOC Date de création :
	 * (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String getNOM_LB_LOC() {
		return "NOM_LB_LOC";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_LOC_SELECT Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String getNOM_LB_LOC_SELECT() {
		return "NOM_LB_LOC_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_LOC Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String[] getVAL_LB_LOC() {
		return getLB_LOC();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_LOC Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String getVAL_LB_LOC_SELECT() {
		return getZone(getNOM_LB_LOC_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_TITRE_POSTE Date de
	 * création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	private String[] getLB_TITRE_POSTE() {
		if (LB_TITRE_POSTE == null)
			LB_TITRE_POSTE = initialiseLazyLB();
		return LB_TITRE_POSTE;
	}

	/**
	 * Setter de la liste: LB_TITRE_POSTE Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	private void setLB_TITRE_POSTE(String[] newLB_TITRE_POSTE) {
		LB_TITRE_POSTE = newLB_TITRE_POSTE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_TITRE_POSTE Date de
	 * création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String getNOM_LB_TITRE_POSTE() {
		return "NOM_LB_TITRE_POSTE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_TITRE_POSTE_SELECT Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String getNOM_LB_TITRE_POSTE_SELECT() {
		return "NOM_LB_TITRE_POSTE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_TITRE_POSTE Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String[] getVAL_LB_TITRE_POSTE() {
		return getLB_TITRE_POSTE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_TITRE_POSTE Date de création : (11/07/11 11:08:04)
	 * 
	 * 
	 */
	public String getVAL_LB_TITRE_POSTE_SELECT() {
		return getZone(getNOM_LB_TITRE_POSTE_SELECT());
	}

	private ArrayList getListeBudget() {
		return listeBudget;
	}

	private void setListeBudget(ArrayList listeBudget) {
		this.listeBudget = listeBudget;
	}

	private ArrayList getListeGrade() {
		return listeGrade;
	}

	private void setListeGrade(ArrayList listeGrade) {
		this.listeGrade = listeGrade;
	}

	private ArrayList getListeLocalisation() {
		return listeLocalisation;
	}

	private void setListeLocalisation(ArrayList listeLocalisation) {
		this.listeLocalisation = listeLocalisation;
	}

	private ArrayList getListeStatut() {
		return listeStatut;
	}

	private void setListeStatut(ArrayList listeStatut) {
		this.listeStatut = listeStatut;
	}

	public ArrayList getListeTitre() {
		return listeTitre;
	}

	private void setListeTitre(ArrayList listeTitre) {
		this.listeTitre = listeTitre;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_DIPLOME_MULTI Date de
	 * création : (11/07/11 14:22:23)
	 * 
	 * 
	 */
	private String[] getLB_DIPLOME_MULTI() {
		if (LB_DIPLOME_MULTI == null)
			LB_DIPLOME_MULTI = initialiseLazyLB();
		return LB_DIPLOME_MULTI;
	}

	/**
	 * Setter de la liste: LB_DIPLOME_MULTI Date de création : (11/07/11
	 * 14:22:23)
	 * 
	 * 
	 */
	private void setLB_DIPLOME_MULTI(String[] newLB_DIPLOME_MULTI) {
		LB_DIPLOME_MULTI = newLB_DIPLOME_MULTI;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_DIPLOME_MULTI Date de
	 * création : (11/07/11 14:22:23)
	 * 
	 * 
	 */
	public String getNOM_LB_DIPLOME_MULTI() {
		return "NOM_LB_DIPLOME_MULTI";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_DIPLOME_MULTI_SELECT Date de création : (11/07/11 14:22:23)
	 * 
	 * 
	 */
	public String getNOM_LB_DIPLOME_MULTI_SELECT() {
		return "NOM_LB_DIPLOME_MULTI_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_DIPLOME_MULTI Date de création : (11/07/11 14:22:23)
	 * 
	 * 
	 */
	public String[] getVAL_LB_DIPLOME_MULTI() {
		return getLB_DIPLOME_MULTI();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_DIPLOME_MULTI Date de création : (11/07/11 14:22:23)
	 * 
	 * 
	 */
	public String getVAL_LB_DIPLOME_MULTI_SELECT() {
		return getZone(getNOM_LB_DIPLOME_MULTI_SELECT());
	}

	private FichePoste getFichePosteCourante() {
		return fichePosteCourante;
	}

	/**
	 * Setter de la fiche de poste courante.
	 * 
	 * @param fichePosteCourante
	 * @throws Exception
	 */
	private void setFichePosteCourante(FichePoste fichePosteCourante) throws Exception {
		this.fichePosteCourante = fichePosteCourante;

		if (fichePosteCourante != null && fichePosteCourante.getIdFichePoste() != null) {
			// Vérifie l'affectation
			setFpCouranteAffectee(getFichePosteCourante().estAffectée(getTransaction()));
			// Init fiches emploi
			setEmploiPrimaire(FicheEmploi.chercherFicheEmploiAvecFichePoste(getTransaction(), getFichePosteCourante(), true));
			setEmploiSecondaire(FicheEmploi.chercherFicheEmploiAvecFichePoste(getTransaction(), getFichePosteCourante(), false));

			// Init Service
			if (getFichePosteCourante().getIdServi() != null && getFichePosteCourante().getIdServi().length() != 0)
				setService(Service.chercherService(getTransaction(), getFichePosteCourante().getIdServi()));
			// Init Responsable
			if (getFichePosteCourante().getIdResponsable() != null && getFichePosteCourante().getIdResponsable().length() != 0)
				setResponsable(FichePoste.chercherFichePoste(getTransaction(), getFichePosteCourante().getIdResponsable()));
			// Init Remplacement
			if (getFichePosteCourante().getIdRemplacement() != null && getFichePosteCourante().getIdRemplacement().length() != 0)
				setRemplacement(FichePoste.chercherFichePoste(getTransaction(), getFichePosteCourante().getIdRemplacement()));
			// Init Infos Affectation FP
			setAgentCourant(AgentNW.chercherAgentAffecteFichePoste(getTransaction(), getFichePosteCourante().getIdFichePoste()));
			// si on a pas trouve d'gent affecté sur FP primaire, on recherche
			// sur secondaire
			if (getAgentCourant() == null)
				setAgentCourant(AgentNW.chercherAgentAffecteFichePosteSecondaire(getTransaction(), getFichePosteCourante().getIdFichePoste()));
			if (getAgentCourant() != null) {
				Affectation aff = Affectation.chercherAffectationActiveAvecAgent(getTransaction(), getAgentCourant().getIdAgent());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
					setAffectationCourante(null);
				} else {
					setAffectationCourante(aff);
				}
				Contrat c = Contrat.chercherContratAgentDateComprise(getTransaction(), getAgentCourant().getIdAgent(), Services.dateDuJour());
				if (getTransaction().isErreur()) {
					getTransaction().traiterErreur();
				}
				setContratCourant(c);
				if (getContratCourant() != null && getContratCourant().getIdTypeContrat() != null)
					setTypeContratCourant(TypeContrat.chercherTypeContrat(getTransaction(), getContratCourant().getIdTypeContrat()));
			}

			majChangementFEAutorise();
		} else {
			setFpCouranteAffectee(false);
		}
	}

	private void initialiseActivites() throws Exception {
		// on fait une liste de toutes les activites
		setListeToutesActi(new ArrayList());
		boolean trouve = false;
		if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {
			// on recupere les activites de la FDP
			setListeActiFP(ActiviteFP.listerActiviteFPAvecFP(getTransaction(), getFichePosteCourante().getIdFichePoste()));
			for (int i = 0; i < getListeActiFP().size(); i++) {
				trouve = false;
				ActiviteFP actiFP = (ActiviteFP) getListeActiFP().get(i);
				Activite activite = Activite.chercherActivite(getTransaction(), actiFP.getIdActivite());
				for (int j = 0; j < getListeToutesActi().size(); j++) {
					Activite tteActi = (Activite) getListeToutesActi().get(j);
					if (tteActi.getIdActivite().equals(activite.getIdActivite())) {
						trouve = true;
						break;
					}
				}
				if (!trouve) {
					getListeToutesActi().add(activite);
					getHashOrigineActivite().put(activite.getIdActivite(), "FDP");
				}
			}
		} else {
			setListeActiFP(new ArrayList());
		}

		// on recupere les activites des differentes FE
		trouve = false;
		if (getEmploiPrimaire() != null && getEmploiPrimaire().getIdFicheEmploi() != null) {
			setListeActiFEP(Activite.listerActiviteAvecFE(getTransaction(), getEmploiPrimaire()));
			for (int i = 0; i < getListeActiFEP().size(); i++) {
				trouve = false;
				Activite actiFP = (Activite) getListeActiFEP().get(i);
				for (int j = 0; j < getListeToutesActi().size(); j++) {
					Activite tteActi = (Activite) getListeToutesActi().get(j);
					if (tteActi.getIdActivite().equals(actiFP.getIdActivite())) {
						trouve = true;
						break;
					}
				}
				if (!trouve) {
					getListeToutesActi().add(actiFP);
					getHashOrigineActivite().put(actiFP.getIdActivite(), getEmploiPrimaire().getRefMairie());
				}
			}
		} else {
			setListeActiFEP(new ArrayList());
		}

		if (getEmploiSecondaire() != null && getEmploiSecondaire().getIdFicheEmploi() != null) {
			trouve = false;
			setListeActiFES(Activite.listerActiviteAvecFE(getTransaction(), getEmploiSecondaire()));
			for (int i = 0; i < getListeActiFES().size(); i++) {
				trouve = false;
				Activite actiFP = (Activite) getListeActiFES().get(i);
				for (int j = 0; j < getListeToutesActi().size(); j++) {
					Activite tteActi = (Activite) getListeToutesActi().get(j);
					if (tteActi.getIdActivite().equals(actiFP.getIdActivite())) {
						trouve = true;
						break;
					}
				}
				if (!trouve) {
					getListeToutesActi().add(actiFP);
					getHashOrigineActivite().put(actiFP.getIdActivite(), getEmploiSecondaire().getRefMairie());
				}
			}

		} else {
			setListeActiFES(new ArrayList());
		}

		// on recupere les activites selectionnées dans l'ecran de selection
		ArrayList listeActiSelect = (ArrayList) VariablesActivite.recuperer(this, "ACTIVITE_PRINC");
		if (listeActiSelect != null && listeActiSelect.size() != 0) {
			if (getListeAjoutActiFP() != null) {
				getListeAjoutActiFP().addAll(listeActiSelect);
			} else {
				setListeAjoutActiFP(listeActiSelect);
			}
			for (int i = 0; i < getListeAjoutActiFP().size(); i++) {
				Activite a = (Activite) getListeAjoutActiFP().get(i);
				if (a != null) {
					if (getListeToutesActi() == null)
						setListeToutesActi(new ArrayList());
					if (!getListeToutesActi().contains(a)) {
						getListeToutesActi().add(a);
						getHashOrigineActivite().put(a.getIdActivite(), "FDP");
					}
				}
			}

		} else {
			setListeAjoutActiFP(new ArrayList());
		}

		// Si liste activites vide alors initialisation.
		boolean dejaCoche = false;
		for (int i = 0; i < getListeToutesActi().size(); i++) {
			dejaCoche = false;
			Activite activite = (Activite) getListeToutesActi().get(i);
			String origineActi = (String) getHashOrigineActivite().get(activite.getIdActivite());

			if (activite != null) {
				addZone(getNOM_ST_ID_ACTI(i), activite.getIdActivite());
				addZone(getNOM_ST_LIB_ACTI(i), activite.getNomActivite());
				addZone(getNOM_ST_LIB_ORIGINE_ACTI(i), origineActi);
				addZone(getNOM_CK_SELECT_LIGNE_ACTI(i), getCHECKED_OFF());

				if (getListeActiFP() != null) {
					// si l'activite fait partie de la liste des activites de la
					// FDP
					for (int j = 0; j < getListeActiFP().size(); j++) {
						ActiviteFP actiFP = (ActiviteFP) getListeActiFP().get(j);
						Activite activiteFP = Activite.chercherActivite(getTransaction(), actiFP.getIdActivite());
						if (activiteFP.equals(activite)) {
							addZone(getNOM_CK_SELECT_LIGNE_ACTI(i), getCHECKED_ON());
							dejaCoche = true;
							break;
						} else {
							if (!dejaCoche) {
								addZone(getNOM_CK_SELECT_LIGNE_ACTI(i), getCHECKED_OFF());
							}
						}
					}
				} else {
					addZone(getNOM_CK_SELECT_LIGNE_ACTI(i), getCHECKED_OFF());
				}
				if (getListeAjoutActiFP() != null) {
					// si l'activite fait partie de la liste des activites
					// ajoutées à la FDP
					for (int j = 0; j < getListeAjoutActiFP().size(); j++) {
						Activite activiteFP = (Activite) getListeAjoutActiFP().get(j);
						if (activiteFP.equals(activite)) {
							addZone(getNOM_CK_SELECT_LIGNE_ACTI(i), getCHECKED_ON());
							dejaCoche = true;
							break;
						} else {
							if (!dejaCoche) {
								addZone(getNOM_CK_SELECT_LIGNE_ACTI(i), getCHECKED_OFF());
							}
						}
					}

				} else {
					addZone(getNOM_CK_SELECT_LIGNE_ACTI(i), getCHECKED_OFF());
				}

			}
		}
		VariablesActivite.enlever(this, "ACTIVITE_PRINC");
		VariablesActivite.enlever(this, "LISTEACTIVITE");
	}

	private void initialiseInfoEmploi() throws Exception {
		// on fait une liste de toutes les niveau etude
		setListeTousNiveau(new ArrayList());
		// on fait une liste de toutes les diplomes
		setListeTousDiplomes(new ArrayList());

		// niveau etude
		if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {
			// on recupere les niveau etude de la FDP
			setListeNiveauFP(NiveauEtudeFP.listerNiveauEtudeFPAvecFP(getTransaction(), getFichePosteCourante()));
			for (int i = 0; i < getListeNiveauFP().size(); i++) {
				NiveauEtudeFP niveauFP = (NiveauEtudeFP) getListeNiveauFP().get(i);
				NiveauEtude niveau = NiveauEtude.chercherNiveauEtude(getTransaction(), niveauFP.getIdNiveauEtude());
				getListeTousNiveau().add(niveau);
			}
		} else {
			setListeNiveauFP(new ArrayList());
		}
		// si il n'y avait pas de niveau etude sur la FDP on affiche celle des
		// FE
		// ON ENELEVE CETTE PATIE --> JIRA SIRH-305
		/*
		 * if (!trouve) { if (getEmploiPrimaire() != null &&
		 * getEmploiPrimaire().getIdFicheEmploi() != null) { // on recupere les
		 * niveau etude de la FEP
		 * setListeNiveauFEP(NiveauEtudeFE.listerNiveauEtudeFEAvecFE
		 * (getTransaction(), getEmploiPrimaire())); for (int i = 0; i <
		 * getListeNiveauFEP().size(); i++) { NiveauEtudeFE niveauFE =
		 * (NiveauEtudeFE) getListeNiveauFEP().get(i); NiveauEtude niveau =
		 * NiveauEtude.chercherNiveauEtude(getTransaction(),
		 * niveauFE.getIdNiveauEtude()); if
		 * (!getListeTousNiveau().contains(niveau)) {
		 * getListeTousNiveau().add(niveau); } } } else { setListeNiveauFEP(new
		 * ArrayList()); } if (getEmploiSecondaire() != null &&
		 * getEmploiSecondaire().getIdFicheEmploi() != null) { // on recupere
		 * les niveau etude de la FES
		 * setListeNiveauFES(NiveauEtudeFE.listerNiveauEtudeFEAvecFE
		 * (getTransaction(), getEmploiSecondaire())); for (int i = 0; i <
		 * getListeNiveauFES().size(); i++) { NiveauEtudeFE niveauFE =
		 * (NiveauEtudeFE) getListeNiveauFES().get(i); NiveauEtude niveau =
		 * NiveauEtude.chercherNiveauEtude(getTransaction(),
		 * niveauFE.getIdNiveauEtude()); if
		 * (!getListeTousNiveau().contains(niveau)) {
		 * getListeTousNiveau().add(niveau); } } } else { setListeNiveauFES(new
		 * ArrayList()); } }
		 */
		String nivEtMulti = Const.CHAINE_VIDE;
		for (int i = 0; i < getListeTousNiveau().size(); i++) {
			NiveauEtude nivEt = (NiveauEtude) getListeTousNiveau().get(i);
			nivEtMulti += nivEt.getLibNiveauEtude() + ", ";
		}
		addZone(getNOM_EF_NIVEAU_ETUDE_MULTI(), nivEtMulti.length() > 0 ? nivEtMulti.substring(0, nivEtMulti.length() - 2) : nivEtMulti);

		// diplome
		if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {
			// on recupere les diplomes de la FDP
			setListeDiplomeFP(DiplomeFP.listerDiplomeFPAvecFP(getTransaction(), getFichePosteCourante()));
			for (int i = 0; i < getListeDiplomeFP().size(); i++) {
				DiplomeFP diplomeFP = (DiplomeFP) getListeDiplomeFP().get(i);
				DiplomeGenerique diplome = DiplomeGenerique.chercherDiplomeGenerique(getTransaction(), diplomeFP.getIdDiplomeGenerique());
				getListeTousDiplomes().add(diplome);
			}
		} else {
			setListeDiplomeFP(new ArrayList());
		}
		// si il n'y avait pas de diplomes sur la FDP on affiche celle des FE
		// ON ENELEVE CETTE PATIE --> JIRA SIRH-305
		/*
		 * if (!trouve) { if (getEmploiPrimaire() != null &&
		 * getEmploiPrimaire().getIdFicheEmploi() != null) { // on recupere les
		 * diplomes de la FEP
		 * setListeDiplomeFEP(DiplomeFE.listerDiplomeFEAvecFE(getTransaction(),
		 * getEmploiPrimaire())); for (int i = 0; i <
		 * getListeDiplomeFEP().size(); i++) { DiplomeFE diplomeFE = (DiplomeFE)
		 * getListeDiplomeFEP().get(i); DiplomeGenerique diplome =
		 * DiplomeGenerique.chercherDiplomeGenerique(getTransaction(),
		 * diplomeFE.getIdDiplomeGenerique()); if
		 * (!getListeTousDiplomes().contains(diplome)) {
		 * getListeTousDiplomes().add(diplome); } } } else {
		 * setListeDiplomeFEP(new ArrayList()); } if (getEmploiSecondaire() !=
		 * null && getEmploiSecondaire().getIdFicheEmploi() != null) { // on
		 * recupere les diplomes de la FES
		 * setListeDiplomeFES(DiplomeFE.listerDiplomeFEAvecFE(getTransaction(),
		 * getEmploiSecondaire())); for (int i = 0; i <
		 * getListeDiplomeFES().size(); i++) { DiplomeFE diplomeFE = (DiplomeFE)
		 * getListeDiplomeFES().get(i); DiplomeGenerique diplome =
		 * DiplomeGenerique.chercherDiplomeGenerique(getTransaction(),
		 * diplomeFE.getIdDiplomeGenerique()); if
		 * (!getListeTousDiplomes().contains(diplome)) {
		 * getListeTousDiplomes().add(diplome); } } } else {
		 * setListeDiplomeFES(new ArrayList()); } }
		 */
		int[] taillesDiplome = { 50 };
		String[] champsDiplome = { "libDiplomeGenerique" };
		setLB_DIPLOME_MULTI(new FormateListe(taillesDiplome, getListeTousDiplomes(), champsDiplome).getListeFormatee());
	}

	/**
	 * 
	 * @param l1
	 * @param l2
	 * @return ArrayListe ayant éléminé de la liste l1 les éléments en communs
	 *         avec l2 fonctionne uniquement avec une liste l1 n'ayant pas 2
	 *         elements identiques
	 */
	public static ArrayList elim_doubure_activites(ArrayList l1, ArrayList l2) {
		if (null == l1)
			return null;

		if (null != l2) {
			for (int i = 0; i < l2.size(); i++) {
				for (int j = 0; j < l1.size(); j++) {
					if ((((Activite) l2.get(i)).getIdActivite()).equals(((Activite) l1.get(j)).getIdActivite()))
						l1.remove(j);
				}
			}
		}
		return l1;
	}

	private void initialiseObservation() {
		// Init observation
		setObservation(Const.CHAINE_VIDE);
		if (getFichePosteCourante() != null) {
			if (getFichePosteCourante().getObservation() != null && getFichePosteCourante().getObservation().length() != 0)
				setObservation(getObservation() + " " + getFichePosteCourante().getObservation());
		}

		addZone(getNOM_EF_OBSERVATION(), getObservation() == null ? Const.CHAINE_VIDE : getObservation());
	}

	private void initialiseMission() {
		// Init missions
		setMission(Const.CHAINE_VIDE);
		if (getFichePosteCourante() != null) {
			if (getFichePosteCourante().getMissions() != null && getFichePosteCourante().getMissions().length() != 0) {
				setMission(getMission() + " " + getFichePosteCourante().getMissions());
			} else {
				if (getEmploiPrimaire() != null) {
					if (!getMission().toUpperCase().contains(getEmploiPrimaire().getDefinitionEmploi().toUpperCase())) {
						setMission(getMission() + " " + getEmploiPrimaire().getDefinitionEmploi());
					}
				}
				if (getEmploiSecondaire() != null) {
					if (!getMission().toUpperCase().contains(getEmploiSecondaire().getDefinitionEmploi().toUpperCase())) {
						setMission(getMission() + " " + getEmploiSecondaire().getDefinitionEmploi());
					}
				}
			}
		}

		addZone(getNOM_EF_MISSIONS(), getMission() == null ? Const.CHAINE_VIDE : getMission());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHE_EMPLOI_PRIMAIRE
	 * Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_RECHERCHE_EMPLOI_PRIMAIRE() {
		return "NOM_PB_RECHERCHE_EMPLOI_PRIMAIRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public boolean performPB_RECHERCHE_EMPLOI_PRIMAIRE(HttpServletRequest request) throws Exception {
		setStatut(STATUT_EMPLOI_PRIMAIRE, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHE_EMPLOI_SECONDAIRE
	 * Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_RECHERCHE_EMPLOI_SECONDAIRE() {
		return "NOM_PB_RECHERCHE_EMPLOI_SECONDAIRE";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_EMPLOI_SECONDAIRE
	 * Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_EMPLOI_SECONDAIRE() {
		return "NOM_PB_SUPPRIMER_EMPLOI_SECONDAIRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public boolean performPB_RECHERCHE_EMPLOI_SECONDAIRE(HttpServletRequest request) throws Exception {
		setStatut(STATUT_EMPLOI_SECONDAIRE, true);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 * 
	 */
	public boolean performPB_SUPPRIMER_EMPLOI_SECONDAIRE(HttpServletRequest request) throws Exception {
		// On enlève la fiche emploi secondaire selectionnée
		setEmploiSecondaire(null);
		addZone(getNOM_ST_EMPLOI_SECONDAIRE(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Getter de la fiche emploi primaire
	 * 
	 * @return FicheEmploi primaire
	 */
	public FicheEmploi getEmploiPrimaire() {
		return emploiPrimaire;
	}

	/**
	 * Setter de la fiche emploi primaire
	 * 
	 * @param newEmploiPrimaire
	 * @throws Exception
	 *             RG_PE_FP_C09
	 */
	private void setEmploiPrimaire(FicheEmploi newEmploiPrimaire) throws Exception {
		// RG_PE_FP_C09
		this.emploiPrimaire = newEmploiPrimaire;
		if (newEmploiPrimaire != null) {
			initialiseInfoEmploi();
			initialiseActivites();
			initialiseCompetence();
		}
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_EMPLOI_PRIMAIRE Date
	 * de création : (13/07/11 11:51:02)
	 * 
	 * 
	 */
	public String getNOM_ST_EMPLOI_PRIMAIRE() {
		return "NOM_ST_EMPLOI_PRIMAIRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_EMPLOI_PRIMAIRE Date de création : (13/07/11 11:51:02)
	 * 
	 * 
	 */
	public String getVAL_ST_EMPLOI_PRIMAIRE() {
		return getZone(getNOM_ST_EMPLOI_PRIMAIRE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_EMPLOI_SECONDAIRE
	 * Date de création : (13/07/11 11:51:02)
	 * 
	 * 
	 */
	public String getNOM_ST_EMPLOI_SECONDAIRE() {
		return "NOM_ST_EMPLOI_SECONDAIRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_EMPLOI_SECONDAIRE Date de création : (13/07/11 11:51:02)
	 * 
	 * 
	 */
	public String getVAL_ST_EMPLOI_SECONDAIRE() {
		return getZone(getNOM_ST_EMPLOI_SECONDAIRE());
	}

	private FicheEmploi getEmploiSecondaire() {
		return emploiSecondaire;
	}

	/**
	 * Setter de la fiche emploi secondaire.
	 * 
	 * @param emploiSecondaire
	 * @throws Exception
	 */
	private void setEmploiSecondaire(FicheEmploi newEmploiSecondaire) throws Exception {
		this.emploiSecondaire = newEmploiSecondaire;
		setListeActiFES(null);
		setListeCompFES(null);
		if (newEmploiSecondaire != null) {
			initialiseInfoEmploi();
			initialiseActivites();
			initialiseCompetence();
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_ACTIVITE Date de
	 * création : (25/07/11 09:42:05)
	 * 
	 * 
	 */
	public String getNOM_PB_AJOUTER_ACTIVITE() {
		return "NOM_PB_AJOUTER_ACTIVITE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/07/11 09:42:05)
	 * 
	 * 
	 */
	public boolean performPB_AJOUTER_ACTIVITE(HttpServletRequest request) throws Exception {
		ArrayList listeToutesActi = new ArrayList();
		if (getListeToutesActi() != null) {
			listeToutesActi.addAll(getListeToutesActi());
		}
		VariablesActivite.ajouter(this, "LISTEACTIVITE", listeToutesActi);
		setStatut(STATUT_ACTI_PRINC, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_COMPETENCE_SAVOIR
	 * Date de création : (18/07/11 16:08:47)
	 * 
	 * 
	 */
	public String getNOM_PB_AJOUTER_COMPETENCE_SAVOIR() {
		return "NOM_PB_AJOUTER_COMPETENCE_SAVOIR";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 * 
	 */
	public boolean performPB_AJOUTER_COMPETENCE_SAVOIR(HttpServletRequest request) throws Exception {
		ArrayList listeToutesCompSavoir = new ArrayList();
		if (getListeToutesComp() != null) {
			for (int i = 0; i < getListeToutesComp().size(); i++) {
				Competence c = (Competence) getListeToutesComp().get(i);
				if (c.getIdTypeCompetence().equals(EnumTypeCompetence.SAVOIR.getCode())) {
					listeToutesCompSavoir.add(c);
				}
			}
		}
		VariablesActivite.ajouter(this, "LISTECOMPETENCESAVOIR", listeToutesCompSavoir);
		setStatut(STATUT_COMPETENCE, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_AJOUTER_COMPETENCE_SAVOIR_FAIRE Date de création : (18/07/11 16:08:47)
	 * 
	 * 
	 */
	public String getNOM_PB_AJOUTER_COMPETENCE_SAVOIR_FAIRE() {
		return "NOM_PB_AJOUTER_COMPETENCE_SAVOIR_FAIRE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 * 
	 */
	public boolean performPB_AJOUTER_COMPETENCE_SAVOIR_FAIRE(HttpServletRequest request) throws Exception {
		ArrayList listeToutesCompSavoirFaire = new ArrayList();
		if (getListeToutesComp() != null) {
			for (int i = 0; i < getListeToutesComp().size(); i++) {
				Competence c = (Competence) getListeToutesComp().get(i);
				if (c.getIdTypeCompetence().equals(EnumTypeCompetence.SAVOIR_FAIRE.getCode())) {
					listeToutesCompSavoirFaire.add(c);
				}
			}
		}
		VariablesActivite.ajouter(this, "LISTECOMPETENCESAVOIRFAIRE", listeToutesCompSavoirFaire);
		setStatut(STATUT_COMPETENCE, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP :
	 * PB_AJOUTER_COMPETENCE_COMPORTEMENT Date de création : (18/07/11 16:08:47)
	 * 
	 * 
	 */
	public String getNOM_PB_AJOUTER_COMPETENCE_COMPORTEMENT() {
		return "NOM_PB_AJOUTER_COMPETENCE_COMPORTEMENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/07/11 16:08:47)
	 * 
	 * 
	 */
	public boolean performPB_AJOUTER_COMPETENCE_COMPORTEMENT(HttpServletRequest request) throws Exception {
		ArrayList listeToutesCompComportement = new ArrayList();
		if (getListeToutesComp() != null) {
			for (int i = 0; i < getListeToutesComp().size(); i++) {
				Competence c = (Competence) getListeToutesComp().get(i);
				if (c.getIdTypeCompetence().equals(EnumTypeCompetence.COMPORTEMENT.getCode())) {
					listeToutesCompComportement.add(c);
				}
			}
		}
		VariablesActivite.ajouter(this, "LISTECOMPETENCECOMPORTEMENT", listeToutesCompComportement);
		setStatut(STATUT_COMPETENCE, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER Date de création :
	 * (25/07/11 14:54:42)
	 * 
	 * 
	 */
	public String getNOM_PB_MODIFIER() {
		return "NOM_PB_MODIFIER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/07/11 14:54:42)
	 * 
	 * 
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_SERVICE Date de création : (26/07/11 09:08:33)
	 * 
	 * 
	 */
	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	/**
	 * Retourne la liste des services.
	 * 
	 * @return listeServices
	 */
	public ArrayList getListeServices() {
		return listeServices;
	}

	/**
	 * Met à jour la liste des services.
	 * 
	 * @param listeServices
	 */
	private void setListeServices(ArrayList listeServices) {
		this.listeServices = listeServices;
	}

	/**
	 * Retourne une hashTable de la hiérarchie des Service selon le code
	 * Service.
	 * 
	 * @return hTree
	 */
	public Hashtable<String, TreeHierarchy> getHTree() {
		return hTree;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_SERVICE Date de
	 * création : (25/07/11 16:45:35)
	 * 
	 * 
	 */
	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	private ArrayList getListeHoraire() {
		return listeHoraire;
	}

	private void setListeHoraire(ArrayList listeHoraire) {
		this.listeHoraire = listeHoraire;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_SPECIFICITES Date
	 * de création : (27/07/11 15:49:01)
	 * 
	 * 
	 */
	public String getNOM_PB_MODIFIER_SPECIFICITES() {
		return "NOM_PB_MODIFIER_SPECIFICITES";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (27/07/11 15:49:01)
	 * 
	 * 
	 */
	public boolean performPB_MODIFIER_SPECIFICITES(HttpServletRequest request) throws Exception {
		// Mise à jour des liste de spécificités à modifier.
		if (getListeAvantage() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_AV_NATURE, getListeAvantage());
		if (getListeDelegation() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_DELEGATION, getListeDelegation());
		if (getListeRegime() != null)
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_REG_INDEMN, getListeRegime());

		setStatut(STATUT_SPECIFICITES, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_RESPONSABLE Date
	 * de création : (29/07/11 11:42:25)
	 * 
	 * 
	 */
	public String getNOM_PB_RECHERCHER_RESPONSABLE() {
		return "NOM_PB_RECHERCHER_RESPONSABLE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/07/11 11:42:25)
	 * 
	 * 
	 */
	public boolean performPB_RECHERCHER_RESPONSABLE(HttpServletRequest request) throws Exception {
		if (!getVAL_EF_CODESERVICE().equals(Const.CHAINE_VIDE))
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_SERVICE, Service.chercherService(getTransaction(), getVAL_EF_CODESERVICE()));

		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_RECHERCHE_POSTE_AVANCEE, Boolean.TRUE);

		setStatut(STATUT_RESPONSABLE, true);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_RESPONSABLE Date de
	 * création : (29/07/11 13:47:43)
	 * 
	 * 
	 */
	public String getNOM_ST_RESPONSABLE() {
		return "NOM_ST_RESPONSABLE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_RESPONSABLE
	 * Date de création : (29/07/11 13:47:43)
	 * 
	 * 
	 */
	public String getVAL_ST_RESPONSABLE() {
		return getZone(getNOM_ST_RESPONSABLE());
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
		return getNOM_EF_RECHERCHE();
	}

	/**
	 * @param focus
	 *            à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Getter de la FichePoste Responsable.
	 * 
	 */
	private FichePoste getResponsable() {
		return responsable;
	}

	/**
	 * Setter de la FichePoste Responsable.
	 * 
	 * @param responsable
	 */
	private void setResponsable(FichePoste resp) throws Exception {
		this.responsable = resp;
		if (resp != null) {
			setAgtResponsable(AgentNW.chercherAgentAffecteFichePoste(getTransaction(), getResponsable().getIdFichePoste()));
			setTitrePosteResponsable(TitrePoste.chercherTitrePoste(getTransaction(), getResponsable().getIdTitrePoste()));
		} else {
			setAgtResponsable(null);
			setTitrePosteResponsable(null);
		}
	}

	/**
	 * Retourne la liste des AvantageNature.
	 * 
	 * @return listeAvantage
	 */
	public ArrayList getListeAvantage() {
		if (listeAvantage == null)
			listeAvantage = new ArrayList();
		return listeAvantage;
	}

	/**
	 * Met à jour la liste des AvantageNature.
	 * 
	 * @param listeAvantage
	 */
	private void setListeAvantage(ArrayList listeAvantage) {
		this.listeAvantage = listeAvantage;
	}

	/**
	 * Retourne la liste des AvantageNature à ajouter.
	 * 
	 * @return listeAvantageAAjouter
	 */
	private ArrayList getListeAvantageAAjouter() {
		if (listeAvantageAAjouter == null)
			listeAvantageAAjouter = new ArrayList();
		return listeAvantageAAjouter;
	}

	/**
	 * Met à jour la liste des AvantageNature à ajouter.
	 * 
	 * @param listeAvantageAAjouter
	 *            listeAvantageAAjouter à définir
	 */
	private void setListeAvantageAAjouter(ArrayList listeAvantageAAjouter) {
		this.listeAvantageAAjouter = listeAvantageAAjouter;
	}

	/**
	 * Retourne la liste des AvantageNature à supprimer.
	 * 
	 * @return listeAvantageASupprimer
	 */
	private ArrayList getListeAvantageASupprimer() {
		if (listeAvantageASupprimer == null)
			listeAvantageASupprimer = new ArrayList();
		return listeAvantageASupprimer;
	}

	/**
	 * Met à jour la liste des AvantageNature à supprimer.
	 * 
	 * @param listeAvantageASupprimer
	 */
	private void setListeAvantageASupprimer(ArrayList listeAvantageASupprimer) {
		this.listeAvantageASupprimer = listeAvantageASupprimer;
	}

	/**
	 * Retourne la liste des Delegation.
	 * 
	 * @return listeDelegation
	 */
	public ArrayList getListeDelegation() {
		if (listeDelegation == null)
			listeDelegation = new ArrayList();
		return listeDelegation;
	}

	/**
	 * Met à jour la liste des Delegation.
	 * 
	 * @param listeDelegation
	 *            listeDelegation à définir
	 */
	private void setListeDelegation(ArrayList listeDelegation) {
		this.listeDelegation = listeDelegation;
	}

	/**
	 * Retourne la liste des Delegation à ajouter.
	 * 
	 * @return listeDelegationAAjouter
	 */
	private ArrayList getListeDelegationAAjouter() {
		if (listeDelegationAAjouter == null)
			listeDelegationAAjouter = new ArrayList();
		return listeDelegationAAjouter;
	}

	/**
	 * Met à jour la liste des Delegation à ajouter.
	 * 
	 * @param listeDelegationAAjouter
	 */
	private void setListeDelegationAAjouter(ArrayList listeDelegationAAjouter) {
		this.listeDelegationAAjouter = listeDelegationAAjouter;
	}

	/**
	 * Retourne la liste des Delegation à supprimer.
	 * 
	 * @return listeDelegationASupprimer
	 */
	private ArrayList getListeDelegationASupprimer() {
		if (listeDelegationASupprimer == null)
			listeDelegationASupprimer = new ArrayList();
		return listeDelegationASupprimer;
	}

	/**
	 * Met à jour la liste des Delegation à supprimer.
	 * 
	 * @param listeDelegationASupprimer
	 */
	private void setListeDelegationASupprimer(ArrayList listeDelegationASupprimer) {
		this.listeDelegationASupprimer = listeDelegationASupprimer;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire.
	 * 
	 * @return listeRegime
	 */
	public ArrayList getListeRegime() {
		if (listeRegime == null)
			listeRegime = new ArrayList();
		return listeRegime;
	}

	/**
	 * Met à jour la liste des RegimeIndemnitaire.
	 * 
	 * @param listeRegime
	 */
	private void setListeRegime(ArrayList listeRegime) {
		this.listeRegime = listeRegime;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire à ajouter.
	 * 
	 * @return listeRegimeAAjouter
	 */
	private ArrayList getListeRegimeAAjouter() {
		if (listeRegimeAAjouter == null)
			listeRegimeAAjouter = new ArrayList();
		return listeRegimeAAjouter;
	}

	/**
	 * Met à jour la liste des RegimeIndemnitaire à ajouter.
	 * 
	 * @param listeRegimeAAjouter
	 */
	private void setListeRegimeAAjouter(ArrayList listeRegimeAAjouter) {
		this.listeRegimeAAjouter = listeRegimeAAjouter;
	}

	/**
	 * Retourne la liste des RegimeIndemnitaire à supprimer.
	 * 
	 * @return listeRegimeASupprimer
	 */
	private ArrayList getListeRegimeASupprimer() {
		if (listeRegimeASupprimer == null)
			listeRegimeASupprimer = new ArrayList();
		return listeRegimeASupprimer;
	}

	/**
	 * Met à jour la liste des RegimeIndemnitaire à supprimer.
	 * 
	 * @param listeRegimeASupprimer
	 */
	private void setListeRegimeASupprimer(ArrayList listeRegimeASupprimer) {
		this.listeRegimeASupprimer = listeRegimeASupprimer;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_OBSERVATION Date de
	 * création : (01/08/11 09:12:27)
	 * 
	 * 
	 */
	public String getNOM_EF_OBSERVATION() {
		return "NOM_EF_OBSERVATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_OBSERVATION Date de création : (01/08/11 09:12:27)
	 * 
	 * 
	 */
	public String getVAL_EF_OBSERVATION() {
		return getZone(getNOM_EF_OBSERVATION());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_MISSIONS Date de
	 * création : (01/08/11 09:12:27)
	 * 
	 * 
	 */
	public String getNOM_EF_MISSIONS() {
		return "NOM_EF_MISSIONS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_MISSIONS Date de création : (01/08/11 09:12:27)
	 * 
	 * 
	 */
	public String getVAL_EF_MISSIONS() {
		return getZone(getNOM_EF_MISSIONS());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODESERVICE Date de
	 * création : (12/08/11 11:27:01)
	 * 
	 * 
	 */
	public String getNOM_EF_CODESERVICE() {
		return "NOM_EF_CODESERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODESERVICE Date de création : (12/08/11 11:27:01)
	 * 
	 * 
	 */
	public String getVAL_EF_CODESERVICE() {
		return getZone(getNOM_EF_CODESERVICE());
	}

	/**
	 * Retourne le service sélectionné.
	 * 
	 * @return service
	 */
	private Service getService() {
		return service;
	}

	/**
	 * Met à jour le service sélectionné.
	 * 
	 * @param service
	 *            service à définir
	 */
	private void setService(Service service) {
		this.service = service;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_DEBUT_VALIDITE
	 * Date de création : (22/08/11 09:09:51)
	 * 
	 * 
	 */
	public String getNOM_EF_DATE_DEBUT_VALIDITE() {
		return "NOM_EF_DATE_DEBUT_VALIDITE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_DEBUT_VALIDITE Date de création : (22/08/11 09:09:51)
	 * 
	 * 
	 */
	public String getVAL_EF_DATE_DEBUT_VALIDITE() {
		return getZone(getNOM_EF_DATE_DEBUT_VALIDITE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_FIN_VALIDITE
	 * Date de création : (22/08/11 09:09:51)
	 * 
	 * 
	 */
	public String getNOM_EF_DATE_FIN_VALIDITE() {
		return "NOM_EF_DATE_FIN_VALIDITE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_FIN_VALIDITE Date de création : (22/08/11 09:09:51)
	 * 
	 * 
	 */
	public String getVAL_EF_DATE_FIN_VALIDITE() {
		return getZone(getNOM_EF_DATE_FIN_VALIDITE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_DATE_DEBUT_APPLI_SERV Date de création : (22/08/11 09:09:51)
	 * 
	 * 
	 */
	public String getNOM_EF_DATE_DEBUT_APPLI_SERV() {
		return "NOM_EF_DATE_DEBUT_APPLI_SERV";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_DEBUT_APPLI_SERV Date de création : (22/08/11 09:09:51)
	 * 
	 * 
	 */
	public String getVAL_EF_DATE_DEBUT_APPLI_SERV() {
		return getZone(getNOM_EF_DATE_DEBUT_APPLI_SERV());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_OPI Date de
	 * création : (22/08/11 11:07:24)
	 * 
	 * 
	 */
	public String getNOM_EF_OPI() {
		return "NOM_EF_OPI";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie : EF_OPI
	 * Date de création : (22/08/11 11:07:24)
	 * 
	 * 
	 */
	public String getVAL_EF_OPI() {
		return getZone(getNOM_EF_OPI());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AFFICHER_LISTE_GRADE Date de
	 * création : (29/08/11 10:08:17)
	 * 
	 * 
	 */
	public String getNOM_PB_AFFICHER_LISTE_GRADE() {
		return "NOM_PB_AFFICHER_LISTE_GRADE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/08/11 10:08:17)
	 * 
	 * 
	 */
	public boolean performPB_AFFICHER_LISTE_GRADE(HttpServletRequest request) throws Exception {
		addZone(getNOM_LB_GRADE_SELECT(), "0");
		setAfficherListeGrade(true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AFFICHER_LISTE_DIPLOME Date
	 * de création : (29/08/11 10:08:17)
	 * 
	 * 
	 */
	public String getNOM_PB_AFFICHER_LISTE_DIPLOME() {
		return "NOM_PB_AFFICHER_LISTE_DIPLOME";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/08/11 10:08:17)
	 * 
	 * 
	 */
	public boolean performPB_AFFICHER_LISTE_DIPLOME(HttpServletRequest request) throws Exception {
		addZone(getNOM_LB_DIPLOME_SELECT(), "0");
		setAfficherListeDiplome(true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AFFICHER_LISTE_NIVEAU Date
	 * de création : (29/08/11 10:08:17)
	 * 
	 * 
	 */
	public String getNOM_PB_AFFICHER_LISTE_NIVEAU() {
		return "NOM_PB_AFFICHER_LISTE_NIVEAU";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/08/11 10:08:17)
	 * 
	 * 
	 */
	public boolean performPB_AFFICHER_LISTE_NIVEAU(HttpServletRequest request) throws Exception {
		addZone(getNOM_LB_NIVEAU_ETUDE_SELECT(), "0");
		setAfficherListeNivEt(true);
		return true;
	}

	/**
	 * Retourne vrai si la liste des grades doit être affichée.
	 * 
	 * @return afficherListeGrade boolean
	 */
	public boolean isAfficherListeGrade() {
		return afficherListeGrade;
	}

	/**
	 * Met à jour l'indicateur d'afichage de la liste des grades.
	 * 
	 * @param afficherListeGrade
	 *            boolean
	 */
	private void setAfficherListeGrade(boolean afficherListeGrade) {
		this.afficherListeGrade = afficherListeGrade;
	}

	/**
	 * Retourne vrai si la liste des diplomes doit être affichée.
	 * 
	 * @return afficherListeDiplome boolean
	 */
	public boolean isAfficherListeDiplome() {
		return afficherListeDiplome;
	}

	/**
	 * Met à jour l'indicateur d'afichage de la liste des diplomes.
	 * 
	 * @param afficherListeDiplome
	 *            boolean
	 */
	private void setAfficherListeDiplome(boolean afficherListeDiplome) {
		this.afficherListeDiplome = afficherListeDiplome;
	}

	/**
	 * Retourne vrai si la liste des niveaux d'étude doit être affichée.
	 * 
	 * @return afficherListeNivEt boolean
	 */
	public boolean isAfficherListeNivEt() {
		return afficherListeNivEt;
	}

	/**
	 * Met à jour l'indicateur d'afichage de la liste des niveaux d'étude.
	 * 
	 * @param afficherListeNivEt
	 *            boolean
	 */
	private void setAfficherListeNivEt(boolean afficherListeNivEt) {
		this.afficherListeNivEt = afficherListeNivEt;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_TITRE_POSTE Date de
	 * création : (30/08/11 10:25:41)
	 * 
	 * 
	 */
	public String getNOM_EF_TITRE_POSTE() {
		return "NOM_EF_TITRE_POSTE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_TITRE_POSTE Date de création : (30/08/11 10:25:41)
	 * 
	 * 
	 */
	public String getVAL_EF_TITRE_POSTE() {
		return getZone(getNOM_EF_TITRE_POSTE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NIVEAU_ETUDE_MULTI
	 * Date de création : (31/08/11 10:18:01)
	 * 
	 * 
	 */
	public String getNOM_EF_NIVEAU_ETUDE_MULTI() {
		return "NOM_EF_NIVEAU_ETUDE_MULTI";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NIVEAU_ETUDE_MULTI Date de création : (31/08/11 10:18:01)
	 * 
	 * 
	 */
	public String getVAL_EF_NIVEAU_ETUDE_MULTI() {
		return getZone(getNOM_EF_NIVEAU_ETUDE_MULTI());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_DUPLIQUER_FP Date de
	 * création : (02/09/11 15:58:29)
	 * 
	 * 
	 */
	public String getNOM_PB_DUPLIQUER_FP() {
		return "NOM_PB_DUPLIQUER_FP";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/09/11 15:58:29)
	 * 
	 * 
	 */
	public boolean performPB_DUPLIQUER_FP(HttpServletRequest request) throws Exception {
		if (getFichePosteCourante() != null && getFichePosteCourante().getIdFichePoste() != null) {
			FichePoste fichePDupliquee = (FichePoste) getFichePosteCourante().clone();
			// par defaut on met l'année courante dans l'annéee
			String anneeCourante = Services.dateDuJour().substring(6, 10);
			fichePDupliquee.setAnneeCreation(anneeCourante);
			addZone(getNOM_EF_ANNEE(), anneeCourante);
			fichePDupliquee.setIdFichePoste(null);
			fichePDupliquee.setNumFP(null);
			addZone(getNOM_ST_NUMERO(), Const.CHAINE_VIDE);

			// Duplique les Delegation
			getListeDelegationAAjouter().clear();
			getListeDelegationASupprimer().clear();
			if (getListeDelegation() != null) {
				getListeDelegationAAjouter().addAll(getListeDelegation());
			}

			// Duplique les Avantages en nature
			getListeAvantageAAjouter().clear();
			getListeAvantageASupprimer().clear();
			if (getListeAvantage() != null) {
				getListeAvantageAAjouter().addAll(getListeAvantage());
			}

			// Duplique les Regime indemnitaire
			getListeRegimeAAjouter().clear();
			getListeRegimeASupprimer().clear();
			if (getListeRegime() != null) {
				getListeRegimeAAjouter().addAll(getListeRegime());
			}

			setAgentCourant(null);
			setAffectationCourante(null);
			setContratCourant(null);
			setTypeContratCourant(null);

			initialiseInfoEmploi();
			initialiseActivites();
			initialiseCompetence();

			setFichePosteCourante(fichePDupliquee);

			setStatut(STATUT_A_DUPLIQUER);
			addZone(getNOM_ST_ACTION(), ACTION_DUPLICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("INF107"));
			return false;
		}
		getTransaction().commitTransaction();
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_FP Date de création
	 * : (09/09/11 09:06:13)
	 * 
	 * 
	 */
	public String getNOM_PB_AJOUTER_FP() {
		return "NOM_PB_AJOUTER_FP";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (09/09/11 09:06:13)
	 * 
	 * 
	 */
	public boolean performPB_AJOUTER_FP(HttpServletRequest request) throws Exception {
		viderFichePoste();
		viderObjetsFichePoste();
		setFichePosteCourante(new FichePoste());

		addZone(getNOM_ST_ACTION(), ACTION_CREATION);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (12/09/11 11:49:01)
	 * 
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (12/09/11 11:49:01)
	 * 
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHE_AVANCEE Date de
	 * création : (13/09/11 08:35:27)
	 * 
	 * 
	 */
	public String getNOM_PB_RECHERCHE_AVANCEE() {
		return "NOM_PB_RECHERCHE_AVANCEE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/09/11 08:35:27)
	 * 
	 * 
	 */
	public boolean performPB_RECHERCHE_AVANCEE(HttpServletRequest request) throws Exception {
		setStatut(STATUT_RECHERCHE_AVANCEE, true);
		return true;
	}

	/**
	 * Vérifie si la modification des spécificités doit être possible.
	 * 
	 * @return afficherModifSpecificites boolean RG_PE_FP_A03
	 */
	public boolean isAfficherModifSpecificites() throws Exception {
		// RG_PE_FP_A03
		if (getFichePosteCourante() != null && getListeStatut() != null && getListeStatut().size() != 0) {
			int numLigneStatut = (Services.estNumerique(getZone(getNOM_LB_STATUT_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_STATUT_SELECT()))
					: -1);
			StatutFP sfp = (StatutFP) getListeStatut().get(numLigneStatut);
			return (!getFichePosteCourante().estAffectée(getTransaction()) && !sfp.getLibStatutFP()
					.equals(EnumStatutFichePoste.INACTIVE.getLibLong()));
		} else
			return false;
	}

	/**
	 * Getter du booléen permettant de dire si la fiche de poste est affectee ou
	 * pas.
	 * 
	 * @return booléen
	 */
	public boolean estFpCouranteAffectee() {
		return fpCouranteAffectee;
	}

	/**
	 * Setter du booléen permettant de dire si la fiche de poste est affectee ou
	 * pas.
	 * 
	 * @param fpCouranteAffectee
	 *            booléen
	 */
	private void setFpCouranteAffectee(boolean fpCouranteAffectee) {
		this.fpCouranteAffectee = fpCouranteAffectee;
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
			setAgtRemplacement(AgentNW.chercherAgentAffecteFichePoste(getTransaction(), getRemplacement().getIdFichePoste()));
			setTitrePosteRemplacement(TitrePoste.chercherTitrePoste(getTransaction(), getRemplacement().getIdTitrePoste()));
		} else {
			setAgtRemplacement(null);
			setTitrePosteRemplacement(null);
		}
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 * 
	 * @return le nom de l'ecran
	 */
	public String getNomEcran() {
		return "ECR-PE-FP";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_REMPLACEMENT Date
	 * de création : (11/10/11 10:23:53)
	 * 
	 * 
	 */
	public String getNOM_PB_RECHERCHER_REMPLACEMENT() {
		return "NOM_PB_RECHERCHER_REMPLACEMENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/10/11 10:23:53)
	 * 
	 * 
	 */
	public boolean performPB_RECHERCHER_REMPLACEMENT(HttpServletRequest request) throws Exception {
		if (!getVAL_EF_CODESERVICE().equals(Const.CHAINE_VIDE))
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_SERVICE, Service.chercherService(getTransaction(), getVAL_EF_CODESERVICE()));

		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_RECHERCHE_POSTE_AVANCEE, Boolean.TRUE);

		setStatut(STATUT_REMPLACEMENT, true);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_REMPLACEMENT Date de
	 * création : (11/10/11 10:23:53)
	 * 
	 * 
	 */
	public String getNOM_ST_REMPLACEMENT() {
		return "NOM_ST_REMPLACEMENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_REMPLACEMENT
	 * Date de création : (11/10/11 10:23:53)
	 * 
	 * 
	 */
	public String getVAL_ST_REMPLACEMENT() {
		return getZone(getNOM_ST_REMPLACEMENT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SELECT_STATUT Date de
	 * création : (08/11/11 11:20:35)
	 * 
	 * 
	 */
	public String getNOM_PB_SELECT_STATUT() {
		return "NOM_PB_SELECT_STATUT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (08/11/11 11:20:35)
	 * 
	 * 
	 */
	public boolean performPB_SELECT_STATUT(HttpServletRequest request) throws Exception {
		// Statut de la fiche
		int numLigneStatut = (Services.estNumerique(getZone(getNOM_LB_STATUT_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_STATUT_SELECT())) : -1);
		if (numLigneStatut == -1 || getListeStatut().size() == 0 || numLigneStatut > getListeStatut().size())
			return true;

		StatutFP statut = (StatutFP) getListeStatut().get(numLigneStatut);
		initialiseChampObligatoire(statut);
		return true;
	}

	/**
	 * Définit si le reponsable est obligatoire en fonction du statut.
	 * 
	 * @param statut
	 */
	private void initialiseChampObligatoire(StatutFP statut) {
		responsableObligatoire = true;
		estFDPInactive = false;
		if (statut.getLibStatutFP().equals(EnumStatutFichePoste.INACTIVE.getLibLong())) {
			responsableObligatoire = false;
			estFDPInactive = true;
		}
	}

	private boolean performPB_IMPRIMER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), ACTION_IMPRESSION);
		// on fait appel à CREER pour valider les modifications et afficher le
		// message "FDP imprimée"
		if (!performPB_CREER(request)) {
			return false;
		}

		imprimeModele(request);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	private boolean imprimeModele(HttpServletRequest request) throws Exception {
		String repPartage = (String) ServletAgent.getMesParametres().get("REPERTOIRE_ACTES");
		String destination = repPartage + "FichePosteVierge/FP_" + getFichePosteCourante().getIdFichePoste() + ".xml";

		String modele = "ModeleFP_Vierge.xml";
		String repModeles = (String) ServletAgent.getMesParametres().get("REPERTOIRE_MODELES_FICHEPOSTE");

		creerModeleDocument("FichePosteVierge", repModeles + modele, destination);
		return true;
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

	private void creerModeleDocument(String repertoire, String modele, String destination) throws Exception {
		// on verifie que les repertoires existent
		verifieRepertoire(repertoire);

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
		FichePoste fp = getFichePosteCourante();

		// requete necessaire
		TitrePoste tp = TitrePoste.chercherTitrePoste(getTransaction(), fp.getIdTitrePoste());
		Grade g = Grade.chercherGrade(getTransaction(), fp.getCodeGrade());
		GradeGenerique gg = GradeGenerique.chercherGradeGenerique(getTransaction(), g.getCodeGradeGenerique());
		FiliereGrade fi = null;
		CadreEmploi cadreEmp = null;
		if (gg != null && gg.getIdCadreEmploi() != null) {
			cadreEmp = CadreEmploi.chercherCadreEmploi(getTransaction(), gg.getIdCadreEmploi());
		}
		if (gg != null && gg.getCdfili() != null) {
			fi = FiliereGrade.chercherFiliereGrade(getTransaction(), gg.getCdfili());
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
		NiveauEtude nivEtu = NiveauEtude.chercherNiveauEtude(getTransaction(), nivEtuFP.getIdNiveauEtude());
		String niveauEtude = nivEtu.getLibNiveauEtude();

		DiplomeFP dipFP = DiplomeFP.chercherDiplomeAvecFP(getTransaction(), fp.getIdFichePoste());
		DiplomeGenerique dip = DiplomeGenerique.chercherDiplomeGenerique(getTransaction(), dipFP.getIdDiplomeGenerique());
		String diplome = dip.getLibDiplomeGenerique();

		// partie concernant l'emploi
		String emploiPrimaire = Const.CHAINE_VIDE;
		if (getEmploiPrimaire() != null) {
			emploiPrimaire = getEmploiPrimaire().getRefMairie();
		}
		String emploiSecondaire = Const.CHAINE_VIDE;
		if (getEmploiSecondaire() != null) {
			emploiSecondaire = getEmploiSecondaire().getRefMairie();
		}
		String budget = fp.getIdBudget() == null ? Const.CHAINE_VIDE : Budget.chercherBudget(getTransaction(), fp.getIdBudget()).getLibBudget();

		// partie concernant le temps de travail du poste
		String reglementaire = Horaire.chercherHoraire(getTransaction(), fp.getIdCdthorReg()).getLibHor();
		String budgete = Horaire.chercherHoraire(getTransaction(), fp.getIdCdthorBud()).getLibHor();

		// partie concernant le poste
		// titulaire
		String titulaireMatr = Const.CHAINE_VIDE;
		String titulaireNom = "Poste vacant.";
		String dateAff = Const.CHAINE_VIDE;
		if (getAgentCourant() != null) {
			Affectation aff = Affectation.chercherAffectationActiveAvecAgent(getTransaction(), getAgentCourant().getIdAgent());
			if (aff != null && aff.getIdAffectation() != null) {
				String prenomTitulaire = getAgentCourant().getPrenomAgent().toLowerCase();
				String premLettreTitulaire = prenomTitulaire.substring(0, 1).toUpperCase();
				String restePrenomTitulaire = prenomTitulaire.substring(1, prenomTitulaire.length()).toLowerCase();
				prenomTitulaire = premLettreTitulaire + restePrenomTitulaire;
				String nomTitulaire = getAgentCourant().getNomAgent().toUpperCase();
				titulaireNom = prenomTitulaire + " " + nomTitulaire;
				titulaireMatr = getAgentCourant().getNoMatricule();
				dateAff = aff.getDateDebutAff() == null ? Const.CHAINE_VIDE : "Affecté depuis le " + aff.getDateDebutAff();
			}
		}

		// responsable hierarchique
		String respFP = Const.CHAINE_VIDE;
		String respTitreFP = Const.CHAINE_VIDE;
		String respMatr = Const.CHAINE_VIDE;
		String respNom = Const.CHAINE_VIDE;
		if (fp.getIdResponsable() != null) {
			FichePoste fpResponsable = FichePoste.chercherFichePoste(getTransaction(), fp.getIdResponsable());
			TitrePoste tpResponsable = TitrePoste.chercherTitrePoste(getTransaction(), fpResponsable.getIdTitrePoste());
			if (getAgtResponsable() != null) {
				respMatr = getAgtResponsable().getNoMatricule();
				String prenomResponsable = getAgtResponsable().getPrenomAgent().toLowerCase();
				String premLettreResponsable = prenomResponsable.substring(0, 1).toUpperCase();
				String restePrenomResponsable = prenomResponsable.substring(1, prenomResponsable.length()).toLowerCase();
				prenomResponsable = premLettreResponsable + restePrenomResponsable;
				String nom = getAgtResponsable().getNomAgent().toUpperCase();
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
		setURLFichier(getScriptOuverture(repertoireStockage + repertoire + destination));
	}

	private void creerModeleDocumentFP(String repertoire, String modele, String destination, String idFichePoste) throws Exception {
		// on verifie que les repertoires existent
		verifieRepertoire(repertoire);

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
		CadreEmploi cadreEmp = null;
		FiliereGrade fi = null;
		if (gg != null && gg.getIdCadreEmploi() != null) {
			cadreEmp = CadreEmploi.chercherCadreEmploi(getTransaction(), gg.getIdCadreEmploi());
		}
		if (gg != null && gg.getCdfili() != null) {
			fi = FiliereGrade.chercherFiliereGrade(getTransaction(), gg.getCdfili());
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
		NiveauEtude nivEtu = NiveauEtude.chercherNiveauEtude(getTransaction(), nivEtuFP.getIdNiveauEtude());
		String niveauEtude = nivEtu.getLibNiveauEtude();

		DiplomeFP dipFP = DiplomeFP.chercherDiplomeAvecFP(getTransaction(), fp.getIdFichePoste());
		DiplomeGenerique dip = DiplomeGenerique.chercherDiplomeGenerique(getTransaction(), dipFP.getIdDiplomeGenerique());
		String diplome = dip.getLibDiplomeGenerique();

		// partie concernant l'emploi
		String emploiPrimaire = Const.CHAINE_VIDE;
		if (getEmploiPrimaire() != null) {
			emploiPrimaire = getEmploiPrimaire().getRefMairie();
		}
		String emploiSecondaire = Const.CHAINE_VIDE;
		if (getEmploiSecondaire() != null) {
			emploiSecondaire = getEmploiSecondaire().getRefMairie();
		}
		String budget = fp.getIdBudget() == null ? Const.CHAINE_VIDE : Budget.chercherBudget(getTransaction(), fp.getIdBudget()).getLibBudget();

		// partie concernant le temps de travail du poste
		String reglementaire = Horaire.chercherHoraire(getTransaction(), fp.getIdCdthorReg()).getLibHor();
		String budgete = Horaire.chercherHoraire(getTransaction(), fp.getIdCdthorBud()).getLibHor();

		// partie concernant le poste
		// titulaire
		String titulaireMatr = Const.CHAINE_VIDE;
		String titulaireNom = "Poste vacant.";
		String dateAff = Const.CHAINE_VIDE;
		if (getAgentCourant() != null) {
			Affectation aff = Affectation.chercherAffectationActiveAvecAgent(getTransaction(), getAgentCourant().getIdAgent());
			AgentNW agent = null;
			Affectation affAgent = null;
			if (getTransaction().isErreur()) {
				getTransaction().traiterErreur();
			} else {
				affAgent = aff;
				agent = AgentNW.chercherAgent(getTransaction(), aff.getIdAgent());
			}
			if (agent != null) {
				String prenomTitulaire = agent.getPrenomAgent().toLowerCase();
				String premLettreTitulaire = prenomTitulaire.substring(0, 1).toUpperCase();
				String restePrenomTitulaire = prenomTitulaire.substring(1, prenomTitulaire.length()).toLowerCase();
				prenomTitulaire = premLettreTitulaire + restePrenomTitulaire;
				String nomTitulaire = agent.getNomAgent().toUpperCase();
				titulaireNom = prenomTitulaire + " " + nomTitulaire;
				titulaireMatr = agent.getNoMatricule();
			}
			if (affAgent != null) {
				dateAff = affAgent.getDateDebutAff() == null ? Const.CHAINE_VIDE : "Affecté depuis le " + affAgent.getDateDebutAff();
			}
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
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
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
			if (getTransaction().isErreur())
				getTransaction().traiterErreur();
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

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INFO_FP Date de
	 * création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getNOM_ST_INFO_FP() {
		return "NOM_ST_INFO_FP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INFO_FP Date
	 * de création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getVAL_ST_INFO_FP() {
		return getZone(getNOM_ST_INFO_FP());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INFO_GRADE Date de
	 * création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getNOM_ST_INFO_GRADE() {
		return "NOM_ST_INFO_GRADE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INFO_GRADE
	 * Date de création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getVAL_ST_INFO_GRADE() {
		return getZone(getNOM_ST_INFO_GRADE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INFO_SERVICE Date de
	 * création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getNOM_ST_INFO_SERVICE() {
		return "NOM_ST_INFO_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INFO_SERVICE
	 * Date de création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getVAL_ST_INFO_SERVICE() {
		return getZone(getNOM_ST_INFO_SERVICE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INFO_REMP Date de
	 * création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getNOM_ST_INFO_REMP() {
		return "NOM_ST_INFO_REMP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INFO_REMP Date
	 * de création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getVAL_ST_INFO_REMP() {
		return getZone(getNOM_ST_INFO_REMP());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_INFO_RESP Date de
	 * création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getNOM_ST_INFO_RESP() {
		return "NOM_ST_INFO_RESP";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_INFO_RESP Date
	 * de création : (29/11/11 16:42:44)
	 * 
	 * 
	 */
	public String getVAL_ST_INFO_RESP() {
		return getZone(getNOM_ST_INFO_RESP());
	}

	/**
	 * Getter de la liste des activités de la FicheEmploi primaire.
	 * 
	 * @return listeActiFEP
	 */
	public ArrayList getListeActiFEP() {
		if (listeActiFEP == null) {
			listeActiFEP = new ArrayList();
		}
		return listeActiFEP;
	}

	/**
	 * Setter de la liste des activités de la FicheEmploi primaire.
	 * 
	 * @param listeActiFEP
	 */
	public void setListeActiFEP(ArrayList listeActiFEP) {
		this.listeActiFEP = listeActiFEP;
	}

	/**
	 * Getter de la liste des activités de la FicheEmploi secondaire.
	 * 
	 * @return listeActiFES
	 */
	public ArrayList getListeActiFES() {
		if (listeActiFES == null) {
			listeActiFES = new ArrayList();
		}
		return listeActiFES;
	}

	/**
	 * Setter de la liste des activités de la FicheEmploi secondaire.
	 * 
	 * @param listeActiFES
	 */
	public void setListeActiFES(ArrayList listeActiFES) {
		this.listeActiFES = listeActiFES;
	}

	/**
	 * Getter de la HashTable des types de nature d'avantage.
	 * 
	 * @return hashNatAv
	 */
	public Hashtable<String, NatureAvantage> getHashNatAv() {
		if (hashNatAv == null)
			hashNatAv = new Hashtable<String, NatureAvantage>();
		return hashNatAv;
	}

	/**
	 * Setter de la HashTable des types de nature d'avantage.
	 * 
	 * @param hashNatAv
	 *            hashNatAv à définir
	 */
	public void setHashNatAv(Hashtable<String, NatureAvantage> hashNatAv) {
		this.hashNatAv = hashNatAv;
	}

	/**
	 * Getter de la HashTable des types d'avantage en nature.
	 * 
	 * @return hashtypAv
	 */
	public Hashtable<String, TypeAvantage> getHashtypAv() {
		if (hashtypAv == null)
			hashtypAv = new Hashtable<String, TypeAvantage>();
		return hashtypAv;
	}

	/**
	 * Setter de la HashTable des types d'avantage en nature.
	 * 
	 * @param hashtypAv
	 */
	public void setHashtypAv(Hashtable<String, TypeAvantage> hashtypAv) {
		this.hashtypAv = hashtypAv;
	}

	/**
	 * Getter de la HashTable des types de délégation.
	 * 
	 * @return hashTypDel
	 */
	public Hashtable<String, TypeDelegation> getHashTypDel() {
		if (hashTypDel == null)
			hashTypDel = new Hashtable<String, TypeDelegation>();
		return hashTypDel;
	}

	/**
	 * Setter de la HashTable des types de délégation.
	 * 
	 * @param hashTypDel
	 */
	public void setHashTypDel(Hashtable<String, TypeDelegation> hashTypDel) {
		this.hashTypDel = hashTypDel;
	}

	/**
	 * Getter de la HashTable des types de régime indemnitaire.
	 * 
	 * @return hashTypRegIndemn
	 */
	public Hashtable<String, TypeRegIndemn> getHashTypRegIndemn() {
		if (hashTypRegIndemn == null)
			hashTypRegIndemn = new Hashtable<String, TypeRegIndemn>();
		return hashTypRegIndemn;
	}

	/**
	 * Setter de la HashTable des types de régime indemnitaire.
	 * 
	 * @param hashTypRegIndemn
	 */
	public void setHashTypRegIndemn(Hashtable<String, TypeRegIndemn> hashTypRegIndemn) {
		this.hashTypRegIndemn = hashTypRegIndemn;
	}

	/**
	 * Met à jour l'autorisation de modifier les FicheEmploi.
	 */
	public void majChangementFEAutorise() {
		// Statut de la fiche
		int numLigneStatut = (Services.estNumerique(getZone(getNOM_LB_STATUT_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_STATUT_SELECT())) : -1);
		if (numLigneStatut == -1 || getListeStatut().size() == 0 || numLigneStatut > getListeStatut().size()) {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "statuts"));
			return;
		}
		StatutFP statut = (StatutFP) getListeStatut().get(numLigneStatut);
		setChangementFEAutorise(!estFpCouranteAffectee() && !statut.getLibStatutFP().equals(EnumStatutFichePoste.INACTIVE.getLibLong())
				&& !statut.getLibStatutFP().equals(EnumStatutFichePoste.VALIDEE.getLibLong()));
	}

	/**
	 * Getter du booleen changementFEAutorise.
	 * 
	 * @return changementFEAutorise
	 */
	public boolean estChangementFEAutorise() {
		return changementFEAutorise;
	}

	/**
	 * Setter du booleen changementFEAutorise.
	 * 
	 * @param changementFEAutorise
	 */
	private void setChangementFEAutorise(boolean changementFEAutorise) {
		this.changementFEAutorise = changementFEAutorise;
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

	/**
	 * Getter de l'Affectation courante.
	 * 
	 * @return affectationCourante
	 */
	private Affectation getAffectationCourante() {
		return affectationCourante;
	}

	/**
	 * Setter de l'Affectation courante.
	 * 
	 * @param affectationCourante
	 */
	private void setAffectationCourante(Affectation affectationCourante) {
		this.affectationCourante = affectationCourante;
	}

	/**
	 * Getter de l'Agent courant.
	 * 
	 * @return agentCourant
	 */
	private AgentNW getAgentCourant() {
		return agentCourant;
	}

	/**
	 * Setter de l'Agent courant.
	 * 
	 * @param agentCourant
	 */
	private void setAgentCourant(AgentNW agentCourant) {
		this.agentCourant = agentCourant;
	}

	/**
	 * Getter du contrat courant de l'agent affecte.
	 * 
	 * @return contratCourant
	 */
	private Contrat getContratCourant() {
		return contratCourant;
	}

	/**
	 * Setter du contrat courant de l'agent affecte.
	 * 
	 * @param contratCourant
	 */
	private void setContratCourant(Contrat contratCourant) {
		this.contratCourant = contratCourant;
	}

	/**
	 * Getter du type de contrat courant de l'agent affecte.
	 * 
	 * @return typeContratCourant
	 */
	private TypeContrat getTypeContratCourant() {
		return typeContratCourant;
	}

	/**
	 * Setter du type de contrat courant de l'agent affecte.
	 * 
	 * @param typeContratCourant
	 *            typeContratCourant à définir
	 */
	private void setTypeContratCourant(TypeContrat typeContratCourant) {
		this.typeContratCourant = typeContratCourant;
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (07/07/11 10:59:29)
	 * 
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_SELECT_STATUT
			if (testerParametre(request, getNOM_PB_SELECT_STATUT())) {
				return performPB_SELECT_STATUT(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_REMPLACEMENT
			if (testerParametre(request, getNOM_PB_RECHERCHER_REMPLACEMENT())) {
				return performPB_RECHERCHER_REMPLACEMENT(request);
			}

			// Si clic sur le bouton PB_RECHERCHE_AVANCEE
			if (testerParametre(request, getNOM_PB_RECHERCHE_AVANCEE())) {
				return performPB_RECHERCHE_AVANCEE(request);
			}

			// Si clic sur le bouton PB_AJOUTER_FP
			if (testerParametre(request, getNOM_PB_AJOUTER_FP())) {
				return performPB_AJOUTER_FP(request);
			}

			// Si clic sur le bouton PB_DUPLIQUER_FP
			if (testerParametre(request, getNOM_PB_DUPLIQUER_FP())) {
				return performPB_DUPLIQUER_FP(request);
			}

			// Si clic sur le bouton PB_AFFICHER_LISTE_GRADE
			if (testerParametre(request, getNOM_PB_AFFICHER_LISTE_GRADE())) {
				return performPB_AFFICHER_LISTE_GRADE(request);
			}

			// Si clic sur le bouton PB_AFFICHER_LISTE_DIPLOME
			if (testerParametre(request, getNOM_PB_AFFICHER_LISTE_DIPLOME())) {
				return performPB_AFFICHER_LISTE_DIPLOME(request);
			}

			// Si clic sur le bouton PB_AFFICHER_LISTE_NIVEAU
			if (testerParametre(request, getNOM_PB_AFFICHER_LISTE_NIVEAU())) {
				return performPB_AFFICHER_LISTE_NIVEAU(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_RESPONSABLE
			if (testerParametre(request, getNOM_PB_RECHERCHER_RESPONSABLE())) {
				return performPB_RECHERCHER_RESPONSABLE(request);
			}

			// Si clic sur le bouton PB_MODIFIER_SPECIFICITES
			if (testerParametre(request, getNOM_PB_MODIFIER_SPECIFICITES())) {
				return performPB_MODIFIER_SPECIFICITES(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			if (testerParametre(request, getNOM_PB_MODIFIER())) {
				return performPB_MODIFIER(request);
			}

			// Si clic sur le bouton PB_AJOUTER_ACTIVITE
			if (testerParametre(request, getNOM_PB_AJOUTER_ACTIVITE())) {
				return performPB_AJOUTER_ACTIVITE(request);
			}

			// Si clic sur le bouton PB_RECHERCHE_EMPLOI_PRIMAIRE
			if (testerParametre(request, getNOM_PB_RECHERCHE_EMPLOI_PRIMAIRE())) {
				return performPB_RECHERCHE_EMPLOI_PRIMAIRE(request);
			}

			// Si clic sur le bouton PB_RECHERCHE_EMPLOI_SECONDAIRE
			if (testerParametre(request, getNOM_PB_RECHERCHE_EMPLOI_SECONDAIRE())) {
				return performPB_RECHERCHE_EMPLOI_SECONDAIRE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_EMPLOI_SECONDAIRE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_EMPLOI_SECONDAIRE())) {
				return performPB_SUPPRIMER_EMPLOI_SECONDAIRE(request);
			}

			// Si clic sur le bouton PB_AJOUTER_GRADE
			if (testerParametre(request, getNOM_PB_AJOUTER_GRADE())) {
				return performPB_AJOUTER_GRADE(request);
			}

			// Si clic sur le bouton PB_AJOUTER_DIPLOME
			if (testerParametre(request, getNOM_PB_AJOUTER_DIPLOME())) {
				return performPB_AJOUTER_DIPLOME(request);
			}

			// Si clic sur le bouton PB_AJOUTER_NIVEAU_ETUDE
			if (testerParametre(request, getNOM_PB_AJOUTER_NIVEAU_ETUDE())) {
				return performPB_AJOUTER_NIVEAU_ETUDE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_DIPLOME
			if (testerParametre(request, getNOM_PB_SUPPRIMER_DIPLOME())) {
				return performPB_SUPPRIMER_DIPLOME(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_NIVEAU_ETUDE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_NIVEAU_ETUDE())) {
				return performPB_SUPPRIMER_NIVEAU_ETUDE(request);
			}

			// Si clic sur le bouton PB_RECHERCHER
			if (testerParametre(request, getNOM_PB_RECHERCHER())) {
				return performPB_RECHERCHER(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_CREER
			if (testerParametre(request, getNOM_PB_CREER())) {
				return performPB_CREER(request);
			}

			// Si clic sur le bouton PB_IMPRIMER
			if (testerParametre(request, getNOM_PB_IMPRIMER())) {
				return performPB_IMPRIMER(request);
			}

			// Si clic sur le bouton PB_AJOUTER_COMPETENCE_SAVOIR
			if (testerParametre(request, getNOM_PB_AJOUTER_COMPETENCE_SAVOIR())) {
				return performPB_AJOUTER_COMPETENCE_SAVOIR(request);
			}

			// Si clic sur le bouton PB_AJOUTER_COMPETENCE_SAVOIR_FAIRE
			if (testerParametre(request, getNOM_PB_AJOUTER_COMPETENCE_SAVOIR_FAIRE())) {
				return performPB_AJOUTER_COMPETENCE_SAVOIR_FAIRE(request);
			}

			// Si clic sur le bouton PB_AJOUTER_COMPETENCE_COMPORTEMENT
			if (testerParametre(request, getNOM_PB_AJOUTER_COMPETENCE_COMPORTEMENT())) {
				return performPB_AJOUTER_COMPETENCE_COMPORTEMENT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_REMPLACEMENT
			if (testerParametre(request, getNOM_PB_SUPPRIMER_REMPLACEMENT())) {
				return performPB_SUPPRIMER_REMPLACEMENT(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OePOSTEFichePoste. Date de création : (07/12/11
	 * 10:22:27)
	 * 
	 * 
	 */
	public OePOSTEFichePoste() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (07/12/11 10:22:27)
	 * 
	 * 
	 */
	public String getJSP() {
		return "OePOSTEFichePoste.jsp";
	}

	private String getObservation() {
		if (observation == null) {
			return Const.CHAINE_VIDE;
		}
		return observation;
	}

	private void setObservation(String observation) {
		this.observation = observation;
	}

	private String getMission() {
		return mission == null ? Const.CHAINE_VIDE : mission.trim();
	}

	private void setMission(String mission) {
		this.mission = mission;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ID_ACTI Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getNOM_ST_ID_ACTI(int i) {
		return "NOM_ST_ID_ACTI_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ID_ACTI Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getVAL_ST_ID_ACTI(int i) {
		return getZone(getNOM_ST_ID_ACTI(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_ACTI Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getNOM_ST_LIB_ACTI(int i) {
		return "NOM_ST_LIB_ACTI_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_ACTI Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getVAL_ST_LIB_ACTI(int i) {
		return getZone(getNOM_ST_LIB_ACTI(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_SELECT_LIGNE_ACTI Date de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getNOM_CK_SELECT_LIGNE_ACTI(int i) {
		return "NOM_CK_SELECT_LIGNE_ACTI_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * à cocher : CK_SELECT_LIGNE_ACTI Date de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getVAL_CK_SELECT_LIGNE_ACTI(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE_ACTI(i));
	}

	public ArrayList getListeToutesActi() {
		return listeToutesActi;
	}

	private void setListeToutesActi(ArrayList listeToutesActi) {
		this.listeToutesActi = listeToutesActi;
	}

	private ArrayList getListeActiFP() {
		return listeActiFP;
	}

	private void setListeActiFP(ArrayList listeActiFP) {
		this.listeActiFP = listeActiFP;
	}

	private ArrayList getListeAjoutActiFP() {
		return listeAjoutActiFP;
	}

	private void setListeAjoutActiFP(ArrayList listeAjoutActiFP) {
		this.listeAjoutActiFP = listeAjoutActiFP;
	}

	private Hashtable<String, String> getHashOrigineActivite() {
		if (hashOrigineActivite == null) {
			hashOrigineActivite = new Hashtable<String, String>();
		}
		return hashOrigineActivite;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_ORIGINE_ACTI
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getNOM_ST_LIB_ORIGINE_ACTI(int i) {
		return "NOM_ST_LIB_ORIGINE_ACTI_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LIB_ORIGINE_ACTI Date de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getVAL_ST_LIB_ORIGINE_ACTI(int i) {
		return getZone(getNOM_ST_LIB_ORIGINE_ACTI(i));
	}

	private ArrayList getListeCompFEP() {
		if (listeCompFEP == null) {
			listeCompFEP = new ArrayList();
		}
		return listeCompFEP;
	}

	private void setListeCompFEP(ArrayList listeCompFEP) {
		this.listeCompFEP = listeCompFEP;
	}

	private ArrayList getListeCompFES() {
		if (listeCompFES == null) {
			listeCompFES = new ArrayList();
		}
		return listeCompFES;
	}

	private void setListeCompFES(ArrayList listeCompFES) {
		this.listeCompFES = listeCompFES;
	}

	private ArrayList getListeCompFP() {
		if (listeCompFP == null) {
			listeCompFP = new ArrayList();
		}
		return listeCompFP;
	}

	private void setListeCompFP(ArrayList listeCompFP) {
		this.listeCompFP = listeCompFP;
	}

	public ArrayList getListeToutesComp() {
		if (listeToutesComp == null) {
			listeToutesComp = new ArrayList();
		}
		return listeToutesComp;
	}

	private void setListeToutesComp(ArrayList listeToutesComp) {
		this.listeToutesComp = listeToutesComp;
	}

	private ArrayList getListeAjoutCompFP() {
		if (listeAjoutCompFP == null) {
			listeAjoutCompFP = new ArrayList();
		}
		return listeAjoutCompFP;
	}

	private void setListeAjoutCompFP(ArrayList listeAjoutCompFP) {
		this.listeAjoutCompFP = listeAjoutCompFP;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ID_COMP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getNOM_ST_ID_COMP(int i) {
		return "NOM_ST_ID_COMP_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ID_COMP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getVAL_ST_ID_COMP(int i) {
		return getZone(getNOM_ST_ID_COMP(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_COMP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getNOM_ST_LIB_COMP(int i) {
		return "NOM_ST_LIB_COMP_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_COMP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getVAL_ST_LIB_COMP(int i) {
		return getZone(getNOM_ST_LIB_COMP(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_TYPE_COMP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getNOM_ST_TYPE_COMP(int i) {
		return "NOM_ST_TYPE_COMP_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_TYPE_COMP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getVAL_ST_TYPE_COMP(int i) {
		return getZone(getNOM_ST_TYPE_COMP(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_SELECT_LIGNE_COMP Date de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getNOM_CK_SELECT_LIGNE_COMP(int i) {
		return "NOM_CK_SELECT_LIGNE_COMP_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * à cocher : CK_SELECT_LIGNE_COMP Date de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getVAL_CK_SELECT_LIGNE_COMP(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE_COMP(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_ORIGINE_COMP
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getNOM_ST_LIB_ORIGINE_COMP(int i) {
		return "NOM_ST_LIB_ORIGINE_COMP_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_LIB_ORIGINE_COMP Date de création : (21/11/11 09:55:36)
	 * 
	 * 
	 */
	public String getVAL_ST_LIB_ORIGINE_COMP(int i) {
		return getZone(getNOM_ST_LIB_ORIGINE_COMP(i));
	}

	private Hashtable<String, String> getHashOrigineCompetence() {
		if (hashOrigineCompetence == null) {
			hashOrigineCompetence = new Hashtable<String, String>();
		}
		return hashOrigineCompetence;
	}

	private ArrayList getListeNiveauFP() {
		return listeNiveauFP;
	}

	private void setListeNiveauFP(ArrayList listeNiveauFP) {
		this.listeNiveauFP = listeNiveauFP;
	}

	private ArrayList getListeTousNiveau() {
		return listeTousNiveau;
	}

	private void setListeTousNiveau(ArrayList listeTousNiveau) {
		this.listeTousNiveau = listeTousNiveau;
	}

	private ArrayList getListeDiplomeFP() {
		return listeDiplomeFP;
	}

	private void setListeDiplomeFP(ArrayList listeDiplomeFP) {
		this.listeDiplomeFP = listeDiplomeFP;
	}

	private ArrayList getListeTousDiplomes() {
		return listeTousDiplomes;
	}

	private void setListeTousDiplomes(ArrayList listeTousDiplomes) {
		this.listeTousDiplomes = listeTousDiplomes;
	}

	private ArrayList getListeDiplome() {
		return listeDiplome;
	}

	private void setListeDiplome(ArrayList listeDiplome) {
		this.listeDiplome = listeDiplome;
	}

	private ArrayList getListeNiveauEtude() {
		return listeNiveauEtude;
	}

	private void setListeNiveauEtude(ArrayList listeNiveauEtude) {
		this.listeNiveauEtude = listeNiveauEtude;
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_AV_TYPE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getNOM_ST_AV_TYPE(int i) {
		return "NOM_ST_AV_TYPE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getVAL_ST_AV_TYPE(int i) {
		return getZone(getNOM_ST_AV_TYPE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_AV_MNT Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getNOM_ST_AV_MNT(int i) {
		return "NOM_ST_AV_MNT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getVAL_ST_AV_MNT(int i) {
		return getZone(getNOM_ST_AV_MNT(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_AV_NATURE
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getNOM_ST_AV_NATURE(int i) {
		return "NOM_ST_AV_NATURE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getVAL_ST_AV_NATURE(int i) {
		return getZone(getNOM_ST_AV_NATURE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_DEL_TYPE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getNOM_ST_DEL_TYPE(int i) {
		return "NOM_ST_DEL_TYPE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getVAL_ST_DEL_TYPE(int i) {
		return getZone(getNOM_ST_DEL_TYPE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique :
	 * ST_DEL_COMMENTAIRE Date de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getNOM_ST_DEL_COMMENTAIRE(int i) {
		return "NOM_ST_DEL_COMMENTAIRE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getVAL_ST_DEL_COMMENTAIRE(int i) {
		return getZone(getNOM_ST_DEL_COMMENTAIRE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_REG_TYPE Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getNOM_ST_REG_TYPE(int i) {
		return "NOM_ST_REG_TYPE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getVAL_ST_REG_TYPE(int i) {
		return getZone(getNOM_ST_REG_TYPE(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_REG_FORFAIT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getNOM_ST_REG_FORFAIT(int i) {
		return "NOM_ST_REG_FORFAIT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getVAL_ST_REG_FORFAIT(int i) {
		return getZone(getNOM_ST_REG_FORFAIT(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_REG_NB_PTS
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getNOM_ST_REG_NB_PTS(int i) {
		return "NOM_ST_REG_NB_PTS" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMPPRO Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 * 
	 */
	public String getVAL_ST_REG_NB_PTS(int i) {
		return getZone(getNOM_ST_REG_NB_PTS(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_REMPLACEMENT Date
	 * de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_REMPLACEMENT() {
		return "NOM_PB_SUPPRIMER_REMPLACEMENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 * 
	 */
	public boolean performPB_SUPPRIMER_REMPLACEMENT(HttpServletRequest request) throws Exception {
		// On enlève la fiche poste remplacée selectionnée
		setRemplacement(null);
		getFichePosteCourante().setIdRemplacement(null);
		addZone(getNOM_ST_REMPLACEMENT(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_INFO_REMP(), Const.CHAINE_VIDE);
		return true;
	}

}
