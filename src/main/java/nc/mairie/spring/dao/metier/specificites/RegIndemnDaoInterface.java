package nc.mairie.spring.dao.metier.specificites;

import java.util.ArrayList;

import nc.mairie.metier.specificites.RegimeIndemnitaire;

public interface RegIndemnDaoInterface {

	public Integer creerRegimeIndemnitaire(Integer idTypeRegIndemn, Integer numRubrique, Double forfait,
			Integer nombrePoints);

	public void supprimerRegimeIndemnitaire(Integer idRegime) throws Exception;

	public ArrayList<RegimeIndemnitaire> listerRegimeIndemnitaireAvecTypeRegime(Integer idTypeRegIndemn)
			throws Exception;

	public ArrayList<RegimeIndemnitaire> listerRegimeIndemnitaireAvecFP(Integer idFichePoste) throws Exception;

	public ArrayList<RegimeIndemnitaire> listerRegimeIndemnitaireAvecAFF(Integer idAffectation) throws Exception;

}
