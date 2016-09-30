package nc.mairie.gestionagent.eae.dto;

import java.util.ArrayList;
import java.util.List;

public class EaePlanActionDto {

	private int							idEae;
	private List<String>				moyensAutres;
	private List<String>				moyensFinanciers;
	private List<String>				moyensMateriels;
	private List<String>				objectifsIndividuels;
	private List<EaeObjectifProDto>		objectifsProfessionnels;
	private List<EaeItemPlanActionDto>	listeObjectifsIndividuels;

	public EaePlanActionDto() {
		objectifsProfessionnels = new ArrayList<EaeObjectifProDto>();
		objectifsIndividuels = new ArrayList<String>();
		moyensMateriels = new ArrayList<String>();
		moyensFinanciers = new ArrayList<String>();
		moyensAutres = new ArrayList<String>();
	}

	public int getIdEae() {
		return idEae;
	}

	public void setIdEae(int idEae) {
		this.idEae = idEae;
	}

	public List<String> getMoyensAutres() {
		return moyensAutres;
	}

	public void setMoyensAutres(List<String> moyensAutres) {
		this.moyensAutres = moyensAutres;
	}

	public List<String> getMoyensFinanciers() {
		return moyensFinanciers;
	}

	public void setMoyensFinanciers(List<String> moyensFinanciers) {
		this.moyensFinanciers = moyensFinanciers;
	}

	public List<String> getMoyensMateriels() {
		return moyensMateriels;
	}

	public void setMoyensMateriels(List<String> moyensMateriels) {
		this.moyensMateriels = moyensMateriels;
	}

	public List<String> getObjectifsIndividuels() {
		return objectifsIndividuels;
	}

	public void setObjectifsIndividuels(List<String> objectifsIndividuels) {
		this.objectifsIndividuels = objectifsIndividuels;
	}

	public List<EaeObjectifProDto> getObjectifsProfessionnels() {
		return objectifsProfessionnels;
	}

	public void setObjectifsProfessionnels(List<EaeObjectifProDto> objectifsProfessionnels) {
		this.objectifsProfessionnels = objectifsProfessionnels;
	}

	public List<EaeItemPlanActionDto> getListeObjectifsIndividuels() {
		return listeObjectifsIndividuels;
	}

	public void setListeObjectifsIndividuels(List<EaeItemPlanActionDto> listeObjectifsIndividuels) {
		this.listeObjectifsIndividuels = listeObjectifsIndividuels;
	}
}
