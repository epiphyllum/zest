package io.renren.zadmin.zorg;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("zorg/jdashboard")
@Tag(name = "j_dashboard")
public class DashboardController {

    // 机构dashboard
    @GetMapping("org")
    public String org() {
        return null;
    }

    // 代理dashboard
    @GetMapping("agent")
    public String agent() {
        return null;
    }

    // 商户dashboard
    @GetMapping("merchant")
    public String merchant() {
        return null;
    }

    // 子商户dashboard
    @GetMapping("sub")
    public String sub() {
        return null;
    }
}
