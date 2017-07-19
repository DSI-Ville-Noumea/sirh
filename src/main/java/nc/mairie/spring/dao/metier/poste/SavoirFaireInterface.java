package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.FichePoste;
import nc.mairie.metier.poste.SavoirFaire;

import java.util.List;

/**
 * Created by gael on 20/07/2017.
 */
public interface SavoirFaireInterface {

    public List<SavoirFaire> listerTousSavoirFaireGeneraux(FichePoste fp);
}
