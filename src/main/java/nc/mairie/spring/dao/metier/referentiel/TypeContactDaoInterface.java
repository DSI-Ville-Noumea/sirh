package nc.mairie.spring.dao.metier.referentiel;

import java.util.ArrayList;

import nc.mairie.metier.commun.TypeContact;

public interface TypeContactDaoInterface {

	public ArrayList<TypeContact> listerTypeContact() throws Exception;

	public TypeContact chercherTypeContact(Integer idTypeContact) throws Exception;

}
