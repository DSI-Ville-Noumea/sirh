package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;

import nc.mairie.metier.poste.CadreEmploiFE;

public interface CadreEmploiFEDaoInterface {

	public ArrayList<CadreEmploiFE> listerCadreEmploiFEAvecFicheEmploi(Integer idFicheEmploi) throws Exception;

	public CadreEmploiFE chercherCadreEmploiFE(Integer idFicheEmploi, Integer idCadreEmploi) throws Exception;

	public void creerCadreEmploiFE(Integer idFicheEmploi, Integer idCadreEmploi) throws Exception;

	public void supprimerCadreEmploiFE(Integer idFicheEmploi, Integer idCadreEmploi) throws Exception;

	public void supprimerCadreEmploiFEAvecFE(Integer idFicheEmploi) throws Exception;

}
