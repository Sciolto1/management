package com.hr.management.controller;

import com.hr.management.common.Result;
import com.hr.management.entity.Notice;
import com.hr.management.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 获取所有通知
     */
    @GetMapping("/list")
    public Result list() {
        List<Notice> list = noticeService.list();
        return Result.success(list);
    }

    /**
     * 添加通知
     */
    @PostMapping("/add")
    public Result add(@RequestBody Notice notice) {
        boolean success = noticeService.add(notice);
        return success ? Result.success() : Result.error("添加失败");
    }

    /**
     * 更新通知
     */
    @PutMapping("/update")
    public Result update(@RequestBody Notice notice) {
        boolean success = noticeService.update(notice);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        boolean success = noticeService.delete(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
