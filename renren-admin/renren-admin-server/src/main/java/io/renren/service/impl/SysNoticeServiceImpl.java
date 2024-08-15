/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package io.renren.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.commons.mybatis.service.impl.CrudServiceImpl;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.dao.SysNoticeDao;
import io.renren.dto.SysNoticeDTO;
import io.renren.entity.SysNoticeEntity;
import io.renren.entity.SysNoticeUserEntity;
import io.renren.enums.NoticeReadStatusEnum;
import io.renren.enums.NoticeStatusEnum;
import io.renren.enums.ReceiverTypeEnum;
import io.renren.service.SysNoticeService;
import io.renren.service.SysNoticeUserService;
import io.renren.service.SysUserService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 通知管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class SysNoticeServiceImpl extends CrudServiceImpl<SysNoticeDao, SysNoticeEntity, SysNoticeDTO> implements SysNoticeService {
    @Resource
    private SysNoticeUserService sysNoticeUserService;
    @Resource
    private SysUserService sysUserService;

    @Override
    public QueryWrapper<SysNoticeEntity> getWrapper(Map<String, Object> params) {
        String noticeType = (String) params.get("noticeType");

        QueryWrapper<SysNoticeEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(noticeType), "notice_type", noticeType);
        wrapper.orderByDesc(Constant.CREATE_DATE);
        return wrapper;
    }

    @Override
    public PageData<SysNoticeDTO> getNoticeUserPage(Map<String, Object> params) {
        //分页
        IPage<SysNoticeEntity> page = getPage(params, null, false);

        //查询
        List<SysNoticeEntity> list = baseDao.getNoticeUserList(params);

        return getPageData(list, page.getTotal(), SysNoticeDTO.class);
    }

    @Override
    public PageData<SysNoticeDTO> getMyNoticePage(Map<String, Object> params) {
        //分页
        IPage<SysNoticeEntity> page = getPage(params, null, false);

        //查询
        params.put("receiverId", SecurityUser.getUserId());
        List<SysNoticeEntity> list = baseDao.getMyNoticeList(params);

        return getPageData(list, page.getTotal(), SysNoticeDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SysNoticeDTO dto) {
        SysNoticeEntity entity = ConvertUtils.sourceToTarget(dto, SysNoticeEntity.class);

        //更新发送者信息
        if (dto.getStatus() == NoticeStatusEnum.SEND.value()) {
            entity.setSenderName(SecurityUser.getUser().getRealName());
            entity.setSenderDate(new Date());
        }

        baseDao.insert(entity);

        //发送通知
        dto.setId(entity.getId());
        sendNotice(dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysNoticeDTO dto) {
        SysNoticeEntity entity = ConvertUtils.sourceToTarget(dto, SysNoticeEntity.class);

        //更新发送者信息
        if (dto.getStatus() == NoticeStatusEnum.SEND.value()) {
            entity.setSenderName(SecurityUser.getUser().getRealName());
            entity.setSenderDate(new Date());
        }

        this.updateById(entity);

        //发送通知
        sendNotice(dto);
    }

    /**
     * 发送通知
     */
    public void sendNotice(SysNoticeDTO notice) {
        //如果是草稿，在不发送通知
        if (notice.getStatus() == NoticeStatusEnum.DRAFT.value()) {
            return;
        }

        //全部用户
        if (notice.getReceiverType() == ReceiverTypeEnum.ALL.value()) {
            //发送给全部用户
            sendAllUser(notice);
        } else {  //选中用户
            List<Long> userIdList = sysUserService.getUserIdListByDeptId(notice.getReceiverTypeList());
            if (userIdList.size() == 0) {
                return;
            }

            //发送给选中用户
            sendUser(notice, userIdList);
        }
    }

    /**
     * 发送给全部用户
     */
    public void sendAllUser(SysNoticeDTO notice) {
        SysNoticeUserEntity noticeUser = new SysNoticeUserEntity()
                .setNoticeId(notice.getId())
                .setReadStatus(NoticeReadStatusEnum.UNREAD.value());
        sysNoticeUserService.insertAllUser(noticeUser);
    }

    /**
     * 发送给选中用户
     */
    public void sendUser(SysNoticeDTO notice, List<Long> userIdList) {
        userIdList.forEach(userId -> {
            SysNoticeUserEntity noticeUser = new SysNoticeUserEntity()
                    .setNoticeId(notice.getId())
                    .setReceiverId(userId)
                    .setReadStatus(NoticeReadStatusEnum.UNREAD.value());

            sysNoticeUserService.save(noticeUser);
        });
    }

}