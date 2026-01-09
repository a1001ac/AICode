package com.ermao.aicode.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ermao.aicode.model.dto.app.AppAddRequest;
import com.ermao.aicode.model.dto.app.AppQueryRequest;
import com.ermao.aicode.model.entity.App;
import com.ermao.aicode.model.entity.User;
import com.ermao.aicode.model.vo.AppVO;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
* @author 21195
*/
public interface AppService extends IService<App> {

    AppVO getAppVO(App app);

    List<AppVO> getAppVOList(List<App> appList);

    QueryWrapper<App> getQueryWrapper(AppQueryRequest appQueryRequest);

    Flux<String> chatToGenCode(String userMessage, Long appId, User loginUser);

    String deployApp(Long appId, User loginUser);

    void generateAppScreenshotAsync(Long appId, String appUrl);

    long addApp(AppAddRequest appAddRequest,User loginUser);

    Map<String, Long> getCountByCodeGenType();
}
