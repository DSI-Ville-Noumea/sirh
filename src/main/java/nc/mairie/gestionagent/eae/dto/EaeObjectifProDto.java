package nc.mairie.gestionagent.eae.dto;

public class EaeObjectifProDto {

	private Integer	idObjectifPro;
	private String	indicateur;
	private String	objectif;

	public EaeObjectifProDto() {

	}

	public String getIndicateur() {
		return indicateur;
	}

	public void setIndicateur(String indicateur) {
		this.indicateur = indicateur;
	}

	public String getObjectif() {
		return objectif;
	}

	public void setObjectif(String objectif) {
		this.objectif = objectif;
	}

	public Integer getIdObjectifPro() {
		return idObjectifPro;
	}

	public void setIdObjectifPro(Integer idObjectifPro) {
		this.idObjectifPro = idObjectifPro;
	}

}
