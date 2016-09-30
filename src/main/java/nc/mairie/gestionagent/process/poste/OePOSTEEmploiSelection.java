package nc.mairie.gestionagent.process.poste;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.poste.FicheEmploi;
import nc.mairie.spring.dao.metier.poste.FicheEmploiDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableActivite;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.springframework.context.ApplicationContext;

/**
 * Process OePOSTEEmploiSelection Date de création : (13/07/11 10:23:55)
 * 
 */
public class OePOSTEEmploiSelection extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<FicheEmploi> listeFicheEmploi = new ArrayList<FicheEmploi>();

	private FicheEmploiDao ficheEmploiDao;

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
		String recherche = (String) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI);
		// on lance la recherche
		if (recherche != null && recherche.length() != 0) {
			addZone(getNOM_EF_RECHERCHE(), recherche);
			performPB_RECHERCHER(request, recherche);
		}
	}

	/**
	 * Constructeur du process OePOSTEEmploiSelection. Date de création :
	 * (13/07/11 10:23:55)
	 * 
	 */
	public OePOSTEEmploiSelection() {
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

		ArrayList<FicheEmploi> eListe = getFicheEmploiDao().listerFicheEmploiavecRefMairieOuLibelle(zone);

		// Si liste vide alors erreur
		if (eListe.size() == 0) {
			setStatut(STATUT_MEME_PROCESS, false, MessageUtils.getMessage("ERR005", "résultat"));
			return false;
		}

		setListeFicheEmploi(eListe);

		int indiceFe = 0;
		if (getListeFicheEmploi() != null) {
			for (int i = 0; i < getListeFicheEmploi().size(); i++) {
				FicheEmploi p = (FicheEmploi) getListeFicheEmploi().get(i);

				addZone(getNOM_ST_CODE(indiceFe), p.getRefMairie());
				addZone(getNOM_ST_LIB(indiceFe), p.getNomMetierEmploi());

				indiceFe++;
			}
		}
		return true;
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getFicheEmploiDao() == null) {
			setFicheEmploiDao(new FicheEmploiDao((SirhDao) context.getBean("sirhDao")));
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
			for (int i = 0; i < getListeFicheEmploi().size(); i++) {
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
		return "OePOSTEEmploiSelection.jsp";
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

		FicheEmploi ficheEmploi = (FicheEmploi) getListeFicheEmploi().get(elemSelection);
		VariableActivite.ajouter(this, VariablesActivite.ACTIVITE_FICHE_EMPLOI, ficheEmploi);

		return true;
	}

	public ArrayList<FicheEmploi> getListeFicheEmploi() {
		if (listeFicheEmploi == null)
			listeFicheEmploi = new ArrayList<FicheEmploi>();
		return listeFicheEmploi;
	}

	private void setListeFicheEmploi(ArrayList<FicheEmploi> listeFicheEmploi) {
		this.listeFicheEmploi = listeFicheEmploi;
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

	public FicheEmploiDao getFicheEmploiDao() {
		return ficheEmploiDao;
	}

	public void setFicheEmploiDao(FicheEmploiDao ficheEmploiDao) {
		this.ficheEmploiDao = ficheEmploiDao;
	}
}
