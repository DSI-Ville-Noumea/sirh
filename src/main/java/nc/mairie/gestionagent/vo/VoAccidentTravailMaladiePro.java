package nc.mairie.gestionagent.vo;

import java.util.Date;

import nc.mairie.metier.hsct.AccidentTravail;
import nc.mairie.metier.hsct.MaladiePro;

public class VoAccidentTravailMaladiePro implements Comparable<VoAccidentTravailMaladiePro> {
	
	private Integer idMaladiePro;
	private Integer idAT;
	private Integer idAgent;
	private Boolean rechute;
	private Date dateDeclaration;
	private Date dateFin;
	private Integer nbJoursItt;
	private Date dateAtInitial;
	private Integer avisCommission;
	private Integer idTypeMp;
	private Integer idTypeAt;
	private Integer idSiege;
	private Date dateTransmissionCafat;
	private Date dateDecisionCafat;
	private Integer tauxPrisEnChargeCafat;
	private Date dateTransmissionAptitude;
	private Integer idAtReference;
	
	private String type;
	
	public VoAccidentTravailMaladiePro() {
	}
	
	public VoAccidentTravailMaladiePro(AccidentTravail at) {
		this.idAT = at.getIdAt();
		this.idAgent = at.getIdAgent();
		this.rechute = at.getRechute();
		this.dateDeclaration = at.getDateAt();
		this.dateFin = at.getDateFin();
		this.nbJoursItt = at.getNbJoursItt();
		this.dateAtInitial = at.getDateAtInitial();
		this.avisCommission = at.getAvisCommission();
		this.idTypeAt = at.getIdTypeAt();
		this.idSiege = at.getIdSiege();
		this.idAtReference = at.getIdAtReference();
	}
	
	public VoAccidentTravailMaladiePro(MaladiePro mp) {
		this.idMaladiePro = mp.getIdMaladiePro();
		this.idAgent = mp.getIdAgent();
		this.rechute = mp.getRechute();
		this.dateDeclaration = mp.getDateDeclaration();
		this.dateFin = mp.getDateFin();
		this.nbJoursItt = mp.getNbJoursItt();
		this.dateAtInitial = mp.getDateAtInitial();
		this.avisCommission = mp.getAvisCommission();
		this.idTypeMp = mp.getIdTypeMp();
		this.dateTransmissionCafat = mp.getDateTransmissionCafat();
		this.dateDecisionCafat = mp.getDateDecisionCafat();
		this.tauxPrisEnChargeCafat = mp.getTauxPrisEnChargeCafat();
		this.dateTransmissionAptitude = mp.getDateTransmissionAptitude();
	}
	
	public Integer getId() {
		return null != idAT ? idAT : idMaladiePro;
	}

	public String getType() {
		return null != type ? type : (null != idAT ? "AT" : "MP");
	}
	
	public boolean isTypeAT() {
		if(null != type
				&& type.equals("AT")) {
			return true;
		}
		if(null != type
				&& type.equals("MP")) {
			return false;
		}
		if(null != idAT) {
			return true;
		}
		return false;
	}
	
	public boolean isTypeMP() {
		if(null != type
				&& type.equals("MP")) {
			return true;
		}
		if(null != type
				&& type.equals("AT")) {
			return false;
		}
		if(null != idMaladiePro) {
			return true;
		}
		return false;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getIdAT() {
		return idAT;
	}
	public void setIdAT(Integer idAT) {
		this.idAT = idAT;
	}
	public Integer getIdMaladiePro() {
		return idMaladiePro;
	}
	public void setIdMaladiePro(Integer idMaladiePro) {
		this.idMaladiePro = idMaladiePro;
	}
	public Integer getIdAgent() {
		return idAgent;
	}
	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}
	public Boolean getRechute() {
		return rechute;
	}
	public void setRechute(Boolean rechute) {
		this.rechute = rechute;
	}
	public Date getDateDeclaration() {
		return dateDeclaration;
	}
	public void setDateDeclaration(Date dateDeclaration) {
		this.dateDeclaration = dateDeclaration;
	}
	public Date getDateFin() {
		return dateFin;
	}
	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}
	public Integer getNbJoursItt() {
		return nbJoursItt;
	}
	public void setNbJoursItt(Integer nbJoursItt) {
		this.nbJoursItt = nbJoursItt;
	}
	public Date getDateAtInitial() {
		return dateAtInitial;
	}
	public void setDateAtInitial(Date dateAtInitial) {
		this.dateAtInitial = dateAtInitial;
	}
	public Integer getAvisCommission() {
		return avisCommission;
	}
	public void setAvisCommission(Integer avisCommission) {
		this.avisCommission = avisCommission;
	}
	public Integer getIdTypeMp() {
		return idTypeMp;
	}
	public void setIdTypeMp(Integer idTypeMp) {
		this.idTypeMp = idTypeMp;
	}
	public Integer getIdTypeAt() {
		return idTypeAt;
	}
	public void setIdTypeAt(Integer idTypeAt) {
		this.idTypeAt = idTypeAt;
	}
	public Integer getIdSiege() {
		return idSiege;
	}
	public void setIdSiege(Integer idSiege) {
		this.idSiege = idSiege;
	}
	public Date getDateTransmissionCafat() {
		return dateTransmissionCafat;
	}
	public void setDateTransmissionCafat(Date dateTransmissionCafat) {
		this.dateTransmissionCafat = dateTransmissionCafat;
	}
	public Date getDateDecisionCafat() {
		return dateDecisionCafat;
	}
	public void setDateDecisionCafat(Date dateDecisionCafat) {
		this.dateDecisionCafat = dateDecisionCafat;
	}
	public Integer getTauxPrisEnChargeCafat() {
		return tauxPrisEnChargeCafat;
	}
	public void setTauxPrisEnChargeCafat(Integer tauxPrisEnChargeCafat) {
		this.tauxPrisEnChargeCafat = tauxPrisEnChargeCafat;
	}
	public Date getDateTransmissionAptitude() {
		return dateTransmissionAptitude;
	}
	public void setDateTransmissionAptitude(Date dateTransmissionAptitude) {
		this.dateTransmissionAptitude = dateTransmissionAptitude;
	}
	public Integer getIdAtReference() {
		return idAtReference;
	}
	public void setIdAtReference(Integer idAtReference) {
		this.idAtReference = idAtReference;
	}

	@Override
	public int compareTo(VoAccidentTravailMaladiePro arg0) {
		return this.dateDeclaration.compareTo(arg0.getDateDeclaration());
	}
}
