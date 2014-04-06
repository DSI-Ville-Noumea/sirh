package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.absence.dto.OrganisationSyndicaleDto;
import nc.mairie.gestionagent.dto.ReturnMessageDto;
import nc.mairie.metier.Const;
import nc.mairie.spring.ws.SirhAbsWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import flexjson.JSONSerializer;

/**
 * Process OePARAMETRAGEElection Date de création : (14/09/11 13:52:54)
 * 
 */
public class OePARAMETRAGEElection extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String ACTION_CREATION = "1";
	public String ACTION_MODIFICATION = "2";

	public String focus = null;
	private String[] LB_ORGANISATION;
	private ArrayList<OrganisationSyndicaleDto> listeOrganisation;

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

		if (getListeOrganisation().size() == 0) {
			initialiseListeOrganisation(request);
		}

	}

	private void initialiseListeOrganisation(HttpServletRequest request) {
		// Liste depuis SIRH-ABS-WS
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
		ArrayList<OrganisationSyndicaleDto> listeOrga = (ArrayList<OrganisationSyndicaleDto>) consuAbs
				.getListeOrganisationSyndicale();
		setListeOrganisation(listeOrga);

		if (getListeOrganisation().size() != 0) {
			int tailles[] = { 100, 20, 10 };
			String padding[] = { "G", "G", "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (OrganisationSyndicaleDto orga : getListeOrganisation()) {
				String ligne[] = { orga.getLibelle(), orga.getSigle(), orga.isActif() ? "" : "non" };

				aFormat.ajouteLigne(ligne);
			}
			setLB_ORGANISATION(aFormat.getListeFormatee());
		} else {
			setLB_ORGANISATION(null);
		}
	}

	/**
	 * Constructeur du process OePARAMETRAGEElection. Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public OePARAMETRAGEElection() {
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

			// Si clic sur le bouton PB_ANNULER_ORGANISATION
			if (testerParametre(request, getNOM_PB_ANNULER_ORGANISATION())) {
				return performPB_ANNULER_ORGANISATION(request);
			}

			// Si clic sur le bouton PB_CREER_ORGANISATION
			if (testerParametre(request, getNOM_PB_CREER_ORGANISATION())) {
				return performPB_CREER_ORGANISATION(request);
			}

			// Si clic sur le bouton PB_MODIFIER_ORGANISATION
			if (testerParametre(request, getNOM_PB_MODIFIER_ORGANISATION())) {
				return performPB_MODIFIER_ORGANISATION(request);
			}

			// Si clic sur le bouton PB_VALIDER_ORGANISATION
			if (testerParametre(request, getNOM_PB_VALIDER_ORGANISATION())) {
				return performPB_VALIDER_ORGANISATION(request);
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
		return "OePARAMETRAGEElection.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-ELEC";
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
		return "";
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	private String[] getLB_ORGANISATION() {
		if (LB_ORGANISATION == null)
			LB_ORGANISATION = initialiseLazyLB();
		return LB_ORGANISATION;
	}

	private void setLB_ORGANISATION(String[] newLB_ORGANISATION) {
		LB_ORGANISATION = newLB_ORGANISATION;
	}

	public String getNOM_LB_ORGANISATION() {
		return "NOM_LB_ORGANISATION";
	}

	public String getNOM_LB_ORGANISATION_SELECT() {
		return "NOM_LB_ORGANISATION_SELECT";
	}

	public String[] getVAL_LB_ORGANISATION() {
		return getLB_ORGANISATION();
	}

	public String getVAL_LB_ORGANISATION_SELECT() {
		return getZone(getNOM_LB_ORGANISATION_SELECT());
	}

	public String getNOM_PB_CREER_ORGANISATION() {
		return "NOM_PB_CREER_ORGANISATION";
	}

	public boolean performPB_CREER_ORGANISATION(HttpServletRequest request) throws Exception {
		viderZonesSaisie();

		// On nomme l'action
		addZone(getNOM_ST_ACTION_ORGANISATION(), ACTION_CREATION);

		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_ORGANISATION());
		return true;
	}

	private void viderZonesSaisie() {
		addZone(getNOM_ST_ACTION_ORGANISATION(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_LIB_ORGANISATION(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_SIGLE_ORGANISATION(), Const.CHAINE_VIDE);
		addZone(getNOM_RG_ORGANISATION_INACTIF(), getNOM_RB_NON());
	}

	public String getNOM_PB_MODIFIER_ORGANISATION() {
		return "NOM_PB_MODIFIER_ORGANISATION";
	}

	public boolean performPB_MODIFIER_ORGANISATION(HttpServletRequest request) throws Exception {

		int indice = (Services.estNumerique(getVAL_LB_ORGANISATION_SELECT()) ? Integer
				.parseInt(getVAL_LB_ORGANISATION_SELECT()) : -1);

		if (indice != -1 && indice < getListeOrganisation().size()) {

			OrganisationSyndicaleDto orga = getListeOrganisation().get(indice);

			addZone(getNOM_EF_LIB_ORGANISATION(), orga.getLibelle());
			addZone(getNOM_EF_SIGLE_ORGANISATION(), orga.getSigle());
			addZone(getNOM_RG_ORGANISATION_INACTIF(), orga.isActif() ? getNOM_RB_OUI() : getNOM_RB_NON());

			addZone(getNOM_ST_ACTION_ORGANISATION(), ACTION_MODIFICATION);
		} else {
			viderZonesSaisie();
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR008", "organisations syndicales"));
		}

		setStatut(STATUT_MEME_PROCESS);
		setFocus(getNOM_PB_ANNULER_ORGANISATION());
		return true;
	}

	public String getNOM_PB_VALIDER_ORGANISATION() {
		return "NOM_PB_VALIDER_ORGANISATION";
	}

	public boolean performPB_VALIDER_ORGANISATION(HttpServletRequest request) throws Exception {

		if (getVAL_ST_ACTION_ORGANISATION() != null && getVAL_ST_ACTION_ORGANISATION() != Const.CHAINE_VIDE) {
			if (getVAL_ST_ACTION_ORGANISATION().equals(ACTION_CREATION)
					|| getVAL_ST_ACTION_ORGANISATION().equals(ACTION_MODIFICATION)) {

				if (!performControlerSaisieOrganisation(request))
					return false;

				OrganisationSyndicaleDto orga = null;
				if (getVAL_ST_ACTION_ORGANISATION().equals(ACTION_CREATION)) {
					orga = new OrganisationSyndicaleDto();
					orga.setLibelle(getVAL_EF_LIB_ORGANISATION());
					orga.setSigle(getVAL_EF_SIGLE_ORGANISATION());
				} else {
					// modification
					int indice = (Services.estNumerique(getVAL_LB_ORGANISATION_SELECT()) ? Integer
							.parseInt(getVAL_LB_ORGANISATION_SELECT()) : -1);
					orga = getListeOrganisation().get(indice);
				}

				Boolean actif = getZone(getNOM_RG_ORGANISATION_INACTIF()).equals(getNOM_RB_OUI());
				orga.setActif(actif);

				// on sauvegarde
				SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
				ReturnMessageDto message = consuAbs.saveOrganisationSyndicale(new JSONSerializer().serialize(orga));

				if (message.getErrors().size() > 0) {
					String err = Const.CHAINE_VIDE;
					for (String erreur : message.getErrors()) {
						err += " " + erreur;
					}
					getTransaction().declarerErreur(err);
				}

			}
		}

		viderZonesSaisie();
		setListeOrganisation(null);

		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_ORGANISATION());
		return true;
	}

	private boolean performControlerSaisieOrganisation(HttpServletRequest request) {
		// Verification libelle not null
		if (getZone(getNOM_EF_LIB_ORGANISATION()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "libellé"));
			setFocus(getDefaultFocus());
			return false;
		}
		if (getZone(getNOM_EF_SIGLE_ORGANISATION()).length() == 0) {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "sigle"));
			setFocus(getDefaultFocus());
			return false;
		}
		return true;
	}

	public String getNOM_PB_ANNULER_ORGANISATION() {
		return "NOM_PB_ANNULER_ORGANISATION";
	}

	public boolean performPB_ANNULER_ORGANISATION(HttpServletRequest request) throws Exception {
		viderZonesSaisie();
		setStatut(STATUT_MEME_PROCESS);

		setFocus(getNOM_PB_ANNULER_ORGANISATION());
		return true;
	}

	public String getNOM_ST_ACTION_ORGANISATION() {
		return "NOM_ST_ACTION_ORGANISATION";
	}

	public String getVAL_ST_ACTION_ORGANISATION() {
		return getZone(getNOM_ST_ACTION_ORGANISATION());
	}

	public String getNOM_EF_LIB_ORGANISATION() {
		return "NOM_EF_LIB_ORGANISATION";
	}

	public String getVAL_EF_LIB_ORGANISATION() {
		return getZone(getNOM_EF_LIB_ORGANISATION());
	}

	public String getNOM_EF_SIGLE_ORGANISATION() {
		return "NOM_EF_SIGLE_ORGANISATION";
	}

	public String getVAL_EF_SIGLE_ORGANISATION() {
		return getZone(getNOM_EF_SIGLE_ORGANISATION());
	}

	public String getNOM_RG_ORGANISATION_INACTIF() {
		return "NOM_RG_ORGANISATION_INACTIF";
	}

	public String getVAL_RG_ORGANISATION_INACTIF() {
		return getZone(getNOM_RG_ORGANISATION_INACTIF());
	}

	public String getNOM_RB_NON() {
		return "NOM_RB_NON";
	}

	public String getNOM_RB_OUI() {
		return "NOM_RB_OUI";
	}

	public ArrayList<OrganisationSyndicaleDto> getListeOrganisation() {
		return listeOrganisation == null ? new ArrayList<OrganisationSyndicaleDto>() : listeOrganisation;
	}

	public void setListeOrganisation(ArrayList<OrganisationSyndicaleDto> listeOrganisation) {
		this.listeOrganisation = listeOrganisation;
	}
}
