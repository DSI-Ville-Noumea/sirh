package nc.mairie.spring.dao.metier.poste;

import java.util.List;

import nc.mairie.metier.poste.NFA;
import nc.mairie.spring.dao.utils.SirhDao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class NFADao extends SirhDao implements NFADaoInterface {

	public static final String CHAMP_CODE_SERVICE = "CODE_SERVICE";
	public static final String CHAMP_NFA = "NFA";

	public NFADao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "SERVICE_NFA";
	}

	@Override
	public void supprimerNFA(String codeService, String nfa) throws Exception {
		String sql = "DELETE FROM " + NOM_TABLE + "  where " + CHAMP_CODE_SERVICE + "=? and " + CHAMP_NFA + "=? ";
		jdbcTemplate.update(sql, new Object[] { codeService, nfa });
	}

	@Override
	public void creerNFA(String codeService, String nfa) throws Exception {
		String sql = "INSERT INTO " + NOM_TABLE + " (" + CHAMP_CODE_SERVICE + "," + CHAMP_NFA + ") " + "VALUES (?,?)";
		jdbcTemplate.update(sql, new Object[] { codeService.toUpperCase(), nfa.toUpperCase() });
	}

	@Override
	public NFA chercherNFAByCodeService(String codeService) throws Exception {
		String sql = "select * from " + NOM_TABLE + " where " + CHAMP_CODE_SERVICE + " = ? ";
		NFA cadre = (NFA) jdbcTemplate.queryForObject(sql, new Object[] { codeService },
				new BeanPropertyRowMapper<NFA>(NFA.class));
		return cadre;
	}

	@Override
	public List<NFA> listerNFA() throws Exception {
		return super.getListe(NFA.class);
	}
}
