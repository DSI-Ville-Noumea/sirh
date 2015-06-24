package nc.mairie.spring.dao.metier.specificites;

import java.util.ArrayList;

import nc.mairie.metier.specificites.DelegationFP;

public interface DelegationFPDaoInterface {

	public void creerDelegationFP(Integer idDelegation, Integer idFichePoste);

	public void supprimerDelegationFP(Integer idDelegation, Integer idFichePoste);

	public ArrayList<DelegationFP> listerDelegationFPAvecFP(Integer idFichePoste);
}
