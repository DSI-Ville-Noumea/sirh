package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.List;

import nc.mairie.metier.poste.DiplomeFE;
import nc.mairie.metier.poste.FEFP;
import nc.mairie.metier.poste.FicheEmploi;

public interface FicheEmploiDaoInterface {

	public void supprimerFicheEmploi(Integer idFicheEmploi) throws Exception;

	public void modifierFicheEmploi(Integer idFicheEmploi, Integer idDomaineFe, Integer idFamilleEmploi,
			String refMairie, String nomMetierEmploi, String precisionsDiplomes, String lienHierarchique,
			String definitionEmploi, Integer idCodeRome) throws Exception;

	public void creerFicheEmploi(Integer idDomaineFe, Integer idFamilleEmploi, String refMairie,
			String nomMetierEmploi, String precisionsDiplomes, String lienHierarchique, String definitionEmploi,
			Integer idCodeRome) throws Exception;

	public Integer genererNumChrono(String prefixe) throws Exception;

	public FicheEmploi chercherFicheEmploiAvecRefMairie(String refMairie) throws Exception;

	public FicheEmploi chercherFicheEmploi(Integer idFicheEmploi) throws Exception;

	public FicheEmploi chercherFicheEmploiAvecFichePoste(boolean emploiPrimaire, ArrayList<FEFP> liens)
			throws Exception;

	public ArrayList<FicheEmploi> listerFicheEmploiAvecDiplome(ArrayList<DiplomeFE> liens)
			throws Exception;

	public ArrayList<FicheEmploi> listerFicheEmploiAvecFamilleEmploi(Integer idFamilleEmploi) throws Exception;

	public ArrayList<FicheEmploi> listerFicheEmploiAvecDomaineEmploi(Integer idDomaineEmploi) throws Exception;

	public ArrayList<FicheEmploi> listerFicheEmploiavecRefMairie(String refMairie) throws Exception;

	public List<FicheEmploi> listerFicheEmploi() throws Exception;

	public ArrayList<FicheEmploi> listerFicheEmploiAvecCodeRome(Integer idCodeRome) throws Exception;

	public ArrayList<FicheEmploi> listerFicheEmploiAvecCriteresAvances(Integer idDomaineEmploi, Integer idFamEmploi,
			String codeRome, String refMairie, String nomMetierEmploi) throws Exception;

}
