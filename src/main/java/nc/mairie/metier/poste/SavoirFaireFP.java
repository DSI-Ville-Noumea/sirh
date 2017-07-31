package nc.mairie.metier.poste;

/**
 * Created by gael on 21/07/2017.
 */
public class SavoirFaireFP {

    private Integer idFichePoste;
    private Integer idSavoirFaire;
    private Integer ordre;

    public SavoirFaireFP() {
    }

    public SavoirFaireFP(Integer idFichePoste, Integer idSavoirFaire) {
        this.idFichePoste = idFichePoste;
        this.idSavoirFaire = idSavoirFaire;
    }

    public SavoirFaireFP(Integer idFichePoste, Integer idSavoirFaire, Integer ordre) {
        this.idFichePoste = idFichePoste;
        this.idSavoirFaire = idSavoirFaire;
        this.ordre = ordre;
    }

    public Integer getIdFichePoste() {
        return idFichePoste;
    }

    public void setIdFichePoste(Integer idFichePoste) {
        this.idFichePoste = idFichePoste;
    }

    public Integer getIdSavoirFaire() {
        return idSavoirFaire;
    }

    public void setIdSavoirFaire(Integer idSavoirFaire) {
        this.idSavoirFaire = idSavoirFaire;
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

        SavoirFaireFP that = (SavoirFaireFP) o;

        if (idFichePoste != null ? !idFichePoste.equals(that.idFichePoste) : that.idFichePoste != null) return false;
        return idSavoirFaire != null ? idSavoirFaire.equals(that.idSavoirFaire) : that.idSavoirFaire == null;
    }

    @Override
    public int hashCode() {
        int result = idFichePoste != null ? idFichePoste.hashCode() : 0;
        result = 31 * result + (idSavoirFaire != null ? idSavoirFaire.hashCode() : 0);
        return result;
    }
}
