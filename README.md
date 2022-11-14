# JSP下的白魔法：JspEncounter

## 基础说明

### 环境要求

环境要求：JDK8

本项目在win10/tomcat10下编译测试，其他平台有问题请反馈

### 工具参数说明

工具有两种模式，通过 --mode 进行设置 ：jsp模式将source文件转换为output ；encode模式则是单纯地使用编码器进行编码。

```
       _           ______                             _
      | |         |  ____|                           | |
      | |___ _ __ | |__   _ __   ___ ___  _   _ _ __ | |_ ___ _ __
  _   | / __| '_ \|  __| | '_ \ / __/ _ \| | | | '_ \| __/ _ \ '__|
 | |__| \__ \ |_) | |____| | | | (_| (_) | |_| | | | | ||  __/ |
  \____/|___/ .__/|______|_| |_|\___\___/ \__,_|_| |_|\__\___|_|
            | |
            |_|
usage: java -jar JspEncounter.jar
 -c,--config <arg>              xlsx config file
 -cdata,--cdata <arg>           cdata cap: -1, 10 ...
 -charset,--charset <arg>       charset name: utf-8, utf-16be ...
 -e,--encode <arg>              encode mode's encoding: charset, cdata,
                                entity
 -entity,--entity <arg>         entity type: 10, 16, 13 ...
 -help                          usage help
 -m,--mode <arg>                jspfile or encode
 -o,--output <arg>              output file: default result.jsp/result.txt
 -removeBom,--removeBom <arg>   removeBom : default true
 -s,--source <arg>              source file

demo:
     java "-Dfile.encoding=utf-8" -jar JspEncounter.jar -m jsp -c matrix.xlsx -s source.jsp -o result.jsp
     java -jar JspEncounter.jar -m encode --encode charset --charset utf-16be -s source.txt -o result.txt
```

#### jsp mode

如下示例，指定配置文件`martix.xlsx`，将源文件source.jsp转换为result.jsp

```
"C:\Program Files\Java\jdk1.8.0_191\bin\java.exe" "-Dfile.encoding=utf-8" -jar JspEncounter.jar -m jsp -c matrix.xlsx -s source.jsp -o result.jsp
```

配置文件的配置可参考 `Jsp下的白魔法` 一文

原始jsp文件要求使用 `<%@page ... >%>`、 `<%! ..%> `、 `<% %>`三种标签，且每种标签可出现多次，但顺序需要固定为这里列出的顺序。

#### encode mode

encode模式的编码器有三种：

```
--mode encode
	--encode cdata
		--cdata -1,10 
	--encode charset
		--charset utf-16be ...
		--removeBom Default: true
	--encode entity
		--entity 10 16 13
```

转换source.txt的字符集：

```
"C:\Program Files\Java\jdk1.8.0_191\bin\java" -jar JspEncounter.jar -m encode --encode charset --charset utf-16be -s source.txt -o result.txt
```

