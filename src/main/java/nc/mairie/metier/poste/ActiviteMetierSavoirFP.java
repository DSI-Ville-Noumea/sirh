package nc.mairie.metier.poste;

/**
 * Created by gael on 21/07/2017.
 */
public class ActiviteMetierSavoirFP {

    private Integer idFichePoste;
    private Integer idActiviteMetier;
    private Integer idSavoirFaire;

    public ActiviteMetierSavoirFP() {
    }

    public ActiviteMetierSavoirFP(Integer idFichePoste, Integer idActiviteMetier, Integer idSavoirFaire) {
        this.idFichePoste = idFichePoste;
        this.idActiviteMetier = idActiviteMetier;
        this.idSavoirFaire = idSavoirFaire;
    }

    public Integer getIdFichePoste() {
        return idFichePoste;
    }

    public void setIdFichePoste(Integer idFichePoste) {
        this.idFichePoste = idFichePoste;
    }

    public Integer getIdActiviteMetier() {
        return idActiviteMetier;
    }

    public void setIdActiviteMetier(Integer idActiviteMetier) {
        this.idActiviteMetier = idActiviteMetier;
    }

    public Integer getIdSavoirFaire() {
        return idSavoirFaire;
    }

    public void setIdSavoirFaire(Integer idSavoirFaire) {
        this.idSavoirFaire = idSavoirFaire;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActiviteMetierSavoirFP that = (ActiviteMetierSavoirFP) o;

        if (!idFichePoste.equals(that.idFichePoste)) return false;
        if (!idActiviteMetier.equals(that.idActiviteMetier)) return false;
        return idSavoirFaire != null ? idSavoirFaire.equals(that.idSavoirFaire) : that.idSavoirFaire == null;
    }

    @Override
    public int hashCode() {
        int result = idFichePoste.hashCode();
        result = 31 * result + idActiviteMetier.hashCode();
        result = 31 * result + (idSavoirFaire != null ? idSavoirFaire.hashCode() : 0);
        return result;
    }
}
