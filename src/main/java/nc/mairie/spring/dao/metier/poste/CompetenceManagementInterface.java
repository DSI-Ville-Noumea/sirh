package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.CompetenceManagement;

import java.util.List;

public interface CompetenceManagementInterface {

    List<CompetenceManagement> listerToutesCompetencesManagement(Integer idNiveauManagement);

}
