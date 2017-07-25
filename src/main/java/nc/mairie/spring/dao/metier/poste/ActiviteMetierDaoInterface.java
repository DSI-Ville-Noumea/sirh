package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.ActiviteMetier;
import nc.mairie.metier.poste.FicheMetier;
import nc.mairie.metier.poste.FichePoste;

import java.util.List;

/**
 * Created by gael on 18/07/2017.
 */
public interface ActiviteMetierDaoInterface {

    public List<ActiviteMetier> listerToutesActiviteMetier(FichePoste fp);

    public List<ActiviteMetier> listerToutesActiviteMetier(FicheMetier fm);
}
