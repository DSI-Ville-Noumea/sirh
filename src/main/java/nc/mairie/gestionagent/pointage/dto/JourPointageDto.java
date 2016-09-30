package nc.mairie.gestionagent.pointage.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.mairie.gestionagent.absence.dto.AbsenceDto;
import nc.noumea.mairie.ads.dto.JsonDateDeserializer;
import nc.noumea.mairie.ads.dto.JsonDateSerializer;

public class JourPointageDto {

	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date date;
	private List<PrimeDto> primes;
	private List<HeureSupDto> heuresSup;
	private List<AbsenceDto> absences;

	public JourPointageDto() {
		primes = new ArrayList<>();
		heuresSup = new ArrayList<>();
		absences = new ArrayList<>();
	}

	public JourPointageDto(JourPointageDto jourPointageTemplate) {
		this();

		// copy all primes every day
		for (PrimeDto prime : jourPointageTemplate.getPrimes())
			primes.add(new PrimeDto(prime));
	}

	public List<PrimeDto> getPrimes() {
		return primes;
	}

	public void setPrimes(List<PrimeDto> primes) {
		this.primes = primes;
	}

	public List<HeureSupDto> getHeuresSup() {
		return heuresSup;
	}

	public void setHeuresSup(List<HeureSupDto> heuresSup) {
		this.heuresSup = heuresSup;
	}

	public List<AbsenceDto> getAbsences() {
		return absences;
	}

	public void setAbsences(List<AbsenceDto> absences) {
		this.absences = absences;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
