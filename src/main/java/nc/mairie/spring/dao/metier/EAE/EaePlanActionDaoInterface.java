package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;

import nc.mairie.metier.eae.EaePlanAction;

public interface EaePlanActionDaoInterface {

	public ArrayList<EaePlanAction> listerPlanActionParType(Integer idEAE, Integer idtypeObj) throws Exception;

	public void creerPlanAction(Integer idEae, Integer idTypeObjectif, String objectif, String mesure) throws Exception;

	public void supprimerEaePlanAction(Integer idEaePlanAction) throws Exception;

	public void modifierEaePlanAction(Integer idEaePlanAction, Integer idTypeObjectif, String objectif, String mesure) throws Exception;
}
