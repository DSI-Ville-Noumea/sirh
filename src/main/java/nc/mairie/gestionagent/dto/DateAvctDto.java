package nc.mairie.gestionagent.dto;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class DateAvctDto {
	
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateAvct;

	public Date getDateAvct() {
		return dateAvct;
	}

	public void setDateAvct(Date dateAvct) {
		this.dateAvct = dateAvct;
	}

}
