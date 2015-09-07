package nc.mairie.spring.dao.metier.poste;

import java.util.List;

import nc.mairie.metier.poste.ActionFdpJob;

public interface ActionFdpJobDaoInterface {

	List<ActionFdpJob> listerActionFdpJobSuppression();

	List<ActionFdpJob> listerActionFdpJobSuppressionErreur();

	List<ActionFdpJob> listerActionFdpJobActivation();

	List<ActionFdpJob> listerActionFdpJobActivationErreur();

	List<ActionFdpJob> listerActionFdpJobDuplication();

	List<ActionFdpJob> listerActionFdpJobDuplicationErreur();

}
