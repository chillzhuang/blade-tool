## 简介
blade-tool 是如梦技术团队作品， 是一个基于 Spring Boot 2 & Spring Cloud Finchley ，封装组合大量组件，用于快速构建中大型API、RESTful API项目的核心包。

注意事项
* 注册中心为 Consul 
* 基于 SpringBoot2.x 版本 以及 SpringCloud Finchley 版本

技术选型&文档
* Spring Boot（[查看Spring Boot学习&使用指南](http://www.jianshu.com/p/1a9fd8936bd8)）
* Spring Cloud（[查看Spring Cloud学习&使用指南](https://springcloud.cc/)）
* Mybatis-Plus（[查看官方文档](https://baomidou.gitee.io/mybatis-plus-doc/#/quick-start)）
* JsonWebToken（[查看官方文档](https://jwt.io/)） 

## 鸣谢
* 如梦技术（[DreamLu](https://www.dreamlu.net/)）
* pigx（[Pig Microservice](https://www.pig4cloud.com/zh-cn/)）
* avue（[avue](https://avue.top/)）
* gitee.ltd（[gitee.ltd](https://gitee.ltd/)）
* 鲸宵（<a href="https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/jx.png" target="_blank">鲸宵</a>）

## 工程结构
``` 
blade-tool
├── blade-core-boot -- 业务包综合模块
├── blade-core-launch -- 基础启动模块
├── blade-core-log -- 日志封装模块 
├── blade-core-mybatis -- mybatis拓展封装模块 
├── blade-core-secure -- 安全模块 
├── blade-core-swagger -- swagger拓展封装模块 
└── blade-core-tool -- 工具包模块 
	 
```

## 开源协议
LGPL（[GNU Lesser General Public License](http://www.gnu.org/licenses/lgpl.html)）

LGPL是GPL的一个为主要为类库使用设计的开源协议。和GPL要求任何使用/修改/衍生之GPL类库的的软件必须采用GPL协议不同。LGPL允许商业软件通过类库引用(link)方式使用LGPL类库而不需要开源商业软件的代码。这使得采用LGPL协议的开源代码可以被商业软件作为类库引用并发布和销售。

但是如果修改LGPL协议的代码或者衍生，则所有修改的代码，涉及修改部分的额外代码和衍生的代码都必须采用LGPL协议。因此LGPL协议的开源代码很适合作为第三方类库被商业软件引用，但不适合希望以LGPL协议代码为基础，通过修改和衍生的方式做二次开发的商业软件采用。

## 用户权益
* 允许以引入不改源码的形式免费用于学习、毕设、公司项目、私活等。
* 特殊情况修改代码，但仍然想闭源需经过作者同意。

## 禁止事项
* 直接将本项目挂淘宝等商业平台出售。
* 非界面代码50%以上相似度的二次开源，二次开源需先联系作者。

注意：若禁止条款被发现有权追讨19999的授权费。

## [SpringBlade2.0](https://gitee.com/smallc/SpringBlade) 界面一览（开源协议为Apache License）
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/springblade-k8s.png "业务系统")
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/springblade-traefik.png "业务系统")
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/springblade-traefik-health.png "业务系统")
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/springblade-harbor.png "业务系统")
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/springblade-consul.png "业务系统")
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/springblade-consul-nodes1.png "业务系统")
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/springblade-consul-nodes2.png "业务系统")
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/springblade-admin1.png "业务系统")
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/springblade-admin2.png "业务系统")
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/springblade-swagger1.png "业务系统")
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/springblade-swagger2.png "业务系统")
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/sword-menu.png "业务系统")
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/sword-menu-edit.png "业务系统")
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/sword-menu-icon.png "业务系统")
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/sword-role.png "业务系统")
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/sword-user.png "业务系统")
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/sword-dict.png "业务系统")
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/sword-locale-cn.png "业务系统")
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/sword-locale-us.png "业务系统")
![业务系统](https://raw.githubusercontent.com/chillzhuang/blade-tool/master/pic/sword-log.png "业务系统")