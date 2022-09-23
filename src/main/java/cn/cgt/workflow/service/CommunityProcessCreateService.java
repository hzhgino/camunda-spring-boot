package cn.cgt.workflow.service;

import cn.cgt.workflow.delegateExpression.CommunityQuestionListDelegate;
import cn.cgt.workflow.delegateExpression.DefaultEndHandlerDelegate;
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
 * 社区过程创建服务
 *
 * @author HuangZh
 * @date 2022/08/09
 */
@Service
public class CommunityProcessCreateService {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private DefaultEndHandlerDelegate defaultEndHandlerDelegate;
    @Autowired
    private CommunityQuestionListDelegate communityQuestionListDelegate;

    /**
     * 社区-主流程创建
     */
    public String createCommunityMainProcess() {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(ProcessDefinitionKeyStore.PROCESS3)
                .latestVersion()
                .singleResult();

        if (processDefinition == null) {
            throw new RuntimeException("流程定义不存在");
        }

        String processDefinitionId = processDefinition.getId();
        //todo 动态获取需要动态赋值的变量

        Map<String, Object> variables = new HashMap<>();
        variables.put("task_1_delegate", communityQuestionListDelegate);
        variables.put("endHandlerService", defaultEndHandlerDelegate);
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, variables);
        return processInstance.getId();
    }

    /**
     * 社区-创建党内通报流程
     *
     * @param assignee 受让人
     */
    public String createCommunityIntraPartyBriefing(String assignee) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(ProcessDefinitionKeyStore.PROCESS4)
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
