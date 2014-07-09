package nc.mairie.spring.dao.metier.specificites;

import java.util.ArrayList;

import nc.mairie.metier.specificites.PrimePointageFP;

public interface PrimePointageFPDaoInterface {

	public void creerPrimePointageFP(Integer numRubrique, Integer idFichePoste);

	public void supprimerPrimePointageFP(Integer idFichePoste, Integer numRubrique);

	public ArrayList<PrimePointageFP> listerPrimePointageFP(Integer idFichePoste);
}
