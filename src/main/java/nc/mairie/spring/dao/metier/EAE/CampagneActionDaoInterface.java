package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.eae.CampagneAction;

public interface CampagneActionDaoInterface {

	public ArrayList<CampagneAction> listerCampagneActionPourCampagne(Integer idCampagneEAE) throws Exception;

	public CampagneAction chercherCampagneAction(Integer idCampagneAction) throws Exception;

	public Integer creerCampagneAction(String nomAction, String message, Date transmettreLe, Date pourLe, Date faitLe,
			String commentaire, Integer idAgentRealisation, Integer idCampagneEAE) throws Exception;

	public void modifierCampagneAction(Integer idCampagneAction, String nomAction, String message,
			Date dateTransmission, Date dateAFaireLe, Date dateFaitLe, String commentaire, Integer idAgentRealisation)
			throws Exception;

	public void supprimerCampagneAction(Integer idCampagneAction) throws Exception;
}
