package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;

import nc.mairie.metier.poste.AutreAppellationEmploi;

public interface AutreAppellationEmploiDaoInterface {

	public void supprimerAutreAppellationEmploiAvecFE(Integer idFicheEmploi) throws Exception;

	public void supprimerAutreAppellationEmploi(Integer idAutreAppellationEmploi) throws Exception;

	public void creerAutreAppellationEmploi(Integer idFicheEmploi, String libAutreAppellationEmploi) throws Exception;

	public ArrayList<AutreAppellationEmploi> listerAutreAppellationEmploiAvecFE(Integer idFicheEmploi) throws Exception;

}
