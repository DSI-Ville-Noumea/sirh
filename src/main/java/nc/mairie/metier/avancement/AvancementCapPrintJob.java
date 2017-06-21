package nc.mairie.metier.avancement;

import java.util.Date;

public class AvancementCapPrintJob {

	private Integer idAvancementCapPrintJob;
	private Integer idAgent;
	private String login;
	private Integer idCap;
	private String codeCap;
	private Integer idCadreEmploi;
	private String libCadreEmploi;
	private boolean isEaes;
	private Date dateSubmission;
	private Date dateStatut;
	private String statut;
	private String jobId;
	private boolean avisEAE;

	public AvancementCapPrintJob() {
		super();
	}

	public String toString() {
		return "AvancementCapPrintJob : [id : " + getIdAvancementCapPrintJob() + ", cap : " + getCodeCap()
				+ ", cadreEmploi : " + getLibCadreEmploi() + "]";
	}

	public Integer getIdAvancementCapPrintJob() {
		return idAvancementCapPrintJob;
	}

	public void setIdAvancementCapPrintJob(Integer idAvancementCapPrintJob) {
		this.idAvancementCapPrintJob = idAvancementCapPrintJob;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Integer getIdCap() {
		return idCap;
	}

	public void setIdCap(Integer idCap) {
		this.idCap = idCap;
	}

	public String getCodeCap() {
		return codeCap;
	}

	public void setCodeCap(String codeCap) {
		this.codeCap = codeCap;
	}

	public Integer getIdCadreEmploi() {
		return idCadreEmploi;
	}

	public void setIdCadreEmploi(Integer idCadreEmploi) {
		this.idCadreEmploi = idCadreEmploi;
	}

	public String getLibCadreEmploi() {
		return libCadreEmploi;
	}

	public void setLibCadreEmploi(String libCadreEmploi) {
		this.libCadreEmploi = libCadreEmploi;
	}

	public boolean isEaes() {
		return isEaes;
	}

	public void setEaes(boolean isEaes) {
		this.isEaes = isEaes;
	}

	public Date getDateSubmission() {
		return dateSubmission;
	}

	public void setDateSubmission(Date dateSubmission) {
		this.dateSubmission = dateSubmission;
	}

	public Date getDateStatut() {
		return dateStatut;
	}

	public void setDateStatut(Date dateStatut) {
		this.dateStatut = dateStatut;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public boolean isAvisEAE() {
		return avisEAE;
	}

	public void setAvisEAE(boolean avisEAE) {
		this.avisEAE = avisEAE;
	}

	@Override
	public boolean equals(Object object) {
		return idAvancementCapPrintJob.toString().equals(
				((AvancementCapPrintJob) object).getIdAvancementCapPrintJob().toString());
	}
}
