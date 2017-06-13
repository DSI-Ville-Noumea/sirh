package nc.mairie.comparator;

import java.util.Comparator;

import nc.mairie.gestionagent.dto.AgentDto;

public class AgentDtoComparator implements Comparator<AgentDto>{
	@Override
	public int compare(AgentDto o1, AgentDto o2) {
		return o1.getNom().compareTo(o2.getNom());
	}

}
