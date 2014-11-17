package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.metier.carriere.Carriere;
import nc.mairie.metier.parametrage.MotifCarriere;
import nc.mairie.metier.parametrage.SPBASE;
import nc.mairie.spring.dao.metier.parametrage.MotifCarriereDao;
import nc.mairie.spring.dao.metier.parametrage.SPBASEDao;
import nc.mairie.spring.dao.utils.MairieDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

/**
 * Process OePARAMETRAGERecrutement Date de création : (14/09/11 13:52:54)
 * 
 */
public class OePARAMETRAGECarriere extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] LB_SPBASE;
	private String[] LB_MOTIF;

	private SPBASEDao spbaseDao;
	private ArrayList<SPBASE> listeSpbase;
	private SPBASE spbaseCourant;

	private ArrayList<MotifCarriere> listeMotif;
	private MotifCarriere motifCourant;
	private MotifCarriereDao motifCarriereDao;

	public String ACTION_CREATION = "1";
	public String ACTION_MODIFICATION = "2";
	public String ACTION_SUPPRESSION = "0";

	public String focus = null;

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (14/09/11 13:52:54)
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
		if (getListeSpbase() == null || getListeSpbase().size() == 0) {
			initialiseListeSpbase(request);
		}
		if (getListeMotif().size() == 0) {
			initialiseListeMotif(request);
		}

	}

	/**
	 * Initialisation de la listes des motifs de carriere Date de création :
	 * (14/09/11)
	 * 
	 */
	private void initialiseListeMotif(HttpServletRequest request) throws Exception {
		setListeMotif(getMotifCarriereDao().listerMotifCarriere());
		if (getListeMotif().size() != 0) {
			int tailles[] = { 100 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<MotifCarriere> list = getListeMotif().listIterator(); list.hasNext();) {
				MotifCarriere ma = (MotifCarriere) list.next();
				String ligne[] = { ma.getLibMotifCarriere() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_MOTIF(aFormat.getListeFormatee());
		} else {
			setLB_MOTIF(null);
		}
	}

	private void initialiseListeSpbase(HttpServletRequest request) throws Exception {
		setListeSpbase(getSpbaseDao().listerSPBASE());
		afficheListeSpbase(request);
	}

	private void afficheListeSpbase(HttpServletRequest request) {
		if (getListeSpbase() != null && getListeSpbase().size() != 0) {
			int tailles[] = { 6, 30 };
			String padding[] = { "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<SPBASE> list = getListeSpbase().listIterator(); list.hasNext();) {
				SPBASE base = (SPBASE) list.next();
				String ligne[] = { base.getCdBase(), base.getLiBase() };

				aFormat.ajouteLigne(ligne);
			}
			setLB_SPBASE(aFormat.getListeFormatee());
		} else {
			setLB_SPBASE(null);
		}

	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getSpbaseDao() == null) {
			setSpbaseDao(new SPBASEDao((MairieDao) context.getBean("mairieDao")));
		}
		if (getMotifCarriereDao() == null) {
			setMotifCarriereDao(new MotifCarriereDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * Constructeur du process OePARAMETRAGEAvancement. Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public OePARAMETRAGECarriere() {
		super();
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_CREER_SPBASE
			if (testerParametre(request, getNOM_PB_CREER_SPBASE())) {
				return performPB_CREER_SPBASE(request);
			}
			// Si clic sur le bouton PB_MODIFIER_SPBASE
			if (testerParametre(request, getNOM_PB_MODIFIER_SPBASE())) {
				return performPB_MODIFIER_SPBASE(request);
			}
			// Si clic sur le bouton PB_ANNULER_SPBASE
			if (testerParametre(request, getNOM_PB_ANNULER_SPBASE())) {
				return performPB_ANNULER_SPBASE(request);
			}
			// Si clic sur le bouton PB_VALIDER_SPBASE
			if (testerParametre(request, getNOM_PB_VALIDER_SPBASE())) {
				return performPB_VALIDER_SPBASE(request);
			}

			// Si clic sur le bouton PB_ANNULER_MOTIF
			if (testerParametre(request, getNOM_PB_ANNULER_MOTIF())) {
				return performPB_ANNULER_MOTIF(request);
			}

			// Si clic sur le bouton PB_CREER_MOTIF
			if (testerParametre(request, getNOM_PB_CREER_MOTIF())) {
				return performPB_CREER_MOTIF(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_MOTIF
			if (testerParametre(request, getNOM_PB_SUPPRIMER_MOTIF())) {
				return performPB_SUPPRIMER_MOTIF(request);
			}

			// Si clic sur le bouton PB_VALIDER_MOTIF
			if (testerParametre(request, getNOM_PB_VALIDER_MOTIF())) {
				return performPB_VALIDER_MOTIF(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (14/09/11 15:20:21)
	 * 
	 */
	public String getJSP() {
		return "OePARAMETRAGECarriere.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-CARRIERE";
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
		return getNOM_PB_ANNULER_SPBASE();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	public SPBASEDao getSpbaseDao() {
		return spbaseDao;
	}

	public void setSpbaseDao(SPBASEDao spbaseDao) {
		this.spbaseDao = spbaseDao;
	}

	public ArrayList<SPBASE> getListeSpbase() {
		return listeSpbase;
	}

	public void setListeSpbase(ArrayList<SPBASE> listeSpbase) {
		this.listeSpbase = listeSpbase;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_SPBASE Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	private String[] getLB_SPBASE() {
		if (LB_SPBASE == null)
			LB_SPBASE = initialiseLazyLB();
		return LB_SPBASE;
	}

	/**
	 * Setter de la liste: LB_SPBASE Date de création : (14/09/11 13:52:54)
	 * 
	 */
	private void setLB_SPBASE(String[] newLB_SPBASE) {
		LB_SPBASE = newLB_SPBASE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_SPBASE Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_SPBASE() {
		return "NOM_LB_SPBASE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_SPBASE_SELECT Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_SPBASE_SELECT() {
		return "NOM_LB_SPBASE_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_SPBASE Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_SPBASE() {
		return getLB_SPBASE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_SPBASE Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_LB_SPBASE_SELECT() {
		return getZone(getNOM_LB_SPBASE_SELECT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_SPBASE Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_CREER_SPBASE() {
		return "NOM_PB_CREER_SPBASE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_CREER_SPBASE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_SPBASE(), ACTION_CREATION);
		addZone(getNOM_EF_CODE_SPBASE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_LIB_SPBASE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_BASE_HEBDO_LEG_H_SPBASE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_BASE_HEBDO_LEG_M_SPBASE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_BASE_HEBDO_H_SPBASE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_BASE_HEBDO_M_SPBASE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_HEURE_LUNDI(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_HEURE_MARDI(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_HEURE_MERCREDI(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_HEURE_JEUDI(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_HEURE_VENDREDI(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_HEURE_SAMEDI(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_HEURE_DIMANCHE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_SPBASE());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_SPBASE Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_MODIFIER_SPBASE() {
		return "NOM_PB_MODIFIER_SPBASE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_MODIFIER_SPBASE(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_SPBASE_SELECT()) ? Integer.parseInt(getVAL_LB_SPBASE_SELECT())
				: -1);
		if (indice != -1 && indice < getListeSpbase().size()) {
			SPBASE base = getListeSpbase().get(indice);
			setSpbaseCourant(base);
			addZone(getNOM_EF_LIB_SPBASE(), base.getLiBase());
			addZone(getNOM_EF_CODE_SPBASE(), base.getCdBase());
			addZone(getNOM_EF_HEURE_LUNDI(), base.getNbhLu().toString());
			addZone(getNOM_EF_HEURE_MARDI(), base.getNbhMa().toString());
			addZone(getNOM_EF_HEURE_MERCREDI(), base.getNbhMe().toString());
			addZone(getNOM_EF_HEURE_JEUDI(), base.getNbhJe().toString());
			addZone(getNOM_EF_HEURE_VENDREDI(), base.getNbhVe().toString());
			addZone(getNOM_EF_HEURE_SAMEDI(), base.getNbhSa().toString());
			addZone(getNOM_EF_HEURE_DIMANCHE(), base.getNbhDi().toString());

			if (base.getNbasCH() != 0) {
				String avantPoint = base.getNbasCH().toString().substring(0, base.getNbasCH().toString().indexOf("."));
				String apresPoint = base.getNbasCH().toString()
						.substring(base.getNbasCH().toString().indexOf(".") + 1, base.getNbasCH().toString().length());

				addZone(getNOM_EF_BASE_HEBDO_LEG_H_SPBASE(), avantPoint.equals("0") ? Const.CHAINE_VIDE : avantPoint);
				addZone(getNOM_EF_BASE_HEBDO_LEG_M_SPBASE(),
						apresPoint.equals("0") ? Const.CHAINE_VIDE : apresPoint.length() == 1 ? apresPoint + "0"
								: apresPoint);
			} else {
				addZone(getNOM_EF_BASE_HEBDO_LEG_H_SPBASE(), Const.CHAINE_VIDE);
				addZone(getNOM_EF_BASE_HEBDO_LEG_M_SPBASE(), Const.CHAINE_VIDE);

			}

			if (base.getNbasHH() != 0) {
				String avantPoint = base.getNbasHH().toString().substring(0, base.getNbasHH().toString().indexOf("."));
				String apresPoint = base.getNbasHH().toString()
						.substring(base.getNbasHH().toString().indexOf(".") + 1, base.getNbasHH().toString().length());

				addZone(getNOM_EF_BASE_HEBDO_H_SPBASE(), avantPoint.equals("0") ? Const.CHAINE_VIDE : avantPoint);
				addZone(getNOM_EF_BASE_HEBDO_M_SPBASE(),
						apresPoint.equals("0") ? Const.CHAINE_VIDE : apresPoint.length() == 1 ? apresPoint + "0"
								: apresPoint);
			} else {
				addZone(getNOM_EF_BASE_HEBDO_H_SPBASE(), Const.CHAINE_VIDE);
				addZone(getNOM_EF_BASE_HEBDO_M_SPBASE(), Const.CHAINE_VIDE);

			}

			addZone(getNOM_ST_ACTION_SPBASE(), ACTION_MODIFICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "bases horaires"));
		}

		setFocus(getNOM_PB_ANNULER_SPBASE());
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_SPBASE Date
	 * de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_ST_ACTION_SPBASE() {
		return "NOM_ST_ACTION_SPBASE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_SPBASE
	 * Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_ST_ACTION_SPBASE() {
		return getZone(getNOM_ST_ACTION_SPBASE());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_SPBASE Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_ANNULER_SPBASE() {
		return "NOM_PB_ANNULER_SPBASE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_ANNULER_SPBASE(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_SPBASE(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_SPBASE());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_SPBASE Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_VALIDER_SPBASE() {
		return "NOM_PB_VALIDER_SPBASE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_VALIDER_SPBASE(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieSPBASE(request))
			return false;

		if (!performControlerRegleGestionSPBASE(request))
			return false;

		if (getVAL_ST_ACTION_SPBASE() != null && getVAL_ST_ACTION_SPBASE() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_SPBASE().equals(ACTION_CREATION)) {
				setSpbaseCourant(new SPBASE());
				getSpbaseCourant().setCdBase(getVAL_EF_CODE_SPBASE());
				getSpbaseCourant().setLiBase(getVAL_EF_LIB_SPBASE());
				getSpbaseCourant().setNbhLu(Double.valueOf(getVAL_EF_HEURE_LUNDI()));
				getSpbaseCourant().setNbhMa(Double.valueOf(getVAL_EF_HEURE_MARDI()));
				getSpbaseCourant().setNbhMe(Double.valueOf(getVAL_EF_HEURE_MERCREDI()));
				getSpbaseCourant().setNbhJe(Double.valueOf(getVAL_EF_HEURE_JEUDI()));
				getSpbaseCourant().setNbhVe(Double.valueOf(getVAL_EF_HEURE_VENDREDI()));
				getSpbaseCourant().setNbhSa(Double.valueOf(getVAL_EF_HEURE_SAMEDI()));
				getSpbaseCourant().setNbhDi(Double.valueOf(getVAL_EF_HEURE_DIMANCHE()));

				String heureBaseLegale = getVAL_EF_BASE_HEBDO_LEG_H_SPBASE().equals(Const.CHAINE_VIDE) ? "0"
						: getVAL_EF_BASE_HEBDO_LEG_H_SPBASE();
				String minuteBaseLegale = getVAL_EF_BASE_HEBDO_LEG_M_SPBASE().equals(Const.CHAINE_VIDE) ? "0"
						: getVAL_EF_BASE_HEBDO_LEG_M_SPBASE();
				String totalBaseLegale = heureBaseLegale + ":" + minuteBaseLegale;
				getSpbaseCourant().setNbasCH(getHeureDouble(totalBaseLegale));

				// on fait le calcul de la base legale
				Double nbHeureCalc = transformeHeure(getSpbaseCourant().getNbhLu())
						+ transformeHeure(getSpbaseCourant().getNbhMa())
						+ transformeHeure(getSpbaseCourant().getNbhMe())
						+ transformeHeure(getSpbaseCourant().getNbhJe())
						+ transformeHeure(getSpbaseCourant().getNbhVe())
						+ transformeHeure(getSpbaseCourant().getNbhSa())
						+ transformeHeure(getSpbaseCourant().getNbhDi());
				getSpbaseCourant().setNbasHH(transformeResultatHeure(nbHeureCalc));

				getSpbaseDao().creerSPBASE(getSpbaseCourant().getCdBase().toUpperCase(),
						getSpbaseCourant().getLiBase().toUpperCase(), getSpbaseCourant().getNbhLu(),
						getSpbaseCourant().getNbhMa(), getSpbaseCourant().getNbhMe(), getSpbaseCourant().getNbhJe(),
						getSpbaseCourant().getNbhVe(), getSpbaseCourant().getNbhSa(), getSpbaseCourant().getNbhDi(),
						getSpbaseCourant().getNbasCH(), getSpbaseCourant().getNbasHH());

			} else if (getVAL_ST_ACTION_SPBASE().equals(ACTION_MODIFICATION)) {
				getSpbaseCourant().setLiBase(getVAL_EF_LIB_SPBASE());
				getSpbaseCourant().setNbhLu(Double.valueOf(getVAL_EF_HEURE_LUNDI()));
				getSpbaseCourant().setNbhMa(Double.valueOf(getVAL_EF_HEURE_MARDI()));
				getSpbaseCourant().setNbhMe(Double.valueOf(getVAL_EF_HEURE_MERCREDI()));
				getSpbaseCourant().setNbhJe(Double.valueOf(getVAL_EF_HEURE_JEUDI()));
				getSpbaseCourant().setNbhVe(Double.valueOf(getVAL_EF_HEURE_VENDREDI()));
				getSpbaseCourant().setNbhSa(Double.valueOf(getVAL_EF_HEURE_SAMEDI()));
				getSpbaseCourant().setNbhDi(Double.valueOf(getVAL_EF_HEURE_DIMANCHE()));

				String heureBaseLegale = getVAL_EF_BASE_HEBDO_LEG_H_SPBASE().equals(Const.CHAINE_VIDE) ? "0"
						: getVAL_EF_BASE_HEBDO_LEG_H_SPBASE();
				String minuteBaseLegale = getVAL_EF_BASE_HEBDO_LEG_M_SPBASE().equals(Const.CHAINE_VIDE) ? "0"
						: getVAL_EF_BASE_HEBDO_LEG_M_SPBASE();
				String totalBaseLegale = heureBaseLegale + ":" + minuteBaseLegale;
				getSpbaseCourant().setNbasCH(getHeureDouble(totalBaseLegale));

				// on fait le calcul de la base legale
				Double nbHeureCalc = transformeHeure(getSpbaseCourant().getNbhLu())
						+ transformeHeure(getSpbaseCourant().getNbhMa())
						+ transformeHeure(getSpbaseCourant().getNbhMe())
						+ transformeHeure(getSpbaseCourant().getNbhJe())
						+ transformeHeure(getSpbaseCourant().getNbhVe())
						+ transformeHeure(getSpbaseCourant().getNbhSa())
						+ transformeHeure(getSpbaseCourant().getNbhDi());
				getSpbaseCourant().setNbasHH(transformeResultatHeure(nbHeureCalc));

				getSpbaseDao().modifierSPBASE(getSpbaseCourant().getCdBase().toUpperCase(),
						getSpbaseCourant().getLiBase().toUpperCase(), getSpbaseCourant().getNbhLu(),
						getSpbaseCourant().getNbhMa(), getSpbaseCourant().getNbhMe(), getSpbaseCourant().getNbhJe(),
						getSpbaseCourant().getNbhVe(), getSpbaseCourant().getNbhSa(), getSpbaseCourant().getNbhDi(),
						getSpbaseCourant().getNbasCH(), getSpbaseCourant().getNbasHH());
				setSpbaseCourant(null);
			}

			initialiseListeSpbase(request);
			addZone(getNOM_ST_ACTION_SPBASE(), Const.CHAINE_VIDE);
		}

		setFocus(getNOM_PB_ANNULER_SPBASE());
		return true;
	}

	private Double transformeResultatHeure(Double nbHeureCalc) {
		String nbHeure = nbHeureCalc.toString();
		String partieEntiere = nbHeure.substring(0, nbHeure.indexOf("."));
		String partieDecimale = nbHeure.substring(nbHeure.indexOf(".") + 1, nbHeure.length());
		if (partieDecimale.equals("25")) {
			partieDecimale = "15";
		} else if (partieDecimale.equals("5")) {
			partieDecimale = "3";
		} else if (partieDecimale.equals("75")) {
			partieDecimale = "45";
		}
		return Double.valueOf(partieEntiere + "." + partieDecimale);
	}

	private Double transformeHeure(Double nbheure) {
		// on transforme les 7.48 en heure
		String nbHeure = nbheure.toString();
		String partieEntiere = nbHeure.substring(0, nbHeure.indexOf("."));
		String partieDecimale = nbHeure.substring(nbHeure.indexOf(".") + 1, nbHeure.length());
		if (partieDecimale.length() == 1) {
			partieDecimale = partieDecimale + "0";
		}
		Integer res = (Integer.valueOf(partieEntiere) * 60) + Integer.valueOf(partieDecimale);
		return Double.valueOf(res) / 60;
	}

	private Double getHeureDouble(String heure) {
		Double res = Double.valueOf(heure.replace(":", "."));

		return res;
	}

	private boolean performControlerRegleGestionSPBASE(HttpServletRequest request) {
		// Vérification des contraintes d'unicité de la base horaire
		if (getVAL_ST_ACTION_SPBASE().equals(ACTION_CREATION)) {

			for (SPBASE motif : getListeSpbase()) {
				if (motif.getLiBase().trim().equals(getVAL_EF_LIB_SPBASE().toUpperCase().trim())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction()
							.declarerErreur(MessageUtils.getMessage("ERR974", "une base horaire", "ce libellé"));
					return false;
				}
				if (motif.getCdBase().trim().equals(getVAL_EF_CODE_SPBASE().toUpperCase().trim())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une base horaire", "ce code"));
					return false;
				}
			}
		}
		return true;
	}

	private boolean performControlerSaisieSPBASE(HttpServletRequest request) {
		// Verification libellé not null
		if (getZone(getNOM_EF_LIB_SPBASE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}
		// Verification code not null
		if (getZone(getNOM_EF_CODE_SPBASE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code"));
			return false;
		}
		// Verification nb heure legale not null
		if (getZone(getNOM_EF_BASE_HEBDO_LEG_H_SPBASE()).length() == 0
				&& getZone(getNOM_EF_BASE_HEBDO_LEG_M_SPBASE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "base légale hebdomadaire"));
			return false;
		}
		// Verification nb heure legale numerique
		if (getZone(getNOM_EF_BASE_HEBDO_LEG_H_SPBASE()).length() != 0
				&& !Services.estNumerique(getZone(getNOM_EF_BASE_HEBDO_LEG_H_SPBASE()))) {
			// "ERR992", "La zone @ doit être numérique.";
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "heures légale"));
			return false;
		}
		// Verification nb heure legale numerique
		if (getZone(getNOM_EF_BASE_HEBDO_LEG_M_SPBASE()).length() != 0
				&& !Services.estNumerique(getZone(getNOM_EF_BASE_HEBDO_LEG_M_SPBASE()))) {
			// "ERR992", "La zone @ doit être numérique.";
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR992", "minutes légale"));
			return false;
		}

		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIB_SPBASE Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_LIB_SPBASE() {
		return "NOM_EF_LIB_SPBASE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_LIB_SPBASE Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_LIB_SPBASE() {
		return getZone(getNOM_EF_LIB_SPBASE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_SPBASE Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_CODE_SPBASE() {
		return "NOM_EF_CODE_SPBASE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_CODE_SPBASE Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_CODE_SPBASE() {
		return getZone(getNOM_EF_CODE_SPBASE());
	}

	public SPBASE getSpbaseCourant() {
		return spbaseCourant;
	}

	public void setSpbaseCourant(SPBASE spbaseCourant) {
		this.spbaseCourant = spbaseCourant;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_BASE_HEBDO_LEG_H_SPBASE Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_BASE_HEBDO_LEG_H_SPBASE() {
		return "NOM_EF_BASE_HEBDO_LEG_H_SPBASE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_BASE_HEBDO_LEG_H_SPBASE Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_BASE_HEBDO_LEG_H_SPBASE() {
		return getZone(getNOM_EF_BASE_HEBDO_LEG_H_SPBASE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_BASE_HEBDO_LEG_M_SPBASE Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_BASE_HEBDO_LEG_M_SPBASE() {
		return "NOM_EF_BASE_HEBDO_LEG_M_SPBASE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_BASE_HEBDO_LEG_M_SPBASE Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_BASE_HEBDO_LEG_M_SPBASE() {
		return getZone(getNOM_EF_BASE_HEBDO_LEG_M_SPBASE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_BASE_HEBDO_H_SPBASE
	 * Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_BASE_HEBDO_H_SPBASE() {
		return "NOM_EF_BASE_HEBDO_H_SPBASE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_BASE_HEBDO_H_SPBASE Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_BASE_HEBDO_H_SPBASE() {
		return getZone(getNOM_EF_BASE_HEBDO_H_SPBASE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_BASE_HEBDO_M_SPBASE
	 * Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_BASE_HEBDO_M_SPBASE() {
		return "NOM_EF_BASE_HEBDO_M_SPBASE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_BASE_HEBDO_M_SPBASE Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_BASE_HEBDO_M_SPBASE() {
		return getZone(getNOM_EF_BASE_HEBDO_M_SPBASE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_MOTIF Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_ST_ACTION_MOTIF() {
		return "NOM_ST_ACTION_MOTIF";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION_MOTIF
	 * Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_ST_ACTION_MOTIF() {
		return getZone(getNOM_ST_ACTION_MOTIF());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIB_MOTIF Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_LIB_MOTIF() {
		return "NOM_EF_LIB_MOTIF";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_LIB_MOTIF Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_LIB_MOTIF() {
		return getZone(getNOM_EF_LIB_MOTIF());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_MOTIF Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	private String[] getLB_MOTIF() {
		if (LB_MOTIF == null)
			LB_MOTIF = initialiseLazyLB();
		return LB_MOTIF;
	}

	/**
	 * Setter de la liste: LB_MOTIF Date de création : (14/09/11 13:52:54)
	 * 
	 */
	private void setLB_MOTIF(String[] newLB_MOTIF) {
		LB_MOTIF = newLB_MOTIF;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_MOTIF Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_MOTIF() {
		return "NOM_LB_MOTIF";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_MOTIF_SELECT Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_MOTIF_SELECT() {
		return "NOM_LB_MOTIF_SELECT";
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_MOTIF Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_MOTIF() {
		return getLB_MOTIF();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_MOTIF Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_LB_MOTIF_SELECT() {
		return getZone(getNOM_LB_MOTIF_SELECT());
	}

	private ArrayList<MotifCarriere> getListeMotif() {
		if (listeMotif == null)
			return new ArrayList<MotifCarriere>();
		return listeMotif;
	}

	private void setListeMotif(ArrayList<MotifCarriere> listeMotif) {
		this.listeMotif = listeMotif;
	}

	private MotifCarriere getMotifCourant() {
		return motifCourant;
	}

	private void setMotifCourant(MotifCarriere motifCourant) {
		this.motifCourant = motifCourant;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_MOTIF Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_ANNULER_MOTIF() {
		return "NOM_PB_ANNULER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_ANNULER_MOTIF(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION_MOTIF(), Const.CHAINE_VIDE);
		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_MOTIF());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_MOTIF Date de création
	 * : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_CREER_MOTIF() {
		return "NOM_PB_CREER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_CREER_MOTIF(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_MOTIF(), ACTION_CREATION);
		addZone(getNOM_EF_LIB_MOTIF(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_MOTIF());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_MOTIF Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_MOTIF() {
		return "NOM_PB_SUPPRIMER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_SUPPRIMER_MOTIF(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_MOTIF_SELECT()) ? Integer.parseInt(getVAL_LB_MOTIF_SELECT()) : -1);
		if (indice != -1 && indice < getListeMotif().size()) {
			MotifCarriere ma = getListeMotif().get(indice);
			setMotifCourant(ma);
			addZone(getNOM_EF_LIB_MOTIF(), ma.getLibMotifCarriere());
			addZone(getNOM_ST_ACTION_MOTIF(), ACTION_SUPPRESSION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "motifs de carrière"));
		}

		setFocus(getNOM_PB_ANNULER_MOTIF());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER_MOTIF Date de
	 * création : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_VALIDER_MOTIF() {
		return "NOM_PB_VALIDER_MOTIF";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_VALIDER_MOTIF(HttpServletRequest request) throws Exception {
		if (!performControlerSaisieMotif(request))
			return false;

		if (!performControlerRegleGestionMotif(request))
			return false;

		if (getVAL_ST_ACTION_MOTIF() != null && getVAL_ST_ACTION_MOTIF() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_MOTIF().equals(ACTION_CREATION)) {
				setMotifCourant(new MotifCarriere());
				getMotifCourant().setLibMotifCarriere(getVAL_EF_LIB_MOTIF());
				getMotifCarriereDao().creerMotifCarriere(getMotifCourant().getLibMotifCarriere());
				if (!getTransaction().isErreur())
					getListeMotif().add(getMotifCourant());
			} else if (getVAL_ST_ACTION_MOTIF().equals(ACTION_SUPPRESSION)) {
				getMotifCarriereDao().supprimerMotifCarriere(getMotifCourant().getIdMotifCarriere());
				if (!getTransaction().isErreur())
					getListeMotif().remove(getMotifCourant());
				setMotifCourant(null);
			}

			if (getTransaction().isErreur())
				return false;

			commitTransaction();
			initialiseListeMotif(request);
			addZone(getNOM_ST_ACTION_MOTIF(), Const.CHAINE_VIDE);
		}

		setFocus(getNOM_PB_ANNULER_MOTIF());
		return true;
	}

	private boolean performControlerSaisieMotif(HttpServletRequest request) throws Exception {
		// Verification libellé motif not null
		if (getZone(getNOM_EF_LIB_MOTIF()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			return false;
		}
		return true;
	}

	private boolean performControlerRegleGestionMotif(HttpServletRequest request) throws Exception {
		// Verification si suppression d'un motif de carriere utilisée sur une
		// carriere
		if (getVAL_ST_ACTION_MOTIF().equals(ACTION_SUPPRESSION)
				&& Carriere.listerCarriereAvecMotif(getTransaction(), getMotifCourant()).size() > 0) {
			// "ERR989",
			// "Suppression impossible. Il existe au moins @ rattaché à @."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR989", "une carrière", "ce motif de carrière"));
			return false;
		}

		// Vérification des contraintes d'unicité du motif de carriere
		if (getVAL_ST_ACTION_MOTIF().equals(ACTION_CREATION)) {

			for (MotifCarriere motif : getListeMotif()) {
				if (motif.getLibMotifCarriere().toUpperCase().equals(getVAL_EF_LIB_MOTIF().toUpperCase())) {
					// "ERR974",
					// "Attention, il existe déjà @ avec @. Veuillez contrôler."
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR974", "un motif de carrière", "ce libellé"));
					return false;
				}
			}
		}

		return true;
	}

	public MotifCarriereDao getMotifCarriereDao() {
		return motifCarriereDao;
	}

	public void setMotifCarriereDao(MotifCarriereDao motifCarriereDao) {
		this.motifCarriereDao = motifCarriereDao;
	}

	public String getNOM_EF_HEURE_LUNDI() {
		return "NOM_EF_HEURE_LUNDI";
	}

	public String getVAL_EF_HEURE_LUNDI() {
		return getZone(getNOM_EF_HEURE_LUNDI());
	}

	public String getNOM_EF_HEURE_MARDI() {
		return "NOM_EF_HEURE_MARDI";
	}

	public String getVAL_EF_HEURE_MARDI() {
		return getZone(getNOM_EF_HEURE_MARDI());
	}

	public String getNOM_EF_HEURE_MERCREDI() {
		return "NOM_EF_HEURE_MERCREDI";
	}

	public String getVAL_EF_HEURE_MERCREDI() {
		return getZone(getNOM_EF_HEURE_MERCREDI());
	}

	public String getNOM_EF_HEURE_JEUDI() {
		return "NOM_EF_HEURE_JEUDI";
	}

	public String getVAL_EF_HEURE_JEUDI() {
		return getZone(getNOM_EF_HEURE_JEUDI());
	}

	public String getNOM_EF_HEURE_VENDREDI() {
		return "NOM_EF_HEURE_VENDREDI";
	}

	public String getVAL_EF_HEURE_VENDREDI() {
		return getZone(getNOM_EF_HEURE_VENDREDI());
	}

	public String getNOM_EF_HEURE_SAMEDI() {
		return "NOM_EF_HEURE_SAMEDI";
	}

	public String getVAL_EF_HEURE_SAMEDI() {
		return getZone(getNOM_EF_HEURE_SAMEDI());
	}

	public String getNOM_EF_HEURE_DIMANCHE() {
		return "NOM_EF_HEURE_DIMANCHE";
	}

	public String getVAL_EF_HEURE_DIMANCHE() {
		return getZone(getNOM_EF_HEURE_DIMANCHE());
	}
}
