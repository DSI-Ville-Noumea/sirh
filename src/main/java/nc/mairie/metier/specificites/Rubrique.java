package nc.mairie.metier.specificites;

import nc.mairie.metier.Const;

/**
 * Objet metier Rubrique
 */
public class Rubrique {

	public Integer norubr;
	public String lirubr;
	// typeImposition
	public String tyimpo;
	// soumisChargeCafat
	public String cdrcaf;
	// numRubriqueRappel
	public Integer rubrap;
	// typePrime
	public String typrim;
	// typeRubrique
	public String tyrubr;
	// categoriePossible
	public String catpos;
	// dateInactivation
	public Integer datina;

	/**
	 * Constructeur Rubrique.
	 */
	public Rubrique() {
		super();
	}

	public Integer getNorubr() {
		return norubr;
	}

	public void setNorubr(Integer norubr) {
		this.norubr = norubr;
	}

	public String getLirubr() {
		return lirubr == null ? Const.CHAINE_VIDE : lirubr.trim();
	}

	public void setLirubr(String lirubr) {
		this.lirubr = lirubr;
	}

	public String getTyimpo() {
		return tyimpo;
	}

	public void setTyimpo(String tyimpo) {
		this.tyimpo = tyimpo;
	}

	public String getCdrcaf() {
		return cdrcaf;
	}

	public void setCdrcaf(String cdrcaf) {
		this.cdrcaf = cdrcaf;
	}

	public Integer getRubrap() {
		return rubrap;
	}

	public void setRubrap(Integer rubrap) {
		this.rubrap = rubrap;
	}

	public String getTyprim() {
		return typrim;
	}

	public void setTyprim(String typrim) {
		this.typrim = typrim;
	}

	public String getTyrubr() {
		return tyrubr;
	}

	public void setTyrubr(String tyrubr) {
		this.tyrubr = tyrubr;
	}

	public String getCatpos() {
		return catpos;
	}

	public void setCatpos(String catpos) {
		this.catpos = catpos;
	}

	public Integer getDatina() {
		return datina;
	}

	public void setDatina(Integer datina) {
		this.datina = datina;
	}
}
