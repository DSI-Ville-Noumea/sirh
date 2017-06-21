package nc.mairie.metier.agent;

import java.util.Date;

import nc.mairie.metier.Const;

/**
 * Objet metier Contrat
 */
public class Contrat {

	public Integer idContrat;
	public Integer idTypeContrat;
	public Integer idMotif;
	public Integer idAgent;
	public Integer idDocument;
	public String numContrat;
	public boolean avenant;
	public Integer idContratRef;
	public Date datdeb;
	public Date dateFinPeriodeEss;
	public Date dateFin;
	public String justification;

	/**
	 * Constructeur Contrat.
	 */
	public Contrat() {
		super();
	}

	public Integer getIdContrat() {
		return idContrat;
	}

	public void setIdContrat(Integer idContrat) {
		this.idContrat = idContrat;
	}

	public Integer getIdTypeContrat() {
		return idTypeContrat;
	}

	public void setIdTypeContrat(Integer idTypeContrat) {
		this.idTypeContrat = idTypeContrat;
	}

	public Integer getIdMotif() {
		return idMotif;
	}

	public void setIdMotif(Integer idMotif) {
		this.idMotif = idMotif;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Integer getIdDocument() {
		return idDocument;
	}

	public void setIdDocument(Integer idDocument) {
		this.idDocument = idDocument;
	}

	public String getNumContrat() {
		return numContrat == null ? Const.CHAINE_VIDE : numContrat.trim();
	}

	public void setNumContrat(String numContrat) {
		this.numContrat = numContrat;
	}

	public boolean isAvenant() {
		return avenant;
	}

	public void setAvenant(boolean avenant) {
		this.avenant = avenant;
	}

	public Integer getIdContratRef() {
		return idContratRef;
	}

	public void setIdContratRef(Integer idContratRef) {
		this.idContratRef = idContratRef;
	}

	public Date getDateFin() {
		return dateFin;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}

	public String getJustification() {
		return justification == null ? Const.CHAINE_VIDE : justification.trim();
	}

	public void setJustification(String justification) {
		this.justification = justification;
	}

	public Date getDatdeb() {
		return datdeb;
	}

	public void setDatdeb(Date datdeb) {
		this.datdeb = datdeb;
	}

	public Date getDateFinPeriodeEss() {
		return dateFinPeriodeEss;
	}

	public void setDateFinPeriodeEss(Date dateFinPeriodeEss) {
		this.dateFinPeriodeEss = dateFinPeriodeEss;
	}

	@Override
	public boolean equals(Object object) {
		return idContrat.toString().equals(((Contrat) object).getIdContrat().toString());
	}
}
