package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.FMFP;
import nc.mairie.metier.poste.FicheMetier;

/**
 * Created by gael on 29/06/2017.
 */
public interface FicheMetierDaoInterface {

    public FicheMetier chercherFicheMetierAvecFichePoste(FMFP lien) throws Exception;

}
