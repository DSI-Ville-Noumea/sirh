package nc.noumea.spring.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import nc.mairie.enums.EnumStatutFichePoste;
import nc.mairie.gestionagent.dto.FichePosteTreeNodeDto;

import org.junit.Test;

public class SirhServiceTest {

	private SirhService sirhService = new SirhService();
	
	@Test
	public void isFPEnfantValideGeleeTransitoire() {
		
		List<FichePosteTreeNodeDto> listFP = new ArrayList<FichePosteTreeNodeDto>();
		
		assertFalse(sirhService.isFPEnfantValideGeleeTransitoire(listFP, 0));
		
		FichePosteTreeNodeDto fpParent = new FichePosteTreeNodeDto();
		fpParent.setIdFichePoste(1);
		fpParent.setIdStatutFDP(new Integer(EnumStatutFichePoste.GELEE.getId()));
		listFP.add(fpParent);
		
		assertTrue(sirhService.isFPEnfantValideGeleeTransitoire(listFP, 0));
		
		fpParent.setIdStatutFDP(new Integer(EnumStatutFichePoste.INACTIVE.getId()));
		
		assertFalse(sirhService.isFPEnfantValideGeleeTransitoire(listFP, 0));
		
		FichePosteTreeNodeDto fpEnfant = new FichePosteTreeNodeDto();
		fpEnfant.setIdFichePoste(2);
		fpEnfant.setIdStatutFDP(new Integer(EnumStatutFichePoste.VALIDEE.getId()));
		fpParent.getFichePostesEnfant().add(fpEnfant);
		
		assertTrue(sirhService.isFPEnfantValideGeleeTransitoire(listFP, 0));
		
		fpEnfant.setIdStatutFDP(new Integer(EnumStatutFichePoste.INACTIVE.getId()));
		
		assertFalse(sirhService.isFPEnfantValideGeleeTransitoire(listFP, 0));
		
		FichePosteTreeNodeDto fpPetitEnfant = new FichePosteTreeNodeDto();
		fpPetitEnfant.setIdFichePoste(3);
		fpPetitEnfant.setIdStatutFDP(new Integer(EnumStatutFichePoste.INACTIVE.getId()));
		fpEnfant.getFichePostesEnfant().add(fpPetitEnfant);
		
		assertFalse(sirhService.isFPEnfantValideGeleeTransitoire(listFP, 0));
		
		FichePosteTreeNodeDto fpPetitEnfant2 = new FichePosteTreeNodeDto();
		fpPetitEnfant2.setIdFichePoste(4);
		fpPetitEnfant2.setIdStatutFDP(new Integer(EnumStatutFichePoste.TRANSITOIRE.getId()));
		fpEnfant.getFichePostesEnfant().add(fpPetitEnfant2);
		
		assertTrue(sirhService.isFPEnfantValideGeleeTransitoire(listFP, 0));
	}
}
