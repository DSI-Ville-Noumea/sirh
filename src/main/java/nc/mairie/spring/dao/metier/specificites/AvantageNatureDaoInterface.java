package nc.mairie.spring.dao.metier.specificites;

import java.util.ArrayList;

import nc.mairie.metier.specificites.AvantageNature;

public interface AvantageNatureDaoInterface {

	public void supprimerAvantageNature(Integer idAvantage) throws Exception;

	public Integer creerAvantageNature(Integer numRubrique, Integer idTypeAvantage, Integer idNatureAvantage,
			Double montant);

	public ArrayList<AvantageNature> listerAvantageNatureAvecAFF(Integer idAffectation) throws Exception;

	public ArrayList<AvantageNature> listerAvantageNatureAvecFP(Integer idFichePoste) throws Exception;

	public ArrayList<AvantageNature> listerAvantageNatureAvecTypeAvantage(Integer idTypeAvantage) throws Exception;

	public ArrayList<AvantageNature> listerAvantageNatureAvecNatureAvantage(Integer idNatureAvantage) throws Exception;

}
