package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.CadreEmploi;
import nc.mairie.metier.poste.FicheEmploi;
import nc.mairie.technique.Transaction;

public interface CadreEmploiDaoInterface {

	public void creerCadreEmploi(String libelleCadreEmploi) throws Exception;

	public void supprimerCadreEmploi(Integer idCadreEmploi) throws Exception;

	public ArrayList<CadreEmploi> listerCadreEmploi() throws Exception;

	public CadreEmploi chercherCadreEmploi(Integer idCadreEmploi) throws Exception;

	public CadreEmploi chercherCadreEmploiByLib(String libCadreEmploi) throws Exception;

	public ArrayList<CadreEmploi> listerCadreEmploiAvecFicheEmploi(Transaction aTransaction, FicheEmploi ficheEmploi)
			throws Exception;

}
