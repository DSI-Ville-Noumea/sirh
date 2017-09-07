<%@page import="nc.mairie.gestionagent.process.OePaginable"%>
<%@ page contentType="text/html; charset=UTF-8" %> 
<%@page import="nc.mairie.technique.BasicProcess"%>

<%OePaginable process = (OePaginable)request.getSession().getAttribute("process"); %>
<LINK href="theme/dataTables.css" rel="stylesheet" type="text/css">
<br /><br />
<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_NEXT_PAGE()%>" value="NEXT_PAGE">
<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_PREVIOUS_PAGE()%>" value="PREVIOUS_PAGE">
<INPUT type="submit" style="visibility : hidden;" name="<%=process.getNOM_PB_CHANGE_PAGINATION()%>" value="CHANGE_PAGINATION">
<div>
	<span class="sigp2" style="width:40px">Afficher
		<SELECT class="sigp2-saisie" name="<%= process.getNOM_LB_PAGE_LINE() %>" onchange='executeBouton("<%=process.getNOM_PB_CHANGE_PAGINATION()%>")' style="width:50px;">
			<%=process.forComboHTML(process.getVAL_LB_PAGE_LINE(), process.getVAL_LB_PAGE_LINE_SELECT()) %>
		</SELECT>
		lignes par page.
	</span>
	
	<span style="margin-left:200px;">
		<% if (process.getPageNumber() == 1) { %>
			<input type="button" onclick='executeBouton("<%=process.getNOM_PB_PREVIOUS_PAGE()%>")' class="paginate_disabled_previous" disabled="disabled" style="float:none;margin-right:10px;"/>
		<% } else { %>
			<input type="button" onclick='executeBouton("<%=process.getNOM_PB_PREVIOUS_PAGE()%>")' class="paginate_enabled_previous" style="float:none;margin-right:10px;"/>
		<% } %>
		page <%= process.getPageNumber() %> / <%= process.getMaxPageNumber() %>
		<% if (process.getPageNumber() == process.getMaxPageNumber()) { %>
			<input type="button" onclick='executeBouton("<%=process.getNOM_PB_NEXT_PAGE()%>")' class="paginate_disabled_next" disabled="disabled" style="float:none;margin-left:10px;"/>
		<% } else { %>
			<input type="button" onclick='executeBouton("<%=process.getNOM_PB_NEXT_PAGE()%>")' class="paginate_enabled_next" style="float:none;margin-left:10px;"/>
		<% } %>
	</span>
	
</div>