package nc.mairie.spring.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ReferenceDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

@Service
public class ADSWSConsumer extends BaseWsConsumer implements IADSWSConsumer {

	private Logger logger = LoggerFactory.getLogger(ADSWSConsumer.class);

	@Autowired
	@Qualifier("adsWsBaseUrl")
	private String adsWsBaseUrl;

	private static final String sirhAdsGetEntiteUrl = "api/entite/";
	private static final String sirhAdsGetEntiteWithWildrenUrl = "/withChildren";
	private static final String sirhAdsGetEntiteByStatutUrl = "api/entite/byStatut";
	private static final String sirhAdsGetEntiteBySigleUrl = "api/entite/sigle/";
	private static final String sirhAdsGetTypeEntiteUrl = "api/typeEntite";
	private static final String sirhAdsGetParentOfEntiteByTypeEntiteUrl = "api/entite/parentOfEntiteByTypeEntite";
	private static final String sirhAdsGetEntiteByCodeServiceSISERVUrl = "api/entite/codeAs400/";
	private static final String sirhAdsGetInfoSiservUrl = "api/entite/infoSiserv/";
	
	

	@Override
	public EntiteDto getEntiteByIdEntite(Integer idEntite) {

		if (null == idEntite) {
			return null;
		}

		String url = String.format(adsWsBaseUrl + sirhAdsGetEntiteUrl + idEntite.toString());

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireRequest(parameters, url);
		try {
			return readResponse(EntiteDto.class, res, url);
		} catch (Exception e) {
			logger.error("L'application ADS ne repond pas." + e.getMessage());
		}

		return null;
	}

	@Override
	public EntiteDto getEntiteWithChildrenByIdEntite(Integer idEntite) {

		if (null == idEntite) {
			return null;
		}

		String url = String.format(adsWsBaseUrl + sirhAdsGetEntiteUrl + idEntite.toString()
				+ sirhAdsGetEntiteWithWildrenUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireRequest(parameters, url);
		try {
			return readResponse(EntiteDto.class, res, url);
		} catch (Exception e) {
			logger.error("L'application ADS ne repond pas." + e.getMessage());
		}

		return null;
	}

	@Override
	public EntiteDto getEntiteBySigle(String sigle) {

		if (null == sigle) {
			return null;
		}

		// #39324 : On ne passe pas par un String.format() à cause des espaces à changer en '%20' pour l'URL
		String url = adsWsBaseUrl + sirhAdsGetEntiteBySigleUrl + sigle.replaceAll(" ", "%20");

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireRequest(parameters, url);
		try {
			return readResponse(EntiteDto.class, res, url);
		} catch (Exception e) {
			logger.error("L'application ADS ne repond pas." + e.getMessage());
		}

		return null;
	}

	@Override
	public List<ReferenceDto> getListTypeEntite() {
		String url = String.format(adsWsBaseUrl + sirhAdsGetTypeEntiteUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireRequest(parameters, url);

		try {
			return readResponseAsList(ReferenceDto.class, res, url);
		} catch (Exception e) {
			logger.error("L'application ADS ne repond pas." + e.getMessage());
		}

		return new ArrayList<ReferenceDto>();
	}

	@Override
	public EntiteDto getParentOfEntiteByTypeEntite(Integer idEntite, Integer idTypeEntite) {
		String url = String.format(adsWsBaseUrl + sirhAdsGetParentOfEntiteByTypeEntiteUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idEntite", idEntite.toString());
		parameters.put("idTypeEntite", idTypeEntite.toString());

		ClientResponse res = createAndFireRequest(parameters, url);

		try {
			return readResponse(EntiteDto.class, res, url);
		} catch (Exception e) {
			logger.error("L'application ADS ne repond pas." + e.getMessage());
		}

		return null;
	}

	@Override
	public EntiteDto getEntiteByCodeServiceSISERV(String serviAS400) {

		if (null == serviAS400) {
			return null;
		}

		String url = String.format(adsWsBaseUrl + sirhAdsGetEntiteByCodeServiceSISERVUrl + serviAS400);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireRequest(parameters, url);
		try {
			return readResponse(EntiteDto.class, res, url);
		} catch (Exception e) {
			logger.error("L'application ADS ne repond pas." + e.getMessage());
		}

		return null;
	}

	@Override
	public List<EntiteDto> getListEntiteByStatut(Integer idStatut) {

		String url = String.format(adsWsBaseUrl + sirhAdsGetEntiteByStatutUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idStatut", idStatut.toString());

		ClientResponse res = createAndFireRequest(parameters, url);

		try {
			return readResponseAsList(EntiteDto.class, res, url);
		} catch (Exception e) {
			logger.error("L'application ADS ne repond pas." + e.getMessage());
		}

		return null;
	}

	@Override
	public EntiteDto getInfoSiservByIdEntite(Integer idEntite) {

		if (null == idEntite) {
			return null;
		}

		String url = String.format(adsWsBaseUrl + sirhAdsGetInfoSiservUrl + idEntite);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireRequest(parameters, url);
		try {
			return readResponse(EntiteDto.class, res, url);
		} catch (Exception e) {
			logger.error("L'application ADS ne repond pas." + e.getMessage());
		}

		return null;
	}

}
