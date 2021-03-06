package nc.mairie.metier.poste;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gael on 18/07/2017.
 */
public class ActiviteMetier {

    private Integer idActiviteMetier;
    private String nomActiviteMetier;
    private List<SavoirFaire> listSavoirFaire = new ArrayList<>();
    private Boolean checked;

    public ActiviteMetier() {
    }

    public ActiviteMetier(Integer idActiviteMetier, String nomActiviteMetier) {
        this.idActiviteMetier = idActiviteMetier;
        this.nomActiviteMetier = nomActiviteMetier;
    }

    public ActiviteMetier(Integer idActiviteMetier, String nomActiviteMetier, List<SavoirFaire> listSavoirFaire) {
        this.idActiviteMetier = idActiviteMetier;
        this.nomActiviteMetier = nomActiviteMetier;
        this.listSavoirFaire = listSavoirFaire;
    }

    public Integer getIdActiviteMetier() {
        return idActiviteMetier;
    }

    public void setIdActiviteMetier(Integer idActiviteMetier) {
        this.idActiviteMetier = idActiviteMetier;
    }

    public String getNomActiviteMetier() {
        return nomActiviteMetier;
    }

    public void setNomActiviteMetier(String nomActiviteMetier) {
        this.nomActiviteMetier = nomActiviteMetier;
    }

    public Boolean isChecked() {
        //Si checked au niveau
        if (checked) {
            return true;
        }
        boolean isChecked = false;
        int i = 0;
        while (!isChecked && i < listSavoirFaire.size()) {
            isChecked = listSavoirFaire.get(i).getChecked();
            i++;
        }
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public List<SavoirFaire> getListSavoirFaire() {
        return listSavoirFaire;
    }

    public void setListSavoirFaire(List<SavoirFaire> listSavoirFaire) {
        this.listSavoirFaire = listSavoirFaire;
    }

    public void addToListSavoirFaire(SavoirFaire savoirFaire) {
        listSavoirFaire.add(savoirFaire);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActiviteMetier that = (ActiviteMetier) o;

        return idActiviteMetier != null ? idActiviteMetier.equals(that.idActiviteMetier) : that.idActiviteMetier == null;
    }
}
