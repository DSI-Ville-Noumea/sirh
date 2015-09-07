package nc.noumea.spring.service;

import nc.mairie.gestionagent.radi.dto.LightUserDto;
import nc.mairie.spring.ws.IRadiWSConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RadiService implements IRadiService {

	@Autowired
	private IRadiWSConsumer radiConsumer;

	@Override
	public LightUserDto getAgentCompteADByLogin(String loginUtilisateur) {
		return radiConsumer.getAgentCompteADByLogin(loginUtilisateur);
	}

	@Override
	public Integer getNomatrWithEmployeeNumber(Integer employeeNumber) {
		return radiConsumer.getNomatrWithEmployeeNumber(employeeNumber);
	}

	@Override
	public boolean asAgentCompteAD(Integer nomatr) {
		return radiConsumer.asAgentCompteAD(nomatr);
	}
}
