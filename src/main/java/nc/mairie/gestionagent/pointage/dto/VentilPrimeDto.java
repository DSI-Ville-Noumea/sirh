package nc.mairie.gestionagent.pointage.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class VentilPrimeDto extends VentilDto {

	private int idRefPrime;
	private String quantite;

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

	public String getQuantite() {
		return quantite;
	}

	public void setQuantite(String quantite) {
		this.quantite = quantite;
	}

}
