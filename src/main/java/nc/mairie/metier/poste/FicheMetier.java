package nc.mairie.metier.poste;

/**
 * Created by gael on 29/06/2017.
 */
public class FicheMetier implements Cloneable {

    private Integer idFicheMetier;
    private Integer idDomaineFM;
    private Integer idFamilleMetier;
    private String refMairie;
    private String nomMetier;
    private String nomMetierLong;
    private String definitionMetier;
    private String cadreStatutaire;

    public Integer getIdFicheMetier() {
        return idFicheMetier;
    }

    public void setIdFicheMetier(Integer idFicheMetier) {
        this.idFicheMetier = idFicheMetier;
    }

    public Integer getIdDomaineFM() {
        return idDomaineFM;
    }

    public void setIdDomaineFM(Integer idDomaineFM) {
        this.idDomaineFM = idDomaineFM;
    }

    public Integer getIdFamilleMetier() {
        return idFamilleMetier;
    }

    public void setIdFamilleMetier(Integer idFamilleMetier) {
        this.idFamilleMetier = idFamilleMetier;
    }

    public String getRefMairie() {
        return refMairie;
    }

    public void setRefMairie(String refMairie) {
        this.refMairie = refMairie;
    }

    public String getNomMetier() {
        return nomMetier;
    }

    public void setNomMetier(String nomMetier) {
        this.nomMetier = nomMetier;
    }

    public String getNomMetierLong() {
		return nomMetierLong;
	}

	public void setNomMetierLong(String nomMetierLong) {
		this.nomMetierLong = nomMetierLong;
	}

	public String getDefinitionMetier() {
        return definitionMetier;
    }

    public void setDefinitionMetier(String definitionMetier) {
        this.definitionMetier = definitionMetier;
    }

    public String getCadreStatutaire() {
        return cadreStatutaire;
    }

    public void setCadreStatutaire(String cadreStatutaire) {
        this.cadreStatutaire = cadreStatutaire;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        return idFicheMetier.equals(o);
    }

    @Override
    public int hashCode() {
        return idFicheMetier.hashCode();
    }
}
