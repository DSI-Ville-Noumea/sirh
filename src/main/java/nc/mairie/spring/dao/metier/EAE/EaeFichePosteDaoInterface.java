package nc.mairie.spring.dao.metier.EAE;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import nc.mairie.spring.domain.metier.EAE.EaeFichePoste;

public interface EaeFichePosteDaoInterface {

	public void creerEaeFichePoste(Integer id, Integer idEae, Integer idSHD, boolean typeFDP, String direction, String service, String section,
			String emploi, String fonction, Date dateEntreeFonction, String grade, String localisation, String mission, String fonctionResp,
			Date dateEntreeServiceResp, Date dateEntreeCollectiviteResp, Date dateEntreeFonctionResp, String codeService)
			throws Exception;

	public Integer getIdEaeFichePoste() throws Exception;

	public EaeFichePoste chercherEaeFichePoste(Integer idEAE, boolean typeFDP) throws Exception;

	public void supprimerEaeFichePoste(Integer idEaeFichePoste) throws Exception;

	public ArrayList<EaeFichePoste> listerEaeFichePosteGrouperParDirectionSection(Integer idCampagneEAE) throws Exception;

	public ArrayList<EaeFichePoste> chercherEaeFichePosteIdEae(Integer idEAE);

}
