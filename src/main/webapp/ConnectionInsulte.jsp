<!-- Sample JSP file --> <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Page Designer V3.5.3 for Windows">
<META http-equiv="Content-Style-Type" content="text/css">
<TITLE>Put Your Title Here</TITLE>
<SCRIPT language="JavaScript">

//afin de sélectionner un élément dans une liste
function executeBouton(nom)
{
document.formu.elements[nom].click();
}

// afin de mettre le focus sur une zone précise
function setfocus(nom)
{
if (document.formu.elements[nom] != null)
document.formu.elements[nom].focus();
}

</SCRIPT>
<LINK rel="stylesheet" href="theme/sigp2.css" type="text/css">
</HEAD>
<BODY bgcolor="#FFFFFF">
<TABLE border="0" width="100%" height="100%">
  <TBODY>
    <TR>
      <TD><FORM name="formu" method="POST">
      <TABLE border="1" width="800" class="TableauEcran">
        <COL span="1" align="center" valign="middle">
        <TBODY>
    <TR>
      <TD align="center">
      <FONT size="6">TRICHEUR ;-)</FONT></TD>
    </TR>
    <TR>
      <TD align="center">
      <TABLE border="0">
        <TBODY>
          <TR>
            <TD><FONT color="#ff0000" face="Lucida Sans Unicode">Le mot de passe est incorrect ou vous n'êtes pas habilités</FONT></TD>
          </TR>
        </TBODY>
      </TABLE>
      </TD>
    </TR>
    <TR>
      <TD align="center"></TD>
    </TR>
  </TBODY>
</TABLE>
      </FORM></TD>
    </TR>
  </TBODY>
</TABLE>
</BODY>
</HTML>