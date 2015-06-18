package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.eae.EaeEvaluateur;

public interface EaeEvaluateurDaoInterface {

	public void creerEaeEvaluateur(Integer idEae, Integer idAgent, String fonction, Date dateEntreeService,
			Date dateEntreeCollectivite, Date dateEntreeFonction) throws Exception;

	public ArrayList<EaeEvaluateur> listerEvaluateurEAE(Integer idEAE) throws Exception;

	public void supprimerEaeEvaluateur(Integer idEvaluateur) throws Exception;

	public void modifierDateEaeEvaluateur(Integer idEaeEvaluateur, EaeEvaluateur evaluateur);

}
