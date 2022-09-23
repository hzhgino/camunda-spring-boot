package cn.cgt.workflow;

import cn.cgt.workflow.service.DefaultTaskCompleteService;
import cn.cgt.workflow.service.DistrictProcessCreateService;
import cn.cgt.workflow.store.ProcessElementVariableStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HuangZh
 * @date 2022/08/09
 */
@RestController
public class DistrictProcessController {
    @Autowired
    private DistrictProcessCreateService districtProcessCreateService;
    @Autowired
    private DefaultTaskCompleteService defaultTaskCompleteService;

    @GetMapping("/anon/district/start")
    public Object startProcess() {
        return districtProcessCreateService.createDistrictDepartmentMainProcess();
    }


    @GetMapping("/anon/district/complete1")
    public Object taskComplete(String taskId, String nextAssignee) {
        defaultTaskCompleteService.completeTask(taskId, nextAssignee);
        return "任务完成-->OK";
    }


    @GetMapping("/anon/district/complete2")
    public Object taskComplete(String taskId, String approval, String nextAssignee) {

        defaultTaskCompleteService.completeTask(taskId, nextAssignee, approval);
        return "任务审批-->OK";
    }

    /**
     * 并行审批节点
     */
    @GetMapping("/anon/district/complete3")
    public Object taskComplete(String taskId, String approval, String[] nextAssignee) {
        Map<String, Object> variables = new HashMap<>();
        //同意
        if ("agree".equals(approval) && nextAssignee.length > 1) {
            variables.put(ProcessElementVariableStore.getNextParallelAssignee(1), nextAssignee[0]);
            variables.put(ProcessElementVariableStore.getNextParallelAssignee(2), nextAssignee[1]);
        }
        variables.put(ProcessElementVariableStore.APPROVAL_VARIABLE, approval);

        defaultTaskCompleteService.completeTask(taskId, variables);
        return "任务并行审批-->OK";
    }

}
