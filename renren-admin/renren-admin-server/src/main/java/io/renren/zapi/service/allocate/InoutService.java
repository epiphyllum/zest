package io.renren.zapi.service.allocate;

import io.renren.zadmin.dao.JInoutDao;
import io.renren.zadmin.dto.JInoutDTO;
import io.renren.zadmin.entity.JInoutEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class InoutService {

    @Resource
    private JInoutDao jInoutDao;

    public void save(JInoutDTO dto) {
        switch(dto.getType()) {
//            case "i2v":
//                handleI2v(dto);
//                break;
//            case "v2i":
//                handleV2i(dto);
//                break;
//            case "m2s":
//                handleM2s(dto);
//                break;
//            case "s2m":
//                handleS2m(dto);
//                break;
        }
    }

    private void handleS2m(JInoutEntity entity) {
    }

    private void handleM2s(JInoutEntity entity) {
    }

    private void handleV2i(JInoutEntity entity) {
    }

    private void handleI2v(JInoutDTO dto) {
    }
}
