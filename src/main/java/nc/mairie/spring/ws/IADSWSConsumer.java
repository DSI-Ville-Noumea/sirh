package nc.mairie.spring.ws;

import java.util.List;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ReferenceDto;

public interface IADSWSConsumer {

	EntiteDto getEntiteByIdEntite(Integer idEntite);

	EntiteDto getEntiteWithChildrenByIdEntite(Integer idEntite);

	EntiteDto getEntiteBySigle(String sigle);

	List<ReferenceDto> getListTypeEntite();

	EntiteDto getParentOfEntiteByTypeEntite(Integer idEntite, Integer idTypeEntite);

	EntiteDto getEntiteByCodeServiceSISERV(String serviAS400);

	List<EntiteDto> getListEntiteByStatut(Integer idStatut);

	EntiteDto getInfoSiservByIdEntite(Integer idEntite);
}
