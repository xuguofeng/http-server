<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>NIO HTTP 服务器</title>
</head>
<body>
<h1>NIO HTTP 服务器</h1>
<h2>使用链接下载文件</h2>
<a target="_blank" href="attachment/备份.zip">备份.zip</a><br />
<a target="_blank" href="attachment/备份.txt">备份.txt</a><br />
<a target="_blank" href="attachment/backup.zip">backup.zip</a><br />
<a target="_blank" href="attachment/backup.txt">backup.txt</a><br />
<a target="_blank" href="attachment/http.out.20180828.7z">http.out.20180828.7z</a><br />
<h2>测试表单提交</h2>
<form action="index.html" method="get">
	姓名：<input name="name" value="${user.name}"><br />
	年龄：<input name="age" value="${user.age}"><br />
	时间：<input name="birthday" value="${(user.birthday)?string("yyyy-MM-dd")}"><br />
	<input type="submit" value="提交" >
</form>
</body>
</html>