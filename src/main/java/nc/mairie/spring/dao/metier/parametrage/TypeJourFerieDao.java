package nc.mairie.spring.dao.metier.parametrage;

import java.util.List;

import nc.mairie.metier.parametrage.TypeJourFerie;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class TypeJourFerieDao extends SirhDao implements TypeJourFerieDaoInterface {

	public static final String CHAMP_LIB_TYPE_JOUR_FERIE = "LIB_TYPE_JOUR_FERIE";

	public TypeJourFerieDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "R_TYPE_JOUR_FERIE";
		super.CHAMP_ID = "ID_TYPE_JOUR_FERIE";
	}

	@Override
	public List<TypeJourFerie> listerTypeJour() throws Exception {
		return super.getListe(TypeJourFerie.class);
	}

	@Override
	public TypeJourFerie chercherTypeJourByLibelle(String libelle) {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_LIB_TYPE_JOUR_FERIE + " = ? ";
		TypeJourFerie type = (TypeJourFerie) jdbcTemplate.queryForObject(sql, new Object[] { libelle },
				new BeanPropertyRowMapper<TypeJourFerie>(TypeJourFerie.class));
		return type;
	}
}
