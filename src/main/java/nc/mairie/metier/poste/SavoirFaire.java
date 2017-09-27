package nc.mairie.metier.poste;

/**
 * Created by gael on 19/07/2017.
 */
public class SavoirFaire {

    public Integer idSavoirFaire;
    public Integer idActiviteMetier;
    public String nomSavoirFaire;
    public Boolean checked;

    public SavoirFaire() {
    }


    public Integer getIdSavoirFaire() {
        return idSavoirFaire;
    }

    public void setIdSavoirFaire(Integer idSavoirFaire) {
        this.idSavoirFaire = idSavoirFaire;
    }

    public Integer getIdActiviteMetier() {
        return idActiviteMetier;
    }

    public void setIdActiviteMetier(Integer idActiviteMetier) {
        this.idActiviteMetier = idActiviteMetier;
    }

    public String getNomSavoirFaire() {
        return nomSavoirFaire;
    }

    public void setNomSavoirFaire(String nomSavoirFaire) {
        this.nomSavoirFaire = nomSavoirFaire;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SavoirFaire)) {
            return false;
        }
        SavoirFaire other = (SavoirFaire) obj;
        if (other.idSavoirFaire == null || this.idSavoirFaire == null) {
            return false;
        }
        return idSavoirFaire == other.idSavoirFaire;
    }
}
