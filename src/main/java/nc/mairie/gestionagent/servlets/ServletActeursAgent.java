package nc.mairie.gestionagent.servlets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.mairie.enums.EnumTypeAbsence;
import nc.mairie.gestionagent.absence.dto.ActeursDto;
import nc.mairie.gestionagent.absence.dto.FiltreSoldeDto;
import nc.mairie.gestionagent.absence.dto.SoldeDto;
import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.ApprobateurDto;
import nc.mairie.metier.Const;
import nc.mairie.spring.utils.ApplicationContextProvider;
import nc.mairie.spring.ws.MSDateTransformer;
import nc.noumea.spring.service.AbsService;
import nc.noumea.spring.service.IAbsService;

import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;

import flexjson.JSONSerializer;

public class ServletActeursAgent extends javax.servlet.http.HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		try {

			IAbsService absService = null;
			if (null == absService) {

				ApplicationContext context = ApplicationContextProvider.getContext();
				absService = (AbsService) context.getBean("absService");
			}

			String nomatr = req.getParameter("nomatr");
			String typeDemande = req.getParameter("typeDemande");
			String dateDemande = req.getParameter("dateDemande");
			ActeursDto dto = absService.getListeActeurs(new Integer(900+nomatr));
			
			StringBuffer result = new StringBuffer();
			// les acteurs de l agent
			if (null != dto) {
				for (AgentDto operateur : dto.getListOperateurs()) {
					result.append("Opérateur : " + operateur.getPrenom() + " " + operateur.getNom() + " ("
							+ new Integer(operateur.getIdAgent() - 9000000).toString() + ") \r");
				}
				for (AgentDto viseur : dto.getListViseurs()) {
					result.append("Viseur : " + viseur.getPrenom() + " " + viseur.getNom() + " ("
							+ new Integer(viseur.getIdAgent() - 9000000).toString() + ") \r");
				}
				for (ApprobateurDto approbateur : dto.getListApprobateurs()) {
					result.append("Approbateur : " + approbateur.getApprobateur().getPrenom() + " "
							+ approbateur.getApprobateur().getNom() + " ("
							+ new Integer(approbateur.getApprobateur().getIdAgent() - 9000000).toString() + ") \r");
				}
				if (dto.getListOperateurs().isEmpty() && dto.getListViseurs().isEmpty()
						&& dto.getListApprobateurs().isEmpty()) {
					result.append("Aucun acteur");
				}
			}

			// le sigle service
			// la base conge
			// le nombre de garde
			// le solde
			if (null != EnumTypeAbsence.getRefTypeAbsenceEnum(new Integer(typeDemande))) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				Integer annee = cal.get(Calendar.YEAR);
				Date dateDeb = new DateTime(annee, 1, 1, 0, 0, 0).toDate();
				Date dateFin = new DateTime(annee, 12, 31, 0, 0, 0).toDate();
				FiltreSoldeDto filtreDto = new FiltreSoldeDto();
				filtreDto.setDateDebut(dateDeb);
				filtreDto.setDateFin(dateFin);
				filtreDto.setDateDemande(dateDemande==null || dateDemande.equals(Const.CHAINE_VIDE)? null : new SimpleDateFormat("dd/MM/yyyy").parse(dateDemande));
				filtreDto.setTypeDemande(new Integer(typeDemande));
				String json = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
						.deepSerialize(filtreDto);
				SoldeDto solde = absService.getSoldeAgent(new Integer(900 + nomatr), json);

				if (null != solde) {
					switch (EnumTypeAbsence.getRefTypeAbsenceEnum(new Integer(typeDemande))) {
						case CONGE:
							result.append("\r Solde Congé Annuel année en cours : " + solde.getSoldeCongeAnnee() + " j");
							result.append("\r Solde Congé Annuel  année précédente : " + solde.getSoldeCongeAnneePrec()
									+ " j");
							break;
						case REPOS_COMP:
							String soldeReposCompHeure = (solde.getSoldeReposCompAnnee() / 60) == 0 ? Const.CHAINE_VIDE
									: solde.getSoldeReposCompAnnee() / 60 + "h ";
							String soldeReposCompMinute = (solde.getSoldeReposCompAnnee() % 60) == 0 ? "" : solde
									.getSoldeReposCompAnnee() % 60 + "m";
							String soldeReposCompPrecHeure = (solde.getSoldeReposCompAnneePrec() / 60) == 0 ? Const.CHAINE_VIDE
									: solde.getSoldeReposCompAnneePrec() / 60 + "h ";
							String soldeReposCompPrecMinute = (solde.getSoldeReposCompAnneePrec() % 60) == 0 ? ""
									: solde.getSoldeReposCompAnneePrec() % 60 + "m";
							result.append("\r Solde Repos Comp. année en cours : " + soldeReposCompHeure
									+ soldeReposCompMinute);
							result.append("\r Solde Repos Comp. année précédente : " + soldeReposCompPrecHeure
									+ soldeReposCompPrecMinute);
							break;
						case RECUP:
							String soldeRecupHeure = (solde.getSoldeRecup() / 60) == 0 ? Const.CHAINE_VIDE : solde
									.getSoldeRecup() / 60 + "h ";
							String soldeRecupMinute = (solde.getSoldeRecup() % 60) == 0 ? "" : solde.getSoldeRecup()
									% 60 + "m";
							result.append("\r Solde récupération: " + soldeRecupHeure + soldeRecupMinute);
							break;
						case ASA_A48:
							result.append("\r Solde : " + solde.getSoldeAsaA48() + " j");
							break;
						case ASA_A54:
							result.append("\r Solde : " + solde.getSoldeAsaA54() + " j");
							break;
						case ASA_A55:
							String soldeAsaA55Heure = (solde.getSoldeAsaA55() / 60) == 0 ? Const.CHAINE_VIDE
									: new Double(solde.getSoldeAsaA55() / 60).intValue() + "h ";
							String soldeAsaA55Minute = (solde.getSoldeAsaA55() % 60) == 0 ? "" : solde.getSoldeAsaA55()
									% 60 + "m";
							result.append("\r Solde : " + soldeAsaA55Heure + soldeAsaA55Minute);
							break;
						case ASA_A52:
							String soldeAsaA52Heure = (solde.getSoldeAsaA52() / 60) == 0 ? Const.CHAINE_VIDE
									: new Double(solde.getSoldeAsaA52() / 60).intValue() + "h ";
							String soldeAsaA52Minute = (solde.getSoldeAsaA52() % 60) == 0 ? "" : solde.getSoldeAsaA52()
									% 60 + "m";
							result.append("\r Solde : " + soldeAsaA52Heure + soldeAsaA52Minute);
							break;
						case MALADIE:
							// TODO
							break;
						default:
							break;
					}
				}
			}

			resp.setContentType("text/html");
			resp.setHeader("Cache-Control", "no-cache");
			resp.getWriter().write(result.toString());
			resp.getWriter().close();
			resp.setStatus(HttpServletResponse.SC_OK);

		} catch (Exception e) {
			resp.setContentType("text/html");
			resp.setHeader("Cache-Control", "no-cache");
			resp.getWriter().write("Une erreur est survenue.");
			resp.getWriter().close();
			resp.setStatus(HttpServletResponse.SC_OK);
		}
	}

}
