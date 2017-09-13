package nc.mairie.gestionagent.process;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import nc.mairie.metier.Const;
import nc.mairie.technique.BasicProcess;
import nc.mairie.technique.Services;

public abstract class OePaginable extends BasicProcess {

	private static final long serialVersionUID = -4517335279542055010L;
	
	// pagination
	private Integer pageSize;
	private Integer pageNumber;
	private Integer maxPageNumber;
	private Integer resultSize;
	
	private ArrayList<String> listePageSize;

	public final static Integer DEFAULT_PAGE_SIZE = 10;
	public final static Integer DEFAULT_OFFSET = 1;
	
	private String[] LB_PAGE_LINE;
	
	protected void initialisePagination() {
		
		if (getPageNumber() == null)
			setPageNumber(DEFAULT_OFFSET);
		
		if (getPageSize() == null)
			setPageSize(DEFAULT_PAGE_SIZE);
		
		if (getMaxPageNumber() == null)
			updateMaxPageNumber();
		
		if (getLB_PAGE_LINE() == LBVide) {
			String[] list = { String.valueOf(10), String.valueOf(25), String.valueOf(50), String.valueOf(100) };

			ArrayList<String> arrayList = new ArrayList<String>();

			for (String an : list)
				arrayList.add(an);

			setListePageSize(arrayList);

			setLB_PAGE_LINE(list);
			addZone(getNOM_LB_PAGE_LINE_SELECT(), Const.ZERO);
		}
	}
	
	public void updateMaxPageNumber() {
		// S'il n'y a pas de résultats, on affiche une seule page vide.
		if (getResultSize() == null) {
			setMaxPageNumber(1);
			return;
		}
		Integer fullPage = getResultSize() / getPageSize();
		boolean isLastPageFull = getResultSize() % getPageSize() == 0;
		if (!isLastPageFull || fullPage == 0)
			fullPage += 1;
		setMaxPageNumber(fullPage);
	}
	
	public void getAllResultCount() {
		// This method has to be override by each children of OePaginable.
	}

	public boolean updatePagination(HttpServletRequest request) throws Exception {
		// recupération nombre de données par page
		int indicePageSize = (Services.estNumerique(getVAL_LB_PAGE_LINE_SELECT()) ? Integer.parseInt(getVAL_LB_PAGE_LINE_SELECT()) : -1);
		String value = getListePageSize().get(indicePageSize);
		
		setPageSize(Integer.valueOf(value));
		
		updateMaxPageNumber();
		
		if (getMaxPageNumber() < getPageNumber())
			setPageNumber(getMaxPageNumber());
		
		return true;
	}
	
	@Override
	public boolean recupererStatut(HttpServletRequest request) throws Exception {

		// Si sélection du nombre de données par page 
		if (testerParametre(request, getNOM_PB_CHANGE_PAGINATION())) {
			return performPB_CHANGE_PAGINATION(request);
		}

		// Si page suivante
		if (testerParametre(request, getNOM_PB_NEXT_PAGE())) {
			return performPB_NEXT_PAGE(request);
		}

		// Si page précédente
		if (testerParametre(request, getNOM_PB_PREVIOUS_PAGE())) {
			return performPB_PREVIOUS_PAGE(request);
		}
		
		return true;
	}

	public boolean performPB_NEXT_PAGE(HttpServletRequest request) throws Exception {
		pageNumber += 1;
		return true;
	}

	public boolean performPB_PREVIOUS_PAGE(HttpServletRequest request) throws Exception {
		if (pageNumber > 1)
			pageNumber -= 1;
		return true;
	}

	public boolean performPB_CHANGE_PAGINATION(HttpServletRequest request) throws Exception {
		updatePagination(request);
		return true;
	}
	
	// Getters / Setters
	private String[] getLB_PAGE_LINE() {
		if (LB_PAGE_LINE == null)
			LB_PAGE_LINE = initialiseLazyLB();
		return LB_PAGE_LINE;
	}

	private void setLB_PAGE_LINE(String[] newLB_PAGE_LINE) {
		LB_PAGE_LINE = newLB_PAGE_LINE;
	}

	public String getNOM_LB_PAGE_LINE() {
		return "NOM_LB_PAGE_LINE";
	}

	public String getNOM_LB_PAGE_LINE_SELECT() {
		return "NOM_LB_PAGE_LINE_SELECT";
	}

	public String[] getVAL_LB_PAGE_LINE() {
		return getLB_PAGE_LINE();
	}

	public String getVAL_LB_PAGE_LINE_SELECT() {
		return getZone(getNOM_LB_PAGE_LINE_SELECT());
	}

	public ArrayList<String> getListePageSize() {
		return listePageSize;
	}

	public void setListePageSize(ArrayList<String> listePageSize) {
		this.listePageSize = listePageSize;
	}

	public String getNOM_PB_CHANGE_PAGINATION() {
		return "NOM_PB_CHANGE_PAGINATION";
	}

	public String getNOM_PB_PREVIOUS_PAGE() {
		return "NOM_PB_PREVIOUS_PAGE";
	}

	public String getNOM_PB_NEXT_PAGE() {
		return "NOM_PB_NEXT_PAGE";
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getMaxPageNumber() {
		return maxPageNumber;
	}

	public void setMaxPageNumber(Integer maxPageNumber) {
		this.maxPageNumber = maxPageNumber;
	}

	public Integer getResultSize() {
		return resultSize;
	}

	public void setResultSize(Integer resultSize) {
		this.resultSize = resultSize;
	}

}