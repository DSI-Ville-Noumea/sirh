package nc.mairie.gestionagent.process.absence;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.absence.dto.MoisAlimAutoCongesAnnuelsDto;
import nc.mairie.metier.Const;
import nc.mairie.spring.ws.SirhAbsWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OeABSAlimentationMensuelle extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String focus = null;
	private Logger logger = LoggerFactory.getLogger(OeABSAlimentationMensuelle.class);

	private String[] LB_MOIS_ALIM_AUTO;
	private List<MoisAlimAutoCongesAnnuelsDto> listeMois;
	private List<MoisAlimAutoCongesAnnuelsDto> listeAlimAuto;
	private MoisAlimAutoCongesAnnuelsDto moisCourant;

	@Override
	public String getJSP() {
		return "OeABSAlimentationMensuelle.jsp";
	}

	public void setFocus(String focus) {
		this.focus = focus;
	}

	public String getFocus() {
		if (focus == null) {
			focus = getDefaultFocus();
		}
		return focus;
	}

	public String getDefaultFocus() {
		return Const.CHAINE_VIDE;
	}

	@Override
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);

		// ----------------------------------//
		// Vérification des droits d'acces. //
		// ----------------------------------//
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Operation impossible. Vous ne disposez pas des droits d'acces a cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseListeMois();
	}

	private void initialiseListeMois() {

		// Si liste mois vide alors affectation
		if (getListeMois() == null || getListeMois().size() == 0) {

			// on recupere la liste des mois disponible en BD dans SIRH-ABS-WS
			SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
			setListeMois(consuAbs.getListeMoisALimAUtoCongeAnnuel());
			logger.debug("Récupération des mois de la table abs_ca_alim_auto_histo : " + getListeMois().size()
					+ " enregistrements.");

			SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");

			int[] tailles = { 30 };
			String padding[] = { "G" };
			FormateListe aFormat = new FormateListe(tailles, padding, false);
			for (ListIterator<MoisAlimAutoCongesAnnuelsDto> list = getListeMois().listIterator(); list.hasNext();) {
				MoisAlimAutoCongesAnnuelsDto mois = (MoisAlimAutoCongesAnnuelsDto) list.next();
				String ligne[] = { sdf.format(mois.getDateMois()) };

				aFormat.ajouteLigne(ligne);

			}
			setLB_MOIS_ALIM_AUTO(aFormat.getListeFormatee(true));
		}
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_AFFICHER
			if (testerParametre(request, getNOM_PB_AFFICHER())) {
				return performPB_AFFICHER(request);
			}
			// Si clic sur le bouton PB_AFFICHER_ERREUR
			if (testerParametre(request, getNOM_PB_AFFICHER_ERREUR())) {
				return performPB_AFFICHER_ERREUR(request);
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public String getNomEcran() {
		return "ECR-ABS-VISU";
	}

	private String[] getLB_MOIS_ALIM_AUTO() {
		if (LB_MOIS_ALIM_AUTO == null)
			LB_MOIS_ALIM_AUTO = initialiseLazyLB();
		return LB_MOIS_ALIM_AUTO;
	}

	private void setLB_MOIS_ALIM_AUTO(String[] newLB_MOIS_ALIM_AUTO) {
		LB_MOIS_ALIM_AUTO = newLB_MOIS_ALIM_AUTO;
	}

	public String getNOM_LB_MOIS_ALIM_AUTO() {
		return "NOM_LB_MOIS_ALIM_AUTO";
	}

	public String getNOM_LB_MOIS_ALIM_AUTO_SELECT() {
		return "NOM_LB_MOIS_ALIM_AUTO_SELECT";
	}

	public String[] getVAL_LB_MOIS_ALIM_AUTO() {
		return getLB_MOIS_ALIM_AUTO();
	}

	public String getVAL_LB_MOIS_ALIM_AUTO_SELECT() {
		return getZone(getNOM_LB_MOIS_ALIM_AUTO_SELECT());
	}

	public String getNOM_PB_AFFICHER() {
		return "NOM_PB_AFFICHER";
	}

	public boolean performPB_AFFICHER(HttpServletRequest request) throws Exception {

		// Recuperation du mois
		MoisAlimAutoCongesAnnuelsDto moisChoisi = null;
		int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_ALIM_AUTO_SELECT()) ? Integer
				.parseInt(getVAL_LB_MOIS_ALIM_AUTO_SELECT()) : -1);
		if (indiceMois > 0) {
			moisChoisi = (MoisAlimAutoCongesAnnuelsDto) getListeMois().get(indiceMois - 1);
			setMoisCourant(moisChoisi);
		} else {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "mois"));
			return false;
		}

		// Liste des alimentation depuis SIRH-ABS-WS
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
		setListeAlimAuto(consuAbs.getListeAlimAutoCongeAnnuel(moisChoisi, false));
		afficheAlimAuto();
		return true;
	}

	private void afficheAlimAuto() {
		for (int j = 0; j < getListeAlimAuto().size(); j++) {
			MoisAlimAutoCongesAnnuelsDto histo = getListeAlimAuto().get(j);
			if (histo.getAgent() != null && histo.getAgent().getIdAgent() != null) {
				addZone(getNOM_ST_NOMATR_AGENT(j),
						histo.getAgent().getIdAgent().toString()
								.substring(3, histo.getAgent().getIdAgent().toString().length()));
				addZone(getNOM_ST_LIB_AGENT(j), histo.getAgent().getNom() + " " + histo.getAgent().getPrenom());
				if (histo.getStatus().length() > 150) {
					addZone(getNOM_ST_STATUT(j), histo.getStatus().substring(0, 150));
				} else {
					addZone(getNOM_ST_STATUT(j), histo.getStatus());
				}
				if (histo.getInfos() != null && histo.getInfos().length() > 150) {
					addZone(getNOM_ST_INFO(j), histo.getInfos().substring(0, 150));
				} else {
					addZone(getNOM_ST_INFO(j), histo.getInfos());
				}
			}
		}
	}

	public List<MoisAlimAutoCongesAnnuelsDto> getListeMois() {
		return listeMois;
	}

	public void setListeMois(List<MoisAlimAutoCongesAnnuelsDto> listeMois) {
		this.listeMois = listeMois;
	}

	public MoisAlimAutoCongesAnnuelsDto getMoisCourant() {
		return moisCourant;
	}

	public void setMoisCourant(MoisAlimAutoCongesAnnuelsDto moisCourant) {
		this.moisCourant = moisCourant;
	}

	public List<MoisAlimAutoCongesAnnuelsDto> getListeAlimAuto() {
		return listeAlimAuto == null ? new ArrayList<MoisAlimAutoCongesAnnuelsDto>() : listeAlimAuto;
	}

	public void setListeAlimAuto(List<MoisAlimAutoCongesAnnuelsDto> listeAlimAuto) {
		this.listeAlimAuto = listeAlimAuto;
	}

	public String getNOM_ST_NOMATR_AGENT(int i) {
		return "NOM_ST_NOMATR_AGENT" + i;
	}

	public String getVAL_ST_NOMATR_AGENT(int i) {
		return getZone(getNOM_ST_NOMATR_AGENT(i));
	}

	public String getNOM_ST_LIB_AGENT(int i) {
		return "NOM_ST_LIB_AGENT" + i;
	}

	public String getVAL_ST_LIB_AGENT(int i) {
		return getZone(getNOM_ST_LIB_AGENT(i));
	}

	public String getNOM_ST_STATUT(int i) {
		return "NOM_ST_STATUT" + i;
	}

	public String getVAL_ST_STATUT(int i) {
		return getZone(getNOM_ST_STATUT(i));
	}

	public String getNOM_ST_INFO(int i) {
		return "NOM_ST_INFO" + i;
	}

	public String getVAL_ST_INFO(int i) {
		return getZone(getNOM_ST_INFO(i));
	}

	public String getNOM_PB_AFFICHER_ERREUR() {
		return "NOM_PB_AFFICHER_ERREUR";
	}

	public boolean performPB_AFFICHER_ERREUR(HttpServletRequest request) throws Exception {

		// Recuperation du mois
		MoisAlimAutoCongesAnnuelsDto moisChoisi = null;
		int indiceMois = (Services.estNumerique(getVAL_LB_MOIS_ALIM_AUTO_SELECT()) ? Integer
				.parseInt(getVAL_LB_MOIS_ALIM_AUTO_SELECT()) : -1);
		if (indiceMois > 0) {
			moisChoisi = (MoisAlimAutoCongesAnnuelsDto) getListeMois().get(indiceMois - 1);
			setMoisCourant(moisChoisi);
		} else {
			// "ERR002","La zone @ est obligatoire."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "mois"));
			return false;
		}

		// Liste des alimentation depuis SIRH-ABS-WS
		SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
		setListeAlimAuto(consuAbs.getListeAlimAutoCongeAnnuel(moisChoisi, true));
		afficheAlimAuto();
		return true;
	}
}
