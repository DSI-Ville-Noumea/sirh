package nc.mairie.spring.dao.metier.EAE;

import java.util.Date;

import nc.mairie.spring.domain.metier.EAE.EaeCampagneTask;

public interface EaeCampagneTaskDaoInterface {

	void creerEaeCampagneTask(Integer idCampagneEae, Integer annee, Integer idAgent, Date dateCalculEae,
			String taskStatus) throws Exception;

	EaeCampagneTask chercherEaeCampagneTask(Integer idCampagneTask) throws Exception;

	EaeCampagneTask chercherEaeCampagneTaskByIdCampagneEae(Integer idCampagneEae) throws Exception;
}
