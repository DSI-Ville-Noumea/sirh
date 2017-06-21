package nc.mairie.metier.suiviMedical;

public class MotifVisiteMed {

	private Integer idMotifVm;
	private String libMotifVm;

	public MotifVisiteMed() {
		super();
	}

	public MotifVisiteMed(Integer idMotifVM, String libMotifVM) {
		super();
		this.idMotifVm = idMotifVM;
		this.libMotifVm = libMotifVM;
	}

	public String toString() {
		return "MOTIF VM : [Id : " + getIdMotifVm() + ", Lib : " + getLibMotifVm() + "]";
	}

	public Integer getIdMotifVm() {
		return idMotifVm;
	}

	public void setIdMotifVm(Integer idMotifVm) {
		this.idMotifVm = idMotifVm;
	}

	public String getLibMotifVm() {
		return libMotifVm;
	}

	public void setLibMotifVm(String libMotifVm) {
		this.libMotifVm = libMotifVm;
	}

	@Override
	public boolean equals(Object object) {
		return idMotifVm.toString().equals(((MotifVisiteMed) object).getIdMotifVm().toString());
	}

}
