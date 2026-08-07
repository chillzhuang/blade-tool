# BladeX-Tool Spring Boot 4 升级适配指南

> 适用工程:`blade-tool`(SpringBlade 核心工具工程,开源扁平版布局)
> 升级区间:Spring Boot 3.5.13 → **4.1.0**,Spring Cloud 2025.0.2 → **2025.1.2**
> 文档定位:照此文档逐模块、逐文件操作即可完成升级;每个主题给出「为什么改 → 改哪个文件 → 改前/改后示例」
> 文档性质：升级指导 / 迁移规范。既供工程师直接阅读执行，也供 AI 依此拆解任务、逐文件推进迁移。

---

## 0. 怎么用这份文档

1. 先读 **第 1 节版本矩阵** 与 **第 2 节执行顺序**,建立全局认知。
2. **严格按第 2 节的顺序推进**:注解处理器(总闸门)→ 依赖坐标 → 底层模块 import → 运行期适配 → 第三方组件。顺序错了会连锁编译失败。
3. 每个主题按「改前 → 改后」示例照做,改完 `mvn clean install -DskipTests` 验证编译。
4. 编译通过即为本文档的交付边界;**应用启动与集成测试由你在真实环境执行**(见第 13 节运行期验证要点)。

---

## 1. 版本矩阵(升级前 → 升级后)

### 1.1 框架核心

| 组件 | 升级前 | 升级后 | 说明 |
|------|--------|--------|------|
| JDK | 17 | **21** | Boot 4 最低仍是 17,本次一并主动升到 21 LTS(见 §16.1);**构建需 JDK 21** |
| Spring Framework | 6.2.17 | 7.0.8 | 由 Boot BOM 传递,**不再单独 import `spring-framework-bom`** |
| Spring Boot | 3.5.13 | **4.1.0** | 总闸 |
| Spring Cloud | 2025.0.2 | **2025.1.2** | 与 Boot 4 强绑定 |
| Spring Cloud Alibaba | 2025.0.0.0 | **2025.1.0.0** | 对应 Cloud 2025.1 线 |
| Spring Boot Admin | 3.5.8 | **4.1.1** | SBA 大版本必须跟随 Boot 大.次版本 |

### 1.2 中间件与第三方

| 组件 | 升级前 | 升级后 | 处置 |
|------|--------|--------|------|
| mica-auto(注解处理器) | 3.1.6 | **4.0.1** | 换坐标版本(总闸门,见 §4) |
| MyBatis-Plus | 3.5.14 | **3.5.17** | 换坐标 + **`IService`/`ServiceImpl` 包迁移**(见 §8) |
| mybatis-spring | 3.0.5 | **4.0.0** | MP 3.5.17 的 boot4 装配要求,Spring 7 线 |
| Web 容器 | Undertow | **Tomcat 11** | Undertow 在 Boot 4 下线(见 §6) |
| JSON 栈 | Jackson 2(Boot 默认) | **Jackson 2.21.4 + `spring-boot-jackson2` 兼容** | Boot 4 默认 Jackson 3,BladeX 锁回 2(见 §5) |
| springdoc | 2.8.13 | **3.0.3** | v3 线才支持 Boot 4 |
| Knife4j | 4.5.0 | **移除** | 无 Boot 4 版,改用 springdoc 原生 UI(见 §9) |
| Druid starter | `druid-spring-boot-3-starter` | **`druid-spring-boot-4-starter:1.2.28`** | 坐标改名 |
| hibernate-validator | 9.0.1.Final(显式 pin) | **9.1.0.Final**(BOM 托管) | 移除过时 pin |
| spring-boot-starter-aop | 存在 | **`spring-boot-starter-aspectj`** | Boot 4 移除了 aop starter |
| spring-retry | BOM 托管 | **2.0.13**(自管) | Boot 4 BOM 不再托管 |
| Apache HttpClient 4 | 非 BOM 托管 | **4.5.14**(自管) | Boot BOM 仅托管 HttpClient 5;HttpClient 4 自 Boot 3.1 起已不在 BOM,JustAuth 需要故自管 |

### 1.3 删除 / 新增的坐标

| 动作 | 坐标 | 原因 |
|------|------|------|
| 删除 | `org.springframework:spring-framework-bom`(import) | 由 `spring-boot-dependencies` 统一托管 SF 7,避免版本漂移 |
| 删除 | `com.github.xiaoymin:knife4j-dependencies`(import)+ `knife4j-openapi3-jakarta-spring-boot-starter` | 无 Boot 4 版 |
| 新增 | `org.springframework.boot:spring-boot-jackson2`(+ `jackson-databind`) | Jackson 2 兼容降级 |
| 新增 | `org.springframework.boot:spring-boot-cache` | Boot 4 缓存自动配置类拆分至独立模块 |
| 新增 | `org.springframework.boot:spring-boot-restclient` | Boot 4 把 `RestTemplateBuilder` 拆到独立模块 |
| 新增 | `com.baomidou:mybatis-plus-extension` | 本模块直接使用 `MybatisPlusInterceptor`/分页/租户等 extension 类,按"直接依赖显式声明"补齐(经 `mybatis-plus-spring` 仍可传递获得,显式声明更稳健) |
| 改名 | `spring-boot-starter-web` → `spring-boot-starter-webmvc` | Undertow 退场,默认 Tomcat |

---

## 2. 升级执行顺序(强约束)

blade-tool 是核心工程,下游 Cloud / Boot 通过 `blade-core-bom` 继承整套版本。**升级必须 blade-tool 先行**,内部顺序不可打乱:

```
① 改父 POM 版本矩阵与坐标(§3)
② 升 mica-auto 到 4.0.1 —— 注解处理器是编译期总闸门(§4)
③ 底层模块 import 迁包:launch / cloud / tool / redis / log 等(§5.3 映射表)
④ 运行期适配:Jackson 2 降级(§5)、Web 容器(§6)、全局异常(§7)
⑤ 第三方组件:MyBatis-Plus 包迁移(§8)、Swagger(§9)、Boot4 移除坐标(§10)
⑥ 全量 mvn clean install(务必带 clean,重新生成 spring.factories / *.imports)
```

> **为什么 mica-auto 是总闸门**:BladeX 全工程的 `@AutoConfiguration`、`EnvironmentPostProcessor`、`LauncherService` 都靠 mica-auto 在编译期生成 `META-INF/spring/*.imports` 与 `META-INF/spring.factories`。mica-auto 3.1.6 把 `EnvironmentPostProcessor` 写成旧键 `org.springframework.boot.env.EnvironmentPostProcessor`,4.0.1 才写 Boot 4 的新键 `org.springframework.boot.EnvironmentPostProcessor`。
> ⚠️【勘误 · 别误判失效方式】Boot **4.1.0 仍向后兼容**:`SpringFactoriesEnvironmentPostProcessorsFactory#getEnvironmentPostProcessors()` 会**同时加载新键与旧键**(旧键实例经内部 `Adapter` 包装后照常执行),旧接口 `org.springframework.boot.env.EnvironmentPostProcessor` 标 `@Deprecated(since=4.0.0, forRemoval=true)`、要到 **Boot 4.2.0** 才移除。因此"不升 mica-auto 会**运行期静默失效**"的说法对 4.1.0 **不成立**——真实两种结果是:① 源码仍用旧接口 + mica 3.1.6 写旧键 → 旧键仍被加载执行(不失效);② 源码已改新接口但 mica 仍 3.1.6 写旧键 → 类型不匹配,启动**显式抛 `IllegalArgumentException`**(大声失败,非静默)。升 mica-auto 到 4.0.1 仍是**必须动作**(对齐新键、面向 4.2.0),只是失效机制并非"静默"。

---

## 3. 父 POM 版本矩阵与坐标(`pom.xml`)

### 3.1 `<properties>` 版本属性

```xml
<!-- 改前 -->
<spring.version>6.2.17</spring.version>
<spring.boot.version>3.5.13</spring.boot.version>
<spring.boot.admin.version>3.5.8</spring.boot.admin.version>
<spring.cloud.version>2025.0.2</spring.cloud.version>
<alibaba.cloud.version>2025.0.0.0</alibaba.cloud.version>
<mica.auto.version>3.1.6</mica.auto.version>
<mybatis.spring.version>3.0.5</mybatis.spring.version>
<mybatis.plus.version>3.5.14</mybatis.plus.version>
<knife4j.version>4.5.0</knife4j.version>

<!-- 改后 -->
<!-- spring.version 属性整行删除:SF 7 由 spring-boot-dependencies 托管 -->
<spring.boot.version>4.1.0</spring.boot.version>
<spring.boot.admin.version>4.1.1</spring.boot.admin.version>
<spring.cloud.version>2025.1.2</spring.cloud.version>
<alibaba.cloud.version>2025.1.0.0</alibaba.cloud.version>
<mica.auto.version>4.0.1</mica.auto.version>
<mybatis.spring.version>4.0.0</mybatis.spring.version>
<mybatis.plus.version>3.5.17</mybatis.plus.version>
<!-- knife4j.version 属性整行删除 -->
<!-- 新增:Boot 4 BOM 不再托管的坐标自管版本 -->
<spring.retry.version>2.0.13</spring.retry.version>
<httpclient.version>4.5.14</httpclient.version>
```

### 3.2 `<dependencyManagement>` 变更

**删除** `spring-framework-bom` import(让 Boot BOM 统一管 SF 7):

```xml
<!-- 删除整块 -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-framework-bom</artifactId>
    <version>${spring.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

**删除** `knife4j-dependencies` import:

```xml
<!-- 删除整块 -->
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-dependencies</artifactId>
    <version>${knife4j.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

**坐标改名**(boot3 → boot4)+ **springdoc 升 3.0.3**:

```xml
mybatis-plus-spring-boot3-starter   → mybatis-plus-spring-boot4-starter
druid-spring-boot-3-starter         → druid-spring-boot-4-starter
springdoc-openapi-starter-webflux-ui: 2.8.13 → 3.0.3
springdoc-openapi-starter-webmvc-ui : 2.8.13 → 3.0.3
```

**移除过时 pin**:删掉 `hibernate-validator` 的 `<version>9.0.1.Final</version>` 显式管理条目(Boot 4.1 BOM 托管为 9.1.0.Final)。

**新增自管坐标**(Boot 4 BOM 不再托管):

```xml
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
    <version>${spring.retry.version}</version>
</dependency>
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>${httpclient.version}</version>
</dependency>
```

### 3.3 父 POM 公共 `<dependencies>`

```xml
<!-- 改前:Boot 4 已移除 aop starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>

<!-- 改后 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aspectj</artifactId>
</dependency>
```

---

## 4. mica-auto 注解处理器(总闸门)

**改动**:仅版本升级(§3.1 已改 `mica.auto.version` = 4.0.1)。mica-auto 4.0.1 的 `BootAutoType` 内置了 Boot 4 的新注册键,升级后重新编译即自动生成正确的 `spring.factories`。

**配套改一处 import**——`blade-starter-loadbalancer` 唯一的 `EnvironmentPostProcessor`:

文件:`blade-starter-loadbalancer/.../rule/GrayscaleEnvPostProcessor.java`

```java
// 改前
import org.springframework.boot.env.EnvironmentPostProcessor;
// 改后(Boot 4 该接口上移根包)
import org.springframework.boot.EnvironmentPostProcessor;
```

**验证**:`mvn clean install` 后检查生成物,键应为新的根包键:

```properties
# blade-starter-loadbalancer/target/classes/META-INF/spring.factories
org.springframework.boot.EnvironmentPostProcessor=\
  org.springblade.core.loadbalancer.rule.GrayscaleEnvPostProcessor
```

> 若这里仍是 `org.springframework.boot.env.EnvironmentPostProcessor`,说明 mica-auto 没升到 4.0.1。该键在 Boot 4.1.0 仍向后兼容加载执行(见 §2 勘误),但要到 **4.2.0** 旧接口移除后才彻底失效;应升 mica-auto 到 4.0.1 写入新键对齐,不要留旧键。

---

## 5. Boot 4 自动配置拆包 import 迁移 + Jackson 2 降级

### 5.1 背景:为什么会大面积编译断裂

Spring Boot 4.0 把巨型 `spring-boot-autoconfigure` 拆成按技术域划分的独立模块,包名从 `org.springframework.boot.autoconfigure.<tech>.*` 重排为 `org.springframework.boot.<tech>.autoconfigure.*`,部分类还顺带改名(如 `RedisAutoConfiguration` → `DataRedisAutoConfiguration`)。旧 import 直接编译不过。

### 5.2 【务必牢记】包名迁移映射表(已在 Boot 4.1.0 实测)

| 旧包 / 类 | 新包 / 类 | 所需 starter |
|-----------|-----------|--------------|
| `autoconfigure.web.ServerProperties` | `web.server.autoconfigure.ServerProperties` | spring-boot-web-server |
| `autoconfigure.web.servlet.WebMvcRegistrations` | `webmvc.autoconfigure.WebMvcRegistrations` | spring-boot-webmvc |
| `autoconfigure.web.servlet.WebMvcProperties` | `webmvc.autoconfigure.WebMvcProperties` | spring-boot-webmvc |
| `autoconfigure.web.servlet.error.BasicErrorController` | `webmvc.autoconfigure.error.BasicErrorController` | spring-boot-webmvc |
| `autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration` | `webmvc.autoconfigure.error.ErrorMvcAutoConfiguration` | spring-boot-webmvc |
| `web.servlet.error.DefaultErrorAttributes/ErrorAttributes/ErrorController` | `webmvc.error.*` | spring-boot-webmvc |
| `autoconfigure.cache.CacheProperties/CacheManagerCustomizer(s)` | `cache.autoconfigure.*` | spring-boot-cache |
| `autoconfigure.data.redis.RedisAutoConfiguration` | `data.redis.autoconfigure.**DataRedisAutoConfiguration**`(改名) | spring-boot-data-redis |
| `autoconfigure.jdbc.DataSourceProperties/DataSourceAutoConfiguration` | `jdbc.autoconfigure.*` | spring-boot-jdbc |
| `autoconfigure.jackson.JacksonAutoConfiguration` | `jackson.autoconfigure.JacksonAutoConfiguration` | spring-boot-jackson(Jackson 3) |
| `autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer` | `jackson2.autoconfigure.*`(deprecated-for-removal) | spring-boot-jackson2 |
| `web.context.WebServerInitializedEvent` | `web.server.context.WebServerInitializedEvent` | spring-boot-web-server |
| `web.client.RestTemplateBuilder` | `restclient.RestTemplateBuilder` | spring-boot-restclient |
| `boot.env.EnvironmentPostProcessor` | `boot.EnvironmentPostProcessor` | spring-boot |

> **保持不变、不要跟着乱改**:`autoconfigure.AutoConfiguration` 注解本体、`autoconfigure.AutoConfigureBefore/After`、`autoconfigure.condition.ConditionalOn*` / `SearchStrategy`、`autoconfigure.SpringBootApplication` / `EnableAutoConfiguration`、`autoconfigure.web.ErrorProperties`、`web.error.ErrorAttributeOptions`、`web.servlet.FilterRegistrationBean` / `ServletRegistrationBean` 均**未迁包**。

### 5.3 逐文件 import 迁移(blade-tool 实际受影响清单)

| 文件 | 改动 |
|------|------|
| `blade-core-launch/.../server/ServerInfo.java` | `ServerProperties` → `web.server.autoconfigure` |
| `blade-core-launch/.../StartEventListener.java` | `WebServerInitializedEvent` → `web.server.context` |
| `blade-core-cloud/.../version/BladeWebMvcRegistrations.java` | `WebMvcRegistrations` → `webmvc.autoconfigure` |
| `blade-core-cloud/.../version/VersionMappingAutoConfiguration.java` | `WebMvcRegistrations` → `webmvc.autoconfigure` |
| `blade-core-cloud/.../http/RestTemplateConfiguration.java` | `RestTemplateBuilder` → `restclient`;并把 `restTemplate()`/`lbRestTemplate()` 改为链式 `requestFactory(...).build()`(Boot 4 的 `RestTemplateBuilder` 不可变,旧的"先 `requestFactory(...)` 再单独 `build()`"两行式会丢弃携带 OkHttp3 工厂的 builder,顺带修掉该隐患) |
| `blade-core-tool/.../config/RedisConfiguration.java` | `RedisAutoConfiguration` → `DataRedisAutoConfiguration`(import + `@AutoConfigureBefore` 参数一起改) |
| `blade-starter-redis/.../config/RedisTemplateConfiguration.java` | 同上(import + `@AutoConfiguration(before=...)` 参数) |
| `blade-starter-redis/.../config/RedisCacheManagerConfig.java` | `CacheManagerCustomizer(s)` → `cache.autoconfigure` |
| `blade-starter-redis/.../config/BladeRedisCacheAutoConfiguration.java` | `CacheManagerCustomizers` / `CacheProperties` → `cache.autoconfigure` |
| `blade-starter-log/.../config/BladeErrorMvcAutoConfiguration.java` | 错误类拆两包 + `ServerProperties`(见 §7.2) |
| `blade-starter-log/.../error/BladeErrorController.java` | `BasicErrorController` → `webmvc.autoconfigure.error`;`ErrorAttributes` → `webmvc.error` |
| `blade-starter-log/.../error/BladeErrorAttributes.java` | `DefaultErrorAttributes` → `webmvc.error` |
| `blade-starter-transaction/.../annotation/SeataCloudApplication.java` | `DataSourceAutoConfiguration` → `jdbc.autoconfigure` |
| `blade-core-tool/.../config/JacksonConfiguration.java` | Jackson 2 降级重构(见 §5.5) |

**改名类(RedisAutoConfiguration → DataRedisAutoConfiguration)示例**——import 与注解参数必须一起改:

```java
// 改前
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
@AutoConfiguration(before = {RedisConfiguration.class, RedisAutoConfiguration.class})

// 改后
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
@AutoConfiguration(before = {RedisConfiguration.class, DataRedisAutoConfiguration.class})
```

### 5.4 拆包后各模块 POM 补齐 split-starter

新包里的类只有对应 starter 在 classpath 时才存在,以下为**硬性补齐**(不补则「找不到符号」):

| 模块 POM | 新增依赖 | 原因 |
|----------|----------|------|
| `blade-starter-redis/pom.xml` | `org.springframework.boot:spring-boot-cache` | 直接引用 `cache.autoconfigure.*`,而 `spring-boot-starter-data-redis` 不带 cache |
| `blade-core-cloud/pom.xml` | `org.springframework.boot:spring-boot-restclient` | `RestTemplateBuilder` 迁到此模块,webmvc starter 不含 |
| `blade-core-tool/pom.xml` | `spring-boot-jackson2` + `jackson-databind` | Jackson 2 降级(见 §5.5) |

版本一律由 BOM 托管,**不写 `<version>`**。

### 5.5 Jackson 2 降级与序列化管线重构

**背景**:Boot 4 默认底座是 Jackson 3(`tools.jackson.*`),BladeX 全链路(`JsonUtil`、`@BladeView` 视图过滤、大数转字符串、null 转空、读写分离转换器)重度依赖 Jackson 2(`com.fasterxml.jackson.*`)。方案是引 `spring-boot-jackson2` 兼容 starter 锁回 Jackson 2,并**不再自建主 `ObjectMapper` Bean 抢占框架 builder 装配管线**,改贡献 `Jackson2ObjectMapperBuilderCustomizer`。

**① `blade-core-tool/pom.xml` 新增依赖**:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-jackson2</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

**② `JacksonConfiguration.java` 重构**——从「自建主 ObjectMapper」改为「贡献 builder 定制器」:

```java
// 改前:自建主 ObjectMapper,抢占 builder 管线,且 import 的 JacksonAutoConfiguration 在 Boot4 已不存在
@AutoConfiguration(before = JacksonAutoConfiguration.class)
@ConditionalOnClass(ObjectMapper.class)
@EnableConfigurationProperties(BladeJacksonProperties.class)
public class JacksonConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = JsonUtil.getInstance();
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }
}

// 改后:贡献 Jackson2ObjectMapperBuilderCustomizer,由框架构建主 ObjectMapper
@AutoConfiguration
@ConditionalOnClass(ObjectMapper.class)
@EnableConfigurationProperties(BladeJacksonProperties.class)
@SuppressWarnings({"deprecation", "removal"})
public class JacksonConfiguration {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer bladeJacksonCustomizer() {
        return builder -> {
            builder.featuresToEnable(
                MapperFeature.DEFAULT_VIEW_INCLUSION,
                // JsonReadFeature 不被 Jackson2ObjectMapperBuilder 接受,改用等价的 JsonParser.Feature
                JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS,
                JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,
                JsonParser.Feature.ALLOW_SINGLE_QUOTES,
                DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            builder.featuresToDisable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                SerializationFeature.FAIL_ON_EMPTY_BEANS,
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            builder.locale(Locale.CHINA);
            builder.timeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
            builder.dateFormat(new SimpleDateFormat(DateUtil.PATTERN_DATETIME, Locale.CHINA));
            builder.modulesToInstall(new BladeJavaTimeModule());
            builder.findModulesViaServiceLoader(true);
            builder.annotationIntrospector(introspector -> new BladeViewAnnotationIntrospector());
        };
    }
}
```

**③ `MessageConfiguration.java`**——必须重写 `extendMessageConverters`(**不是** `configureMessageConverters`),在框架构建好的默认转换器列表上原地替换 JSON / String 转换器。

> ⚠️【务必看清,这是升级中最隐蔽的一个坑】Spring 7 的 `WebMvcConfigurationSupport.getMessageConverters()` 执行顺序是:先 `configureMessageConverters(list)` → 若 list 仍为空才 `addDefaultHttpMessageConverters(list)`(这一步才注册 ByteArray/String/Resource/ResourceRegion/Form 以及 JSON 转换器)→ 最后 `extendMessageConverters(list)`。
> 若重写 `configureMessageConverters` 并向 list 添加任何转换器,list 变为非空,`addDefaultHttpMessageConverters` 会被**整段跳过**,MVC 最终只剩下你加入的这一个转换器——文件下载、`byte[]`/图片/PDF、纯文本、表单请求体全部 406/`HttpMessageNotWritableException` 失败。**编译与纯 JSON 接口都正常,极难在冒烟测试中发现。**
> 正解是重写 `extendMessageConverters`:它在默认列表构建完成之后回调,拿到的是**完整**的默认转换器,再原地替换 JSON(因 §5.5⑤ 已强制 `preferred-json-mapper=jackson2`,默认 JSON 转换器为 `MappingJackson2HttpMessageConverter`,属 `AbstractJackson2HttpMessageConverter`)与 String 转换器即可,其余转换器原样保留。

```java
@Override
@SuppressWarnings("removal")
public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    MappingApiJackson2HttpMessageConverter bladeJson =
        new MappingApiJackson2HttpMessageConverter(objectMapper, properties);
    boolean jsonReplaced = false;
    ListIterator<HttpMessageConverter<?>> iterator = converters.listIterator();
    while (iterator.hasNext()) {
        HttpMessageConverter<?> converter = iterator.next();
        if (converter instanceof AbstractJackson2HttpMessageConverter) {
            iterator.set(bladeJson);
            jsonReplaced = true;
        } else if (converter instanceof StringHttpMessageConverter) {
            iterator.set(new StringHttpMessageConverter(Charsets.UTF_8));
        }
    }
    // 兜底:未命中默认 JSON 转换器时插到队首,确保 blade 转换器优先生效
    if (!jsonReplaced) {
        converters.add(0, bladeJson);
    }
}
```

**④ 抑制弃用 / 待删除告警**:Boot4 下 Jackson 2 的 Spring 集成类(`AbstractJackson2HttpMessageConverter`、`MappingJackson2HttpMessageConverter`、`MappingJacksonValue`、`Jackson2ObjectMapperBuilderCustomizer` 等)被标记为 deprecated-for-removal,部分 Jackson 2 `ObjectMapper` mutator 也被弃用,需逐类补 `@SuppressWarnings`。作用域按就近原则——弃用点在类声明(如 `extends` 待删除父类)或贯穿整类的放**类级**,只落在单个方法内的放**方法级**;`deprecation`(普通弃用)与 `removal`(待删除弃用)是不同 lint 类别,按该类实际触发的类别精确填写、不冗余:

| 类 / 方法 | 作用域 | 注解 | 触发点 |
|---|---|---|---|
| `jackson/AbstractReadWriteJackson2HttpMessageConverter` | 类级 | `@SuppressWarnings("removal")` | `extends AbstractJackson2HttpMessageConverter`(待删除),类声明即触发 |
| `jackson/MappingApiJackson2HttpMessageConverter` | 类级 | `@SuppressWarnings("deprecation")` | 覆盖 `initWriteObjectMapper` 里 Jackson 2 可变式定制 API(`setSerializerFactory` / `getSerializerProvider().setNullValueSerializer`);⚠️ **勘误**:在锁定的 jackson-databind 2.21.4 中这两个方法**尚未**标 `@Deprecated`(经 javap 核实),故此处属**前向/防御性**抑制,本类无 removal |
| `jackson/BladeViewResponseAdvice` | 类级 | `@SuppressWarnings("removal")` | 用 `MappingJacksonValue`(待删除)包装 `@BladeView` 视图响应 |
| `config/JacksonConfiguration` | 类级 | `@SuppressWarnings({"deprecation", "removal"})` | `JsonParser.Feature.*`(弃用)+ `Jackson2ObjectMapperBuilderCustomizer`(待删除) |
| `config/MessageConfiguration#extendMessageConverters` | 方法级 | `@SuppressWarnings("removal")` | 方法内引用 `AbstractJackson2HttpMessageConverter`(待删除) |
| `blade-core-cloud RestTemplateConfiguration#configMessageConverters` | 方法级 | `@SuppressWarnings("removal")` | 方法内引用 `MappingJackson2HttpMessageConverter`(待删除) |

> 与 Jackson 无关的 `org.springframework.lang.Nullable/NonNull` 在 Spring 7 也被弃用(官方推 JSpecify),那是一处独立、面广的迁移,不在本表范围,别混入 Jackson 的 `@SuppressWarnings`。

**⑤ 【关键】强制 preferred-json-mapper = jackson2**——新增一个 `LauncherService`,否则 Boot 4 默认注册 Jackson 3 转换器,§5.5③ 的 `instanceof AbstractJackson2HttpMessageConverter` 命不中,Blade 的 JSON 处理静默失效:

新增 `blade-core-tool/.../config/JacksonLauncherServiceImpl.java`:

```java
@AutoService(LauncherService.class)
public class JacksonLauncherServiceImpl implements LauncherService {
    @Override
    public void launcher(SpringApplicationBuilder builder, String appName, String profile) {
        Properties props = System.getProperties();
        // 命令式转换器与反应式编解码器分别指定首选实现，两处同锁 Jackson 2，servlet / webflux 两侧序列化一致
        props.setProperty("spring.http.converters.preferred-json-mapper", "jackson2");
        props.setProperty("spring.http.codecs.preferred-json-mapper", "jackson2");
    }
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
```

### 5.6 Redis pub/sub 容器与 Boot 4.1 新增 `@RedisListener` 自动配置的竞争

**背景**:Boot 4.1 的 `spring-boot-data-redis` 新增了注解驱动监听自动配置 `DataRedisAnnotationDrivenConfiguration`(由 `DataRedisAutoConfiguration` `@Import`,条件 `@ConditionalOnClass({org.springframework.data.redis.annotation.EnableRedisListeners, org.springframework.messaging.Message})`),用于支撑新的 `@RedisListener` 注解。它会注册一个 `@Bean(name = "redisMessageListenerContainer")` + `@ConditionalOnMissingBean` 的默认容器。

BladeX 自有的 Redis 发布订阅(`@RPubSubListener` + `RPubSubListenerDetector`)在 `blade-starter-redis` 的 `RedisPubSubConfiguration` 里**同样**注册一个同名 `redisMessageListenerContainer`(也是 `@ConditionalOnMissingBean`)。`spring-messaging`(经 spring-data-redis 传递)与 `EnableRedisListeners` 在 BladeX classpath 上均存在,故 Boot 这个自动配置**会激活**,与 BladeX 的容器**同名、同条件**竞争。

**行为分析**:两者都 `@ConditionalOnMissingBean` → 不会重复 Bean 报错,功能上任一容器都能用(BladeX 的 detector 只是 `getBean(RedisMessageListenerContainer.class)` 抓容器再挂监听);但**哪个容器胜出取决于自动配置处理顺序**——`RedisPubSubConfiguration` 原先未声明任何 `@AutoConfigureBefore/After`,顺序不确定,存在隐性非确定性。

**改动**(`blade-starter-redis/.../config/RedisPubSubConfiguration.java`)——让 BladeX 容器确定性胜出:

```java
// 改前
@AutoConfiguration
public class RedisPubSubConfiguration {

// 改后(新增 import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration)
@AutoConfiguration(before = DataRedisAutoConfiguration.class)
public class RedisPubSubConfiguration {
```

> 写法与同模块 `RedisTemplateConfiguration`(§5.3)一致:排在 `DataRedisAutoConfiguration` 之前,BladeX 的 `redisMessageListenerContainer` 先注册,Boot 的 `@ConditionalOnMissingBean` 随即退让;容器所需的 `RedisConnectionFactory` 由 bean 方法入参在实例化时解析,不受配置类排序影响。
> **与 §5.3 两处 Redis 改动的区别**:§5.3 里 `RedisConfiguration` / `RedisTemplateConfiguration` 调 `before=` 是因**类改名**(`RedisAutoConfiguration`→`DataRedisAutoConfiguration`)顺带修正既有 `before` 列表;本处 `RedisPubSubConfiguration` 原本**无** `before`、属**新增**,根因是 Boot 4.1 新引入的 `@RedisListener` 自动配置,是独立的适配点。
> **运行期验证**:启动后确认 `@RPubSubListener` 收发一次正常即可。

---

## 6. Web 容器:Undertow → Tomcat

**背景**:Spring Boot 4 把基线抬到 Servlet 6.1 / Jakarta EE 11(Tomcat 11、Jetty 12.1),Undertow 当时无对应 GA,Spring 遂移除了 Undertow 的 starter 与自动配置——`spring-boot-starter-undertow` 已无 4.x 正式版(仓库仅剩从未转正的 `4.0.0-M1` 里程碑),回归默认 Tomcat 11。

**① `blade-core-launch/pom.xml`**——`web` + `undertow` 两个依赖合并为一个 `webmvc`:

```xml
<!-- 改前 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-undertow</artifactId>
</dependency>

<!-- 改后(webmvc 默认带 Tomcat) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>
```

**② `blade-core-boot/src/main/resources/bootstrap.yml`**——`server.undertow.*` 换算为 `server.tomcat.*`:

```yaml
# 改前
server:
  undertow:
    buffer-size: 1024
    direct-buffers: true
    threads:
      io: 16
      worker: 400

# 改后(Tomcat 无独立 IO 线程 / buffer 概念:worker→threads.max、io→threads.min-spare,buffer 项废弃)
server:
  tomcat:
    # 线程配置
    threads:
      # 最大工作线程数(可同时处理请求的线程上限)
      max: 400
      # 最小空闲工作线程数(保持存活的常驻线程数)
      min-spare: 16
```

**③(可选)`logback_dev.xml`**——Undertow 日志器换 Tomcat 命名空间:`io.undertow`→`org.apache.catalina`、`org.xnio.nio`→`org.apache.coyote`。

**④ `blade-core-boot/src/main/resources/bootstrap.yml`——Servlet 编码配置改前缀 `server.servlet.encoding` → `spring.servlet.encoding`**

Spring Boot 4.0 把 Servlet 编码属性从 `server.servlet.encoding.*` 迁到新的 `spring-boot-servlet` 模块下的 `spring.servlet.encoding.*`(绑定类 `org.springframework.boot.servlet.autoconfigure.ServletEncodingProperties`)。旧前缀在 Boot 4.1.0 的配置元数据里被标记为 `level: error`(非 warning)、`since: 4.0.0`,属**硬迁移**:旧键不再生效,只保留元数据供 IDE / 校验工具报错提示。判定依据是 Boot 自身随包发布的 `META-INF/spring-configuration-metadata.json`,比任何博客都权威。

```yaml
# 改前(server 命名空间下,Boot 4 起报错)
server:
  servlet:
    encoding:
      charset: UTF-8
      force: true

# 改后(并入既有 spring.servlet 块,与 multipart 平级)
spring:
  servlet:
    multipart:
      max-file-size: 256MB
      max-request-size: 1024MB
    # 编码配置
    encoding:
      charset: UTF-8
      force: true
```

> 键名一一对应,仅前缀 `server` → `spring`:`charset` / `force` / `enabled` / `force-request` / `force-response` 均照迁。`force: true` 是 BladeX 的刻意设置(Boot 默认 `force=false`),强制请求与响应都用 UTF-8,避免中文响应乱码,故保留而非删除。YAML 里 `spring:` 顶层键唯一,须把 `encoding` 并入已存在的 `spring.servlet` 块,不能新起一个 `spring:` 造成重复键。

> **下游连锁**:blade-core-launch 是全工程唯一声明 Web 容器 starter 的地方,下游所有 app(尤其未自带容器 starter 的 BladeX-Boot)会**静默从 Undertow 切到 Tomcat**,须回归测试。

---

## 7. 全局异常:SF 7 校验异常体系适配

**背景**:方法级 `@Validated` 校验自 Spring 6.1 起改抛 `HandlerMethodValidationException`(Boot 3.5 的 Spring 6.2 即具备,本次补齐其全局处理);Boot 4 另有错误处理链拆包、静态资源 404 的 `NoResourceFoundException`,以及 `ErrorProperties` 来源从 `ServerProperties` 迁到 `WebProperties`。

### 7.1 `blade-starter-log/.../error/BladeRestExceptionTranslator.java` 新增/调整处理器

```java
// 去掉对 Hibernate 内部类 PathImpl 的依赖,改用标准 jakarta.validation.Path
// 改前
import org.hibernate.validator.internal.engine.path.PathImpl;
String path = ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();
// 改后
import jakarta.validation.Path;
String path = "";
for (Path.Node node : violation.getPropertyPath()) {
    path = node.getName();
}

// 新增 SF7 方法级校验异常(注意:用 getParameterValidationResults(),SF7 无 getAllValidationResults())
@ExceptionHandler(HandlerMethodValidationException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public R handleError(HandlerMethodValidationException e) {
    String message = e.getParameterValidationResults().stream()
        .flatMap(result -> result.getResolvableErrors().stream()
            .map(error -> String.format("%s:%s", result.getMethodParameter().getParameterName(), error.getDefaultMessage())))
        .findFirst()
        .orElse(ResultCode.PARAM_VALID_ERROR.getMessage());
    return R.fail(ResultCode.PARAM_VALID_ERROR, message);
}

// 新增 静态资源 404(Spring 6.1+ 起 ResourceHttpRequestHandler 对缺失静态资源抛此异常,与已移除的 throw-exception-if-no-handler-found 无关)
@ExceptionHandler(NoResourceFoundException.class)
@ResponseStatus(HttpStatus.NOT_FOUND)
public R handleError(NoResourceFoundException e) {
    log.warn("404资源不存在:{}", e.getResourcePath());
    return R.fail(ResultCode.NOT_FOUND);   // 只回 404、不把资源路径回显给客户端(避免信息泄露)
}

// 同时把既有 NoHandlerFoundException 处理器对齐:log.error→log.warn、去掉 e.getMessage() 返回参(只回 ResultCode.NOT_FOUND)

// 新增 上传超限 413
@ExceptionHandler(MaxUploadSizeExceededException.class)
@ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
public R handleError(MaxUploadSizeExceededException e) {
    return R.fail(ResultCode.FAILURE, "上传文件大小超过系统限制");
}
```

新增 import:`org.springframework.web.method.annotation.HandlerMethodValidationException`、`org.springframework.web.multipart.MaxUploadSizeExceededException`、`org.springframework.web.servlet.resource.NoResourceFoundException`。

### 7.2 `blade-starter-log/.../config/BladeErrorMvcAutoConfiguration.java`——`ServerProperties.getError()` 已移除

Boot 4 把 `ErrorProperties` 的来源从 `ServerProperties` 挪到了 `WebProperties`:

```java
// 改前
import org.springframework.boot.autoconfigure.web.ServerProperties;
private final ServerProperties serverProperties;
return new BladeErrorController(errorAttributes, serverProperties.getError());

// 改后
import org.springframework.boot.autoconfigure.web.WebProperties;
@EnableConfigurationProperties({BladeLogProperties.class, WebProperties.class})
private final WebProperties webProperties;
return new BladeErrorController(errorAttributes, webProperties.getError());
```

---

## 8. MyBatis-Plus 3.5.17 包迁移(易漏)

**背景**:MyBatis-Plus 3.5.17 起把 `IService` / `ServiceImpl` 从 `com.baomidou.mybatisplus.extension.service.*` 迁到了 **`com.baomidou.mybatisplus.spring.service.*`**(位于 `mybatis-plus-spring` 模块)。

**① `blade-starter-mybatis/pom.xml` 显式声明 `mybatis-plus-extension`**(本模块直接使用 `MybatisPlusInterceptor`、分页/租户插件等 extension 类;虽经 `mybatis-plus-spring` 仍可传递获得,按"直接使用即显式声明"补齐更稳健):

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-extension</artifactId>
</dependency>
```

**② 源码 import 迁移**(blade-tool 内 5 处,业务工程凡用到 `IService`/`ServiceImpl` 同样要改):

```java
// 改前
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
// 改后
import com.baomidou.mybatisplus.spring.service.IService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
```

受影响文件:`BaseService`、`BaseServiceImpl`(blade-starter-mybatis)、`TenantGuard`(blade-starter-tenant)、`IReportFileService`、`ReportFileServiceImpl`(blade-starter-report)。

> `com.baomidou.mybatisplus.extension.plugins.*`(拦截器、分页、租户)**未迁包**,不要改。

---

## 9. Swagger:移除 Knife4j,改用 springdoc 3.0.3 原生 UI

**背景**:Knife4j 最新 4.5.0 仅绑定 springdoc 2.x / Boot 2.2~3.x,无 Boot 4 版且已停更,Boot 4 下不可用。

**① `blade-starter-swagger/pom.xml`**:

```xml
<!-- 改前 -->
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
</dependency>
<!-- 改后 -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
</dependency>
```

**② 删除** vendored 的 knife4j 影子类 `blade-starter-swagger/src/main/java/com/github/xiaoymin/knife4j/spring/extension/Knife4jOpenApiCustomizer.java`(整个 `com/github/xiaoymin` 目录树)。

**③ `SwaggerLauncherServiceImpl.java`**:删掉 `knife4j.enable` / `knife4j.production` 两处属性写入(prod/dev 两个分支都删),`springdoc.*` 开关已完整覆盖启停。同时清掉两处历史遗留:
- `springdoc.api-usage.enabled`——springdoc 并无此属性(宽松绑定下静默无效),两个分支都删。
- dev 分支的 `spring.mvc.pathmatch.matching-strategy=ANT_PATH_MATCHER`——这是 springdoc 2 / Boot 2.6 时代的旧 workaround,springdoc 3 + Boot4 原生走 `PathPatternParser`,且它只在非 prod 设置会导致 dev 用 AntPathMatcher、prod 用 PathPatternParser,两套路径匹配语义不一致(尾斜杠、`{*path}` 通配、矩阵变量),必须删除让各环境统一。

**④ `SwaggerWebConfiguration.java`**:删掉 `doc.html` 与 `/webjars/**` 资源映射(springdoc 自托管),连带去掉 `implements WebMvcConfigurer` 与 `addResourceHandlers` 方法;**保留** `@AutoConfiguration` + `@BladePropertySource("classpath:/blade-swagger.yml")`(它负责加载默认配置)。

**⑤ `blade-swagger.yml`**:删掉整段 `knife4j:` 配置;`springdoc.swagger-ui` 的 `tags-sorter` / `operations-sorter` **均不设**——接口与分组顺序改由 BladeX 自研的 `@ApiOrder` 控制(见 §9⑧),设 `alpha` / `method` 会在前端覆盖自定义顺序。

> `SwaggerAutoConfiguration.java` 的 springdoc 依赖(`io.swagger.v3.oas.models.*` 与 `org.springdoc.core.*`)在 3.0.3 下包路径一致、**无需迁移改动**;但⚠️ 它并非"零改动"——本次它正是 `@ApiOrder` 排序引擎的实现载体(见 §9⑧):新增 `GlobalOperationCustomizer`(写 `x-order`)、`GlobalOpenApiCustomizer`(按序重排 paths/tags)及 ASM 字节码行号读取等逻辑,是本节最大的一处代码新增。
> **注意**:springdoc 3.0.3 内部经 swagger-core 仍依赖 Jackson 2,须配合 §5 的 `spring-boot-jackson2` 在 classpath,否则 Swagger-UI 启动报 Jackson 版本错。

**⑥ 安全放行(springdoc 路径)**:springdoc UI 与规格端点需匿名可达。框架默认放行清单 `blade-core-secure` 的 `SecureRegistry.defaultExcludePatterns` 已含 `/v3/api-docs/**`、`/swagger-ui/**`、`/swagger-ui.html`(`/swagger-ui/**` 为 UI 静态资源,`/swagger-ui.html` 为跳转入口),下游 servlet 应用继承即可。若应用另有自建放行清单(Boot 单体的 `BladeConfiguration`、Cloud 网关的 `AuthProvider`),须同步补齐这三项,并去掉 `/doc.html`、`/swagger-resources/**` 等 knife4j / springfox 旧路径。

**⑦ 代码生成器模板**:`blade-starter-develop/src/main/resources/templates/controller.java.vm` 里的 `@ApiOperationSupport` import 与注解一并删除——它是 `.vm` 资源,下游按 `*.java` 批量清理的脚本覆盖不到,须单独处理,否则该生成器产出的 Controller 引用已移除的 knife4j 包、编译不过(排序改用 §9⑧ 的 `@ApiOrder`)。删干净后**还须在模板 `@Tag` 上方补一行无参 `@ApiOrder` + 其 import**(`org.springblade.core.swagger.annotation.ApiOrder`),让生成的 Controller 与手写控制器一致带排序(见下「落地约定」);下游 Boot(`src/main/resources/templates/`)、Cloud(`blade-ops/blade-develop/src/main/resources/templates/`)的同名模板副本一并补。`src/test/resources/templates/` 下的旧扁平路径 `.vm` 副本自模板重组为 `api/` 子目录后不再被加载,应直接删除,test 侧仅保留 `code.properties` 作为数据源配置覆盖点。

**⑧ 接口排序:`@ApiOrder`(替代 knife4j 的 `@ApiOperationSupport(order = n)`)**

springdoc / OpenAPI 3 无接口排序原生注解,blade-starter-swagger 新增 `org.springblade.core.swagger.annotation.ApiOrder`:

- **方法上** `@ApiOrder(n)`:该接口显式序号,值越小越靠前。
- **类上** `@ApiOrder(n)`:`n` 为该控制器分组(`@Tag`)的序号,类内接口按源码声明顺序排列;个别方法可再标方法级注解精确覆盖。
- 未标注:回落路径 / tag 名字母序。

实现在 `SwaggerAutoConfiguration`:`GlobalOperationCustomizer` 读注解写入 `x-order` 扩展(类级用字节码行号取声明序,反射不保证方法顺序)并记录「tag → 类级序号」;`GlobalOpenApiCustomizer` 按 `x-order` 重排 `paths`、按类级序号重排顶层 `tags`。swagger-ui 仅在两个 sorter 未设时保留 spec 顺序(§9⑤),并须保持 `springdoc.writer-with-order-by-keys` 默认 `false`。

**落地约定(本次统一)**:三工程**每个带 `@Tag` 的控制器**均在 `@Tag` 上方补一行**无参 `@ApiOrder`**(值取默认 `Integer.MAX_VALUE`),方法体只保留 `@Operation`——效果是分组内接口按**源码声明顺序**展示、各 `@Tag` 分组之间因类级序号相等而回落 tag 名字母序;仅当个别接口要打破源码顺序时,才在该方法上补 `@ApiOrder(n)` 精确覆盖。删 knife4j 排序注解(§3.1 / §4.2)后**只删不补**会让接口在 springdoc UI 下按字母序、丢失原有顺序,故「删旧 + 补 `@ApiOrder`」是同一件事的两半。代码生成器模板 `controller.java.vm`(blade-tool `blade-starter-develop` 源模板 + 下游 Boot/Cloud `blade-develop` 的 main 副本,共 3 份)同步在 `@Tag` 上方生成 `@ApiOrder`,新生成的 Controller 开箱即带排序。

> 提示:类级 `@ApiOrder(n)` 的 `value` 用作 `@Tag` 分组之间的排序序号(`SwaggerAutoConfiguration#recordTagOrder` 写入、`GlobalOpenApiCustomizer` 据此重排顶层 `tags`);分组内各接口不受此值影响,按源码声明顺序排列。本次全量采用**无参**形式,`value` 恒为默认值、各分组落 tag 名字母序;如需固定分组顺序,给对应控制器标 `@ApiOrder(n)` 即可。

---

## 10. Boot 4 BOM 移除的坐标(自管版本)

| 坐标 | Boot 4 状况 | 处置 |
|------|------------|------|
| `spring-boot-starter-aop` | 已移除 | 换 `spring-boot-starter-aspectj`(§3.3) |
| `org.springframework.retry:spring-retry` | BOM 不再托管 | 自管 2.0.13(§3.2),`RetryConfiguration` 无源码改动 |
| `org.apache.httpcomponents:httpclient`(HttpClient 4) | 非 BOM 托管(Boot BOM 仅托管 HttpClient 5) | 自管 4.5.14(§3.2),`blade-starter-social` 的 JustAuth 依赖它 |

---

## 11. Druid / 数据源坐标

`blade-starter-mybatis/pom.xml` 与 `blade-core-boot/pom.xml`:

```
druid-spring-boot-3-starter → druid-spring-boot-4-starter
```

`blade-core-boot/pom.xml` 另需:`mybatis-plus-spring-boot3-starter → mybatis-plus-spring-boot4-starter`。

> Boot 4 把 JDBC 从 web starter 拆出;本工程用数据源的模块通过 `druid-spring-boot-4-starter` 已传递 `spring-boot-jdbc`,无需单独声明。

### 11.1 Nacos 客户端(Cloud 微服务)

`blade-core-cloud/pom.xml` 重新声明的 `com.alibaba.nacos:nacos-client` 上排除 `com.alibaba.nacos:logback-adapter`,并把 `alibaba.nacos.version` 升到 `3.2.2`。Boot4 托管的 logback 版本较新,Nacos 自带的 logback 适配器与之不匹配,不排除时启动期存在绑定失败风险:

```xml
<dependency>
    <groupId>com.alibaba.nacos</groupId>
    <artifactId>nacos-client</artifactId>
    <exclusions>
        <exclusion>
            <groupId>com.alibaba.nacos</groupId>
            <artifactId>logback-adapter</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

---

## 12. 编译验证

```bash
# 全量 clean install(务必带 clean,重新生成 spring.factories / *.imports)
mvn clean install -DskipTests -Dmaven.test.skip=true
```

通过标准:24 个模块全部 `SUCCESS`。验证生成物:

```bash
# 1. EnvironmentPostProcessor 注册键为新的根包键
cat blade-starter-loadbalancer/target/classes/META-INF/spring.factories
# 期望:org.springframework.boot.EnvironmentPostProcessor=...GrayscaleEnvPostProcessor

# 2. JacksonLauncherServiceImpl 已注册
cat blade-core-tool/target/classes/META-INF/services/org.springblade.core.launch.service.LauncherService
```

---

## 13. 运行期验证要点(交由真实环境执行)

编译通过不等于运行正确,升级后重点验证:

1. **JSON 序列化**:`@BladeView` 视图过滤、大数转字符串、null 转空、读写分离转换器是否生效(确认活跃转换器为 `MappingApiJackson2HttpMessageConverter`)。
2. **MyBatis-Plus + Druid** 在 Boot 4 下的自动装配与 SQL 执行。
3. **Web 容器** 已切 Tomcat,压测吞吐特征与 Undertow 时代不同。
4. **spring-retry 2.0.13 / Seata / springdoc UI** 在 Spring 7 运行期兼容。
5. **UReport 报表模块**(`blade-starter-report`)编译通过,运行期需验证。

---

## 14. 下游工程连锁影响(重要)

blade-tool 发版后,下游 SpringBlade Cloud / Boot 通过 `blade-core-bom` 自动获得整套 Boot 4 版本管理。但下游仍需自行处理:

- **MyBatis-Plus 包迁移(⚠️ 静默运行期风险)**:MyBatis-Plus 3.5.17 把 `IService`/`ServiceImpl` 从 `com.baomidou.mybatisplus.extension.service.*` 迁到了 `com.baomidou.mybatisplus.spring.service.*`。业务代码里凡 `import com.baomidou.mybatisplus.extension.service.IService/ServiceImpl` 都要改到 `spring.service`(§8)。**升级前务必全量 grep 下游二开工程**——任何仍编译在旧包上的二开模块或第三方库会在运行期抛 `NoClassDefFoundError`;若二开生态面广、一时改不完,可临时把 MyBatis-Plus 钉在 `3.5.16`(该版本仍保留旧 `extension.service` 包),推迟这次迁移。
- **Web 容器切 Tomcat**:静默继承,须回归。下游工程**自己的** yml 里若有 `server.undertow.*`(如 `application.yml`、`bootstrap.yml`、Nacos 配置模板)也要一并换成 `server.tomcat.*`,否则那段是被忽略的死配置、原线程调优静默失效;`logback` 里的 `io.undertow`/`org.xnio` 日志器可换成 `org.apache.catalina`/`org.apache.coyote`。
- **Knife4j 彻底移除**(无 Boot4 版):下游须一并删干净——`knife4j-openapi3-ui` / `knife4j-gateway-spring-boot-starter` 依赖改 springdoc 3.0.3、`doc.html` 入口去除、yml 里的 `knife4j:` 配置整段删除、业务控制器里的 `@ApiOperationSupport` / `@ApiSort` 注解与 import 全部删除(不保留 `knife4j-annotations` 兼容包)。接口排序改用 BladeX 的 `@ApiOrder`(见 §9⑧)。
- **各自直接依赖**:如 `dynamic-datasource-spring-boot3-starter`(Cloud)需换 boot4 版。

(下游 Cloud / Boot 的详细升级步骤见同目录另两份文档。)

---

## 15. Spring 7 空值注解迁移(JSpecify)

> 本次升级已执行该迁移(采用下文**方案一 · 机械替换**)。本节记录背景、映射、落地范围与验证结果。

### 背景
Spring Framework 7 把自带的空值注解 `org.springframework.lang.@Nullable / @NonNull / @NonNullApi / @NonNullFields` 全部标记为 `@Deprecated(since = "7.0")`(**仅弃用,未标 forRemoval**),官方转向业界标准 **JSpecify**(`org.jspecify.annotations.*`)。

**官方依据**(可直接查证):
- Spring 随源码发布的 `org.springframework.lang.Nullable` javadoc 明写 `@deprecated use {@link org.jspecify.annotations.Nullable} instead`,类上带 `@Deprecated(since = "7.0")`;且该包 `package-info.java` 自身已改用 `@org.jspecify.annotations.NullMarked`(即 Spring 官方源码内部也已切到 JSpecify)。
- Spring Framework 参考文档 · Null-safety:https://docs.spring.io/spring-framework/reference/core/null-safety.html
- Spring 官方博客《Null-safe applications with Spring Boot 4》(2025-11-12):https://spring.io/blog/2025/11/12/null-safe-applications-with-spring-boot-4/
- 立项 issue《Migrate to JSpecify annotations for nullability constraints》#28797:https://github.com/spring-projects/spring-framework/issues/28797

官方在上述博客中明确**建议整个生态**(含 Reactor、Micrometer 及依赖 Spring 的各库)统一采用 JSpecify——这正是本次把 Reactor / Jakarta / JetBrains 等其他库的空值注解也一并归一到 JSpecify 的依据。

### 依赖
JSpecify **1.0.0** 已由 Boot 4.1 BOM 托管,并经 `spring-core` 以 compile 作用域传递到 classpath——**迁移无需新增任何依赖**。

### 完成情况
本次分两步,把三工程**所有**空值注解统一到 JSpecify:

**第一步 · Spring 弃用注解迁移**(`org.springframework.lang.*` → `org.jspecify.annotations.*`)
- **blade-tool**:60 个文件、63 处 import(56 `@Nullable` + 7 `@NonNull`);无包级 `@NonNullApi/@NonNullFields`,故无需 `@NullMarked`。
- **Cloud**:1 个文件(`blade-gateway/.../filter/GatewayFilter.java`)。
- **Boot**:无 `org.springframework.lang.*` 引用,无需改动。
- 落地方式:import 就地替换,最小 diff;14 处数组 / 可变参数入参 + 4 处数组返回值按 TYPE_USE 校正(见下)。

**第二步 · 其他库空值注解归一**(统一到 JSpecify,消除多套注解并存)
| 文件 | 原注解 | 改后 |
|---|---|---|
| `blade-starter-i18n`:`I18nLocaleResolver` / `I18nAutoConfiguration` | `jakarta.annotation.@Nonnull`(4 处) | `org.jspecify.annotations.@NonNull` |
| `blade-core-cloud`:`OkHttp3ClientHttpRequestFactory` | `org.jetbrains.annotations.@NotNull`(1 处) | `org.jspecify.annotations.@NonNull` |
| Cloud `blade-gateway`:`JwtCrypto` | `reactor.util.annotation.@Nullable`(3 处) | `org.jspecify.annotations.@Nullable` |

> 简单名变化需留意:`@Nonnull` / `@NotNull`(旧库表示"非空")在 JSpecify 中统一为 `@NonNull`,故这几处除换 import 外还需改注解名;`@Nullable` 简单名不变、仅换 import。这几处均为标量参数 / 返回值(无数组),不涉及 TYPE_USE 位移;import 均放到分组内的正确字母序位置。归一后三工程只剩 `org.jspecify.annotations.*` 一套空值注解——共 **68 处 import(57 `@Nullable` + 11 `@NonNull`)**。

### 迁移映射(Spring 弃用注解)
| 旧(`org.springframework.lang`) | 新(`org.jspecify.annotations`) |
|---|---|
| `@Nullable` | `@Nullable` |
| `@NonNull` | `@NonNull`(或在 `@NullMarked` 作用域下直接删除) |
| `@NonNullApi` / `@NonNullFields`(package-info) | `@NullMarked`(包 / 模块级) |

### ⚠️ 关键差异:声明级 → 类型用法级(TYPE_USE)
JSpecify 的注解是 **`@Target(TYPE_USE)`**,Spring 的是 `@Target({METHOD, PARAMETER, FIELD})` 声明级。类型用法注解必须**紧贴类型**,以下位置换 import 后需手工校正:
- **返回值**:`public @Nullable String get()`——注解须紧贴返回类型,不能放在 `public` 等修饰符之前。
- **数组**:`String @Nullable []`(数组本身可空)与 `@Nullable String[]`(元素可空)语义不同,TYPE_USE 能区分、声明级不能。
- **泛型**:`List<@Nullable String>` 只有 TYPE_USE 能标注类型参数。

简单的字段 / 参数 / 普通返回类型可直接换 import(视觉位置不变);上述三类位置需调整。换完**必须编译核验**,并把 IDE 的空值分析规则切到 JSpecify。

**本次实际处理**:14 处数组 / 可变参数入参(全在 `blade-core-tool` 的 `JsonUtil` / `Func` / `CollectionUtil` / `DigestUtil` / `StringUtil` 共 13 个方法,其中 `DigestUtil.slowEquals` 的两个 `byte[]` 入参各计一处),以及 4 处 `byte[]` 数组返回值(`AesUtil.decryptFormHex` / `decryptFormBase64`、`HexUtil.decode`、`RedisKeySerializer.serialize`),语义均为"该数组 / 可变参数 / 返回值可为 null",故按 TYPE_USE 规则把注解放在数组位:

```java
// 换 import 后仍需手工校正的数组位(否则语义从"数组可空"漂移成"元素可空")
public static <T> T readValue(byte @Nullable [] content, Class<T> valueType)   // 入参:由 @Nullable byte[] content 调整
public static String format(@Nullable String message, Object @Nullable ... arguments)  // 可变参数同理
public static byte @Nullable [] decryptFormHex(@Nullable String content, String aesTextKey)  // 返回值:注解须在数组位,不能置于 public 修饰符前
```

> **能这么放心机械替换的根因**:Spring 的 `@Nullable/@NonNull` 是**声明级**(`@Target({METHOD, PARAMETER, FIELD})`),所以现存用法必然只落在方法 / 参数 / 字段声明上——这些位置 JSpecify 的 TYPE_USE 注解都能绑定到对应类型,编译等价;唯独数组 / 可变参数的"元素位 vs 数组位"才有语义差,单独校正即可。其余标量字段 / 参数 / 返回值仅换 import,位置不变。

### 采用方案:方案一 · 机械替换(本次已执行)
- **方案一 · 机械替换(已采用,低风险)**:逐 import 替换 `org.springframework.lang.Nullable → org.jspecify.annotations.Nullable`、`NonNull → org.jspecify.annotations.NonNull`,并按上文校正数组 / 可变参数的 TYPE_USE 位置。语义等价,脚本批量完成。
- **方案二 · JSpecify 惯用法(未采用)**:在包级 `package-info.java` 或模块级加 `@NullMarked`(默认一切非空),仅在可空处保留 `@Nullable`、删除显式 `@NonNull`。更贴合 JSpecify 设计,但"默认非空"会暴露隐含可空点、需逐一甄别,风险与工作量更高;可作为后续代码现代化的独立议题。

### 归一边界(哪些不并入 JSpecify)
本次已把三工程内所有**纯元数据**空值注解并入 JSpecify;唯一需排除的是 **`lombok.@NonNull`**——它在编译期**生成运行时判空代码**(非纯元数据),换成 JSpecify 会丢掉该运行时检查、属行为变更。经全量扫描,三工程**均不存在** `lombok.@NonNull`,故本次无此顾虑;后续若引入,不得机械替换。

### 验证
blade-tool(24 模块)、Cloud(23 模块)、Boot 均 `mvn clean install` `BUILD SUCCESS`。全量 grep 复核:三工程空值注解**仅存** `org.jspecify.annotations.*` 一套(68 处 import),零 `org.springframework.lang.*` / `jakarta.annotation` / `reactor.util.annotation` / `org.jetbrains.annotations` 残留,无 `@Nonnull` / `@NotNull` 遗留用法,无重复 import,无元素位数组注解。TYPE_USE 数组语法(`byte @Nullable []`)编译通过;IDE 空值分析规则可切到 JSpecify。

---

## 16. 升级收尾:JDK 21、依赖时效性与打包优化

> 核心迁移完成后同批落地的一批收尾优化。三工程均在 JDK 21 下 `mvn clean install` `BUILD SUCCESS`。

### 16.1 JDK 17 → 21
三工程 `<java.version>` 由 17 抬到 **21 LTS**(Boot 4 最低仍是 17,此为主动升级)。`maven-compiler-plugin` 以 `${java.version}` 驱动 `source`/`target`,改属性即全生效;三份 README 的 JDK 徽章与技术栈表同步改 21+。**升级后构建需 JDK 21**。

此外,运行期基础镜像须同步升级:Boot 单体 1 个、Cloud 微服务 9 个 `Dockerfile` 的基础镜像由 `bladex/alpine-java:openjdk17_cn_slim` 改为 `openjdk21_cn_slim`(注意注释里的阿里云备用镜像行 `registry.cn-hangzhou.aliyuncs.com/bladex-repo/alpine-java:openjdk17_cn_slim` 也要一并改成 `openjdk21_cn_slim`)——`java.version` 只决定编译目标,基础镜像才决定容器内实际运行的 JRE,两者版本号必须对齐,否则 JDK 21 编出的字节码在 openjdk17 镜像里会 `UnsupportedClassVersionError` 起不来。blade-tool 为纯库工程无 Dockerfile,不涉及。

### 16.2 依赖时效性刷新
对照 Boot 4.1.0 BOM 与 Maven Central 核过一轮第三方版本:

| 依赖 | 处置 | 说明 |
|---|---|---|
| lombok | **去 pin**,回落 BOM `1.18.46` | 本地原 pin `1.18.42` 反而把 BOM 降级了 |
| commons-lang3 | **去 pin** | 原 pin `3.20.0` 与 BOM 完全一致,属冗余自管 |
| guava | `33.5.0-jre` → `33.6.0-jre` | 非 BOM 托管,自管刷新 |
| maven-compiler-plugin | `3.14.1` → `3.15.0` | 对齐 Boot 4.1.0 自身构建/BOM 版本 |
| maven-jar-plugin | `3.4.2` → `3.5.0` | 对齐 Boot 4.1.0 BOM |
| disruptor | **删除孤儿属性** | 全仓零引用的死属性 |
| okhttp / protostuff | **保留** | okhttp 5.x 跨大版本、有硬阻断风险;protostuff 1.8.0 已是最新 GA(Java 21 下依赖 `sun.misc.Unsafe`,Redis 序列化路径建议运行期验证) |

### 16.3 清理未使用的 JAXB 依赖
`blade-core-launch` 原声明 `javax.xml.bind:jaxb-api` + `com.sun.xml.bind:jaxb-core/jaxb-impl`(旧 javax 坐标,当年为 JDK 11+ 补 JAXB)。四维度全量扫描确认三工程**零处使用** JAXB(无 `@Xml*` 注解、无 `JAXBContext`/`Marshaller`/`Unmarshaller`、无 Spring OXM/WebService),属历史死依赖,已删除。删后重复的 JAXB 运行时实现(RI)消除;现代 `jakarta.xml.bind` API 仍由框架传递引入,将来若需 XML 绑定,按 `jakarta.xml.bind:jakarta.xml.bind-api` + `org.glassfish.jaxb:jaxb-runtime` 引入即可(勿用回旧 javax 那套)。保留 `jakarta.activation-api`(现代 Activation 规范,非 JAXB)。此外,父 POM `<dependencyManagement>` 里与旧 JAXB 配套的孤儿托管条目(`javax.xml.bind:jaxb-api`、`com.sun.xml.bind:jaxb-core/jaxb-impl`、`javax.activation:activation`)已无任何模块引用,一并删除。

### 16.4 清理 Boot 4 已移除的死配置键
`blade-core-boot/bootstrap.yml` 删除 `spring.mvc.throw-exception-if-no-handler-found`——该键在 Boot 4 配置元数据里已 `level=error`、无替代(`since 3.2.0`;新默认即抛 `NoHandlerFoundException`,行为不变)。留着是被静默忽略的死键、会被 IDE / 配置校验 / `spring-boot-properties-migrator` 标红,且随核心 starter 传播下游。

### 16.5 javadoc 发布打包:规避模块路径报错
`-P release` 发布时 `maven-javadoc-plugin` 在 JDK 9+ 下走模块路径,会因**内嵌 Tomcat 与 servlet-api 分裂包**、以及**个别依赖自动模块名无法推导**(如 `nacos-client-3.2.2` 的 `META-INF/services` 声明了不在同 jar 的 provider 类)而报大量 javadoc 错误(原 `failOnError=false` 已让其非致命)。根治:release profile 的 javadoc 配置加 `<legacyMode>true</legacyMode>`(走类路径、不推导模块描述符),所有 javadoc 报错归零、javadoc jar 正常产出。此为构建期问题,与运行期无关。

### 16.6 `BladeErrorController` 补 `@SuppressWarnings("removal")`
`blade-starter-log/.../error/BladeErrorController#errorHtml` 用到 SF7 标记 `forRemoval` 的 `MappingJackson2JsonView`,与 §5.5④ 其余 6 处策略一致,补 `@SuppressWarnings("removal")` 消除告警。

### 16.7 启用 Boot 4.1 进程信息贡献器(可观测性收尾)
Boot 4.1 新增 `ProcessInfoContributor`,可让 `/actuator/info` 携带进程运行时长、启动时间、时区、工作目录等信息(便于 Spring Boot Admin 面板观测线上运行状态)。该贡献器默认关闭,需显式开启 `management.info.process.enabled=true`。

BladeX 在 `blade-core-launch` 的 `BladeApplication.createSpringApplicationBuilder` 默认 props 块里(紧邻既有 `info.version` / `info.desc` 等 actuator info 默认)加一行:

```java
props.setProperty("management.info.process.enabled", "true");
```

- **放这里的原因**:`spring-boot-starter-actuator` 是父 POM 的全局公共依赖,全下游 app(Cloud + Boot)继承,一处默认惠及所有应用、零下游改动(契合同工程 `JacksonLauncherServiceImpl` / `SwaggerLauncherServiceImpl` 框架级设默认值的惯例)。
- **只开 `process`**:未开 `env`——`management.info.env.*` 会把已解析的配置属性值暴露到 `/actuator/info`,有泄露风险。
- **可覆盖**:命令行 `--management.info.process.enabled=false` 优先级高于 System 属性,可关闭。
- **无对外泄露**:BladeX 默认仅放行 `/actuator/health`(见 `SecureRegistry` / `BladeRequestFilter`),`/info` 端点受访问控制保护。

---

## 17. 方法注释规范统一 + 代码生成器模板同步

> 与 SB4 迁移同批推进的注释治理:按分层收敛方法注释风格,并让代码生成器产出的代码开箱即符合该规范。此节为「改造要求 + 改造记录」,后续新增 / 二开代码及生成器改动均以此为准对照着改。

### 17.1 分层注释规范(改造要求)

| 层 | 规范 | 为什么 |
|---|---|---|
| **Controller** | 方法 javadoc **只留摘要行**,不写 `@param`/`@return` | 接口文档由 springdoc 的 `@Operation`/`@Parameter` 承载,javadoc 标签与其重复且易残缺;摘要行已够 IDE 速览 |
| **Service / Mapper 接口** | 方法 javadoc **必须完整**:每个 `@param` 有意义描述、`@return` 有意义描述,不留空标签;`void` 方法不写 `@return` | 接口是调用方唯一契约来源(实现类 override 方法继承其注释),契约注释残缺会顺 IDE 悬浮提示传播到所有调用点 |
| **ServiceImpl** | public override 方法**不强制**注释(继承接口契约);**private 方法必须完整**(摘要 + `@param`/`@return`) | 私有方法无接口契约兜底,是实现内唯一注释来源 |
| **Entity / VO / DTO / Wrapper** | 不在本轮注释规范范围;字段用 OpenAPI 3 `@Schema`,Wrapper 的 override 方法同 ServiceImpl 处理 | — |

补充硬性要求:
- 描述从架构师视角写,准确、平实、专业;不写口水话、不逐行复述代码、不堆砌黑话。
- `@param` 名与方法签名**严格一致**;多个 `@param` 描述列对齐;**对齐同文件既有规范注释的措辞**,不另造风格。
- 空标签形态(须消除):`@param page`(参数名后为空)、`@return`(空)。完整形态(目标):`@param page 分页参数` / `@return 用户分页数据`。
- 接口排序注解 `@ApiOrder`:每个带 `@Tag` 的 Controller 在 `@Tag` 上方加无参 `@ApiOrder`,详见 §9⑧。

### 17.2 改造记录(Boot + Cloud)
- **Controller**:含方法级标签的控制器 javadoc 精简为摘要行(Boot 6 文件 / Cloud 8 文件),删除空 `@param`/`@return`;类级 `@author`/`@since` 不动。
- **Service / Mapper 接口**:填全空 `@param`/`@return`(Boot 23 文件、Cloud 25 文件,约 195 个接口方法),对齐同文件既有措辞;并补齐 1 处**缺失**的 `@param`(Boot `MenuMapper#grantTreeByRole`)。
- **ServiceImpl private 方法**:补全 javadoc——两工程各 `RoleServiceImpl#grantRoleMenu/grantDataScope/grantApiScope`、`TenantServiceImpl#getTenantId`。
- **`void` 方法**:删除多余空 `@return`(如 `IUserService#importUser`,void 无返回值)。
- 复核口径:全改动**仅注释行**(零签名 / 代码 / import 变动),目标层空标签、缺失 `@param`、错配 `@param` 三项全部归零。
- 范围之外(已知待办):Cloud Feign 层 `*Client` 接口与 `ResponseProvider` 尚有空标签,不属本轮四层,后续如需同规格补;`RegionMapper#lazyList/lazyTree` 声明返回 `List<RoleVO>` 而 SQL 实为行政区划,疑历史类型笔误,因禁改签名仅按真实语义写 `@return`。

### 17.3 代码生成器模板同步(`blade-starter-develop/src/main/resources/templates/`)
让生成的代码开箱符合 §17.1,并保持 SB4 / 最新 Swagger 风格:
- `controller.java.vm`:`@Tag` 上方 `@ApiOrder` + import(见 §9⑧⑦);方法 javadoc 本即摘要行,无 `@param`/`@return`。
- `service.java.vm` / `mapper.java.vm`:`select{Entity}Page` 空标签补为 `@param page 分页参数` / `@param {entityPath} {comment}查询条件` / `@return {comment}分页数据`。
- `serviceImpl.java.vm`:仅 public override 方法、无 private 方法,按规范不加注释。
- 模板已是 SB4 风格:`jakarta.*`、OpenAPI 3(`io.swagger.v3.oas.annotations.*`,含实体 / VO 的 `@Schema`),无 `javax.*` / knife4j / springfox。
- 下游 Boot、Cloud 各自 `blade-develop` 内的 `controller.java.vm` 模板副本同步同一改动(见 §9⑧⑦)。

---

## 18. 代码生成器 `blade-starter-develop` 结构重构与生成器逻辑收敛

> 承接 §17 的模板治理,进一步把生成器模块的**模板目录**与**Java 逻辑**重构到最新形态,并修复 MyBatis-Plus Generator 的过期 API。此节为「改造要求 + 改造记录」,便于后续读取与检验。

### 18.1 模板目录结构(`blade-starter-develop/src/main/resources/templates/`)
- `api/`:9 个后端 `.vm`(controller / entity / entityDTO / entityVO / mapper / mapper.xml / service / serviceImpl / wrapper)。
- `saber/`:`api.js.vm` + `crud.vue.vm`(前端 **Vue3 Composition API + TypeScript** 现代化版)。
- `sql/`:`menu.sql.vm`(菜单初始化 SQL 模板)。
- `code.properties`:生成器参数配置。

### 18.2 生成器 Java 逻辑收敛
`develop` 包收敛后仅保留 `CodeGenerator`(入口)与 `support/BladeCodeGenerator`(配置):
- `BladeCodeGenerator`:后端模板路径指向 `/templates/api/*`、前端 `/templates/saber/{api.js,crud.vue}.vm`;移除按前端类型的多目录分流逻辑(生成器统一使用 `saber` 前端模板)与 `DevelopConstant` 依赖;菜单 SQL 经 `customFile`(`menu.sql`)+ `outputCustomFile` 接线,用 `IdWorker` 生成 5 个菜单 ID(主 / 增 / 改 / 删 / 查),输出到 `{outputDir}/sql/{entity}.menu.sql`。**注:`systemName` 字段予以保留**(默认值由 `DevelopConstant.SABER3_NAME` 改为字面量 `"Saber"`),因下游 Boot/Cloud 的 `CodeController` 与测试 `CodeGenerator` 仍调用 `setSystemName(...)`——保留该字段可让下游**零改动**编译通过,只是它不再参与前端模板分流。
- `CodeGenerator`:移除 `SYSTEM_NAME` 与 `setSystemName`。
- `DevelopConstant.java`:整文件删除(全仓零引用)。

### 18.3 修复 MyBatis-Plus Generator 3.5.8 过期 API(`templateConfig` → builder 级 `*Template`)
- **过期点**:`TemplateConfig.Builder` 的 `entity/service/serviceImpl/mapper/xml/controller` 六个模板路径方法在 3.5.8 已 `@Deprecated`。
- **改法**(参照 BladeX-Tool 商业版思路、以本工程 Velocity/`.vm` 自行适配,非照搬其 Beetl 源码):删除 `.templateConfig(builder -> builder.disable(TemplateType.ENTITY).entity(...) … .controller(...))`,改用 `strategyConfig` 各 builder 的非过期方法:
  - `entityBuilder().javaTemplate("/templates/api/entity.java.vm")`
  - `serviceBuilder().serviceTemplate(…service.java.vm).serviceImplTemplate(…serviceImpl.java.vm)`
  - `mapperBuilder().mapperTemplate(…mapper.java.vm).mapperXmlTemplate(…mapper.xml.vm)`
  - `controllerBuilder().template(…controller.java.vm)`
  并移除随之无用的 `import …generator.config.TemplateType;`。
- **语义等价依据**:生成引擎运行时直接读取 builder 级字段(`AbstractTemplateEngine` 用 `entity.getJavaTemplate()`、`mapper.getMapperTemplatePath()` 等);`VelocityTemplateEngine.templateFilePath` 对 `.vm` 结尾做条件判断(已带则不再追加),故沿用带 `.vm` 的完整路径安全;旧 `.disable(TemplateType.ENTITY)` 本被紧随的 `.entity(...)`(内部置 `disableEntity=false`)抵消,移除对生成行为无影响。

### 18.4 验证
- JDK 21 下 `mvn -pl blade-starter-develop compile` **成功**,`-Dmaven.compiler.showDeprecation=true` 对 `BladeCodeGenerator` **无 deprecation 告警**。
- 代码引用的后端(`api/`)、前端(`saber/`)与菜单 SQL(`sql/`)模板路径与磁盘一一对应;无 `DevelopConstant` 残留。`systemName` 字段**按下游 API 兼容需要保留于 `BladeCodeGenerator`**(仅前端分流逻辑移除,见 §18.2),不属残留。
- 注:`saber/crud.vue.vm`(Vue3+TS)属采纳的现代化模板,运行期生成未连库实测;`beforeOutputFile` 里历史遗留的 `System.out.println` 与本次重构无关,按最小改动保留。

### 18.5 生成物 POJO 包结构(entity / dto / vo 归入 `pojo/`)

旧版生成的 `entity/`、`dto/`、`vo/` 平铺在模块根;5.0.0 起统一归入 `pojo/` 子包(`wrapper` / `mapper` / `controller` / `service`(含 `service/impl`)位置不变)。以 `notice` 为例:

```
notice/pojo/entity/Notice.java      package …notice.pojo.entity
notice/pojo/dto/NoticeDTO.java      package …notice.pojo.dto
notice/pojo/vo/NoticeVO.java        package …notice.pojo.vo
notice/wrapper/NoticeWrapper.java   package …notice.wrapper   (不动)
notice/{mapper,controller,service,service/impl}/…            (不动)
```

实现(`BladeCodeGenerator` + 2 个模板;entity/dto/vo 三模板不涉此项):
- `packageConfig` 的 entity 子包设为 **`.entity("pojo.entity")`**(与 `.serviceImpl("service.impl")→service/impl/` 同机制,MP 把点转成目录层级)。
- `outputCustomFile` 里 dto、vo 两处自定义输出路径**各含一层 `pojo/`**;wrapper 输出路径在模块根。
- dto/vo 的包由模板既有的 `$package.Entity.replace("entity","dto"/"vo")` 自动推导(entity 为 `pojo.entity` 时得 `pojo.dto`/`pojo.vo`),这三个模板无需为此改动。
- `wrapper.java.vm` 与 `controller.java.vm` 内 wrapper 包的推导用 **`$package.Entity.replace("pojo.entity","wrapper")`**,使 wrapper 落在模块根(而非 `pojo.wrapper`);其对 entity/vo 的 import 自动指向 `pojo.*`。

**下游连锁**:Boot/Cloud 各带一套本地模板副本,在 classpath 上遮蔽 blade-tool jar 内模板 —— 下游实际用本地副本生成,故本节与 §18.1 的目录形态需在下游本地副本上一并对齐(目录 `api/`/`saber/`、wrapper 推导同上),否则要么命不中本地副本(回落 jar 内模板)、要么 wrapper 包路径与目录不符而编译失败。

**验证**:`blade-starter-develop` JDK21 `clean install` 通过,jar 内模板仅 `api/`(9)+`saber/`(2);notice 结构推演 dto→`pojo.dto`、vo→`pojo.vo`、wrapper→根 三处「包↔路径」一致。

> ⚠️ 维护提示:`.vm` 是 Velocity 模板,常被 IDE 当 Java 误格式化(如把 `package $!{package.Entity};` 拆行、`#foreach($pkg in $!{table.importPackages})` 打断而生成坏代码)。改动后务必核对指令配平(`#if`+`#foreach` 数 == `#end` 数)与实际产物,勿让编辑器自动重排 `.vm`。
