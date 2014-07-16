package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.MotifNonRecrutement;

public interface MotifNonRecrutementDaoInterface {

	public void creerMotifNonRecrutement(String libelleMotifNonRecrutement) throws Exception;

	public void supprimerMotifNonRecrutement(Integer idMotifNonRecrutement) throws Exception;

	public ArrayList<MotifNonRecrutement> listerMotifNonRecrutement() throws Exception;

}
