package nc.mairie.gestionagent.process;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.enums.EnumNationalite;
import nc.mairie.enums.EnumSexe;
import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.Agent;
import nc.mairie.metier.agent.Enfant;
import nc.mairie.metier.agent.LienEnfantAgent;
import nc.mairie.metier.agent.Scolarite;
import nc.mairie.metier.commun.Commune;
import nc.mairie.metier.commun.CommuneEtrangere;
import nc.mairie.metier.commun.Departement;
import nc.mairie.metier.commun.Pays;
import nc.mairie.spring.dao.metier.agent.AgentDao;
import nc.mairie.spring.dao.metier.agent.EnfantDao;
import nc.mairie.spring.dao.metier.agent.LienEnfantAgentDao;
import nc.mairie.spring.dao.metier.agent.ScolariteDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.VariablesActivite;

import org.springframework.context.ApplicationContext;

/**
 * Process OeENFANTGestion Date de création : (25/03/03 15:33:10)
 * 
 */
public class OeENFANTGestion extends BasicProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUT_RECHERCHER_AGENT = 1;
	public static final int STATUT_LIEU_NAISS_FRANCE = 2;
	public static final int STATUT_LIEU_NAISS_AUTRE = 3;
	public static final int STATUT_AUTRE_PARENT = 4;
	public static final int STATUT_LIEU_NAISS = 5;
	public static final int STATUT_ENFANT_HOMONYME = 6;

	private String[] LB_NATIONALITE;

	public String ACTION_SUPPRESSION = "Suppression d'une fiche enfant.";
	public String ACTION_MODIFICATION = "Modification d'une fiche enfant.";
	public String ACTION_CONSULTATION = "Consultation d'une fiche enfant.";
	public String ACTION_CREATION = "Création d'une fiche enfant.";

	private Agent agentCourant;
	private LienEnfantAgent lienEnfantAgentCourant;
	private ArrayList<Enfant> listeEnfants;
	private List<Scolarite> listeScolarites;
	private Enfant enfantCourant;
	private Scolarite scolariteCourant;
	private Pays paysNaissance;
	private Object communeNaissance;
	private Agent autreParentCourant;
	private LienEnfantAgent lienEnfantAutreParent;

	private ScolariteDao scolariteDao;
	private LienEnfantAgentDao lienEnfantAgentDao;
	private EnfantDao enfantDao;
	private AgentDao agentDao;

	public String focus = null;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();

		if (getScolariteDao() == null) {
			setScolariteDao(new ScolariteDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getLienEnfantAgentDao() == null) {
			setLienEnfantAgentDao(new LienEnfantAgentDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getEnfantDao() == null) {
			setEnfantDao(new EnfantDao((SirhDao) context.getBean("sirhDao")));
		}
		if (getAgentDao() == null) {
			setAgentDao(new AgentDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	/**
	 * Alimente l'enfant courant.
	 * 
	 * @param request
	 * @throws Exception
	 */
	private void alimenterEnfantCourant(HttpServletRequest request) throws Exception {

		// Affectation des zones
		getEnfantCourant().setNom(getVAL_EF_NOM());
		getEnfantCourant().setPrenom(getVAL_EF_PRENOM());
		getEnfantCourant().setDateNaissance(sdf.parse(getVAL_EF_DATE_NAISS()));
		getEnfantCourant().setDateDeces(
				getVAL_EF_DATE_DECES().equals(Const.CHAINE_VIDE) ? null : sdf.parse(getVAL_EF_DATE_DECES()));
		getEnfantCourant().setCommentaire(getVAL_EF_COMMENTAIRE());

		// Nationalite
		int indiceNation = (Services.estNumerique(getVAL_LB_NATIONALITE_SELECT()) ? Integer
				.parseInt(getVAL_LB_NATIONALITE_SELECT()) : -1);
		getEnfantCourant().setNationalite(getLB_NATIONALITE()[indiceNation].substring(0, 1));

		// Lieu de naissance
		if (getPaysNaissance() != null) {
			CommuneEtrangere comm = (CommuneEtrangere) getCommuneNaissance();
			getEnfantCourant().setCodePaysNaissEt(Integer.valueOf(comm.getCodPays()));
			getEnfantCourant().setCodeCommuneNaissEt(Integer.valueOf(comm.getCodCommuneEtrangere()));
			getEnfantCourant().setCodeCommuneNaissFr(null);
		} else {
			Commune comm = (Commune) getCommuneNaissance();
			if (comm != null) {
				getEnfantCourant().setCodePaysNaissEt(null);
				getEnfantCourant().setCodeCommuneNaissFr(Integer.valueOf(comm.getCodCommune()));
			} else {
				getEnfantCourant().setCodePaysNaissEt(null);
				getEnfantCourant().setCodeCommuneNaissFr(null);
			}
			getEnfantCourant().setCodeCommuneNaissEt(null);
		}

		if (getVAL_RG_SEXE().equals(getNOM_RB_SEXE_M())) {
			getEnfantCourant().setSexe(EnumSexe.MASCULIN.getCode());
		} else {
			getEnfantCourant().setSexe(EnumSexe.FEMININ.getCode());
		}
	}

	/**
	 * Affiche l'autre parent enfant sélectionné
	 */
	private void afficheAutreParent(HttpServletRequest request) throws Exception {

		// Si parent est null
		if (getAutreParentCourant() == null) {
			addZone(getNOM_ST_AUTRE_PARENT(), Const.CHAINE_VIDE);
		} else {
			addZone(getNOM_ST_AUTRE_PARENT(), getAutreParentCourant().getNomAgent() + " "
					+ getAutreParentCourant().getPrenomAgent());
		}
	}

	/**
	 * Affiche la liste des scolarites de l'enfant
	 */
	private void afficheListeScolarite(HttpServletRequest request) throws Exception {

		setListeScolarites(getScolariteDao().listerScolariteEnfant(getEnfantCourant().getIdEnfant()));
		rafraichirListeScolarite(request);
	}

	/**
	 * Rafraichit l'affichage de la liste des scolarites de l'enfant
	 */
	private void rafraichirListeScolarite(HttpServletRequest request) throws Exception {
		int indiceScol = 0;
		if (getListeScolarites() != null) {
			for (int i = 0; i < getListeScolarites().size(); i++) {
				Scolarite scol = (Scolarite) getListeScolarites().get(i);

				addZone(getNOM_ST_DATE_DEBUT(indiceScol), sdf.format(scol.getDateDebutScolarite()));
				addZone(getNOM_ST_DATE_FIN(indiceScol),
						scol.getDateFinScolarite() == null ? "&nbsp;" : sdf.format(scol.getDateFinScolarite()));

				indiceScol++;
			}
		}
	}

	/**
	 * Affiche l'enfant sélectionné
	 */
	private void afficheEnfantCourant(HttpServletRequest request) throws Exception {

		// Récup du Enfant courant
		Enfant aEnfant = getEnfantCourant();

		// Si vide, on vide tout
		if (aEnfant == null) {
			videZonesDeSaisie(request);
			return;
		}

		// Récup des lien parent
		ArrayList<LienEnfantAgent> liens = getLienEnfantAgentDao().listerLienEnfantAgentAvecEnfant(
				aEnfant.getIdEnfant());
		setAutreParentCourant(null);
		setLienEnfantAutreParent(null);

		if (liens.size() > 2) {
			String listeMatriculesParents = Const.CHAINE_VIDE;
			for (int i = 0; i < liens.size(); i++) {
				LienEnfantAgent aLien = (LienEnfantAgent) liens.get(i);
				listeMatriculesParents += aLien.getIdAgent().toString().substring(2) + " ";
			}
			// ERR020 : Données erronées. Un enfant ne peut avoir plus de 2
			// parents et celui-ci est lié aux agents suivants : @.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR020", listeMatriculesParents));
		} else {
			for (int i = 0; i < liens.size(); i++) {
				LienEnfantAgent aLien = (LienEnfantAgent) liens.get(i);
				if (aLien.getIdAgent().toString().equals(getAgentCourant().getIdAgent().toString())) {
					setLienEnfantAgentCourant(aLien);
				} else {
					setLienEnfantAutreParent(aLien);
					Agent a = getAgentDao().chercherAgent(aLien.getIdAgent());
					setAutreParentCourant(a);
				}
			}
		}

		// Alim zones
		addZone(getNOM_EF_NOM(), aEnfant.getNom());
		addZone(getNOM_EF_PRENOM(), aEnfant.getPrenom());
		addZone(getNOM_EF_DATE_NAISS(), sdf.format(aEnfant.getDateNaissance()));
		addZone(getNOM_EF_DATE_DECES(),
				aEnfant.getDateDeces() == null ? Const.CHAINE_VIDE : sdf.format(aEnfant.getDateDeces()));
		String posNationF = (LB_NATIONALITE[0].startsWith("F") ? "0" : "1");
		String posNationE = (posNationF.equals("0") ? "1" : "0");
		addZone(getNOM_LB_NATIONALITE_SELECT(),
				EnumNationalite.FRANCAISE.getCode().equals(aEnfant.getNationalite()) ? posNationF : posNationE);
		addZone(getNOM_RG_SEXE(), EnumSexe.MASCULIN.getCode().equals(aEnfant.getSexe()) ? getNOM_RB_SEXE_M()
				: getNOM_RB_SEXE_F());
		addZone(getNOM_RG_CHARGE(), getLienEnfantAgentCourant().isEnfantACharge() ? getNOM_RB_CHARGE_O()
				: getNOM_RB_CHARGE_N());
		addZone(getNOM_EF_COMMENTAIRE(), aEnfant.getCommentaire());

		// Affichage de autre parent
		afficheAutreParent(request);

		// Affichage lieu de naissance
		afficheLieuNaissance(request);

		// Affichage de la liste de scolarites
		afficheListeScolarite(request);
	}

	private void afficheEnfantSuppression(HttpServletRequest request) throws Exception {

		// Récup du Enfant courant
		Enfant aEnfant = getEnfantCourant();

		// Si vide, on vide tout
		if (aEnfant == null) {
			videZonesDeSaisie(request);
			return;
		}

		// Récup des lien parent
		ArrayList<LienEnfantAgent> liens = getLienEnfantAgentDao().listerLienEnfantAgentAvecEnfant(
				aEnfant.getIdEnfant());
		boolean estACharge = false;
		setAutreParentCourant(null);

		for (int i = 0; i < liens.size(); i++) {
			LienEnfantAgent aLien = (LienEnfantAgent) liens.get(i);
			if (aLien.getIdAgent().toString().equals(getAgentCourant().getIdAgent().toString())) {
				setLienEnfantAgentCourant(aLien);
				estACharge = aLien.isEnfantACharge();
			} else {
				setLienEnfantAutreParent(aLien);
				Agent a = getAgentDao().chercherAgent(aLien.getIdAgent());
				setAutreParentCourant(a);
			}
		}

		// Alim zones
		addZone(getNOM_ST_NOM(), aEnfant.getNom());
		addZone(getNOM_ST_PRENOM(), aEnfant.getPrenom());
		addZone(getNOM_ST_DATENAISS(), sdf.format(aEnfant.getDateNaissance()));
		addZone(getNOM_ST_DATEDECES(),
				aEnfant.getDateDeces() == null ? Const.CHAINE_VIDE : sdf.format(aEnfant.getDateDeces()));
		if (aEnfant.getNationalite().equals(EnumNationalite.FRANCAISE.getCode())) {
			addZone(getNOM_ST_NATIONALITE(), EnumNationalite.FRANCAISE.getValue());
		} else {
			addZone(getNOM_ST_NATIONALITE(), EnumNationalite.ETRANGERE.getValue());
		}
		addZone(getNOM_ST_SEXE(), aEnfant.getSexe().equals(EnumSexe.MASCULIN.getCode()) ? EnumSexe.MASCULIN.getCode()
				: EnumSexe.FEMININ.getCode());
		addZone(getNOM_ST_ACHARGE(), estACharge ? "Oui" : "Non");
		addZone(getNOM_ST_COMMENTAIRE(), aEnfant.getCommentaire());

		// Affichage de autre parent
		afficheAutreParent(request);

		// Affichage lieu de naissance
		afficheLieuNaissance(request);

		// Affichage de la liste de scolarites
		afficheListeScolarite(request);
	}

	/**
	 * Affiche le lieu de naissance en fonction de la commune
	 */
	private void afficheLieuNaissance(HttpServletRequest request) throws Exception {

		// recup du lieu de naissance. En session si présent et sur l'enfant
		// courant sinon
		Pays paysNaiss = (Pays) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_PAYS);
		VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_PAYS);
		Object commNaiss;
		if (paysNaiss != null) {
			commNaiss = (CommuneEtrangere) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_COMMUNE_ET);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_COMMUNE_ET);
		} else {
			commNaiss = (Commune) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_COMMUNE_FR);
			VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_COMMUNE_FR);
			if (commNaiss == null) {
				if (getEnfantCourant() != null) {
					if (getEnfantCourant().getCodePaysNaissEt() != null) {
						commNaiss = CommuneEtrangere
								.chercherCommuneEtrangere(getTransaction(), getEnfantCourant().getCodePaysNaissEt()
										.toString(), getEnfantCourant().getCodeCommuneNaissEt());
					} else if (getEnfantCourant().getCodeCommuneNaissFr() != null
							&& getEnfantCourant().getCodeCommuneNaissFr() != 0) {
						commNaiss = Commune.chercherCommune(getTransaction(), getEnfantCourant()
								.getCodeCommuneNaissFr());
					}
				}
			}
		}
		setPaysNaissance(paysNaiss);
		setCommuneNaissance(commNaiss);

		// Si instance de commune
		if (getCommuneNaissance() instanceof Commune) {
			Commune c = (Commune) getCommuneNaissance();
			String pays = null;
			// Si departement DOM TOM
			if (c.getCodDepartement().compareTo("97") >= 0) {
				Departement aDep = Departement.chercherDepartementCommune(getTransaction(), c);
				pays = aDep.getLibDepartement();
			} else {
				pays = Const.COMMUNE_FRANCE;
			}
			addZone(getNOM_ST_PAYS_NAISS(), pays);
			addZone(getNOM_ST_COMMUNE_NAISS(), c.getLibCommune());
			// Si instance de CommuneEtrangere
		} else if (getCommuneNaissance() instanceof CommuneEtrangere) {
			CommuneEtrangere c = (CommuneEtrangere) getCommuneNaissance();
			// recup du pays de la commune Etrangere
			Pays p = Pays.chercherPays(getTransaction(), Integer.valueOf(c.getCodPays()));
			setPaysNaissance(p);

			addZone(getNOM_ST_PAYS_NAISS(), p.getLibPays());
			addZone(getNOM_ST_COMMUNE_NAISS(), c.getLibCommuneEtrangere());
			// Sinon
		} else {
			addZone(getNOM_ST_PAYS_NAISS(), Const.CHAINE_VIDE);
			addZone(getNOM_ST_COMMUNE_NAISS(), Const.CHAINE_VIDE);
		}
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (27/03/2003
	 * 10:53:36)
	 * 
	 * @return nc.mairie.metier.agent.Agent
	 */
	public Agent getAgentCourant() {
		return agentCourant;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (27/03/2003
	 * 15:22:22)
	 * 
	 * @return nc.mairie.metier.agent.Agent
	 */
	private Agent getAutreParentCourant() {
		return autreParentCourant;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (27/03/2003
	 * 14:31:00)
	 * 
	 * @return java.lang.Object
	 */
	private java.lang.Object getCommuneNaissance() {
		return communeNaissance;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (27/03/2003
	 * 11:27:55)
	 * 
	 * @return nc.mairie.metier.agent.Enfant
	 */
	private Enfant getEnfantCourant() {
		return enfantCourant;
	}

	/**
	 * Getter de la liste avec un lazy initialize : LB_NATIONALITE Date de
	 * création : (25/03/03 15:33:11)
	 * 
	 */
	private String[] getLB_NATIONALITE() {
		if (LB_NATIONALITE == null)
			LB_NATIONALITE = initialiseLazyLB();
		return LB_NATIONALITE;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (27/03/2003
	 * 10:55:12)
	 * 
	 * @return ArrayList
	 */
	public ArrayList<Enfant> getListeEnfants() {
		if (listeEnfants == null) {
			listeEnfants = new ArrayList<Enfant>();
		}
		return listeEnfants;
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_DECES Date de
	 * création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_EF_DATE_DECES() {
		return "NOM_EF_DATE_DECES";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_NAISS Date de
	 * création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_EF_DATE_NAISS() {
		return "NOM_EF_DATE_NAISS";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_NOM Date de
	 * création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_EF_NOM() {
		return "NOM_EF_NOM";
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_PRENOM Date de
	 * création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_EF_PRENOM() {
		return "NOM_EF_PRENOM";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_ENFANT Date de création :
	 * (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_LB_ENFANT() {
		return "NOM_LB_ENFANT";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_ENFANT_SELECT Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_LB_ENFANT_SELECT() {
		return "NOM_LB_ENFANT_SELECT";
	}

	/**
	 * Retourne le nom de la zone pour la JSP : NOM_LB_NATIONALITE Date de
	 * création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_LB_NATIONALITE() {
		return "NOM_LB_NATIONALITE";
	}

	/**
	 * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
	 * NOM_LB_NATIONALITE_SELECT Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_LB_NATIONALITE_SELECT() {
		return "NOM_LB_NATIONALITE_SELECT";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_ANNULER Date de création :
	 * (25/03/03 15:37:09)
	 * 
	 */
	public String getNOM_PB_ANNULER() {
		return "NOM_PB_ANNULER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AUTRE_PARENT Date de
	 * création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_PB_AUTRE_PARENT() {
		return "NOM_PB_AUTRE_PARENT";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AUTRE_PARENT_VIRE Date de
	 * création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_PB_AUTRE_PARENT_VIRE() {
		return "NOM_PB_AUTRE_PARENT_VIRE";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_CREER Date de création :
	 * (25/03/03 15:37:09)
	 * 
	 */
	public String getNOM_PB_CREER() {
		return "NOM_PB_CREER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_LIEU_NAISS Date de création
	 * : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_PB_LIEU_NAISS() {
		return "NOM_PB_LIEU_NAISS";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER Date de création
	 * : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_PB_RECHERCHER() {
		return "NOM_PB_RECHERCHER";
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
	 * (25/03/03 15:37:09)
	 * 
	 */
	public String getNOM_PB_VALIDER() {
		return "NOM_PB_VALIDER";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_CHARGE_N Date de
	 * création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_RB_CHARGE_N() {
		return "NOM_RB_CHARGE_N";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_CHARGE_O Date de
	 * création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_RB_CHARGE_O() {
		return "NOM_RB_CHARGE_O";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_PAYS_NAISS_AUTRE Date de
	 * création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_RB_PAYS_NAISS_AUTRE() {
		return "NOM_RB_PAYS_NAISS_AUTRE";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_PAYS_NAISS_FRANCE Date
	 * de création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_RB_PAYS_NAISS_FRANCE() {
		return "NOM_RB_PAYS_NAISS_FRANCE";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SCOLARISE_N Date de
	 * création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_RB_SCOLARISE_N() {
		return "NOM_RB_SCOLARISE_N";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SCOLARISE_O Date de
	 * création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_RB_SCOLARISE_O() {
		return "NOM_RB_SCOLARISE_O";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SEXE_F Date de création
	 * : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_RB_SEXE_F() {
		return "NOM_RB_SEXE_F";
	}

	/**
	 * Retourne le nom du radio bouton pour la JSP : RB_SEXE_M Date de création
	 * : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_RB_SEXE_M() {
		return "NOM_RB_SEXE_M";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_CHARGE
	 * Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_RG_CHARGE() {
		return "NOM_RG_CHARGE";
	}

	/**
	 * Retourne le nom du groupe de radio boutons coché pour la JSP : RG_SEXE
	 * Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_RG_SEXE() {
		return "NOM_RG_SEXE";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getNOM_ST_ACTION() {
		return "NOM_ST_ACTION";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
	 * création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_ST_AGENT() {
		return "NOM_ST_AGENT";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AUTRE_PARENT Date de
	 * création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_ST_AUTRE_PARENT() {
		return "NOM_ST_AUTRE_PARENT";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_COMMUNE_NAISS Date
	 * de création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_ST_COMMUNE_NAISS() {
		return "NOM_ST_COMMUNE_NAISS";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_PAYS_NAISS Date de
	 * création : (25/03/03 15:33:11)
	 * 
	 */
	public String getNOM_ST_PAYS_NAISS() {
		return "NOM_ST_PAYS_NAISS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_DECES Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public String getVAL_EF_DATE_DECES() {
		return getZone(getNOM_EF_DATE_DECES());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_NAISS Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public String getVAL_EF_DATE_NAISS() {
		return getZone(getNOM_EF_DATE_NAISS());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie : EF_NOM
	 * Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public String getVAL_EF_NOM() {
		return getZone(getNOM_EF_NOM());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_PRENOM Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public String getVAL_EF_PRENOM() {
		return getZone(getNOM_EF_PRENOM());
	}

	/**
	 * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
	 * JSP : LB_NATIONALITE Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public String[] getVAL_LB_NATIONALITE() {
		return getLB_NATIONALITE();
	}

	/**
	 * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
	 * la JSP : LB_NATIONALITE Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public String getVAL_LB_NATIONALITE_SELECT() {
		return getZone(getNOM_LB_NATIONALITE_SELECT());
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_CHARGE
	 * Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public String getVAL_RG_CHARGE() {
		return getZone(getNOM_RG_CHARGE());
	}

	/**
	 * Retourne la valeur du radio bouton (RB_) coché dans la JSP : RG_SEXE Date
	 * de création : (25/03/03 15:33:11)
	 * 
	 */
	public String getVAL_RG_SEXE() {
		return getZone(getNOM_RG_SEXE());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACTION Date de
	 * création : (11/02/03 14:20:31)
	 * 
	 */
	public String getVAL_ST_ACTION() {
		return getZone(getNOM_ST_ACTION());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de
	 * création : (25/03/03 15:33:11)
	 * 
	 */
	public String getVAL_ST_AGENT() {
		return getZone(getNOM_ST_AGENT());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AUTRE_PARENT
	 * Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public String getVAL_ST_AUTRE_PARENT() {
		return getZone(getNOM_ST_AUTRE_PARENT());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMMUNE_NAISS
	 * Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public String getVAL_ST_COMMUNE_NAISS() {
		return getZone(getNOM_ST_COMMUNE_NAISS());
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_PAYS_NAISS
	 * Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public String getVAL_ST_PAYS_NAISS() {
		return getZone(getNOM_ST_PAYS_NAISS());
	}

	/**
	 * Initialisation de la liste des contacts
	 * 
	 */
	private void initialiseListeEnfants(HttpServletRequest request) throws Exception {

		// Recherche des enfants de l'agent
		ArrayList<Enfant> a = getEnfantDao().listerEnfantAgent(getAgentCourant().getIdAgent(), getLienEnfantAgentDao());
		setListeEnfants(a);

		int indiceEnfant = 0;
		if (getListeEnfants() != null) {
			for (int i = 0; i < getListeEnfants().size(); i++) {
				Enfant aEnfant = (Enfant) getListeEnfants().get(i);
				String nomCommune = null;
				// si commune renseignée
				if (aEnfant.getCodeCommuneNaissEt() != null && aEnfant.getCodeCommuneNaissEt() != 0) {
					CommuneEtrangere c = CommuneEtrangere.chercherCommuneEtrangere(getTransaction(), aEnfant
							.getCodePaysNaissEt().toString(), aEnfant.getCodeCommuneNaissEt());
					nomCommune = c.getLibCommuneEtrangere();
				} else if (aEnfant.getCodeCommuneNaissFr() != null && aEnfant.getCodeCommuneNaissFr() != 0) {
					Commune c = Commune.chercherCommune(getTransaction(), aEnfant.getCodeCommuneNaissFr());
					nomCommune = c.getLibCommune();
				}

				addZone(getNOM_ST_NOM(indiceEnfant),
						aEnfant.getNom().equals(Const.CHAINE_VIDE) ? "&nbsp;" : aEnfant.getNom());
				addZone(getNOM_ST_PRENOM(indiceEnfant), aEnfant.getPrenom().equals(Const.CHAINE_VIDE) ? "&nbsp;"
						: aEnfant.getPrenom());
				addZone(getNOM_ST_SEXE(indiceEnfant),
						aEnfant.getSexe().equals(Const.CHAINE_VIDE) ? "&nbsp;" : aEnfant.getSexe());
				addZone(getNOM_ST_DATE_NAISS(indiceEnfant), sdf.format(aEnfant.getDateNaissance()));
				addZone(getNOM_ST_LIEU_NAISS(indiceEnfant), nomCommune == null ? "&nbsp;" : nomCommune);

				indiceEnfant++;
			}
		}

		// init des zones et actions
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		setEnfantCourant(null);
		afficheEnfantCourant(request);
	}

	/**
	 * Initialisation des zones à afficher dans la JSP Alimentation des listes,
	 * s'il y en a, avec setListeLB_XXX() ATTENTION : Les Objets dans la liste
	 * doivent avoir les Fields PUBLIC Utilisation de la méthode
	 * addZone(getNOMxxx, String); Date de création : (25/03/03 15:33:10)
	 * 
	 */
	public void initialiseZones(HttpServletRequest request) throws Exception {
		// POUR RESTER SUR LA MEME PAGE LORS DE LA RECHERCHE D'UN AGENT
		VariableGlobale.ajouter(request, "PROCESS_MEMORISE", this);
		if (MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);
		}

		// Vérification des droits d'accès.
		if (MairieUtils.estInterdit(request, getNomEcran())) {
			// "ERR190",
			// "Opération impossible. Vous ne disposez pas des droits d'accès à cette option."
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR190"));
			throw new Exception();
		}

		initialiseDao();
		initialiseListeDeroulante();

		// Si agentCourant vide ou si etat recherche
		if (getAgentCourant() == null || etatStatut() == STATUT_RECHERCHER_AGENT
				|| MaClasse.STATUT_RECHERCHE_AGENT == etatStatut()) {
			Agent aAgent = (Agent) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_AGENT_MAIRIE);
			if (aAgent != null) {
				setAgentCourant(aAgent);
				// initialisation fenêtre si changement de l'agent
				initialiseListeEnfants(request);
			} else {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
				return;
			}
		}

		// Si enfantCourant vide
		if (getEnfantCourant() == null) {
			Enfant aEnfant = (Enfant) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_ENFANT_COURANT);
			if (aEnfant != null) {
				setEnfantCourant(aEnfant);
				VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_ENFANT_COURANT);
			}
		}

		// Si le statut est ENFANT_HOMONYME
		if (etatStatut() == STATUT_ENFANT_HOMONYME) {
			// Reinitialisation de la liste des enfants
			initialiseListeEnfants(request);
		}

		// Si le statut est AUTRE_PARENT
		if (etatStatut() == STATUT_AUTRE_PARENT) {
			// recup de l'agent Activite
			Agent a = (Agent) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			if (a != null) {
				setAutreParentCourant(a);
				afficheAutreParent(request);
				VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
			}
		}

		// Si le statut est LIEU_NAISS
		if (etatStatut() == STATUT_LIEU_NAISS) {
			afficheLieuNaissance(request);
		}
	}

	/**
	 * Initialise les listes déroulantes et les Radio bouton.
	 * 
	 * @throws Exception
	 *             RG_AG_EN_A03 RG_AG_EN_A02 RG_AG_EN_A01
	 */
	private void initialiseListeDeroulante() throws Exception {
		// Si liste nationalité vide alors affectation
		// RG_AG_EN_A03
		if (getLB_NATIONALITE() == LBVide) {
			ArrayList<String> a = new ArrayList<String>();
			a.add(EnumNationalite.getValues()[0]);
			a.add(EnumNationalite.getValues()[1]);
			setLB_NATIONALITE((String[]) a.toArray(new String[0]));
		}

		// Si aucun sexe alors garçon par défaut
		// RG_AG_EN_A02

		if (getZone(getNOM_RG_SEXE()).length() == 0) {
			addZone(getNOM_RG_SEXE(), getNOM_RB_SEXE_M());
		}

		// Si aucun à charge alors non défaut
		// RG_AG_EN_A01
		if (getZone(getNOM_RG_CHARGE()).length() == 0) {
			addZone(getNOM_RG_CHARGE(), getNOM_RB_CHARGE_N());
		}
	}

	/**
	 * Contrôle les zones saisies Date de création : (17/03/03 11:01:57)
	 * 
	 * RG_AG_EN_C02 RG_AG_EN_C04 RG_AG_EN_C03
	 */
	private boolean performControlerSaisie(HttpServletRequest request) throws Exception {

		// Nom de l'enfant obligatoire
		if (Const.CHAINE_VIDE.equals(getVAL_EF_NOM())) {
			// ERR002 : La zone @ est obligatoire.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Nom"));
			setFocus(getNOM_EF_NOM());
			return false;
		}

		// Prénom de l'enfant obligatoire
		if (Const.CHAINE_VIDE.equals(getVAL_EF_PRENOM())) {
			// ERR002 : La zone @ est obligatoire.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Prénom"));
			setFocus(getNOM_EF_PRENOM());
			return false;
		}

		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_NAISS()))) {
			// format de date
			if (!Services.estUneDate(getZone(getNOM_EF_DATE_NAISS()))) {
				// ERR007 : La date @ est incorrecte. Elle doit être au format
				// date.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de naissance"));
				setFocus(getNOM_EF_DATE_NAISS());
				return false;
			}
			// La date de naissance ne doit pas être supérieure à la date du
			// jour
			// RG_AG_EN_C02
			if (Services.compareDates(getZone(getNOM_EF_DATE_NAISS()), Services.dateDuJour()) >= 0) {
				// ERR204 : La date @ doit être inférieure à la date @.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR204", "de naissance", "du jour"));
				setFocus(getNOM_EF_DATE_NAISS());
				return false;
			}
			// la date de naissance ne doit pas être < date naissance agent
			String dateAgent = sdf.format(getAgentCourant().getDateNaissance());
			int compare = Services.compareDates(getZone(getNOM_EF_DATE_NAISS()), dateAgent);
			if (compare == -9999) {
				// ERR203 : Opération impossible. Veuillez vérifier le format
				// des dates.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR203"));
				return false;
			} else if (compare < 1) {
				// ERR205 : La date @ doit être supérieure à la date @.
				getTransaction().declarerErreur(
						MessageUtils.getMessage("ERR205", "de naissance de l'enfant", "de naissance de l'agent ("
								+ getAgentCourant().getDateNaissance() + ")"));
				setFocus(getNOM_EF_DATE_NAISS());
				return false;
			}
			// la date de naissance ne doit pas être supérieure à la date de
			// début de scolarité -2
			// RG_AG_EN_C04
			for (int i = 0; i < getListeScolarites().size(); i++) {
				Scolarite scol = (Scolarite) getListeScolarites().get(i);
				if (Services.compareDates(getVAL_EF_DATE_NAISS(),
						Services.ajouteAnnee(Services.formateDate(sdf.format(scol.getDateDebutScolarite())), -2)) >= 0) {
					// Si date de début inférieure à date de naissance + 2 ans
					if (Services.compareDates(sdf.format(scol.getDateDebutScolarite()),
							Services.ajouteAnnee(Services.formateDate(getZone(getNOM_EF_DATE_NAISS())), 2)) < 0) {
						// "ERR205","La date @ doit être supérieure à la date @"
						getTransaction().declarerErreur(
								MessageUtils.getMessage("ERR205", "début scolarité", "de naissance + 2 ans"));
						return false;
					}
				}
			}
		} else {
			// ERR002 : La zone @ est obligatoire.
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Date de naissance"));
			setFocus(getNOM_EF_DATE_NAISS());
			return false;
		}

		if (!(Const.CHAINE_VIDE).equals(getZone(getNOM_EF_DATE_DECES()))) {
			// ERR007 : La date @ est incorrecte. Elle doit être au format date.
			if (!Services.estUneDate(getZone(getNOM_EF_DATE_DECES()))) {
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "de décès"));
				setFocus(getNOM_EF_DATE_DECES());
				return false;
			}
			// la date de décès ne doit pas être supérieure à la date du jour
			// RG_AG_EN_C03
			if (Services.compareDates(getZone(getNOM_EF_DATE_DECES()), Services.dateDuJour()) >= 0) {
				// ERR204 : La date @ doit être inférieure à la date @.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR204", "de décès", "du jour"));
				setFocus(getNOM_EF_DATE_NAISS());
				return false;
			}

			// La date de décès doit être supérieure à la date de naissance
			if (Services.compareDates(getZone(getNOM_EF_DATE_NAISS()), getZone(getNOM_EF_DATE_DECES())) >= 0) {
				// ERR204 : La date @ doit être inférieure à la date @.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR204", "de naissance", "de décès"));
				setFocus(getNOM_EF_DATE_DECES());
				return false;
			}
		}

		return true;
	}

	/**
	 * Contrôle les scolarites saisies Date de création : (05/05/11)
	 * RG_AG_EN_C06 RG_AG_EN_C05 RG_AG_EN_C04
	 */
	private boolean performControlerSaisieScolarite(HttpServletRequest request, String dateDebutScol, String dateFinScol)
			throws Exception {
		if (!performControlerSaisie(request))
			return false;
		else {
			// Date de début
			// RG_AG_EN_C06
			if (dateDebutScol == null) {
				// "ERR002","La zone @ est obligatoire"
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR002", "Date début scolarité"));
				return false;
			} else if (!Services.estUneDate(dateDebutScol)) {
				// ERR007 : La date @ est incorrecte. Elle doit être au format
				// date.
				getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "début scolarité"));
				setFocus(getNOM_EF_DATE_DEBUT_SCOLARITE());
				return false;
			} else {
				// Si date de début inférieure à date de naissance + 2 ans
				// RG_AG_EN_C04
				if (Services.compareDates(dateDebutScol,
						Services.ajouteAnnee(Services.formateDate(getZone(getNOM_EF_DATE_NAISS())), 2)) < 0) {
					// "ERR205","La date @ doit être supérieure à la date @"
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR205", "début scolarité", "de naissance + 2 ans"));
					return false;
				}
			}

			// Date de fin
			if (dateFinScol != null) {
				if (!Services.estUneDate(dateFinScol)) {
					// ERR007 : La date @ est incorrecte. Elle doit être au
					// format date.
					getTransaction().declarerErreur(MessageUtils.getMessage("ERR007", "fin scolarité"));
					setFocus(getNOM_EF_DATE_FIN_SCOLARITE());
					return false;
				}

				// Si Date fin scolarité saisie, elle doit être supérieure à la
				// Date début scolarité
				// RG_AG_EN_C05
				if (Services.compareDates(dateDebutScol, dateFinScol) >= 0) {
					// "ERR204","La date @ doit être inférieure à la date @"
					getTransaction().declarerErreur(
							MessageUtils.getMessage("ERR204", "début scolarité", "fin scolarité"));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:37:09)
	 * 
	 */
	public boolean performPB_ANNULER(HttpServletRequest request) throws Exception {
		if (Const.CHAINE_VIDE.equals(getVAL_ST_ACTION())) {
			setStatut(STATUT_PROCESS_APPELANT);
		} else {
			initialiseListeEnfants(request);
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_AUTRE_PARENT(HttpServletRequest request) throws Exception {

		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		// On met l'agent courant en var d'activité
		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, getAgentCourant());

		setStatut(STATUT_AUTRE_PARENT, true);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_AUTRE_PARENT_VIRE(HttpServletRequest request) throws Exception {

		// On vire l'autre parent
		setAutreParentCourant(null);
		afficheAutreParent(request);

		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:37:09)
	 * 
	 */
	public boolean performPB_CREER(HttpServletRequest request) throws Exception {

		// init du Enfant courant
		setEnfantCourant(null);
		setCommuneNaissance(null);
		setListeScolarites(new ArrayList<Scolarite>());
		afficheEnfantCourant(request);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CREATION);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_LIEU_NAISS(HttpServletRequest request) throws Exception {

		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		setStatut(STATUT_LIEU_NAISS, true);

		return true;

	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
	 * 
	 */
	public boolean performPB_RECHERCHER(HttpServletRequest request) throws Exception {
		setStatut(STATUT_RECHERCHER_AGENT, true);
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:37:09)
	 * 
	 */
	public boolean performPB_VALIDER(HttpServletRequest request) throws Exception {
		// Si aucune action en cours
		if (getZone(getNOM_ST_ACTION()).length() == 0) {
			// "Vous ne pouvez pas valider, il n'y a pas d'action en cours."
			setStatut(STATUT_MEME_PROCESS, true, MessageUtils.getMessage("ERR006"));
			return false;
		}

		// Si Action Suppression
		if (getZone(getNOM_ST_ACTION()).equals(ACTION_SUPPRESSION)) {
			if (!performPB_VALIDER_Supprimer(request))
				return false;
			// Si Action Modification
		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_MODIFICATION)) {
			if (!performPB_VALIDER_Modifier(request))
				return false;
			// Si Action Creation
		} else if (getZone(getNOM_ST_ACTION()).equals(ACTION_CREATION)) {
			if (!performPB_VALIDER_Creer(request))
				return false;
		}

		// Tout s'est bien passé
		commitTransaction();
		initialiseListeEnfants(request);

		return true;
	}

	/**
	 * Création de l'enfant saisi. Date de création : (25/03/03 15:37:09)
	 * 
	 * RG_AG_EN_C01
	 */
	private boolean performPB_VALIDER_Creer(HttpServletRequest request) throws Exception {

		setEnfantCourant(new Enfant());

		// Controle des saisies
		if (!performControlerSaisie(request))
			return false;

		// Affectation des zones
		alimenterEnfantCourant(request);

		// Verif enfant pas déjà existant (même nom, prénom, date de Naissance)
		Date dateNaiss = sdf.parse(Services.formateDate(getVAL_EF_DATE_NAISS()));
		ArrayList<Enfant> listEnfant = getEnfantDao().listerEnfantHomonyme(getVAL_EF_NOM(), getVAL_EF_PRENOM(),
				dateNaiss);
		if (getTransaction().isErreur()) {
			getTransaction().declarerErreur(getTransaction().traiterErreur());
			return false;
		}
		if (listEnfant.size() > 0) {
			// Enfants homonymes trouvés
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LST_ENFANT_HOMONYME, listEnfant);
			VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_ENFANT_COURANT, getEnfantCourant());
			setStatut(STATUT_ENFANT_HOMONYME, true);
		} else {

			// Creation enfant
			Integer id = getEnfantDao().creerEnfant(getEnfantCourant().getIdDocument(), getEnfantCourant().getNom(),
					getEnfantCourant().getPrenom(), getEnfantCourant().getSexe(),
					getEnfantCourant().getDateNaissance(), getEnfantCourant().getCodePaysNaissEt(),
					getEnfantCourant().getCodeCommuneNaissEt(), getEnfantCourant().getCodeCommuneNaissFr(),
					getEnfantCourant().getDateDeces(), getEnfantCourant().getNationalite(),
					getEnfantCourant().getCommentaire());

			// Mise à jour du lien enfant parent
			boolean estACharge = getVAL_RG_CHARGE().equals(getNOM_RB_CHARGE_O());
			if (getAgentCourant() != null) {
				LienEnfantAgent newLien = new LienEnfantAgent();
				newLien.setIdAgent(getAgentCourant().getIdAgent());
				newLien.setIdEnfant(id);
				newLien.setEnfantACharge(estACharge);
				getLienEnfantAgentDao().creerLienEnfantAgent(newLien.getIdAgent(), newLien.getIdEnfant(),
						newLien.isEnfantACharge());
				if (getTransaction().isErreur())
					return false;
			}

			// Mise à jour du lien enfant autreParent
			// RG_AG_EN_C01
			if (getAutreParentCourant() != null) {
				setLienEnfantAutreParent(new LienEnfantAgent());
				getLienEnfantAutreParent().setIdAgent(getAutreParentCourant().getIdAgent());
				getLienEnfantAutreParent().setIdEnfant(id);
				getLienEnfantAutreParent().setEnfantACharge(false);
				getLienEnfantAgentDao().creerLienEnfantAgent(getLienEnfantAutreParent().getIdAgent(),
						getLienEnfantAutreParent().getIdEnfant(), getLienEnfantAutreParent().isEnfantACharge());
				if (getTransaction().isErreur())
					return false;
			}

			// Liste des scolarites
			for (int i = 0; i < getListeScolarites().size(); i++) {
				Scolarite scol = (Scolarite) getListeScolarites().get(i);
				scol.setIdEnfant(getEnfantCourant().getIdEnfant());

				getScolariteDao().creerScolarite(scol.getIdEnfant(), scol.getDateDebutScolarite(),
						scol.getDateFinScolarite());

				if (getTransaction().isErreur())
					return false;
			}
		}

		getTransaction().commitTransaction();
		return true;

	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:37:09)
	 * 
	 * RG_AG_EN_C01
	 */
	private boolean performPB_VALIDER_Modifier(HttpServletRequest request) throws Exception {
		// Controle des saisies
		if (!performControlerSaisie(request))
			return false;

		// Modification de l'enfant courant
		alimenterEnfantCourant(request);
		getEnfantDao().modifierEnfant(getEnfantCourant().getIdEnfant(), getEnfantCourant().getIdDocument(),
				getEnfantCourant().getNom(), getEnfantCourant().getPrenom(), getEnfantCourant().getSexe(),
				getEnfantCourant().getDateNaissance(), getEnfantCourant().getCodePaysNaissEt(),
				getEnfantCourant().getCodeCommuneNaissEt(), getEnfantCourant().getCodeCommuneNaissFr(),
				getEnfantCourant().getDateDeces(), getEnfantCourant().getNationalite(),
				getEnfantCourant().getCommentaire());

		// Mise à jour du lien enfant parent
		getLienEnfantAgentCourant().setEnfantACharge(getVAL_RG_CHARGE().equals(getNOM_RB_CHARGE_O()));
		getLienEnfantAgentDao().modifierLienEnfantAgent(getLienEnfantAgentCourant().getIdAgent(),
				getLienEnfantAgentCourant().getIdEnfant(), getLienEnfantAgentCourant().isEnfantACharge());

		// Mise à jour du lien enfant autreParent
		// RG_AG_EN_C01
		if (getLienEnfantAutreParent() == null && getAutreParentCourant() != null) {
			LienEnfantAgent newLien = new LienEnfantAgent();
			newLien.setIdAgent(getAutreParentCourant().getIdAgent());
			newLien.setIdEnfant(getEnfantCourant().getIdEnfant());
			newLien.setEnfantACharge(false);
			getLienEnfantAgentDao().creerLienEnfantAgent(newLien.getIdAgent(), newLien.getIdEnfant(),
					newLien.isEnfantACharge());
		}
		if (getLienEnfantAutreParent() != null) {
			if (getAutreParentCourant() != null) {
				getLienEnfantAutreParent().setIdAgent(getAutreParentCourant().getIdAgent());
				getLienEnfantAutreParent().setEnfantACharge(false);
				getLienEnfantAgentDao().modifierLienEnfantAgent(getLienEnfantAutreParent().getIdAgent(),
						getLienEnfantAutreParent().getIdEnfant(), getLienEnfantAutreParent().isEnfantACharge());
			} else {
				getLienEnfantAgentDao().supprimerLienEnfantAgent(getLienEnfantAutreParent().getIdAgent(),
						getLienEnfantAutreParent().getIdEnfant());
			}
		}
		if (getTransaction().isErreur())
			return false;

		// Liste des scolarites
		List<Scolarite> scolaritesActuelles = getScolariteDao().listerScolariteEnfant(getEnfantCourant().getIdEnfant());
		for (int i = 0; i < scolaritesActuelles.size(); i++) {
			Scolarite scol = (Scolarite) scolaritesActuelles.get(i);
			if (!getListeScolarites().contains(scol)) {
				getScolariteDao().supprimerScolarite(scol.getIdScolarite());
			}
			getListeScolarites().remove(scol);
		}
		for (int j = 0; j < getListeScolarites().size(); j++) {
			Scolarite scol = (Scolarite) getListeScolarites().get(j);
			getScolariteDao().creerScolarite(scol.getIdEnfant(), scol.getDateDebutScolarite(),
					scol.getDateFinScolarite());
		}

		commitTransaction();
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:37:09)
	 * 
	 * RG_AG_EN_C01
	 */
	private boolean performPB_VALIDER_Supprimer(HttpServletRequest request) throws Exception {

		// Suppression du lien enfant parent
		getLienEnfantAgentDao().supprimerLienEnfantAgent(getLienEnfantAgentCourant().getIdAgent(),
				getLienEnfantAgentCourant().getIdEnfant());
		if (getTransaction().isErreur())
			return false;

		// Si l'enfant n'est plus lié à aucun agent
		// RG_AG_EN_C01
		if (getLienEnfantAutreParent() == null) {
			// Suppression des scolarites
			for (int i = 0; i < getListeScolarites().size(); i++) {
				Scolarite scol = (Scolarite) getListeScolarites().get(i);
				getScolariteDao().supprimerScolarite(scol.getIdScolarite());
			}
			if (getTransaction().isErreur())
				return false;

			// Suppression de l'enfant
			getEnfantDao().supprimerEnfant(getEnfantCourant().getIdEnfant(), getLienEnfantAgentDao());
			if (getTransaction().isErreur())
				return false;
		}

		getTransaction().commitTransaction();
		return true;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (27/03/2003
	 * 10:53:36)
	 * 
	 * @param newAgentCourant
	 *            nc.mairie.metier.agent.Agent
	 */
	private void setAgentCourant(Agent newAgentCourant) {
		agentCourant = newAgentCourant;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (27/03/2003
	 * 15:22:22)
	 * 
	 * @param newAutreParentCourant
	 *            nc.mairie.metier.agent.Agent
	 */
	private void setAutreParentCourant(Agent newAutreParentCourant) {
		autreParentCourant = newAutreParentCourant;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (27/03/2003
	 * 14:31:00)
	 * 
	 * @param newCommuneNaissance
	 *            java.lang.Object
	 */
	private void setCommuneNaissance(java.lang.Object newCommuneNaissance) {
		communeNaissance = newCommuneNaissance;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (27/03/2003
	 * 11:27:55)
	 * 
	 * @param newEnfantCourant
	 *            nc.mairie.metier.agent.Enfant
	 */
	private void setEnfantCourant(Enfant newEnfantCourant) {
		enfantCourant = newEnfantCourant;
	}

	/**
	 * Setter de la liste: LB_NATIONALITE Date de création : (25/03/03 15:33:11)
	 * 
	 */
	private void setLB_NATIONALITE(String[] newLB_NATIONALITE) {
		LB_NATIONALITE = newLB_NATIONALITE;
	}

	/**
	 * Insérez la description de la méthode ici. Date de création : (27/03/2003
	 * 10:55:12)
	 * 
	 * @param newListeEnfants
	 *            ArrayList
	 */
	private void setListeEnfants(ArrayList<Enfant> newListeEnfants) {
		listeEnfants = newListeEnfants;
	}

	/**
	 * Initialisation de la liste des contacts
	 * 
	 * RG_AG_EN_A02 RG_AG_EN_A01
	 */
	private void videZonesDeSaisie(HttpServletRequest request) throws Exception {

		// On vide les zone de saisie
		addZone(getNOM_EF_NOM(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_PRENOM(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_NAISS(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_DECES(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_COMMUNE_NAISS(), Const.CHAINE_VIDE);
		addZone(getNOM_ST_PAYS_NAISS(), Const.CHAINE_VIDE);
		addZone(getNOM_LB_NATIONALITE_SELECT(), "0");
		// RG_AG_EN_A02
		addZone(getNOM_RG_SEXE(), getNOM_RB_SEXE_M());
		// RG_AG_EN_A01
		addZone(getNOM_RG_CHARGE(), getNOM_RB_CHARGE_N());
		addZone(getNOM_ST_AUTRE_PARENT(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_DEBUT_SCOLARITE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_DATE_FIN_SCOLARITE(), Const.CHAINE_VIDE);
		addZone(getNOM_EF_COMMENTAIRE(), Const.CHAINE_VIDE);
		setListeScolarites(null);
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
		return getNOM_EF_NOM();
	}

	/**
	 * @param focus
	 *            focus à définir.
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACHARGE Date de
	 * création : (29/09/08 10:53:21)
	 * 
	 */
	public String getNOM_ST_ACHARGE() {
		return "NOM_ST_ACHARGE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_ACHARGE Date
	 * de création : (29/09/08 10:53:21)
	 * 
	 */
	public String getVAL_ST_ACHARGE() {
		return getZone(getNOM_ST_ACHARGE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_AUTREPARENT Date de
	 * création : (29/09/08 10:53:21)
	 * 
	 */
	public String getNOM_ST_AUTREPARENT() {
		return "NOM_ST_AUTREPARENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_AUTREPARENT
	 * Date de création : (29/09/08 10:53:21)
	 * 
	 */
	public String getVAL_ST_AUTREPARENT() {
		return getZone(getNOM_ST_AUTREPARENT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATEDECES Date de
	 * création : (29/09/08 10:53:21)
	 * 
	 */
	public String getNOM_ST_DATEDECES() {
		return "NOM_ST_DATEDECES";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATEDECES Date
	 * de création : (29/09/08 10:53:21)
	 * 
	 */
	public String getVAL_ST_DATEDECES() {
		return getZone(getNOM_ST_DATEDECES());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATENAISS Date de
	 * création : (29/09/08 10:53:21)
	 * 
	 */
	public String getNOM_ST_DATENAISS() {
		return "NOM_ST_DATENAISS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATENAISS Date
	 * de création : (29/09/08 10:53:21)
	 * 
	 */
	public String getVAL_ST_DATENAISS() {
		return getZone(getNOM_ST_DATENAISS());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_LIEUNAISS Date de
	 * création : (29/09/08 10:53:21)
	 * 
	 */
	public String getNOM_ST_LIEUNAISS() {
		return "NOM_ST_LIEUNAISS";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIEUNAISS Date
	 * de création : (29/09/08 10:53:21)
	 * 
	 */
	public String getVAL_ST_LIEUNAISS() {
		return getZone(getNOM_ST_LIEUNAISS());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NATIONALITE Date de
	 * création : (29/09/08 10:53:21)
	 * 
	 */
	public String getNOM_ST_NATIONALITE() {
		return "NOM_ST_NATIONALITE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NATIONALITE
	 * Date de création : (29/09/08 10:53:21)
	 * 
	 */
	public String getVAL_ST_NATIONALITE() {
		return getZone(getNOM_ST_NATIONALITE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM Date de création
	 * : (29/09/08 10:53:21)
	 * 
	 */
	public String getNOM_ST_NOM() {
		return "NOM_ST_NOM";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM Date de
	 * création : (29/09/08 10:53:21)
	 * 
	 */
	public String getVAL_ST_NOM() {
		return getZone(getNOM_ST_NOM());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_PRENOM Date de
	 * création : (29/09/08 10:53:21)
	 * 
	 */
	public String getNOM_ST_PRENOM() {
		return "NOM_ST_PRENOM";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_PRENOM Date de
	 * création : (29/09/08 10:53:21)
	 * 
	 */
	public String getVAL_ST_PRENOM() {
		return getZone(getNOM_ST_PRENOM());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SCOLARISE Date de
	 * création : (29/09/08 10:53:21)
	 * 
	 */
	public String getNOM_ST_SCOLARISE() {
		return "NOM_ST_SCOLARISE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SCOLARISE Date
	 * de création : (29/09/08 10:53:21)
	 * 
	 */
	public String getVAL_ST_SCOLARISE() {
		return getZone(getNOM_ST_SCOLARISE());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_SEXE Date de
	 * création : (29/09/08 10:53:21)
	 * 
	 */
	public String getNOM_ST_SEXE() {
		return "NOM_ST_SEXE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SEXE Date de
	 * création : (29/09/08 10:53:21)
	 * 
	 */
	public String getVAL_ST_SEXE() {
		return getZone(getNOM_ST_SEXE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_COMMENTAIRE Date de
	 * création : (18/04/11 12:00:12)
	 * 
	 */
	public String getNOM_EF_COMMENTAIRE() {
		return "NOM_EF_COMMENTAIRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_COMMENTAIRE Date de création : (18/04/11 12:00:12)
	 * 
	 */
	public String getVAL_EF_COMMENTAIRE() {
		return getZone(getNOM_EF_COMMENTAIRE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP :
	 * EF_DATE_DEBUT_SCOLARITE Date de création : (18/04/11 12:07:21)
	 * 
	 */
	public String getNOM_EF_DATE_DEBUT_SCOLARITE() {
		return "NOM_EF_DATE_DEBUT_SCOLARITE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_DEBUT_SCOLARITE Date de création : (18/04/11 12:07:21)
	 * 
	 */
	public String getVAL_EF_DATE_DEBUT_SCOLARITE() {
		return getZone(getNOM_EF_DATE_DEBUT_SCOLARITE());
	}

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_DATE_FIN_SCOLARITE
	 * Date de création : (18/04/11 12:07:21)
	 * 
	 */
	public String getNOM_EF_DATE_FIN_SCOLARITE() {
		return "NOM_EF_DATE_FIN_SCOLARITE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_DATE_FIN_SCOLARITE Date de création : (18/04/11 12:07:21)
	 * 
	 */
	public String getVAL_EF_DATE_FIN_SCOLARITE() {
		return getZone(getNOM_EF_DATE_FIN_SCOLARITE());
	}

	public Pays getPaysNaissance() {
		return paysNaissance;
	}

	public void setPaysNaissance(Pays paysNaissance) {
		this.paysNaissance = paysNaissance;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_AJOUTER_SCOLARITE Date de
	 * création : (18/04/11 15:28:48)
	 * 
	 */
	public String getNOM_PB_AJOUTER_SCOLARITE() {
		return "NOM_PB_AJOUTER_SCOLARITE";
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/04/11 15:28:48)
	 * 
	 */
	public boolean performPB_AJOUTER_SCOLARITE(HttpServletRequest request) throws Exception {
		// Récup des zones saisies
		String dateDebutScolarite = getZone(getNOM_EF_DATE_DEBUT_SCOLARITE()).length() == 0 ? null
				: getZone(getNOM_EF_DATE_DEBUT_SCOLARITE());
		String dateFinScolarite = getZone(getNOM_EF_DATE_FIN_SCOLARITE()).length() == 0 ? null
				: getZone(getNOM_EF_DATE_FIN_SCOLARITE());

		if (performControlerSaisieScolarite(request, dateDebutScolarite, dateFinScolarite)) {
			// Affectation des attributs
			setScolariteCourant(new Scolarite());
			getScolariteCourant().setIdEnfant(getEnfantCourant() == null ? null : getEnfantCourant().getIdEnfant());
			getScolariteCourant().setDateDebutScolarite(sdf.parse(dateDebutScolarite));
			getScolariteCourant().setDateFinScolarite(sdf.parse(dateFinScolarite));

			// Ajout à la liste
			getListeScolarites().add(getScolariteCourant());
			rafraichirListeScolarite(request);
		}

		return true;
	}

	/**
	 * Retourne la scolarite courante
	 * 
	 * @return nc.mairie.metier.agent.Scolarite
	 */
	public Scolarite getScolariteCourant() {
		return scolariteCourant;
	}

	/**
	 * Initialise la scolarite courante
	 * 
	 * @param scolariteCourant
	 */
	public void setScolariteCourant(Scolarite scolariteCourant) {
		this.scolariteCourant = scolariteCourant;
	}

	/**
	 * Retourne la liste des scolarites de l'enfant courant
	 * 
	 * @return ArrayList d'objets Scolarite
	 */
	public List<Scolarite> getListeScolarites() {
		if (listeScolarites == null)
			listeScolarites = new ArrayList<Scolarite>();
		return listeScolarites;
	}

	/**
	 * Met à jour la liste des scolarites de l'enfant courant
	 * 
	 * @param listeScolarites
	 */
	private void setListeScolarites(List<Scolarite> listeScolarites) {
		this.listeScolarites = listeScolarites;
	}

	/**
	 * Retourne un objet lien enfant-agent
	 * 
	 * @return LienEnfantAgent
	 */
	private LienEnfantAgent getLienEnfantAgentCourant() {
		return lienEnfantAgentCourant;
	}

	/**
	 * Met à jour le lien enfant-agent
	 * 
	 * @param lienEnfantAgentCourant
	 */
	private void setLienEnfantAgentCourant(LienEnfantAgent lienEnfantAgentCourant) {
		this.lienEnfantAgentCourant = lienEnfantAgentCourant;
	}

	/**
	 * Retourne un objet lien enfant-autreParent
	 * 
	 * @return LienEnfantAgent
	 */
	private LienEnfantAgent getLienEnfantAutreParent() {
		return lienEnfantAutreParent;
	}

	/**
	 * Met à jour le lien enfant-autreParent
	 * 
	 * @param lienEnfantAgentCourant
	 */
	private void setLienEnfantAutreParent(LienEnfantAgent lienEnfantAutreParent) {
		this.lienEnfantAutreParent = lienEnfantAutreParent;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_COMMENTAIRE Date de
	 * création : (08/06/11 11:38:56)
	 * 
	 */
	public String getNOM_ST_COMMENTAIRE() {
		return "NOM_ST_COMMENTAIRE";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_COMMENTAIRE
	 * Date de création : (08/06/11 11:38:56)
	 * 
	 */
	public String getVAL_ST_COMMENTAIRE() {
		return getZone(getNOM_ST_COMMENTAIRE());
	}

	/**
	 * Constructeur du process OeENFANTGestion. Date de création : (03/10/11
	 * 13:44:56)
	 * 
	 */
	public OeENFANTGestion() {
		super();
	}

	public String getNomEcran() {
		return "ECR-AG-DP-ENFANTS";
	}

	/**
	 * Méthode appelée par la servlet qui aiguille le traitement : en fonction
	 * du bouton de la JSP Date de création : (25/03/03 15:33:10)
	 * 
	 */
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si on arrive de la JSP alors on traite le get
		if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

			// Si clic sur le bouton PB_AJOUTER_SCOLARITE
			if (testerParametre(request, getNOM_PB_AJOUTER_SCOLARITE())) {
				return performPB_AJOUTER_SCOLARITE(request);
			}

			// Si clic sur le bouton PB_SUPPRIMER_SCOLARITE
			for (int i = 0; i < getListeScolarites().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER_SCOLARITE(i))) {
					return performPB_SUPPRIMER_SCOLARITE(request, i);
				}
			}

			// Si clic sur le bouton PB_ANNULER
			if (testerParametre(request, getNOM_PB_ANNULER())) {
				return performPB_ANNULER(request);
			}

			// Si clic sur le bouton PB_CREER
			if (testerParametre(request, getNOM_PB_CREER())) {
				return performPB_CREER(request);
			}

			// Si clic sur le bouton PB_VALIDER
			if (testerParametre(request, getNOM_PB_VALIDER())) {
				return performPB_VALIDER(request);
			}

			// Si clic sur le bouton PB_AUTRE_PARENT
			if (testerParametre(request, getNOM_PB_AUTRE_PARENT())) {
				return performPB_AUTRE_PARENT(request);
			}

			// Si clic sur le bouton PB_AUTRE_PARENT_VIRE
			if (testerParametre(request, getNOM_PB_AUTRE_PARENT_VIRE())) {
				return performPB_AUTRE_PARENT_VIRE(request);
			}

			// Si clic sur le bouton PB_LIEU_NAISS
			if (testerParametre(request, getNOM_PB_LIEU_NAISS())) {
				return performPB_LIEU_NAISS(request);
			}

			// Si clic sur le bouton PB_RECHERCHER
			if (testerParametre(request, getNOM_PB_RECHERCHER())) {
				return performPB_RECHERCHER(request);
			}

			// Si clic sur le bouton PB_MODIFIER
			for (int i = 0; i < getListeEnfants().size(); i++) {
				if (testerParametre(request, getNOM_PB_MODIFIER(i))) {
					return performPB_MODIFIER(request, i);
				}
			}

			// Si clic sur le bouton PB_CONSULTER
			for (int i = 0; i < getListeEnfants().size(); i++) {
				if (testerParametre(request, getNOM_PB_CONSULTER(i))) {
					return performPB_CONSULTER(request, i);
				}
			}

			// Si clic sur le bouton PB_SUPPRIMER
			for (int i = 0; i < getListeEnfants().size(); i++) {
				if (testerParametre(request, getNOM_PB_SUPPRIMER(i))) {
					return performPB_SUPPRIMER(request, i);
				}
			}

		}
		// Si TAG INPUT non géré par le process
		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom de la JSP du process Zone à utiliser dans un champ caché
	 * dans chaque formulaire de la JSP. Date de création : (17/10/11 10:58:59)
	 * 
	 */
	public String getJSP() {
		return "OeENFANTGestion.jsp";
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_NOM Date de création
	 * : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_NOM(int i) {
		return "NOM_ST_NOM" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_NOM Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_NOM(int i) {
		return getZone(getNOM_ST_NOM(i));
	}

	/**
	 * Retourne pour la JSP le PRENOM de la zone statique : ST_PRENOM Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_PRENOM(int i) {
		return "NOM_ST_PRENOM" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_PRENOM Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_PRENOM(int i) {
		return getZone(getNOM_ST_PRENOM(i));
	}

	/**
	 * Retourne pour la JSP le SEXE de la zone statique : ST_SEXE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_SEXE(int i) {
		return "NOM_ST_SEXE" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_SEXE Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_SEXE(int i) {
		return getZone(getNOM_ST_SEXE(i));
	}

	/**
	 * Retourne pour la JSP le DATE_NAISS de la zone statique : ST_DATE_NAISS
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_NAISS(int i) {
		return "NOM_ST_DATE_NAISS" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_NAISS
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_NAISS(int i) {
		return getZone(getNOM_ST_DATE_NAISS(i));
	}

	/**
	 * Retourne pour la JSP le LIEU_NAISS de la zone statique : ST_LIEU_NAISS
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_LIEU_NAISS(int i) {
		return "NOM_ST_LIEU_NAISS" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_LIEU_NAISS
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_LIEU_NAISS(int i) {
		return getZone(getNOM_ST_LIEU_NAISS(i));
	}

	public String getNOM_PB_MODIFIER(int i) {
		return "NOM_PB_MODIFIER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (16/08/11 15:48:02)
	 * 
	 */
	public boolean performPB_MODIFIER(HttpServletRequest request, int indiceEltAModifier) throws Exception {

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_MODIFICATION);

		// Récup de l'enfant courant
		Enfant aEnfant = (Enfant) getListeEnfants().get(indiceEltAModifier);
		setEnfantCourant(aEnfant);

		// Affichage de l'enfant courant
		afficheEnfantCourant(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_VISUALISATION Date de
	 * création : (29/09/11 10:03:38)
	 * 
	 */
	public String getNOM_PB_CONSULTER(int i) {
		return "NOM_PB_CONSULTER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (29/09/11 10:03:38)
	 * 
	 */
	public boolean performPB_CONSULTER(HttpServletRequest request, int indiceEltAConsulter) throws Exception {
		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_CONSULTATION);

		// Récup de l'enfant courant
		Enfant aEnfant = (Enfant) getListeEnfants().get(indiceEltAConsulter);
		setEnfantCourant(aEnfant);

		// Affichage de l'enfant courant
		afficheEnfantSuppression(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMMER Date de création
	 * : (05/09/11 11:31:37)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER(int i) {
		return "NOM_PB_SUPPRIMER" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
	 * 
	 */
	public boolean performPB_SUPPRIMER(HttpServletRequest request, int indiceEltASuprimer) throws Exception {

		// Si pas d'agent courant alors erreur
		if (getAgentCourant() == null) {
			// "ERR004","Vous devez d'abord rechercher un agent"
			getTransaction().declarerErreur(MessageUtils.getMessage("ERR004"));
			return false;
		}

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), Const.CHAINE_VIDE);

		// On nomme l'action
		addZone(getNOM_ST_ACTION(), ACTION_SUPPRESSION);

		// Récup de l'enfant courant
		Enfant aEnfant = (Enfant) getListeEnfants().get(indiceEltASuprimer);
		setEnfantCourant(aEnfant);

		// Affichage de l'enfant courant
		afficheEnfantSuppression(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_DEBUT Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_DEBUT(int i) {
		return "NOM_ST_DATE_DEBUT" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_DEBUT
	 * Date de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_DEBUT(int i) {
		return getZone(getNOM_ST_DATE_DEBUT(i));
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_DATE_FIN Date de
	 * création : (18/08/11 10:21:15)
	 * 
	 */
	public String getNOM_ST_DATE_FIN(int i) {
		return "NOM_ST_DATE_FIN" + i;
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_FIN Date
	 * de création : (18/08/11 10:21:15)
	 * 
	 */
	public String getVAL_ST_DATE_FIN(int i) {
		return getZone(getNOM_ST_DATE_FIN(i));
	}

	/**
	 * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_SCOLARITE Date de
	 * création : (18/04/11 15:28:48)
	 * 
	 */
	public String getNOM_PB_SUPPRIMER_SCOLARITE(int i) {
		return "NOM_PB_SUPPRIMER_SCOLARITE" + i;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * règles de gestion du process - Positionne un statut en fonction de ces
	 * règles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (18/04/11 15:28:48)
	 * 
	 */
	public boolean performPB_SUPPRIMER_SCOLARITE(HttpServletRequest request, int elemASupprimer) throws Exception {

		// Suppression de la scolarité
		getListeScolarites().remove(elemASupprimer);
		rafraichirListeScolarite(request);

		setStatut(STATUT_MEME_PROCESS);
		return true;
	}

	public ScolariteDao getScolariteDao() {
		return scolariteDao;
	}

	public void setScolariteDao(ScolariteDao scolariteDao) {
		this.scolariteDao = scolariteDao;
	}

	public LienEnfantAgentDao getLienEnfantAgentDao() {
		return lienEnfantAgentDao;
	}

	public void setLienEnfantAgentDao(LienEnfantAgentDao lienEnfantAgentDao) {
		this.lienEnfantAgentDao = lienEnfantAgentDao;
	}

	public EnfantDao getEnfantDao() {
		return enfantDao;
	}

	public void setEnfantDao(EnfantDao enfantDao) {
		this.enfantDao = enfantDao;
	}

	public AgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(AgentDao agentDao) {
		this.agentDao = agentDao;
	}

}
