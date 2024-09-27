package io.renren.zapi.service.allocate;

import io.renren.zadmin.dao.JAllocateDao;
import io.renren.zadmin.dto.JAllocateDTO;
import io.renren.zadmin.entity.JAllocateEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AllocateService {

    @Resource
    private JAllocateDao jAllocateDao;

    public void save(JAllocateDTO dto) {
        switch (dto.getType()) {
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

    private void handleS2m(JAllocateEntity entity) {
    }

    private void handleM2s(JAllocateEntity entity) {
    }

    private void handleV2i(JAllocateEntity entity) {
    }

    private void handleI2v(JAllocateDTO dto) {
    }
}
