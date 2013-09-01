package nc.mairie.gestionagent.process.pointage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.ConsultPointageDto;
import nc.mairie.gestionagent.dto.RefEtatDto;
import nc.mairie.gestionagent.dto.RefTypePointageDto;
import nc.mairie.metier.Const;
import nc.mairie.metier.agent.AgentNW;
import nc.mairie.metier.droits.Siidma;
import nc.mairie.metier.poste.Service;
import nc.mairie.spring.ws.SirhPtgWSConsumer;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.FormateListe;
import nc.mairie.technique.Services;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;
import nc.mairie.utils.MairieUtils;
import nc.mairie.utils.MessageUtils;
import nc.mairie.utils.TreeHierarchy;
import nc.mairie.utils.VariablesActivite;

/**
 * Process OeAGENTAccidentTravail Date de création : (30/06/11 13:56:32)
 *
 */
public class OePTGVisualisation extends BasicProcess {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final int STATUT_RECHERCHER_AGENT_MAX = 2;
    public static final int STATUT_RECHERCHER_AGENT_MIN = 1;
    public static final int STATUT_SAISIE_PTG = 3;
    public Hashtable<String, TreeHierarchy> hTree = null;
    private String[] LB_ETAT;
    private String[] LB_TYPE;
    private ArrayList<RefEtatDto> listeEtats;
    private HashMap<Integer, ConsultPointageDto> listePointage;
    private ArrayList<Service> listeServices;
    private ArrayList<RefTypePointageDto> listeTypes;
    private HashMap<Integer, List<ConsultPointageDto>> history = new HashMap<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat hrs = new SimpleDateFormat("HH:mm");
    private AgentNW loggedAgent;

    private void afficheListePointages() {

        for (ConsultPointageDto ptg : getListePointage().values()) {
            Integer i = ptg.getIdPointage();
            AgentDto agtPtg = ptg.getAgent();

            addZone(getNOM_ST_AGENT(i), agtPtg.getNom() + " " + agtPtg.getPrenom() + " (" + agtPtg.getIdAgent().toString().substring(3, agtPtg.getIdAgent().toString().length()) + ")   ");
            addZone(getMATRICULE_ST_AGENT(i), agtPtg.getIdAgent().toString().substring(3, agtPtg.getIdAgent().toString().length()));
            addZone(getNOM_ST_TYPE(i), ptg.getTypePointage());
            //  System.out.println("recu :"+ptg.getDate()+"-->"+sdf.format(ptg.getDate()));
            addZone(getNOM_ST_DATE(i), sdf.format(ptg.getDate()));
            addZone(getNOM_ST_DATE_DEB(i), hrs.format(ptg.getDebut()));
            if (ptg.getFin() != null) {
                addZone(getNOM_ST_DATE_FIN(i), hrs.format(ptg.getFin()));
            }
            addZone(getNOM_ST_DUREE(i), ptg.getQuantite());
            addZone(getNOM_ST_MOTIF(i), ptg.getMotif() + " - " + ptg.getCommentaire());
            addZone(getNOM_ST_ETAT(i), EtatPointageEnum.getEtatPointageEnum(ptg.getIdRefEtat()).name());
            addZone(getNOM_ST_DATE_SAISIE(i), sdf.format(ptg.getDateSaisie()) + " à " + hrs.format(ptg.getDateSaisie()));
        }
    }

    /**
     * Retourne une hashTable de la hiérarchie des Service selon le code
     * Service.
     *
     * @return hTree
     */
    public Hashtable<String, TreeHierarchy> getHTree() {
        return hTree;
    }

    @Override
    public String getJSP() {
        return "OePTGVisualisation.jsp";
    }

    /**
     * Getter de la liste avec un lazy initialize : LB_ETAT Date de création :
     * (28/11/11)
     *
     */
    private String[] getLB_ETAT() {
        if (LB_ETAT == null) {
            LB_ETAT = initialiseLazyLB();
        }
        return LB_ETAT;
    }

    /**
     * Getter de la liste avec un lazy initialize : LB_TYPE Date de création :
     * (28/11/11)
     *
     */
    private String[] getLB_TYPE() {
        if (LB_TYPE == null) {
            LB_TYPE = initialiseLazyLB();
        }
        return LB_TYPE;
    }

    public ArrayList<RefEtatDto> getListeEtats() {
        return listeEtats;
    }

    public HashMap<Integer, ConsultPointageDto> getListePointage() {
        return listePointage == null ? new HashMap<Integer, ConsultPointageDto>() : listePointage;
    }

    /**
     * Retourne la liste des services.
     *
     * @return listeServices
     */
    public ArrayList<Service> getListeServices() {
        return listeServices;
    }

    public ArrayList<RefTypePointageDto> getListeTypes() {
        return listeTypes;
    }

    /**
     * Retourne le nom d'une zone de saisie pour la JSP : EF_SERVICE Date de
     * création : (13/09/11 11:47:15)
     *
     */
    public String getNOM_EF_SERVICE() {
        return "NOM_EF_SERVICE";
    }

    /**
     * Retourne le nom de la zone pour la JSP : NOM_LB_ETAT Date de création :
     * (28/11/11)
     *
     */
    public String getNOM_LB_ETAT() {
        return "NOM_LB_ETAT";
    }

    /**
     * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
     * NOM_LB_ETAT_SELECT Date de création : (28/11/11)
     *
     */
    public String getNOM_LB_ETAT_SELECT() {
        return "NOM_LB_ETAT_SELECT";
    }

    /**
     * Retourne le nom de la zone pour la JSP : NOM_LB_TYPE Date de création :
     * (28/11/11)
     *
     */
    public String getNOM_LB_TYPE() {
        return "NOM_LB_TYPE";
    }

    /**
     * Retourne le nom de la zone de la ligne sélectionnée pour la JSP :
     * NOM_LB_TYPE_SELECT Date de création : (28/11/11)
     *
     */
    public String getNOM_LB_TYPE_SELECT() {
        return "NOM_LB_TYPE_SELECT";
    }

    /**
     * Retourne le nom d'un bouton pour la JSP : PB_VALIDER Date de création :
     * (05/09/11 11:31:37)
     *
     */
    public String getNOM_PB_FILTRER() {
        return "NOM_PB_FILTRER";
    }

    /**
     * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_AGENT_MAX Date de
     * création : (02/08/11 09:42:00)
     *
     */
    public String getNOM_PB_RECHERCHER_AGENT_MAX() {
        return "NOM_PB_RECHERCHER_AGENT_MAX";
    }

    /**
     * Retourne le nom d'un bouton pour la JSP : PB_RECHERCHER_AGENT_MIN Date de
     * création : (02/08/11 09:42:00)
     *
     */
    public String getNOM_PB_RECHERCHER_AGENT_MIN() {
        return "NOM_PB_RECHERCHER_AGENT_MIN";
    }

    /**
     * Retourne le nom d'un bouton pour la JSP :
     * PB_SUPPRIMER_RECHERCHER_AGENT_MAX Date de création : (13/07/11 09:49:02)
     *
     *
     */
    public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX() {
        return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX";
    }

    /**
     * Retourne le nom d'un bouton pour la JSP :
     * PB_SUPPRIMER_RECHERCHER_AGENT_MIN Date de création : (13/07/11 09:49:02)
     *
     *
     */
    public String getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN() {
        return "NOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN";
    }

    /**
     * Retourne le nom d'un bouton pour la JSP : PB_SUPPRIMER_RECHERCHER_SERVICE
     * Date de création : (13/07/11 09:49:02)
     *
     *
     */
    public String getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE() {
        return "NOM_PB_SUPPRIMER_RECHERCHER_SERVICE";
    }

    /**
     * Retourne pour la JSP le nom de la zone statique : ST_AGENT Date de
     * création : (21/11/11 09:55:36)
     *
     */
    public String getNOM_ST_AGENT(int i) {
        return "NOM_ST_AGENT_" + i;
    }

    /**
     * Retourne pour la JSP le nom de la zone statique : ST_AGENT_MAX Date de
     * création : (02/08/11 09:40:42)
     *
     */
    public String getNOM_ST_AGENT_MAX() {
        return "NOM_ST_AGENT_MAX";
    }

    /**
     * Retourne pour la JSP le nom de la zone statique : ST_AGENT_MIN Date de
     * création : (02/08/11 09:40:42)
     *
     */
    public String getNOM_ST_AGENT_MIN() {
        return "NOM_ST_AGENT_MIN";
    }

    /**
     * Retourne pour la JSP le nom de la zone statique : ST_CODE_SERVICE Date de
     * création : (13/09/11 08:45:29)
     *
     */
    public String getNOM_ST_CODE_SERVICE() {
        return "NOM_ST_CODE_SERVICE";
    }

    /**
     * Retourne pour la JSP le nom de la zone statique : ST_DATE Date de
     * création : (21/11/11 09:55:36)
     *
     */
    public String getNOM_ST_DATE(int i) {
        return "NOM_ST_DATE_" + i;
    }

    /**
     * Retourne pour la JSP le nom de la zone statique : ST_DATE_CAP Date de
     * création : (21/11/11 09:55:36)
     *
     */
    public String getNOM_ST_DATE_DEB(int i) {
        return "getNOM_ST_DATE_DEB" + i;
    }

    /**
     * Retourne pour la JSP le nom de la zone statique
     *
     */
    public String getNOM_ST_DATE_FIN(int i) {
        return "getNOM_ST_DATE_FIN" + i;
    }

    /**
     * Retourne pour la JSP le nom de la zone statique : ST_DATE_CAP Date de
     * création : (21/11/11 09:55:36)
     *
     */
    public String getNOM_ST_DATE_MAX() {
        return "NOM_ST_DATE_MAX";
    }

    /**
     * Retourne pour la JSP le nom de la zone statique : ST_DATE_CAP Date de
     * création : (21/11/11 09:55:36)
     *
     */
    public String getNOM_ST_DATE_MIN() {
        return "NOM_ST_DATE_MIN";
    }

    /**
     * Retourne pour la JSP le nom de la zone statique
     *
     */
    public String getNOM_ST_DATE_SAISIE(int i) {
        return "getNOM_ST_DATE_SAISIE" + i;
    }

    /**
     * Retourne pour la JSP le nom de la zone statique
     *
     */
    public String getNOM_ST_DUREE(int i) {
        return "getNOM_ST_DUREE" + i;
    }

    /**
     * Retourne pour la JSP le nom de la zone statique
     *
     */
    public String getNOM_ST_ETAT(int i) {
        return "getNOM_ST_ETAT" + i;
    }

    /**
     * Retourne pour la JSP le nom de la zone statique
     *
     */
    public String getNOM_ST_MOTIF(int i) {
        return "getNOM_ST_MOTIF" + i;
    }

    /**
     * Retourne pour la JSP le nom de la zone statique : ST_TYPE Date de
     * création : (21/11/11 09:55:36)
     *
     */
    public String getNOM_ST_TYPE(int i) {
        return "NOM_ST_TYPE_" + i;
    }

    public String getMATRICULE_ST_AGENT(int i) {
        return "NOM_ST_MATRICULE_" + i;
    }

    /**
     * Getter du nom de l'écran (pour la gestion des droits)
     */
    public String getNomEcran() {
        return "ECR-PTG-VISU";
    }

    public String getVal_Del(int i) {
        return "Delete_F" + i;
    }

    /**
     * - Traite et affecte les zones saisies dans la JSP. - Implémente les
     * règles de gestion du process - Positionne un statut en fonction de ces
     * règles : setStatut(STATUT, boolean veutRetour) ou
     * setStatut(STATUT,Message d'erreur) Date de création : (13/07/11 09:49:02)
     *
     *
     */
    public String getVal_DelAll() {
        return "Delete_All";
    }

    public String getVal_Delay(int i) {
        return "Delay_F" + i;
    }

    public String getVal_DelayAll() {
        return "Delay_All";
    }

    /**
     * Retourne la valeur à afficher par la JSP pour la zone de saisie :
     * EF_SERVICE Date de création : (13/09/11 11:47:15)
     *
     */
    public String getVAL_EF_SERVICE() {
        return getZone(getNOM_EF_SERVICE());
    }

    /**
     * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
     * JSP : LB_ETAT Date de création : (28/11/11 09:55:36)
     *
     */
    public String[] getVAL_LB_ETAT() {
        return getLB_ETAT();
    }

    /**
     * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
     * la JSP : LB_ETAT Date de création : (28/11/11)
     *
     */
    public String getVAL_LB_ETAT_SELECT() {
        return getZone(getNOM_LB_ETAT_SELECT());
    }

    /**
     * Méthode à personnaliser Retourne la valeur à afficher pour la zone de la
     * JSP : LB_TYPE Date de création : (28/11/11 09:55:36)
     *
     */
    public String[] getVAL_LB_TYPE() {
        return getLB_TYPE();
    }

    /**
     * Méthode à personnaliser Retourne l'indice à sélectionner pour la zone de
     * la JSP : LB_TYPE Date de création : (28/11/11)
     *
     */
    public String getVAL_LB_TYPE_SELECT() {
        return getZone(getNOM_LB_TYPE_SELECT());
    }

    /**
     * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT Date de
     * création : (21/11/11 09:55:36)
     *
     */
    public String getVAL_ST_AGENT(int i) {
        return getZone(getNOM_ST_AGENT(i));
    }

    /**
     *
     */
    public String getSAISIE_PTG(int i) {
        return "getSAISIE_PTG_" + i;
    }

    /**
     * Retourne la valeur à afficher par la JSP pour la zone
     *
     */
    public String getVAL_MATRICULE_AGENT(int i) {
        return getZone(getMATRICULE_ST_AGENT(i));
    }

    /**
     * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT_MAX Date
     * de création : (02/08/11 09:40:42)
     *
     */
    public String getVAL_ST_AGENT_MAX() {
        return getZone(getNOM_ST_AGENT_MAX());
    }

    /**
     * Retourne la valeur à afficher par la JSP pour la zone : ST_AGENT_MIN Date
     * de création : (02/08/11 09:40:42)
     *
     */
    public String getVAL_ST_AGENT_MIN() {
        return getZone(getNOM_ST_AGENT_MIN());
    }

    /**
     * Retourne la valeur à afficher par la JSP pour la zone : ST_CODE_SERVICE
     * Date de création : (13/09/11 08:45:29)
     *
     */
    public String getVAL_ST_CODE_SERVICE() {
        return getZone(getNOM_ST_CODE_SERVICE());
    }

    /**
     * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE Date de
     * création : (21/11/11 09:55:36)
     *
     */
    public String getVAL_ST_DATE(int i) {
        return getZone(getNOM_ST_DATE(i));
    }

    /**
     * Retourne la valeur à afficher par la JSP pour la zone
     *
     */
    public String getVAL_ST_DATE_DEB(int i) {
        return getZone(getNOM_ST_DATE_DEB(i));
    }

    /**
     * Retourne la valeur à afficher par la JSP pour la zone
     *
     */
    public String getVAL_ST_DATE_FIN(int i) {
        return getZone(getNOM_ST_DATE_FIN(i));
    }

    /**
     * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_CAP Date
     * de création : (21/11/11 09:55:36)
     *
     */
    public String getVAL_ST_DATE_MAX() {
        return getZone(getNOM_ST_DATE_MAX());
    }

    /**
     * Retourne la valeur à afficher par la JSP pour la zone : ST_DATE_CAP Date
     * de création : (21/11/11 09:55:36)
     *
     */
    public String getVAL_ST_DATE_MIN() {
        return getZone(getNOM_ST_DATE_MIN());
    }

    /**
     * Retourne la valeur à afficher par la JSP pour la zone
     *
     */
    public String getVAL_ST_DATE_SAISIE(int i) {
        return getZone(getNOM_ST_DATE_SAISIE(i));
    }

    /**
     * Retourne la valeur à afficher par la JSP pour la zone
     *
     */
    public String getVAL_ST_DUREE(int i) {
        return getZone(getNOM_ST_DUREE(i));
    }

    /**
     * Retourne la valeur à afficher par la JSP pour la zone
     *
     */
    public String getVAL_ST_ETAT(int i) {
        return getZone(getNOM_ST_ETAT(i));
    }

    /**
     * Retourne la valeur à afficher par la JSP pour la zone
     *
     */
    public String getVAL_ST_MOTIF(int i) {
        return getZone(getNOM_ST_MOTIF(i));
    }

    /**
     * Retourne la valeur à afficher par la JSP pour la zone : ST_TYPE Date de
     * création : (21/11/11 09:55:36)
     *
     */
    public String getVAL_ST_TYPE(int i) {
        return getZone(getNOM_ST_TYPE(i));
    }

    public String getVal_Valid(int i) {
        return "Valid_" + i;
    }

    public String getVal_ValidAll() {
        return "Valid_All";
    }

    public String getValHistory(int id) {
        return "History_" + id;
    }

    /**
     * Initialisation des liste déroulantes de l'écran convocation du suivi
     * médical.
     */
    private void initialiseListeDeroulante() throws Exception {
        SirhPtgWSConsumer t = new SirhPtgWSConsumer();
        // Si liste etat vide alors affectation
        if (getLB_ETAT() == LBVide) {
            List<RefEtatDto> etats = t.getEtatsPointage();
            setListeEtats((ArrayList<RefEtatDto>) etats);
            int[] tailles = {50};
            FormateListe aFormat = new FormateListe(tailles);
            for (RefEtatDto etat : etats) {
                String ligne[] = {etat.getLibelle()};
                aFormat.ajouteLigne(ligne);
            }
            setLB_ETAT(aFormat.getListeFormatee(true));
            addZone(getNOM_LB_ETAT_SELECT(), Const.ZERO);

        }
        // Si liste type vide alors affectation
        if (getLB_TYPE() == LBVide) {
            List<RefTypePointageDto> types = t.getTypesPointage();
            setListeTypes((ArrayList<RefTypePointageDto>) types);

            int[] tailles = {50};
            FormateListe aFormat = new FormateListe(tailles);
            for (RefTypePointageDto type : types) {
                String ligne[] = {type.getLibelle()};
                aFormat.ajouteLigne(ligne);
            }
            setLB_TYPE(aFormat.getListeFormatee(true));
            addZone(getNOM_LB_TYPE_SELECT(), Const.ZERO);

        }
        // Si la liste des services est nulle
        if (getListeServices() == null || getListeServices().isEmpty()) {
            ArrayList<Service> services = Service.listerServiceActif(getTransaction());
            setListeServices(services);

            // Tri par codeservice
            Collections.sort(getListeServices(), new Comparator<Object>() {
                public int compare(Object o1, Object o2) {
                    Service s1 = (Service) o1;
                    Service s2 = (Service) o2;
                    return (s1.getCodService().compareTo(s2.getCodService()));
                }
            });

            // alim de la hTree
            hTree = new Hashtable<>();
            TreeHierarchy parent = null;
            for (int i = 0; i < getListeServices().size(); i++) {
                Service serv = (Service) getListeServices().get(i);

                if (Const.CHAINE_VIDE.equals(serv.getCodService())) {
                    continue;
                }

                // recherche du supérieur
                String codeService = serv.getCodService();
                while (codeService.endsWith("A")) {
                    codeService = codeService.substring(0, codeService.length() - 1);
                }
                codeService = codeService.substring(0, codeService.length() - 1);
                codeService = Services.rpad(codeService, 4, "A");
                parent = hTree.get(codeService);
                int indexParent = (parent == null ? 0 : parent.getIndex());
                hTree.put(serv.getCodService(), new TreeHierarchy(serv, i, indexParent));

            }
        }

    }

    @Override
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

        // Initialisation des listes déroulantes
        initialiseListeDeroulante();
        if (etatStatut() == STATUT_SAISIE_PTG) {
            performPB_FILTRER();
        }
        if (etatStatut() == STATUT_RECHERCHER_AGENT_MIN) {
            AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
            VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
            if (agt != null) {
                addZone(getNOM_ST_AGENT_MIN(), agt.getNoMatricule());
            }
        }
        if (etatStatut() == STATUT_RECHERCHER_AGENT_MAX) {
            AgentNW agt = (AgentNW) VariablesActivite.recuperer(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
            VariablesActivite.enlever(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE);
            if (agt != null) {
                addZone(getNOM_ST_AGENT_MAX(), agt.getNoMatricule());
            }
        }
        UserAppli uuser = (UserAppli) VariableGlobale.recuperer(request, VariableGlobale.GLOBAL_USER_APPLI);
        if (!uuser.getUserName().equals("nicno85") && !uuser.getUserName().equals("levch80")) {
            Siidma user = Siidma.chercherSiidma(getTransaction(), uuser.getUserName().toUpperCase());
            if (getTransaction().isErreur()) {
                getTransaction().traiterErreur();
            }
            if (user != null && user.getNomatr() != null) {
                loggedAgent = AgentNW.chercherAgentParMatricule(getTransaction(), user.getNomatr());
            }
        } else {
            loggedAgent = AgentNW.chercherAgentParMatricule(getTransaction(), "5138");
        }
    }

    private boolean performControlerFiltres() {
        String dateDeb = getVAL_ST_DATE_MIN();
        if (dateDeb.equals(Const.CHAINE_VIDE)) {
            return false;
        }
        return true;
    }

    /**
     * - Traite et affecte les zones saisies dans la JSP. - Implémente les
     * règles de gestion du process - Positionne un statut en fonction de ces
     * règles : setStatut(STATUT, boolean veutRetour) ou
     * setStatut(STATUT,Message d'erreur) Date de création : (05/09/11 11:31:37)
     *
     */
    public boolean performPB_FILTRER() throws Exception {
        if (!performControlerFiltres()) {
            // "ERR500",
            // "Le champ date de début est obligatoire."
            getTransaction().declarerErreur(MessageUtils.getMessage("ERR500"));
            return false;
        }
        SirhPtgWSConsumer t = new SirhPtgWSConsumer();

        String dateDeb = getVAL_ST_DATE_MIN();
        String dateMin = Services.convertitDate(dateDeb, "dd/MM/yyyy", "yyyyMMdd");

        String dateFin = getVAL_ST_DATE_MAX();
        String dateMax = null;
        if (!dateFin.equals(Const.CHAINE_VIDE)) {
            dateMax = Services.convertitDate(dateFin, "dd/MM/yyyy", "yyyyMMdd");
        } else {
            //     dateMax = new SimpleDateFormat("yyyyMMdd").format(new Date());
            dateMax = dateMin;
            addZone(getNOM_ST_DATE_MAX(), getVAL_ST_DATE_MIN());
        }
        // etat
        int numEtat = (Services.estNumerique(getZone(getNOM_LB_ETAT_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_ETAT_SELECT())) : -1);
        RefEtatDto etat = null;
        if (numEtat != -1 && numEtat != 0) {
            etat = (RefEtatDto) getListeEtats().get(numEtat - 1);
        }
        // type
        int numType = (Services.estNumerique(getZone(getNOM_LB_TYPE_SELECT())) ? Integer.parseInt(getZone(getNOM_LB_TYPE_SELECT())) : -1);
        RefTypePointageDto type = null;
        if (numType != -1 && numType != 0) {
            type = (RefTypePointageDto) getListeTypes().get(numType - 1);
        }
        String idAgentMin = getVAL_ST_AGENT_MIN().equals(Const.CHAINE_VIDE) ? null : "900" + getVAL_ST_AGENT_MIN();

        if (getVAL_ST_AGENT_MAX().equals(Const.CHAINE_VIDE)) {
            addZone(getNOM_ST_AGENT_MAX(), getVAL_ST_AGENT_MIN());
        }

        String idAgentMax = getVAL_ST_AGENT_MAX().equals(Const.CHAINE_VIDE) ? null : "900" + getVAL_ST_AGENT_MAX();

        // si superieur à 1000 alors on bloque
        List<String> idAgents = new ArrayList<String>();

        if (idAgentMin != null && idAgentMax == null) {
            if (!idAgents.contains(idAgentMin)) {
                idAgents.add(idAgentMin);
            }
        } else if (idAgentMin != null && idAgentMax != null) {
            ArrayList<AgentNW> listAgent = AgentNW.listerAgentEntreDeuxIdAgent(getTransaction(), Integer.valueOf(idAgentMin), Integer.valueOf(idAgentMax));
            for (AgentNW ag : listAgent) {
                if (!idAgents.contains(Integer.valueOf(ag.getIdAgent()))) {
                    idAgents.add(ag.getIdAgent());
                }
            }
        }

        String codeService = getVAL_ST_CODE_SERVICE();
        if (!codeService.equals(Const.CHAINE_VIDE)) {
            ArrayList<AgentNW> listAgent = AgentNW.listerAgentAvecServiceCommencant(getTransaction(), codeService);
            for (AgentNW ag : listAgent) {
                if (!idAgents.contains(Integer.valueOf(ag.getIdAgent()))) {
                    idAgents.add(ag.getIdAgent());
                }
            }
        }

        if (idAgents.isEmpty()) {
            idAgents = null;
        } else if (idAgents.size() >= 1000) {
            // "ERR501",
            // "La sélection des filtres engendre plus de 1000 agents. Merci de réduire la sélection."
            getTransaction().declarerErreur(MessageUtils.getMessage("ERR501"));
            return false;
        }

        List<ConsultPointageDto> _listePointage = t.getVisualisationPointage(dateMin, dateMax, idAgents, etat != null ? etat.getIdRefEtat() : null, type != null ? type.getIdRefTypePointage() : null);
        setListePointage((ArrayList<ConsultPointageDto>) _listePointage);
        loadHistory();

        afficheListePointages();

        return true;
    }

    public boolean performPB_VALID(HttpServletRequest request) throws Exception {
        changeState(getListePointage().values(), EtatPointageEnum.APPROUVE);
        return true;
    }

    public boolean performPB_VALID(HttpServletRequest request, int i) throws Exception {
        changeState(getListePointage().get(i), EtatPointageEnum.APPROUVE);
        return true;
    }

    public boolean performPB_DEL(HttpServletRequest request) throws Exception {
        changeState(getListePointage().values(), EtatPointageEnum.REJETE);
        return true;
    }

    public boolean performPB_DEL(HttpServletRequest request, int i) throws Exception {
        changeState(getListePointage().get(i), EtatPointageEnum.REJETE);
        return true;
    }

    public boolean performPB_DELAY(HttpServletRequest request) throws Exception {
        changeState(getListePointage().values(), EtatPointageEnum.EN_ATTENTE);
        return true;
    }

    public boolean performPB_DELAY(HttpServletRequest request, int i) throws Exception {
        changeState(getListePointage().get(i), EtatPointageEnum.EN_ATTENTE);
        return true;
    }

    private void changeState(ConsultPointageDto ptg, EtatPointageEnum state) {
        ArrayList<ConsultPointageDto> param = new ArrayList<>();
        param.add(ptg);
        changeState(param, state);
    }

    private void changeState(Collection<ConsultPointageDto> ptg, EtatPointageEnum state) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (ConsultPointageDto pt : ptg) {
            ids.add(pt.getIdPointage());
            refreshHistory(pt.getIdPointage());
        }
        SirhPtgWSConsumer t = new SirhPtgWSConsumer();
        if (getLoggedAgent() == null) {
            System.out.println("Agent complètement nul!");

        } else {
            t.setPtgState(ids, state.ordinal(), getLoggedAgent().getIdAgent());
            try {
                performPB_FILTRER();
            } catch (Exception e) {
                System.out.println("Exception in performPB_FILTRER");
            }
        }
    }

    /**
     * - Traite et affecte les zones saisies dans la JSP. - Implémente les
     * règles de gestion du process - Positionne un statut en fonction de ces
     * règles : setStatut(STATUT, boolean veutRetour) ou
     * setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:42:00)
     *
     */
    public boolean performPB_RECHERCHER_AGENT_MAX(HttpServletRequest request) throws Exception {

        // On met l'agent courant en var d'activité
        VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());

        setStatut(STATUT_RECHERCHER_AGENT_MAX, true);
        return true;
    }

    /**
     * - Traite et affecte les zones saisies dans la JSP. - Implémente les
     * règles de gestion du process - Positionne un statut en fonction de ces
     * règles : setStatut(STATUT, boolean veutRetour) ou
     * setStatut(STATUT,Message d'erreur) Date de création : (02/08/11 09:42:00)
     *
     */
    public boolean performPB_RECHERCHER_AGENT_MIN(HttpServletRequest request) throws Exception {
        // On met l'agent courant en var d'activité
        VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_MAIRIE, new AgentNW());

        setStatut(STATUT_RECHERCHER_AGENT_MIN, true);
        return true;
    }

    /**
     * - Traite et affecte les zones saisies dans la JSP. - Implémente les
     * règles de gestion du process - Positionne un statut en fonction de ces
     * règles : setStatut(STATUT, boolean veutRetour) ou
     * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
     *
     */
    public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_MAX(HttpServletRequest request) throws Exception {
        // On enlève l'agent selectionnée
        addZone(getNOM_ST_AGENT_MAX(), Const.CHAINE_VIDE);
        return true;
    }

    /**
     * - Traite et affecte les zones saisies dans la JSP. - Implémente les
     * règles de gestion du process - Positionne un statut en fonction de ces
     * règles : setStatut(STATUT, boolean veutRetour) ou
     * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
     *
     */
    public boolean performPB_SUPPRIMER_RECHERCHER_AGENT_MIN(HttpServletRequest request) throws Exception {
        // On enlève l'agent selectionnée
        addZone(getNOM_ST_AGENT_MIN(), Const.CHAINE_VIDE);
        return true;
    }

    /**
     * - Traite et affecte les zones saisies dans la JSP. - Implémente les
     * règles de gestion du process - Positionne un statut en fonction de ces
     * règles : setStatut(STATUT, boolean veutRetour) ou
     * setStatut(STATUT,Message d'erreur) Date de création : (25/03/03 15:33:11)
     *
     */
    public boolean performPB_SUPPRIMER_RECHERCHER_SERVICE(HttpServletRequest request) throws Exception {
        // On enlève le service selectionnée
        addZone(getNOM_ST_CODE_SERVICE(), Const.CHAINE_VIDE);
        addZone(getNOM_EF_SERVICE(), Const.CHAINE_VIDE);
        return true;
    }

    @Override
    public boolean recupererStatut(HttpServletRequest request) throws Exception {

        // Si on arrive de la JSP alors on traite le get
        if (request.getParameter("JSP") != null && request.getParameter("JSP").equals(getJSP())) {

            // Si clic sur le bouton PB_FILTRER
            if (testerParametre(request, getNOM_PB_FILTRER())) {
                return performPB_FILTRER();
            }

            // Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_SERVICE
            if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_SERVICE())) {
                return performPB_SUPPRIMER_RECHERCHER_SERVICE(request);
            }

            // Si clic sur le bouton PB_RECHERCHER_AGENT_MIN
            if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_MIN())) {
                return performPB_RECHERCHER_AGENT_MIN(request);
            }

            // Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT_MIN
            if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MIN())) {
                return performPB_SUPPRIMER_RECHERCHER_AGENT_MIN(request);
            }

            // Si clic sur le bouton PB_RECHERCHER_AGENT_MAX
            if (testerParametre(request, getNOM_PB_RECHERCHER_AGENT_MAX())) {
                return performPB_RECHERCHER_AGENT_MAX(request);
            }

            // Si clic sur le bouton PB_SUPPRIMER_RECHERCHER_AGENT_MAX
            if (testerParametre(request, getNOM_PB_SUPPRIMER_RECHERCHER_AGENT_MAX())) {
                return performPB_SUPPRIMER_RECHERCHER_AGENT_MAX(request);
            }

            if (testerParametre(request, getVal_ValidAll())) {
                return performPB_VALID(request);
            }

            if (testerParametre(request, getVal_DelayAll())) {
                return performPB_DELAY(request);
            }

            if (testerParametre(request, getVal_DelAll())) {
                return performPB_DEL(request);
            }

            for (int i : getListePointage().keySet()) {
                if (testerParametre(request, getVal_Del(i))) {
                    return performPB_DEL(request, i);
                }
                if (testerParametre(request, getVal_Valid(i))) {
                    return performPB_VALID(request, i);
                }
                if (testerParametre(request, getVal_Delay(i))) {
                    return performPB_DELAY(request, i);
                }
                if (testerParametre(request, getSAISIE_PTG(i))) {
                    //System.out.println("enreg visu agent :"+getVAL_MATRICULE_AGENT(i));
                    //System.out.println("enreg visu lundi :"+getLundi(i));
                    VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_AGENT_PTG, getVAL_MATRICULE_AGENT(i));
                    VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_LUNDI_PTG, getLundi(i));
                    // TODO
                    setStatut(STATUT_SAISIE_PTG, true);
                    return true;
                }
            }
        }
        // Si TAG INPUT non géré par le process
        setStatut(STATUT_MEME_PROCESS);
        return true;
    }

    /**
     * Setter de la liste: LB_ETAT Date de création : (28/11/11)
     *
     */
    private void setLB_ETAT(String[] newLB_ETAT) {
        LB_ETAT = newLB_ETAT;
    }

    /**
     * Setter de la liste: LB_TYPE Date de création : (28/11/11)
     *
     */
    private void setLB_TYPE(String[] newLB_TYPE) {
        LB_TYPE = newLB_TYPE;
    }

    public void setListeEtats(ArrayList<RefEtatDto> listeEtats) {
        this.listeEtats = listeEtats;
    }

    public void setListePointage(ArrayList<ConsultPointageDto> _listePointage) {
        listePointage = new HashMap<>();
        for (ConsultPointageDto ptg : _listePointage) {
            listePointage.put(ptg.getIdPointage(), ptg);
        }
    }

    /**
     * Met à jour la liste des services.
     *
     * @param listeServices
     */
    private void setListeServices(ArrayList<Service> listeServices) {
        this.listeServices = listeServices;
    }

    public void setListeTypes(ArrayList<RefTypePointageDto> listeTypes) {
        this.listeTypes = listeTypes;
    }

    public AgentNW getLoggedAgent() {
        return loggedAgent;
    }

    private void loadHistory() {
        for (Integer i : listePointage.keySet()) {
            loadHistory(i);
        }
    }

    public int getHistorySize() {
        return history.size();
    }

    private void loadHistory(int ptgId) {
        if (!history.containsKey(ptgId)) {
            SirhPtgWSConsumer t = new SirhPtgWSConsumer();
            history.put(ptgId, t.getVisualisationHistory(ptgId));
            // System.out.println(ptgId + " history.size() " + history.size() +
            // " --");
            // for (ConsultPointageDto d : history.get(ptgId))
            // System.out.print(d.getIdPointage() + ",");
        }

    }

    private void refreshHistory(int ptgId) {
        history.remove(ptgId);
        SirhPtgWSConsumer t = new SirhPtgWSConsumer();
        history.put(ptgId, t.getVisualisationHistory(ptgId));
    }

    public String getHistory(int ptgId) {
        if (!history.containsKey(ptgId)) {
            SirhPtgWSConsumer t = new SirhPtgWSConsumer();
            history.put(ptgId, t.getVisualisationHistory(ptgId));
        }
        /**
         * try { performPB_FILTRER(); } catch (Exception e) {
         * System.out.println("Exception in performPB_FILTRER"); }
         *
         */
        List<ConsultPointageDto> data = history.get(ptgId);
        int numParams = 7;
        String[][] ret = new String[data.size()][numParams];
        int index = 0;
        for (ConsultPointageDto p : data) {
            ret[index][0] = formatDate(p.getDate());
            ret[index][1] = formatHeure(p.getDebut());
            ret[index][2] = formatHeure(p.getFin());
            ret[index][3] = "" + p.getQuantite();
            ret[index][4] = p.getMotif() + " - " + p.getCommentaire();
            ret[index][5] = EtatPointageEnum.getEtatPointageEnum(p.getIdRefEtat()).name();
            ret[index][6] = formatDate(p.getDateSaisie()) + " à " + formatHeure(p.getDateSaisie());
            index++;
        }

        StringBuilder strret = new StringBuilder();
        for (int i = 0; i < data.size(); i++) {
            // strret.append("[");
            for (int j = 0; j < numParams; j++) {
                strret.append(ret[i][j]).append(",");
            }
            strret.deleteCharAt(strret.lastIndexOf(","));
            strret.append("|");
        }
        strret.deleteCharAt(strret.lastIndexOf("|"));
        return strret.toString();
    }

    private String formatDate(Date d) {
        if (d != null) {
            return sdf.format(d);
        } else {
            return "";
        }
    }

    private String formatHeure(Date d) {
        if (d != null) {
            return hrs.format(d);
        } else {
            return "";
        }
    }

    public String getLundi(int idPtg) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(listePointage.get(idPtg).getDate());
        cal.set(Calendar.DAY_OF_WEEK, -6); // back to previous week
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // jump to next monday.
        return sdf.format(cal.getTime());
    }

    public List<ConsultPointageDto> getHistoryTable(int ptgId) {
        return history.get(ptgId);
    }
}
