package com.unicom.tv.service.interfaces;

import com.unicom.tv.bean.interfaces.request.PayRequest;
import com.unicom.tv.bean.interfaces.response.PayResponse;

public interface PayService {

	PayResponse pay(PayRequest request);
}
