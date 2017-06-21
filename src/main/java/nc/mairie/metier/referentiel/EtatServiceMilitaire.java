package nc.mairie.metier.referentiel;

/**
 * Objet metier EtatServiceMilitaire
 */
public class EtatServiceMilitaire {

	public Integer idEtatService;
	public String codeEtatService;
	public String libEtatService;

	/**
	 * Constructeur EtatServiceMilitaire.
	 */
	public EtatServiceMilitaire() {
		super();
	}

	/**
	 * Getter de l'attribut idEtatService.
	 */
	public Integer getIdEtatService() {
		return idEtatService;
	}

	/**
	 * Setter de l'attribut idEtatService.
	 */
	public void setIdEtatService(Integer newIdEtatService) {
		idEtatService = newIdEtatService;
	}

	/**
	 * Getter de l'attribut codeEtatService.
	 */
	public String getCodeEtatService() {
		return codeEtatService;
	}

	/**
	 * Setter de l'attribut codeEtatService.
	 */
	public void setCodeEtatService(String newCodeEtatService) {
		codeEtatService = newCodeEtatService;
	}

	/**
	 * Getter de l'attribut libEtatService.
	 */
	public String getLibEtatService() {
		return libEtatService;
	}

	/**
	 * Setter de l'attribut libEtatService.
	 */
	public void setLibEtatService(String newLibEtatService) {
		libEtatService = newLibEtatService;
	}

	@Override
	public boolean equals(Object object) {
		return idEtatService.toString().equals(((EtatServiceMilitaire) object).getIdEtatService().toString());
	}
}
