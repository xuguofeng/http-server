# http-server
A http server demo developed by java nio

## 1. 运行环境准备

操作系统 - Windows 7 64位操作系统<br />
JDK - java version "1.8.0_141"<br />
maven - Apache Maven 3.2.1<br />
工作目录 - D:\_tmp\<br />


## 2. clone项目

```java
D:\_tmp>git clone https://github.com/xuguofeng/http-server.git
```


## 3. 使用maven打包

```java
D:\_tmp>cd http-server
D:\_tmp\http-server>mvn clean package appassembler:assemble
```

执行之后，项目会部署到target/build下面


## 4. 目录结构

conf - 存放配置文件<br />
lib - 存放jar文件<br />
logs - 存放日志文件<br />
tmp - 存放临时文件<br />
WebContent - 部署web站点<br />


## 5. 部署启动服务

到target/build下<br />
	
首先，修改httpserver.bat文件，第65行<br />

```java
set BASEDIR=%~dp0\..
```
改为：<br />
```java
set BASEDIR=%~dp0\.
```

然后，双击httpserver.bat文件即可启动服务器<br />

服务器默认部署的根目录是WebContent目录，您可以在这个目录下面创建子目录存放web站点


## 6. 浏览器访问

[http://localhost:8082/doc/index.html](http://localhost:8082/doc/index.html)
