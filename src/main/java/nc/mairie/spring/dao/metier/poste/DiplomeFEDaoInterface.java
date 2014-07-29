package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;

import nc.mairie.metier.poste.DiplomeFE;

public interface DiplomeFEDaoInterface {

	public void supprimerDiplomeFEAvecFE(Integer idFicheEmploi) throws Exception;

	public void supprimerDiplomeFE(Integer idFicheEmploi, Integer idDiplome) throws Exception;

	public void creerDiplomeFE(Integer idFicheEmploi, Integer idDiplome) throws Exception;

	public DiplomeFE chercherDiplomeFE(Integer idFicheEmploi, Integer idDiplome) throws Exception;

	public ArrayList<DiplomeFE> listerDiplomeFEAvecDiplome(Integer idDiplome) throws Exception;

	public ArrayList<DiplomeFE> listerDiplomeFEAvecFE(Integer idFicheEmploi) throws Exception;

}
