package cn.cgt.workflow.utils;

import org.camunda.bpm.model.bpmn.Query;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;

import java.util.Collection;

/**
 * 流程元素工具类
 *
 * @author HuangZh
 * @date 2022/08/09
 */
public class ProcessElementUtil {


    /**
     * 获取扩展元素值
     *
     * @param extensionElements 扩展元素
     * @param extensionName     扩展名
     *
     * @return {@link String}
     */
    public static String getExtensionElementsValue(ExtensionElements extensionElements, String extensionName) {
        if (extensionElements == null) {
            return "";
        }
        Query<CamundaProperties> camundaPropertiesQuery = extensionElements.getElementsQuery()
                .filterByType(CamundaProperties.class);
        if (camundaPropertiesQuery.count() == 0) {
            //没有扩展属性
            return "";
        }
        Collection<CamundaProperty> properties = camundaPropertiesQuery
                .singleResult()
                .getCamundaProperties();
        for (CamundaProperty property : properties) {
            String camundaName = property.getCamundaName();
            if (extensionName.equals(camundaName)) {
                return property.getCamundaValue();
            }
        }
        return "";
    }

}
