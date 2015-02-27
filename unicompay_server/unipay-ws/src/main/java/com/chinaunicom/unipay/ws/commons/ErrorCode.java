package com.chinaunicom.unipay.ws.commons;

/**
 * Created by 兵 on 2015/1/8.
 */
public enum ErrorCode {

    //19PAY
     PAY19_CODE_(""),
     PAY19_CODE_00002("701"),
     PAY19_CODE_10014("702"),
     PAY19_CODE_10016("703"),
     PAY19_CODE_10029("704"),
     PAY19_CODE_10030("705"),
     PAY19_CODE_10031("706"),
     PAY19_CODE_10062("707"),
     PAY19_CODE_10076("708"),
     PAY19_CODE_10082("709"),
     PAY19_CODE_10083("710"),
     PAY19_CODE_10091("711"),
     PAY19_CODE_10110("712"),
     PAY19_CODE_10118("713"),
     PAY19_CODE_10119("714"),
     PAY19_CODE_10120("715"),
     PAY19_CODE_10123("716"),
     PAY19_CODE_10124("717"),
     PAY19_CODE_10131("718"),
     PAY19_CODE_11111("719"),
     PAY19_CODE_81000("720"),
     PAY19_CODE_81001("721"),
     PAY19_CODE_81006("722"),
     PAY19_CODE_81007("723"),
     PAY19_CODE_82019("724"),
     PAY19_CODE_82009("725"),
     PAY19_CODE_40000("726"),

    PAY19_ERROR_CODE_(""),
    PAY19_ERROR_CODE_00000("支付成功!"),

    PAY19_ERROR_CODE_00002("支付失败！您选择的充值卡面值与实际面值不符。"),
    PAY19_ERROR_CODE_10014("金额格式异常"),
    PAY19_ERROR_CODE_10016("该订单支付已成功，不能重复提交"),
    PAY19_ERROR_CODE_10029("运营商系统维护，支付通道暂时关闭"),
    PAY19_ERROR_CODE_10030("运营商系统维护，该面值暂时关闭"),
    PAY19_ERROR_CODE_10031("商户没有开通此支付通道"),
    PAY19_ERROR_CODE_10062("商户不支持余额卡支付"),
    PAY19_ERROR_CODE_10076("该订单支付已失败，不能重复提交"),
    PAY19_ERROR_CODE_10082("支付失败，该卡为失效卡，已锁卡。"),
    PAY19_ERROR_CODE_10083("很抱歉！该卡已连续二次支付不成功，请更换其他充值卡支付。"),
    PAY19_ERROR_CODE_10091("该卡正在处理中，请不要重复提交"),
    PAY19_ERROR_CODE_10110("系统忙，请稍后再试"),
    PAY19_ERROR_CODE_10118("商户不支持多卡支付"),
    PAY19_ERROR_CODE_10119("充值卡面额选择错误"),
    PAY19_ERROR_CODE_10120("该订单正在处理中，不能重复提交"),
    PAY19_ERROR_CODE_10123("输入参数有误"),
    PAY19_ERROR_CODE_10124("运营商系统临时维护，该省充值卡暂时无法支付，可继续提交，且不影响其它省充值卡支付。"),
    PAY19_ERROR_CODE_10131("余额卡余额不足"),
    PAY19_ERROR_CODE_11111("MD5验证失败或订单参数有误。"),
    PAY19_ERROR_CODE_81000("充值卡已失效"),
    PAY19_ERROR_CODE_81001("运营商处理失败，该卡可再次提交"),
    PAY19_ERROR_CODE_81006("充值卡金额不足以支付订单"),
    PAY19_ERROR_CODE_81007("无效的卡号密码"),
    PAY19_ERROR_CODE_82019("暂不支持该卡支付"),
    PAY19_ERROR_CODE_82009("卡号密码加解密失败"),
    PAY19_ERROR_CODE_40000("运营商正在处理中"),

    //POINT
    POINT_CODE_(""),

    POINT_CODE_1001("601"),

    POINT_CODE_1101("602"),

    POINT_CODE_1114("603"),

    POINT_CODE_2001("604"),

    POINT_CODE_2002("605"),

    POINT_CODE_2003("606"),

    POINT_CODE_1996("607"),

    POINT_CODE_1997("608"),

    POINT_CODE_1998("609"),

    POINT_CODE_1999("610"),


    POINT_CODE_200001("611"),

    POINT_CODE_300001("612"),

    POINT_ERROR_CODE_200001("查无此计费点信息"),

    POINT_ERROR_CODE_300001("关卡消费过的用户"),

    //ALIQRCODE
    ALIQRCODE_(""),
    ALIQRCODE_AE_BARCODE_PARAMETER_IS_NULL("802"),
    ALIQRCODE_AE_BARCODE_PARTNER_INFO_IS_NULL("803"),
    ALIQRCODE_AE_BARCODE_BIZTYPE_ILLEGAL("804"),
    ALIQRCODE_AE_BARCODE_MOUNT_FORMAT_ERROR("805"),
    ALIQRCODE_AE_BARCODE_GENERAL_CODE_FAIL("806"),
    ALIQRCODE_AE_BARCODE_OPERATE_CODE_FAIL("807"),
    ALIQRCODE_AE_BARCODE_PARAMETER_ILLEGAL("808"),
    ALIQRCODE_ILLEGAL_PARTNER("809"),
    ALIQRCODE_ILLEGAL_SIGN("810"),
    ALIQRCODE_ILLEGAL_DYN_MD5_KEY("811"),
    ALIQRCODE_ILLEGAL_ENCRYPT("812"),
    ALIQRCODE_ILLEGAL_ARGUMENT("813");


    private String value;
    
    private ErrorCode(String value){this.value = value;}

    public String getValue() {
        return value;
    }

    public static int getCode(Enum type,String srccode){
        return Integer.parseInt(ErrorCode.valueOf(type+srccode).getValue());
    }
    public static String getMsg(Enum type,String srccode){
        return ErrorCode.valueOf(type+srccode).getValue();
    }
}
