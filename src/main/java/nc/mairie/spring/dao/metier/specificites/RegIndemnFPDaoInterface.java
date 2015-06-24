package nc.mairie.spring.dao.metier.specificites;

import java.util.ArrayList;

import nc.mairie.metier.specificites.RegIndemFP;

public interface RegIndemnFPDaoInterface {

	public void creerRegIndemFP(Integer idRegime, Integer idFichePoste);

	public void supprimerRegIndemFP(Integer idRegime, Integer idFichePoste);

	public ArrayList<RegIndemFP> listerRegIndemFPFPAvecFP(Integer idFichePoste);
}
