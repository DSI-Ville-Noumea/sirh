package nc.mairie.metier.poste;

/**
 * Created by gael on 21/07/2017.
 */
public class ActiviteGeneraleFP {

    private Integer idFichePoste;
    private Integer idActiviteGenerale;

    public ActiviteGeneraleFP() {
    }

    public ActiviteGeneraleFP(Integer idFichePoste, Integer idActiviteGenerale) {
        this.idFichePoste = idFichePoste;
        this.idActiviteGenerale = idActiviteGenerale;
    }

    public Integer getIdFichePoste() {
        return idFichePoste;
    }

    public void setIdFichePoste(Integer idFichePoste) {
        this.idFichePoste = idFichePoste;
    }

    public Integer getIdActiviteGenerale() {
        return idActiviteGenerale;
    }

    public void setIdActiviteGenerale(Integer idActiviteGenerale) {
        this.idActiviteGenerale = idActiviteGenerale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActiviteGeneraleFP that = (ActiviteGeneraleFP) o;

        if (idFichePoste != null ? !idFichePoste.equals(that.idFichePoste) : that.idFichePoste != null) return false;
        return idActiviteGenerale != null ? idActiviteGenerale.equals(that.idActiviteGenerale) : that.idActiviteGenerale == null;
    }

    @Override
    public int hashCode() {
        int result = idFichePoste != null ? idFichePoste.hashCode() : 0;
        result = 31 * result + (idActiviteGenerale != null ? idActiviteGenerale.hashCode() : 0);
        return result;
    }
}
