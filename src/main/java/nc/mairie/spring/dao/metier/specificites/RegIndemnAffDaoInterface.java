package nc.mairie.spring.dao.metier.specificites;

import java.util.ArrayList;

import nc.mairie.metier.specificites.RegIndemnAFF;

public interface RegIndemnAffDaoInterface {

	public void creerRegIndemnAFF(Integer idRegime, Integer idAffectation);

	public void supprimerRegIndemnAFF(Integer idRegime, Integer idAffectation);

	public RegIndemnAFF chercherRegIndemnAFF(Integer idRegime, Integer idAffectation) throws Exception;

	public ArrayList<RegIndemnAFF> listerRegIndemnAFFAvecRI(Integer idRegime) throws Exception;
}
