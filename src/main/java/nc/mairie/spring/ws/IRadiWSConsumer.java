package nc.mairie.spring.ws;

import nc.mairie.gestionagent.radi.dto.LightUserDto;

public interface IRadiWSConsumer {

	boolean asAgentCompteAD(Integer nomatr);

	LightUserDto getAgentCompteADByLogin(String login);

	Integer getEmployeeNumberWithNomatr(Integer nomatr);

	Integer getIdAgentWithNomatr(Integer nomatr);

	Integer getNomatrWithIdAgent(Integer idAgent);

	Integer getNomatrWithEmployeeNumber(Integer employeeNumber);
}
