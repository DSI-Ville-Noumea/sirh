package nc.mairie.droits.process;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.poste.Affectation;
import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.Service;
import nc.mairie.spring.ws.SirhPtgWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;
import flexjson.JSONSerializer;

/**
 * Process OeDROITSGestion Date de cr�ation : (10/10/11 14:37:55)
 */
public class OeDROITSKiosque extends BasicProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATUT_APPROBATEUR = 1;
	public String ACTION_CREATION = "Cr�ation d'un approbateur.";
	public String ACTION_SUPPRESSION = "Suppression d'un approbateur.";

	public boolean majAppro = true;

	private ArrayList<AgentWithServiceDto> listeApprobateurs = new ArrayList<AgentWithServiceDto>();

	public String focus = null;

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
	 *            focus � d�finir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Initialisation des zones � afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la m�thode
	 * addZone(getNOMxxx, String); Date de cr�ation : (10/10/11 16:15:05)
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		// V�rification des droits d'acc�s.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Op�ration impossible. Vous ne disposez pas des droits d'acc�s � cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		if (etatStatut() == STATUT_APPROBATEUR) {
			initialiseApprobateurs(request);
		} else {
			if (isMajAppro()) {
				SirhPtgWSConsumer t = new SirhPtgWSConsumer();
				getListeApprobateurs().addAll((ArrayList<AgentWithServiceDto>) t.getApprobateurs());
			}
		}
		afficheListeApprobateurs();

	}

	private void initialiseApprobateurs(HttpServletRequest request) throws Exception {

		ArrayList<AgentNW> listeEvaluateurSelect = (ArrayList<AgentNW>) VariablesActivite.recuperer(this,
				"APPROBATEURS");
		VariablesActivite.enlever(this, "APPROBATEURS");

		if (listeEvaluateurSelect != null && listeEvaluateurSelect.size() > 0) {
			for (int j = 0; j < listeEvaluateurSelect.size(); j++) {
				AgentNW agt = listeEvaluateurSelect.get(j);
				// on cree les approbateurs en base de donn�es

				// on recupere l'agent ajout� eventuellement

				Affectation affCourante = Affectation.chercherAffectationActiveAvecAgent(getTransaction(),
						agt.getIdAgent());
				if (getTransaction().isErreur()) {
					// "ERR400", //
					// "L'agent @ n'est affect� � aucun poste. Il ne peut �tre ajout� en tant qu'approbateur."
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR400", agt.getIdAgent()));
					throw new Exception();

				}
				FichePoste fpCourante = FichePoste.chercherFichePoste(getTransaction(), affCourante.getIdFichePoste());
				Service serv = Service.chercherService(getTransaction(), fpCourante.getIdServi());
				AgentWithServiceDto agentDto = new AgentWithServiceDto();
				agentDto.setIdAgent(Integer.valueOf(agt.getIdAgent()));
				agentDto.setNom(agt.getNomAgent());
				agentDto.setPrenom(agt.getPrenomAgent());
				agentDto.setCodeService(fpCourante.getIdServi());
				agentDto.setService(serv.getLibService());
				getListeApprobateurs().add(agentDto);

			}

		}

	}

	private void afficheListeApprobateurs() throws Exception {

		for (int i = 0; i < getListeApprobateurs().size(); i++) {
			AgentWithServiceDto ag = getListeApprobateurs().get(i);

			addZone(getNOM_ST_AGENT(i), ag.getNom() + " " + ag.getPrenom() + " ("
					+ ag.getIdAgent().toString().substring(3, ag.getIdAgent().toString().length()) + ")");
			addZone(getNOM_ST_SERVICE(i), ag.getService() + " (" + ag.getCodeService() + ")");
		}

	}

	/**
	 * Retourne le nom de l'ecran utilis� par la gestion des droits
	 */
	public String getNomEcran() {
		return "ECR-DROIT-KIOSQUE";
	}

	/**
	 * M�thode appel�e par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de cr�ation : (10/10/11 14:37:55)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_AJOUTER
			if (testerParametre(request, getNOM_PB_AJOUTER())) {
				return performPB_AJOUTER(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListeApprobateurs().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

		}
		// Si TAG INPUT non g�r� par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Constructeur du process OeDROITSGestion. Date de cr�ation : (20/10/11
	 * 11:05:27)
	 * 
	 */
	public OeDROITSKiosque() {
		super();
	}

	/**
	 * Retourne le nom de la JSP du process Zone � utiliser dans un champ cach�
	 * dans chaque formulaire de la JSP. Date de cr�ation : (20/10/11 11:05:27)
	 * 
	 */
	public String getJSP() {
		return "OeDROITSKiosque.jsp";
	}

	public ArrayList<AgentWithServiceDto> getListeApprobateurs() {
		return listeApprobateurs;
	}

	public void setListeApprobateurs(ArrayList<AgentWithServiceDto> listeApprobateurs) {
		this.listeApprobateurs = listeApprobateurs;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_AGENT(int i) {
		return "NOM_ST_AGENT" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_AGENT Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_AGENT(int i) {
		return getZone(getNOM_ST_AGENT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SERVICE Date de
	 * cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_SERVICE(int i) {
		return "NOM_ST_SERVICE" + i;
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_SERVICE Date
	 * de cr�ation : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_SERVICE(int i) {
		return getZone(getNOM_ST_SERVICE(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * cr�ation : (05/09/11 11:39:24)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne la valeur � afficher par la JSP pour la zone : ST_ACTION Date de
	 * cr�ation : (05/09/11 11:39:24)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER Date de cr�ation :
	 * (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_AJOUTER() {
		return "NOM_PB_AJOUTER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_AJOUTER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_APPROBATEUR, true);
		return true;

	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER Date de cr�ation
	 * : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER(int i) {
		return "NOM_PB_SUPPRIMER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASuprimer) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		AgentWithServiceDto agentSelec = getListeApprobateurs().get(indiceEltASuprimer);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);

		if (getListeApprobateurs().contains(agentSelec)) {
			getListeApprobateurs().remove(agentSelec);
		}

		afficheListeApprobateurs();
		setMajAppro(false);
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de cr�ation :
	 * (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		SirhPtgWSConsumer t = new SirhPtgWSConsumer();
		List<AgentWithServiceDto> listeEnvoi = getListeApprobateurs();
		List<AgentWithServiceDto> listeAgentErreur = t.setApprobateurs(new JSONSerializer().serialize(listeEnvoi));
		if (listeAgentErreur.size() > 0) {
			String agents = Const.CHAINE_VIDE;
			for (AgentWithServiceDto agentDtoErreur : listeAgentErreur) {
				agents += " - " + agentDtoErreur.getNom() + " " + agentDtoErreur.getPrenom();
			}
			// "INF600",
			// "Les agents suivants n'ont pu �tre ajout� en tant qu'approbateurs car ils sont dej� op�rateurs : @"
			getTransaction().declarerErreur(MessageUtils.getMessage("INF600", agents));
			getListeApprobateurs().clear();
			return false;
		}
		getListeApprobateurs().clear();
		setMajAppro(true);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de cr�ation :
	 * (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Impl�mente les
	 * r�gles de gestion du process - Positionne un statut en fonction de ces
	 * r�gles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de cr�ation : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		getListeApprobateurs().clear();
		setMajAppro(true);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public boolean isMajAppro() {
		return majAppro;
	}

	public void setMajAppro(boolean majAppro) {
		this.majAppro = majAppro;
	}
}
