package nc.noumea.mairie.ads.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement
public class EntiteDto {

	private Integer idEntite;
	private String sigle;
	private String label;
	private String labelCourt;
	private ReferenceDto typeEntite;
	private String codeServi;
	private List<EntiteDto> enfants;
	private EntiteDto entiteParent;
	private EntiteDto entiteRemplacee;

	private Integer idStatut;
	private Integer idAgentCreation;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateCreation;
	private Integer idAgentModification;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateModification;
	private String refDeliberationActif;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateDeliberationActif;
	private String refDeliberationInactif;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateDeliberationInactif;
	private String nfa;

	public EntiteDto() {
		enfants = new ArrayList<>();
	}

	public Integer getIdEntite() {
		return idEntite;
	}

	public void setIdEntite(Integer idEntite) {
		this.idEntite = idEntite;
	}

	public String getSigle() {
		return sigle;
	}

	public void setSigle(String sigle) {
		this.sigle = sigle;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabelCourt() {
		return labelCourt;
	}

	public void setLabelCourt(String labelCourt) {
		this.labelCourt = labelCourt;
	}

	public ReferenceDto getTypeEntite() {
		return typeEntite;
	}

	public void setTypeEntite(ReferenceDto typeEntite) {
		this.typeEntite = typeEntite;
	}

	public String getCodeServi() {
		return codeServi;
	}

	public void setCodeServi(String codeServi) {
		this.codeServi = codeServi;
	}

	public List<EntiteDto> getEnfants() {
		return enfants;
	}

	public void setEnfants(List<EntiteDto> enfants) {
		this.enfants = enfants;
	}

	public EntiteDto getEntiteParent() {
		return entiteParent;
	}

	public void setEntiteParent(EntiteDto entiteParent) {
		this.entiteParent = entiteParent;
	}

	public EntiteDto getEntiteRemplacee() {
		return entiteRemplacee;
	}

	public void setEntiteRemplacee(EntiteDto entiteRemplacee) {
		this.entiteRemplacee = entiteRemplacee;
	}

	public Integer getIdStatut() {
		return idStatut;
	}

	public void setIdStatut(Integer idStatut) {
		this.idStatut = idStatut;
	}

	public Integer getIdAgentCreation() {
		return idAgentCreation;
	}

	public void setIdAgentCreation(Integer idAgentCreation) {
		this.idAgentCreation = idAgentCreation;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public Integer getIdAgentModification() {
		return idAgentModification;
	}

	public void setIdAgentModification(Integer idAgentModification) {
		this.idAgentModification = idAgentModification;
	}

	public Date getDateModification() {
		return dateModification;
	}

	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
	}

	public String getRefDeliberationActif() {
		return refDeliberationActif;
	}

	public void setRefDeliberationActif(String refDeliberationActif) {
		this.refDeliberationActif = refDeliberationActif;
	}

	public Date getDateDeliberationActif() {
		return dateDeliberationActif;
	}

	public void setDateDeliberationActif(Date dateDeliberationActif) {
		this.dateDeliberationActif = dateDeliberationActif;
	}

	public String getRefDeliberationInactif() {
		return refDeliberationInactif;
	}

	public void setRefDeliberationInactif(String refDeliberationInactif) {
		this.refDeliberationInactif = refDeliberationInactif;
	}

	public Date getDateDeliberationInactif() {
		return dateDeliberationInactif;
	}

	public void setDateDeliberationInactif(Date dateDeliberationInactif) {
		this.dateDeliberationInactif = dateDeliberationInactif;
	}

	public String getNfa() {
		return nfa;
	}

	public void setNfa(String nfa) {
		this.nfa = nfa;
	}

}
