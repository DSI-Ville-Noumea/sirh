package nc.mairie.gestionagent.dto;

import java.util.Date;

import nc.mairie.spring.domain.metier.EAE.EAE;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class EaeIdentificationDto implements IJSONSerialize, IJSONDeserialize<EaeIdentificationDto> {
	public int idEae;
	public Date dateEntretien;
	public EaeIdentificationSituationDto situation;

	public EaeIdentificationDto() {
	}

	public EaeIdentificationDto(EAE eae) {
		this();
		this.idEae = eae.getIdEAE();
		this.dateEntretien = eae.getDateEntretien();
		try {
			this.situation = new EaeIdentificationSituationDto(eae);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static JSONSerializer getSerializerForEaeIdentificationDto() {

		JSONSerializer serializer = new JSONSerializer().exclude("*.class").include("idEae").include("dateEntretien").include("situation.*")
				.transform(new MSDateTransformer(), Date.class).exclude("*");

		return serializer;
	}

	public static JSONDeserializer<EaeIdentificationDto> getDeserializerForEaeIdentificationDto() {

		JSONDeserializer<EaeIdentificationDto> deserializer = new JSONDeserializer<EaeIdentificationDto>().use(Date.class, new MSDateTransformer());

		return deserializer;
	}

	@Override
	public EaeIdentificationDto deserializeFromJSON(String json) {
		return getDeserializerForEaeIdentificationDto().deserializeInto(json, this);
	}

	@Override
	public String serializeInJSON() {
		return getSerializerForEaeIdentificationDto().serialize(this);
	}

	public int getIdEae() {
		return idEae;
	}

	public void setIdEae(int idEae) {
		this.idEae = idEae;
	}

	public Date getDateEntretien() {
		return dateEntretien;
	}

	public void setDateEntretien(Date dateEntretien) {
		this.dateEntretien = dateEntretien;
	}

	public EaeIdentificationSituationDto getSituation() {
		return situation;
	}

	public void setSituation(EaeIdentificationSituationDto situation) {
		this.situation = situation;
	}
}
