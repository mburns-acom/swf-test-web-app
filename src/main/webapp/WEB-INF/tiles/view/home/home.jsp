<%@ include file="/WEB-INF/tiles/include.jsp"%>
<div id="ajaxload">
	<h1>Home</h1>
	<h4>Welcome to the test application.</h4>
	
	<h3>Projects</h3>
	<div id="table-div" style="margin-bottom:20px">
		<table class="table table-striped table-hover">
		<thead>
			<th>ID</th><th>Name</th>
		</thead>
		<tbody>
		
			<c:forEach var="project" items="${page.content}">
				<tr>
					<td><a href="${pageContext.request.contextPath}/project/${project.id}">${project.id}</a></td>
					<td>${project.name}</td>
				</tr>
			</c:forEach>
		</tbody>
		</table>
	</div>
	
	
</div>