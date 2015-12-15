package nc.noumea.spring.service;

import java.util.ArrayList;
import java.util.List;

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

		result.append(construitFinArbre(getCurrentTree(), serviceSaisi));

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

					result.append(ajouteNoeud(entite.getNfa(), entite, enfant, withSelectionRadioBouton, transitoire, style));
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

		result.append(construitFinArbre(getCurrentTree(), serviceSaisi));

		logger.debug("Fin construction Arbre Des Service avec provisoires, actifs et transitoires");

		return result.toString();
	}

	private String construitFinArbre(EntiteDto currentTree, String serviceSaisi) {
		StringBuilder result = new StringBuilder();

		result.append("document.write(d);");
		result.append("d.closeAll();");

		result.append(openToServiceSaisi(currentTree, serviceSaisi));

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
					result.append(ajouteNoeud(entite.getNfa(), entite, enfant, withSelectionRadioBouton, transitoire, style));
					result.append(buildTreeEntitiesProvisoireActifTransitoire(enfant, withSelectionRadioBouton));
				}

			}
		}

		return result.toString();
	}

	private Object ajouteNoeud(String nfa, EntiteDto entite, EntiteDto enfant, boolean withSelectionRadioBouton, String transitoire, String style) {
		String result = "";
		if (nfa == null || nfa.equals(Const.CHAINE_VIDE)) {
			result = "d.add(" + enfant.getIdEntite() + "," + entite.getIdEntite() + ",\"" + transitoire + enfant.getSigle() + " " + enfant.getLabel().replace("'", " ") + "\",'" + enfant.getSigle()
					+ "','" + enfant.getIdEntite() + "','',\"" + style + "\",'" + withSelectionRadioBouton + "');";
		} else {
			result = "d.add(" + enfant.getIdEntite() + "," + entite.getIdEntite() + ",\"" + transitoire + enfant.getSigle() + " " + enfant.getLabel().replace("'", " ") + "\",'" + enfant.getSigle()
					+ "','" + enfant.getIdEntite() + "','" + nfa + "',\"" + style + "\",'" + withSelectionRadioBouton + "');";
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

	@Override
	public String getCurrentWholeTreeWithAgent(EntiteWithAgentWithServiceDto tree, boolean withSelectionRadioBouton) {

		logger.debug("Debut construction Arbre Des Service avec agents");

		StringBuffer result = new StringBuffer();
		result.append(construitDebutArbreWithAgent(tree, withSelectionRadioBouton));

		result.append(buildTreeEntitiesWithAgent(tree, withSelectionRadioBouton));

		result.append(construitFinArbre(tree, null));

		logger.debug("Fin construction Arbre Des Service avec agents");

		return result.toString();
	}

	private String construitDebutArbreWithAgent(EntiteWithAgentWithServiceDto tree, boolean withSelectionRadioBouton) {
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
		result.append(selectServiceWithRadioBouton(tree));
		result.append("}");

		result.append("</script>");

		// generation de l arbre
		result.append("<SCRIPT language=\"javascript\" src=\"js/dtree.js\"></SCRIPT>");
		result.append("<script type=\"text/javascript\">");

		result.append("d = new dTree('d');");
		result.append("d.add(" + tree.getIdEntite() + ",-1,\"Services\");");
		return result.toString();
	}

	private String buildTreeEntitiesWithAgent(EntiteWithAgentWithServiceDto entite, boolean withSelectionRadioBouton) {
		StringBuffer result = new StringBuffer();
		if (null != entite) {
			// on ajoute les agents
			for (AgentWithServiceDto ag : entite.getListAgentWithServiceDto()) {
				String styleAgent = "font-weight: normal;";
				result.append(ajouteNoeudAgent(ag, entite, withSelectionRadioBouton, styleAgent));
			}
			if (entite.getEntiteEnfantWithAgents() != null) {
				for (EntiteWithAgentWithServiceDto enfant : entite.getEntiteEnfantWithAgents()) {

					result.append(ajouteNoeud(entite.getNfa(), entite, enfant, withSelectionRadioBouton, "", ""));
					result.append(buildTreeEntitiesWithAgent(enfant, withSelectionRadioBouton));
				}
			}
		}

		return result.toString();
	}

	private Object ajouteNoeudAgent(AgentWithServiceDto ag, EntiteDto entite, boolean withSelectionRadioBouton, String style) {
		String result = "";
		result = "d.add(" + ag.getIdAgent() + "," + entite.getIdEntite() + ",\"" + ag.getNom().replace("'", " ") + " " + ag.getPrenom().replace("'", " ") + "\",'" + ag.getNom() + "','"
				+ ag.getIdAgent() + "','',\"" + style + "\",'" + withSelectionRadioBouton + "');";

		return result;
	}

}
