package nc.mairie.gestionagent.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.mairie.gestionagent.process.absence.OeABSVisualisation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletHistoriqueAbsence extends javax.servlet.http.HttpServlet {

	private Logger logger = LoggerFactory.getLogger(ServletHistoriqueAbsence.class);

	public OeABSVisualisation visu = new OeABSVisualisation();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		try {
			String idAbsence = req.getParameter("idAbsence");
			String idDemande = req.getParameter("idDemande");
			resp.setContentType("text/html");
			resp.setHeader("Cache-Control", "no-cache");
			resp.getWriter().write(visu.getHistory(new Integer(idAbsence), new Integer(idDemande)));
			resp.getWriter().close();
			resp.setStatus(HttpServletResponse.SC_OK);

		} catch (Exception e) {
			logger.debug(e.getMessage());
			resp.reset();
			resp.setContentType("text/html");
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Une erreur est survenue pour afficher l'historique de l'absence.");
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}
