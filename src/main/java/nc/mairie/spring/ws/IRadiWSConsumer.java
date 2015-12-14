package nc.mairie.spring.ws;

import nc.mairie.gestionagent.radi.dto.LightUserDto;

public interface IRadiWSConsumer {

	public boolean asAgentCompteAD(Integer nomatr);

	public LightUserDto getAgentCompteADByLogin(String login);

	public Integer getEmployeeNumberWithNomatr(Integer nomatr);

	public Integer getIdAgentWithNomatr(Integer nomatr);

	public Integer getNomatrWithIdAgent(Integer idAgent);

	public Integer getNomatrWithEmployeeNumber(Integer employeeNumber);
}
