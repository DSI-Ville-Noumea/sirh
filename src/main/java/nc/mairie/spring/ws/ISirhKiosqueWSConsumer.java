package nc.mairie.spring.ws;

import nc.mairie.gestionagent.dto.KiosqueDto;

public interface ISirhKiosqueWSConsumer {

	KiosqueDto setDroitEvalueEAE(String idDocument,boolean removeDroit);
}
