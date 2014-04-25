package nc.mairie.spring.ws;

import nc.mairie.gestionagent.radi.dto.LightUserDto;

public interface IRadiWSConsumer {

	public boolean asAgentCompteAD(Integer nomatr);

	public LightUserDto getAgentCompteAD(Integer nomatr);

	public LightUserDto getAgentCompteADByLogin(String login);

	public String getEmployeeNumberWithNomatr(Integer nomatr);

	public String getIdAgentWithNomatr(Integer nomatr);

	public String getNomatrWithIdAgent(Integer idAgent);

	public String getNomatrWithEmployeeNumber(Integer employeeNumber);
}
