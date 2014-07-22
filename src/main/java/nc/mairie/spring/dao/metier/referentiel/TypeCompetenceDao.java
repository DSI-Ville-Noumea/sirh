package nc.mairie.spring.dao.metier.referentiel;

import nc.mairie.metier.referentiel.TypeCompetence;
import nc.mairie.spring.dao.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class TypeCompetenceDao extends SirhDao implements TypeCompetenceDaoInterface {

	public static final String CHAMP_LIB_TYPE_COMPETENCE = "LIB_TYPE_COMPETENCE";

	public TypeCompetenceDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "R_TYPE_COMPETENCE";
		super.CHAMP_ID = "ID_TYPE_COMPETENCE";
	}

	@Override
	public TypeCompetence chercherTypeCompetenceAvecLibelle(String typeComp) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_LIB_TYPE_COMPETENCE + " = ? ";
		TypeCompetence cadre = (TypeCompetence) jdbcTemplate.queryForObject(sql, new Object[] { typeComp },
				new BeanPropertyRowMapper<TypeCompetence>(TypeCompetence.class));
		return cadre;
	}

	@Override
	public TypeCompetence chercherTypeCompetence(Integer idTypeComp) throws Exception {
		return super.chercherObject(TypeCompetence.class, idTypeComp);
	}
}
