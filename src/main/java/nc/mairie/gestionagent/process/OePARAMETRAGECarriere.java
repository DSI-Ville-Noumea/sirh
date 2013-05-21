package nc.mairie.gestionagent.process;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.spring.dao.metier.parametrage.SPBASEDao;
import nc.mairie.spring.domain.metier.parametrage.SPBASE;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.springframework.context.ApplicationContext;

/**
 * Process OePARAMETRAGERecrutement Date de cr�ation : (14/09/11 13:52:54)
 * 
 */
public class OePARAMETRAGECarriere extends nc.mairie.technique.BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] LB_SPBASE;
	private String[] LB_HEURE_LUNDI;
	private String[] LB_HEURE_MARDI;
	private String[] LB_HEURE_MERCREDI;
	private String[] LB_HEURE_JEUDI;
	private String[] LB_HEURE_VENDREDI;
	private String[] LB_HEURE_SAMEDI;
	private String[] LB_HEURE_DIMANCHE;

	private SPBASEDao spbaseDao;
	private ArrayList<SPBASE> listeSpbase;
	private SPBASE spbaseCourant;

	private ArrayList<String> listeHeure;

	public String ACTION_CREATION = "1";
	public String ACTION_MODIFICATION = "2";

	public String focus = null;

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		// ----------------------------------//
		// V�rification des droits d'acc�s. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Op�ration impossible. Vous ne disposez pas des droits d'acc�s � cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}
		initialiseDao();
		if (getListeSpbase().size() == 0) {
			initialiseListeSpbase(request);
		}
		if (getListeHeure().size() == 0) {
			initialiseListeHeure(request);
		}

	}

	private void initialiseListeHeure(HttpServletRequest request) {
		setListeHeure(new ArrayList<String>());
		int heureDeb = 3; // heures depart
		int minuteDeb = 0; // minutes debut
		int diffFinDeb = 8 * 60; // diff�rence en minute entre le d�but et
									// la
									// fin
		int interval = 15; // interval en minute

		SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm"); // format
																		// de
																		// la
																		// date

		GregorianCalendar deb = new GregorianCalendar();
		if (heureDeb > 11) // gestion AM PM
			deb.set(GregorianCalendar.AM_PM, GregorianCalendar.PM);
		else
			deb.set(GregorianCalendar.AM_PM, GregorianCalendar.AM);
		deb.set(GregorianCalendar.HOUR, heureDeb % 12);
		deb.set(GregorianCalendar.MINUTE, minuteDeb);

		GregorianCalendar fin = (GregorianCalendar) deb.clone();
		fin.set(GregorianCalendar.MINUTE, diffFinDeb);

		getListeHeure().add(formatDate.format(deb.getTime()));
		Integer i = 1;
		while (deb.compareTo(fin) < 0) {
			deb.add(GregorianCalendar.MINUTE, interval);
			getListeHeure().add(formatDate.format(deb.getTime()));
			i++;
		}
		String[] a = new String[34];
		a[0] = "";
		for (int j = 0; j < getListeHeure().size(); j++) {
			a[j + 1] = getListeHeure().get(j);
		}
		setLB_HEURE_LUNDI(a);
		setLB_HEURE_MARDI(a);
		setLB_HEURE_MERCREDI(a);
		setLB_HEURE_JEUDI(a);
		setLB_HEURE_VENDREDI(a);
		setLB_HEURE_SAMEDI(a);
		setLB_HEURE_DIMANCHE(a);

	}

	private void initialiseListeSpbase(HttpServletRequest request) throws Exception {
		setListeSpbase(getSpbaseDao().listerSPBASE());
		if (getListeSpbase().size() != 0) {
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
			setSpbaseDao((SPBASEDao) context.getBean("spbaseDao"));
		}
	}

	/**
	 * Constructeur du process OePARAMETRAGEAvancement. Date de cr�ation :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public OePARAMETRAGECarriere() {
		super();
	}

	/**
	 * M�thode appel�e par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de cr�ation : (14/09/11 13:52:54)
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

		}
		// Si TAG INPUT non g�r� par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (14/09/11 15:20:21)
	 * 
	 */
	public String getJSP() {
		return "OePARAMETRAGECarriere.jsp";
	}

	/**
	 * Retourne le nom de l'�cran (notamment pour d�terminer les droits
	 * associ�s).
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
	 *            focus � d�finir.
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
		return listeSpbase == null ? new ArrayList<SPBASE>() : listeSpbase;
	}

	public void setListeSpbase(ArrayList<SPBASE> listeSpbase) {
		this.listeSpbase = listeSpbase;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_SPBASE Date de cr�ation :
	 * (14/09/11 13:52:54)
	 * 
	 */
	private String[] getLB_SPBASE() {
		if (LB_SPBASE == null)
			LB_SPBASE = initialiseLazyLB();
		return LB_SPBASE;
	}

	/**
	 * Setter de la liste: LB_SPBASE Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	private void setLB_SPBASE(String[] newLB_SPBASE) {
		LB_SPBASE = newLB_SPBASE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_SPBASE Date de cr�ation :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_SPBASE() {
		return "NOM_LB_SPBASE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_SPBASE_SELECT Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_LB_SPBASE_SELECT() {
		return "NOM_LB_SPBASE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_SPBASE Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String[] getVAL_LB_SPBASE() {
		return getLB_SPBASE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_SPBASE Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_LB_SPBASE_SELECT() {
		return getZone(getNOM_LB_SPBASE_SELECT());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER_SPBASE Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_CREER_SPBASE() {
		return "NOM_PB_CREER_SPBASE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_CREER_SPBASE(HttpServletRequest request) throws Exception {
		// On nomme l'action
		addZone(getNOM_ST_ACTION_SPBASE(), ACTION_CREATION);
		addZone(getNOM_EF_CODE_SPBASE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_LIB_SPBASE(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_HEURE_LUNDI_SELECT(), Const.ZERO);
		addZone(getNOM_LB_HEURE_MARDI_SELECT(), Const.ZERO);
		addZone(getNOM_LB_HEURE_MERCREDI_SELECT(), Const.ZERO);
		addZone(getNOM_LB_HEURE_JEUDI_SELECT(), Const.ZERO);
		addZone(getNOM_LB_HEURE_VENDREDI_SELECT(), Const.ZERO);
		addZone(getNOM_LB_HEURE_SAMEDI_SELECT(), Const.ZERO);
		addZone(getNOM_LB_HEURE_DIMANCHE_SELECT(), Const.ZERO);
		addZone(getNOM_EF_BASE_HEBDO_LEG_H_SPBASE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_BASE_HEBDO_LEG_M_SPBASE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_BASE_HEBDO_SPBASE(), Const.CHAINE_VIDE);

		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_SPBASE());
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_MODIFIER_SPBASE Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_MODIFIER_SPBASE() {
		return "NOM_PB_MODIFIER_SPBASE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public boolean performPB_MODIFIER_SPBASE(HttpServletRequest request) throws Exception {
		int indice = (Services.estNumerique(getVAL_LB_SPBASE_SELECT()) ? Integer.parseInt(getVAL_LB_SPBASE_SELECT()) : -1);
		if (indice != -1 && indice < getListeSpbase().size()) {
			SPBASE base = getListeSpbase().get(indice);
			setSpbaseCourant(base);
			addZone(getNOM_EF_LIB_SPBASE(), base.getLiBase());
			addZone(getNOM_EF_CODE_SPBASE(), base.getCdBase());
			if (base.getNbhLu() != 0) {
				String heureLundi = getStringHeure(base.getNbhLu());
				Integer resHeure = getListeHeure().indexOf(heureLundi) + 1;
				addZone(getNOM_LB_HEURE_LUNDI_SELECT(), resHeure.toString());
			} else {
				addZone(getNOM_LB_HEURE_LUNDI_SELECT(), Const.ZERO);
			}
			if (base.getNbhMa() != 0) {
				String heureMardi = getStringHeure(base.getNbhMa());
				Integer resHeure = getListeHeure().indexOf(heureMardi) + 1;
				addZone(getNOM_LB_HEURE_MARDI_SELECT(), resHeure.toString());
			} else {
				addZone(getNOM_LB_HEURE_MARDI_SELECT(), Const.ZERO);
			}
			if (base.getNbhMe() != 0) {
				String heureMercredi = getStringHeure(base.getNbhMe());
				Integer resHeure = getListeHeure().indexOf(heureMercredi) + 1;
				addZone(getNOM_LB_HEURE_MERCREDI_SELECT(), resHeure.toString());
			} else {
				addZone(getNOM_LB_HEURE_MERCREDI_SELECT(), Const.ZERO);
			}
			if (base.getNbhJe() != 0) {
				String heureJeudi = getStringHeure(base.getNbhJe());
				Integer resHeure = getListeHeure().indexOf(heureJeudi) + 1;
				addZone(getNOM_LB_HEURE_JEUDI_SELECT(), resHeure.toString());
			} else {
				addZone(getNOM_LB_HEURE_JEUDI_SELECT(), Const.ZERO);
			}
			if (base.getNbhVe() != 0) {
				String heureVendredi = getStringHeure(base.getNbhVe());
				Integer resHeure = getListeHeure().indexOf(heureVendredi) + 1;
				addZone(getNOM_LB_HEURE_VENDREDI_SELECT(), resHeure.toString());
			} else {
				addZone(getNOM_LB_HEURE_VENDREDI_SELECT(), Const.ZERO);
			}
			if (base.getNbhSa() != 0) {
				String heureSamedi = getStringHeure(base.getNbhSa());
				Integer resHeure = getListeHeure().indexOf(heureSamedi) + 1;
				addZone(getNOM_LB_HEURE_SAMEDI_SELECT(), resHeure.toString());
			} else {
				addZone(getNOM_LB_HEURE_SAMEDI_SELECT(), Const.ZERO);
			}
			if (base.getNbhDi() != 0) {
				String heureDimanche = getStringHeure(base.getNbhDi());
				Integer resHeure = getListeHeure().indexOf(heureDimanche) + 1;
				addZone(getNOM_LB_HEURE_DIMANCHE_SELECT(), resHeure.toString());
			} else {
				addZone(getNOM_LB_HEURE_DIMANCHE_SELECT(), Const.ZERO);
			}

			if (base.getNbasCH() != 0) {
				String avantPoint = base.getNbasCH().toString().substring(0, base.getNbasCH().toString().indexOf("."));
				String apresPoint = base.getNbasCH().toString()
						.substring(base.getNbasCH().toString().indexOf(".") + 1, base.getNbasCH().toString().length());

				addZone(getNOM_EF_BASE_HEBDO_LEG_H_SPBASE(), avantPoint.equals("0") ? Const.CHAINE_VIDE : avantPoint);
				addZone(getNOM_EF_BASE_HEBDO_LEG_M_SPBASE(), apresPoint.equals("0") ? Const.CHAINE_VIDE : apresPoint);
			} else {
				addZone(getNOM_EF_BASE_HEBDO_LEG_H_SPBASE(), Const.CHAINE_VIDE);
				addZone(getNOM_EF_BASE_HEBDO_LEG_M_SPBASE(), Const.CHAINE_VIDE);

			}

			addZone(getNOM_EF_BASE_HEBDO_SPBASE(), base.getNbasHH().toString());
			addZone(getNOM_ST_ACTION_SPBASE(), ACTION_MODIFICATION);
		} else {
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "bases horaires"));
		}

		setFocus(getNOM_PB_ANNULER_SPBASE());
		return true;
	}

	private String getStringHeure(Double nbh) {
		String res = nbh.toString().replace(".", ":");
		String avantPoint = res.substring(0, res.indexOf(":"));
		String apresPoint = res.substring(res.indexOf(":") + 1, res.length());

		if (avantPoint.length() == 1) {
			res = "0" + res;
		}
		if (apresPoint.length() == 1) {
			res = res + "0";
		}
		return res;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_SPBASE Date
	 * de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_ST_ACTION_SPBASE() {
		return "NOM_ST_ACTION_SPBASE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACTION_SPBASE
	 * Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_ST_ACTION_SPBASE() {
		return getZone(getNOM_ST_ACTION_SPBASE());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER_SPBASE Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_ANNULER_SPBASE() {
		return "NOM_PB_ANNULER_SPBASE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
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
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_PB_VALIDER_SPBASE() {
		return "NOM_PB_VALIDER_SPBASE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (14/09/11 13:52:54)
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

				String heureChoisie = getListeHeure().get(Integer.valueOf(getVAL_LB_HEURE_LUNDI_SELECT()));
				// getSpbaseDao().creerSPBASE(getSpbaseCourant().getCdBase(),
				// getSpbaseCourant().getLiBase());
				// TODO

				getListeSpbase().add(getSpbaseCourant());
			} else if (getVAL_ST_ACTION_SPBASE().equals(ACTION_MODIFICATION)) {
				getSpbaseCourant().setLiBase(getVAL_EF_LIB_SPBASE());
				// getSpbaseDao().modifierSPBASE(getSpbaseCourant().getLiBase());
				// TODO
				setSpbaseCourant(null);
			}

			initialiseListeSpbase(request);
			addZone(getNOM_ST_ACTION_SPBASE(), Const.CHAINE_VIDE);
		}

		setFocus(getNOM_PB_ANNULER_SPBASE());
		return true;
	}

	private boolean performControlerRegleGestionSPBASE(HttpServletRequest request) {
		// V�rification des contraintes d'unicit� de la base horaire
		if (getVAL_ST_ACTION_SPBASE().equals(ACTION_CREATION)) {

			for (SPBASE motif : getListeSpbase()) {
				if (motif.getLiBase().trim().equals(getVAL_EF_LIB_SPBASE().toUpperCase().trim())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une base horaire", "ce libell�"));
					return false;
				}
				if (motif.getCdBase().trim().equals(getVAL_EF_CODE_SPBASE().toUpperCase().trim())) {
					// "ERR974",
					// "Attention, il existe d�j� @ avec @. Veuillez contr�ler."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR974", "une base horaire", "ce code"));
					return false;
				}
			}
		}
		return true;
	}

	private boolean performControlerSaisieSPBASE(HttpServletRequest request) {
		// Verification libell� not null
		if (getZone(getNOM_EF_LIB_SPBASE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libell�"));
			return false;
		}
		// Verification code not null
		if (getZone(getNOM_EF_CODE_SPBASE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "code"));
			return false;
		}
		// Verification nb heure legale not null
		if (getZone(getNOM_EF_BASE_HEBDO_LEG_H_SPBASE()).length() == 0 && getZone(getNOM_EF_BASE_HEBDO_LEG_M_SPBASE()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "base l�gale hebdomadaire"));
			return false;
		}
		return true;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIB_SPBASE Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_LIB_SPBASE() {
		return "NOM_EF_LIB_SPBASE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_LIB_SPBASE Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_LIB_SPBASE() {
		return getZone(getNOM_EF_LIB_SPBASE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_CODE_SPBASE Date de
	 * cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_CODE_SPBASE() {
		return "NOM_EF_CODE_SPBASE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_CODE_SPBASE Date de cr�ation : (14/09/11 13:52:54)
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
	 * EF_BASE_HEBDO_LEG_H_SPBASE Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_BASE_HEBDO_LEG_H_SPBASE() {
		return "NOM_EF_BASE_HEBDO_LEG_H_SPBASE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_BASE_HEBDO_LEG_H_SPBASE Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_BASE_HEBDO_LEG_H_SPBASE() {
		return getZone(getNOM_EF_BASE_HEBDO_LEG_H_SPBASE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_BASE_HEBDO_LEG_M_SPBASE Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_BASE_HEBDO_LEG_M_SPBASE() {
		return "NOM_EF_BASE_HEBDO_LEG_M_SPBASE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_BASE_HEBDO_LEG_M_SPBASE Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_BASE_HEBDO_LEG_M_SPBASE() {
		return getZone(getNOM_EF_BASE_HEBDO_LEG_M_SPBASE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_BASE_HEBDO_SPBASE
	 * Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getNOM_EF_BASE_HEBDO_SPBASE() {
		return "NOM_EF_BASE_HEBDO_SPBASE";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone de saisie :
	 * EF_BASE_HEBDO_SPBASE Date de cr�ation : (14/09/11 13:52:54)
	 * 
	 */
	public String getVAL_EF_BASE_HEBDO_SPBASE() {
		return getZone(getNOM_EF_BASE_HEBDO_SPBASE());
	}

	public ArrayList<String> getListeHeure() {
		return listeHeure == null ? new ArrayList<String>() : listeHeure;
	}

	public void setListeHeure(ArrayList<String> listeHeure) {
		this.listeHeure = listeHeure;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_HEURE_LUNDI Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_HEURE_LUNDI() {
		if (LB_HEURE_LUNDI == null)
			LB_HEURE_LUNDI = initialiseLazyLB();
		return LB_HEURE_LUNDI;
	}

	/**
	 * Setter de la liste: LB_HEURE_LUNDI Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	private void setLB_HEURE_LUNDI(String[] newLB_HEURE_LUNDI) {
		LB_HEURE_LUNDI = newLB_HEURE_LUNDI;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_HEURE_LUNDI Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_LUNDI() {
		return "NOM_LB_HEURE_LUNDI";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_HEURE_LUNDI_SELECT Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_LUNDI_SELECT() {
		return "NOM_LB_HEURE_LUNDI_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_HEURE_LUNDI Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_HEURE_LUNDI() {
		return getLB_HEURE_LUNDI();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_HEURE_LUNDI Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_HEURE_LUNDI_SELECT() {
		return getZone(getNOM_LB_HEURE_LUNDI_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_HEURE_MARDI Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_HEURE_MARDI() {
		if (LB_HEURE_MARDI == null)
			LB_HEURE_MARDI = initialiseLazyLB();
		return LB_HEURE_MARDI;
	}

	/**
	 * Setter de la liste: LB_HEURE_MARDI Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	private void setLB_HEURE_MARDI(String[] newLB_HEURE_MARDI) {
		LB_HEURE_MARDI = newLB_HEURE_MARDI;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_HEURE_MARDI Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_MARDI() {
		return "NOM_LB_HEURE_MARDI";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_HEURE_MARDI_SELECT Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_MARDI_SELECT() {
		return "NOM_LB_HEURE_MARDI_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_HEURE_MARDI Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_HEURE_MARDI() {
		return getLB_HEURE_MARDI();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_HEURE_MARDI Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_HEURE_MARDI_SELECT() {
		return getZone(getNOM_LB_HEURE_MARDI_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_HEURE_MERCREDI Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_HEURE_MERCREDI() {
		if (LB_HEURE_MERCREDI == null)
			LB_HEURE_MERCREDI = initialiseLazyLB();
		return LB_HEURE_MERCREDI;
	}

	/**
	 * Setter de la liste: LB_HEURE_MERCREDI Date de cr�ation : (21/11/11
	 * 09:55:36)
	 * 
	 */
	private void setLB_HEURE_MERCREDI(String[] newLB_HEURE_MERCREDI) {
		LB_HEURE_MERCREDI = newLB_HEURE_MERCREDI;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_HEURE_MERCREDI Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_MERCREDI() {
		return "NOM_LB_HEURE_MERCREDI";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_HEURE_MERCREDI_SELECT Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_MERCREDI_SELECT() {
		return "NOM_LB_HEURE_MERCREDI_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_HEURE_MERCREDI Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_HEURE_MERCREDI() {
		return getLB_HEURE_MERCREDI();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_HEURE_MERCREDI Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_HEURE_MERCREDI_SELECT() {
		return getZone(getNOM_LB_HEURE_MERCREDI_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_HEURE_JEUDI Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_HEURE_JEUDI() {
		if (LB_HEURE_JEUDI == null)
			LB_HEURE_JEUDI = initialiseLazyLB();
		return LB_HEURE_JEUDI;
	}

	/**
	 * Setter de la liste: LB_HEURE_JEUDI Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	private void setLB_HEURE_JEUDI(String[] newLB_HEURE_JEUDI) {
		LB_HEURE_JEUDI = newLB_HEURE_JEUDI;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_HEURE_JEUDI Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_JEUDI() {
		return "NOM_LB_HEURE_JEUDI";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_HEURE_JEUDI_SELECT Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_JEUDI_SELECT() {
		return "NOM_LB_HEURE_JEUDI_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_HEURE_JEUDI Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_HEURE_JEUDI() {
		return getLB_HEURE_JEUDI();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_HEURE_JEUDI Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_HEURE_JEUDI_SELECT() {
		return getZone(getNOM_LB_HEURE_JEUDI_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_HEURE_VENDREDI Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_HEURE_VENDREDI() {
		if (LB_HEURE_VENDREDI == null)
			LB_HEURE_VENDREDI = initialiseLazyLB();
		return LB_HEURE_VENDREDI;
	}

	/**
	 * Setter de la liste: LB_HEURE_VENDREDI Date de cr�ation : (21/11/11
	 * 09:55:36)
	 * 
	 */
	private void setLB_HEURE_VENDREDI(String[] newLB_HEURE_VENDREDI) {
		LB_HEURE_VENDREDI = newLB_HEURE_VENDREDI;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_HEURE_VENDREDI Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_VENDREDI() {
		return "NOM_LB_HEURE_VENDREDI";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_HEURE_VENDREDI_SELECT Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_VENDREDI_SELECT() {
		return "NOM_LB_HEURE_VENDREDI_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_HEURE_VENDREDI Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_HEURE_VENDREDI() {
		return getLB_HEURE_VENDREDI();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_HEURE_VENDREDI Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_HEURE_VENDREDI_SELECT() {
		return getZone(getNOM_LB_HEURE_VENDREDI_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_HEURE_SAMEDI Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_HEURE_SAMEDI() {
		if (LB_HEURE_SAMEDI == null)
			LB_HEURE_SAMEDI = initialiseLazyLB();
		return LB_HEURE_SAMEDI;
	}

	/**
	 * Setter de la liste: LB_HEURE_SAMEDI Date de cr�ation : (21/11/11
	 * 09:55:36)
	 * 
	 */
	private void setLB_HEURE_SAMEDI(String[] newLB_HEURE_SAMEDI) {
		LB_HEURE_SAMEDI = newLB_HEURE_SAMEDI;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_HEURE_SAMEDI Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_SAMEDI() {
		return "NOM_LB_HEURE_SAMEDI";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_HEURE_SAMEDI_SELECT Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_SAMEDI_SELECT() {
		return "NOM_LB_HEURE_SAMEDI_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_HEURE_SAMEDI Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_HEURE_SAMEDI() {
		return getLB_HEURE_SAMEDI();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_HEURE_SAMEDI Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_HEURE_SAMEDI_SELECT() {
		return getZone(getNOM_LB_HEURE_SAMEDI_SELECT());
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_HEURE_DIMANCHE Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	private String[] getLB_HEURE_DIMANCHE() {
		if (LB_HEURE_DIMANCHE == null)
			LB_HEURE_DIMANCHE = initialiseLazyLB();
		return LB_HEURE_DIMANCHE;
	}

	/**
	 * Setter de la liste: LB_HEURE_DIMANCHE Date de cr�ation : (21/11/11
	 * 09:55:36)
	 * 
	 */
	private void setLB_HEURE_DIMANCHE(String[] newLB_HEURE_DIMANCHE) {
		LB_HEURE_DIMANCHE = newLB_HEURE_DIMANCHE;
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_HEURE_DIMANCHE Date de
	 * cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_DIMANCHE() {
		return "NOM_LB_HEURE_DIMANCHE";
	}

	/**
	 * Retourne le nom de la zone de la ligne s�lectionn�e pour la JSP :
	 * NOM_LB_HEURE_DIMANCHE_SELECT Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_LB_HEURE_DIMANCHE_SELECT() {
		return "NOM_LB_HEURE_DIMANCHE_SELECT";
	}

	/**
	 * M�thode � personnaliser Retourne la valeur � afficher pour la zone de la
	 * JSP : LB_HEURE_DIMANCHE Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String[] getVAL_LB_HEURE_DIMANCHE() {
		return getLB_HEURE_DIMANCHE();
	}

	/**
	 * M�thode � personnaliser Retourne l'indice � s�lectionner pour la zone de
	 * la JSP : LB_HEURE_DIMANCHE Date de cr�ation : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_LB_HEURE_DIMANCHE_SELECT() {
		return getZone(getNOM_LB_HEURE_DIMANCHE_SELECT());
	}
}
