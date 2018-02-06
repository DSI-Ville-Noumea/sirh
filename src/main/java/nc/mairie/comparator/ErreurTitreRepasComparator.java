package nc.mairie.comparator;

import java.util.Comparator;

import nc.mairie.gestionagent.pointage.dto.TitreRepasEtatPayeurTaskDto;

public class ErreurTitreRepasComparator implements Comparator<TitreRepasEtatPayeurTaskDto> {

	@Override
	public int compare(TitreRepasEtatPayeurTaskDto arg0, TitreRepasEtatPayeurTaskDto arg1) {
		return arg1.getDateExport().compareTo(arg0.getDateExport());
	}

}
