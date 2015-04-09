package nc.mairie.gestionagent.process.poste;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeCompetence;
import nc.mairie.metier.poste.Competence;
import nc.mairie.spring.dao.metier.poste.CompetenceDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.utils.VariablesActivite;

import org.springframework.context.ApplicationContext;

/**
 * Process OePOSTEFEActiviteSelection Date de création : (03/02/09 14:56:59)
 * 
 */
public class OePOSTEFECompetenceSelection extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Competence> listeCompetences;
	public String focus = null;
	private CompetenceDao competenceDao;

	/**
	 * @return Returns the listeCompetences.
	 */
	public ArrayList<Competence> getListeCompetences() {
		return listeCompetences;
	}

	/**
	 * @param listeCompetences
	 *            The listeCompetences to set.
	 */
	public void setListeCompetences(ArrayList<Competence> listeCompetences) {
		this.listeCompetences = listeCompetences;
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getCompetenceDao() == null) {
			setCompetenceDao(new CompetenceDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (03/02/09 14:56:59)
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void initialiseZones(HttpServletRequest request) throws Exception {
		initialiseDao();
		if (getListeCompetences() == null) {
			ArrayList<Competence> xcludeListeSavoir = (ArrayList<Competence>) VariablesActivite.recuperer(this,
					"LISTECOMPETENCESAVOIR");
			ArrayList<Competence> xcludeListeSavoirFaire = (ArrayList<Competence>) VariablesActivite.recuperer(this,
					"LISTECOMPETENCESAVOIRFAIRE");
			ArrayList<Competence> xcludeListeComportement = (ArrayList<Competence>) VariablesActivite.recuperer(this,
					"LISTECOMPETENCECOMPORTEMENT");
			ArrayList<Competence> aListe = new ArrayList<Competence>();

			if (xcludeListeSavoir != null) {
				aListe = getCompetenceDao().listerCompetenceAvecType(EnumTypeCompetence.SAVOIR.getCode());
				aListe = elim_doubure_competences(aListe, xcludeListeSavoir);
			} else if (xcludeListeSavoirFaire != null) {
				aListe = getCompetenceDao().listerCompetenceAvecType(EnumTypeCompetence.SAVOIR_FAIRE.getCode());
				aListe = elim_doubure_competences(aListe, xcludeListeSavoirFaire);
			} else if (xcludeListeComportement != null) {
				aListe = getCompetenceDao().listerCompetenceAvecType(EnumTypeCompetence.COMPORTEMENT.getCode());
				aListe = elim_doubure_competences(aListe, xcludeListeComportement);
			}

			// Affectation de la liste
			setListeCompetences(new ArrayList<Competence>());
			for (int j = 0; j < aListe.size(); j++) {
				Competence competence = (Competence) aListe.get(j);
				Integer i = competence.getIdCompetence();
				if (competence != null) {
					getListeCompetences().add(competence);
					addZone(getNOM_ST_ID_COMP(i), competence.getIdCompetence().toString());
					addZone(getNOM_ST_LIB_COMP(i), competence.getNomCompetence());
				}
			}
		}
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (03/02/09 14:56:59)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (03/02/09 14:56:59)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * 
	 * @param l1
	 * @param l2
	 * @return ArrayListe ayant elemine de la liste l1 les éléments en communs
	 *         avec l2 fonctionne uniquement avec une liste l1 n'ayant pas 2
	 *         elements identiques
	 */
	public static ArrayList<Competence> elim_doubure_competences(ArrayList<Competence> l1, ArrayList<Competence> l2) {
		if (null == l1)
			return null;

		if (null != l2) {
			for (int i = 0; i < l2.size(); i++) {
				for (int j = 0; j < l1.size(); j++) {
					if ((((Competence) l2.get(i)).getIdCompetence().toString()).equals(((Competence) l1.get(j)).getIdCompetence().toString()))
						l1.remove(j);
				}
			}
		}
		return l1;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (19/07/11 16:22:13)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (19/07/11 16:22:13)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		ArrayList<Competence> listCompSelect = new ArrayList<Competence>();
		for (int j = 0; j < getListeCompetences().size(); j++) {
			// on recupere la ligne concernée
			Competence comp = (Competence) getListeCompetences().get(j);
			Integer i = comp.getIdCompetence();
			// si la colonne selection est cochée
			if (getVAL_CK_SELECT_LIGNE(i).equals(getCHECKED_ON())) {
				listCompSelect.add(comp);
			}
		}
		VariablesActivite.ajouter(this, "COMPETENCE", listCompSelect);

		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (03/02/09 14:56:59)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OePOSTEFEActiviteSelection. Date de création :
	 * (24/08/11 09:15:05)
	 * 
	 */
	public OePOSTEFECompetenceSelection() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (24/08/11 09:15:05)
	 * 
	 */
	public String getJSP() {
		return "OePOSTEFECompetenceSelection.jsp";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ID_COMP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_ID_COMP(int i) {
		return "NOM_ST_ID_COMP_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ID_COMP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_ID_COMP(int i) {
		return getZone(getNOM_ST_ID_COMP(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB_COMP Date de
	 * création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_ST_LIB_COMP(int i) {
		return "NOM_ST_LIB_COMP_" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIB_COMP Date
	 * de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_ST_LIB_COMP(int i) {
		return getZone(getNOM_ST_LIB_COMP(i));
	}

	/**
	 * Retourne le nom de la case à cocher sélectionnée pour la JSP :
	 * CK_SELECT_LIGNE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getNOM_CK_SELECT_LIGNE(int i) {
		return "NOM_CK_SELECT_LIGNE_" + i;
	}

	/**
	 * Retourne la valeur de la case à cocher à afficher par la JSP pour la case
	 * a cocher : CK_SELECT_LIGNE Date de création : (21/11/11 09:55:36)
	 * 
	 */
	public String getVAL_CK_SELECT_LIGNE(int i) {
		return getZone(getNOM_CK_SELECT_LIGNE(i));
	}

	public CompetenceDao getCompetenceDao() {
		return competenceDao;
	}

	public void setCompetenceDao(CompetenceDao competenceDao) {
		this.competenceDao = competenceDao;
	}

}
