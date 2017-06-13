package nc.mairie.comparator;

import java.util.Comparator;

import nc.mairie.gestionagent.dto.ApprobateurDto;

public class ApprobateurDtoNameComparator implements Comparator<ApprobateurDto>{

	@Override
	public int compare(ApprobateurDto o1, ApprobateurDto o2) {
		return o1.getApprobateur().getNom().compareTo(o2.getApprobateur().getNom());
	}

}
