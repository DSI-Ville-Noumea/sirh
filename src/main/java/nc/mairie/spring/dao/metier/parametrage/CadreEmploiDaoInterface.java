package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.CadreEmploi;
import nc.mairie.spring.dao.metier.poste.CadreEmploiFEDao;

public interface CadreEmploiDaoInterface {

	public void creerCadreEmploi(String libelleCadreEmploi) throws Exception;

	public void supprimerCadreEmploi(Integer idCadreEmploi) throws Exception;

	public ArrayList<CadreEmploi> listerCadreEmploi() throws Exception;

	public CadreEmploi chercherCadreEmploi(Integer idCadreEmploi) throws Exception;

	public CadreEmploi chercherCadreEmploiByLib(String libCadreEmploi) throws Exception;

	public ArrayList<CadreEmploi> listerCadreEmploiAvecFicheEmploi(CadreEmploiFEDao cadreEmploiFEDao,
			Integer idFicheEmploi) throws Exception;

}
