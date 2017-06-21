package nc.mairie.metier.agent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Objet metier AutreAdministrationAgent
 */
public class AutreAdministrationAgent {

	public Integer idAutreAdmin;
	public Integer idAgent;
	public Date dateEntree;
	public Date dateSortie;
	public Integer fonctionnaire;

	/**
	 * Constructeur AutreAdministrationAgent.
	 */
	public AutreAdministrationAgent() {
		super();
	}

	public AutreAdministrationAgent(Integer idAutreAdmin, Integer idAgent, Date dateEntree) {
		this.idAutreAdmin = idAutreAdmin;
		this.idAgent = idAgent;
		this.dateEntree = dateEntree;
	}

	/**
	 * Getter de l'attribut idAutreAdmin.
	 */
	public Integer getIdAutreAdmin() {
		return idAutreAdmin;
	}

	/**
	 * Setter de l'attribut idAutreAdmin.
	 */
	public void setIdAutreAdmin(Integer newIdAutreAdmin) {
		idAutreAdmin = newIdAutreAdmin;
	}

	/**
	 * Getter de l'attribut idAgent.
	 */
	public Integer getIdAgent() {
		return idAgent;
	}

	/**
	 * Setter de l'attribut idAgent.
	 */
	public void setIdAgent(Integer newIdAgent) {
		idAgent = newIdAgent;
	}

	/**
	 * Getter de l'attribut dateEntree.
	 */
	public Date getDateEntree() {
		return dateEntree;
	}

	/**
	 * Setter de l'attribut dateEntree.
	 */
	public void setDateEntree(Date newDateEntree) {
		dateEntree = newDateEntree;
	}

	/**
	 * Getter de l'attribut dateSortie.
	 */
	public Date getDateSortie() {
		return dateSortie;
	}

	/**
	 * Setter de l'attribut dateSortie.
	 */
	public void setDateSortie(Date newDateSortie) {
		dateSortie = newDateSortie;
	}

	public Integer getFonctionnaire() {
		return fonctionnaire == null ? 0 : fonctionnaire;
	}

	public void setFonctionnaire(Integer fonctionnaire) {
		this.fonctionnaire = fonctionnaire;
	}

	@Override
	public boolean equals(Object object) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		AutreAdministrationAgent obj = (AutreAdministrationAgent) object;
		return idAutreAdmin.toString().equals(obj.getIdAutreAdmin().toString())
				&& idAgent.toString().equals(obj.getIdAgent().toString())
				&& sdf.format(dateEntree).equals(sdf.format(obj.getDateEntree()));
	}
}
