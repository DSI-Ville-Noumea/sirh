package nc.mairie.gestionagent.dto;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class VentilPrimeDto extends VentilDto {

    private int idRefPrime;
    private int quantite;

    public VentilPrimeDto() {
    }

    public int getIdVentilPrime() {
        return idVentil;
    }

    public void setIdVentilPrime(int idVentilPrime) {
        this.idVentil = idVentilPrime;
    }

    public Date getDateDebutMois() {
        return date;
    }

    public void setDateDebutMois(Date dateDebutMois) {
        this.date = dateDebutMois;
    }

    public int getIdRefPrime() {
        return idRefPrime;
    }

    public void setIdRefPrime(int idRefPrime) {
        this.idRefPrime = idRefPrime;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
