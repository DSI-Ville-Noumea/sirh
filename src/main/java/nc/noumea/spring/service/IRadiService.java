package nc.noumea.spring.service;

import nc.mairie.gestionagent.radi.dto.LightUserDto;

public interface IRadiService {

	LightUserDto getAgentCompteADByLogin(String loginUtilisateur);

	Integer getNomatrWithEmployeeNumber(Integer employeeNumber);

	boolean asAgentCompteAD(Integer nomatr);
}
