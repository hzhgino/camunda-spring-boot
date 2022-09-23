package cn.cgt.workflow.service;

import cn.cgt.workflow.store.ProcessElementVariableStore;
import cn.cgt.workflow.utils.ProcessElementUtil;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认的任务处理服务
 *
 * @author HuangZh
 * @date 2022/08/09
 */
@Service
@Slf4j
public class DefaultTaskCompleteService {

    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    /*
    审配类型：1、直接完成，仅需赋值下一个任务受让人
            2、专属网关，如果通过，赋值：下一个受让人、审批通过，如果驳回：仅需赋值审批意见
            3、并行网管，同时赋值两个下一节点的受让

     */

    /**
     * 直接完成，仅需赋值下一个任务受让人
     */
    public void completeTask(String taskId, String nextAssignee) {
        Task task = this.queryTask(taskId);
        Map<String, Object> variables = new HashMap<>();
        String nextAssigneeVariable = this.queryTaskExtensionValue(task, ProcessElementVariableStore.NEXT_ASSIGNEE);
        if (StringUtils.hasText(nextAssigneeVariable) && StringUtils.hasText(nextAssignee)) {
            variables.put(nextAssigneeVariable, nextAssignee);
        }
        log.info("任务开始,taskId:{},variables:{}", taskId, variables);
        taskService.complete(taskId, variables);
        log.info("任务完成,taskId:{},variables:{}", taskId, variables);
    }


    /**
     * 完成任务
     * 专属网关，如果通过，赋值：下一个受让人、审批通过，如果驳回：仅需赋值审批意见
     *
     * @param taskId       任务id
     * @param nextAssignee 下一个受让人
     * @param approval     审批意见(agree、rejected)
     */

    public void completeTask(String taskId, String nextAssignee, String approval) {
        Task task = this.queryTask(taskId);
        Map<String, Object> variables = new HashMap<>();
        //设置审批意见变量
        String approvalVariable = this.queryTaskExtensionValue(task, ProcessElementVariableStore.APPROVAL_VARIABLE);
        if (StringUtils.hasText(approvalVariable) && StringUtils.hasText(approval)) {
            variables.put(approvalVariable, approval);
        }

        //设置下一个受让人变量
        String nextAssigneeVariable = this.queryTaskExtensionValue(task, ProcessElementVariableStore.NEXT_ASSIGNEE);
        if (StringUtils.hasText(nextAssigneeVariable) &&
                StringUtils.hasText(nextAssignee) &&
                "agree".equals(approval)
        ) {
            variables.put(nextAssigneeVariable, nextAssignee);
        }
        log.info("任务开始,taskId:{},variables:{}", taskId, variables);
        taskService.complete(taskId, variables);
        log.info("任务完成,taskId:{},variables:{}", taskId, variables);
    }


    /**
     * 完成任务
     * 并行网管，同时赋值多个下一节点的受让人
     *
     * @param taskId    任务id
     * @param variables 变量
     */
    public void completeTask(String taskId, Map<String, Object> variables) {
        Task task = this.queryTask(taskId);
        //设置下一个受让人变量
        log.info("任务开始,taskId:{},variables:{}", taskId, variables);
        taskService.complete(taskId, variables);
        log.info("任务完成,taskId:{},variables:{}", taskId, variables);
    }


    /**
     * 获取任务扩展值
     *
     * @param taskId 任务id
     * @param key    关键
     *
     * @return {@link String}
     */
    public String getTaskExtensionValue(String taskId, String key) {
        Task task = this.queryTask(taskId);
        return this.queryTaskExtensionValue(task, key);
    }


    /**
     * 查询任务
     *
     * @param taskId 任务id
     *
     * @return {@link Task}
     */
    private Task queryTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        return task;
    }


    /**
     * 查询任务的扩展属性
     */
    private String queryTaskExtensionValue(Task task, String extensionName) {
        String processDefinitionId = task.getProcessDefinitionId();
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
        BaseElement baseElement = bpmnModelInstance.getModelElementById(task.getTaskDefinitionKey());
        ExtensionElements extensionElements = baseElement.getExtensionElements();
        return ProcessElementUtil.getExtensionElementsValue(extensionElements, extensionName);
    }


}
