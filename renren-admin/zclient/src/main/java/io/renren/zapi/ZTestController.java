package io.renren.zapi;


import com.fasterxml.jackson.core.JsonProcessingException;
import io.renren.zapi.dto.DepositRequest;
import io.renren.zapi.dto.DepositResponse;
import io.renren.zapi.dto.Result;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("ztest")
@Slf4j
public class ZTestController {
    @Resource
    private TestService testService;

    private Map<String, Method> methodMap = new HashMap<>();

    @PostConstruct
    public void init() {
        Method[] methods = TestService.class.getMethods();
        for (Method method : methods) {
            if(method.getReturnType().isAssignableFrom(Result.class)) {
                methodMap.put(method.getName(), method);
            }
        }
    }

    @GetMapping
    public Result charge(@RequestParam("name") String name) throws JsonProcessingException, InvocationTargetException, IllegalAccessException {
        return (Result)methodMap.get(name).invoke(testService, "string");
    }
}
