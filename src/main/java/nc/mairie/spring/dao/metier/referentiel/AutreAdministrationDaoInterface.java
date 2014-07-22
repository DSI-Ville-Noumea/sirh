package nc.mairie.spring.dao.metier.referentiel;

import java.util.ArrayList;

import nc.mairie.metier.referentiel.AutreAdministration;

public interface AutreAdministrationDaoInterface {

	public ArrayList<AutreAdministration> listerAutreAdministration() throws Exception;

	public AutreAdministration chercherAutreAdministration(Integer idAutreAdmin) throws Exception;

	public void supprimerAutreAdministration(Integer idAutreAdmin) throws Exception;

	public void creerAutreAdministration(String libAutreAdmin) throws Exception;

}
