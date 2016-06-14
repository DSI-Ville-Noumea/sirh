package nc.mairie.gestionagent.pointage.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DpmIndemniteAnneeDto implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6104807700822860344L;

	private Integer idDpmIndemAnnee;
	private Integer annee;
	private Date dateDebut;
	private Date dateFin;
	private List<DpmIndemniteChoixAgentDto> listDpmIndemniteChoixAgentDto;
	
	public DpmIndemniteAnneeDto() {
		listDpmIndemniteChoixAgentDto = new ArrayList<DpmIndemniteChoixAgentDto>();
	}
	
	public DpmIndemniteAnneeDto(Integer annee) {
		this();
		this.annee = annee;
	}
	
	public void addDpmIndemniteChoixAgentDto(DpmIndemniteChoixAgentDto choixAgentDto) {
		if(null != getListDpmIndemniteChoixAgentDto())
			getListDpmIndemniteChoixAgentDto().add(choixAgentDto);
	}

	public Integer getIdDpmIndemAnnee() {
		return idDpmIndemAnnee;
	}

	public void setIdDpmIndemAnnee(Integer idDpmIndemAnnee) {
		this.idDpmIndemAnnee = idDpmIndemAnnee;
	}

	public Integer getAnnee() {
		return annee;
	}

	public void setAnnee(Integer annee) {
		this.annee = annee;
	}

	public Date getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	public Date getDateFin() {
		return dateFin;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}

	public List<DpmIndemniteChoixAgentDto> getListDpmIndemniteChoixAgentDto() {
		return listDpmIndemniteChoixAgentDto;
	}

	public void setListDpmIndemniteChoixAgentDto(List<DpmIndemniteChoixAgentDto> listDpmIndemniteChoixAgentDto) {
		this.listDpmIndemniteChoixAgentDto = listDpmIndemniteChoixAgentDto;
	}

	@Override
	public String toString() {
		return "DpmIndemniteAnneeDto [idDpmIndemAnnee=" + idDpmIndemAnnee + ", annee=" + annee + ", dateDebut=" + dateDebut + ", dateFin=" + dateFin
				+ ", listDpmIndemniteChoixAgentDto=" + listDpmIndemniteChoixAgentDto + "]";
	}
	
}
