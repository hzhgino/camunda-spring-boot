package cn.cgt.workflow;

import cn.cgt.workflow.store.ProcessElementVariableStore;
import cn.cgt.workflow.utils.ProcessElementUtil;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程查询控制器
 *
 * @author HuangZh
 * @date 2022/08/09
 */
@RestController
public class ProcessQueryController {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;


    /**
     * 查询进程id
     *
     * @param processDefinitionKey 流程定义关键
     *
     * @return {@link Object}
     */
    @GetMapping("/anon/query/process/definition")
    public Object queryProcessId(String processDefinitionKey) {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).list();
        return list.stream().map(ProcessDefinition::getId).collect(Collectors.toList());
    }


    /**
     * 创建过程
     *
     * @param processDefinitionId 流程定义id
     *
     * @return {@link Object}
     */
    @GetMapping("/anon/create/task")
    public Object createProcess(String processDefinitionId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("task_1_assignee", "被巡察党组织");
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, variables);
        //return list.stream().map(ProcessDefinition::getId).collect(Collectors.toList());
        return "processInstanceId:" + processInstance.getId();
    }

    @Autowired
    private TaskService taskService;

    /**
     * 查询任务列表
     *
     * @param assingee assingee
     *
     * @return {@link Object}
     */
    @GetMapping("/anon/todo/task/query")
    public Object queryTaskList(String assignee) {
        List<String> list = taskService.createTaskQuery().taskAssignee(assignee).list()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        return list;
    }


    /**
     * 完整
     *
     * @param taskId       任务id
     * @param nextAssignee 下一个受让人
     * @param approval     批准
     *
     * @return {@link Object}
     */
    @GetMapping("/anon/todo/task/complete")
    public Object complete(String taskId, String nextAssignee, String approval) {
        Map<String, Object> variables = new HashMap<>();

        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        //获取流程元素 getBpmnModel
        String processDefinitionId = task.getProcessDefinitionId();
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
        BaseElement modelElementById = bpmnModelInstance.getModelElementById(task.getTaskDefinitionKey());
        ExtensionElements extensionElements = modelElementById.getExtensionElements();

        //受让人
        String assigneeVariable = ProcessElementUtil.getExtensionElementsValue(extensionElements, ProcessElementVariableStore.NEXT_ASSIGNEE);
        if (StringUtils.hasText(assigneeVariable) && StringUtils.hasText(nextAssignee)) {
            variables.put(assigneeVariable, nextAssignee);
        }


        //审批意见
        String approvalVariable = ProcessElementUtil.getExtensionElementsValue(extensionElements, ProcessElementVariableStore.APPROVAL_VARIABLE);
        if (StringUtils.hasText(approvalVariable) && StringUtils.hasText(approval)) {
            variables.put(approvalVariable, approval);
        }
        taskService.complete(taskId, variables);
        return variables;
    }


    /**
     * 平行审批方法处理
     */
    @GetMapping("/anon/todo/task/parallel/complete")
    public Object taskParallelComplete(String taskId, String approval) {
        Map<String, Object> variables = new HashMap<>();

        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        //获取流程元素 getBpmnModel
        String processDefinitionId = task.getProcessDefinitionId();
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
        BaseElement modelElementById = bpmnModelInstance.getModelElementById(task.getTaskDefinitionKey());
        ExtensionElements extensionElements = modelElementById.getExtensionElements();

        String nextParallelAssignee = "xxx";
        String extensionElementsValue = ProcessElementUtil.getExtensionElementsValue(extensionElements, ProcessElementVariableStore.getNextParallelAssignee(1));
        if (StringUtils.hasText(extensionElementsValue) && StringUtils.hasText(nextParallelAssignee)) {
            variables.put(extensionElementsValue, nextParallelAssignee);
        }

        nextParallelAssignee = "镇党委";
        extensionElementsValue = ProcessElementUtil.getExtensionElementsValue(extensionElements, ProcessElementVariableStore.getNextParallelAssignee(2));
        if (StringUtils.hasText(extensionElementsValue) && StringUtils.hasText(nextParallelAssignee)) {
            variables.put(extensionElementsValue, nextParallelAssignee);
        }


        //审批意见
        String approvalVariable = ProcessElementUtil.getExtensionElementsValue(extensionElements, ProcessElementVariableStore.APPROVAL_VARIABLE);
        if (StringUtils.hasText(approvalVariable) && StringUtils.hasText(approval)) {
            variables.put(approvalVariable, approval);
        }


        taskService.complete(taskId, variables);

        return variables;
    }


    /**
     * 删除过程
     *
     * @param processDefinitionId 流程定义id
     *
     * @return {@link Object}
     */
    @GetMapping("/deleteProcess")
    public Object deleteProcess(String processDefinitionId) {

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        if (processDefinition != null) {
            repositoryService.deleteProcessDefinition(processDefinitionId, true);
        }
        return "-->ok";
    }


}
