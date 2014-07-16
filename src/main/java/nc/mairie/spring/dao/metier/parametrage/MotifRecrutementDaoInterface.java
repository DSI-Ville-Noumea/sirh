package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.MotifRecrutement;

public interface MotifRecrutementDaoInterface {

	public void creerMotifRecrutement(String libelleMotifRecrutement) throws Exception;

	public void supprimerMotifRecrutement(Integer idMotifRecrutement) throws Exception;

	public ArrayList<MotifRecrutement> listerMotifRecrutement() throws Exception;

}
