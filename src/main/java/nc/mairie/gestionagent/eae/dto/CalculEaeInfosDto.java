package nc.mairie.gestionagent.eae.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class CalculEaeInfosDto {

	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateDebut;

	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateFin;
	
	private FichePosteDto fichePostePrincipale;
	
	private FichePosteDto fichePosteSecondaire;
	
	private FichePosteDto fichePosteResponsable;
	
	private List<DiplomeDto> listDiplome;
	
	private List<ParcoursProDto> listParcoursPro;
	
	private List<FormationDto> listFormation;
	// mairie.SPADMN
	private PositionAdmAgentDto positionAdmAgentEnCours;
	private PositionAdmAgentDto positionAdmAgentAncienne;
	// mairie.Spcarr
	CarriereDto carriereFonctionnaireAncienne;
	CarriereDto carriereActive;
	CarriereDto carriereAncienneDansGrade;
	

	public PositionAdmAgentDto getPositionAdmAgentEnCours() {
		return positionAdmAgentEnCours;
	}

	public void setPositionAdmAgentEnCours(
			PositionAdmAgentDto positionAdmAgentEnCours) {
		this.positionAdmAgentEnCours = positionAdmAgentEnCours;
	}

	public FichePosteDto getFichePostePrincipale() {
		return fichePostePrincipale;
	}

	public void setFichePostePrincipale(FichePosteDto fichePostePrincipale) {
		this.fichePostePrincipale = fichePostePrincipale;
	}

	public FichePosteDto getFichePosteSecondaire() {
		return fichePosteSecondaire;
	}

	public void setFichePosteSecondaire(FichePosteDto fichePosteSecondaire) {
		this.fichePosteSecondaire = fichePosteSecondaire;
	}

	public FichePosteDto getFichePosteResponsable() {
		return fichePosteResponsable;
	}

	public void setFichePosteResponsable(FichePosteDto fichePosteResponsable) {
		this.fichePosteResponsable = fichePosteResponsable;
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

	public List<DiplomeDto> getListDiplome() {
		return listDiplome;
	}

	public void setListDiplome(List<DiplomeDto> listDiplome) {
		this.listDiplome = listDiplome;
	}

	public List<ParcoursProDto> getListParcoursPro() {
		return listParcoursPro;
	}

	public void setListParcoursPro(List<ParcoursProDto> listParcoursPro) {
		this.listParcoursPro = listParcoursPro;
	}

	public List<FormationDto> getListFormation() {
		return listFormation;
	}

	public void setListFormation(List<FormationDto> listFormation) {
		this.listFormation = listFormation;
	}

	public PositionAdmAgentDto getPositionAdmAgentAncienne() {
		return positionAdmAgentAncienne;
	}

	public void setPositionAdmAgentAncienne(
			PositionAdmAgentDto positionAdmAgentAncienne) {
		this.positionAdmAgentAncienne = positionAdmAgentAncienne;
	}

	public CarriereDto getCarriereFonctionnaireAncienne() {
		return carriereFonctionnaireAncienne;
	}

	public void setCarriereFonctionnaireAncienne(
			CarriereDto carriereFonctionnaireAncienne) {
		this.carriereFonctionnaireAncienne = carriereFonctionnaireAncienne;
	}

	public CarriereDto getCarriereActive() {
		return carriereActive;
	}

	public void setCarriereActive(CarriereDto carriereActive) {
		this.carriereActive = carriereActive;
	}

	public CarriereDto getCarriereAncienneDansGrade() {
		return carriereAncienneDansGrade;
	}

	public void setCarriereAncienneDansGrade(CarriereDto carriereAncienneDansGrade) {
		this.carriereAncienneDansGrade = carriereAncienneDansGrade;
	}
	
	
}
