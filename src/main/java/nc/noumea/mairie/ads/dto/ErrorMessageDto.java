package nc.noumea.mairie.ads.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ErrorMessageDto {

	private Integer idEntite;
	private String sigle;
	private String message;

	public String getSigle() {
		return sigle;
	}

	public void setSigle(String sigle) {
		this.sigle = sigle;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getIdEntite() {
		return idEntite;
	}

	public void setIdEntite(Integer idEntite) {
		this.idEntite = idEntite;
	}

}
