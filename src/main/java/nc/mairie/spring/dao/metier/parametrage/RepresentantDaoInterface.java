package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.parametrage.Representant;

public interface RepresentantDaoInterface {

	public ArrayList<Representant> listerRepresentantOrderByNom() throws Exception;

	public Representant chercherRepresentant(Integer idRepresentant) throws Exception;

	public void creerRepresentant(Integer idTypeRepresentant, String nomRepresentant, String prenomRepresentant) throws Exception;

	public void modifierRepresentant(Integer idRepresentant, Integer idTypeRepresentant, String nomRepresentant, String prenomRepresentant)
			throws Exception;

	public void supprimerRepresentant(Integer idRepresentant) throws Exception;

}
