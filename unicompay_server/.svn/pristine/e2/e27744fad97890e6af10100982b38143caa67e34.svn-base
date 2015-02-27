package com.unicom.order.interceptor;

import com.tydic.common.bean.http.respose.ErrorResponse;
import com.tydic.common.bean.http.respose.Response;
import com.tydic.common.constant.code.CodeTypeConstant;
import com.tydic.common.constant.code.ErrorCodeConstants;
import com.tydic.common.exception.InterfaceServiceException;
import com.tydic.common.interfaces.context.InterfaceContext;
import com.tydic.common.interfaces.context.InterfaceManager;
import com.tydic.common.interfaces.feature.ParseNodeNameFilter;
import com.tydic.common.interfaces.interceptors.plugins.InterceptorPlugin;
import com.tydic.utils.JsonUtil;
import com.tydic.utils.ObjectIsNull;
import com.unicom.order.util.ThreeDESCoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by sh-zhaogx3 on 2014/8/6.
 */
public class OrderInterfaceHandlerInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderInterfaceHandlerInterceptor.class);

    private String[] rexPassUrl;

    @Autowired
    private OrderLogInterceptorPlugin logInterceptorPlugin;

    private List<InterceptorPlugin> afterPlugin;

    private List<InterceptorPlugin> beforePlugin;

    public void setAfterPlugin(List<InterceptorPlugin> afterPlugin) {
        this.afterPlugin = afterPlugin;
    }

    public void setBeforePlugin(List<InterceptorPlugin> beforePlugin) {
        this.beforePlugin = beforePlugin;
    }

    public void setRexPassUrl(String[] rexPassUrl) {
        this.rexPassUrl = rexPassUrl;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception arg3) throws Exception {
        response.setHeader(CodeTypeConstant.CODE_HTTP_REQ_MSG_ID, InterfaceManager.getInterfaceContext().getMsgId());
        try {
            InterfaceManager.getInterfaceContext().setResTime(new Date());
            if (null != afterPlugin) {
                for (InterceptorPlugin plugin : afterPlugin) {
                    plugin.after(request);
                }
            }
        } finally {
            InterfaceManager.removeContext();
        }

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView arg3) throws Exception {

    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
        response.setCharacterEncoding("UTF-8");
        String encodeing = request.getHeader("Accept-Encoding");
        if(ObjectIsNull.check(encodeing)){
            response.setHeader("Content-Encoding", encodeing);
        }
        InterfaceContext context = this.bindContext(request, response);
        InterfaceManager.setInterfaceContext(context);

//        if(null!=rexPassUrl&&rexPassUrl.length>0){
//            for(String passUrl : rexPassUrl){
//
//                System.out.println(passUrl);
//            }
//        }
//        System.out.println("requestUri:" + request.getRequestURI());
        if(null!=rexPassUrl&&rexPassUrl.length>0){
            for(String passUrl : rexPassUrl){
                if(request.getRequestURI().matches(passUrl)){
                    return true;
                }
            }
        }
        try {
//			if(!ObjectIsNull.check(context.getSignature())&&!checkSignature(context)){
//				this.writeError(context, ErrorCodeConstants.HASH_SIGNATURE_ERROR, "请求非法");
//				logInterceptorPlugin.after(request);
//				return false;
//			}
            if (null != beforePlugin) {
                for (InterceptorPlugin plugin : beforePlugin) {
                    if (!plugin.before(request)) {
                        logInterceptorPlugin.after(request);
                        return false;
                    }
                }
            }
        }catch (InterfaceServiceException e) {
            LOGGER.error(e.getMessage(),e);
            this.writeError(context, e.getMessage(), e.getErrCode());
            logInterceptorPlugin.after(request);
            return false;
        }catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            this.writeError(context, "系统错误", ErrorCodeConstants.SYSTEM_ERROR_CODE);
            logInterceptorPlugin.after(request);
            return false;
        }
        return true;
    }

    protected void writeData(InterfaceContext context,String response) throws Exception {
        try {
            context.setResBody(ThreeDESCoder.decode(response));
            context.getResponse().getWriter().write(response);
        } catch (IOException e) {
            throw new IOException(e.getMessage(),e);
        }
    }

//	private boolean checkSignature(InterfaceContext context){
//		StringBuffer signatureBuffer = new StringBuffer(800);
//		signatureBuffer.append(context.getClientId()).append(context.getClientSecret()).append(context.getReqBody()).append(context.getAccessToken());
//		return Md5Utils.encodePassword(signatureBuffer.toString()).equals(context.getSignature());
//	}

    protected void writeError(InterfaceContext context,String errorCode,String errorMsg) throws Exception{
        Response response = new Response();
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(StringUtils.isBlank(errorCode) ? ErrorCodeConstants.SYSTEM_ERROR_CODE : errorCode);
        errorResponse.setErrorMsg(StringUtils.isBlank(errorMsg) ? "系统错误." : errorMsg);
        context.setErrorMsg(errorResponse.getErrorCode()+":"+errorResponse.getErrorMsg());
        context.setStas(CodeTypeConstant.INTERFACE_ERROR_CODE);
        response.setResponse(errorResponse);
        response.setResponseValue(CodeTypeConstant.CODE_SYSTEM_ERROR);
        writeData(context,ThreeDESCoder.encode(this.toJsonString(response)));
    }

    protected String toJsonString(Response obj) {
        return JsonUtil.toJSONString(obj, new ParseNodeNameFilter());
    }

    /**
     * 构造上下文
     *
     * @param request
     *            void
     */
    private InterfaceContext bindContext(HttpServletRequest request, HttpServletResponse response) throws Exception {
        InterfaceContext context = new InterfaceContext();
        context.setRequest(request);
        context.setResponse(response);
        context.setMsgId(request.getHeader("msg_id"));
        context.setReqTime(request.getHeader("req_time"));
        context.setClientId(request.getHeader("client_id"));
        context.setClientSecret(request.getHeader("client_secret"));
        context.setAccessToken(request.getHeader("access_token"));
        context.setSdkVersion(request.getHeader("sdk_version"));
        context.setCrtTime(new Date());
        if(null!=rexPassUrl&&rexPassUrl.length>0){
            for(String passUrl : rexPassUrl){
                if(request.getRequestURI().matches(passUrl)){
                    return context;
                }
            }
        }
        byte[] byteArray;
        try {
            byteArray = IOUtils.toByteArray(request.getInputStream());
            String charset = StringUtils.isNotBlank(request.getCharacterEncoding()) ? request.getCharacterEncoding() : "UTF-8";
            context.setReqBody(new String(byteArray, charset));
        } catch (Exception e) {
            throw new InterfaceServiceException("获取Bod异常",ErrorCodeConstants.SYSTEM_PARAM_CODE,CodeTypeConstant.CODE_SYSTEM_ERROR);
        }
        return context;
    }

}
