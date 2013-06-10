package nc.mairie.gestionagent.process.avancement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatAvancement;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.avancement.AvancementFonctionnaires;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.carriere.FiliereGrade;
import nc.mairie.metier.carriere.Grade;
import nc.mairie.metier.poste.Service;
import nc.mairie.metier.referentiel.AvisCap;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.TreeHierarchy;
import nc.mairie.utils.VariablesActivite;

/**
 * Process OeAVCTFonctionnaires Date de création : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTFonctCarrieres extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;
	private String[] LB_ANNEE;
	private String[] LB_FILIERE;

	private String[] listeAnnee;
	private String anneeSelect;
	private ArrayList<FiliereGrade> listeFiliere;
	private ArrayList<Service> listeServices;
	public Hashtable<String, TreeHierarchy> hTree = null;

	private ArrayList<AvancementFonctionnaires> listeAvct;

	public String agentEnErreur = Const.CHAINE_VIDE;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {

		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		// ----------------------------------//
		// Vérification des droits d'accès. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		// Initialisation des listes déroulantes
		initialiseListeDeroulante();

		initialiseListeService();

		if (etatStatut() == STATUT_RECHERCHER_AGENT) {
			AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			addZone(getNOM_ST_AGENT(), agt.getNoMatricule());
		}

		// Si liste avancements vide alors initialisation.
		if (getListeAvct().size() == 0) {
			agentEnErreur = Const.CHAINE_VIDE;
		}
	}

	private void initialiseListeService() throws Exception {
		// Si la liste des services est nulle
		if (getListeServices() == null || getListeServices().size() == 0) {
			ArrayList<Service> services = Service.listerServiceActif(getTransaction());
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
			hTree = new Hashtable<String, TreeHierarchy>();
			TreeHierarchy parent = null;
			for (int i = 0; i < getListeServices().size(); i++) {
				Service serv = (Service) getListeServices().get(i);

				if (Const.CHAINE_VIDE.equals(serv.getCodService()))
					continue;

				// recherche du supérieur
				String codeService = serv.getCodService();
				while (codeService.endsWith("A")) {
					codeService = codeService.substring(0, codeService.length() - 1);
				}
				codeService = codeService.substring(0, codeService.length() - 1);
				codeService = Services.rpad(codeService, 4, "A");
				parent = hTree.get(codeService);
				int indexParent = (parent == null ? 0 : parent.getIndex());
				hTree.put(serv.getCodService(), new TreeHierarchy(serv, i, indexParent));

			}
		}
	}

	/**
	 * Initialisation des liste déroulantes de l'écran Avancement des
	 * fonctionnaires.
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste annee vide alors affectation
		if (getLB_ANNEE() == LBVide) {
			String anneeCourante = (String) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_ANNEE_SIMULATION_AVCT);
			if (anneeCourante == null || anneeCourante.length() == 0)
				anneeCourante = Services.dateDuJour().substring(6, 10);
			setListeAnnee(new String[5]);
			getListeAnnee()[0] = String.valueOf(Integer.parseInt(anneeCourante));

			// TODO
			// changement de l'année pour faire au mieux.
			// getListeAnnee()[0] =
			// String.valueOf(Integer.parseInt(anneeCourante) + 1);
			getListeAnnee()[1] = String.valueOf(Integer.parseInt(anneeCourante) + 2);
			getListeAnnee()[2] = String.valueOf(Integer.parseInt(anneeCourante) + 3);
			getListeAnnee()[3] = String.valueOf(Integer.parseInt(anneeCourante) + 4);
			getListeAnnee()[4] = String.valueOf(Integer.parseInt(anneeCourante) + 5);
			setLB_ANNEE(getListeAnnee());
			addZone(getNOM_LB_ANNEE_SELECT(), Const.ZERO);
			setAnneeSelect(String.valueOf(Integer.parseInt(anneeCourante) + 1));
		}

		// Si liste medecins vide alors affectation
		if (getListeFiliere() == null || getListeFiliere().size() == 0) {
			setListeFiliere(FiliereGrade.listerFiliereGrade(getTransaction()));

			int[] tailles = { 30 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<FiliereGrade> list = getListeFiliere().listIterator(); list.hasNext();) {
				FiliereGrade fili = (FiliereGrade) list.next();
				String ligne[] = { fili.getLibFiliere() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_FILIERE(aFormat.getListeFormatee(true));

		}
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_FILTRER
			if (testerParametre(request, getNOM_PB_FILTRER())) {
				return performPB_FILTRER(request);
			}

			// Si clic sur le bouton PB_IMPRIMER
			if (testerParametre(request, getNOM_PB_IMPRIMER())) {
				return performPB_IMPRIMER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_AFFECTER
			if (testerParametre(request, getNOM_PB_AFFECTER())) {
				return performPB_AFFECTER(request);
			}

			// Si clic sur le bouton PB_RECHERCHER_AGENT
			if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT())) {
				return performPB_RECHERCHER_AGENT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT())) {
				return performPB_SUPPRIMER_RECHERCHER_AGENT(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_SERVICE
			if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE())) {
				return performPB_SUPPRIMER_RECHERCHER_SERVICE(request);
			}

			// Si clic sur le bouton PB_RAFRAICHIR
			for (int i = 0; i < getListeAvct().size(); i++) {
				AvancementFonctionnaires avct = getListeAvct().get(i);
				if (testerParametre(request, getNOM_PB_RAFRAICHIR(Integer.valueOf(avct.getIdAvct())))) {
					return performPB_RAFRAICHIR(request, avct.getIdAvct());
				}
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeAVCTFonctionnaires. Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public OeAVCTFonctCarrieres() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTFonctCarrieres.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CHANGER_ANNEE Date de
	 * création : (28/11/11)
	 * 
	 */
	public String getNOM_PB_FILTRER() {
		return "NOM_PB_FILTRER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (28/11/11)
	 * 
	 */
	public boolean performPB_FILTRER(HttpServletRequest request) throws Exception {
		int indiceAnnee = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
		String annee = (String) getListeAnnee()[indiceAnnee];
		setAnneeSelect(annee);

		// Recuperation filiere
		FiliereGrade filiere = null;
		int indiceFiliere = (Services.estNumerique(getVAL_LB_FILIERE_SELECT()) ? Integer.parseInt(getVAL_LB_FILIERE_SELECT()) : -1);
		if (indiceFiliere > 0) {
			filiere = (FiliereGrade) getListeFiliere().get(indiceFiliere - 1);
		}

		// recuperation agent
		AgentNW agent = null;
		if (getVAL_ST_AGENT().length() != 0) {
			agent = AgentNW.chercherAgentParMatricule(getTransaction(), getVAL_ST_AGENT());
		}

		// recuperation du service
		ArrayList<String> listeSousService = null;
		if (getVAL_ST_CODE_SERVICE().length() != 0) {
			// on recupere les sous-service du service selectionne
			Service serv = Service.chercherService(getTransaction(), getVAL_ST_CODE_SERVICE());
			listeSousService = Service.listSousServiceBySigle(getTransaction(), serv.getSigleService());
		}

		String reqEtat = " and (ETAT='" + EnumEtatAvancement.ARRETE.getValue() + "' ) and avct.ID_AVIS_EMP!= 5 ";
		setListeAvct(AvancementFonctionnaires.listerAvancementAvecAnneeEtat(getTransaction(), annee, reqEtat, filiere, agent, listeSousService));

		afficheListeAvancement();
		return true;
	}

	private void afficheListeAvancement() throws Exception {
		for (int j = 0; j < getListeAvct().size(); j++) {
			AvancementFonctionnaires av = (AvancementFonctionnaires) getListeAvct().get(j);
			Integer i = Integer.valueOf(av.getIdAvct());
			AgentNW agent = AgentNW.chercherAgent(getTransaction(), av.getIdAgent());
			Grade gradeAgent = Grade.chercherGrade(getTransaction(), av.getGrade());
			Grade gradeSuivantAgent = Grade.chercherGrade(getTransaction(), av.getIdNouvGrade());

			addZone(getNOM_ST_AGENT(i), agent.getNomAgent() + " <br> " + agent.getPrenomAgent() + " <br> " + agent.getNoMatricule());
			addZone(getNOM_ST_DIRECTION(i), av.getDirectionService() + " <br> " + av.getSectionService());
			addZone(getNOM_ST_CATEGORIE(i), (av.getCodeCadre() == null ? "&nbsp;" : av.getCodeCadre()) + " <br> " + av.getFiliere());
			addZone(getNOM_ST_DATE_CAP(i), av.getDateCap());
			addZone(getNOM_ST_GRADE(i),
					av.getGrade() + " <br> " + (av.getIdNouvGrade() != null && av.getIdNouvGrade().length() != 0 ? av.getIdNouvGrade() : "&nbsp;"));
			String libGrade = gradeAgent == null ? "&nbsp;" : gradeAgent.getLibGrade();
			String libNouvGrade = gradeSuivantAgent == null ? "&nbsp;" : gradeSuivantAgent.getLibGrade();
			addZone(getNOM_ST_GRADE_LIB(i), libGrade + " <br> " + libNouvGrade);

			addZone(getNOM_ST_NUM_AVCT(i), av.getIdAvct());

			String idAvisEmp = AvisCap.chercherAvisCap(getTransaction(), av.getIdAvisEmp()).getLibCourtAvisCAP().toUpperCase();
			String dateAvctFinale = Const.CHAINE_VIDE;
			if (idAvisEmp.equals("MIN")) {
				dateAvctFinale = av.getDateAvctMini();
			} else if (idAvisEmp.equals("MOY")) {
				dateAvctFinale = av.getDateAvctMoy();
			} else if (idAvisEmp.equals("MAX")) {
				dateAvctFinale = av.getDateAvctMaxi();
			} else if (idAvisEmp.equals("FAV")) {
				dateAvctFinale = av.getDateAvctMoy();
			}
			addZone(getNOM_ST_DATE_AVCT(i), dateAvctFinale);

			addZone(getNOM_ST_NUM_ARRETE(i), av.getNumArrete());
			addZone(getNOM_ST_DATE_ARRETE(i), av.getDateArrete().equals(Const.DATE_NULL) ? Const.CHAINE_VIDE : av.getDateArrete());
			addZone(getNOM_CK_AFFECTER(i), av.getEtat().equals(av.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue())) ? getCHECKED_ON()
					: getCHECKED_OFF());
			addZone(getNOM_ST_ETAT(i), av.getEtat());
			addZone(getNOM_ST_CARRIERE_SIMU(i), av.getCarriereSimu() == null ? "&nbsp;" : "oui");

		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_IMPRIMER Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_IMPRIMER() {
		return "NOM_PB_IMPRIMER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_IMPRIMER(HttpServletRequest request) throws Exception {
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_AFFECTER(HttpServletRequest request) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);

		// format date de debut
		if (!getVAL_ST_DATE_ARR_GLOBALE().equals(Const.CHAINE_VIDE) && !Services.estUneDate(getVAL_ST_DATE_ARR_GLOBALE())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "d'arrêté"));
			return false;
		}
		// on recupere les lignes qui sont cochées pour affecter
		int nbAgentAffectes = 0;
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recupère la ligne concernée
			AvancementFonctionnaires avct = (AvancementFonctionnaires) getListeAvct().get(j);
			Integer i = Integer.valueOf(avct.getIdAvct());
			// si l'etat de la ligne n'est pas deja 'affecte' et que la colonne
			// affecté est cochée
			if (!avct.getEtat().equals(EnumEtatAvancement.AFFECTE)) {
				if (getVAL_CK_AFFECTER(i).equals(getCHECKED_ON())) {
					// on recupere l'agent concerné
					AgentNW agentCarr = AgentNW.chercherAgent(getTransaction(), avct.getIdAgent());
					// on recupere la carrière en cours
					Carriere carr = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agentCarr);
					// si la carriere est bien la derniere de la liste
					if (carr.getDateFin() == null || carr.getDateFin().equals("0")) {
						// alors on fait les modifs sur avancement
						avct.setEtat(EnumEtatAvancement.AFFECTE.getValue());
						addZone(getNOM_ST_ETAT(i), avct.getEtat());
						// on traite le numero et la date d'arreté
						String dateArr = getVAL_ST_DATE_ARR_GLOBALE();
						avct.setDateArrete(dateArr.equals(Const.CHAINE_VIDE) ? null : dateArr);
						avct.setNumArrete(getVAL_ST_NUM_ARRETE(i).equals(Const.CHAINE_VIDE) ? null : getVAL_ST_NUM_ARRETE(i));

						// pourla date d'avancement
						String idAvisEmp = AvisCap.chercherAvisCap(getTransaction(), avct.getIdAvisEmp()).getLibCourtAvisCAP().toUpperCase();
						String dateAvctFinale = Const.ZERO;
						if (idAvisEmp.equals("MIN")) {
							dateAvctFinale = avct.getDateAvctMini();
						} else if (idAvisEmp.equals("MOY")) {
							dateAvctFinale = avct.getDateAvctMoy();
						} else if (idAvisEmp.equals("MAX")) {
							dateAvctFinale = avct.getDateAvctMaxi();
						} else if (idAvisEmp.equals("FAV")) {
							dateAvctFinale = avct.getDateAvctMoy();
						} else {
							agentEnErreur += agentCarr.getNomAgent() + " " + agentCarr.getPrenomAgent() + " (" + agentCarr.getNoMatricule() + "); ";
							continue;
						}

						// on ferme cette carriere
						carr.setDateFin(dateAvctFinale);
						carr.modifierCarriere(getTransaction(), agentCarr, user);

						// on crée un nouvelle carriere
						Carriere nouvelleCarriere = new Carriere();
						nouvelleCarriere.setCodeCategorie(carr.getCodeCategorie());
						nouvelleCarriere.setReferenceArrete(avct.getNumArrete() == null ? Const.ZERO : avct.getNumArrete());
						nouvelleCarriere.setDateArrete(avct.getDateArrete() == null ? Const.ZERO : avct.getDateArrete());
						nouvelleCarriere.setDateDebut(dateAvctFinale);
						nouvelleCarriere.setDateFin(Const.ZERO);
						// on calcul Grade - ACC/BM en fonction de l'avis CAP
						// il est différent du resultat affiché dans le tableau
						// si AVIS_CAP != MOY
						// car pour la simulation on prenait comme ref de calcul
						// la duree MOY
						calculAccBm(avct, carr, nouvelleCarriere, idAvisEmp);

						// on recupere iban du grade
						Grade gradeSuivant = Grade.chercherGrade(getTransaction(), avct.getIdNouvGrade());
						nouvelleCarriere.setIban(Services.lpad(gradeSuivant.getIban(), 7, "0"));

						nouvelleCarriere.setCodeMotif(avct.getIdMotifAvct());

						// champ à remplir pour creer une carriere NB : on
						// reprend ceux de la carriere precedente
						nouvelleCarriere.setCodeBase(carr.getCodeBase());
						nouvelleCarriere.setCodeTypeEmploi(carr.getCodeTypeEmploi());
						nouvelleCarriere.setCodeBaseHoraire2(carr.getCodeBaseHoraire2());
						nouvelleCarriere.setModeReglement(carr.getModeReglement());
						nouvelleCarriere.setTypeContrat(carr.getTypeContrat());

						nouvelleCarriere.creerCarriere(getTransaction(), agentCarr, user);

						// on enregistre

						if (getTransaction().isErreur()) {
							return false;
						} else {
							nbAgentAffectes += 1;
						}
					} else {
						// si ce n'est pas la derniere carriere du tableau ie :
						// si datfin!=0
						// on met l'agent dans une variable et on affiche cette
						// liste à l'ecran
						agentEnErreur += agentCarr.getNomAgent() + " " + agentCarr.getPrenomAgent() + " (" + agentCarr.getNoMatricule() + "); ";
						// on met un 'S' dans son avancement
						avct.setCarriereSimu("S");
						avct.modifierAvancement(getTransaction());
					}
				}
			}
		}
		// on valide les modifis
		commitTransaction();

		// "INF201","@ agents ont été affectés."
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF201", String.valueOf(nbAgentAffectes)));
		performPB_FILTRER(request);
		return true;
	}

	private void calculAccBm(AvancementFonctionnaires avct, Carriere ancienneCarriere, Carriere nouvelleCarriere, String libCourtAvisCap)
			throws Exception {
		// calcul BM/ACC applicables
		int nbJoursBM = 0;
		int nbJoursACC = 0;
		Grade gradeActuel = Grade.chercherGrade(getTransaction(), ancienneCarriere.getCodeGrade());
		if (gradeActuel.getBm().equals(Const.OUI)) {
			nbJoursBM += (Integer.parseInt(ancienneCarriere.getBMAnnee()) * 360) + (Integer.parseInt(ancienneCarriere.getBMMois()) * 30)
					+ Integer.parseInt(ancienneCarriere.getBMJour());
		}
		if (gradeActuel.getAcc().equals(Const.OUI)) {
			nbJoursACC += (Integer.parseInt(ancienneCarriere.getACCAnnee()) * 360) + (Integer.parseInt(ancienneCarriere.getACCMois()) * 30)
					+ Integer.parseInt(ancienneCarriere.getACCJour());
		}

		int nbJoursBonus = nbJoursBM + nbJoursACC;

		// Calcul date avancement au Grade actuel
		if (libCourtAvisCap.equals("MIN")) {
			if (nbJoursBonus > Integer.parseInt(gradeActuel.getDureeMin()) * 30) {
				avct.setDateAvctMini(ancienneCarriere.getDateDebut().substring(0, 6) + avct.getAnnee());
				nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMin()) * 30;
			} else {
				avct.setDateAvctMini(Services.enleveJours(
						Services.ajouteMois(Services.formateDate(ancienneCarriere.getDateDebut()), Integer.parseInt(gradeActuel.getDureeMin())),
						nbJoursBonus));
				nbJoursBonus = 0;
			}
		} else if (libCourtAvisCap.equals("MOY") || libCourtAvisCap.equals("FAV")) {
			avct.setDureeStandard(gradeActuel.getDureeMoy());
			if (nbJoursBonus > Integer.parseInt(gradeActuel.getDureeMoy()) * 30) {
				avct.setDateAvctMoy(ancienneCarriere.getDateDebut().substring(0, 6) + avct.getAnnee());
				nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMoy()) * 30;
			} else {
				avct.setDateAvctMoy(Services.enleveJours(
						Services.ajouteMois(Services.formateDate(ancienneCarriere.getDateDebut()), Integer.parseInt(gradeActuel.getDureeMoy())),
						nbJoursBonus));
				nbJoursBonus = 0;
			}
		} else if (libCourtAvisCap.equals("MAX")) {
			if (nbJoursBonus > Integer.parseInt(gradeActuel.getDureeMax()) * 30) {
				avct.setDateAvctMaxi(ancienneCarriere.getDateDebut().substring(0, 6) + avct.getAnnee());
				nbJoursBonus -= Integer.parseInt(gradeActuel.getDureeMax()) * 30;
			} else {
				avct.setDateAvctMaxi(Services.enleveJours(
						Services.ajouteMois(Services.formateDate(ancienneCarriere.getDateDebut()), Integer.parseInt(gradeActuel.getDureeMax())),
						nbJoursBonus));
				nbJoursBonus = 0;
			}
		}

		// Calcul du grade suivant (BM/ACC)
		Grade gradeSuivant = Grade.chercherGrade(getTransaction(), gradeActuel.getCodeGradeSuivant());
		if (libCourtAvisCap.equals("MIN")) {
			boolean isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMin()) * 30);
			while (isReliquatSuffisant && gradeSuivant.getCodeGradeSuivant() != null && gradeSuivant.getCodeGradeSuivant().length() > 0
					&& gradeSuivant.getDureeMin() != null && gradeSuivant.getDureeMin().length() > 0) {
				nbJoursBonus -= Integer.parseInt(gradeSuivant.getDureeMin()) * 30;
				gradeSuivant = Grade.chercherGrade(getTransaction(), gradeSuivant.getCodeGradeSuivant());
				isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMin()) * 30);
			}
		} else if (libCourtAvisCap.equals("MOY") || libCourtAvisCap.equals("FAV")) {
			boolean isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMoy()) * 30);
			while (isReliquatSuffisant && gradeSuivant.getCodeGradeSuivant() != null && gradeSuivant.getCodeGradeSuivant().length() > 0
					&& gradeSuivant.getDureeMoy() != null && gradeSuivant.getDureeMoy().length() > 0) {
				nbJoursBonus -= Integer.parseInt(gradeSuivant.getDureeMoy()) * 30;
				gradeSuivant = Grade.chercherGrade(getTransaction(), gradeSuivant.getCodeGradeSuivant());
				isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMoy()) * 30);
			}
		} else if (libCourtAvisCap.equals("MAX")) {
			boolean isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMax()) * 30);
			while (isReliquatSuffisant && gradeSuivant.getCodeGradeSuivant() != null && gradeSuivant.getCodeGradeSuivant().length() > 0
					&& gradeSuivant.getDureeMax() != null && gradeSuivant.getDureeMax().length() > 0) {
				nbJoursBonus -= Integer.parseInt(gradeSuivant.getDureeMax()) * 30;
				gradeSuivant = Grade.chercherGrade(getTransaction(), gradeSuivant.getCodeGradeSuivant());
				isReliquatSuffisant = (nbJoursBonus > Integer.parseInt(gradeSuivant.getDureeMax()) * 30);
			}
		}

		int nbJoursRestantsBM = nbJoursBonus > nbJoursACC ? nbJoursBonus - nbJoursACC : Integer.parseInt(Const.ZERO);
		int nbJoursRestantsACC = nbJoursBonus - nbJoursRestantsBM;

		// on met à jour les champs de l'avancement pour affichage tableau
		avct.setNouvBMAnnee(String.valueOf(nbJoursRestantsBM / 365));
		avct.setNouvBMMois(String.valueOf((nbJoursRestantsBM % 365) / 30));
		avct.setNouvBMJour(String.valueOf((nbJoursRestantsBM % 365) % 30));

		avct.setNouvACCAnnee(String.valueOf(nbJoursRestantsACC / 365));
		avct.setNouvACCMois(String.valueOf((nbJoursRestantsACC % 365) / 30));
		avct.setNouvACCJour(String.valueOf((nbJoursRestantsACC % 365) % 30));

		avct.setIdNouvGrade(gradeSuivant.getCodeGrade() == null || gradeSuivant.getCodeGrade().length() == 0 ? null : gradeSuivant.getCodeGrade());
		// avct.setLibNouvGrade(gradeSuivant.getLibGrade());

		avct.modifierAvancement(getTransaction());

		// on met à jour les champs pour la creation de la carriere
		nouvelleCarriere.setCodeGrade(avct.getIdNouvGrade());
		nouvelleCarriere.setACCAnnee(avct.getNouvACCAnnee());
		nouvelleCarriere.setACCMois(avct.getNouvACCMois());
		nouvelleCarriere.setACCJour(avct.getNouvACCJour());
		nouvelleCarriere.setBMAnnee(avct.getNouvBMAnnee());
		nouvelleCarriere.setBMMois(avct.getNouvBMMois());
		nouvelleCarriere.setBMJour(avct.getNouvBMJour());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AFFECTER Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_AFFECTER() {
		return "NOM_PB_AFFECTER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		// format date de debut
		if (!getVAL_ST_DATE_ARR_GLOBALE().equals(Const.CHAINE_VIDE) && !Services.estUneDate(getVAL_ST_DATE_ARR_GLOBALE())) {
			// "ERR007",
			// "La date @ est incorrecte. Elle doit être au format date."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "d'arrêté"));
			return false;
		}
		// on sauvegarde l'état du tableau
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recupère la ligne concernée
			AvancementFonctionnaires avct = (AvancementFonctionnaires) getListeAvct().get(j);
			Integer i = Integer.valueOf(avct.getIdAvct());
			// on fait les modifications
			if (!avct.getEtat().equals(EnumEtatAvancement.AFFECTE)) {
				// on traite l'etat
				if (getVAL_CK_AFFECTER(i).equals(getCHECKED_ON())) {
					// avct.setEtat(EnumEtatAvancement.VALIDE.getValue());
				} else {
					avct.setEtat(EnumEtatAvancement.ARRETE.getValue());
				}
				// on traite le numero et la date d'arreté
				String dateArr = getVAL_ST_DATE_ARR_GLOBALE();
				avct.setDateArrete(dateArr.equals(Const.CHAINE_VIDE) ? null : dateArr);
				avct.setNumArrete(getVAL_ST_NUM_ARRETE(i).equals(Const.CHAINE_VIDE) ? null : getVAL_ST_NUM_ARRETE(i));

			}
			avct.modifierAvancement(getTransaction());
			if (getTransaction().isErreur())
				return false;
		}
		// on enregistre
		commitTransaction();
		performPB_FILTRER(request);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_AGENT(int i) {
		return "NOM_ST_AGENT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CATEGORIE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CATEGORIE(int i) {
		return "NOM_ST_CATEGORIE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CATEGORIE Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CATEGORIE(int i) {
		return getZone(getNOM_ST_CATEGORIE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_AVCT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_AVCT(int i) {
		return "NOM_ST_DATE_AVCT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_AVCT Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_AVCT(int i) {
		return getZone(getNOM_ST_DATE_AVCT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_CAP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_CAP(int i) {
		return "NOM_ST_DATE_CAP_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_CAP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_CAP(int i) {
		return getZone(getNOM_ST_DATE_CAP(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DIRECTION Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DIRECTION(int i) {
		return "NOM_ST_DIRECTION_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DIRECTION Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DIRECTION(int i) {
		return getZone(getNOM_ST_DIRECTION(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE_LIB Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_GRADE_LIB(int i) {
		return "NOM_ST_GRADE_LIB_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_GRADE_LIB Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_GRADE_LIB(int i) {
		return getZone(getNOM_ST_GRADE_LIB(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_GRADE(int i) {
		return "NOM_ST_GRADE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_GRADE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_GRADE(int i) {
		return getZone(getNOM_ST_GRADE(i));
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : ST_DATE_ARRETE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_ARRETE(int i) {
		return "NOM_ST_DATE_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * ST_DATE_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */

	public String getVAL_ST_DATE_ARRETE(int i) {
		return getZone(getNOM_ST_DATE_ARRETE(i));
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_ST_ARRETE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NUM_ARRETE(int i) {
		return "NOM_ST_NUM_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * ST_NUM_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NUM_ARRETE(int i) {
		return getZone(getNOM_ST_NUM_ARRETE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NUM_AVCT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_NUM_AVCT(int i) {
		return "NOM_ST_NUM_AVCT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NUM_AVCT Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_NUM_AVCT(int i) {
		return getZone(getNOM_ST_NUM_AVCT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ETAT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ETAT(int i) {
		return "NOM_ST_ETAT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ETAT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ETAT(int i) {
		return getZone(getNOM_ST_ETAT(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_AFFECTER Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_AFFECTER(int i) {
		return "NOM_CK_AFFECTER_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * à cocher : CK_AFFECTER Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_AFFECTER(int i) {
		return getZone(getNOM_CK_AFFECTER(i));
	}

	/**
	 * Getter de la liste des avancements des fonctionnaires.
	 * 
	 * @return listeAvct
	 */
	public ArrayList<AvancementFonctionnaires> getListeAvct() {
		return listeAvct == null ? new ArrayList<AvancementFonctionnaires>() : listeAvct;
	}

	/**
	 * Setter de la liste des avancements des fonctionnaires.
	 * 
	 * @param listeAvct
	 */
	private void setListeAvct(ArrayList<AvancementFonctionnaires> listeAvct) {
		this.listeAvct = listeAvct;
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-AVCT-FONCT-CARRIERES";
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_ANNEE Date de création :
	 * (28/11/11)
	 * 
	 */
	private String[] getLB_ANNEE() {
		if (LB_ANNEE == null)
			LB_ANNEE = initialiseLazyLB();
		return LB_ANNEE;
	}

	/**
	 * Setter de la liste: LB_ANNEE Date de création : (28/11/11)
	 * 
	 */
	private void setLB_ANNEE(String[] newLB_ANNEE) {
		LB_ANNEE = newLB_ANNEE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ANNEE Date de création :
	 * (28/11/11)
	 * 
	 */
	public String getNOM_LB_ANNEE() {
		return "NOM_LB_ANNEE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ANNEE_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_ANNEE_SELECT() {
		return "NOM_LB_ANNEE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_ANNEE Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_ANNEE() {
		return getLB_ANNEE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_ANNEE Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_ANNEE_SELECT() {
		return getZone(getNOM_LB_ANNEE_SELECT());
	}

	/**
	 * Getter de la liste des années possibles.
	 * 
	 * @return listeAnnee
	 */
	private String[] getListeAnnee() {
		return listeAnnee;
	}

	/**
	 * Setter de la liste des années possibles.
	 * 
	 * @param listeAnnee
	 */
	private void setListeAnnee(String[] listeAnnee) {
		this.listeAnnee = listeAnnee;
	}

	/**
	 * Getter de l'annee sélectionnée.
	 * 
	 * @return anneeSelect
	 */
	public String getAnneeSelect() {
		return anneeSelect;
	}

	/**
	 * Setter de l'année sélectionnée
	 * 
	 * @param newAnneeSelect
	 */
	public void setAnneeSelect(String newAnneeSelect) {
		this.anneeSelect = newAnneeSelect;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CARRIERE_SIMU Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_CARRIERE_SIMU(int i) {
		return "NOM_ST_CARRIERE_SIMU_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CARRIERE_SIMU
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_CARRIERE_SIMU(int i) {
		return getZone(getNOM_ST_CARRIERE_SIMU(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_USER_VALID_CARRIERE
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_USER_VALID_CARRIERE(int i) {
		return "NOM_ST_USER_VALID_CARRIERE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_USER_VALID_CARRIERE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_USER_VALID_CARRIERE(int i) {
		return getZone(getNOM_ST_USER_VALID_CARRIERE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (02/08/11 09:40:42)
	 * 
	 */
	public String getNOM_ST_AGENT() {
		return "NOM_ST_AGENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (02/08/11 09:40:42)
	 * 
	 */
	public String getVAL_ST_AGENT() {
		return getZone(getNOM_ST_AGENT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_AGENT Date de
	 * création : (02/08/11 09:42:00)
	 * 
	 */
	public String getNOM_PB_RECHERCHER_AGENT() {
		return "NOM_PB_RECHERCHER_AGENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:42:00)
	 * 
	 */
	public boolean performPB_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());

		setStatut(STATUT_RECHERCHER_AGENT, true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_RECHERCHER_AGENT
	 * Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_AGENT(HttpServletRequest request) throws Exception {
		// On enlève l'agent selectionnée
		addZone(getNOM_ST_AGENT(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_SERVICE Date de
	 * création : (13/09/11 11:47:15)
	 * 
	 */
	public String getNOM_EF_SERVICE() {
		return "NOM_EF_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_SERVICE Date de création : (13/09/11 11:47:15)
	 * 
	 */
	public String getVAL_EF_SERVICE() {
		return getZone(getNOM_EF_SERVICE());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_RECHERCHER_SERVICE
	 * Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE() {
		return "NOM_PB_SUPPRIMER_RECHERCHER_SERVICE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 09:49:02)
	 * 
	 * 
	 */

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_SUPPRIMER_RECHERCHER_SERVICE(HttpServletRequest request) throws Exception {
		// On enlève le service selectionnée
		addZone(getNOM_ST_CODE_SERVICE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SERVICE(), Const.CHAINE_VIDE);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE_SERVICE Date de
	 * création : (13/09/11 08:45:29)
	 * 
	 */
	public String getNOM_ST_CODE_SERVICE() {
		return "NOM_ST_CODE_SERVICE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_CODE_SERVICE
	 * Date de création : (13/09/11 08:45:29)
	 * 
	 */
	public String getVAL_ST_CODE_SERVICE() {
		return getZone(getNOM_ST_CODE_SERVICE());
	}

	/**
	 * Retourne la liste des services.
	 * 
	 * @return listeServices
	 */
	public ArrayList<Service> getListeServices() {
		return listeServices;
	}

	/**
	 * Met à jour la liste des services.
	 * 
	 * @param listeServices
	 */
	private void setListeServices(ArrayList<Service> listeServices) {
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
	 * Getter de la liste avec un lazy initialize : LB_FILIERE Date de création
	 * : (28/11/11)
	 * 
	 */
	private String[] getLB_FILIERE() {
		if (LB_FILIERE == null)
			LB_FILIERE = initialiseLazyLB();
		return LB_FILIERE;
	}

	/**
	 * Setter de la liste: LB_FILIERE Date de création : (28/11/11)
	 * 
	 */
	private void setLB_FILIERE(String[] newLB_FILIERE) {
		LB_FILIERE = newLB_FILIERE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_FILIERE Date de création
	 * : (28/11/11)
	 * 
	 */
	public String getNOM_LB_FILIERE() {
		return "NOM_LB_FILIERE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_FILIERE_SELECT Date de création : (28/11/11)
	 * 
	 */
	public String getNOM_LB_FILIERE_SELECT() {
		return "NOM_LB_FILIERE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_FILIERE Date de création : (28/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_FILIERE() {
		return getLB_FILIERE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_FILIERE Date de création : (28/11/11)
	 * 
	 */
	public String getVAL_LB_FILIERE_SELECT() {
		return getZone(getNOM_LB_FILIERE_SELECT());
	}

	public ArrayList<FiliereGrade> getListeFiliere() {
		return listeFiliere == null ? new ArrayList<FiliereGrade>() : listeFiliere;
	}

	public void setListeFiliere(ArrayList<FiliereGrade> listeFiliere) {
		this.listeFiliere = listeFiliere;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RAFRAICHIR Date de création
	 * : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_RAFRAICHIR(int i) {
		return "NOM_PB_RAFRAICHIR_" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public boolean performPB_RAFRAICHIR(HttpServletRequest request, String idAvct) throws Exception {
		AvancementFonctionnaires avct = AvancementFonctionnaires.chercherAvancement(getTransaction(), idAvct);
		AgentNW agent = AgentNW.chercherAgent(getTransaction(), avct.getIdAgent());
		Carriere carrEnCours = Carriere.chercherCarriereEnCoursAvecAgent(getTransaction(), agent);

		// on regarde si l'agent a une carriere de simulation dejà
		// saisie
		// autrement dis si la carriere actuelle a pour datfin 0
		if (carrEnCours.getDateFin() == null || carrEnCours.getDateFin().equals(Const.ZERO)) {
			avct.setCarriereSimu(null);
		} else {
			avct.setCarriereSimu("S");
		}
		avct.modifierAvancement(getTransaction());
		commitTransaction();

		performPB_FILTRER(request);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_ARR Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_ARR_GLOBALE() {
		return "NOM_ST_DATE_ARR_GLOBALE_";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_ARR Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_ARR_GLOBALE() {
		return getZone(getNOM_ST_DATE_ARR_GLOBALE());
	}
}