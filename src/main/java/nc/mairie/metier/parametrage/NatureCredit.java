package nc.mairie.metier.parametrage;

public class NatureCredit {

	public Integer idNatureCredit;
	public String libNatureCredit;
	public Integer ordreAff;

	public NatureCredit() {
		super();
	}

	public String toString() {
		return "Nature Credit : [idNatureCredit : " + getIdNatureCredit() + ", libNatureCredit : "
				+ getLibNatureCredit() + "]";
	}

	public Integer getIdNatureCredit() {
		return idNatureCredit;
	}

	public void setIdNatureCredit(Integer idNatureCredit) {
		this.idNatureCredit = idNatureCredit;
	}

	public String getLibNatureCredit() {
		return libNatureCredit;
	}

	public void setLibNatureCredit(String libNatureCredit) {
		this.libNatureCredit = libNatureCredit;
	}

	public Integer getOrdreAffichage() {
		return ordreAff;
	}

	public void setOrdreAffichage(Integer ordreAffichage) {
		this.ordreAff = ordreAffichage;
	}

	@Override
	public boolean equals(Object object) {
		return idNatureCredit.toString().equals(((NatureCredit) object).getIdNatureCredit().toString());
	}
}
