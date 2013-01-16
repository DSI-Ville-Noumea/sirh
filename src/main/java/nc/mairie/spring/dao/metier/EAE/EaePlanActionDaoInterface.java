package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.EAE.EaePlanAction;

public interface EaePlanActionDaoInterface {

	public ArrayList<EaePlanAction> listerPlanActionParType(Integer idEAE, Integer idtypeObj) throws Exception;

}
