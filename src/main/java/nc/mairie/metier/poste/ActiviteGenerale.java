package nc.mairie.metier.poste;

/**
 * Created by gael on 20/07/2017.
 */
public class ActiviteGenerale {

    private Integer idActiviteGenerale;
    private String nomActiviteGenerale;
    private Boolean checked;

    public ActiviteGenerale() {
    }

    public ActiviteGenerale(Integer idActiviteGenerale, String nomActiviteGenerale, Boolean checked) {
        this.idActiviteGenerale = idActiviteGenerale;
        this.nomActiviteGenerale = nomActiviteGenerale;
        this.checked = checked;
    }

    public Integer getIdActiviteGenerale() {
        return idActiviteGenerale;
    }

    public void setIdActiviteGenerale(Integer idActiviteGenerale) {
        this.idActiviteGenerale = idActiviteGenerale;
    }

    public String getNomActiviteGenerale() {
        return nomActiviteGenerale;
    }

    public void setNomActiviteGenerale(String nomActiviteGenerale) {
        this.nomActiviteGenerale = nomActiviteGenerale;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActiviteGenerale that = (ActiviteGenerale) o;

        return idActiviteGenerale != null ? idActiviteGenerale.equals(that.idActiviteGenerale) : that.idActiviteGenerale == null;
    }

    @Override
    public int hashCode() {
        return idActiviteGenerale != null ? idActiviteGenerale.hashCode() : 0;
    }
}
