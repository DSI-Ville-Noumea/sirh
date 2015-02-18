package nc.mairie.gestionagent.absence.dto;

import java.util.Date;
import java.util.List;

public class RestitutionMassiveDto {

	private Integer idRestitutionMassive;
	private Date dateRestitution;
	private Date dateModification;
	private String status;
	private boolean isMatin;
	private boolean isApresMidi;
	private boolean isJournee;
	private String motif;
	
	private List<RestitutionMassiveHistoDto> listHistoAgents;

	public Date getDateRestitution() {
		return dateRestitution;
	}

	public void setDateRestitution(Date dateRestitution) {
		this.dateRestitution = dateRestitution;
	}

	public boolean isMatin() {
		return isMatin;
	}

	public void setMatin(boolean isMatin) {
		this.isMatin = isMatin;
	}

	public boolean isApresMidi() {
		return isApresMidi;
	}

	public void setApresMidi(boolean isApresMidi) {
		this.isApresMidi = isApresMidi;
	}

	public boolean isJournee() {
		return isJournee;
	}

	public void setJournee(boolean isJournee) {
		this.isJournee = isJournee;
	}

	public String getMotif() {
		return motif;
	}

	public void setMotif(String motif) {
		this.motif = motif;
	}

	public Integer getIdRestitutionMassive() {
		return idRestitutionMassive;
	}

	public void setIdRestitutionMassive(Integer idRestitutionMassive) {
		this.idRestitutionMassive = idRestitutionMassive;
	}

	public Date getDateModification() {
		return dateModification;
	}

	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<RestitutionMassiveHistoDto> getListHistoAgents() {
		return listHistoAgents;
	}

	public void setListHistoAgents(List<RestitutionMassiveHistoDto> listHistoAgents) {
		this.listHistoAgents = listHistoAgents;
	}

}
