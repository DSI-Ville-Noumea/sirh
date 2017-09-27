package nc.mairie.gestionagent.process.poste;

import nc.mairie.metier.poste.FicheMetier;
import nc.mairie.spring.dao.metier.poste.FicheMetierDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableActivite;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Process OePOSTEMetierSelection Date de création : (13/07/11 10:23:55)
 * 
 */
public class OePOSTEMetierSelection extends BasicProcess {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private List<FicheMetier> listeFicheMetier = new ArrayList<FicheMetier>();


	private FicheMetierDao ficheMetierDao;

	/**
	 * Initialisation des zones à  afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (13/07/11 10:23:55)
	 *
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		initialiseDao();
		// #17319
		// on recupere la recherche
		String recherche = (String) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_METIER);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_METIER);
		// on lance la recherche
		if (recherche != null && recherche.length() != 0) {
			addZone(getNOM_EF_RECHERCHE(), recherche);
			performPB_RECHERCHER(request, recherche);
		}
	}

	/**
	 * Constructeur du process OePOSTEMetierSelection. Date de création :
	 * (13/07/11 10:23:55)
	 *
	 */
	public OePOSTEMetierSelection() {
		super();
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (13/07/11 10:23:55)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 10:23:55)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER Date de création
	 * : (13/07/11 10:23:55)
	 * 
	 */
	public String getNOM_PB_RECHERCHER() {
		return "NOM_PB_RECHERCHER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 10:23:55)
	 * 
	 */
	public boolean performPB_RECHERCHER(HttpServletRequest request, String zone) throws Exception {

		List<FicheMetier> eListe = getFicheMetierDao().listerFicheMetierAvecRefMairieOuLibelle(zone);

		// Si liste vide alors erreur
		if (eListe.size() == 0) {
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("ERR005", "résultat"));
			return false;
		}

		setListeFicheMetier(eListe);

		int indiceFe = 0;
		if (getListeFicheMetier() != null) {
			for (int i = 0; i < getListeFicheMetier().size(); i++) {
				FicheMetier p = (FicheMetier) getListeFicheMetier().get(i);

				addZone(getNOM_ST_CODE(indiceFe), p.getRefMairie());
				addZone(getNOM_ST_LIB(indiceFe), p.getNomMetier());

				indiceFe++;
			}
		}
		return true;
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getFicheMetierDao() == null) {
			setFicheMetierDao(new FicheMetierDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_RECHERCHE Date de
	 * création : (13/07/11 10:23:55)
	 * 
	 */
	public String getNOM_EF_RECHERCHE() {
		return "NOM_EF_RECHERCHE";
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone de saisie :
	 * EF_RECHERCHE Date de création : (13/07/11 10:23:55)
	 * 
	 */
	public String getVAL_EF_RECHERCHE() {
		return getZone(getNOM_EF_RECHERCHE());
	}

	/**
	 * méthode appelee par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (13/07/11 10:23:55)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_VALIDER
			for (int i = 0; i < getListeFicheMetier().size(); i++) {
				if (testerParametre(request, getNOM_PB_VALIDER(i))) {
					return performPB_VALIDER(request, i);
				}
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_RECHERCHER
			if (testerParametre(request, getNOM_PB_RECHERCHER())) {
				return performPB_RECHERCHER(request, getVAL_EF_RECHERCHE());
			}
		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone a utiliser dans un champ cache
	 * dans chaque formulaire de la JSP. Date de création : (13/07/11 10:31:33)
	 * 
	 */
	public String getJSP() {
		return "OePOSTEMetierSelection.jsp";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (13/07/11 10:31:33)
	 * 
	 */
	public String getNOM_PB_VALIDER(int i) {
		return "NOM_PB_VALIDER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 10:31:33)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request, int elemSelection) throws Exception {
		setStatut(STATUT_PROCESS_APPELANT);

		FicheMetier ficheMetier = (FicheMetier) getListeFicheMetier().get(elemSelection);
		VariableActivite.ajouter(this, VariablesActivite.ACTIVITE_FICHE_METIER, ficheMetier);

		return true;
	}

	public List<FicheMetier> getListeFicheMetier() {
		if (listeFicheMetier == null) {
			listeFicheMetier = new ArrayList<>();
		}
		return listeFicheMetier;
	}

	public void setListeFicheMetier(List<FicheMetier> listeFicheMetier) {
		this.listeFicheMetier = listeFicheMetier;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_CODE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_CODE(int i) {
		return "NOM_ST_CODE" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_CODE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_CODE(int i) {
		return getZone(getNOM_ST_CODE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIB Date de création
	 * : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIB(int i) {
		return "NOM_ST_LIB" + i;
	}

	/**
	 * Retourne la valeur à  afficher par la JSP pour la zone : ST_LIB Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIB(int i) {
		return getZone(getNOM_ST_LIB(i));
	}

	public FicheMetierDao getFicheMetierDao() {
		return ficheMetierDao;
	}

	public void setFicheMetierDao(FicheMetierDao ficheMetierDao) {
		this.ficheMetierDao = ficheMetierDao;
	}
}
