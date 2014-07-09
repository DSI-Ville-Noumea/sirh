package nc.mairie.spring.dao.metier.parametrage;

import java.util.ArrayList;

import nc.mairie.metier.parametrage.TitrePermis;

public interface TitrePermisDaoInterface {

	public ArrayList<TitrePermis> listerTitrePermis() throws Exception;

	public TitrePermis chercherTitrePermis(Integer idTitrePermis) throws Exception;

	public void creerTitrePermis(String libelleTitre) throws Exception;

	public void modifierTitrePermis(Integer idTitre, String libelleTitre) throws Exception;

	public void supprimerTitrePermis(Integer idTitrePermis) throws Exception;

}
