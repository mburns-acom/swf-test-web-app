<%@ include file="/WEB-INF/tiles/include.jsp"%>

<c:set var="ajaxQuickApprovalUrl" value="${pageContext.request.contextPath}/projects/quickapproval/" />
<c:set var="ajaxQuickFailUrl" value="${pageContext.request.contextPath}/projects/quickfail/" />

<script type="text/javascript">
function quickApproval(btn, qasessionId) {
	var url = '${ajaxQuickApprovalUrl}' + qasessionId;
	btn.disabled = true;

	
	$.get(url, function(rdata) {
		$('#div_result_msg_'+qasessionId).html(rdata);
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
	<h1>Project - ${project.name}</h1>
	
	<c:if test="${!empty message}">
		<c:set var="msgClass" value="success" />
		<c:if test="${fn:contains(message,'Error') || fn:contains(message,'Exception')}">
			<c:set var="msgClass" value="error" />
		</c:if>
		<p class="${msgClass}">${message}</p>
		<br />
	</c:if>
	
	<h3>Containers</h3>
	<div id="table-div" style="margin-bottom:20px">
		<table class="table table-striped table-hover">
		<thead>
			<tr><th>ID</th><th>ParentId</th><th>Name</th></tr>
		</thead>
		<tbody>
			<c:if test="${empty containers}">
				<tr><td colspan="3">No containers.</td></tr>
			</c:if>
			<c:forEach var="container" items="${containers}">
				<tr>
					<td>${container.id}</td>
					<td><c:if test="${container.parentId >= 0}">${container.parentId}</c:if></td>
					<td>${container.name}</td>
				</tr>
			</c:forEach>
		</tbody>
		</table>
	</div>
	
	<h3>QA Sessions</h3>
	<div id="work-table-div" style="margin-bottom:20px">
		<table class="table table-striped table-hover">
		<thead>
			<tr><th>ID</th><th>Status</th><th>Created</th><th>Completed</th><th>Result</th><th>Reason</th></tr>
		</thead>
		<tbody>
			<c:if test="${empty work}">
				<tr><td colspan="3">No QA Sessions.</td></tr>
			</c:if>
			<c:forEach var="qa" items="${work}">
				<tr>
					<td>${qa.id}</td>
					<td>
						${qa.status}
						<c:if test="${'ready' == qa.status}">
							<br /><a href="${pageContext.request.contextPath}/qasessions/${qa.id}">View Images</a>
							<br /><input type="button" value="Quick Approval" class="default" onclick="javascript:quickApproval(this, ${qa.id})" />
							&nbsp;<input type="button" value="Quick Fail" class="default" onclick="javascript:quickFail(this, ${qa.id})" />
						</c:if>
					</td>
					<td><fmt:formatDate type="both" dateStyle="short" value="${qa.createDate.time}" /></td>
					<td><fmt:formatDate type="both" dateStyle="short" value="${qa.lastUpdatedDate.time}" /></td>
					<td id="div_result_msg_${qa.id}">${qa.result}</td>
					<td>${qa.reason}</td>
				</tr>
			</c:forEach>
		</tbody>
		</table>
	</div>
	
	<h3>Upload Work</h3>
	<div id="upload-table-div" style="margin-bottom:20px">
		<p>Here you can upload a zip file of pdf pages to start processing.</p>
		<form:form commandName="zipUpload" id="zipUpload" name="zipUpload" method="post" action="${pageContext.request.contextPath}/projects/${project.id}/uploadZipFile" enctype="multipart/form-data">
			<input type="file" name="file" maxlength="255" size="40" />
			<input type="submit" id="upload_button" value="Upload" />
		</form:form>
	</div>
	
</div>