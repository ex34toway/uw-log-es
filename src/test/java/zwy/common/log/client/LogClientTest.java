package zwy.common.log.client;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang.RandomStringUtils;
import org.assertj.core.util.Lists;
import org.junit.BeforeClass;
import org.junit.Test;
import zwy.common.log.client.service.LogService;
import zwy.common.log.client.vo.LogInterface;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author liliang
 * @since 2018-04-25
 */
public class LogClientTest {

    private static LogClient logClient;

    @BeforeClass
    public static void setUp() {
        LogClientProperties logClientProperties = new LogClientProperties();
        logClient = new LogClient(new LogService(logClientProperties));
    }

    @Test
    public void testLogTestData() {
        int count = 0;
        Stopwatch stopwatch = Stopwatch.createStarted();
        while (count < 10000) {
            LogInterface logInterface = new LogInterface();
            logInterface.setInterfaceType(1);
            logInterface.setInterfaceConfigId(Long.parseLong(RandomStringUtils.randomNumeric(6)));
            logInterface.setMchId(Long.parseLong(RandomStringUtils.randomNumeric(6)));
            logInterface.setProductType(10);
            logInterface.setProductId(Long.parseLong(RandomStringUtils.randomNumeric(6)));
            logInterface.setInterfaceProductId(RandomStringUtils.randomNumeric(11));
            logInterface.setInterfaceFunction("zwy.common.log.client.logInterface");
            logInterface.setRequestDate(new Date());
            logInterface.setRequestBody("你吃饭了吗?");
            logInterface.setResponseDate(new Date());
            logInterface.setResponseBody("吃了");
            logClient.log(logInterface);
            count++;
        }
        System.out.println("elapsed wall time " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testLog() {
        LogInterface logInterface = new LogInterface();
        logInterface.setInterfaceType(1);
        logInterface.setInterfaceConfigId(Long.parseLong(RandomStringUtils.randomNumeric(6)));
        logInterface.setMchId(Long.parseLong(RandomStringUtils.randomNumeric(6)));
        logInterface.setProductType(10);
        logInterface.setProductId(Long.parseLong(RandomStringUtils.randomNumeric(6)));
        logInterface.setInterfaceProductId(RandomStringUtils.randomNumeric(11));
        logInterface.setInterfaceFunction("zwy.common.log.client.logInterface");
        logInterface.setRequestDate(new Date());
        logInterface.setRequestBody("你吃饭了吗?");
        logInterface.setResponseDate(new Date());
        logInterface.setResponseBody("吃了");
        logClient.log(logInterface);
    }

    @Test
    public void testWriteBulkLog() {

        List<LogInterface> dataList = Lists.newArrayList();
        LogInterface logInterface1 = new LogInterface();
        logInterface1.setInterfaceType(1);
        logInterface1.setInterfaceConfigId(Long.parseLong(RandomStringUtils.randomNumeric(6)));
        logInterface1.setMchId(Long.parseLong(RandomStringUtils.randomNumeric(6)));
        logInterface1.setProductType(10);
        logInterface1.setProductId(Long.parseLong(RandomStringUtils.randomNumeric(6)));
        logInterface1.setInterfaceProductId(RandomStringUtils.randomNumeric(11));
        logInterface1.setInterfaceFunction("zwy.common.log.client.logInterface");
        logInterface1.setRequestDate(new Date());
        logInterface1.setRequestBody("你吃饭了吗?");
        logInterface1.setResponseDate(new Date());
        logInterface1.setResponseBody("吃了");
        dataList.add(logInterface1);

        LogInterface logInterface2 = new LogInterface();
        logInterface2.setInterfaceType(1);
        logInterface2.setInterfaceConfigId(Long.parseLong(RandomStringUtils.randomNumeric(6)));
        logInterface2.setMchId(Long.parseLong(RandomStringUtils.randomNumeric(6)));
        logInterface2.setProductType(10);
        logInterface2.setProductId(Long.parseLong(RandomStringUtils.randomNumeric(6)));
        logInterface2.setInterfaceProductId(RandomStringUtils.randomNumeric(11));
        logInterface2.setInterfaceFunction("zwy.common.log.client.logInterface");
        logInterface2.setRequestDate(new Date());
        logInterface2.setRequestBody("你吃饭了吗?");
        logInterface2.setResponseDate(new Date());
        logInterface2.setResponseBody("吃了");
        dataList.add(logInterface2);

        logClient.bulkLog(dataList);
    }

    @Test
    public void testQueryLogBySimpleQuery() {
        List<LogInterface> dataList = logClient.simpleQueryLog(LogInterface.class,"q=productId:926270");
        dataList.size();
    }

    @Test
    public void testQueryLogByDSLQuery() {
        String dslQuery = "{" +
                "\"query\": {" +
                "\"bool\": {" +
                "\"must\": [" +
                "{\n" +
                "\"term\": {" +
                "\"productType\": 10" +
                "}\n" +
                "}\n" +
                "],\n" +
                "\"must_not\": [ ]," +
                "\"should\": [ ]" +
                "}" +
                "}," +
                "\"from\": 0," +
                "\"size\": 10," +
                "\"sort\": [\"requestDate\"]," +
                "\"aggs\": { }" +
                "}";
        List<LogInterface> dataList = logClient.dslQueryLog(LogInterface.class,dslQuery);
        dataList.size();
    }


    @Test
    public void testQueryLogBySql() {
        List<LogInterface> dataList = logClient.sqlQueryLog(LogInterface.class,
                "select * from zwy.common.log.client.vo.loginterface where responseDate > 1524666600000 limit 10 ");
        dataList.size();
    }
}
