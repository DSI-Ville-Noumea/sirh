package nc.mairie.enums;

/**
 * This is the enum used by tables MATMUT and MATMUTHIST (schema MAIRIE), for the field CODVAL
 * (#44481)
 * Created on the 2nd of February 2018
 * @author teo
 *
 */
public enum EnumModificationPA {

	CREE("C"),
	ANNULE("A"),
	TRAITE("T"),
	NON_TRAITE("N"),
	VENTILE("V");
	
	private String code;
	
	private EnumModificationPA(String _code) {
		this.code = _code;
	}

	public String getCode() {
		return code;
	}

}