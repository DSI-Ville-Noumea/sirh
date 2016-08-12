package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.PathAlfresco;

public interface PathAlfrescoDaoInterface {

	ArrayList<PathAlfresco> listerPathAlfresco() throws Exception;

	PathAlfresco chercherPathAlfresco(Integer idPathAlfresco) throws Exception;
}
