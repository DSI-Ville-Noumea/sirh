package nc.mairie.gestionagent.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.noumea.spring.service.IPtgService;
import nc.noumea.spring.service.PtgService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class ServletEtatPayeur extends javax.servlet.http.HttpServlet {

	private Logger logger = LoggerFactory.getLogger(ServletEtatPayeur.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String idEtatPayeur = req.getParameter("idEtatPayeur");
		String nomFichier = req.getParameter("nomFichier");

		IPtgService ptgService = null;
		if (null == ptgService) {

			ApplicationContext context = ApplicationContextProvider.getContext();
			ptgService = (PtgService) context.getBean("ptgService");
		}

		byte[] fileByte = ptgService.downloadFicheEtatsPayeur(new Integer(idEtatPayeur));

		OutputStream os = resp.getOutputStream();
		try {
			resp.setContentType("Application/x-pdf");
			resp.setHeader("Content-disposition", "inline; filename=" + nomFichier + ".pdf");
			os.write(fileByte);
			os.flush();
			os.close();
		} catch (IOException e) {
			logger.debug(e.getMessage());
			resp.reset();
			resp.setContentType("text/html");
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Une erreur est survenue pour afficher l'édition " + nomFichier
							+ ". Merci de contacter votre responsable.");
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			resp.reset();
			resp.setContentType("text/html");
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Une erreur est survenue pour afficher l'édition " + nomFichier
							+ ". Merci de contacter votre responsable.");
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			os.flush();
			os.close();
		}
	}
}
