<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>用户管理</title>
	<script type="text/javascript" src="nio/user/js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="nio/user/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="nio/user/js/heart.js"></script>
	<link rel="stylesheet" href="nio/user/css/bootstrap.min.css" />
	<link rel="stylesheet" href="nio/user/css/bootstrap-theme.min.css" />
</head>
<body>
	<div class="container">
		<h3 style="text-align: center;">用户管理</h3>
		<#if users??&&(users?size>0)>
		<table class="table table-bordered table-hover">
			<tr>
				<th>ID</th>
				<th>Username</th>
				<th>Mobile</th>
				<th>Email</th>
				<th>Birthday</th>
				<th>Create time</th>
			</tr>
		<#list users as u>
		    <tr>
				<td>${u.id }</td>
				<td>${u.username }</td>
				<td>${u.mobile }</td>
				<td>${u.email }</td>
				<td>${(u.birthday)?string("yyyy-MM-dd") }</td>
				<td>${(u.createTime)?string("yyyy-MM-dd HH:mm:ss") }</td>
			</tr>
		</#list>
		</table>
		</#if>
	</div>
</body>
</html>
