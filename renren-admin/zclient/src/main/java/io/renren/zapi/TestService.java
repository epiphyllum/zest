package io.renren.zapi;

import io.renren.zapi.dto.Result;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    public Result<String>  a(String body) {
        Result<String> stringResult = new Result<>();
        stringResult.setData("String");
        return stringResult;
    }

    public Result<Integer> b(String body){
        Result<Integer> integerResult = new Result<>();
        integerResult.setData(1);
        return integerResult;
    }
}
