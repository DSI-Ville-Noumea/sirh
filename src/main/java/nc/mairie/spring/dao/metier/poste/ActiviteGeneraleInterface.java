package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.ActiviteGenerale;
import nc.mairie.metier.poste.FichePoste;

import java.util.List;

/**
 * Created by gael on 20/07/2017.
 */
public interface ActiviteGeneraleInterface {

    public List<ActiviteGenerale> listerToutesActiviteGenerale(FichePoste fp, Integer idFicheMetierPrimaire, Integer idFicheMetierSecondaire);

    public void supprimerToutesActiviteGenerale(FichePoste fp);

    public List<ActiviteGenerale> listerToutesActiviteGeneraleChecked(FichePoste fp);
}
