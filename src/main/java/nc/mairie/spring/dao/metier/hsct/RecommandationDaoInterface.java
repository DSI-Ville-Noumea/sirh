package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;

import nc.mairie.metier.hsct.Recommandation;

public interface RecommandationDaoInterface {

	public void creerRecommandation(String description) throws Exception;

	public void supprimerRecommandation(Integer idRecommandation) throws Exception;

	public ArrayList<Recommandation> listerRecommandation() throws Exception;

	public Recommandation chercherRecommandation(Integer idRecommandation) throws Exception;

}
