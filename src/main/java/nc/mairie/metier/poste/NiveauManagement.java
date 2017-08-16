package nc.mairie.metier.poste;

public class NiveauManagement {

    private Integer idNiveauManagement;
    private String libNiveauManagement;

    public NiveauManagement() {
    }

    public NiveauManagement(Integer idNiveauManagement, String libNiveauManagement) {
        this.idNiveauManagement = idNiveauManagement;
        this.libNiveauManagement = libNiveauManagement;
    }

    public Integer getIdNiveauManagement() {
        return idNiveauManagement;
    }

    public void setIdNiveauManagement(Integer idNiveauManagement) {
        this.idNiveauManagement = idNiveauManagement;
    }

    public String getLibNiveauManagement() {
        return libNiveauManagement;
    }

    public void setLibNiveauManagement(String libNiveauManagement) {
        this.libNiveauManagement = libNiveauManagement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NiveauManagement that = (NiveauManagement) o;

        return idNiveauManagement != null ? idNiveauManagement.equals(that.idNiveauManagement) : that.idNiveauManagement == null;
    }

    @Override
    public int hashCode() {
        return idNiveauManagement != null ? idNiveauManagement.hashCode() : 0;
    }
}
