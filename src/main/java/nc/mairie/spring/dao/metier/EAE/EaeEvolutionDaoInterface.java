package nc.mairie.spring.dao.metier.EAE;

import java.util.Date;

import nc.mairie.metier.eae.EaeEvolution;

public interface EaeEvolutionDaoInterface {

	public EaeEvolution chercherEaeEvolution(Integer idEAE) throws Exception;

	public void modifierMobiliteEaeEvolution(Integer idEaeEvolution, boolean mobGeo, boolean mobFonct, boolean mobServ, boolean mobDir,
			boolean mobColl, boolean mobAutre) throws Exception;

	public void modifierChangementMetierEaeEvolution(Integer idEaeEvolution, boolean chgtMetier) throws Exception;

	public void modifierDelaiEaeEvolution(Integer idEaeEvolution, String delai) throws Exception;

	public void modifierAutresInfosEaeEvolution(Integer idEaeEvolution, boolean concours, boolean vae, boolean tempsPartiel, boolean retraite,
			boolean autrePerspective) throws Exception;

	public void modifierLibelleEaeEvolution(Integer idEaeEvolution, String nomCollectivite, String nomConcours, String nomVae,
			String libAutrePerspective) throws Exception;

	public void modifierDateRetraiteEaeEvolution(Integer idEaeEvolution, Date dateRetraite) throws Exception;

	public void modifierCommentaireEaeEvaluation(Integer idEaeEvolution, Integer idCree) throws Exception;

	public void modifierPourcTpsPartielEaeEvolution(Integer idEaeEvolution, Integer idSpbhorTpsPartiel) throws Exception;

	public void creerEaeEvolution(EaeEvolution evolution) throws Exception;
}
