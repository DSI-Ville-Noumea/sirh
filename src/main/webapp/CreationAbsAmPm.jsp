					<table>
	            		<tr>
	            			<td width="80px">
                        		<span class="sigp2Mandatory">Date début : </span>
	            			</td>
	            			<td>
		                        <input id="<%=process.getNOM_ST_DATE_DEBUT()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_DEBUT()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_DEBUT()%>" >
		                        <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_DEBUT()%>', 'dd/mm/y');">
	            			</td>
	            			<td>
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_DEBUT_MAM(), process.getNOM_RB_M()) %> ><span class="sigp2Mandatory">M</span>
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_DEBUT_MAM(), process.getNOM_RB_AM()) %> ><span class="sigp2Mandatory">A</span>
	            			</td>
	            		</tr>
	            		<tr>
	            			<td>
                        		<span class="sigp2Mandatory">Date fin : </span>
	            			</td>
	            			<td>
		                        <input id="<%=process.getNOM_ST_DATE_FIN()%>" class="sigp2-saisie" maxlength="10"	name="<%= process.getNOM_ST_DATE_FIN()%>" size="10" type="text"	value="<%= process.getVAL_ST_DATE_FIN()%>" >
		                        <IMG  src="images/calendrier.gif" hspace="5" onclick="return showCalendar('<%=process.getNOM_ST_DATE_FIN()%>', 'dd/mm/y');">
	            			</td>
	            			<td>
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_FIN_MAM(), process.getNOM_RB_M()) %> ><span class="sigp2Mandatory">M</span>
								<INPUT type="radio" <%= process.forRadioHTML(process.getNOM_RG_FIN_MAM(), process.getNOM_RB_AM()) %> ><span class="sigp2Mandatory">A</span>
	            			</td>
	            		</tr>
	            	</table>