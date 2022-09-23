package cn.cgt.workflow;

import cn.cgt.workflow.service.CommunityProcessCreateService;
import cn.cgt.workflow.service.DefaultTaskCompleteService;
import cn.cgt.workflow.store.ProcessElementVariableStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 社区过程控制器
 *
 * @author HuangZh
 * @date 2022/08/10
 */
@RestController
public class CommunityProcessController {

    @Autowired
    private CommunityProcessCreateService communityProcessCreateService;
    @Autowired
    private DefaultTaskCompleteService defaultTaskCompleteService;


    @GetMapping("/anon/community/ipb/start")
    public Object startProcess(String assignee) {
        return communityProcessCreateService.createCommunityIntraPartyBriefing(assignee);
    }


    @GetMapping("/anon/community/complete1")
    public Object taskComplete(String taskId, String nextAssignee) {
        defaultTaskCompleteService.completeTask(taskId, nextAssignee);
        return "任务完成-->OK";
    }


    @GetMapping("/anon/community/complete2")
    public Object taskComplete(String taskId, String approval, String nextAssignee) {

        defaultTaskCompleteService.completeTask(taskId, nextAssignee, approval);
        return "任务审批-->OK";
    }

    /**
     * 并行审批节点
     */
    @GetMapping("/anon/community/complete3")
    public Object taskComplete(String taskId, String approval, String[] nextAssignee) {
        Map<String, Object> variables = new HashMap<>();
        //同意
        if ("agree".equals(approval) && nextAssignee.length > 1) {
            String parallelAssignee1 = defaultTaskCompleteService.getTaskExtensionValue(taskId, ProcessElementVariableStore.getNextParallelAssignee(1));
            String parallelAssignee2 = defaultTaskCompleteService.getTaskExtensionValue(taskId, ProcessElementVariableStore.getNextParallelAssignee(2));
            variables.put(parallelAssignee1, nextAssignee[0]);
            variables.put(parallelAssignee2, nextAssignee[1]);
        }
        variables.put(defaultTaskCompleteService.getTaskExtensionValue(taskId, ProcessElementVariableStore.APPROVAL_VARIABLE), approval);
        defaultTaskCompleteService.completeTask(taskId, variables);
        return "任务并行审批-->OK";
    }


}
