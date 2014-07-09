package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.RepresentantCap;

public interface RepresentantCapDaoInterface {


	public ArrayList<RepresentantCap> listerRepresentantCapParRepresentant(Integer idRepresentant) throws Exception;

	public ArrayList<RepresentantCap> listerRepresentantCapParCap(Integer idCap) throws Exception;

	public void creerRepresentantCap(Integer idRepresentant, Integer idCap, Integer position) throws Exception;

	public void supprimerRepresentantCapParCap(Integer idCap) throws Exception;

}
