package nc.mairie.gestionagent.absence.dto;

public class RefTypeSaisiCongeAnnuelDto {

	private Integer idRefTypeSaisiCongeAnnuel;
	private String codeBaseHoraireAbsence;
	private String description;
	private boolean calendarDateDebut;
	private boolean chkDateDebut;
	private boolean calendarDateFin;
	private boolean chkDateFin;
	private boolean calendarDateReprise;
	private Integer quotaMultiple;
	private boolean decompteSamedi;
	private boolean pieceJointe;
	private boolean motif;
	private String infosComplementaires;

	// #45163 : This field seems not used...
	private boolean consecutif;

	public RefTypeSaisiCongeAnnuelDto() {
	}

	public Integer getIdRefTypeSaisiCongeAnnuel() {
		return idRefTypeSaisiCongeAnnuel;
	}

	public void setIdRefTypeSaisiCongeAnnuel(Integer idRefTypeSaisiCongeAnnuel) {
		this.idRefTypeSaisiCongeAnnuel = idRefTypeSaisiCongeAnnuel;
	}

	public String getCodeBaseHoraireAbsence() {
		return codeBaseHoraireAbsence;
	}

	public void setCodeBaseHoraireAbsence(String codeBaseHoraireAbsence) {
		this.codeBaseHoraireAbsence = codeBaseHoraireAbsence;
	}

	public boolean isCalendarDateDebut() {
		return calendarDateDebut;
	}

	public void setCalendarDateDebut(boolean calendarDateDebut) {
		this.calendarDateDebut = calendarDateDebut;
	}

	public boolean isChkDateDebut() {
		return chkDateDebut;
	}

	public void setChkDateDebut(boolean chkDateDebut) {
		this.chkDateDebut = chkDateDebut;
	}

	public boolean isCalendarDateFin() {
		return calendarDateFin;
	}

	public void setCalendarDateFin(boolean calendarDateFin) {
		this.calendarDateFin = calendarDateFin;
	}

	public boolean isChkDateFin() {
		return chkDateFin;
	}

	public void setChkDateFin(boolean chkDateFin) {
		this.chkDateFin = chkDateFin;
	}

	public boolean isCalendarDateReprise() {
		return calendarDateReprise;
	}

	public void setCalendarDateReprise(boolean calendarDateReprise) {
		this.calendarDateReprise = calendarDateReprise;
	}

	public Integer getQuotaMultiple() {
		return quotaMultiple;
	}

	public void setQuotaMultiple(Integer quotaMultiple) {
		this.quotaMultiple = quotaMultiple;
	}

	public boolean isDecompteSamedi() {
		return decompteSamedi;
	}

	public void setDecompteSamedi(boolean decompteSamedi) {
		this.decompteSamedi = decompteSamedi;
	}

	public boolean isConsecutif() {
		return consecutif;
	}

	public void setConsecutif(boolean consecutif) {
		this.consecutif = consecutif;
	}

	@Override
	public boolean equals(Object obj) {
		return idRefTypeSaisiCongeAnnuel.toString().equals(
				((RefTypeSaisiCongeAnnuelDto) obj).getIdRefTypeSaisiCongeAnnuel().toString());
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isPieceJointe() {
		return pieceJointe;
	}

	public void setPieceJointe(boolean pieceJointe) {
		this.pieceJointe = pieceJointe;
	}

	public String getInfosComplementaires() {
		return infosComplementaires;
	}

	public void setInfosComplementaires(String infosComplementaires) {
		this.infosComplementaires = infosComplementaires;
	}

	public boolean isMotif() {
		return motif;
	}

	public void setMotif(boolean motif) {
		this.motif = motif;
	}

}
