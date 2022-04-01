/**
 * Copyright (c) 2018-2028, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springblade.develop.support;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.develop.constant.DevelopConstant;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 代码生成器配置类
 *
 * @author Chill
 */
@Data
@Slf4j
public class BladeCodeGenerator {
	/**
	 * 代码所在系统
	 */
	private String systemName = DevelopConstant.SABER_NAME;
	/**
	 * 代码模块名称
	 */
	private String codeName;
	/**
	 * 代码所在服务名
	 */
	private String serviceName = "blade-service";
	/**
	 * 代码生成的包名
	 */
	private String packageName = "org.springblade.test";
	/**
	 * 代码后端生成的地址
	 */
	private String packageDir;
	/**
	 * 代码前端生成的地址
	 */
	private String packageWebDir;
	/**
	 * 需要去掉的表前缀
	 */
	private String[] tablePrefix = {"blade_"};
	/**
	 * 需要生成的表名(两者只能取其一)
	 */
	private String[] includeTables = {"blade_test"};
	/**
	 * 需要排除的表名(两者只能取其一)
	 */
	private String[] excludeTables = {};
	/**
	 * 是否包含基础业务字段
	 */
	private Boolean hasSuperEntity = Boolean.TRUE;
	/**
	 * 是否包含包装器
	 */
	private Boolean hasWrapper = Boolean.TRUE;
	/**
	 * 基础业务字段
	 */
	private String[] superEntityColumns = {"create_time", "create_user", "create_dept", "update_time", "update_user", "status", "is_deleted"};
	/**
	 * 租户字段
	 */
	private String tenantColumn = "tenant_id";
	/**
	 * 数据库驱动名
	 */
	private String driverName;
	/**
	 * 数据库链接地址
	 */
	private String url;
	/**
	 * 数据库用户名
	 */
	private String username;
	/**
	 * 数据库密码
	 */
	private String password;

	/**
	 * 代码生成执行
	 */
	public void run() {
		Properties props = getProperties();
		String url = Func.toStr(this.url, props.getProperty("spring.datasource.url"));
		String username = Func.toStr(this.username, props.getProperty("spring.datasource.username"));
		String password = Func.toStr(this.password, props.getProperty("spring.datasource.password"));
		String servicePackage = serviceName.split("-").length > 1 ? serviceName.split("-")[1] : serviceName;

		Map<String, Object> customMap = new HashMap<>(11);
		customMap.put("codeName", codeName);
		customMap.put("serviceName", serviceName);
		customMap.put("servicePackage", servicePackage);
		customMap.put("servicePackageLowerCase", servicePackage.toLowerCase());
		customMap.put("tenantColumn", tenantColumn);
		customMap.put("hasWrapper", hasWrapper);
		Map<String, String> customFile = new HashMap<>(15);
		customFile.put("menu.sql", "/templates/sql/menu.sql.vm");
		customFile.put("entityVO.java", "/templates/entityVO.java.vm");
		customFile.put("entityDTO.java", "/templates/entityDTO.java.vm");
		if (hasWrapper) {
			customFile.put("wrapper.java", "/templates/wrapper.java.vm");
		}
		if (Func.isNotBlank(packageWebDir)) {
			if (Func.equals(systemName, DevelopConstant.SWORD_NAME)) {
				customFile.put("action.js", "/templates/sword/action.js.vm");
				customFile.put("model.js", "/templates/sword/model.js.vm");
				customFile.put("service.js", "/templates/sword/service.js.vm");
				customFile.put("list.js", "/templates/sword/list.js.vm");
				customFile.put("add.js", "/templates/sword/add.js.vm");
				customFile.put("edit.js", "/templates/sword/edit.js.vm");
				customFile.put("view.js", "/templates/sword/view.js.vm");
			} else if (Func.equals(systemName, DevelopConstant.SABER_NAME)) {
				customFile.put("api.js", "/templates/saber/api.js.vm");
				customFile.put("crud.vue", "/templates/saber/crud.vue.vm");
			}
		}

		FastAutoGenerator.create(url, username, password)
			.globalConfig(builder -> builder.author(props.getProperty("author")).dateType(DateType.TIME_PACK).enableSwagger().outputDir(getOutputDir()).disableOpenDir())
			.packageConfig(builder -> builder.parent(packageName).controller("controller").entity("entity").service("service").serviceImpl("service.impl").mapper("mapper").xml("mapper"))
			.strategyConfig(builder -> builder.addTablePrefix(tablePrefix).addInclude(includeTables).addExclude(excludeTables)
				.entityBuilder().naming(NamingStrategy.underline_to_camel).columnNaming(NamingStrategy.underline_to_camel).enableLombok().superClass("org.springblade.core.mp.base.BaseEntity").addSuperEntityColumns(superEntityColumns)
				.serviceBuilder().superServiceClass("org.springblade.core.mp.base.BaseService").superServiceImplClass("org.springblade.core.mp.base.BaseServiceImpl").formatServiceFileName("I%sService").formatServiceImplFileName("%sServiceImpl")
				.mapperBuilder().enableMapperAnnotation().enableBaseResultMap().enableBaseColumnList().formatMapperFileName("%sMapper").formatXmlFileName("%sMapper")
				.controllerBuilder().superClass("org.springblade.core.boot.ctrl.BladeController").formatFileName("%sController").enableRestStyle().enableHyphenStyle()
			)
			.templateConfig(builder -> builder.disable(TemplateType.ENTITY)
				.entity("/templates/entity.java.vm")
				.service("/templates/service.java.vm")
				.serviceImpl("/templates/serviceImpl.java.vm")
				.mapper("/templates/mapper.java.vm")
				.xml("/templates/mapper.xml.vm")
				.controller("/templates/controller.java.vm"))
			.injectionConfig(builder -> builder.beforeOutputFile(
					(tableInfo, objectMap) -> System.out.println("tableInfo: " + tableInfo.getEntityName() + " objectMap: " + objectMap.size())
				).customMap(customMap).customFile(customFile)
			)
			.templateEngine(new VelocityTemplateEngine() {
				@Override
				protected void outputCustomFile(Map<String, String> customFile, TableInfo tableInfo, Map<String, Object> objectMap) {
					String entityName = tableInfo.getEntityName();
					String entityNameLower = tableInfo.getEntityName().toLowerCase();

					customFile.forEach((key, value) -> {
						String outputPath = getPathInfo(OutputFile.other);
						if (StringUtil.equals(key, "menu.sql")) {
							objectMap.put("entityKey", entityNameLower);
							objectMap.put("menuId", IdWorker.getId());
							objectMap.put("addMenuId", IdWorker.getId());
							objectMap.put("editMenuId", IdWorker.getId());
							objectMap.put("removeMenuId", IdWorker.getId());
							objectMap.put("viewMenuId", IdWorker.getId());
							outputPath = getOutputDir() + StringPool.SLASH + "sql" + StringPool.SLASH + entityNameLower + ".menu.sql";
						}
						if (StringUtil.equals(key, "entityVO.java")) {
							outputPath = getOutputDir() + StringPool.SLASH + packageName.replace(StringPool.DOT, StringPool.SLASH) + StringPool.SLASH + "vo" + StringPool.SLASH + entityName + "VO" + StringPool.DOT_JAVA;
						}

						if (StringUtil.equals(key, "entityDTO.java")) {
							outputPath = getOutputDir() + StringPool.SLASH + packageName.replace(StringPool.DOT, StringPool.SLASH) + StringPool.SLASH + "dto" + StringPool.SLASH + entityName + "DTO" + StringPool.DOT_JAVA;
						}

						if (StringUtil.equals(key, "wrapper.java")) {
							outputPath = getOutputDir() + StringPool.SLASH + packageName.replace(StringPool.DOT, StringPool.SLASH) + StringPool.SLASH + "wrapper" + StringPool.SLASH + entityName + "Wrapper" + StringPool.DOT_JAVA;
						}

						if (StringUtil.equals(key, "action.js")) {
							outputPath = getOutputWebDir() + StringPool.SLASH + "actions" + StringPool.SLASH + entityNameLower + ".js";
						}

						if (StringUtil.equals(key, "model.js")) {
							outputPath = getOutputWebDir() + StringPool.SLASH + "models" + StringPool.SLASH + entityNameLower + ".js";
						}

						if (StringUtil.equals(key, "service.js")) {
							outputPath = getOutputWebDir() + StringPool.SLASH + "services" + StringPool.SLASH + entityNameLower + ".js";
						}

						if (StringUtil.equals(key, "list.js")) {
							outputPath = getOutputWebDir() + StringPool.SLASH + "pages" + StringPool.SLASH + StringUtil.upperFirst(servicePackage) + StringPool.SLASH + entityName + StringPool.SLASH + entityName + ".js";
						}

						if (StringUtil.equals(key, "add.js")) {
							outputPath = getOutputWebDir() + StringPool.SLASH + "pages" + StringPool.SLASH + StringUtil.upperFirst(servicePackage) + StringPool.SLASH + entityName + StringPool.SLASH + entityName + "Add.js";
						}

						if (StringUtil.equals(key, "edit.js")) {
							outputPath = getOutputWebDir() + StringPool.SLASH + "pages" + StringPool.SLASH + StringUtil.upperFirst(servicePackage) + StringPool.SLASH + entityName + StringPool.SLASH + entityName + "Edit.js";
						}

						if (StringUtil.equals(key, "view.js")) {
							outputPath = getOutputWebDir() + StringPool.SLASH + "pages" + StringPool.SLASH + StringUtil.upperFirst(servicePackage) + StringPool.SLASH + entityName + StringPool.SLASH + entityName + "View.js";
						}

						if (StringUtil.equals(key, "api.js")) {
							outputPath = getOutputWebDir() + StringPool.SLASH + "api" + StringPool.SLASH + servicePackage.toLowerCase() + StringPool.SLASH + entityNameLower + ".js";
						}

						if (StringUtil.equals(key, "crud.vue")) {
							outputPath = getOutputWebDir() + StringPool.SLASH + "views" + StringPool.SLASH + servicePackage.toLowerCase() + StringPool.SLASH + entityNameLower + ".vue";
						}
						outputFile(new File(String.valueOf(outputPath)), objectMap, value, Boolean.TRUE);
					});
				}
			})
			.execute();

	}


	/**
	 * 获取配置文件
	 *
	 * @return 配置Props
	 */
	private Properties getProperties() {
		// 读取配置文件
		Resource resource = new ClassPathResource("/templates/code.properties");
		Properties props = new Properties();
		try {
			props = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}

	/**
	 * 生成到项目中
	 *
	 * @return outputDir
	 */
	public String getOutputDir() {
		return (Func.isBlank(packageDir) ? System.getProperty("user.dir") + "/blade-ops/blade-develop" : packageDir) + "/src/main/java";
	}

	/**
	 * 生成到Web项目中
	 *
	 * @return outputDir
	 */
	public String getOutputWebDir() {
		return (Func.isBlank(packageWebDir) ? System.getProperty("user.dir") : packageWebDir) + "/src";
	}

}
