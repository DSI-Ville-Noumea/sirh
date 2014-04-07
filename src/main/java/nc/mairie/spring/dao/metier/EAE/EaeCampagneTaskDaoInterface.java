package nc.mairie.spring.dao.metier.EAE;

import java.util.Date;

import nc.mairie.spring.domain.metier.EAE.EaeCampagneTask;

public interface EaeCampagneTaskDaoInterface {

	Integer creerEaeCampagneTask(Integer idCampagneEae, Integer annee, String idAgent, Date dateCalculEae, String taskStatus) throws Exception;
	
	EaeCampagneTask chercherEaeCampagneTask(Integer idCampagneTask) throws Exception;
	
	EaeCampagneTask chercherEaeCampagneTaskByIdCampagneEae(Integer idCampagneEae) throws Exception;
}
