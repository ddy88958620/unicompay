package com.unicom.tv.controller;

import com.alibaba.fastjson.JSON;

import com.unicom.tv.bean.pojo.ChargePoint;
import com.unicom.tv.service.interfaces.ChargePointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("charger")
public class ChargePointController extends AbstractController {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;

	@Resource
	private ChargePointService cpService;


	@RequestMapping(value = "ChargePoint", method = RequestMethod.GET)
    @ResponseBody
	public ResponseEntity<String> getChargePoint(HttpServletRequest request, HttpServletResponse response) {
        String pointid = request.getParameter("pointid");
        String cporderid = request.getParameter("cporderid");
        String jpKey = "ChargePoint." + pointid + "." + cporderid;

            if(!redisTemplate.hasKey(jpKey)){
                ChargePoint c = new ChargePoint();
                c.setPointid(pointid);
                c = cpService.getChargePoint(c);
                //设置缓存并设置缓存时间
                redisTemplate.opsForValue().set(jpKey,"1",60*30, TimeUnit.SECONDS);
                return this.getOrginResponse(c);
            }
        return null;
	}
}
