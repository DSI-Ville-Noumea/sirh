package nc.mairie.metier.poste;

import java.util.Date;

/**
 * Objet metier Recrutement
 */
public class Recrutement {

	public Integer idRecrutement;
	public Integer idMotifRecrut;
	public Integer idMotifNonRecrut;
	public Integer idFichePoste;
	public Integer referenceSes;
	public String referenceMairie;
	public String referenceDrhfpnc;
	public Date dateOuverture;
	public Date dateValidation;
	public Date dateCloture;
	public Date dateTransmission;
	public Date dateReponse;
	public Integer nbCandRecues;
	public String nomAgentRecrute;

	/**
	 * Constructeur Recrutement.
	 */
	public Recrutement() {
		super();
	}

	/**
	 * Getter de l'attribut idRecrutement.
	 */
	public Integer getIdRecrutement() {
		return idRecrutement;
	}

	/**
	 * Setter de l'attribut idRecrutement.
	 */
	public void setIdRecrutement(Integer newIdRecrutement) {
		idRecrutement = newIdRecrutement;
	}

	/**
	 * Getter de l'attribut idMotifRecrut.
	 */
	public Integer getIdMotifRecrut() {
		return idMotifRecrut;
	}

	/**
	 * Setter de l'attribut idMotifRecrut.
	 */
	public void setIdMotifRecrut(Integer newIdMotifRecrut) {
		idMotifRecrut = newIdMotifRecrut;
	}

	/**
	 * Getter de l'attribut idMotifNonRecrut.
	 */
	public Integer getIdMotifNonRecrut() {
		return idMotifNonRecrut;
	}

	/**
	 * Setter de l'attribut idMotifNonRecrut.
	 */
	public void setIdMotifNonRecrut(Integer newIdMotifNonRecrut) {
		idMotifNonRecrut = newIdMotifNonRecrut;
	}

	/**
	 * Getter de l'attribut idFichePoste.
	 */
	public Integer getIdFichePoste() {
		return idFichePoste;
	}

	/**
	 * Setter de l'attribut idFichePoste.
	 */
	public void setIdFichePoste(Integer newIdFichePoste) {
		idFichePoste = newIdFichePoste;
	}

	/**
	 * Getter de l'attribut refSES.
	 */
	public Integer getReferenceSes() {
		return referenceSes;
	}

	/**
	 * Setter de l'attribut refSES.
	 */
	public void setReferenceSes(Integer newRefSES) {
		referenceSes = newRefSES;
	}

	/**
	 * Getter de l'attribut refMairie.
	 */
	public String getReferenceMairie() {
		return referenceMairie;
	}

	/**
	 * Setter de l'attribut refMairie.
	 */
	public void setReferenceMairie(String newRefMairie) {
		referenceMairie = newRefMairie;
	}

	/**
	 * Getter de l'attribut refDRHFPNC.
	 */
	public String getReferenceDrhfpnc() {
		return referenceDrhfpnc;
	}

	/**
	 * Setter de l'attribut refDRHFPNC.
	 */
	public void setReferenceDrhfpnc(String newRefDRHFPNC) {
		referenceDrhfpnc = newRefDRHFPNC;
	}

	/**
	 * Getter de l'attribut dateOuverture.
	 */
	public Date getDateOuverture() {
		return dateOuverture;
	}

	/**
	 * Setter de l'attribut dateOuverture.
	 */
	public void setDateOuverture(Date newDateOuverture) {
		dateOuverture = newDateOuverture;
	}

	/**
	 * Getter de l'attribut dateValidation.
	 */
	public Date getDateValidation() {
		return dateValidation;
	}

	/**
	 * Setter de l'attribut dateValidation.
	 */
	public void setDateValidation(Date newDateValidation) {
		dateValidation = newDateValidation;
	}

	/**
	 * Getter de l'attribut dateCloture.
	 */
	public Date getDateCloture() {
		return dateCloture;
	}

	/**
	 * Setter de l'attribut dateCloture.
	 */
	public void setDateCloture(Date newDateCloture) {
		dateCloture = newDateCloture;
	}

	/**
	 * Getter de l'attribut dateTransmission.
	 */
	public Date getDateTransmission() {
		return dateTransmission;
	}

	/**
	 * Setter de l'attribut dateTransmission.
	 */
	public void setDateTransmission(Date newDateTransmission) {
		dateTransmission = newDateTransmission;
	}

	/**
	 * Getter de l'attribut dateReponse.
	 */
	public Date getDateReponse() {
		return dateReponse;
	}

	/**
	 * Setter de l'attribut dateReponse.
	 */
	public void setDateReponse(Date newDateReponse) {
		dateReponse = newDateReponse;
	}

	/**
	 * Getter de l'attribut nbCandRecues.
	 */
	public Integer getNbCandRecues() {
		return nbCandRecues;
	}

	/**
	 * Setter de l'attribut nbCandRecues.
	 */
	public void setNbCandRecues(Integer newNbCandRecues) {
		nbCandRecues = newNbCandRecues;
	}

	/**
	 * Getter de l'attribut nomAgentRecrute.
	 */
	public String getNomAgentRecrute() {
		return nomAgentRecrute;
	}

	/**
	 * Setter de l'attribut nomAgentRecrute.
	 */
	public void setNomAgentRecrute(String newNomAgentRecrute) {
		nomAgentRecrute = newNomAgentRecrute;
	}

	@Override
	public boolean equals(Object object) {
		return idRecrutement.toString().equals(((Recrutement) object).getIdRecrutement().toString());
	}
}
