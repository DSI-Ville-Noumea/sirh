package nc.mairie.metier.poste;

import java.util.Date;

/**
 * Objet metier Activite
 */
public class ActionFdpJob {

	private Integer idActionFdpJob;
	private Integer idAgent;
	private Integer idFichePoste;
	private Integer idNewServiceAds;
	private String typeAction;
	private String statut;
	private Date dateSubmission;
	private Date dateStatut;

	public ActionFdpJob() {
		super();
	}

	public Integer getIdActionFdpJob() {
		return idActionFdpJob;
	}

	public void setIdActionFdpJob(Integer idActionFdpJob) {
		this.idActionFdpJob = idActionFdpJob;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Integer getIdFichePoste() {
		return idFichePoste;
	}

	public void setIdFichePoste(Integer idFichePoste) {
		this.idFichePoste = idFichePoste;
	}

	public Integer getIdNewServiceAds() {
		return idNewServiceAds;
	}

	public void setIdNewServiceAds(Integer idNewServiceAds) {
		this.idNewServiceAds = idNewServiceAds;
	}

	public String getTypeAction() {
		return typeAction;
	}

	public void setTypeAction(String typeAction) {
		this.typeAction = typeAction;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
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

}
