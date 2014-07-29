package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;

import nc.mairie.metier.poste.TitrePoste;

public interface TitrePosteDaoInterface {

	public ArrayList<TitrePoste> listerTitrePoste() throws Exception;

	public TitrePoste chercherTitrePoste(Integer idTitrePoste) throws Exception;

	public void creerTitrePoste(String libTitrePoste) throws Exception;

	public void supprimerTitrePoste(Integer idTitrePoste) throws Exception;

}
