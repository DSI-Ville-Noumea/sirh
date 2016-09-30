package nc.mairie.gestionagent.eae.dto;

public enum EaeTypeDeveloppementEnum {

	CONNAISSANCE("Connaissance"),
	COMPETENCE("Compétence"),
	CONCOURS("Concours"),
	PERSONNEL("Personnel"),
	COMPORTEMENT("Comportement"),
	FORMATEUR("Formateur");
	
	private String type;
	
	private EaeTypeDeveloppementEnum(String _type) {
		this.type = _type;
	}
	
	@Override
	public String toString() {
		return type;
	}
}
