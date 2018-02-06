package nc.mairie.metier.carriere;

import java.util.Date;

public class MATMUTHIST {

	public Integer pkey;
	public Integer nomatr;
	public Integer perrep;
	public String codval;
	public Date timelog;
	public String iduser;
	
	public Integer getPkey() {
		return pkey;
	}
	
	public void setPkey(Integer pkey) {
		this.pkey = pkey;
	}
	
	public Integer getNomatr() {
		return nomatr;
	}
	
	public void setNomatr(Integer nomtar) {
		this.nomatr = nomtar;
	}
	
	public Integer getPerrep() {
		return perrep;
	}
	
	public void setPerrep(Integer perrep) {
		this.perrep = perrep;
	}
	
	public String getCodval() {
		return codval;
	}
	
	public void setCodval(String codval) {
		this.codval = codval;
	}
	
	public Date getTimelog() {
		return timelog;
	}
	
	public void setTimelog(Date timelog) {
		this.timelog = timelog;
	}
	
	public String getIduser() {
		return iduser;
	}
	
	public void setIduser(String iduser) {
		this.iduser = iduser;
	}
}
