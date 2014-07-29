package nc.mairie.spring.dao.metier.poste;

import java.util.List;

import nc.mairie.metier.poste.Budget;
import nc.mairie.spring.dao.utils.SirhDao;

public class BudgetDao extends SirhDao implements BudgetDaoInterface {

	public static final String CHAMP_LIB_BUDGET = "LIB_BUDGET";

	public BudgetDao(SirhDao sirhDao) {
		super.dataSource = sirhDao.getDataSource();
		super.jdbcTemplate = sirhDao.getJdbcTemplate();
		super.NOM_TABLE = "R_BUDGET";
		super.CHAMP_ID = "ID_BUDGET";
	}

	@Override
	public Budget chercherBudget(Integer idBudget) throws Exception {
		return super.chercherObject(Budget.class, idBudget);
	}

	@Override
	public List<Budget> listerBudget() throws Exception {
		return super.getListe(Budget.class);
	}
}
