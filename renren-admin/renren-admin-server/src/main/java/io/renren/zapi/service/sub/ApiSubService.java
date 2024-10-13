package io.renren.zapi.service.sub;

import io.renren.commons.tools.utils.Result;
import io.renren.zapi.ApiContext;
import io.renren.zapi.service.sub.dto.SubCreate;
import io.renren.zapi.service.sub.dto.SubCreateRes;
import io.renren.zapi.service.sub.dto.SubQuery;
import io.renren.zapi.service.sub.dto.SubQueryRes;
import org.springframework.stereotype.Service;

@Service
public class ApiSubService {
    public Result<SubCreateRes> subCreate(SubCreate request, ApiContext context) { return null;}
    public Result<SubQueryRes> subQuery(SubQuery req, ApiContext context) { return null;}
}
