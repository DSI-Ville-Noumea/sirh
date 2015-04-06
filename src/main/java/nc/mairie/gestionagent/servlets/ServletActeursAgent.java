package nc.mairie.gestionagent.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.mairie.gestionagent.absence.dto.ActeursDto;
import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.ApprobateurDto;
import nc.mairie.spring.ws.SirhAbsWSConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletActeursAgent extends javax.servlet.http.HttpServlet {

	private Logger logger = LoggerFactory.getLogger(ServletActeursAgent.class);

	public SirhAbsWSConsumer consuAbs = new SirhAbsWSConsumer();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		try {
			String nomatr = req.getParameter("nomatr");
			ActeursDto dto = consuAbs.getListeActeurs(new Integer(900+nomatr));
			
			StringBuffer result = new StringBuffer();
			
			if(null != dto) {
				for(AgentDto operateur : dto.getListOperateurs()) {
					result.append("Opérateur : " 
							+ operateur.getPrenom() + " " 
							+ operateur.getNom() + " ("
							+ new Integer(operateur.getIdAgent() - 9000000).toString() 
							+ ") \r");
				}
				for(AgentDto viseur : dto.getListViseurs()) {
					result.append("Viseur : " 
							+ viseur.getPrenom() + " " 
							+ viseur.getNom() + " ("
							+ new Integer(viseur.getIdAgent() - 9000000).toString() 
							+ ") \r");
				}
				for(ApprobateurDto approbateur : dto.getListApprobateurs()) {
					result.append("Approbateur : " 
							+ approbateur.getApprobateur().getPrenom() + " " 
							+ approbateur.getApprobateur().getNom() + " ("
							+ new Integer(approbateur.getApprobateur().getIdAgent() - 9000000).toString() 
							+ ") \r");
				}
			}
			
			resp.setContentType("text/html");
			resp.setHeader("Cache-Control", "no-cache");
			resp.getWriter().write(result.toString());
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
