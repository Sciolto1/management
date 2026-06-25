package com.hr.management.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hr.management.entity.Notice;
import com.hr.management.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

/**
 * 通知公告服务
 */
@Service
public class NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    /**
     * 获取所有通知（按日期降序）
     */
    public List<Notice> list() {
        QueryWrapper<Notice> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("publish_date");
        return noticeMapper.selectList(wrapper);
    }

    /**
     * 添加通知
     */
    public boolean add(Notice notice) {
        if (notice.getPublishDate() == null) {
            notice.setPublishDate(LocalDate.now());
        }
        return noticeMapper.insert(notice) > 0;
    }

    /**
     * 更新通知
     */
    public boolean update(Notice notice) {
        return noticeMapper.updateById(notice) > 0;
    }

    /**
     * 删除通知
     */
    public boolean delete(Integer id) {
        return noticeMapper.deleteById(id) > 0;
    }
}
