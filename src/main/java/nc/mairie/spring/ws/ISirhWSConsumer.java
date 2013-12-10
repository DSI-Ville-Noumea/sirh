package nc.mairie.spring.ws;

import nc.mairie.gestionagent.dto.SoldeCongeDto;

public interface ISirhWSConsumer {

	SoldeCongeDto getSoldeConge(String idAgent);

}
