package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaeEvolutionDao;
import nc.mairie.spring.domain.metier.EAE.EaeEvolution;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaeEvolutionResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaeEvolution eaeEvolution = new EaeEvolution();
		eaeEvolution.setIdEaeEvolution(rs.getInt(EaeEvolutionDao.CHAMP_ID_EAE_EVOLUTION));
		eaeEvolution.setIdEae(rs.getInt(EaeEvolutionDao.CHAMP_ID_EAE));
		eaeEvolution.setMobiliteGeo(rs.getBoolean(EaeEvolutionDao.CHAMP_MOBILITE_GEO));
		eaeEvolution.setMobiliteFonct(rs.getBoolean(EaeEvolutionDao.CHAMP_MOBILITE_FONCT));
		eaeEvolution.setChangementMetier(rs.getBoolean(EaeEvolutionDao.CHAMP_CHANGEMENT_METIER));
		eaeEvolution.setDelaiEnvisage(rs.getString(EaeEvolutionDao.CHAMP_DELAI_ENVISAGE));
		eaeEvolution.setMobiliteService(rs.getBoolean(EaeEvolutionDao.CHAMP_MOBILITE_SERVICE));
		eaeEvolution.setMobiliteDirection(rs.getBoolean(EaeEvolutionDao.CHAMP_MOBILITE_DIRECTION));
		eaeEvolution.setMobiliteCollectivite(rs.getBoolean(EaeEvolutionDao.CHAMP_MOBILITE_COLLECTIVITE));
		eaeEvolution.setMobiliteAutre(rs.getBoolean(EaeEvolutionDao.CHAMP_MOBILITE_AUTRE));
		eaeEvolution.setNomCollectivite(rs.getString(EaeEvolutionDao.CHAMP_NOM_COLLECTIVITE));
		eaeEvolution.setConcours(rs.getBoolean(EaeEvolutionDao.CHAMP_CONCOURS));
		eaeEvolution.setNomConcours(rs.getString(EaeEvolutionDao.CHAMP_NOM_CONCOURS));
		eaeEvolution.setVae(rs.getBoolean(EaeEvolutionDao.CHAMP_VAE));
		eaeEvolution.setNomVae(rs.getString(EaeEvolutionDao.CHAMP_NOM_VAE));
		eaeEvolution.setTempsPartiel(rs.getBoolean(EaeEvolutionDao.CHAMP_TEMPS_PARTIEL));
		eaeEvolution.setIdSpbhorTpsPartiel(rs.getInt(EaeEvolutionDao.CHAMP_TEMPS_PARTIEL_ID_SPBHOR));
		eaeEvolution.setRetraite(rs.getBoolean(EaeEvolutionDao.CHAMP_RETRAITE));
		eaeEvolution.setDateRetraite(rs.getDate(EaeEvolutionDao.CHAMP_DATE_RETRAITE));
		eaeEvolution.setAutrePerspective(rs.getBoolean(EaeEvolutionDao.CHAMP_AUTRE_PERSPECTIVE));
		eaeEvolution.setLibAutrePerspective(rs.getString(EaeEvolutionDao.CHAMP_LIB_AUTRE_PERSPECTIVE));
		eaeEvolution.setIdComEvolution(rs.getInt(EaeEvolutionDao.CHAMP_ID_EAE_COM_EVOLUTION));

		return eaeEvolution;
	}
}
