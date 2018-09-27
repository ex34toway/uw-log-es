package uw.log.es;

import org.junit.BeforeClass;
import org.junit.Test;
import uw.httpclient.http.ObjectMapper;
import uw.log.es.service.LogService;
import uw.log.es.vo.LogInterface;
import uw.log.es.vo.LogInterfaceOrder;
import uw.log.es.vo.SearchResponse;


/**
 * @author liliang
 * @since 2018-07-27
 */
public class LogClientDSLTest {

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
    public void testDslResultParse() {
        String result = "{\"took\":3,\"timed_out\":false,\"_shards\":{\"total\":75,\"successful\":75,\"skipped\":0,\"failed\":0},\"hits\":{\"total\":16862329,\"max_score\":0,\"hits\":[]},\"aggregations\":{\"refId\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":10002,\"doc_count\":10794056,\"total\":{\"value\":10794056}},{\"key\":10003,\"doc_count\":4359901,\"total\":{\"value\":4359901}},{\"key\":0,\"doc_count\":1708372,\"total\":{\"value\":1708372}}]}}}";
        SearchResponse<Object> response = null;
        try {
            response = ObjectMapper.DEFAULT_JSON_MAPPER.parse(result,
                    ObjectMapper.DEFAULT_JSON_MAPPER
                            .constructParametricType(SearchResponse.class, Object.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(response);
    }
}
