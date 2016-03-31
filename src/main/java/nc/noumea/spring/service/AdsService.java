package nc.noumea.spring.service;

import java.util.ArrayList;
import java.util.List;

import nc.mairie.gestionagent.dto.AgentDto;
import nc.mairie.gestionagent.dto.AgentWithServiceDto;
import nc.mairie.gestionagent.dto.EntiteWithAgentWithServiceDto;
import nc.mairie.metier.Const;
import nc.mairie.spring.dao.utils.SirhDao;
import nc.mairie.spring.ws.IADSWSConsumer;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ReferenceDto;
import nc.noumea.mairie.ads.dto.StatutEntiteEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdsService implements IAdsService {

	private Logger logger = LoggerFactory.getLogger(AdsService.class);

	@Autowired
	private IADSWSConsumer adsConsumer;

	@Autowired
	private SirhDao sirhDao;

	private EntiteDto currentTree;

	@Override
	public EntiteDto getCurrentWholeTree() {
		// #16549 : on ne veut afficher l'arbre qu'à partir de VDN
		// on recupere l'entite VDN
		EntiteDto entiteVDN = adsConsumer.getEntiteBySigle("VDN");
		if (entiteVDN == null || entiteVDN.getIdEntite() == null) {
			return new EntiteDto();
		}
		// on recupere les enfants de VDN
		return adsConsumer.getEntiteWithChildrenByIdEntite(entiteVDN.getIdEntite());
	}

	@Override
	public String getCurrentWholeTreeActifTransitoireJS(String serviceSaisi, boolean withSelectionRadioBouton) {

		logger.debug("Debut construction Arbre Des Service avec actifs et transitoires");
		// TODO voir comment faire pour savoir si l arbre a ete modifie pour
		// eviter de charger a chaque fois
		// if (null == getCurrentTree() || getCurrentTree().getIdRevision() !=
		// getCurrentRevision().getIdRevision()) {
		EntiteDto tree = getCurrentWholeTree();
		setCurrentTree(tree);
		// }

		StringBuffer result = new StringBuffer();
		result.append(construitDebutArbre(withSelectionRadioBouton));

		result.append(buildTreeEntitiesActifTransitoire(getCurrentTree(), withSelectionRadioBouton));

		result.append(construitFinArbre(getCurrentTree(), serviceSaisi, null));

		logger.debug("Fin construction Arbre Des Service avec actifs et transitoires");

		return result.toString();
	}

	private String openToServiceSaisi(EntiteDto entite, String serviceSaisi) {

		if (null != entite && null != entite.getEnfants() && null != serviceSaisi) {
			for (EntiteDto enfant : entite.getEnfants()) {
				if (serviceSaisi.equals(enfant.getSigle())) {
					return "d.openTo(" + enfant.getIdEntite() + ", true);";
				}
				String result = openToServiceSaisi(enfant, serviceSaisi);
				if (null != result && !"".equals(result.trim())) {
					return result;
				}
			}
		}

		return "";
	}

	private String selectServiceWithRadioBouton(EntiteDto entite) {

		StringBuffer result = new StringBuffer();

		if (null != entite && null != entite.getEnfants()) {
			for (EntiteDto enfant : entite.getEnfants()) {
				result.append("		var box = document.formu.elements['rbd" + enfant.getIdEntite() + "'];");
				result.append("		if(box!=null){");
				result.append("			box.checked = false;");
				result.append("		}");
				result.append(selectServiceWithRadioBouton(enfant));
			}
		}

		return result.toString();
	}

	private String buildTreeEntitiesActifTransitoire(EntiteDto entite, boolean withSelectionRadioBouton) {

		StringBuffer result = new StringBuffer();
		if (null != entite && null != entite.getEnfants()) {
			for (EntiteDto enfant : entite.getEnfants()) {
				// #16520 : on ne prends que les actifs ou transitoires
				if (enfant.getIdStatut() != null
						&& (enfant.getIdStatut().toString().equals(String.valueOf(StatutEntiteEnum.ACTIF.getIdRefStatutEntite())) || enfant.getIdStatut().toString()
								.equals(String.valueOf(StatutEntiteEnum.TRANSITOIRE.getIdRefStatutEntite())))) {
					String transitoire = "";
					String style = "";
					if (enfant.getIdStatut().toString().equals(String.valueOf(StatutEntiteEnum.TRANSITOIRE.getIdRefStatutEntite()))) {
						transitoire = " (T)";
						style = "font-style:italic;";
					}

					result.append(ajouteNoeud(entite.getNfa(), entite, enfant, withSelectionRadioBouton, transitoire, style, null));
					result.append(buildTreeEntitiesActifTransitoire(enfant, withSelectionRadioBouton));
				}
			}
		}

		return result.toString();
	}

	@Override
	public EntiteDto getEntiteByIdEntite(Integer idEntite) {
		return adsConsumer.getEntiteByIdEntite(idEntite);
	}

	@Override
	public EntiteDto getEntiteWithChildrenByIdEntite(Integer idEntite) {
		return adsConsumer.getEntiteWithChildrenByIdEntite(idEntite);
	}

	@Override
	public EntiteDto getEntiteBySigle(String sigle) {
		return adsConsumer.getEntiteBySigle(sigle);
	}

	@Override
	public List<Integer> getListIdsEntiteWithEnfantsOfEntite(Integer idEntite) {

		List<Integer> result = new ArrayList<Integer>();

		EntiteDto entiteDto = adsConsumer.getEntiteWithChildrenByIdEntite(idEntite);

		result.add(entiteDto.getIdEntite().intValue());
		result.addAll(getListIdsEntiteEnfants(entiteDto));

		return result;
	}

	private List<Integer> getListIdsEntiteEnfants(EntiteDto entiteDto) {

		List<Integer> result = new ArrayList<Integer>();

		if (null != entiteDto && null != entiteDto.getEnfants()) {
			for (EntiteDto enfant : entiteDto.getEnfants()) {
				result.add(enfant.getIdEntite().intValue());
				result.addAll(getListIdsEntiteEnfants(enfant));
			}
		}
		return result;
	}

	@Override
	public List<String> getListSiglesWithEnfantsOfEntite(Integer idEntite) {

		List<String> result = new ArrayList<String>();

		EntiteDto entiteDto = adsConsumer.getEntiteWithChildrenByIdEntite(idEntite);

		result.add(entiteDto.getSigle());
		result.addAll(getListSigleEnfants(entiteDto));

		return result;
	}

	private List<String> getListSigleEnfants(EntiteDto entiteDto) {

		List<String> result = new ArrayList<String>();

		if (null != entiteDto && null != entiteDto.getEnfants()) {
			for (EntiteDto enfant : entiteDto.getEnfants()) {
				result.add(enfant.getSigle());
				result.addAll(getListSigleEnfants(enfant));
			}
		}
		return result;
	}

	public EntiteDto getCurrentTree() {
		return currentTree;
	}

	public void setCurrentTree(EntiteDto currentTree) {
		this.currentTree = currentTree;
	}

	@Override
	public EntiteDto getEntiteByCodeServiceSISERV(String serviAS400) {
		return adsConsumer.getEntiteByCodeServiceSISERV(serviAS400);
	}

	@Override
	public EntiteDto getAffichageDirection(Integer idEntite) {
		if (idEntite == null)
			return null;
		// on appel ADS pour connaitre la liste des types d'netité pour passer
		// en paramètre ensuite le type "direction"
		List<ReferenceDto> listeType = adsConsumer.getListTypeEntite();
		ReferenceDto type = null;
		for (ReferenceDto r : listeType) {
			if (r.getLabel().toUpperCase().equals("AFFICHAGE SIRH DE TYPE DIRECTION")) {
				type = r;
				break;
			}
		}
		if (type == null) {
			return null;
		}
		return adsConsumer.getParentOfEntiteByTypeEntite(idEntite, type.getId());
	}

	@Override
	public EntiteDto getAffichageSection(Integer idEntite) {
		if (idEntite == null)
			return null;
		// on appel ADS pour connaitre la liste des types d'netité pour passer
		// en paramètre ensuite le type "section"
		List<ReferenceDto> listeType = adsConsumer.getListTypeEntite();
		ReferenceDto type = null;
		for (ReferenceDto r : listeType) {
			if (r.getLabel().toUpperCase().equals("AFFICHAGE SIRH DE TYPE SECTION")) {
				type = r;
				break;
			}
		}
		if (type == null) {
			return null;
		}
		return adsConsumer.getParentOfEntiteByTypeEntite(idEntite, type.getId());
	}

	@Override
	public String getCurrentWholeTreePrevisionActifTransitoireJS(String serviceSaisi, boolean withSelectionRadioBouton) {

		logger.debug("Debut construction Arbre Des Service avec provisoires, actifs et transitoires");
		// TODO voir comment faire pour savoir si l arbre a ete modifie pour
		// eviter de charger a chaque fois
		// if (null == getCurrentTree() || getCurrentTree().getIdRevision() !=
		// getCurrentRevision().getIdRevision()) {
		EntiteDto tree = getCurrentWholeTree();
		setCurrentTree(tree);
		// }

		StringBuffer result = new StringBuffer();
		result.append(construitDebutArbre(withSelectionRadioBouton));

		result.append(buildTreeEntitiesProvisoireActifTransitoire(getCurrentTree(), withSelectionRadioBouton));

		result.append(construitFinArbre(getCurrentTree(), serviceSaisi, null));

		logger.debug("Fin construction Arbre Des Service avec provisoires, actifs et transitoires");

		return result.toString();
	}

	/**
	 * 
	 * @param currentTree L arbre a afficher
	 * @param serviceSaisi Le service pre selectionne et donc ouvrir l arbre sur ce service
	 * @param listIdNodeOpen Les nodes de l arbre a ouvrir
	 * @return
	 */
	private String construitFinArbre(EntiteDto currentTree, String serviceSaisi, List<Integer> listIdNodeOpen) {
		StringBuilder result = new StringBuilder();

		result.append("document.write(d);");
		result.append("d.closeAll();");

		result.append(openToServiceSaisi(currentTree, serviceSaisi));
		
		if(null != listIdNodeOpen) {
			for(Integer idNode : listIdNodeOpen) {
				result.append("d.openTo('" + idNode + "', 'true'); ");
			}
		}

		result.append("</script>");
		result.append("</div>");
		return result.toString();
	}

	private String construitDebutArbre(boolean withSelectionRadioBouton) {
		StringBuilder result = new StringBuilder();
		result.append("<div id=\"treeHierarchy\" style=\"display: ");
		result.append(withSelectionRadioBouton ? "block" : "none");
		result.append("; height: 360; width: 500; overflow:auto; " + "background-color: #f4f4f4; border-width: 1px; border-style: solid;z-index:1;\">");

		result.append("<script type=\"text/javascript\">");
		// afin d afficher la hierarchie des services
		result.append("function agrandirHierarchy() {");
		result.append("		hier = 	document.getElementById('treeHierarchy');");

		result.append("		if (hier.style.display!=\"none\") {");
		result.append("			reduireHierarchy();");
		result.append("		} else {");
		result.append("			hier.style.display=\"block\";");
		result.append("		}");
		result.append("	}");
		// ou reduire
		result.append("	function reduireHierarchy() {");
		result.append("		hier = 	document.getElementById('treeHierarchy');");
		result.append("		hier.style.display=\"none\";");
		result.append("	}");

		result.append("function selectService(id, sigle) {	");
		result.append(selectServiceWithRadioBouton(getCurrentTree()));
		result.append("}");

		result.append("</script>");

		// generation de l arbre
		result.append("<SCRIPT language=\"javascript\" src=\"js/dtree.js\"></SCRIPT>");
		result.append("<script type=\"text/javascript\">");

		result.append("d = new dTree('d');");
		result.append("d.add(" + getCurrentTree().getIdEntite() + ",-1,\"Services\");");
		return result.toString();
	}

	private Object buildTreeEntitiesProvisoireActifTransitoire(EntiteDto entite, boolean withSelectionRadioBouton) {
		StringBuffer result = new StringBuffer();

		if (null != entite && null != entite.getEnfants()) {
			for (EntiteDto enfant : entite.getEnfants()) {

				// #16520 : on ne prends que les actifs ou transitoires ou
				// provisoires pour les FDP
				if (enfant.getIdStatut() != null
						&& (enfant.getIdStatut().toString().equals(String.valueOf(StatutEntiteEnum.ACTIF.getIdRefStatutEntite())) || enfant.getIdStatut().toString()
								.equals(String.valueOf(StatutEntiteEnum.TRANSITOIRE.getIdRefStatutEntite())))
						|| enfant.getIdStatut().toString().equals(String.valueOf(StatutEntiteEnum.PREVISION.getIdRefStatutEntite()))) {
					String transitoire = "";
					String style = "";
					if (enfant.getIdStatut().toString().equals(String.valueOf(StatutEntiteEnum.TRANSITOIRE.getIdRefStatutEntite()))) {
						transitoire = " (T)";
						style = "font-style:italic;";
					} else if (enfant.getIdStatut().toString().equals(String.valueOf(StatutEntiteEnum.PREVISION.getIdRefStatutEntite()))) {
						transitoire = " (P)";
						style = "font-weight: bold;";
					}
					result.append(ajouteNoeud(entite.getNfa(), entite, enfant, withSelectionRadioBouton, transitoire, style, null));
					result.append(buildTreeEntitiesProvisoireActifTransitoire(enfant, withSelectionRadioBouton));
				}

			}
		}

		return result.toString();
	}

	private Object ajouteNoeud(String nfa, EntiteDto entite, EntiteDto enfant, boolean withSelectionRadioBouton, String transitoire, String style, Boolean check) {
		String result = "";
		if (nfa == null || nfa.equals(Const.CHAINE_VIDE)) {
			result = "d.add(" + enfant.getIdEntite() + "," + entite.getIdEntite() + ",\"" + transitoire + enfant.getSigle() + " " + (null != enfant.getLabel() ? enfant.getLabel().replace("'", " ") : "") + "\",'" + enfant.getSigle()
					+ "','" + enfant.getIdEntite() + "','',\"" + style + "\",'" + withSelectionRadioBouton + "', '" + check + "');";
		} else {
			result = "d.add(" + enfant.getIdEntite() + "," + entite.getIdEntite() + ",\"" + transitoire + enfant.getSigle() + " " + (null != enfant.getLabel() ? enfant.getLabel().replace("'", " ") : "") + "\",'" + enfant.getSigle()
					+ "','" + enfant.getIdEntite() + "','" + nfa + "',\"" + style + "\",'" + withSelectionRadioBouton + "', '" + check + "');";
		}
		return result;
	}

	@Override
	public List<EntiteDto> getListEntiteByStatut(Integer idStatut) {
		return adsConsumer.getListEntiteByStatut(idStatut);
	}

	@Override
	public EntiteDto getInfoSiservByIdEntite(Integer idEntite) {
		return adsConsumer.getInfoSiservByIdEntite(idEntite);
	}

	@Override
	public EntiteDto getAffichageService(Integer idEntite) {
		if (idEntite == null)
			return null;
		// on appel ADS pour connaitre la liste des types d'netité pour passer
		// en paramètre ensuite le type "direction"
		List<ReferenceDto> listeType = adsConsumer.getListTypeEntite();
		ReferenceDto type = null;
		for (ReferenceDto r : listeType) {
			if (r.getLabel().toUpperCase().equals("AFFICHAGE SIRH DE TYPE SERVICE")) {
				type = r;
				break;
			}
		}
		if (type == null) {
			return null;
		}
		return adsConsumer.getParentOfEntiteByTypeEntite(idEntite, type.getId());
	}

	@Override
	public List<EntiteDto> getListEntiteDto(EntiteDto entiteWithChildren) {

		List<EntiteDto> result = new ArrayList<EntiteDto>();

		result.add(entiteWithChildren);
		getListEntiteDtoRecursive(entiteWithChildren, result);

		return result;
	}

	private void getListEntiteDtoRecursive(EntiteDto entiteDto, List<EntiteDto> result) {

		if (null != entiteDto && null != entiteDto.getEnfants()) {
			for (EntiteDto enfant : entiteDto.getEnfants()) {
				result.add(enfant);
				getListEntiteDtoRecursive(enfant, result);
			}
		}
	}

	@Override
	public EntiteDto getListEntiteDtoByIdService(List<EntiteDto> listEntiteDto, Integer idService) {

		if (null != listEntiteDto && null != idService) {
			for (EntiteDto entite : listEntiteDto) {
				if (entite.getIdEntite().equals(idService)) {
					return entite;
				}
			}
		}

		return null;
	}

	/**
	 * Est appele depuis la gestion des droits du kiosque
	 */
	@Override
	public String getCurrentWholeTreeWithAgent(EntiteWithAgentWithServiceDto tree, boolean withCkeckBox, 
			List<AgentDto> listAgentsExistants, List<AgentDto> filtreAgents) {

		logger.debug("Debut construction Arbre Des Service avec agents");

		List<Integer> listIdNodeOpen = new ArrayList<Integer>();
		
		StringBuffer result = new StringBuffer();
		result.append(construitDebutArbreWithAgent(tree, withCkeckBox));

		// cette partie ajoute le premier dossier Service dans l arbre sous le noeud Services
		EntiteDto entiteService = new EntiteDto();
		entiteService.setIdEntite(0);
		result.append(ajouteNoeud(tree.getNfa(), entiteService, tree, withCkeckBox, "", "", 
				isAllAgentsCheck(tree, filtreAgents, listAgentsExistants)));
		
		if(isOneAgentCheck(tree, filtreAgents, listAgentsExistants)) {
			listIdNodeOpen.add(tree.getIdEntite());
		}
		
		result.append(buildTreeEntitiesWithAgent(tree, withCkeckBox, listAgentsExistants, filtreAgents, listIdNodeOpen));

		result.append(construitFinArbre(tree, null, listIdNodeOpen));

		logger.debug("Fin construction Arbre Des Service avec agents");

		return result.toString();
	}

	/**
	 * 
	 * @param tree l arbre principal
	 * @param withCkeckBox avec ou sans checkbox
	 * @param treeWithAgentsOthersServices les services et agents ajoutes
	 * 		 a la main par la DRH mais ne faisant pas partie de l arbre principal
	 * @return Le debut du Javascript pour l affichage de l arbre
	 */
	private String construitDebutArbreWithAgent(EntiteWithAgentWithServiceDto tree, boolean withCkeckBox) {
		StringBuilder result = new StringBuilder();
		result.append("<div id=\"treeHierarchy\" style=\"display: ");
		result.append(withCkeckBox ? "block" : "none");
		result.append("; height: 360; width: 500; overflow:auto; " + "background-color: #f4f4f4; border-width: 1px; border-style: solid;z-index:1;\">");

		result.append("<script type=\"text/javascript\">");
		// afin d afficher la hierarchie des services
		result.append("function agrandirHierarchy() {");
		result.append("		hier = 	document.getElementById('treeHierarchy');");

		result.append("		if (hier.style.display!=\"none\") {");
		result.append("			reduireHierarchy();");
		result.append("		} else {");
		result.append("			hier.style.display=\"block\";");
		result.append("		}");
		result.append("	}");
		// ou reduire
		result.append("	function reduireHierarchy() {");
		result.append("		hier = 	document.getElementById('treeHierarchy');");
		result.append("		hier.style.display=\"none\";");
		result.append("	}");

		result.append("function selectService(id, sigle) {	 \n");
		result.append(selectServiceWithCheckBoxBouton(tree));
		result.append("} \n");

		result.append("function deselectService(id, sigle) {	");
		result.append(" if(id > 9000000) { ");
		result.append("		var box = document.formu.elements['NOM_CK_AGENT_'+id];");
		result.append("		var idParent = box.title;");
		result.append("		var boxParent = document.formu.elements['NOM_CK_AGENT_'+idParent];");
		result.append("		if(boxParent!=null){");
		result.append("			boxParent.checked = false;");
		result.append("		}");
		result.append("	}");
		result.append(" if(id < 9000000) { ");
		result.append(	deselectServiceWithCheckBoxBouton(tree));
		result.append("	}");
		result.append("}");

		result.append("</script>");

		// generation de l arbre
		result.append("<SCRIPT language=\"javascript\" src=\"js/dtreeCheckBox.js\"></SCRIPT> \n");
		result.append("<script type=\"text/javascript\"> \n");

		result.append("d = new dTree('d'); \n");
		result.append("d.add(0,-1,\"Services\"); \n");
		return result.toString();
	}

	private String selectServiceWithCheckBoxBouton(EntiteWithAgentWithServiceDto entite) {

		StringBuffer result = new StringBuffer();

		if (null != entite) {
			for(AgentWithServiceDto agent : entite.getListAgentWithServiceDto()) {
				result.append("		var box = document.formu.elements['NOM_CK_AGENT_" + agent.getIdAgent() + "']; \n");
				result.append("		if(box!=null && box.title == id){ \n");
				result.append("			box.checked = true; \n");
				result.append("		} \n");
			}
			if(null != entite.getEntiteEnfantWithAgents()) {
				for (EntiteWithAgentWithServiceDto enfant : entite.getEntiteEnfantWithAgents()) {
					result.append(selectServiceWithCheckBoxBouton(enfant));
				}
			}
		}

		return result.toString();
	}

	private String deselectServiceWithCheckBoxBouton(EntiteWithAgentWithServiceDto entite) {

		StringBuffer result = new StringBuffer();
		
		if (null != entite) { 
			for(AgentWithServiceDto agent : entite.getListAgentWithServiceDto()) {
				result.append("		var box = document.formu.elements['NOM_CK_AGENT_" + agent.getIdAgent() + "']; \n");
				result.append("		if(box!=null && box.title == id){  \n");
				result.append("			box.checked = false; \n");
				result.append("		} \n");
			}
			
			if(null != entite.getEntiteEnfantWithAgents()) {
				for (EntiteWithAgentWithServiceDto enfant : entite.getEntiteEnfantWithAgents()) {
//					for(AgentWithServiceDto agent : enfant.getListAgentWithServiceDto()) {
//						result.append("		var box = document.formu.elements['NOM_CK_AGENT_" + agent.getIdAgent() + "']; \n");
//						result.append("		if(box!=null && box.title == id){  \n");
//						result.append("			box.checked = false; \n");
//						result.append("		} \n");
//					}
					
					result.append(deselectServiceWithCheckBoxBouton(enfant));
				}
			}
		}

		return result.toString();
	}

	private String buildTreeEntitiesWithAgent(EntiteWithAgentWithServiceDto entite, boolean withCkeckBox, 
			List<AgentDto> listAgentsExistants, List<AgentDto> filtreAgents, List<Integer> listIdNodeOpen) {
		
		StringBuffer result = new StringBuffer();
		if (null != entite) {
			
			// on ajoute les agents
			for (AgentWithServiceDto ag : entite.getListAgentWithServiceDto()) {
				AgentDto agentTmpDto = new AgentDto(ag);
				if(null == filtreAgents
						|| filtreAgents.contains(agentTmpDto)) {
					String styleAgent = "font-weight: normal;";
					result.append(ajouteNoeudAgent(ag, entite, withCkeckBox, styleAgent, listAgentsExistants));
				}
			}
			if (entite.getEntiteEnfantWithAgents() != null) {
				for (EntiteWithAgentWithServiceDto enfant : entite.getEntiteEnfantWithAgents()) {

					// on teste si au moins un agent est affiche dans le service
					// sinon on affiche pas le service
					if(isOneAgentInService(enfant, filtreAgents)) {
						result.append(ajouteNoeud(entite.getNfa(), entite, enfant, withCkeckBox, "", "", 
								isAllAgentsCheck(enfant, filtreAgents, listAgentsExistants)));
						
						if(isOneAgentCheck(enfant, filtreAgents, listAgentsExistants)) {
							listIdNodeOpen.add(enfant.getIdEntite());
						}
						
						result.append(buildTreeEntitiesWithAgent(enfant, withCkeckBox, listAgentsExistants, filtreAgents, listIdNodeOpen));
					}
				}
			}
		}

		return result.toString();
	}
	
	/**
	 * Permet de savoir s il y a au moins un agent dans le service et ses sous services a afficher
	 * et ainsi on affiche ou non le service dans l arbre
	 * 
	 * Inutile d'afficher un service si celui est vide
	 * 
	 * @param entite  EntiteWithAgentWithServiceDto Branche de l arbre avec les agents affectes a celui-ci
	 * @param filtreAgents Filtre des agents a afficher : agents affectes a l approbateur
	 * @return TRUE ou FALSE
	 */
	private boolean isOneAgentInService(EntiteWithAgentWithServiceDto entite, List<AgentDto> filtreAgents) {
		
		for (AgentWithServiceDto ag : entite.getListAgentWithServiceDto()) {
			AgentDto agentTmpDto = new AgentDto(ag);
			if(null == filtreAgents
					|| filtreAgents.contains(agentTmpDto)) {
				return true;
			}
		}
		// bug #30004 
		for(EntiteWithAgentWithServiceDto enfant : entite.getEntiteEnfantWithAgents()) {
			boolean result = isOneAgentInService(enfant, filtreAgents);
			if(result)
				return result;
		}
		
		return false;
	}
	
	/**
	 * Permet de cocher un service si tous les agents du service sont coches
	 * 
	 * @param entite EntiteWithAgentWithServiceDto Branche de l arbre avec les agents affectes a celui-ci
	 * @param filtreAgents Filtre des agents a afficher : agents affectes a l approbateur
	 * @param listAgentsExistants : liste des agents affectes a l acteur que l on traite (operateur, viseur, approbateur)
	 * @return TRUE ou FALSE selon que tous les agents de la branche soient coches
	 */
	private boolean isAllAgentsCheck(EntiteWithAgentWithServiceDto entite, List<AgentDto> filtreAgents, List<AgentDto> listAgentsExistants) {
		
		if(null != listAgentsExistants
				&& !listAgentsExistants.isEmpty()
				&& null != entite.getListAgentWithServiceDto()
				&& !entite.getListAgentWithServiceDto().isEmpty()) {
			for (AgentWithServiceDto ag : entite.getListAgentWithServiceDto()) {
				AgentDto agentTmpDto = new AgentDto(ag);
				if((null == filtreAgents
						|| filtreAgents.contains(agentTmpDto))
					&& !listAgentsExistants.contains(agentTmpDto)) {
					return false;
				}
			}
			return true;
		}
		
		return false;
	}
	
	/**
	 * Permet d ouvrir le noeud d un service si au moins un agent est coche
	 * 
	 * @param entite EntiteWithAgentWithServiceDto Branche de l arbre avec les agents affectes a celui-ci
	 * @param filtreAgents Filtre des agents a afficher : agents affectes a l approbateur
	 * @param listAgentsExistants Liste des agents affectes a l acteur que l on traite (operateur, viseur, approbateur)
	 * @return TRUE ou FALSE si au moins un agent est coche
	 */
	private boolean isOneAgentCheck(EntiteWithAgentWithServiceDto entite, List<AgentDto> filtreAgents, List<AgentDto> listAgentsExistants) {
		
		if(null != listAgentsExistants
				&& !listAgentsExistants.isEmpty()) {
			for (AgentWithServiceDto ag : entite.getListAgentWithServiceDto()) {
				AgentDto agentTmpDto = new AgentDto(ag);
				if((null == filtreAgents
						|| filtreAgents.contains(agentTmpDto))
					&& listAgentsExistants.contains(agentTmpDto)) {
					return true;
				}
			}
		}
		
		return false;
	}

	private Object ajouteNoeudAgent(AgentWithServiceDto ag, EntiteDto entite, boolean withCkeckBox, String style, 
			List<AgentDto> listAgentsAffectes) {
		
		String isCheck = isAgentChecke(ag, listAgentsAffectes);
		String result = "";
		result = "d.add(" + ag.getIdAgent() + "," + entite.getIdEntite() + ",\"" + ag.getNom().replace("'", " ") + " " + ag.getPrenom().replace("'", " ") + "\",\"" + ag.getNom().replace("'", " ") + "\",\""
				+ ag.getIdAgent() + "\",\"\",\"" + style + "\",\"" + withCkeckBox + "\", \"" + isCheck + "\", \"" + isCheck + "\");";

		return result;
	}
	
	private String isAgentChecke(AgentWithServiceDto ag, List<AgentDto> listAgentsExistants) {
		
		if(null != ag
				&& null != ag.getIdAgent()
				&& null != listAgentsExistants
				&& !listAgentsExistants.isEmpty()) {
			for(AgentDto agentExistant : listAgentsExistants) {
				if(ag.getIdAgent().equals(agentExistant.getIdAgent())) {
					return "true";
				}
			}
		}
		
		return "false";
	}

}
