package nc.mairie.gestionagent.process.absence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Request;

import com.oreilly.servlet.MultipartRequest;

import nc.mairie.gestionagent.servlets.ServletAgent;
import nc.mairie.metier.Const;
import nc.mairie.technique.BasicProcess;
import nc.mairie.utils.VariablesActivite;
public class OeABSGestionPJ extends BasicProcess {
	
	private static final long serialVersionUID = 445405658078291598L;

	public String								focus								= null;

	public MultipartRequest						multi								= null;
	public File									fichierUpload						= null;
	public List<File>							listFichierUpload					= new ArrayList<File>();

	public boolean								isImporting							= false;
	
	@Override
	public String getJSP() {
		return "OeABSGestionPJ.jsp";
	}

	@Override
	public void initialiseZones(HttpServletRequest request) throws Exception {
	}

	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {
		
		if (multi != null) {
			// Si clic sur le bouton PB_CREER_DOC
			if (multi.getParameter(getNOM_PB_VALIDER_DOCUMENT_CREATION()) != null) {
				return performPB_VALIDER_DOCUMENT_CREATION(request);
			}
			// Si clic sur le bouton PB_ANNULER_DOCUMENT
			if (multi.getParameter(getNOM_PB_ANNULER_DOCUMENT()) != null) {
				return performPB_ANNULER_DOCUMENT(request);
			}
		}
		
		return false;
	}
	
	@Override
	public boolean recupererPreControles(HttpServletRequest request) throws Exception {
		String type = request.getHeader("Content-Type");
		String repTemp = (String) ServletAgent.getMesParametres().get("REPERTOIRE_TEMP");
		@SuppressWarnings("unused")
		String JSP = null;
		multi = null;

		if (type != null && type.indexOf("multipart/form-data") != -1) {
			request.setCharacterEncoding("UTF-8");
			multi = new MultipartRequest(request, repTemp, 10 * 1024 * 1024, "UTF-8");
			JSP = multi.getParameter("JSP");
		} else {
			JSP = request.getParameter("JSP");
		}
		return true;
	}

	/**
	 * - Traite et affecte les zones saisies dans la JSP. - Implémente les
	 * regles de gestion du process - Positionne un statut en fonction de ces
	 * regles : setStatut(STATUT, boolean veutRetour) ou
	 * setStatut(STATUT,Message d'erreur) Date de création : (11/10/11 08:38:48)
	 * 
	 */
	public boolean performPB_VALIDER_DOCUMENT_CREATION(HttpServletRequest request) throws Exception {
		// on sauvegarde le nom du fichier parcourir
		if (multi != null) {
			if (multi.getFile(getNOM_EF_LIENDOCUMENT()) != null) {
				File file = multi.getFile(getNOM_EF_LIENDOCUMENT());
				if (null != listFichierUpload) {
					boolean isAjout = true;
					for (File fileTmp : listFichierUpload) {
						if (fileTmp.getName().equals(file.getName())) {
							isAjout = false;
							break;
						}
					}
					if (isAjout)
						listFichierUpload.add(file);
				}
			}
		}

		VariablesActivite.ajouter(this, VariablesActivite.ACTIVITE_DEMANDE_PIECE_JOINTE, listFichierUpload);
		setStatut(STATUT_PROCESS_APPELANT);

		return true;
	}

	public boolean performPB_ANNULER_DOCUMENT(HttpServletRequest request) {
		setStatut(STATUT_PROCESS_APPELANT);
		return true;
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

	/**
	 * Retourne le nom d'une zone de saisie pour la JSP : EF_LIENDOCUMENT
	 */
	public String getNOM_EF_LIENDOCUMENT() {
		return "NOM_EF_LIENDOCUMENT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone de saisie :
	 * EF_LIENDOCUMENT
	 */
	public String getVAL_EF_LIENDOCUMENT() {
		return getZone(getNOM_EF_LIENDOCUMENT());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_DOCUMENT
	 */
	public String getNOM_PB_VALIDER_DOCUMENT_CREATION() {
		return "NOM_PB_VALIDER_DOCUMENT_CREATION";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_DOCUMENT
	 */
	public String getVAL_PB_VALIDER_DOCUMENT_CREATION() {
		return getZone(getNOM_PB_VALIDER_DOCUMENT_CREATION());
	}

	/**
	 * Retourne pour la JSP le nom de la zone statique : ST_ACTION_DOCUMENT
	 */
	public String getNOM_PB_VALIDER_DOCUMENT_AJOUT() {
		return "PB_VALIDER_DOCUMENT_AJOUT";
	}

	/**
	 * Retourne la valeur à afficher par la JSP pour la zone :
	 * ST_ACTION_DOCUMENT
	 */
	public String getVAL_PB_VALIDER_DOCUMENT_AJOUT() {
		return getZone(getNOM_PB_VALIDER_DOCUMENT_AJOUT());
	}

	public String getNOM_PB_ANNULER_DOCUMENT() {
		return "NOM_PB_ANNULER_DOCUMENT";
	}

}
