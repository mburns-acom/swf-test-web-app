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

<style>
div.img {
	margin: 5px;
	border: 1px solid #ccc;
	float: left;
	width: 300px;
}

div.img:hover {
	border: 1px solid #777;
}

div.img img {
	width: 100%;
	height: auto;
 }

div.desc {
	padding: 15px;
	text-align: center;
	word-wrap: break-word;
	overflow: hidden;
}
</style>


<div id="mainPage" style="clear:both;">
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
	
	<div id="buttons-div" style="margin-bottom:20px">
		<input type="button" value="Approve" class="default" onclick="javascript:quickApproval(this, ${qa.id})" />
		&nbsp;<input type="button" value="Fail" class="default" onclick="javascript:quickFail(this, ${qa.id})" />
	</div>
	
	<h5>Images</h5>
	<div id="images-div" style="margin-bottom:50px;clear:both;">
		<c:if test="${empty images}">
			<div>No Images.</div>
		</c:if>
		<c:forEach var="image" items="${images}">
			<div class="img">
				<c:set var="imageUrl" value="${imageUrls[image.id]}" />
				<img src="${imageUrl}" width="300" height="300" />
				<div class="desc">${image.name}</div>
			</div>
		</c:forEach>
	</div>
	
</div>
