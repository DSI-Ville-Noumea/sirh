package nc.mairie.metier.poste;

/**
 * Created by gael on 21/07/2017.
 */
public class ConditionExerciceFP {

    private Integer idFichePoste;
    private Integer idConditionExercice;
    private Integer ordre;

    public ConditionExerciceFP() {
    }

    public ConditionExerciceFP(Integer idFichePoste, Integer idConditionExercice) {
        this.idFichePoste = idFichePoste;
        this.idConditionExercice = idConditionExercice;
    }

    public ConditionExerciceFP(Integer idFichePoste, Integer idConditionExercice, Integer ordre) {
        this.idFichePoste = idFichePoste;
        this.idConditionExercice = idConditionExercice;
        this.ordre = ordre;
    }

    public Integer getIdFichePoste() {
        return idFichePoste;
    }

    public void setIdFichePoste(Integer idFichePoste) {
        this.idFichePoste = idFichePoste;
    }

    public Integer getIdConditionExercice() {
        return idConditionExercice;
    }

    public void setIdConditionExercice(Integer idConditionExercice) {
        this.idConditionExercice = idConditionExercice;
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

        ConditionExerciceFP that = (ConditionExerciceFP) o;

        if (idFichePoste != null ? !idFichePoste.equals(that.idFichePoste) : that.idFichePoste != null) return false;
        return idConditionExercice != null ? idConditionExercice.equals(that.idConditionExercice) : that.idConditionExercice == null;
    }

    @Override
    public int hashCode() {
        int result = idFichePoste != null ? idFichePoste.hashCode() : 0;
        result = 31 * result + (idConditionExercice != null ? idConditionExercice.hashCode() : 0);
        return result;
    }
}
