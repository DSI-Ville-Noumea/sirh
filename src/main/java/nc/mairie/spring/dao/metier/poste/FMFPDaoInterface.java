package nc.mairie.spring.dao.metier.poste;

import nc.mairie.metier.poste.FMFP;

/**
 * Created by gael on 29/06/2017.
 */
public interface FMFPDaoInterface {

    public FMFP chercherFMFPAvecNumFP(Integer idFicheMetier, boolean metierPrimaire) throws Exception;

}
