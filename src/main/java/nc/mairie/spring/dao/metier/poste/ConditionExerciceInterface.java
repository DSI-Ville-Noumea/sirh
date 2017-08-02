package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.ConditionExercice;
import nc.mairie.metier.poste.FichePoste;

import java.util.List;

/**
 * Created by gael on 20/07/2017.
 */
public interface ConditionExerciceInterface {

    public List<ConditionExercice> listerToutesConditionExercice(FichePoste fp, Integer idFicheMetierPrimaire, Integer idFicheMetierSecondaire);

    public void supprimerToutesConditionExercice(FichePoste fp);

    public List<ConditionExercice> listerToutesConditionExerciceChecked(FichePoste fp, Integer idFicheMetierPrimaire, Integer idFicheMetierSecondaire);

}
