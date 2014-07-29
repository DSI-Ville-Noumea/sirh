package nc.mairie.spring.dao.metier.parametrage;

import java.util.List;

import nc.mairie.metier.parametrage.CodeRome;
import nc.mairie.spring.dao.utils.SirhDao;

public class CodeRomeDao extends SirhDao implements CodeRomeDaoInterface {

	public static final String CHAMP_LIB_CODE_ROME = "LIB_CODE_ROME";
	public static final String CHAMP_DESC_CODE_ROME = "DESC_CODE_ROME";

	public CodeRomeDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "P_CODE_ROME";
		super.CHAMP_ID = "ID_CODE_ROME";
	}

	@Override
	public void creerCodeRome(String libelleCodeRome, String descCodeRome) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_LIB_CODE_ROME + "," + CHAMP_DESC_CODE_ROME + ") "
				+ "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { libelleCodeRome.toUpperCase(), descCodeRome });

	}

	@Override
	public void supprimerCodeRome(Integer idCodeRome) throws Exception {
		super.supprimerObject(idCodeRome);
	}

	@Override
	public List<CodeRome> listerCodeRome() throws Exception {
		return super.getListe(CodeRome.class);
	}
}
