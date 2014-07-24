package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.List;

import nc.mairie.metier.poste.CategorieFE;

public interface CategorieFEDaoInterface {

	public ArrayList<CategorieFE> listerCategorieFEAvecCategorie(Integer idCategorie) throws Exception;

	public void supprimerCategorieFEAvecFE(Integer idFicheEmploi) throws Exception;

	public void supprimerCategorieFE(Integer idFicheEmploi, Integer idCategorie) throws Exception;

	public void creerCategorieFE(Integer idFicheEmploi, Integer idCategorie) throws Exception;

	public CategorieFE chercherCategorieFE(Integer idFicheEmploi, Integer idCategorie) throws Exception;

	public ArrayList<CategorieFE> listerCategorieFEAvecFE(Integer idFicheEmploi) throws Exception;

	public List<CategorieFE> listerCategorieFE() throws Exception;

}
