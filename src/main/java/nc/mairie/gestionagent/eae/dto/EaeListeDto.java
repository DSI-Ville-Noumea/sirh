package nc.mairie.gestionagent.eae.dto;

import java.util.ArrayList;
import java.util.List;

public class EaeListeDto {

	private String					courant;
	private List<ValeurListeDto>	liste;

	public EaeListeDto() {
		liste = new ArrayList<ValeurListeDto>();
	}

	public String getCourant() {
		return courant;
	}

	public void setCourant(String courant) {
		this.courant = courant;
	}

	public List<ValeurListeDto> getListe() {
		return liste;
	}

	public void setListe(List<ValeurListeDto> liste) {
		this.liste = liste;
	}
}
