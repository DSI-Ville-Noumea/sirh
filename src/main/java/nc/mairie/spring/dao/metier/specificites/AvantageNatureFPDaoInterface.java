package nc.mairie.spring.dao.metier.specificites;

import java.util.ArrayList;

import nc.mairie.metier.specificites.AvantageNatureFP;

public interface AvantageNatureFPDaoInterface {

	public void creerAvantageNatureFP(Integer idAvantage, Integer idFichePOste);

	public void supprimerAvantageNatureFP(Integer idAvantage, Integer idFichePOste);

	public ArrayList<AvantageNatureFP> listerAvantageNatureFPAvecFP(Integer idFichePoste);
}
