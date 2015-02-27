package com.unicom.tv.service.interfaces;

import com.unicom.tv.bean.interfaces.request.BindRequest;
import com.unicom.tv.bean.interfaces.request.CheckImeiRequest;
import com.unicom.tv.bean.interfaces.request.GetCodeRequest;
import com.unicom.tv.bean.interfaces.request.GetKeyRequest;
import com.unicom.tv.bean.interfaces.request.UnbindRequest;
import com.unicom.tv.bean.interfaces.response.BindResponse;
import com.unicom.tv.bean.interfaces.response.CheckImeiResponse;
import com.unicom.tv.bean.interfaces.response.GetCodeResponse;
import com.unicom.tv.bean.interfaces.response.GetKeyResponse;
import com.unicom.tv.bean.interfaces.response.UnbindResponse;

public interface AccountService {

	CheckImeiResponse getState(CheckImeiRequest request);
	
	GetCodeResponse getCode(GetCodeRequest request);
	
	GetKeyResponse getKey(GetKeyRequest request);
	
	BindResponse bind(BindRequest request);
	
	UnbindResponse unbind(UnbindRequest request);
}
