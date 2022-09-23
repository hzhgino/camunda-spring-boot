package cn.cgt.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HuangZh
 * @date 2022/08/09
 */
@RestController
public class RootController {

    @GetMapping("/")
    public String root() {
        return "xxx";
    }

}
