package com.unicom.tv.dao.impl;

import com.unicom.tv.bean.pojo.ChargePoint;
import com.unicom.tv.dao.ChargePointDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * Created by zhushuai on 2015/1/27.
 */
@Repository
public class ChargePointDaoImpl implements ChargePointDao {

    @Resource(name = "sqlSession")
    private SqlSessionTemplate sqlSession;

    @Override
    public ChargePoint getChargePoint(ChargePoint chargePoint) {

        return sqlSession.selectOne("chargePoint.getChargePoint",chargePoint);
    }
}
