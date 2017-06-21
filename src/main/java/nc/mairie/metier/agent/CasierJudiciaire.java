package nc.mairie.metier.agent;

import java.util.Date;

import nc.mairie.metier.Const;

/**
 * Objet metier CasierJudiciaire
 */
public class CasierJudiciaire {

	public Integer idCasierJud;
	public Integer idAgent;
	public Integer idDocument;
	public String numExtrait;
	public Date dateExtrait;
	public boolean privationDroitsCiv;
	public String commExtrait;

	/**
	 * Constructeur CasierJudiciaire.
	 */
	public CasierJudiciaire() {
		super();
	}

	/**
	 * Getter de l'attribut idCasierJud.
	 */
	public Integer getIdCasierJud() {
		return idCasierJud;
	}

	/**
	 * Setter de l'attribut idCasierJud.
	 */
	public void setIdCasierJud(Integer newIdCasierJud) {
		idCasierJud = newIdCasierJud;
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
	 * Getter de l'attribut numExtrait.
	 */
	public String getNumExtrait() {
		return numExtrait == null ? Const.CHAINE_VIDE : numExtrait.trim();
	}

	/**
	 * Setter de l'attribut numExtrait.
	 */
	public void setNumExtrait(String newNumExtrait) {
		numExtrait = newNumExtrait;
	}

	/**
	 * Getter de l'attribut dateExtrait.
	 */
	public Date getDateExtrait() {
		return dateExtrait;
	}

	/**
	 * Setter de l'attribut dateExtrait.
	 */
	public void setDateExtrait(Date newDateExtrait) {
		dateExtrait = newDateExtrait;
	}

	/**
	 * Getter de l'attribut privationDroitsCiv.
	 */
	public boolean isPrivationDroitsCiv() {
		return privationDroitsCiv;
	}

	/**
	 * Setter de l'attribut privationDroitsCiv.
	 */
	public void setPrivationDroitsCiv(boolean newPrivationDroitsCiv) {
		privationDroitsCiv = newPrivationDroitsCiv;
	}

	/**
	 * Getter de l'attribut commExtrait.
	 */
	public String getCommExtrait() {
		return commExtrait == null ? Const.CHAINE_VIDE : commExtrait.trim();
	}

	/**
	 * Setter de l'attribut commExtrait.
	 */
	public void setCommExtrait(String newCommExtrait) {
		commExtrait = newCommExtrait;
	}

	@Override
	public boolean equals(Object object) {
		return idCasierJud.toString().equals(((CasierJudiciaire) object).getIdCasierJud().toString());
	}
}
