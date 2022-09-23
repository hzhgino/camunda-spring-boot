package cn.cgt.workflow.delegateExpression;

import cn.cgt.workflow.store.ProcessElementVariableStore;
import cn.cgt.workflow.utils.ProcessElementUtil;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * 区属-任务下发处理程序服务
 *
 * @author HuangZh
 * @date 2022/08/09
 */
@Component
@Slf4j
public class DistrictIssueQuestionListDelegate implements JavaDelegate, Serializable {


    private static final long serialVersionUID = 6267069658991035120L;

    @Override
    public void execute(DelegateExecution execution) {

        String currentActivityId = execution.getCurrentActivityId();
        BpmnModelInstance bpmnModelInstance = execution.getBpmnModelInstance();
        BaseElement baseElement = bpmnModelInstance.getModelElementById(currentActivityId);
        ExtensionElements extensionElements = baseElement.getExtensionElements();
        String nextAssigneeVariable = ProcessElementUtil.getExtensionElementsValue(extensionElements, ProcessElementVariableStore.NEXT_ASSIGNEE);
        if (StringUtils.hasText(nextAssigneeVariable)) {
            execution.setVariable(nextAssigneeVariable, "");
        }
        log.info(execution.getEventName());
    }
}
