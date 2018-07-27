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
    private String index;

    /**
     * 索引Pattern
     */
    private FastDateFormat indexPattern;

    public IndexConfigVo(String index, FastDateFormat indexPattern) {
        this.index = index;
        this.indexPattern = indexPattern;
    }

    public String getIndex() {
        return index;
    }

    public FastDateFormat getIndexPattern() {
        return indexPattern;
    }
}
