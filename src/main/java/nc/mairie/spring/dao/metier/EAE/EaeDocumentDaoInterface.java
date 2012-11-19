package nc.mairie.spring.dao.metier.EAE;

import java.util.ArrayList;

import nc.mairie.spring.domain.metier.EAE.EaeDocument;

public interface EaeDocumentDaoInterface {

	public void creerEaeDocument(Integer idCampagneEae, Integer idCampagneAction, Integer idDocument, String type) throws Exception;

	public EaeDocument chercherEaeDocument(Integer idDocument) throws Exception;

	public void supprimerEaeDocument(Integer idEaeDocument) throws Exception;

	public ArrayList<EaeDocument> listerEaeDocument(Integer idCampagneEae, Integer idCampagneAction, String type) throws Exception;
}
