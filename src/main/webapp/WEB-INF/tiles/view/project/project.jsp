<%@ include file="/WEB-INF/tiles/include.jsp"%>
<div id="ajaxload">
	<h1>Project - ${project.name}</h1>
	
	<h3>Containers</h3>
	<div id="table-div" style="margin-bottom:20px">
		<table class="table table-striped table-hover">
		<thead>
			<tr><th>ID</th><th>Name</th><th>ParentId</th></tr>
		</thead>
		<tbody>
		
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
		
			<c:forEach var="unit" items="${work}">
				<tr>
					<td>${unit.id}</td>
					<td>${unit.name}</td>
				</tr>
			</c:forEach>
		</tbody>
		</table>
	</div>
	
</div>