package com.unicom.tv.service;

import com.unicom.tv.bean.remote.request.TVBindRequest;
import com.unicom.tv.bean.remote.request.TVCodeRequest;
import com.unicom.tv.bean.remote.request.TVPayRequest;
import com.unicom.tv.bean.remote.request.TVUnbindRequest;
import com.unicom.tv.bean.remote.response.TVBindResponse;
import com.unicom.tv.bean.remote.response.TVCodeResponse;
import com.unicom.tv.bean.remote.response.TVPayResponse;
import com.unicom.tv.bean.remote.response.TVUnbindResponse;

public interface RemotePayService {

	TVCodeResponse getTVCode(TVCodeRequest request);
	
	TVBindResponse bindTV(TVBindRequest request);
	
	TVUnbindResponse unbindTV(TVUnbindRequest request);
	
	TVPayResponse payTV(TVPayRequest payRequest);
}
