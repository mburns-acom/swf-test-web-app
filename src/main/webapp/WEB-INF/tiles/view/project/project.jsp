<%@ include file="/WEB-INF/tiles/include.jsp"%>
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
			<tr><th>ID</th><th>Name</th><th>ParentId</th></tr>
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
	
	<h3>Work Units</h3>
	<div id="work-table-div" style="margin-bottom:20px">
		<table class="table table-striped table-hover">
		<thead>
			<tr><th>ID</th><th>Name</th></tr>
		</thead>
		<tbody>
			<c:if test="${empty work}">
				<tr><td colspan="3">No work units.</td></tr>
			</c:if>
			<c:forEach var="unit" items="${work}">
				<tr>
					<td>${unit.id}</td>
					<td>${unit.name}</td>
				</tr>
			</c:forEach>
		</tbody>
		</table>
	</div>
	
	<h3>Upload Work</h3>
	<div id="upload-table-div" style="margin-bottom:20px">
		<p>Here you can upload a zip file of pdf pages to start processing.</p>
		<form:form commandName="zipUpload" id="zipUpload" name="zipUpload" method="post" action="${pageContext.request.contextPath}/projects/${project.guid}/uploadZipFile" enctype="multipart/form-data">
			<input type="file" name="file" maxlength="255" size="40" />
			<input type="submit" id="upload_button" value="Upload" />
		</form:form>
	</div>
	
</div>