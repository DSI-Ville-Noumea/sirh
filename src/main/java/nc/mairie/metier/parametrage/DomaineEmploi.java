package nc.mairie.metier.parametrage;

/**
 * Objet metier DomaineEmploi
 */
public class DomaineEmploi {
	public Integer idDomaineFe;
	public String codeDomaineFe;
	public String libDomaineFe;

	/**
	 * Surcharge de la methode equals
	 * 
	 * @param object
	 * @return true si egaux. Faux sinon.
	 * @see String#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		return idDomaineFe.toString().equals(((DomaineEmploi) object).getIdDomaineFe().toString());
	}

	/**
	 * Constructeur DomaineEmploi.
	 */
	public DomaineEmploi() {
		super();
	}

	/**
	 * Getter de l'attribut idDomaineEmploi.
	 */
	public Integer getIdDomaineFe() {
		return idDomaineFe;
	}

	/**
	 * Setter de l'attribut idDomaineEmploi.
	 */
	public void setIdDomaineFe(Integer newIdDomaineEmploi) {
		idDomaineFe = newIdDomaineEmploi;
	}

	/**
	 * Getter de l'attribut codeDomaineEmploi.
	 */
	public String getCodeDomaineFe() {
		return codeDomaineFe;
	}

	/**
	 * Setter de l'attribut codeDomaineEmploi.
	 */
	public void setCodeDomaineFe(String newCodeDomaineEmploi) {
		codeDomaineFe = newCodeDomaineEmploi;
	}

	/**
	 * Getter de l'attribut libDomaineEmploi.
	 */
	public String getLibDomaineFe() {
		return libDomaineFe;
	}

	/**
	 * Setter de l'attribut libDomaineEmploi.
	 */
	public void setLibDomaineFe(String newLibDomaineEmploi) {
		libDomaineFe = newLibDomaineEmploi;
	}
}
