package nc.mairie.metier.diplome;

import java.util.Date;

import nc.mairie.metier.Const;

/**
 * Objet metier DiplomeAgent
 */
public class DiplomeAgent {

	public Integer idDiplome;
	public Integer idTitreDiplome;
	public Integer idAgent;
	public Integer idDocument;
	public Integer idSpecialiteDiplome;
	public Date dateObtention;
	public String nomEcole;

	/**
	 * Constructeur DiplomeAgent.
	 */
	public DiplomeAgent() {
		super();
	}

	/**
	 * Getter de l'attribut idDiplome.
	 */
	public Integer getIdDiplome() {
		return idDiplome;
	}

	/**
	 * Setter de l'attribut idDiplome.
	 */
	public void setIdDiplome(Integer newIdDiplome) {
		idDiplome = newIdDiplome;
	}

	/**
	 * Getter de l'attribut idTitreDiplome.
	 */
	public Integer getIdTitreDiplome() {
		return idTitreDiplome;
	}

	/**
	 * Setter de l'attribut idTitreDiplome.
	 */
	public void setIdTitreDiplome(Integer newIdTitreDiplome) {
		idTitreDiplome = newIdTitreDiplome;
	}

	/**
	 * Getter de l'attribut idAgent.
	 */
	public Integer getIdAgent() {
		return idAgent;
	}

	/**
	 * Setter de l'attribut idAgent.
	 */
	public void setIdAgent(Integer newIdAgent) {
		idAgent = newIdAgent;
	}

	/**
	 * Getter de l'attribut idDocument.
	 */
	public Integer getIdDocument() {
		return idDocument;
	}

	/**
	 * Setter de l'attribut idDocument.
	 */
	public void setIdDocument(Integer newIdDocument) {
		idDocument = newIdDocument;
	}

	/**
	 * Getter de l'attribut idSpecialiteDiplome.
	 */
	public Integer getIdSpecialiteDiplome() {
		return idSpecialiteDiplome;
	}

	/**
	 * Setter de l'attribut idSpecialiteDiplome.
	 */
	public void setIdSpecialiteDiplome(Integer newIdSpecialiteDiplome) {
		idSpecialiteDiplome = newIdSpecialiteDiplome;
	}

	/**
	 * Getter de l'attribut dateObtention.
	 */
	public Date getDateObtention() {
		return dateObtention;
	}

	/**
	 * Setter de l'attribut dateObtention.
	 */
	public void setDateObtention(Date newDateObtention) {
		dateObtention = newDateObtention;
	}

	/**
	 * Getter de l'attribut nomEcole.
	 */
	public String getNomEcole() {
		return nomEcole == null ? Const.CHAINE_VIDE : nomEcole.trim();
	}

	/**
	 * Setter de l'attribut nomEcole.
	 */
	public void setNomEcole(String newNomEcole) {
		nomEcole = newNomEcole;
	}

	@Override
	public boolean equals(Object object) {
		return idDiplome.toString().equals(((DiplomeAgent) object).getIdDiplome().toString());
	}
}
