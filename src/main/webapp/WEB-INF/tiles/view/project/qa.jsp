<%@ include file="/WEB-INF/tiles/include.jsp"%>

<c:set var="ajaxQuickApprovalUrl" value="${pageContext.request.contextPath}/projects/quickapproval/" />

<script type="text/javascript">
function quickApproval(btn, qasessionId) {
	var url = '${ajaxQuickApprovalUrl}' + qasessionId;
	btn.disabled = true;

	
	$.get(url, function(rdata) {
		$('#div_result_msg'+qasessionId).html(rdata);
	}, "html");
	
	return false;
}

function quickFail(btn, qasessionId) {
	var url = '${ajaxQuickFailUrl}' + qasessionId;
	btn.disabled = true;

	
	$.get(url, function(rdata) {
		$('#div_result_msg_'+qasessionId).html(rdata);
	}, "html");
	
	return false;
}
</script>


<div id="ajaxload">
	<h1>QA Session - ${qa.id} - <fmt:formatDate type="both" dateStyle="short" value="${qa.createDate.time}" /></h1>
	<h5>Project - ${project.name}</h5>
	<h5>Container - ${container.name}</h5>
	
	<c:if test="${!empty message}">
		<c:set var="msgClass" value="success" />
		<c:if test="${fn:contains(message,'Error') || fn:contains(message,'Exception')}">
			<c:set var="msgClass" value="error" />
		</c:if>
		<p class="${msgClass}">${message}</p>
		<br />
	</c:if>
	
	<br />
		<input type="button" value="Approve" class="default" onclick="javascript:quickApproval(this, ${qa.id})" />
		&nbsp;<input type="button" value="Fail" class="default" onclick="javascript:quickFail(this, ${qa.id})" />
	<br />
	
	<h3>Images</h3>
	<div id="work-table-div" style="margin-bottom:20px">
		<table class="table table-striped table-hover">
		<thead>
			<tr><th>Thumbnail</th></tr>
		</thead>
		<tbody>
			<c:if test="${empty images}">
				<tr><td colspan="3">No Images.</td></tr>
			</c:if>
			<c:forEach var="image" items="${images}">
				<tr>
					<td>
						<c:set var="imageUrl" value="${imageUrls[image.id]}" />
						<img src="${imageUrl}" />
					</td>
				</tr>
			</c:forEach>
		</tbody>
		</table>
	</div>
	
</div>
