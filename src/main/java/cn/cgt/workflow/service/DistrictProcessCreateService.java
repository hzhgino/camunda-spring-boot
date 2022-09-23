package cn.cgt.workflow.service;

import cn.cgt.workflow.delegateExpression.DefaultEndHandlerDelegate;
import cn.cgt.workflow.delegateExpression.DistrictIssueQuestionListDelegate;
import cn.cgt.workflow.store.ProcessDefinitionKeyStore;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 区流程创建服务
 *
 * @author HuangZh
 * @date 2022/08/09
 */
@Service
public class DistrictProcessCreateService {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private DefaultEndHandlerDelegate defaultEndHandlerDelegate;

    @Autowired
    private DistrictIssueQuestionListDelegate districtIssueQuestionListDelegate;

    /**
     * 创建地区部门主要过程
     * 区属-主要流程
     */
    public String createDistrictDepartmentMainProcess() {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(ProcessDefinitionKeyStore.PROCESS1)
                .latestVersion()
                .singleResult();

        if (processDefinition == null) {
            throw new RuntimeException("流程定义不存在");
        }

        String processDefinitionId = processDefinition.getId();
        //todo 动态获取需要动态赋值的变量

        Map<String, Object> variables = new HashMap<>();
        variables.put("task_1_delegate", districtIssueQuestionListDelegate);
        variables.put("endHandlerService", defaultEndHandlerDelegate);
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, variables);
        return processInstance.getId();
    }

    /**
     * 区属部门-民主生活会议流程创建
     */
    public String createDistrictDemocraticConference(String assignee) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(ProcessDefinitionKeyStore.PROCESS2)
                .latestVersion()
                .singleResult();

        if (processDefinition == null) {
            throw new RuntimeException("流程定义不存在");
        }

        String processDefinitionId = processDefinition.getId();
        Map<String, Object> variables = new HashMap<>();
        variables.put("task_1_assignee", assignee);
        variables.put("endHandlerService", defaultEndHandlerDelegate);
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, variables);
        return processInstance.getId();
    }


}
