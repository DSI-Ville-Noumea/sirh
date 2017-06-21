package nc.mairie.metier.poste;

import nc.mairie.metier.Const;

/**
 * Objet metier FicheEmploi
 */
public class FicheEmploi implements Cloneable {

	public Integer idFicheEmploi;
	public Integer idDomaineFe;
	public Integer idFamilleEmploi;
	public String refMairie;
	public String nomMetierEmploi;
	public String precisionsDiplomes;
	public String lienHierarchique;
	public String definitionEmploi;
	public Integer idCodeRome;

	/**
	 * Constructeur FicheEmploi.
	 */
	public FicheEmploi() {
		super();
	}

	/**
	 * Getter de l'attribut idFicheEmploi.
	 */
	public Integer getIdFicheEmploi() {
		return idFicheEmploi;
	}

	/**
	 * Setter de l'attribut idFicheEmploi.
	 */
	public void setIdFicheEmploi(Integer newIdFicheEmploi) {
		idFicheEmploi = newIdFicheEmploi;
	}

	public Integer getIdCodeRome() {
		return idCodeRome;
	}

	public void setIdCodeRome(Integer idCodeRome) {
		this.idCodeRome = idCodeRome;
	}

	/**
	 * Getter de l'attribut idDomaineFE.
	 */
	public Integer getIdDomaineFe() {
		return idDomaineFe;
	}

	/**
	 * Setter de l'attribut idDomaineFE.
	 */
	public void setIdDomaineFe(Integer newIdDomaineFE) {
		idDomaineFe = newIdDomaineFE;
	}

	/**
	 * Getter de l'attribut idFamilleEmploi.
	 */
	public Integer getIdFamilleEmploi() {
		return idFamilleEmploi;
	}

	/**
	 * Setter de l'attribut idFamilleEmploi.
	 */
	public void setIdFamilleEmploi(Integer newIdFamilleEmploi) {
		idFamilleEmploi = newIdFamilleEmploi;
	}

	/**
	 * Getter de l'attribut refMairie.
	 */
	public String getRefMairie() {
		return refMairie;
	}

	/**
	 * Setter de l'attribut refMairie.
	 */
	public void setRefMairie(String newRefMairie) {
		refMairie = newRefMairie;
	}

	/**
	 * Getter de l'attribut nomMetierEmploi.
	 */
	public String getNomMetierEmploi() {
		return nomMetierEmploi == null ? Const.CHAINE_VIDE : nomMetierEmploi.trim();
	}

	/**
	 * Setter de l'attribut nomMetierEmploi.
	 */
	public void setNomMetierEmploi(String newNomMetierEmploi) {
		nomMetierEmploi = newNomMetierEmploi;
	}

	/**
	 * Getter de l'attribut precisionsDiplomes.
	 */
	public String getPrecisionsDiplomes() {
		return precisionsDiplomes;
	}

	/**
	 * Setter de l'attribut precisionsDiplomes.
	 */
	public void setPrecisionsDiplomes(String newPrecisionsDiplomes) {
		precisionsDiplomes = newPrecisionsDiplomes;
	}

	/**
	 * Getter de l'attribut lienHierarchique.
	 */
	public String getLienHierarchique() {
		return lienHierarchique;
	}

	/**
	 * Setter de l'attribut lienHierarchique.
	 */
	public void setLienHierarchique(String newLienHierarchique) {
		lienHierarchique = newLienHierarchique;
	}

	/**
	 * Getter de l'attribut definitionEmploi.
	 */
	public String getDefinitionEmploi() {
		return definitionEmploi == null ? Const.CHAINE_VIDE : definitionEmploi.trim();
	}

	/**
	 * Setter de l'attribut definitionEmploi.
	 */
	public void setDefinitionEmploi(String newDefinitionEmploi) {
		definitionEmploi = newDefinitionEmploi;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object object) {
		return idFicheEmploi.toString().equals(((FicheEmploi) object).getIdFicheEmploi().toString());
	}
}
