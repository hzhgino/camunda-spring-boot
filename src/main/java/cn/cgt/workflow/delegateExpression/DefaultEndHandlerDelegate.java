package cn.cgt.workflow.delegateExpression;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 默认处理程序服务
 *
 * @author HuangZh
 * @date 2022/08/09
 */
@Component
@Slf4j
public class DefaultEndHandlerDelegate implements JavaDelegate, Serializable {

    private static final long serialVersionUID = 1752089043320941299L;

    @Override
    public void execute(DelegateExecution execution) {
        String name = execution.getCurrentActivityName();
        String eventName = execution.getEventName();
        log.info("---> name :" + name + " eventName :" + eventName);
    }
}
