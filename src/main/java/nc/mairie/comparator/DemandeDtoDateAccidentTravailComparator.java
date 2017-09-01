package nc.mairie.comparator;

import java.util.Comparator;

import nc.mairie.gestionagent.absence.dto.DemandeDto;

public class DemandeDtoDateAccidentTravailComparator implements Comparator<DemandeDto>{
	@Override
	public int compare(DemandeDto o1, DemandeDto o2) {
		if (null == o1.getDateAccidentTravail())
			return -1;
		
		if (null == o2.getDateAccidentTravail())
			return 1;

		// ajout du "0 -" pour trier en ordre decroissant
		return 0 - o1.getDateAccidentTravail().compareTo(o2.getDateAccidentTravail());
	}
}
