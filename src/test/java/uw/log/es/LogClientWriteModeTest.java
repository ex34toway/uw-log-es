package uw.log.es;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import uw.log.es.service.LogService;
import uw.log.es.vo.LogInterface;
import uw.log.es.vo.LogInterfaceOrder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author liliang
 * @since 2018-07-27
 */
public class LogClientWriteModeTest {

    private static LogClient logClient;

    @BeforeClass
    public static void setUpTest() {
        LogClientProperties logClientProperties = new LogClientProperties();
        LogClientProperties.EsConfig esConfig = new LogClientProperties.EsConfig();
        esConfig.setClusters("http://localhost:9200");
        esConfig.setMode(LogClientProperties.LogMode.READ_WRITE);
        esConfig.setAppInfoOverwrite(false);
        esConfig.setMaxFlushInMilliseconds(1000);
        esConfig.setMaxBytesOfBatch(5*1024*1024);
        esConfig.setMaxBatchThreads(5);
        logClientProperties.setEs(esConfig);
        logClient = new LogClient(new LogService(logClientProperties,null,null));
        logClient.regLogObjectWithIndexPattern(LogInterface.class,"_yyyy-MM");
        logClient.regLogObjectWithIndexPattern(LogInterfaceOrder.class,"_yyyy-MM");
    }

    @Test
    public void testWriteLog() throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i = 0 ; i< 1000; i++) {
            LogInterface logInterface = new LogInterface();
            logInterface.setInterfaceType(1);
            logInterface.setInterfaceConfigId(Long.parseLong(RandomStringUtils.randomNumeric(6)));
            logInterface.setSaasId(Long.parseLong(RandomStringUtils.randomNumeric(6)));
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
        System.out.println("----"+stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }


    @AfterClass
    public static void tearDownTest() {
        logClient.destroyLog();
    }
}
