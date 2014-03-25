package nc.mairie.gestionagent.absence.dto;

import nc.mairie.gestionagent.pointage.dto.PointageDto;

public class AbsenceDto extends PointageDto {

	private Integer idRefTypeAbsence;

	public AbsenceDto() {
	}

	public Integer getIdRefTypeAbsence() {
		return idRefTypeAbsence;
	}

	public void setIdRefTypeAbsence(Integer idRefTypeAbsence) {
		this.idRefTypeAbsence = idRefTypeAbsence;
	}
}
