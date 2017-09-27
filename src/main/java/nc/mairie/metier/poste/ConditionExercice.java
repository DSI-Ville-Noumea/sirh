package nc.mairie.metier.poste;

/**
 * Created by gael on 20/07/2017.
 */
public class ConditionExercice {

    private Integer idConditionExercice;
    private String nomConditionExercice;
    private Boolean checked;

    public ConditionExercice() {
    }

    public ConditionExercice(Integer idConditionExercice, String nomConditionExercice, Boolean checked) {
        this.idConditionExercice = idConditionExercice;
        this.nomConditionExercice = nomConditionExercice;
        this.checked = checked;
    }

    public Integer getIdConditionExercice() {
        return idConditionExercice;
    }

    public void setIdConditionExercice(Integer idConditionExercice) {
        this.idConditionExercice = idConditionExercice;
    }

    public String getNomConditionExercice() {
        return nomConditionExercice;
    }

    public void setNomConditionExercice(String nomConditionExercice) {
        this.nomConditionExercice = nomConditionExercice;
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

        ConditionExercice that = (ConditionExercice) o;

        return idConditionExercice != null ? idConditionExercice.equals(that.idConditionExercice) : that.idConditionExercice == null;
    }

    @Override
    public int hashCode() {
        return idConditionExercice != null ? idConditionExercice.hashCode() : 0;
    }
}
