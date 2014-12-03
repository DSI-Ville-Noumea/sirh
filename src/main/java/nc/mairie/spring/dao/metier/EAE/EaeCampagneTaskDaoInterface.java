package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.eae.EaeCampagneTask;

public interface EaeCampagneTaskDaoInterface {

	void creerEaeCampagneTask(Integer idCampagneEae, Integer annee, Integer idAgent, Date dateCalculEae,
			String taskStatus) throws Exception;

	EaeCampagneTask chercherEaeCampagneTask(Integer idCampagneTask) throws Exception;

	EaeCampagneTask chercherEaeCampagneTaskByIdCampagneEae(Integer idCampagneEae) throws Exception;

	ArrayList<EaeCampagneTask> listerCampagneTask(Integer annee);
}
