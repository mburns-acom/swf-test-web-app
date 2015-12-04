<%@ include file="/WEB-INF/tiles/include.jsp"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles-extras" prefix="tilesx" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title><tiles:getAsString name="title" /></title>

		<!-- Latest compiled and minified CSS -->
		<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<!-- 		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
		<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script> -->

        <link rel="stylesheet" href='<c:url value="/public/css/site.css"/>'>
        
        <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
        <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
        <!--[if lt IE 9]>
          <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
          <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
        <![endif]-->
    </head>
    <body>
        <!-- Header -->
        <tiles:insertAttribute name="header" />
        <!-- Body -->
        <div class="container">
	        <tiles:insertAttribute name="body" />
	        <!-- Footer -->
	        <tiles:insertAttribute name="footer" />
        </div>
        <tilesx:useAttribute id="files" name="jsfiles" classname="java.util.List" />
		<c:forEach var="file" items="${files}">
		  <script src='<c:url value="${file}"/>'></script>
		</c:forEach>
    </body>
</html>