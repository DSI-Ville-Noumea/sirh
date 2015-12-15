package nc.noumea.spring.service;

import java.util.List;

import nc.mairie.gestionagent.dto.EntiteWithAgentWithServiceDto;
import nc.noumea.mairie.ads.dto.EntiteDto;

public interface IAdsService {

	EntiteDto getCurrentWholeTree();

	EntiteDto getAffichageDirection(Integer idEntite);

	EntiteDto getAffichageSection(Integer idEntite);

	EntiteDto getAffichageService(Integer idEntite);

	String getCurrentWholeTreeActifTransitoireJS(String serviceSaisi, boolean withSelectionRadioBouton);

	EntiteDto getEntiteByIdEntite(Integer idEntite);

	List<Integer> getListIdsEntiteWithEnfantsOfEntite(Integer idEntite);

	EntiteDto getEntiteBySigle(String sigle);

	EntiteDto getEntiteWithChildrenByIdEntite(Integer idEntite);

	List<String> getListSiglesWithEnfantsOfEntite(Integer idEntite);

	EntiteDto getEntiteByCodeServiceSISERV(String serviAS400);

	String getCurrentWholeTreePrevisionActifTransitoireJS(String serviceSaisi, boolean withSelectionRadioBouton);

	List<EntiteDto> getListEntiteByStatut(Integer idStatut);

	EntiteDto getInfoSiservByIdEntite(Integer idEntite);

	List<EntiteDto> getListEntiteDto(EntiteDto entiteWithChildren);

	EntiteDto getListEntiteDtoByIdService(List<EntiteDto> listEntiteDto, Integer idService);

	String getCurrentWholeTreeWithAgent(EntiteWithAgentWithServiceDto tree, boolean withSelectionRadioBouton);
}
