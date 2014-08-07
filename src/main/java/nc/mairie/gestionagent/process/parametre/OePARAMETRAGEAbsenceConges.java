package nc.mairie.gestionagent.process.parametre;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumTypeGroupeAbsence;
import nc.mairie.gestionagent.absence.dto.TypeAbsenceDto;
import nc.mairie.metier.Const;
import nc.mairie.spring.ws.SirhAbsWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

/**
 * Process OePARAMETRAGERecrutement Date de création : (14/09/11 13:52:54)
 * 
 */
public class OePARAMETRAGEAbsenceConges extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String focus = null;
	private ArrayList<TypeAbsenceDto> listeTypeAbsence;

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

		if (getListeTypeAbsence().size() == 0) {
			initialiseListeTypeAbsence(request);
		}

	}

	private void initialiseListeTypeAbsence(HttpServletRequest request) {
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
		setListeTypeAbsence((ArrayList<TypeAbsenceDto>) consuAbs.getListeRefTypeAbsenceDto());

		for (TypeAbsenceDto abs : getListeTypeAbsence()) {
			if (abs.getGroupeAbsence() == null
					|| abs.getGroupeAbsence().getIdRefGroupeAbsence() != EnumTypeGroupeAbsence.CONGES_EXCEP.getValue()) {
				continue;
			}
			Integer i = abs.getIdRefTypeAbsence();
			addZone(getNOM_ST_TYPE_CONGE(i), abs.getLibelle());
			String unite = Const.CHAINE_VIDE;
			if (abs.getTypeSaisiDto().isChkDateDebut()) {
				unite = "0,5 j";
			} else if (abs.getTypeSaisiDto().isCalendarHeureDebut()) {
				unite = "1 h";
			} else if (abs.getTypeSaisiDto().isCalendarDateDebut()) {
				unite = "1 j";
			}
			addZone(getNOM_ST_UNITE(i), unite);
			addZone(getNOM_ST_INFO(i), abs.getTypeSaisiDto().getMessageAlerte() == null ? Const.CHAINE_VIDE : abs
					.getTypeSaisiDto().getMessageAlerte());
		}
	}

	/**
	 * Constructeur du process OePARAMETRAGEAbsence. Date de création :
	 * (14/09/11 13:52:54)
	 * 
	 */
	public OePARAMETRAGEAbsenceConges() {
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

			// Si clic sur le bouton PB_AJOUTER_CONGES
			if (testerParametre(request, getNOM_PB_AJOUTER_CONGES())) {
				return performPB_AJOUTER_CONGES(request);
			}

			// Si clic sur les boutons du tableau
			for (TypeAbsenceDto abs : getListeTypeAbsence()) {
				int indiceAbs = abs.getIdRefTypeAbsence();
				// Si clic sur le bouton PB_MODIFIER_CONGES
				if (testerParametre(request, getNOM_PB_MODIFIER_CONGES(indiceAbs))) {
					return performPB_MODIFIER_CONGES(request, indiceAbs);
				}
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
		return "OePARAMETRAGEAbsenceConges.jsp";
	}

	/**
	 * Retourne le nom de l'écran (notamment pour déterminer les droits
	 * associés).
	 */
	public String getNomEcran() {
		return "ECR-PARAM-ABS-CONG";
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

	public ArrayList<TypeAbsenceDto> getListeTypeAbsence() {
		return listeTypeAbsence == null ? new ArrayList<TypeAbsenceDto>() : listeTypeAbsence;
	}

	public void setListeTypeAbsence(ArrayList<TypeAbsenceDto> listeTypeAbsence) {
		this.listeTypeAbsence = listeTypeAbsence;
	}

	public String getNOM_ST_TYPE_CONGE(int i) {
		return "NOM_ST_TYPE_CONGE_" + i;
	}

	public String getVAL_ST_TYPE_CONGE(int i) {
		return getZone(getNOM_ST_TYPE_CONGE(i));
	}

	public String getNOM_ST_UNITE(int i) {
		return "NOM_ST_UNITE_" + i;
	}

	public String getVAL_ST_UNITE(int i) {
		return getZone(getNOM_ST_UNITE(i));
	}

	public String getNOM_ST_INFO(int i) {
		return "NOM_ST_INFO_" + i;
	}

	public String getVAL_ST_INFO(int i) {
		return getZone(getNOM_ST_INFO(i));
	}

	public String getNOM_PB_AJOUTER_CONGES() {
		return "NOM_PB_AJOUTER_CONGES";
	}

	public boolean performPB_AJOUTER_CONGES(HttpServletRequest request) throws Exception {

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNOM_PB_MODIFIER_CONGES(int i) {
		return "NOM_PB_MODIFIER_CONGES_" + i;
	}

	public boolean performPB_MODIFIER_CONGES(HttpServletRequest request, int idDemande) throws Exception {
		// on recupere la demande
		TypeAbsenceDto aChercher = new TypeAbsenceDto();
		aChercher.setIdRefTypeAbsence(idDemande);
		TypeAbsenceDto type = getListeTypeAbsence().get(getListeTypeAbsence().indexOf(aChercher));
		

		// On pose le statut
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}
}
