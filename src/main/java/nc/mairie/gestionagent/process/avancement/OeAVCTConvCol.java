package nc.mairie.gestionagent.process.avancement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumEtatAvancement;
import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.HistoPrime;
import nc.mairie.metier.agent.PositionAdm;
import nc.mairie.metier.agent.Prime;
import nc.mairie.metier.avancement.AvancementConvCol;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.agent.HistoPrimeDao;
import nc.mairie.spring.dao.metier.avancement.AvancementConvColDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

/**
 * Process OeAVCTFonctionnaires Date de création : (21/11/11 09:55:36)
 * 
 */
public class OeAVCTConvCol extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] LB_ANNEE;

	private String[] listeAnnee;
	private String anneeSelect;

	private ArrayList<AvancementConvCol> listeAvct;

	public String agentEnErreur = Const.CHAINE_VIDE;

	private AvancementConvColDao avancementConvColDao;
	private HistoPrimeDao histoPrimeDao;
	private AgentDao agentDao;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getAvancementConvColDao() == null) {
			setAvancementConvColDao(new AvancementConvColDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getHistoPrimeDao() == null) {
			setHistoPrimeDao(new HistoPrimeDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
	}

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
		initialiseDao();
		// Initialisation des listes déroulantes
		initialiseListeDeroulante();

		// Si liste avancements vide alors initialisation.
		if (getListeAvct().size() == 0) {
			int indiceAnnee = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer
					.parseInt(getVAL_LB_ANNEE_SELECT()) : -1);
			String annee = (String) getListeAnnee()[indiceAnnee];
			setListeAvct(getAvancementConvColDao().listerAvancementConvColAvecAnnee(Integer.valueOf(annee)));

			for (int j = 0; j < getListeAvct().size(); j++) {
				AvancementConvCol av = (AvancementConvCol) getListeAvct().get(j);
				Integer i = av.getIdAvct();
				Agent agent = getAgentDao().chercherAgent(av.getIdAgent());

				addZone(getNOM_ST_GRADE(i), av.getGrade());
				addZone(getNOM_ST_GRADE_LIB(i), av.getLibGrade() == null ? "&nbsp;" : av.getLibGrade());
				addZone(getNOM_ST_DIRECTION(i), av.getDirectionService() + " <br> " + av.getSectionService());
				addZone(getNOM_ST_MATRICULE(i), agent.getNomatr().toString());
				addZone(getNOM_ST_AGENT(i), agent.getNomAgent() + " <br> " + agent.getPrenomAgent());
				addZone(getNOM_ST_DATE_EMBAUCHE(i),
						av.getDateEmbauche() == null ? "&nbsp;" : sdf.format(av.getDateEmbauche()));
				PositionAdm pa = PositionAdm.chercherPositionAdm(getTransaction(), av.getCodePa());
				addZone(getNOM_ST_PA(i), pa.getLiPAdm());

				addZone(getNOM_CK_VALID_DRH(i),
						av.getEtat().equals(EnumEtatAvancement.TRAVAIL.getValue()) ? getCHECKED_OFF() : getCHECKED_ON());
				addZone(getNOM_ST_MOTIF_AVCT(i), "REVALORISATION");
				addZone(getNOM_CK_PROJET_ARRETE(i), av.getEtat().equals(EnumEtatAvancement.TRAVAIL.getValue())
						|| av.getEtat().equals(EnumEtatAvancement.SGC.getValue()) ? getCHECKED_OFF() : getCHECKED_ON());
				addZone(getNOM_EF_NUM_ARRETE(i), av.getNumArrete());
				addZone(getNOM_EF_DATE_ARRETE(i),
						av.getDateArrete() == null ? Const.CHAINE_VIDE : sdf.format(av.getDateArrete()));
				addZone(getNOM_CK_AFFECTER(i), av.getEtat().equals(EnumEtatAvancement.VALIDE.getValue())
						|| av.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue()) ? getCHECKED_ON()
						: getCHECKED_OFF());
				addZone(getNOM_ST_ETAT(i), av.getEtat());
				addZone(getNOM_ST_CARRIERE_SIMU(i), av.getCarriereSimu() == null ? "&nbsp;" : av.getCarriereSimu());
				addZone(getNOM_ST_MONTANT_PRIME(i),
						(av.getMontantPrime1200() == null ? "&nbsp;" : av.getMontantPrime1200())
								+ " <br> "
								+ (av.getMontantPrime1200() == null
										|| av.getMontantPrime1200().equals(Const.CHAINE_VIDE) ? "&nbsp;" : Integer
										.valueOf(av.getMontantPrime1200()) == 30 ? "30" : String.valueOf(Integer
										.valueOf(av.getMontantPrime1200()) + 1)));
			}
		}
	}

	/**
	 * Initialisation des liste déroulantes.
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste annee vide alors affectation
		if (getLB_ANNEE() == LBVide) {
			String anneeCourante = (String) ServletAgent.getMesParametres().get("ANNEE_AVCT");
			setListeAnnee(new String[1]);
			getListeAnnee()[0] = String.valueOf(Integer.parseInt(anneeCourante));

			setLB_ANNEE(getListeAnnee());
			addZone(getNOM_LB_ANNEE_SELECT(), Const.ZERO);
			setAnneeSelect(String.valueOf(Integer.parseInt(anneeCourante) + 1));
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

			// Si clic sur le bouton PB_CHANGER_ANNEE
			if (testerParametre(request, getNOM_PB_CHANGER_ANNEE())) {
				return performPB_CHANGER_ANNEE(request);
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
	public OeAVCTConvCol() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getJSP() {
		return "OeAVCTConvCol.jsp";
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
	 * Retourne le nom d'un bouton pour la JSP : PB_FILTRER Date de création :
	 * (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_PB_FILTRER() {
		return "NOM_PB_FILTRER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CHANGER_ANNEE Date de
	 * création : (28/11/11)
	 * 
	 */
	public String getNOM_PB_CHANGER_ANNEE() {
		return "NOM_PB_CHANGER_ANNEE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (28/11/11)
	 * 
	 */
	public boolean performPB_CHANGER_ANNEE(HttpServletRequest request) throws Exception {
		int indiceAnnee = (Services.estNumerique(getVAL_LB_ANNEE_SELECT()) ? Integer.parseInt(getVAL_LB_ANNEE_SELECT())
				: -1);
		String annee = (String) getListeAnnee()[indiceAnnee];
		if (!annee.equals(getAnneeSelect())) {
			setListeAvct(null);
			setAnneeSelect(annee);
		}
		return true;
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
	public boolean performPB_AFFECTER(HttpServletRequest request) throws Exception {
		UserAppli user = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
		// on recupere les lignes qui sont cochées pour affecter
		int nbAgentAffectes = 0;
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recupère la ligne concernée
			AvancementConvCol avct = (AvancementConvCol) getListeAvct().get(j);
			Integer i = avct.getIdAvct();
			// si l'etat de la ligne n'est pas deja 'affecte' et que la colonne
			// affecté est cochée
			if (!avct.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue())) {
				if (getVAL_CK_AFFECTER(i).equals(getCHECKED_ON())) {

					// on crée une ligne de prime
					Agent agent = getAgentDao().chercherAgent(avct.getIdAgent());

					// on regarde si la prime existe dejà ou pas
					@SuppressWarnings("unused")
					Prime primeExist = Prime.chercherPrime1200ByRubrAndDate(getTransaction(), agent.getNomatr(),
							avct.getAnnee() + "0101");
					if (getTransaction().isErreur()) {
						getTransaction().traiterErreur();
						avct.setCarriereSimu(null);
					} else {
						// c'est qu'il existe une prime pour cette date

						// si ce n'est pas la derniere carriere du tableau ie :
						// si datfin!=0
						// on met l'agent dans une variable et on affiche cette
						// liste à l'ecran
						agentEnErreur += agent.getNomAgent() + " " + agent.getPrenomAgent() + " (" + agent.getNomatr()
								+ "); ";
						// on met un 'S' dans son avancement
						avct.setCarriereSimu("S");
						getAvancementConvColDao().modifierAvancementConvCol(avct.getIdAvct(), avct.getIdAgent(),
								avct.getAnnee(), avct.getEtat(), avct.getNumArrete(), avct.getDateArrete(),
								avct.getDateEmbauche(), avct.getGrade(), avct.getLibGrade(),
								avct.getDirectionService(), avct.getSectionService(), avct.getCarriereSimu(),
								avct.getMontantPrime1200(), avct.getCodePa());
						continue;
					}

					// alors on fait les modifs sur avancement
					avct.setEtat(EnumEtatAvancement.AFFECTE.getValue());
					addZone(getNOM_ST_ETAT(i), avct.getEtat());

					// on traite le numero et la date d'arreté
					avct.setDateArrete(getVAL_EF_DATE_ARRETE(i).equals(Const.CHAINE_VIDE) ? null : sdf
							.parse(getVAL_EF_DATE_ARRETE(i)));
					avct.setNumArrete(getVAL_EF_NUM_ARRETE(i));
					getAvancementConvColDao().modifierAvancementConvCol(avct.getIdAvct(), avct.getIdAgent(),
							avct.getAnnee(), avct.getEtat(), avct.getNumArrete(), avct.getDateArrete(),
							avct.getDateEmbauche(), avct.getGrade(), avct.getLibGrade(), avct.getDirectionService(),
							avct.getSectionService(), avct.getCarriereSimu(), avct.getMontantPrime1200(),
							avct.getCodePa());

					// on recherche la derniere ligne de prime pour la rubrique
					// 1200(prime ancienneté)
					Prime prime = Prime.chercherDernierePrimeOuverteAvecRubrique(getTransaction(), agent.getNomatr(),
							"1200");
					// si il y en a une alors on la ferme et on en crée une
					// nouvelle
					if (!getTransaction().isErreur()) {
						if (!prime.getDatDeb().equals("01/01/" + avct.getAnnee())) {
							prime.setDatFin("01/01/" + avct.getAnnee());
							prime.setNoRubr(prime.getNoRubr());
							prime.setDatDeb(prime.getDatDeb());
							// RG_AG_PR_A04
							HistoPrime histo = new HistoPrime(prime);
							getHistoPrimeDao().creerHistoPrime(histo, user, EnumTypeHisto.MODIFICATION);
							prime.modifierPrime(getTransaction(), agent, user);

							Prime newPrime = new Prime();
							newPrime.setNoMatr(agent.getNomatr().toString());
							if ((Integer.valueOf(prime.getMtPri()) + 1) > 30) {
								newPrime.setMtPri("30");
							} else {
								newPrime.setMtPri(String.valueOf(Integer.valueOf(prime.getMtPri()) + 1));
							}
							newPrime.setDatDeb("01/01/" + avct.getAnnee());
							newPrime.setDatFin(Const.ZERO);
							newPrime.setRefArr(avct.getNumArrete());
							newPrime.setDateArrete(sdf.format(avct.getDateArrete()));
							newPrime.setNoRubr("1200");
							// RG_AG_PR_A04
							HistoPrime histo2 = new HistoPrime(newPrime);
							getHistoPrimeDao().creerHistoPrime(histo2, user, EnumTypeHisto.CREATION);
							newPrime.creerPrime(getTransaction(), user);
						}
					} else {
						getTransaction().traiterErreur();

						Prime newPrime = new Prime();
						newPrime.setNoMatr(agent.getNomatr().toString());
						newPrime.setMtPri("3");
						newPrime.setDatDeb("01/01/" + avct.getAnnee());
						newPrime.setDatFin(Const.ZERO);
						newPrime.setRefArr(avct.getNumArrete());
						newPrime.setDateArrete(sdf.format(avct.getDateArrete()));
						newPrime.setNoRubr("1200");
						// RG_AG_PR_A04
						HistoPrime histo2 = new HistoPrime(newPrime);
						getHistoPrimeDao().creerHistoPrime(histo2, user, EnumTypeHisto.CREATION);
						newPrime.creerPrime(getTransaction(), user);
					}

					if (getTransaction().isErreur()) {
						return false;
					} else {
						nbAgentAffectes += 1;
					}
				}
			}
		}
		// on valide les modifis
		commitTransaction();
		// on remet à vide pour réinitialiser l'affichage
		setListeAvct(new ArrayList<AvancementConvCol>());
		// "INF201","@ agents ont été affectés."
		setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("INF201", String.valueOf(nbAgentAffectes)));
		return true;
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
		// on sauvegarde l'état du tableau
		for (int j = 0; j < getListeAvct().size(); j++) {
			// on recupère la ligne concernée
			AvancementConvCol avct = (AvancementConvCol) getListeAvct().get(j);
			Integer i = avct.getIdAvct();

			// on fait les modifications
			if (!avct.getEtat().equals(EnumEtatAvancement.AFFECTE.getValue())) {
				// on traite l'etat
				if (getVAL_CK_AFFECTER(i).equals(getCHECKED_ON())) {
					avct.setEtat(EnumEtatAvancement.VALIDE.getValue());
				} else if (getVAL_CK_PROJET_ARRETE(i).equals(getCHECKED_ON())) {
					avct.setEtat(EnumEtatAvancement.SEF.getValue());
				} else if (getVAL_CK_VALID_DRH(i).equals(getCHECKED_ON())) {
					avct.setEtat(EnumEtatAvancement.SGC.getValue());
				} else {
					avct.setEtat(EnumEtatAvancement.TRAVAIL.getValue());
				}
				// on traite le numero et la date d'arreté
				avct.setDateArrete(getVAL_EF_DATE_ARRETE(i).equals(Const.CHAINE_VIDE) ? null : sdf
						.parse(getVAL_EF_DATE_ARRETE(i)));
				avct.setNumArrete(getVAL_EF_NUM_ARRETE(i));
			}
			getAvancementConvColDao().modifierAvancementConvCol(avct.getIdAvct(), avct.getIdAgent(), avct.getAnnee(),
					avct.getEtat(), avct.getNumArrete(), avct.getDateArrete(), avct.getDateEmbauche(), avct.getGrade(),
					avct.getLibGrade(), avct.getDirectionService(), avct.getSectionService(), avct.getCarriereSimu(),
					avct.getMontantPrime1200(), avct.getCodePa());
			if (getTransaction().isErreur())
				return false;
		}
		// on enregistre
		commitTransaction();

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
	 * Retourne pour la JSP le nom de la zone statique : ST_MOTIF_AVCT Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_MOTIF_AVCT(int i) {
		return "NOM_ST_MOTIF_AVCT_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MOTIF_AVCT
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_MOTIF_AVCT(int i) {
		return getZone(getNOM_ST_MOTIF_AVCT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_EMBAUCHE Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_DATE_EMBAUCHE(int i) {
		return "NOM_ST_DATE_EMBAUCHE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_EMBAUCHE
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_DATE_EMBAUCHE(int i) {
		return getZone(getNOM_ST_DATE_EMBAUCHE(i));
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
	 * Retourne pour la JSP le nom de la zone statique : ST_GRADE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_GRADE_LIB(int i) {
		return "NOM_ST_GRADE_LIB_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_GRADE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_GRADE_LIB(int i) {
		return getZone(getNOM_ST_GRADE_LIB(i));
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_ARRETE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_EF_DATE_ARRETE(int i) {
		return "NOM_EF_DATE_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_EF_DATE_ARRETE(int i) {
		return getZone(getNOM_EF_DATE_ARRETE(i));
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NUM_ARRETE Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_EF_NUM_ARRETE(int i) {
		return "NOM_EF_NUM_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_NUM_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_EF_NUM_ARRETE(int i) {
		return getZone(getNOM_EF_NUM_ARRETE(i));
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
	 * Retourne pour la JSP le nom de la zone statique : ST_MONTANT_PRIME Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_MONTANT_PRIME(int i) {
		return "NOM_ST_MONTANT_PRIME_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_MONTANT_PRIME
	 * Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_MONTANT_PRIME(int i) {
		return getZone(getNOM_ST_MONTANT_PRIME(i));
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
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_PROJET_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_PROJET_ARRETE(int i) {
		return "NOM_CK_PROJET_ARRETE_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * à cocher : CK_PROJET_ARRETE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_PROJET_ARRETE(int i) {
		return getZone(getNOM_CK_PROJET_ARRETE(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_VALID_DRH Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_VALID_DRH(int i) {
		return "NOM_CK_VALID_DRH_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * à cocher : CK_VALID_DRH Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_VALID_DRH(int i) {
		return getZone(getNOM_CK_VALID_DRH(i));
	}

	/**
	 * Getter de la liste des avancements des fonctionnaires.
	 * 
	 * @return listeAvct
	 */
	public ArrayList<AvancementConvCol> getListeAvct() {
		return listeAvct == null ? new ArrayList<AvancementConvCol>() : listeAvct;
	}

	/**
	 * Setter de la liste des avancements des fonctionnaires.
	 * 
	 * @param listeAvct
	 */
	private void setListeAvct(ArrayList<AvancementConvCol> listeAvct) {
		this.listeAvct = listeAvct;
	}

	/**
	 * Getter du nom de l'écran (pour la gestion des droits)
	 */
	public String getNomEcran() {
		return "ECR-AVCT-CONVCOL";
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

	public String getNOM_ST_PA(int i) {
		return "NOM_ST_PA_" + i;
	}

	public String getVAL_ST_PA(int i) {
		return getZone(getNOM_ST_PA(i));
	}

	public String getNOM_ST_MATRICULE(int i) {
		return "NOM_ST_MATRICULE_" + i;
	}

	public String getVAL_ST_MATRICULE(int i) {
		return getZone(getNOM_ST_MATRICULE(i));
	}

	public AvancementConvColDao getAvancementConvColDao() {
		return avancementConvColDao;
	}

	public void setAvancementConvColDao(AvancementConvColDao avancementConvColDao) {
		this.avancementConvColDao = avancementConvColDao;
	}

	public HistoPrimeDao getHistoPrimeDao() {
		return histoPrimeDao;
	}

	public void setHistoPrimeDao(HistoPrimeDao histoPrimeDao) {
		this.histoPrimeDao = histoPrimeDao;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

}