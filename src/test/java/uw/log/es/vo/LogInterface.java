package uw.log.es.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * 一般接口日志Vo
 *
 * @author liliang
 * @since 2018-04-25
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogInterface extends LogBaseVo implements Serializable {
    private static final long serialVersionUID = -6488666771783357081L;

    public static LogInterface init(int interfaceType, int interfaceConfigId, long saasId, int productType, long productId,
                                    String interfaceProductId, String interfaceFunction) {
        LogInterface logInterface = new LogInterface();
        logInterface.setInterfaceType(interfaceType);
        logInterface.setInterfaceConfigId(interfaceConfigId);
        logInterface.setSaasId(saasId);
        logInterface.setProductType(productType);
        logInterface.setProductId(productId);
        logInterface.setInterfaceProductId(interfaceProductId);
        logInterface.setInterfaceFunction(interfaceFunction);
        return logInterface;
    }

    public LogInterface request(String requestBody) {
        setRequestBody(requestBody);
        setRequestDate(new java.util.Date());
        return this;
    }

    public LogInterface response(String responseBody, int success) {
        setResponseBody(responseBody);
        setResponseDate(new java.util.Date());
        setStatus(success);
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
     * 运营商ID
     */
    private long saasId = -1;

    /**
     * 系统产品类型
     */
    private int productType = -1;

    /**
     * 产品ID
     */
    private long productId = -1;

    /**
     * 接口产品ID
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
     * @return the mchId
     * @return
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
