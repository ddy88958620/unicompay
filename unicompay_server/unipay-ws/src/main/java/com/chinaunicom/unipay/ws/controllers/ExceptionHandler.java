package com.chinaunicom.unipay.ws.controllers;

import com.chinaunicom.unipay.ws.persistence.Order;
import com.chinaunicom.unipay.ws.services.PointException;
import com.chinaunicom.unipay.ws.services.UnipayException;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Frank
 * Date: 2014/10/30
 * Time: 17:29
 */
public class ExceptionHandler implements Interceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void intercept(ActionInvocation ai) {

        Response rsp;
        try {
            ai.invoke();
            return;
        } catch (ReplayException e) {
            logger.warn(e.getMessage() + "-->" + e.getData(), e);
            rsp = new Response(399, e.getMessage());
        } catch (UnipayException e) {
            Order order = Order.dao.findById(e.getOrderid());
            if(order != null) {
                order.setPayresult(e.getCode()).setStatus(1).update();
            }
            logger.warn(e.getMessage() + ", orderid: " + e.getOrderid(), e);
            rsp = new Response(Integer.parseInt(e.getCode()), e.getMessage());
        }catch (PointException e) {
            Order order = Order.dao.findById(e.getData().getUuid());
            if(order != null) {
                order.setPayresult(String.valueOf(e.getData().getResult())).setStatus(1).update();
            }
            logger.warn(e.getMessage() + ", orderid: " + e.getData().getUuid(), e);
            rsp = new PointController.PointResponse(e.getData().getResult(), e.getData().getUuid());
        }catch (PointException.Data e) {
            Order order = Order.dao.findById(e.getUuid());
            if(order != null) {
                order.setPayresult(String.valueOf(e.getResult())).setStatus(1).update();
            }
            logger.warn(e.getMessage() + ", orderid: " + e.getUuid(), e);
            rsp = new PointController.PointResponse.Data(e.getResult(), e.getUuid());
        } catch (Exception e) {
            logger.error("系统内部错误！", e);
            rsp = new Response(399, e.getMessage());
        }

        ai.getController().renderJson(rsp);

    }
}
