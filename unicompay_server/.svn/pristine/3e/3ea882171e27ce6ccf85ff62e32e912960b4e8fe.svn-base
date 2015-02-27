package com.unicom.tv.dao.impl;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.unicom.tv.bean.pojo.TV;
import com.unicom.tv.dao.TVDao;

@Repository
public class TVDaoImpl implements TVDao {

	@Resource(name = "sqlSession")
	private SqlSessionTemplate sqlSession;
	
	@Override
	public void addTV(TV tv) {
		sqlSession.insert("tv.addTV", tv);
	}

	@Override
	public void updateToGetCodeState(TV tv) {
		sqlSession.update("tv.updateToGetCodeState", tv);
	}
	
	@Override
	public void unbindRelationship(TV tv) {
		sqlSession.update("tv.unbindRelationship", tv);
	}
	
	@Override
	public void saveBindRelationship(TV tv) {
		sqlSession.update("tv.saveBindRelationship", tv);
	}

	@Override
	public TV getTV(TV tv) {
		return sqlSession.selectOne("tv.getTV", tv);
	}

}
