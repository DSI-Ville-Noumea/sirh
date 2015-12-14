package nc.mairie.spring.ws;

import java.util.HashMap;
import java.util.List;

import nc.mairie.gestionagent.radi.dto.LightUserDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

@Service
public class RadiWSConsumer extends BaseWsConsumer implements IRadiWSConsumer {
	private Logger logger = LoggerFactory.getLogger(RadiWSConsumer.class);

	@Autowired
	@Qualifier("radiWsBaseUrl")
	private String radiWsBaseUrl;

	// droits
	private static final String searchAgentRadi = "users";

	@Override
	public boolean asAgentCompteAD(Integer nomatr) {
		String url = String.format(radiWsBaseUrl + searchAgentRadi);
		HashMap<String, String> params = new HashMap<>();
		params.put("employeenumber", getEmployeeNumberWithNomatr(nomatr).toString());
		logger.debug("Call " + url + " with employeenumber=" + getEmployeeNumberWithNomatr(nomatr));
		ClientResponse res = createAndFireRequest(params, url);

		if (res.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return false;
		} else if (res.getStatus() == HttpStatus.OK.value()) {
			return true;
		} else {
			throw new BaseWsConsumerException(String.format("An error occured when querying '%s'. Return code is : %s", url, res.getStatus()));
		}
	}

	@Override
	public Integer getEmployeeNumberWithNomatr(Integer nomatr) {
		return Integer.valueOf("90" + nomatr);
	}

	@Override
	public Integer getIdAgentWithNomatr(Integer nomatr) {
		return Integer.valueOf("900" + nomatr);
	}

	@Override
	public Integer getNomatrWithIdAgent(Integer idAgent) {
		return Integer.valueOf(idAgent.toString().substring(3, idAgent.toString().length()));
	}

	@Override
	public LightUserDto getAgentCompteADByLogin(String login) {
		String url = String.format(radiWsBaseUrl + searchAgentRadi);
		HashMap<String, String> params = new HashMap<>();
		params.put("sAMAccountName", login);
		logger.debug("Call " + url + " with sAMAccountName=" + login);
		ClientResponse res = createAndFireRequest(params, url);
		List<LightUserDto> list = readResponseAsList(LightUserDto.class, res, url);
		return list.size() == 0 ? null : list.get(0);
	}

	@Override
	public Integer getNomatrWithEmployeeNumber(Integer employeeNumber) {
		return Integer.valueOf(employeeNumber.toString().substring(2, employeeNumber.toString().length()));
	}
}
