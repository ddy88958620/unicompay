package com.unicom.tv.service.interfaces.impl;

import com.unicom.tv.bean.pojo.ChargePoint;
import com.unicom.tv.dao.ChargePointDao;
import com.unicom.tv.service.interfaces.ChargePointService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zhushuai on 2015/1/27.
 */
@Service
public class ChargePointServiceImpl implements ChargePointService {

    @Resource
    private ChargePointDao chargePointDao;
    @Override
    public ChargePoint getChargePoint(ChargePoint chargePoint) {
        return chargePointDao.getChargePoint(chargePoint);
    }
}
