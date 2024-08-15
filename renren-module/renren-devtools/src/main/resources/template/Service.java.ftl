package ${package}<#if moduleName??>.${moduleName}</#if>.service<#if subModuleName??>.${subModuleName}</#if>;

import ${package}.commons.mybatis.service.CrudService;
import ${package}<#if moduleName??>.${moduleName}</#if>.dto<#if subModuleName??>.${subModuleName}</#if>.${ClassName}DTO;
import ${package}<#if moduleName??>.${moduleName}</#if>.entity<#if subModuleName??>.${subModuleName}</#if>.${ClassName}Entity;

/**
 * ${tableComment}
 *
 * @author ${author} ${email}
 * @since ${version} ${date}
 */
public interface ${ClassName}Service extends CrudService<${ClassName}Entity, ${ClassName}DTO> {

}