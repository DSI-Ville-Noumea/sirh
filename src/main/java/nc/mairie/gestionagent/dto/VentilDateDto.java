package nc.mairie.gestionagent.dto;

import java.util.Date;

public class VentilDateDto {

	private Date dateVentil;
	private boolean isPaie;
	private String typeChaine;

	public VentilDateDto() {
	}

	@Override
	public String toString() {
		return "VentilDateDto: [date : " + getDateVentil() + ",isPaie : "
				+ isPaie() + ", typeChaine : " + getTypeChaine() + "]";
	}

	public Date getDateVentil() {
		return dateVentil;
	}

	public void setDateVentil(Date dateVentil) {
		this.dateVentil = dateVentil;
	}

	public boolean isPaie() {
		return isPaie;
	}

	public void setPaie(boolean isPaie) {
		this.isPaie = isPaie;
	}

	public String getTypeChaine() {
		return typeChaine;
	}

	public void setTypeChaine(String typeChaine) {
		this.typeChaine = typeChaine;
	}

}
