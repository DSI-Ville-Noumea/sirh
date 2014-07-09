package nc.mairie.spring.dao.metier.eae;

import java.util.ArrayList;

import nc.mairie.metier.eae.EaeFinalisation;

public interface EaeFinalisationDaoInterface {

	public String chercherDernierDocumentFinalise(Integer idEAE) throws Exception;

	public ArrayList<EaeFinalisation> listerDocumentFinalise(Integer idEAE) throws Exception;

}
