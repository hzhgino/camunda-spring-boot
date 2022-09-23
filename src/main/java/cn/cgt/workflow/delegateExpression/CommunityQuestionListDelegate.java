package cn.cgt.workflow.delegateExpression;

import cn.cgt.workflow.store.ProcessElementVariableStore;
import cn.cgt.workflow.utils.ProcessElementUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 社区-任务下发处理程序服务
 *
 * @author HuangZh
 * @date 2022/08/09
 */
@Component
public class CommunityQuestionListDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {

        String currentActivityId = execution.getCurrentActivityId();
        BpmnModelInstance bpmnModelInstance = execution.getBpmnModelInstance();
        BaseElement baseElement = bpmnModelInstance.getModelElementById(currentActivityId);
        ExtensionElements extensionElements = baseElement.getExtensionElements();
        String nextAssigneeVariable = ProcessElementUtil.getExtensionElementsValue(extensionElements, ProcessElementVariableStore.NEXT_ASSIGNEE);
        if (StringUtils.hasText(nextAssigneeVariable)) {
            execution.setVariable(nextAssigneeVariable, "被组织");
        }
        System.out.println(execution.getEventName());
    }
}
