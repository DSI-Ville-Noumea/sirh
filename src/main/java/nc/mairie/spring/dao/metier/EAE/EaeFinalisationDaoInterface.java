package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.EAE.EaeFinalisation;

public interface EaeFinalisationDaoInterface {

	public String chercherDernierDocumentFinalise(Integer idEAE) throws Exception;

	public ArrayList<EaeFinalisation> listerDocumentFinalise(Integer idEAE) throws Exception;

}
