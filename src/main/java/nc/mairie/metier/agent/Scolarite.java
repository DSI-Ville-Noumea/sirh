package nc.mairie.metier.agent;

import java.util.Date;

/**
 * Objet metier Scolarite
 */
public class Scolarite  {
	public Integer idScolarite;
	public Integer idEnfant;
	public Date dateDebutScolarite;
	public Date dateFinScolarite;

	/**
	 * Constructeur Scolarite.
	 */
	public Scolarite() {
		super();
	}

	

	public Integer getIdScolarite() {
		return idScolarite;
	}



	public void setIdScolarite(Integer idScolarite) {
		this.idScolarite = idScolarite;
	}



	public Integer getIdEnfant() {
		return idEnfant;
	}



	public void setIdEnfant(Integer idEnfant) {
		this.idEnfant = idEnfant;
	}



	public void setDateDebutScolarite(Date dateDebutScolarite) {
		this.dateDebutScolarite = dateDebutScolarite;
	}



	public void setDateFinScolarite(Date dateFinScolarite) {
		this.dateFinScolarite = dateFinScolarite;
	}


	

	public Date getDateDebutScolarite() {
		return dateDebutScolarite;
	}



	public Date getDateFinScolarite() {
		return dateFinScolarite;
	}



	/**
	 * Surcharge de la methode equals
	 * @param object
	 * @return boolean true si egaux, false sinon
	 */
	public boolean equals(Object object) {
		if (object != null) {
			Scolarite scol = (Scolarite) object;
			if (getDateDebutScolarite() == null && scol.getDateDebutScolarite() != null)
				return false;
			if (getDateDebutScolarite() != null && scol.getDateDebutScolarite() == null)
				return false;
			if (getDateDebutScolarite() != null && scol.getDateDebutScolarite() != null && !getDateDebutScolarite().equals(scol.getDateDebutScolarite()))
				return false;

			if (getDateFinScolarite() == null && scol.getDateFinScolarite() != null)
				return false;
			if (getDateFinScolarite() != null && scol.getDateFinScolarite() == null)
				return false;
			if (getDateFinScolarite() != null && scol.getDateFinScolarite() != null && !getDateFinScolarite().equals(scol.getDateFinScolarite()))
				return false;

			if (getIdScolarite() == null && scol.getIdScolarite() != null)
				return false;
			if (getIdScolarite() != null && scol.getIdScolarite() == null)
				return false;
			if (getIdScolarite() != null && scol.getIdScolarite() != null && !getIdScolarite().toString().equals(scol.getIdScolarite().toString()))
				return false;

			if (getIdEnfant() == null && scol.getIdEnfant() != null)
				return false;
			if (getIdEnfant() != null && scol.getIdEnfant() == null)
				return false;
			if (getIdEnfant() != null && scol.getIdEnfant() != null && !getIdEnfant().toString().equals(scol.getIdEnfant().toString()))
				return false;
		}
		return true;
	}
}
