package uw.log.es.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * 订单接口日志Vo
 *
 * @author liliang
 * @since 2018-04-25
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogInterfaceOrder extends LogBaseVo implements Serializable {
    private static final long serialVersionUID = 6795821538602784086L;

    public static LogInterfaceOrder init(int interfaceType, long interfaceConfigId,
                                         long orderId, long saasId, long mchId,
                                         long productId, String interfaceProductId, String interfaceFunction) {
        LogInterfaceOrder logInterfaceOrder = new LogInterfaceOrder();
        logInterfaceOrder.setInterfaceType(interfaceType);
        logInterfaceOrder.setInterfaceConfigId(interfaceConfigId);
        logInterfaceOrder.setOrderId(orderId);
        logInterfaceOrder.setSaasId(saasId);
        logInterfaceOrder.setMchId(mchId);
        logInterfaceOrder.setProductId(productId);
        logInterfaceOrder.setInterfaceProductId(interfaceProductId);
        logInterfaceOrder.setInterfaceFunction(interfaceFunction);
        return logInterfaceOrder;
    }

    public LogInterfaceOrder request(String requestBody) {
        setRequestBody(requestBody);
        setRequestDate(new java.util.Date());
        return this;
    }

    public LogInterfaceOrder response(String responseBody,int status) {
        setResponseBody(responseBody);
        setResponseDate(new java.util.Date());
        setStatus(status);
        return this;
    }

    /**
     * 接口类型.1供应商 2渠道商
     */
    private int interfaceType = -1;

    /**
     * 接口编号
     */
    private long interfaceConfigId = -1;

    /**
     * 系统订单号
     */
    private long orderId = -1;

    /**
     * 供应商订单号
     */
    private String interfaceOrderId;

    /**
     * 运营商ID
     */
    private long saasId = -1;

    /**
     * 分销商ID
     */
    private long mchId = -1;

    /**
     * 系统产品类型
     */
    private int productType = -1;

    /**
     * 系统产品ID
     */
    private long productId = -1;

    /**
     * 对接产品ID
     */
    private String interfaceProductId;

    /**
     * 请求接口类型
     */
    private String interfaceFunction;

    /**
     * 请求数据
     */
    private String requestBody;

    /**
     * 返回数据
     */
    private String responseBody;

    /**
     * 请求时间
     */
    private java.util.Date requestDate;

    /**
     * 接收时间
     */
    private java.util.Date responseDate;

    /**
     * 状态。1成功 -1失败
     */
    private int status = -1;

    /**
     * @return the interfaceType
     */
    public int getInterfaceType() {
        return interfaceType;
    }

    /**
     * @param interfaceType
     *            the interfaceType to set
     */
    public void setInterfaceType(int interfaceType) {
        this.interfaceType = interfaceType;
    }

    /**
     * @return the interfaceConfigId
     */
    public long getInterfaceConfigId() {
        return interfaceConfigId;
    }

    /**
     * @param interfaceConfigId
     *            the interfaceConfigId to set
     */
    public void setInterfaceConfigId(long interfaceConfigId) {
        this.interfaceConfigId = interfaceConfigId;
    }

    /**
     * @return the orderId
     */
    public long getOrderId() {
        return orderId;
    }

    /**
     * @param orderId
     *            the orderId to set
     */
    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    /**
     * @return the interfaceOrderId
     */
    public String getInterfaceOrderId() {
        return interfaceOrderId;
    }

    /**
     * @param interfaceOrderId
     *            the interfaceOrderId to set
     */
    public void setInterfaceOrderId(String interfaceOrderId) {
        this.interfaceOrderId = interfaceOrderId;
    }

    /**
     * @return the saasId
     */
    public long getSaasId() {
        return saasId;
    }

    /**
     * @param saasId
     *            the saasId to set
     */
    public void setSaasId(long saasId) {
        this.saasId = saasId;
    }

    /**
     * @return the mchId
     */
    public long getMchId() {
        return mchId;
    }

    /**
     * @param mchId
     *            the mchId to set
     */
    public void setMchId(long mchId) {
        this.mchId = mchId;
    }

    /**
     * @return the productType
     */
    public int getProductType() {
        return productType;
    }

    /**
     * @param productType
     *            the productType to set
     */
    public void setProductType(int productType) {
        this.productType = productType;
    }

    /**
     * @return the productId
     */
    public long getProductId() {
        return productId;
    }

    /**
     * @param productId
     *            the productId to set
     */
    public void setProductId(long productId) {
        this.productId = productId;
    }

    /**
     * @return the interfaceProductId
     */
    public String getInterfaceProductId() {
        return interfaceProductId;
    }

    /**
     * @param interfaceProductId
     *            the interfaceProductId to set
     */
    public void setInterfaceProductId(String interfaceProductId) {
        this.interfaceProductId = interfaceProductId;
    }

    /**
     * @return the interfaceFunction
     */
    public String getInterfaceFunction() {
        return interfaceFunction;
    }

    /**
     * @param interfaceFunction
     *            the interfaceFunction to set
     */
    public void setInterfaceFunction(String interfaceFunction) {
        this.interfaceFunction = interfaceFunction;
    }

    /**
     * @return the requestBody
     */
    public String getRequestBody() {
        return requestBody;
    }

    /**
     * @param requestBody
     *            the requestBody to set
     */
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    /**
     * @return the responseBody
     */
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * @param responseBody
     *            the responseBody to set
     */
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    /**
     * @return the requestDate
     */
    public java.util.Date getRequestDate() {
        return requestDate;
    }

    /**
     * @param requestDate
     *            the requestDate to set
     */
    public void setRequestDate(java.util.Date requestDate) {
        this.requestDate = requestDate;
    }

    /**
     * @return the responseDate
     */
    public java.util.Date getResponseDate() {
        return responseDate;
    }

    /**
     * @param responseDate
     *            the responseDate to set
     */
    public void setResponseDate(java.util.Date responseDate) {
        this.responseDate = responseDate;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

}
