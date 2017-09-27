package nc.mairie.metier.poste;

public class CompetenceManagement {

    private Integer idCompetenceManagement;
    private Integer idNiveauManagement;
    private String libCompetenceManagement;
    private Integer ordre;

    public CompetenceManagement() {
    }

    public CompetenceManagement(Integer idCompetenceManagement, Integer idNiveauManagement, String libCompetenceManagement, Integer ordre) {
        this.idCompetenceManagement = idCompetenceManagement;
        this.idNiveauManagement = idNiveauManagement;
        this.libCompetenceManagement = libCompetenceManagement;
        this.ordre = ordre;
    }

    public Integer getIdCompetenceManagement() {
        return idCompetenceManagement;
    }

    public void setIdCompetenceManagement(Integer idCompetenceManagement) {
        this.idCompetenceManagement = idCompetenceManagement;
    }

    public Integer getIdNiveauManagement() {
        return idNiveauManagement;
    }

    public void setIdNiveauManagement(Integer idNiveauManagement) {
        this.idNiveauManagement = idNiveauManagement;
    }

    public String getLibCompetenceManagement() {
        return libCompetenceManagement;
    }

    public void setLibCompetenceManagement(String libCompetenceManagement) {
        this.libCompetenceManagement = libCompetenceManagement;
    }

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompetenceManagement that = (CompetenceManagement) o;

        return idCompetenceManagement != null ? idCompetenceManagement.equals(that.idCompetenceManagement) : that.idCompetenceManagement == null;
    }

    @Override
    public int hashCode() {
        return idCompetenceManagement != null ? idCompetenceManagement.hashCode() : 0;
    }
}
