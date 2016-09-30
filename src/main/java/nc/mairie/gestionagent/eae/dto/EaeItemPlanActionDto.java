package nc.mairie.gestionagent.eae.dto;

public class EaeItemPlanActionDto {

	private Integer idItemPlanAction;
	private String libelle;

	public EaeItemPlanActionDto() {
		
	}
	
	public EaeItemPlanActionDto(String pLibelle) {
		this.libelle = pLibelle;
	}
	
	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public Integer getIdItemPlanAction() {
		return idItemPlanAction;
	}

	public void setIdItemPlanAction(Integer idItemPlanAction) {
		this.idItemPlanAction = idItemPlanAction;
	}
	
}
