package nc.mairie.gestionagent.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RefEtatDto {

	private Integer idRefEtat;
	private String libelle;

	public RefEtatDto() {
	}

	public Integer getIdRefEtat() {
		return idRefEtat;
	}

	public void setIdRefEtat(Integer idRefEtat) {
		this.idRefEtat = idRefEtat;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
}
