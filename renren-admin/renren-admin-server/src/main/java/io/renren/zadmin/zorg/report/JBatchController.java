package io.renren.zadmin.zorg.report;

import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.utils.ExcelUtils;
import io.renren.commons.tools.validator.AssertUtils;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.renren.zadmin.dto.JBatchDTO;
import io.renren.zadmin.excel.JBatchExcel;
import io.renren.zadmin.service.JBatchService;
import io.renren.zmanager.JBatchManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


/**
 * j_batch
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-19
 */
@RestController
@RequestMapping("zorg/jbatch")
@Tag(name = "j_batch")
public class JBatchController {
    @Resource
    private JBatchService jBatchService;
    @Resource
    private JBatchManager jBatchManager;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jbatch:page')")
    public Result<PageData<JBatchDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<JBatchDTO> page = jBatchService.page(params);
        return new Result<PageData<JBatchDTO>>().ok(page);
    }

    // 导出
    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jbatch:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JBatchDTO> list = jBatchService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "j_batch", list, JBatchExcel.class);
    }

    // 匹处理
    @GetMapping("run")
    @Operation(summary = "运行批处理")
    @LogOperation("允许批处理")
    @PreAuthorize("hasAuthority('zorg:jbatch:run')")
    public Result run(@RequestParam("batchType") String batchType, @RequestParam("batchDate") String batchDate) throws Exception {
        CompletableFuture.runAsync(() -> {
            jBatchManager.run(batchType, batchDate);
        });
        return new Result<>();
    }

    // 匹处理
    @GetMapping("cron")
    @Operation(summary = "运行批处理")
    @LogOperation("允许批处理")
    public Result cron(@RequestParam("batchType") String batchType, @RequestParam("batchDate") String batchDate) throws Exception {
        CompletableFuture.runAsync(() -> {
            jBatchManager.run(batchType, batchDate);
        });
        return new Result<>();
    }


    // 重新运行
    @GetMapping("rerun")
    @Operation(summary = "重新运行批处理")
    @LogOperation("重新运行批处理")
    @PreAuthorize("hasAuthority('zorg:jbatch:rerun')")
    public Result rerun(@RequestParam("id") Long id) {
        CompletableFuture.runAsync(() -> {
            jBatchManager.rerun(id);
        });
        return new Result();
    }
}