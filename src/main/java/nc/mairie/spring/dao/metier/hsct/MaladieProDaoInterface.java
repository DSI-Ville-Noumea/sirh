package nc.mairie.spring.dao.metier.hsct;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.hsct.MaladiePro;

public interface MaladieProDaoInterface {

	public ArrayList<MaladiePro> listerMaladieProAgent(Integer idAgent) throws Exception;

	ArrayList<MaladiePro> listerMaladieProAvecTypeMP(Integer idTypeMp)
			throws Exception;

	public MaladiePro chercherMaladiePro(Integer idMP) throws Exception;

	public void modifierMaladiePro(Integer idMP, Integer idAgent, Boolean rechute,
			Date dateDeclaration, Date dateFin, Integer nbJoursITT,
			Integer avisCommission, Integer idTypeMP,
			Date dateTransmissionCafat, Date dateDecisionCafat,
			Integer tauxPrisEnChargeCafat, Date dateTransmissionAptitude) throws Exception;

	public void supprimerMaladiePro(Integer idMP) throws Exception;

	void creerMaladiePro(Integer idAgent, Boolean rechute,
			Date dateDeclaration, Date dateFin, Integer nbJoursITT,
			Integer avisCommission, Integer idTypeMP,
			Date dateTransmissionCafat, Date dateDecisionCafat,
			Integer tauxPrisEnChargeCafat, Date dateTransmissionAptitude)
			throws Exception;

}
