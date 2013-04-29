package nc.mairie.gestionagent.dto;

import java.util.Date;

import nc.mairie.spring.dao.metier.EAE.EAEDao;
import nc.mairie.spring.dao.metier.EAE.EaeEvalueDao;
import nc.mairie.spring.domain.metier.EAE.EAE;

import com.ibm.icu.text.SimpleDateFormat;

public class EaeIdentificationSituationDto {

	public String directionService;
	public String fonction;
	public String emploi;
	public Date dateEntreeAdministration;
	public Date dateEntreeFonction;
	public Date dateEntreeFonctionnaire;
	private EAEDao eaeDao;
	private EaeEvalueDao eaeEvalueDao;

	public EaeIdentificationSituationDto() {

	}

	public EAEDao getEaeDao() {
		return eaeDao;
	}

	public void setEaeDao(EAEDao eaeDao) {
		this.eaeDao = eaeDao;
	}

	public EaeIdentificationSituationDto(EAE eae) throws Exception {

		this.directionService = "test direction";
		this.fonction = "test fonction";
		this.emploi = "test emploi";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		this.dateEntreeFonction = sdf.parse("2013-01-01");

		this.dateEntreeAdministration = sdf.parse("2013-01-01");
		this.dateEntreeFonctionnaire = sdf.parse("2013-01-01");
	}

	public String getDirectionService() {
		return directionService;
	}

	public void setDirectionService(String directionService) {
		this.directionService = directionService;
	}

	public String getFonction() {
		return fonction;
	}

	public void setFonction(String fonction) {
		this.fonction = fonction;
	}

	public String getEmploi() {
		return emploi;
	}

	public void setEmploi(String emploi) {
		this.emploi = emploi;
	}

	public Date getDateEntreeAdministration() {
		return dateEntreeAdministration;
	}

	public void setDateEntreeAdministration(Date dateEntreeAdministration) {
		this.dateEntreeAdministration = dateEntreeAdministration;
	}

	public Date getDateEntreeFonction() {
		return dateEntreeFonction;
	}

	public void setDateEntreeFonction(Date dateEntreeFonction) {
		this.dateEntreeFonction = dateEntreeFonction;
	}

	public Date getDateEntreeFonctionnaire() {
		return dateEntreeFonctionnaire;
	}

	public void setDateEntreeFonctionnaire(Date dateEntreeFonctionnaire) {
		this.dateEntreeFonctionnaire = dateEntreeFonctionnaire;
	}

	public EaeEvalueDao getEaeEvalueDao() {
		return eaeEvalueDao;
	}

	public void setEaeEvalueDao(EaeEvalueDao eaeEvalueDao) {
		this.eaeEvalueDao = eaeEvalueDao;
	}
}
