package com.chinaunicom.unipay.ws.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Administrator on 2015/1/26 0026.
 */
public interface IAliPayService {

    //获取二维码
    public AliResponse getQrcode(AliPay pay) throws Exception;

    //用户下单通知
    public NotifyResponse returnNotify(Map param,String charset) throws Exception;

    public static class CallbackResponse {
        private String success;

        public String getSuccess() {
            return success;
        }

        public void setSuccess(String success) {
            this.success = success;
        }

    }

    public static class NotifyData{

        private String partner;
        private String qrcode;
        //商品名称
        private String subject;
        //支付宝交易号
        private String trade_no;
        //商户网站唯一订单号
        private String out_trade_no;
        //交易总金额
        private String total_fee;
        //交易状态
        private String trade_status;
        //买家支付宝账号
        private String buyer_email;
        //交易创建时间 yyyy-MM-dd HH:mm:ss
        private String gmt_create;
        //交易付款时间 yyyy-MM-dd HH:mm:ss
        private String gmt_payment;

        public String getPartner() {
            return partner;
        }

        public void setPartner(String partner) {
            this.partner = partner;
        }

        public String getQrcode() {
            return qrcode;
        }

        public void setQrcode(String qrcode) {
            this.qrcode = qrcode;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getTrade_no() {
            return trade_no;
        }

        public void setTrade_no(String trade_no) {
            this.trade_no = trade_no;
        }

        public String getOut_trade_no() {
            return out_trade_no;
        }

        public void setOut_trade_no(String out_trade_no) {
            this.out_trade_no = out_trade_no;
        }

        public String getTotal_fee() {
            return total_fee;
        }

        public void setTotal_fee(String total_fee) {
            this.total_fee = total_fee;
        }

        public String getTrade_status() {
            return trade_status;
        }

        public void setTrade_status(String trade_status) {
            this.trade_status = trade_status;
        }

        public String getBuyer_email() {
            return buyer_email;
        }

        public void setBuyer_email(String buyer_email) {
            this.buyer_email = buyer_email;
        }

        public String getGmt_create() {
            return gmt_create;
        }

        public void setGmt_create(String gmt_create) {
            this.gmt_create = gmt_create;
        }

        public String getGmt_payment() {
            return gmt_payment;
        }

        public void setGmt_payment(String gmt_payment) {
            this.gmt_payment = gmt_payment;
        }

        public boolean isSuccess() throws Exception{
            return "TRADE_FINISHED".equals(trade_status) || "TRADE_SUCCESS".equals(trade_status);
        }
    }

    public static class NotifyResponse{

        private String out_trade_no;
        private String is_success;
        private String error_code;

        public String getOut_trade_no() {
            return out_trade_no;
        }

        public void setOut_trade_no(String out_trade_no) {
            this.out_trade_no = out_trade_no;
        }

        public String getIs_success() {
            return is_success;
        }

        public void setIs_success(String is_success) {
            this.is_success = is_success;
        }

        public String getError_code() {
            return error_code;
        }

        public void setError_code(String error_code) {
            this.error_code = error_code;
        }

        @JsonIgnore
        public boolean isSuccess() {
            return "T".equals(is_success);
        }

    }

    public static class NotifyPay {
        //买家支付宝账号对应的支付宝唯一用户号。以2088开头的纯16位数字
        private String user_id;
        //二维码
        private String qrcode;
        //用户下单的商品编号
        private String goods_id;
        //用户下单的商品名称
        private String goods_name;
        //用户购买指定商品的数量
        private String quantity;
        //用户购买商品的总价，单位元，精确到小数点后2位
        private String price;
        //通常表示规格、颜色、款式等
        private String sku_id;
        //商品属性名称
        private String sku_name;
        //只有个别支付宝特约商户才需要处理该返回值，其他商户不需要解析该字段
        private String context_data;

        //收货人所在省份
        private String prov;
        //收货人所在城市
        private String city;
        //收货人所在县区名称
        private String area;
        //收货人详细地址
        private String address;
        //收货人姓名
        private String buyer_name;
        //收货人地址的邮政编码
        private String post_code;
        //收货人联系电话
        private String phone;

        //签名
        private String sign;
        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getQrcode() {
            return qrcode;
        }

        public void setQrcode(String qrcode) {
            this.qrcode = qrcode;
        }

        public String getGoods_id() {
            return goods_id;
        }

        public void setGoods_id(String goods_id) {
            this.goods_id = goods_id;
        }

        public String getGoods_name() {
            return goods_name;
        }

        public void setGoods_name(String goods_name) {
            this.goods_name = goods_name;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getSku_id() {
            return sku_id;
        }

        public void setSku_id(String sku_id) {
            this.sku_id = sku_id;
        }

        public String getSku_name() {
            return sku_name;
        }

        public void setSku_name(String sku_name) {
            this.sku_name = sku_name;
        }

        public String getContext_data() {
            return context_data;
        }

        public void setContext_data(String context_data) {
            this.context_data = context_data;
        }

        public String getProv() {
            return prov;
        }

        public void setProv(String prov) {
            this.prov = prov;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getBuyer_name() {
            return buyer_name;
        }

        public void setBuyer_name(String buyer_name) {
            this.buyer_name = buyer_name;
        }

        public String getPost_code() {
            return post_code;
        }

        public void setPost_code(String post_code) {
            this.post_code = post_code;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

    }

    public static class ResponseKey{
        public String name;
        @JacksonXmlText
        public String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class AliPay{

        //接口名称 alipay.mobile.qrcode.manage
        private String service;
        //合作者身份ID
        private String partner;
        //参数编码字符集
        private String _input_charset;
        //sign_type MD5
        private String sign_type;
        //签名
        private String sign;
        //接口调用时间
        private String timestamp;
        //动作 add modify stop restart
        private String method;
        //二维码 “https://qr.alipay.com/”开头，加上一串字符串。
        private String qrcode;
        //业务类型 10：商品码；9：商户码（友宝售货机码），友宝目前只支持9；11：链接码；12：链接码（预授权业务）。
        private String biz_type;
        //业务数据
        private BizData biz_data;

        public static class BizData{

            //交易类型 1：即时到账 2：担保交易 当本参数设置为2时，need_address必须为T。
            private String trade_type;
            //是否需要收货地址 T：需要 F：不需要
            private String need_address;
            //商品明细
            private GoodsInfo goods_info;
            //通知商户下单URL
            private String return_url;
            //通知商户支付结果url
            private String notify_url;
            //查询商品信息url
            private String query_url;
            //扩展属性
            private ExtInfo ext_info;
            //备注
            private String memo;
            //链接地址
            private String url;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getTrade_type() {
                return trade_type;
            }

            public void setTrade_type(String trade_type) {
                this.trade_type = trade_type;
            }

            public String getNeed_address() {
                return need_address;
            }

            public void setNeed_address(String need_address) {
                this.need_address = need_address;
            }

            public GoodsInfo getGoods_info() {
                return goods_info;
            }

            public void setGoods_info(GoodsInfo goods_info) {
                this.goods_info = goods_info;
            }

            public String getReturn_url() {
                return return_url;
            }

            public void setReturn_url(String return_url) {
                this.return_url = return_url;
            }

            public String getNotify_url() {
                return notify_url;
            }

            public void setNotify_url(String notify_url) {
                this.notify_url = notify_url;
            }

            public String getQuery_url() {
                return query_url;
            }

            public void setQuery_url(String query_url) {
                this.query_url = query_url;
            }

            public ExtInfo getExt_info() {
                return ext_info;
            }

            public void setExt_info(ExtInfo ext_info) {
                this.ext_info = ext_info;
            }

            public String getMemo() {
                return memo;
            }

            public void setMemo(String memo) {
                this.memo = memo;
            }
            public static class GoodsInfo{

                //商品编号
                private String id;
                //商品名称
                private String name;
                //商品价格
                private String price;
                //商品总库存
                private String inventory;
                //商品属性标题
                private String sku_title;
                //商品属性
                private List<Sku> sku;
                //商品有效期
                private String expiry_date;
                //商品描述
                private String desc;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getPrice() {
                    return price;
                }

                public void setPrice(String price) {
                    this.price = price;
                }

                public String getInventory() {
                    return inventory;
                }

                public void setInventory(String inventory) {
                    this.inventory = inventory;
                }

                public String getSku_title() {
                    return sku_title;
                }

                public void setSku_title(String sku_title) {
                    this.sku_title = sku_title;
                }

                public List<Sku> getSku() {
                    return sku;
                }

                public void setSku(List<Sku> sku) {
                    this.sku = sku;
                }

                public String getExpiry_date() {
                    return expiry_date;
                }

                public void setExpiry_date(String expiry_date) {
                    this.expiry_date = expiry_date;
                }

                public String getDesc() {
                    return desc;
                }

                public void setDesc(String desc) {
                    this.desc = desc;
                }

                public static class Sku{

                    private String sku_id;
                    private String sku_name;
                    private String sku_price;
                    private String sku_inventory;

                    public String getSku_id() {
                        return sku_id;
                    }

                    public void setSku_id(String sku_id) {
                        this.sku_id = sku_id;
                    }

                    public String getSku_name() {
                        return sku_name;
                    }

                    public void setSku_name(String sku_name) {
                        this.sku_name = sku_name;
                    }

                    public String getSku_price() {
                        return sku_price;
                    }

                    public void setSku_price(String sku_price) {
                        this.sku_price = sku_price;
                    }

                    public String getSku_inventory() {
                        return sku_inventory;
                    }

                    public void setSku_inventory(String sku_inventory) {
                        this.sku_inventory = sku_inventory;
                    }
                }
            }
            public static class ExtInfo{

                //单次购买上限
                private String single_limit;
                //单用户购买上限
                private String user_limit;
                //支付超时时间
                private String pay_timeout;
                //二维码logo名称
                private String logo_name;
                //自定义收集用户信息扩展字段
                private String ext_field;

                public String getSingle_limit() {
                    return single_limit;
                }

                public void setSingle_limit(String single_limit) {
                    this.single_limit = single_limit;
                }

                public String getUser_limit() {
                    return user_limit;
                }

                public void setUser_limit(String user_limit) {
                    this.user_limit = user_limit;
                }

                public String getPay_timeout() {
                    return pay_timeout;
                }

                public void setPay_timeout(String pay_timeout) {
                    this.pay_timeout = pay_timeout;
                }

                public String getLogo_name() {
                    return logo_name;
                }

                public void setLogo_name(String logo_name) {
                    this.logo_name = logo_name;
                }

                public String getExt_field() {
                    return ext_field;
                }

                public void setExt_field(String ext_field) {
                    this.ext_field = ext_field;
                }
            }
        }


        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getPartner() {
            return partner;
        }

        public void setPartner(String partner) {
            this.partner = partner;
        }

        public String get_input_charset() {
            return _input_charset;
        }

        public void set_input_charset(String _input_charset) {
            this._input_charset = _input_charset;
        }

        public String getSign_type() {
            return sign_type;
        }

        public void setSign_type(String sign_type) {
            this.sign_type = sign_type;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getQrcode() {
            return qrcode;
        }

        public void setQrcode(String qrcode) {
            this.qrcode = qrcode;
        }

        public String getBiz_type() {
            return biz_type;
        }

        public void setBiz_type(String biz_type) {
            this.biz_type = biz_type;
        }

        public BizData getBiz_data() {
            return biz_data;
        }

        public void setBiz_data(BizData biz_data) {
            this.biz_data = biz_data;
        }
    }

    public class AliResponse {
        private String is_success;
        private String error;
        private Request request;

        private Response response;

        private String sign;

        private String sign_type;

        public String getIs_success() {
            return is_success;
        }

        public void setIs_success(String is_success) {
            this.is_success = is_success;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public Request getRequest() {
            return request;
        }

        public void setRequest(Request request) {
            this.request = request;
        }

        public Response getResponse() {
            return response;
        }

        public void setResponse(Response response) {
            this.response = response;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getSign_type() {
            return sign_type;
        }

        public void setSign_type(String sign_type) {
            this.sign_type = sign_type;
        }

        public static class Request{
            @JacksonXmlElementWrapper(useWrapping = false)
            private List<ResponseKey> param;

            public List<ResponseKey> getParam() {
                return param;
            }

            public void setParam(List<ResponseKey> param) {
                this.param = param;
            }
        }

        public static class Response{
            private Alipay alipay;

            public Alipay getAlipay() {
                return alipay;
            }

            public void setAlipay(Alipay alipay) {
                this.alipay = alipay;
            }

            public static class Alipay{
                private String qrcode;
                private String qrcode_img_url;
                private String result_code;
                private String error_message;

                public String getQrcode() {
                    return qrcode;
                }

                public void setQrcode(String qrcode) {
                    this.qrcode = qrcode;
                }

                public String getQrcode_img_url() {
                    return qrcode_img_url;
                }

                public void setQrcode_img_url(String qrcode_img_url) {
                    this.qrcode_img_url = qrcode_img_url;
                }

                public String getResult_code() {
                    return result_code;
                }

                public void setResult_code(String result_code) {
                    this.result_code = result_code;
                }

                public String getError_message() {
                    return error_message;
                }

                public void setError_message(String error_message) {
                    this.error_message = error_message;
                }

                public boolean isSuccess(){
                    return "SUCCESS".equals(result_code);
                }
            }

        }

        public boolean isSucess() throws Exception{
            return "T".equals(is_success);
        }

    }


}
