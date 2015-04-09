package nc.mairie.gestionagent.servlets;

import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import nc.mairie.gestionagent.robot.MaClasse;
import nc.mairie.gestionagent.robot.RobotAgent;
import nc.mairie.metier.Const;
import nc.mairie.metier.droits.Autorisation;
import nc.mairie.robot.Robot;
import nc.mairie.servlets.Frontale;
import nc.mairie.spring.dao.metier.droits.AutorisationDao;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Transaction;
import nc.mairie.technique.UserAppli;
import nc.mairie.technique.VariableGlobale;

import org.springframework.context.ApplicationContext;

/**
 * Insérez la description du type ici. Date de création : (30/10/2002 14:15:09)
 */
public class ServletAgent extends Frontale {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AutorisationDao autorisationDao;

	/**
	 * Insérez la description de la méthode ici. Date de création : (05/11/2002
	 * 09:00:21)
	 */
	public ServletAgent() {
		super();
	}

	@Override
	public void performTask(HttpServletRequest request, HttpServletResponse response) {
		// recup de la demande déaction envoyée par la recherche en haut a
		// gauche
		String ACTION = request.getParameter("ACTION");
		if (ACTION != null && !ACTION.equals(Const.CHAINE_VIDE)) {
			// On Vérifie si on a une session
			BasicProcess processCourant = (BasicProcess) VariableGlobale.recuperer(request,
					VariableGlobale.GLOBAL_PROCESS);

			if (processCourant != null) {
				BasicProcess processMemorise = (BasicProcess) VariableGlobale.recuperer(request, "PROCESS_MEMORISE");
				if (processMemorise != null) {
					processCourant = processMemorise;
					processCourant.setStatut(MaClasse.STATUT_RECHERCHE_AGENT, true);
				}
				VariableGlobale.ajouter(request, VariableGlobale.GLOBAL_PROCESS, processCourant);
			}
		}
		super.performTask(request, response);
	}

	/**
	 * Récupération du statut
	 * 
	 * @param request
	 *            Object that encapsulates the request to the servlet
	 * @param response
	 *            Object that encapsulates the response from the servlet
	 * @param processCourant
	 */
	@Override
	protected boolean performRecupererStatut(javax.servlet.http.HttpServletRequest request,
			javax.servlet.http.HttpServletResponse response, BasicProcess processCourant) throws Exception {

		if (request.getParameter("ACTION") != null && processCourant.etatStatut() == MaClasse.STATUT_RECHERCHE_AGENT)
			return true;
		else
			return super.performRecupererStatut(request, response, processCourant);

	}

	/**
	 * Retourne le robot de navigation de la servlet
	 */
	protected Robot getServletRobot() {
		return new RobotAgent();
	}

	protected boolean performControleHabilitation(HttpServletRequest request) throws Exception {
		if (!super.performControleHabilitation(request))
			return false;
		initialiseDao();
		UserAppli aUserAppli = getUserAppli(request);

		boolean droitsOK = false;
		String s;
		for (int i = 0; i < aUserAppli.getListeDroits().size(); i++) {
			s = (String) aUserAppli.getListeDroits().get(i);
			if (s.startsWith("ECR")) {
				droitsOK = true;
				break;
			}
		}

		if (!droitsOK) {
			// init des ghabilitations
			initialiseHabilitations(request);

			// Si pas d'habilitation alors erreur
			if (aUserAppli.getListeDroits().size() == 0) {
				String message = "Le user " + aUserAppli.getUserName() + " n'est pas habilité a utiliser l'application";
				System.out.println(message);
				throw new Exception(message);
				// return false;
			}
		}
		return true;
	}

	private void initialiseDao() {
		// on initialise le dao
		ApplicationContext context = ApplicationContextProvider.getContext();
		if (getAutorisationDao() == null) {
			setAutorisationDao(new AutorisationDao((SirhDao) context.getBean("sirhDao")));
		}
	}

	public void initialiseHabilitations(javax.servlet.http.HttpServletRequest request) throws Exception {
		// recup du userAppli
		UserAppli aUserAppli = getUserAppli(request);

		Transaction t = new Transaction(getUserAppli(request));
		ArrayList<Autorisation> autorisations = getAutorisationDao().listerAutorisationAvecUtilisateur(
				aUserAppli.getUserName());
		t.rollbackTransaction();

		Autorisation a;
		aUserAppli.getListeDroits().clear();
		for (int i = 0; i < autorisations.size(); i++) {
			a = (Autorisation) autorisations.get(i);
			aUserAppli.getListeDroits().add(a.getLibAutorisation().toUpperCase());
		}
	}

	/**
	 * Datasource pour l'acces aux données de l'AS400 Ici, les données sur
	 * l'AS400 concerne le schéma MAIRIE uniquement
	 * 
	 * @return DataSource
	 * @throws NamingException
	 */
	public static DataSource getDatasource(String jndi_name) throws NamingException {
		// InitialContext ctx = new InitialContext();
		// DataSource ds = (DataSource) ctx.lookup((String)
		// ServletAgent.getMesParametres().get("DATASOURCE_AS400_MAIRIE"));

		InitialContext ctx = new InitialContext();
		DataSource ds = null;
		try {
			ds = (DataSource) ctx.lookup("java:comp/env/" + jndi_name);

		} catch (Exception e) {
			try {
				ds = (DataSource) ctx.lookup(jndi_name);

			} catch (NamingException ex) {
				System.err.println("Aucun datasource envisagé : " + ex.getMessage());
				throw ex;
			}
		}

		return ds;
	}

	public AutorisationDao getAutorisationDao() {
		return autorisationDao;
	}

	public void setAutorisationDao(AutorisationDao autorisationDao) {
		this.autorisationDao = autorisationDao;
	}
}
