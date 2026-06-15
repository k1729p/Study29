package kp.clients.redis;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.json.Path2;
import redis.clients.jedis.search.FTCreateParams;
import redis.clients.jedis.search.IndexDataType;
import redis.clients.jedis.search.SearchResult;
import redis.clients.jedis.search.aggr.AggregationBuilder;
import redis.clients.jedis.search.aggr.AggregationResult;
import redis.clients.jedis.search.aggr.Reducers;
import redis.clients.jedis.search.schemafields.NumericField;
import redis.clients.jedis.search.schemafields.SchemaField;
import redis.clients.jedis.search.schemafields.TextField;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Probabilistic {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    /**
     *
     */
    public static void indexAndQueryDocuments(RedisClient redisClient) {
        JSONObject user1 = new JSONObject()
                .put("name", "Paul John").put("email", "paul.john@example.com").put("age", 42).put("city", "London");
        JSONObject user2 = new JSONObject()
                .put("name", "Eden Zamir").put("email", "eden.zamir@example.com").put("age", 29).put("city", "Tel Aviv");
        JSONObject user3 = new JSONObject()
                .put("name", "Paul Zamir").put("email", "paul.zamir@example.com").put("age", 35).put("city", "Tel Aviv");
        try {
            redisClient.ftDropIndex("idx:users");
        } catch (JedisDataException j) {
            // ignore
        }
        redisClient.del("user:1", "user:2", "user:3");
        SchemaField[] schema = {
                TextField.of("$.name").as("name"),
                TextField.of("$.city").as("city"),
                NumericField.of("$.age").as("age")
        };
        String createResult = redisClient.ftCreate("idx:users",
                FTCreateParams.createParams().on(IndexDataType.JSON).addPrefix("user:"),
                schema
        );
        //System.out.println(createResult);
        String user1Set = redisClient.jsonSet("user:1", new Path2("$"), user1);
        String user2Set = redisClient.jsonSet("user:2", new Path2("$"), user2);
        String user3Set = redisClient.jsonSet("user:3", new Path2("$"), user3);
        SearchResult findPaulResult = redisClient.ftSearch("idx:users", "Paul @age:[30 40]");
        //SearchResult citiesResult = redisClient.ftSearch("idx:users","Paul", FTSearchParams.searchParams().returnFields("city"));
        logger.info("Find total results[{}]", findPaulResult.getTotalResults());
        findPaulResult.getDocuments().forEach(doc -> {
            logger.info("document id[{}], value[{}]", doc.getId(), doc.getString("$"));
            doc.getProperties().forEach(property -> {
                logger.info("\tproperty key[{}], value[{}]", property.getKey(), property.getValue());
            });
        });
        AggregationResult aggregationResult = redisClient.ftAggregate("idx:users",
                new AggregationBuilder("*").groupBy("@city", Reducers.count().as("count"))
        );
        logger.info("Aggregate total results[{}]", aggregationResult.getTotalResults());
        aggregationResult.getRows().forEach(row -> {
            logger.info("\tcity[{}], count[{}]", row.getString("city"), row.getLong("count"));
        });
        logger.info("indexAndQueryDocuments():");
    }
    /**
     * <p>
     * <a href="https://redis.io/docs/latest/develop/clients/jedis/prob/">...</a>
     * <a href="https://redis.io/docs/latest/develop/data-types/#probabilistic-data-types">...</a>
     * </p>
     */
    public static void probabilisticDataTypes(RedisClient redisClient) {

        redisClient.flushAll(); //FIXME too much deleted

        List<Boolean> res1 = redisClient.bfMAdd(
                "recorded_users",
                "andy", "cameron", "david", "michelle"
        );
        boolean res2 = redisClient.bfExists("recorded_users", "cameron");
        logger.info("Bloom Filter cameron exists[{}]", res2);

        boolean res3 = redisClient.bfExists("recorded_users", "kaitlyn");
        logger.info("Bloom Filter kaitlyn exists[{}]", res3);
        boolean res4 = redisClient.cfAdd("other_users", "paolo");
        boolean res5 = redisClient.cfAdd("other_users", "kaitlyn");
        boolean res6 = redisClient.cfAdd("other_users", "rachel");
        List<Boolean> res7 = redisClient.cfMExists(
                "other_users",
                "paolo", "rachel", "andy"
        );
        logger.info("Cuckoo Filter paolo rachel andy exists[{}]", res7);
        redisClient.cfDel("other_users", "paolo");
        boolean res9 = redisClient.cfExists("other_users", "paolo");
        logger.info("Cuckoo Filter deleted paolo exists[{}]", res9);

        long res10 = redisClient.pfadd("group:1", "andy", "cameron", "david");
        logger.info("HyperLogLog 1 add[{}]", res10);

        long res11 = redisClient.pfcount("group:1");
        logger.info("HyperLogLog 1 count[{}]", res11);
        long res12 = redisClient.pfadd(
                "group:2",
                "kaitlyn", "michelle", "paolo", "rachel"
        );
        logger.info("HyperLogLog 2 add[{}]", res12);
        long res13 = redisClient.pfcount("group:2");
        logger.info("HyperLogLog 2 count[{}]", res13);
        String res14 = redisClient.pfmerge("both_groups", "group:1", "group:2");
        logger.info("HyperLogLog 2 merge[{}]", res14);
        long res15 = redisClient.pfcount("both_groups");
        logger.info("HyperLogLog 2 merged count[{}]", res15);

        // Specify that you want to keep the counts within 0.01
        // (0.1%) of the true value with a 0.005 (0.05%) chance
        // of going outside this limit.
        String res16 = redisClient.cmsInitByProb("items_sold", 0.01, 0.005);
        System.out.println(res16);  // >>> OK

        Map<String, Long> firstItemIncrements = new HashMap<>();
        firstItemIncrements.put("bread", 300L);
        firstItemIncrements.put("tea", 200L);
        firstItemIncrements.put("coffee", 200L);
        firstItemIncrements.put("beer", 100L);

        List<Long> res17 = redisClient.cmsIncrBy("items_sold",
                firstItemIncrements
        );
        res17.sort(null);
        System.out.println();  // >>> [100, 200, 200, 300]

        Map<String, Long> secondItemIncrements = new HashMap<>();
        secondItemIncrements.put("bread", 100L);
        secondItemIncrements.put("coffee", 150L);

        List<Long> res18 = redisClient.cmsIncrBy("items_sold",
                secondItemIncrements
        );
        res18.sort(null);
        System.out.println(res18);  // >>> [350, 400]

        List<Long> res19 = redisClient.cmsQuery(
                "items_sold",
                "bread", "tea", "coffee", "beer"
        );
        res19.sort(null);
        System.out.println(res19);  // >>> [100, 200, 350, 400]
        String res20 = redisClient.tdigestCreate("male_heights");
        System.out.println(res20);  // >>> OK

        String res21 = redisClient.tdigestAdd("male_heights",
                175.5, 181, 160.8, 152, 177, 196, 164);
        System.out.println(res21);  // >>> OK

        double res22 = redisClient.tdigestMin("male_heights");
        System.out.println(res22);  // >>> 152.0

        double res23 = redisClient.tdigestMax("male_heights");
        System.out.println(res23);  // >>> 196.0

        List<Double> res24 = redisClient.tdigestQuantile("male_heights", 0.75);
        System.out.println(res24);  // >>> [181.0]

        // Note that the CDF value for 181 is not exactly 0.75.
        // Both values are estimates.
        List<Double> res25 = redisClient.tdigestCDF("male_heights", 181);
        System.out.println(res25);  // >>> [0.7857142857142857]

        String res26 = redisClient.tdigestCreate("female_heights");
        System.out.println(res26);  // >>> OK

        String res27 = redisClient.tdigestAdd("female_heights",
                155.5, 161, 168.5, 170, 157.5, 163, 171);
        System.out.println(res27);  // >>> OK

        List<Double> res28 = redisClient.tdigestQuantile("female_heights", 0.75);
        System.out.println(res28);  // >>> [170.0]

        String res29 = redisClient.tdigestMerge(
                "all_heights",
                "male_heights", "female_heights"
        );
        System.out.println(res29);  // >>> OK
        List<Double> res30 = redisClient.tdigestQuantile("all_heights", 0.75);
        System.out.println(res30);  // >>> [175.5]
        String res31 = redisClient.topkReserve("top_3_songs", 3L, 2000L, 7L, 0.925D);
        System.out.println(res31);  // >>> OK

        Map<String, Long> songIncrements = new HashMap<>();
        songIncrements.put("Starfish Trooper", 3000L);
        songIncrements.put("Only one more time", 1850L);
        songIncrements.put("Rock me, Handel", 1325L);
        songIncrements.put("How will anyone know?", 3890L);
        songIncrements.put("Average lover", 4098L);
        songIncrements.put("Road to everywhere", 770L);

        List<String> res32 = redisClient.topkIncrBy("top_3_songs",
                songIncrements
        );
        System.out.println(res32);
        // >>> [null, null, null, null, null, Rock me, Handel]

        List<String> res33 = redisClient.topkList("top_3_songs");
        System.out.println(res33);
        // >>> [Average lover, How will anyone know?, Starfish Trooper]

        List<Boolean> res34 = redisClient.topkQuery("top_3_songs",
                "Starfish Trooper", "Road to everywhere"
        );
        System.out.println(res34);
        logger.info("probabilisticDataTypes():");
    }
}
