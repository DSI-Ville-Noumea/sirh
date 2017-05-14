package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;

import nc.mairie.metier.hsct.BeneficiaireObligationAmenage;

public interface BeneficiaireObligationAmenageDaoInterface {

	public void creerBeneficiaireObligationAmenage(BeneficiaireObligationAmenage boe) throws Exception;

	public void modifierBeneficiaireObligationAmenage(BeneficiaireObligationAmenage boe) throws Exception;

	public void supprimerBeneficiaireObligationAmenage(Integer idBoe) throws Exception;

	public BeneficiaireObligationAmenage chercherBeneficiaireObligationAmenage(Integer idBoe) throws Exception;

	public ArrayList<BeneficiaireObligationAmenage> listerBeneficiaireObligationAmenageByAgent(Integer idAgent) throws Exception;
}
