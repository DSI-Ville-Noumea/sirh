package nc.mairie.spring.dao.metier.eae;

import nc.mairie.metier.eae.EaeCommentaire;

public interface EaeCommentaireDaoInterface {

	public EaeCommentaire chercherEaeCommentaire(Integer idEaeCommentaire) throws Exception;

	public Integer creerEaeCommentaire(String commentaire) throws Exception;

	public void modifierEaeCommentaire(Integer idEaeCommentaire, String commentaire) throws Exception;

}
