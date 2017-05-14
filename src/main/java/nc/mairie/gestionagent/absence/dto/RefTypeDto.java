package nc.mairie.gestionagent.absence.dto;

import java.io.Serializable;

public class RefTypeDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3854094040343020224L;
	
	private Integer idRefType;
	private String code;
	private String libelle;
	
	public RefTypeDto() {
	}
	
	public Integer getIdRefType() {
		return idRefType;
	}
	public void setIdRefType(Integer idRefType) {
		this.idRefType = idRefType;
	}
	public String getLibelle() {
		return libelle;
	}
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public boolean equals(Object o) {
		
		if(null == this.idRefType
				|| null == ((RefTypeDto)o).getIdRefType()) {
			return false;
		}
		
		return this.idRefType.equals(((RefTypeDto)o).getIdRefType());
	}
}
