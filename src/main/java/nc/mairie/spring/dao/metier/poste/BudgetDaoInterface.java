package nc.mairie.spring.dao.metier.poste;

import java.util.List;

import nc.mairie.metier.poste.Budget;

public interface BudgetDaoInterface {

	public Budget chercherBudget(Integer idBudget) throws Exception;

	public List<Budget> listerBudget() throws Exception;

}
