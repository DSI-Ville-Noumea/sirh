package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.metier.poste.Recrutement;

public interface RecrutementDaoInterface {

	public ArrayList<Recrutement> listerRecrutementAvecMotifNonRec(Integer idMotifNonRecr) throws Exception;

	public ArrayList<Recrutement> listerRecrutementAvecMotifRec(Integer idMotifRecr) throws Exception;

	public void creerRecrutement(Integer idMotifRecrut, Integer idMotifNonRecrut, Integer idFichePoste,
			Integer referenceSes, String referenceMairie, String referenceDrhfpnc, Date dateOuverture,
			Date dateValidation, Date dateCloture, Date dateTransmission, Date dateReponse, Integer nbCandRecues,
			String nomAgentRecrute) throws Exception;

	public void supprimerRecrutement(Integer idRecrutement) throws Exception;


}
