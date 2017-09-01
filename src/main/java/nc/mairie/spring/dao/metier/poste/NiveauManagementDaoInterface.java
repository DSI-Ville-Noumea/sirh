package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.NiveauManagement;

import java.util.List;

public interface NiveauManagementDaoInterface {

    public List<NiveauManagement> listerNiveauManagement();

    public NiveauManagement getNiveauManagement(Integer idNiveauManagement);
}
