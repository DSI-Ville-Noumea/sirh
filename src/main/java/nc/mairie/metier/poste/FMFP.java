package nc.mairie.metier.poste;

/**
 * Created by gael on 29/06/2017.
 */
public class FMFP {

    private Integer idFicheMetier;
    private Integer idFichePoste;
    private Boolean fmPrimaire;

    public FMFP() {}

    public FMFP(Integer idFicheMetier, Integer idFichePoste, Boolean fmPrimaire) {
        this.idFicheMetier = idFicheMetier;
        this.idFichePoste = idFichePoste;
        this.fmPrimaire = fmPrimaire;
    }

    public Integer getIdFicheMetier() {
        return idFicheMetier;
    }

    public void setIdFicheMetier(Integer idFicheMetier) {
        this.idFicheMetier = idFicheMetier;
    }

    public Integer getIdFichePoste() {
        return idFichePoste;
    }

    public void setIdFichePoste(Integer idFichePoste) {
        this.idFichePoste = idFichePoste;
    }

    public Boolean getFmPrimaire() {
        return fmPrimaire;
    }

    public void setFmPrimaire(Boolean fmPrimaire) {
        this.fmPrimaire = fmPrimaire;
    }

    @Override
    public boolean equals(Object o) {
        return idFicheMetier == ((FMFP) o).idFicheMetier
                && idFichePoste == ((FMFP) o).idFichePoste;
    }
}
