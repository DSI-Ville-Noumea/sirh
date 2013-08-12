package nc.mairie.gestionagent.process.pointage;

public enum TypeSaisieEnum {

	CASE_A_COCHER(0),
	NB_HEURES(1),
	NB_INDEMNITES(2),
	PERIODE_HEURES(3);
	
	private TypeSaisieEnum(int _saisie) {
	}
	
	@Override
	public String toString() {
		return this.name();
	}
	
}
