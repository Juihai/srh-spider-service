package com.shenruihai.spider.controller.manage;

import com.shenruihai.spider.service.SpiderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author shenruihai
 * @date 2022/5/16
 */
@RestController
@RequestMapping("/manage")
public class SpiderManageController {

    @Autowired
    ApplicationContext applicationContext;

    /**
     * 手动修复数据
     * @param serviceName
     * @param subjectId
     * @return
     */
    @RequestMapping("/fixData")
    public ResponseEntity<String> fixBookData(String serviceName, String subjectId){
        if(StringUtils.isBlank(serviceName)){
            return ResponseEntity.ok("serviceName is null.");
        }
        if(StringUtils.isBlank(subjectId)){
            return ResponseEntity.ok("subjectId is null.");
        }
        SpiderService spiderService = (SpiderService) applicationContext.getBean(serviceName);
        boolean ret = spiderService.spiderDancer(Long.parseLong(subjectId));
        return ResponseEntity.ok("fixBookDate Finished, result: "+ ret);
    }



}
