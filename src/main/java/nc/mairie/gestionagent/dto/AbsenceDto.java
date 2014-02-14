package nc.mairie.gestionagent.dto;

public class AbsenceDto extends PointageDto {

	private Integer idTypeAbsence;

	public AbsenceDto() {
	}

	public Integer getIdTypeAbsence() {
		return idTypeAbsence;
	}

	public void setIdTypeAbsence(Integer idTypeAbsence) {
		this.idTypeAbsence = idTypeAbsence;
	}
}
