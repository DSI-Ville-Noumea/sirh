if (window.Event) 
document.captureEvents(Event.MOUSEUP); 
function fonctions_de_la_page_desactivees() 
{
event.touche_pas_a_ma_page = true
event.returnValue = false;
return false;
}
function click_droit_desactive(e)
{
if (window.Event) 
{
if (e.which == 2 || e.which == 3)
return false;
}
else
if (event.button == 2 || event.button == 3)
{
event.touche_pas_a_ma_page = true
event.returnValue = false;
return false;
}
}
document.oncontextmenu = fonctions_de_la_page_desactivees; 
document.onmousedown = click_droit_desactive; 

