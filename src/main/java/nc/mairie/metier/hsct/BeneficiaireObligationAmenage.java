package nc.mairie.metier.hsct;

import java.util.Date;

public class BeneficiaireObligationAmenage {

	public Integer idBoe;
	public Integer idAgent;
	public String type;
	public Date dateDebut;
	public Date dateAttribution;
	public Date dateFin;
	public Integer idNaturePosteAmenage;
	public String natureHandicap;
	public Integer taux;
	public String origineIpp;
	
	// utile a l affichage mais pas en BDD
	public Integer nbDoc;
	
	public BeneficiaireObligationAmenage() {
	}
	
	public Integer getIdBoe() {
		return idBoe;
	}
	public void setIdBoe(Integer idBoe) {
		this.idBoe = idBoe;
	}
	public Integer getIdAgent() {
		return idAgent;
	}
	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getDateDebut() {
		return dateDebut;
	}
	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}
	public Date getDateAttribution() {
		return dateAttribution;
	}
	public void setDateAttribution(Date dateAttribution) {
		this.dateAttribution = dateAttribution;
	}
	public Date getDateFin() {
		return dateFin;
	}
	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}
	public Integer getIdNaturePosteAmenage() {
		return idNaturePosteAmenage;
	}
	public void setIdNaturePosteAmenage(Integer idNaturePosteAmenage) {
		this.idNaturePosteAmenage = idNaturePosteAmenage;
	}
	public String getNatureHandicap() {
		return natureHandicap;
	}
	public void setNatureHandicap(String natureHandicap) {
		this.natureHandicap = natureHandicap;
	}
	public Integer getTaux() {
		return taux;
	}
	public void setTaux(Integer taux) {
		this.taux = taux;
	}
	public String getOrigineIpp() {
		return origineIpp;
	}
	public void setOrigineIpp(String origineIpp) {
		this.origineIpp = origineIpp;
	}

	public Integer getNbDoc() {
		return nbDoc;
	}

	public void setNbDoc(Integer nbDoc) {
		this.nbDoc = nbDoc;
	}
	
}
