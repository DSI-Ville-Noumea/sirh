package nc.mairie.comparator;

import java.util.Comparator;

import nc.mairie.gestionagent.absence.dto.DemandeDto;

public class DemandeDtoDateDebutComparator implements Comparator<DemandeDto>{
	@Override
	public int compare(DemandeDto o1, DemandeDto o2) {
		// ajout du "0 -" pour trier en ordre decroissant
		return 0 - o1.getDateDebut().compareTo(o2.getDateDebut());
	}
}
