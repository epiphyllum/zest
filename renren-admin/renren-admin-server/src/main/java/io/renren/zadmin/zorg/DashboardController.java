package io.renren.zadmin.zorg;

import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.DateUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JSubDao;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.ZestConstant;
import io.renren.zdashboard.JDashboardAgent;
import io.renren.zdashboard.JDashboardMerchant;
import io.renren.zdashboard.JDashboardOperation;
import io.renren.zdashboard.JDashboardSub;
import io.renren.zdashboard.dto.AgentDashboardDTO;
import io.renren.zdashboard.dto.MerchantDashboardDTO;
import io.renren.zdashboard.dto.OrgDashboardDTO;
import io.renren.zdashboard.dto.SubDashboardDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("zorg/jdashboard")
@Tag(name = "j_dashboard")
@Slf4j
public class DashboardController {
    public static Date date1121 = DateUtils.parse("2024-11-21", DateUtils.DATE_PATTERN);

    @Resource
    private JSubDao jSubDao;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private JDashboardSub jDashboardSub;
    @Resource
    private JDashboardMerchant jDashboardMerchant;
    @Resource
    private JDashboardAgent jDashboardAgent;
    @Resource
    private JDashboardOperation jDashboardOperation;

    // 机构dashboard
    @GetMapping("operation")
    public Result<Map<String, OrgDashboardDTO>> org() {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals(ZestConstant.USER_TYPE_OPERATION)) {
            throw new RenException("请求非法");
        }

        Map<String, OrgDashboardDTO> dashboard = jDashboardOperation.dashboard(CommonUtils.todayDate());
        Result<Map<String, OrgDashboardDTO>> result = new Result<>();
        result.setData(dashboard);
        return result;
    }

    // 代理dashboard
    @GetMapping("agent")
    public Result<Map<String, AgentDashboardDTO>> agent(@RequestParam(value = "agentId", required = false) Long agentId) {
        UserDetail user = SecurityUser.getUser();
        if (agentId == null) {
            if (!user.getUserType().equals(ZestConstant.USER_TYPE_AGENT)) {
                throw new RenException("请求非法");
            }
            agentId = user.getDeptId();
        } else {
            if (user.getUserType().equals(ZestConstant.USER_TYPE_OPERATION)) {
                throw new RenException("请求非法");
            }
        }
        Map<String, AgentDashboardDTO> dashboard = jDashboardAgent.dashboard(CommonUtils.todayDate(), agentId);
        Result<Map<String, AgentDashboardDTO>> result = new Result<>();
        result.setData(dashboard);
        return result;
    }

    // 商户dashboard
    @GetMapping("merchant")
    public Result<Map<String, MerchantDashboardDTO>> merchant(@RequestParam(value = "merchantId", required = false) Long merchantId) {
        UserDetail user = SecurityUser.getUser();
        if (merchantId == null) {
            if (!user.getUserType().equals(ZestConstant.USER_TYPE_MERCHANT)) {
                throw new RenException("非法请求");
            }
            merchantId = user.getDeptId();
        } else {
            if (!user.getUserType().equals(ZestConstant.USER_TYPE_AGENT) &&
                    !user.getUserType().equals(ZestConstant.USER_TYPE_OPERATION)
            ) {
                throw new RenException("非法请求");
            }

            JMerchantEntity merchant = jMerchantDao.selectById(merchantId);
            Long agentId = merchant.getAgentId();
            // 代理查看
            if (user.getUserType().equals(ZestConstant.USER_TYPE_AGENT) &&
                    !agentId.equals(user.getDeptId())
            ) {
                throw new RenException("非法请求");
            }
            // 机构查看
        }
        Map<String, MerchantDashboardDTO> dashboard = jDashboardMerchant.dashboard(CommonUtils.todayDate(), merchantId);
        Result<Map<String, MerchantDashboardDTO>> result = new Result<>();
        result.setData(dashboard);
        return result;
    }

    // 子商户dashboard
    @GetMapping("sub")
    public Result<Map<String, SubDashboardDTO>> sub(@RequestParam(value = "subId", required = false) Long subId) {
        UserDetail user = SecurityUser.getUser();
        if (subId == null) {
            if (!user.getUserType().equals(ZestConstant.USER_TYPE_SUB)) {
                throw new RenException("非法请求");
            } else {
                log.info("子商户访问: ");
                subId = user.getDeptId();
            }
        } else {
            // 商户看自己的子商户dashboard
            if (!user.getUserType().equals(ZestConstant.USER_TYPE_MERCHANT) &&
                    !user.getUserType().equals(ZestConstant.USER_TYPE_AGENT) &&
                    !user.getUserType().equals(ZestConstant.USER_TYPE_OPERATION
                    )
            ) {
                throw new RenException("非法请求");
            }
            // subId 有值的情况下:
            JSubEntity jSubEntity = jSubDao.selectById(subId);
            Long merchantId = jSubEntity.getMerchantId();
            Long agentId = jSubEntity.getAgentId();

            // 代理查看
            if (user.getUserType().equals(ZestConstant.USER_TYPE_AGENT) &&
                    !agentId.equals(user.getDeptId())
            ) {
                throw new RenException("非法请求");
            }

            // 商户查看
            if (user.getUserType().equals(ZestConstant.USER_TYPE_MERCHANT) &&
                    !merchantId.equals(user.getDeptId())
            ) {
                throw new RenException("非法请求");
            }
            // 机构查看
        }
        try {
            Map<String, SubDashboardDTO> dashboard = jDashboardSub.dashboard(CommonUtils.todayDate(), subId);
            Result<Map<String, SubDashboardDTO>> result = new Result<>();
            result.setData(dashboard);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
}
