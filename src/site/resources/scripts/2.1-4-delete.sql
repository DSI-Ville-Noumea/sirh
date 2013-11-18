----------------------------------------------------------------
-- RECETTE
----------------------------------------------------------------
update sirh2.histo_fiche_poste set id_cdthor_bud =0 where (id_cdthor_bud =13 or id_cdthor_bud =14);
update sirh2.histo_fiche_poste set id_cdthor_reg =0 where (id_cdthor_reg =13 or id_cdthor_reg =14);
update sirh2.fiche_poste set id_cdthor_bud =0 where (id_cdthor_bud =13 or id_cdthor_bud =14);
update sirh2.fiche_poste set id_cdthor_reg =0 where (id_cdthor_reg =13 or id_cdthor_reg =14);
delete from mairie2.spbhor where cdthor=13 or cdthor=14;

----------------------------------------------------------------
-- PROD
----------------------------------------------------------------
update sirh.histo_fiche_poste set id_cdthor_bud =0 where (id_cdthor_bud =13 or id_cdthor_bud =14);
update sirh.histo_fiche_poste set id_cdthor_reg =0 where (id_cdthor_reg =13 or id_cdthor_reg =14);
update sirh.fiche_poste set id_cdthor_bud =0 where (id_cdthor_bud =13 or id_cdthor_bud =14);
update sirh.fiche_poste set id_cdthor_reg =0 where (id_cdthor_reg =13 or id_cdthor_reg =14);
delete from mairie.spbhor where cdthor=13 or cdthor=14;



