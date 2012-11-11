// afin de changer l'origine
var origineCourant = 'france';
function changeOrigine(pOrigine)
{
	if (document.getElementById(pOrigine) != null){
		document.getElementById(origineCourant).style.display='none';
		document.getElementById(pOrigine).style.display='block';
		origineCourant = pOrigine;
	}
	<%=process.setLB_VIET(null)>
}