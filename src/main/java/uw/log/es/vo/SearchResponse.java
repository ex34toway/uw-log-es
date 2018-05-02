package uw.log.es.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.ArrayList;

/**
 * 查询返回的Response
 *
 * @author liliang
 * @since 2018-04-25
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResponse<T> {

    /**
     *
     */
    private Shards shards = new Shards();

    /**
     *
     */
    private HitsResponse<T> hitsResponse = new HitsResponse<T>();

    /**
     * false
     */
    private boolean timedOut;

    /**
     * 1
     */
    private int took;

    public void setShards(Shards shards) {
        this.shards = shards;
    }

    @JsonProperty("_shards")
    public Shards getShards (){
        return this.shards;
    }

    public void setHisResponse(HitsResponse<T> hitsResponse) {
        this.hitsResponse = hitsResponse;
    }

    @JsonProperty("hits")
    public HitsResponse getHisResponse () {
        return this.hitsResponse;
    }

    public void setTimedOut(boolean timedOut) {
        this.timedOut = timedOut;
    }

    @JsonProperty("timed_out")
    public boolean getTimedOut () {
        return this.timedOut;
    }

    public void setTook(int took) {
        this.took = took;
    }

    @JsonProperty("took")
    public int getTook (){
        return this.took;
    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Shards {
        /**
         * 5
         */
        private String total;

        /**
         * 0
         */
        private String failed;

        /**
         * 5
         */
        private String successful;

        /**
         * 0
         */
        private String skipped;

        public void setTotal(String total) {
            this.total = total;
        }

        @JsonProperty("total")
        public String getTotal (){
            return this.total;
        }

        public void setFailed(String failed) {
            this.failed = failed;
        }

        @JsonProperty("failed")
        public String getFailed (){
            return this.failed;
        }

        public void setSuccessful(String successful) {
            this.successful = successful;
        }

        @JsonProperty("successful")
        public String getSuccessful (){
            return this.successful;
        }

        public void setSkipped(String skipped) {
            this.skipped = skipped;
        }

        @JsonProperty("skipped")
        public String getSkipped (){
            return this.skipped;
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hits <T> {
        /**
         * 索引
         */
        private String index;

        /**
         * 类型
         */
        private String type;

        /**
         * 记录
         */
        private T source;

        /**
         * 主键
         */
        private String id;

        /**
         * 1.0
         */
        private String score;

        public void setIndex(String index) {
            this.index = index;
        }

        @JsonProperty("_index")
        public String getIndex (){
            return this.index;
        }

        public void setType(String type) {
            this.type = type;
        }

        @JsonProperty("_type")
        public String getType (){
            return this.type;
        }

        public void setSource(T source) {
            this.source = source;
        }

        @JsonProperty("_source")
        public T getSource (){
            return this.source;
        }

        public void setId(String id) {
            this.id = id;
        }

        @JsonProperty("_id")
        public String getId (){
            return this.id;
        }

        public void setScore(String score) {
            this.score = score;
        }

        @JsonProperty("_score")
        public String getScore () {
            return this.score;
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HitsResponse <T> {
        /**
         *
         */
        private List<Hits<T>> hits = new ArrayList<Hits<T>>();

        /**
         * 1
         */
        private int total;

        /**
         * 1.0
         */
        private String maxScore;

        public void setHits(List<Hits<T>> hits) {
            this.hits = hits;
        }

        @JsonProperty("hits")
        public List<Hits<T>> getHits (){
            return this.hits;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        @JsonProperty("total")
        public int getTotal (){
            return this.total;
        }

        public void getMaxScore(String maxScore) {
            this.maxScore = maxScore;
        }

        @JsonProperty("max_score")
        public String getMaxScore (){
            return this.maxScore;
        }
    }
}