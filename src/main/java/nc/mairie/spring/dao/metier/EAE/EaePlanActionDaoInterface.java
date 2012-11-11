package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.EAE.EaePlanAction;

public interface EaePlanActionDaoInterface {

	public ArrayList<EaePlanAction> listerEaePlanAction(Integer idEAE) throws Exception;

	public ArrayList<EaePlanAction> listerEaePlanActionPourType(Integer idEAE, Integer idEaeTypeObjectif) throws Exception;

}
