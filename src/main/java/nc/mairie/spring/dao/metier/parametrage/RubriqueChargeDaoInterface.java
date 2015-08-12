package nc.mairie.spring.dao.metier.parametrage;

import java.util.List;

import nc.mairie.metier.parametrage.RubriqueCharge;

public interface RubriqueChargeDaoInterface {

	public List<RubriqueCharge> getListRubriqueCharge();

	public void creerRubriqueCharge(RubriqueCharge rubriqueChargeCourant);

	public void modifierRubriqueCharge(RubriqueCharge rubriqueChargeCourant);

	public void supprimerRubriqueCharge(RubriqueCharge rubriqueChargeCourant) throws Exception;

}
