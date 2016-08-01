package nc.mairie.spring.dao.metier.poste;

import java.util.ArrayList;
import java.util.Date;

import nc.mairie.enums.EnumTypeHisto;
import nc.mairie.metier.poste.HistoFichePoste;
import nc.mairie.technique.UserAppli;

public interface HistoFichePosteDaoInterface {

	public void creerHistoFichePoste(HistoFichePoste histoFP, UserAppli user, EnumTypeHisto typeHisto) throws Exception;

	public void creerHistoFichePosteBD(HistoFichePoste histoFP) throws Exception;

	public void modifierDateFinAppliServHistoFichePoste(HistoFichePoste histoFP) throws Exception;

	public ArrayList<HistoFichePoste> listerHistoFichePosteById(Integer idFichePoste) throws Exception;

	public ArrayList<HistoFichePoste> listerHistoFichePosteDansDate(Integer idFichePoste, Date dateDebutAff, Date dateFinAff) throws Exception;

	public ArrayList<HistoFichePoste> listerHistoFichePosteAvecTitrePoste(Integer idTitrePoste) throws Exception;

}
