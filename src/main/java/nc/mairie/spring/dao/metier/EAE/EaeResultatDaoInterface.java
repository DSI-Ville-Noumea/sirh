package nc.mairie.spring.dao.metier.EAE;

import java.sql.Connection;

public interface EaeResultatDaoInterface {

	public Connection creerEaeResultat(Integer idEae, Integer idTypeObjectif, String objectif, String resultat, String commentaire) throws Exception;

}
