package nc.mairie.metier.parametrage;

public class RubriqueCharge {

	public Integer norubr;
	public boolean showCodeCharge;
	public boolean showCreancier;
	public boolean showMatriculeCharge;
	public boolean showMontant;
	public boolean showDonneesMutu;

	public RubriqueCharge() {
		super();
	}

	public String toString() {
		return "RubriqueCharge : [noRubr : " + getNorubr() + "]";
	}

	public boolean isShowCodeCharge() {
		return showCodeCharge;
	}

	public void setShowCodeCharge(boolean showCodeCharge) {
		this.showCodeCharge = showCodeCharge;
	}

	public boolean isShowCreancier() {
		return showCreancier;
	}

	public void setShowCreancier(boolean showCreancier) {
		this.showCreancier = showCreancier;
	}

	public boolean isShowMatriculeCharge() {
		return showMatriculeCharge;
	}

	public void setShowMatriculeCharge(boolean showMatriculeCharge) {
		this.showMatriculeCharge = showMatriculeCharge;
	}

	public boolean isShowMontant() {
		return showMontant;
	}

	public void setShowMontant(boolean showMontant) {
		this.showMontant = showMontant;
	}

	public boolean isShowDonneesMutu() {
		return showDonneesMutu;
	}

	public void setShowDonneesMutu(boolean showDonneesMutu) {
		this.showDonneesMutu = showDonneesMutu;
	}

	public Integer getNorubr() {
		return norubr;
	}

	public void setNorubr(Integer norubr) {
		this.norubr = norubr;
	}
}
