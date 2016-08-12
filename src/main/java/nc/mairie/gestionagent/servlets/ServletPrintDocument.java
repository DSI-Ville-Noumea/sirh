package nc.mairie.gestionagent.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import nc.mairie.gestionagent.process.agent.OeAGENTContrat;
import nc.mairie.gestionagent.process.agent.OeAGENTEmploisAffectation;
import nc.mairie.gestionagent.process.agent.OeAGENTEmploisPoste;
import nc.mairie.gestionagent.process.avancement.OeAVCTFonctArretes;
import nc.mairie.gestionagent.process.avancement.OeAVCTFonctDetaches;
import nc.mairie.gestionagent.process.avancement.OeAVCTFonctPrepaCAP;
import nc.mairie.gestionagent.process.avancement.OeAVCTFonctionnaireAutre;
import nc.mairie.gestionagent.process.poste.OePOSTEFichePoste;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.noumea.spring.service.ISirhService;

public class ServletPrintDocument extends javax.servlet.http.HttpServlet {

	private Logger logger = LoggerFactory.getLogger(ServletPrintDocument.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		// on recupere les parametres GET
		String fromPage = req.getParameter("fromPage");
		String nomFichier = req.getParameter("nomFichier");
		if(null == nomFichier
				|| "".equals(nomFichier.trim())) {
			nomFichier = "document.pdf";
		}else{
			if(!nomFichier.contains(".doc")
					&& !nomFichier.contains(".pdf")) {
				nomFichier += ".pdf"; 
			}
		}

		ISirhService sirhService = null;
		if (null == sirhService) {
			ApplicationContext context = ApplicationContextProvider.getContext();
			sirhService = (ISirhService) context.getBean("sirhService");
		}
		
		// Selon de la page appelante, on appelle un doc different
		byte[] fileByte = null;
		try {
			//// OeAGENTEmploisAffectation //// 
			if(OeAGENTEmploisAffectation.class.getName().equals(fromPage)) {
				String typeDocument = req.getParameter("typeDocument");
				String idAffectation = req.getParameter("idAffectation");
				
				if (typeDocument.equals("Interne")) {
					fileByte = sirhService.downloadNoteService(new Integer(idAffectation), null);
				} else {
					fileByte = sirhService.downloadNoteService(new Integer(idAffectation), typeDocument);
				}
			}
			
			//// OeAGENTEmploisPoste ////
			if(OeAGENTEmploisPoste.class.getName().equals(fromPage)) {
				String idFichePoste = req.getParameter("idFichePoste");
				
				fileByte =  sirhService.downloadFichePoste(new Integer(idFichePoste));
			}
	
			//// OeAGENTContrat ////
			if(OeAGENTContrat.class.getName().equals(fromPage)) {
				String idContrat = req.getParameter("idContrat");
				String idAgent = req.getParameter("idAgent");
				
				fileByte = sirhService.downloadContrat(new Integer(idAgent), new Integer(idContrat));
			}

			//// OeAVCTFonctArretes ////
			if(OeAVCTFonctArretes.class.getName().equals(fromPage)) {
				String listeImpressionChangementClasse = req.getParameter("listeImpressionChangementClasse");
				String listeImpressionAvancementDiff = req.getParameter("listeImpressionAvancementDiff");
				String anneeSelect = req.getParameter("anneeSelect");
				
				if(null != listeImpressionChangementClasse
						&& !"".equals(listeImpressionChangementClasse)) {
					fileByte = sirhService.downloadArrete(listeImpressionChangementClasse.toString().replace("[", "").replace("]", "").replace(" ", ""), true, Integer.valueOf(anneeSelect),
							false);
				}
				if(null != listeImpressionAvancementDiff
						&& !"".equals(listeImpressionAvancementDiff)) {
					fileByte = sirhService.downloadArrete(listeImpressionAvancementDiff.toString().replace("[", "").replace("]", "").replace(" ", ""), false, Integer.valueOf(anneeSelect),
							false);
				}
			}

			//// OeAVCTFonctDetaches ////
			if(OeAVCTFonctDetaches.class.getName().equals(fromPage)) {
				String listeImpressionChangementClasse = req.getParameter("listeImpressionChangementClasse");
				String listeImpressionAvancementDiff = req.getParameter("listeImpressionAvancementDiff");
				String anneeSelect = req.getParameter("anneeSelect");
				
				if(null != listeImpressionChangementClasse
						&& !"".equals(listeImpressionChangementClasse)) {
					fileByte = sirhService.downloadArrete(listeImpressionChangementClasse.toString().replace("[", "").replace("]", "").replace(" ", ""), true, Integer.valueOf(anneeSelect),
							true);
				}
				if(null != listeImpressionAvancementDiff
						&& !"".equals(listeImpressionAvancementDiff)) {
					fileByte = sirhService.downloadArrete(listeImpressionAvancementDiff.toString().replace("[", "").replace("]", "").replace(" ", ""), false, Integer.valueOf(anneeSelect),
							true);
				}
			}

			//// OeAVCTFonctionnaireAutre ////
			if(OeAVCTFonctionnaireAutre.class.getName().equals(fromPage)) {
				String listeImpressionChangementClasse = req.getParameter("listeImpressionChangementClasse");
				String listeImpressionAvancementDiff = req.getParameter("listeImpressionAvancementDiff");
				String anneeSelect = req.getParameter("anneeSelect");
				
				if(null != listeImpressionChangementClasse
						&& !"".equals(listeImpressionChangementClasse)) {
					fileByte = sirhService.downloadArrete(listeImpressionChangementClasse.toString().replace("[", "").replace("]", "").replace(" ", ""), true, Integer.valueOf(anneeSelect),
							false);
				}
				if(null != listeImpressionAvancementDiff
						&& !"".equals(listeImpressionAvancementDiff)) {
					fileByte = sirhService.downloadArrete(listeImpressionAvancementDiff.toString().replace("[", "").replace("]", "").replace(" ", ""), false, Integer.valueOf(anneeSelect),
							false);
				}
			}
			
			//// OeAVCTFonctPrepaCAP ////
			if(OeAVCTFonctPrepaCAP.class.getName().equals(fromPage)) {
				String idCap = req.getParameter("idCap");
				String idCadreEmploi = req.getParameter("idCadreEmploi");
				
				fileByte = sirhService.downloadTableauAvancement(Integer.valueOf(idCap), Integer.valueOf(idCadreEmploi), true, "PDF");
			}
			
			//// OePOSTEFichePoste ////
			if(OePOSTEFichePoste.class.getName().equals(fromPage)) {
				String idFichePoste = req.getParameter("idFichePoste");
				
				fileByte = sirhService.downloadFichePoste(Integer.valueOf(idFichePoste));
			}
			
		} catch (NumberFormatException e) {
			logger.debug(e.getMessage());
			resp.reset();
			resp.setContentType("text/html");
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Une erreur est survenue pour afficher l'édition " + nomFichier
							+ ". Merci de contacter votre responsable. (Réf. technique : " + fromPage + " )");
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			
			return;
		} catch (Exception e) {
			logger.debug(e.getMessage());
			resp.reset();
			resp.setContentType("text/html");
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Une erreur est survenue pour afficher l'édition " + nomFichier
							+ ". Merci de contacter votre responsable. (Réf. technique : " + fromPage + " )");
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			
			return;
		}
		
		OutputStream os = resp.getOutputStream();
		try {
			resp.setContentType("application/octet-stream");
			resp.setHeader("Content-disposition", "inline; filename=" + nomFichier);
			os.write(fileByte);
			os.flush();
			os.close();
		} catch (IOException e) {
			logger.debug(e.getMessage());
			resp.reset();
			resp.setContentType("text/html");
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Une erreur est survenue pour afficher l'édition " + nomFichier
							+ ". Merci de contacter votre responsable. (Réf. technique : " + fromPage + " )");
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			resp.reset();
			resp.setContentType("text/html");
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Une erreur est survenue pour afficher l'édition " + nomFichier
							+ ". Merci de contacter votre responsable. (Réf. technique : " + fromPage + " )");
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			os.flush();
			os.close();
		}
	}
}
