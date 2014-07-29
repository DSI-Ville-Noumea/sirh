package nc.mairie.spring.dao.metier.referentiel;

import java.util.ArrayList;
import java.util.List;

import nc.mairie.metier.poste.NiveauEtudeFE;
import nc.mairie.metier.referentiel.NiveauEtude;

public interface NiveauEtudeDaoInterface {

	public NiveauEtude chercherNiveauEtude(Integer idNiveau) throws Exception;

	public ArrayList<NiveauEtude> listerNiveauEtudeAvecFE(ArrayList<NiveauEtudeFE> liens) throws Exception;

	public List<NiveauEtude> listerNiveauEtude() throws Exception;

}
