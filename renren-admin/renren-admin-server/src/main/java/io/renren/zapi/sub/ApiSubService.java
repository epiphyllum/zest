package io.renren.zapi.sub;

import io.renren.commons.tools.utils.Result;
import io.renren.zapi.ApiContext;
import io.renren.zapi.sub.dto.SubCreate;
import io.renren.zapi.sub.dto.SubCreateRes;
import io.renren.zapi.sub.dto.SubQuery;
import io.renren.zapi.sub.dto.SubQueryRes;
import org.springframework.stereotype.Service;

@Service
public class ApiSubService {
    public Result<SubCreateRes> subCreate(SubCreate request, ApiContext context) { return null;}
    public Result<SubQueryRes> subQuery(SubQuery req, ApiContext context) { return null;}
}
