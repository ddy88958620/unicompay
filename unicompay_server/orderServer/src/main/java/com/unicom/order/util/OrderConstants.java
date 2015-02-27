package com.unicom.order.util;

/**
 * Created by sh-zhaogx3 on 2014/8/1.
 */
public class OrderConstants {
    public static final long DEFAULT_PAGE_SIZE = 20L;
    public static final long DEFAULT_CURRENT_PAGE = 1L;

    public static final String USER_ORDER_BIZ = "user_order_biz";
    public static final String GET_USER_ORDER_BIZ = "get_user_order_biz";

    public static final String ERROR_MESSAGE_PARAM = "params not complete.";
    public static final String ORDER_SAME_PRODUCT = "the user had order this product.";
    public static final String NOT_ORDER_PRODUCT = "the user don't order this product.";
    public static final String PAY_FAILED_MESSAGE = "pay failed.";

    public static final String PARAM_IS_NULL = "param is null.";
    public static final String ORDER_IS_NULL = "order is null.";
    public static final String ERROR_STATUS = "error status.";

    public static final String SYSTEM_ERROR_MESSAGE = "system error.";

    public static final String ORDER_STATUS_INIT = "inti";
    public static final String ORDER_STATUS_CHECKED = "checked";
    public static final String ORDER_STATUS_FINISH = "finish";
    public static final String ORDER_STATUS_CANCEL = "cancel";

}
