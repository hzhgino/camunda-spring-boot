package cn.cgt.workflow.store;

/**
 * 流程元素变量存储
 *
 * @author HuangZh
 * @date 2022/08/09
 */
public class ProcessElementVariableStore {

    /**
     * 下一个受让人
     */
    public static final String NEXT_ASSIGNEE = "next_assignee";
    /**
     * 批准变量
     */
    public static final String APPROVAL_VARIABLE = "approval_variable";

    /**
     * 下一个平行受让人
     */
    private static final String NEXT_PARALLEL_ASSIGNEE = "next_parallel_assignee";


    /**
     * 获取平行受让人
     *
     * @param assigneeNumber 受让人编号
     *
     * @return {@link String}
     */
    public static String getNextParallelAssignee(Integer assigneeNumber) {
        return NEXT_PARALLEL_ASSIGNEE + "_" + assigneeNumber;
    }
}
