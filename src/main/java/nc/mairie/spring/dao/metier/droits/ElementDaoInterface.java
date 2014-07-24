package nc.mairie.spring.dao.metier.droits;

import java.util.ArrayList;

import nc.mairie.metier.droits.Element;

public interface ElementDaoInterface {

	public ArrayList<Element> listerElement() throws Exception;

	public Integer creerElement(String libElement) throws Exception;

}
