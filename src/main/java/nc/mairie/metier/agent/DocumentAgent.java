package nc.mairie.metier.agent;

/**
 * Objet metier LienDocumentAgent
 */
public class DocumentAgent {

	public Integer idAgent;
	public Integer idDocument;

	/**
	 * Constructeur LienDocumentAgent.
	 */
	public DocumentAgent() {
		super();
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
	 * Getter de l'attribut idDocument.
	 */
	public Integer getIdDocument() {
		return idDocument;
	}

	/**
	 * Setter de l'attribut idDocument.
	 */
	public void setIdDocument(Integer newIdDocument) {
		idDocument = newIdDocument;
	}

	@Override
	public boolean equals(Object object) {
		return idDocument.toString().equals(((DocumentAgent) object).getIdDocument().toString())
				&& idAgent.toString().equals(((DocumentAgent) object).getIdAgent().toString());
	}
}
