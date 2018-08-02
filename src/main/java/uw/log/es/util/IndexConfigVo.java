package uw.log.es.util;

import org.apache.commons.lang.time.FastDateFormat;

/**
 * 索引配置Vo
 *
 * @author liliang
 * @since 2018-07-27
 */
public class IndexConfigVo {

    /**
     * 索引名称
     */
    private final String index;

    /**
     * 查询索引,它通常可以是index*模式
     */
    private final String queryIndex;

    /**
     * 索引Pattern
     */
    private final FastDateFormat indexPattern;

    public IndexConfigVo(String index,String queryIndex,final FastDateFormat indexPattern) {
        this.index = index;
        this.queryIndex = queryIndex;
        this.indexPattern = indexPattern;
    }

    public String getIndex() {
        return index;
    }

    public String getQueryIndex() {
        return queryIndex;
    }

    public FastDateFormat getIndexPattern() {
        return indexPattern;
    }
}
