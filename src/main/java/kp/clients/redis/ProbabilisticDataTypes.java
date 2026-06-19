package kp.clients.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.RedisClient;

import java.lang.invoke.MethodHandles;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The approximate calculations with probabilistic data types.
 */
public class ProbabilisticDataTypes {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String USERS_BF = "users_bf";
    private static final String USERS_CF = "users_cf";
    private static final String[] PRESENT_USERS = {"Alice", "Bob"};
    private static final String[] CHECKED_USERS = {"Alice", "Bob", "Charlie"};
    private static final List<String> YEAR_LIST = Stream.of(Month.values()).map(Month::toString).toList();
    private static final String MONTHS_1 = "months_1";
    private static final String MONTHS_2 = "months_2";
    private static final String MONTHS_3 = "months_3";
    private static final String MONTHS_1_2 = "months_1_2";
    private static final String MONTHS_2_3 = "months_2_3";
    private static final String MONTHS_1_2_3 = "months_1_2_3";
    private static final String SOLD_ITEMS = "sold_items";
    private static final String HEIGHTS_MALE = "heights_male";
    private static final String HEIGHTS_FEMALE = "heights_female";
    private static final String HEIGHTS_ALL = "heights_all";
    private static final String TOP_3 = "top_3";

    /**
     * Checks the item membership with Bloom filter and Cuckoo filter.
     *
     * @param redisClient the redis client
     */
    public static void checkItemMembership(RedisClient redisClient) {

        redisClient.del(USERS_BF, USERS_CF);

        redisClient.bfMAdd(USERS_BF, PRESENT_USERS);
        List<Boolean> existsList = redisClient.bfMExists(USERS_BF, CHECKED_USERS);
        logger.info("Bloom Filter:");
        logger.info("  'Alice' exists[{}], 'Bob' exists[{}], 'Charlie' exists[{}]",
                existsList.getFirst(), existsList.get(1), existsList.get(2));
        logger.info("BF info {}", redisClient.bfInfo(USERS_BF));
        logger.info("- ".repeat(20));

        redisClient.cfInsert(USERS_CF, PRESENT_USERS);
        existsList = redisClient.cfMExists(USERS_CF, CHECKED_USERS);
        logger.info("Cuckoo Filter:");
        logger.info("  'Alice' exists[{}], 'Bob' exists[{}], 'Charlie' exists[{}]",
                existsList.getFirst(), existsList.get(1), existsList.get(2));
        redisClient.cfDel(USERS_CF, "Bob");
        existsList = redisClient.cfMExists(USERS_CF, CHECKED_USERS);
        logger.info("  'Alice' exists[{}], 'Bob' exists[{}], 'Charlie' exists[{}]",
                existsList.getFirst(), existsList.get(1), existsList.get(2));
        logger.info("CF info {}", redisClient.cfInfo(USERS_CF));
        logger.info("- ".repeat(20));
    }

    /**
     * Calculates the set cardinality.
     *
     * @param redisClient the redis client
     */
    public static void calculateSetCardinality(RedisClient redisClient) {

        redisClient.del(MONTHS_1, MONTHS_2, MONTHS_3, MONTHS_1_2, MONTHS_2_3, MONTHS_1_2_3);

        final List<String> list1 = YEAR_LIST.subList(0, 5);
        redisClient.pfadd(MONTHS_1, list1.toArray(new String[0]));
        final List<String> list2 = YEAR_LIST.subList(3, 9);
        redisClient.pfadd(MONTHS_2, list2.toArray(new String[0]));
        final List<String> list3 = YEAR_LIST.subList(5, 12);
        redisClient.pfadd(MONTHS_3, list3.toArray(new String[0]));
        redisClient.pfmerge(MONTHS_1_2, MONTHS_1, MONTHS_2);
        redisClient.pfmerge(MONTHS_2_3, MONTHS_2, MONTHS_3);
        redisClient.pfmerge(MONTHS_1_2_3, MONTHS_1_2, MONTHS_2_3);

        logger.info("HyperLogLog:");
        logger.info("  'months_1'     count[{}], list1{}", redisClient.pfcount(MONTHS_1), list1);
        logger.info("  'months_2'     count[{}], list2{}", redisClient.pfcount(MONTHS_2), list2);
        logger.info("  'months_3'     count[{}], list3{}", redisClient.pfcount(MONTHS_3), list3);
        logger.info("  'months_1_2'   count[{}]", redisClient.pfcount(MONTHS_1_2));
        logger.info("  'months_2_3'   count[{}]", redisClient.pfcount(MONTHS_2_3));
        logger.info("  'months_1_2_3' count[{}]", redisClient.pfcount(MONTHS_1_2_3));
        logger.info("- ".repeat(20));
    }

    /**
     * Tracks approximate item frequencies with CMS for memory-efficient statistics on data streams.
     *
     * @param redisClient the redis client
     */
    public static void countItemFrequency(RedisClient redisClient) {
        redisClient.del(SOLD_ITEMS);
        // Initializes a Count-Min Sketch to accommodate requested tolerances.
        // Keep the counts within 0.1% of the true value
        //   with a 0.05% chance of going outside this limit.
        redisClient.cmsInitByProb(SOLD_ITEMS, 0.01, 0.005);
        redisClient.cmsIncrBy(SOLD_ITEMS, Map.of(
                "beer", 10L,
                "bread", 20L,
                "coffee", 30L,
                "tea", 40L
        ));
        redisClient.cmsIncrBy(SOLD_ITEMS, Map.of(
                "beer", 1L,
                "coffee", 3L
        ));
        redisClient.cmsIncrBy(SOLD_ITEMS, Map.of(
                "bread", 2L,
                "tea", 4L
        ));
        final List<Long> countForItemList =
                redisClient.cmsQuery(SOLD_ITEMS, "beer", "bread", "coffee", "tea");
        logger.info("Count-min sketch:");
        logger.info("  'beer'   sold[{}]", countForItemList.getFirst());
        logger.info("  'bread'  sold[{}]", countForItemList.get(1));
        logger.info("  'coffee' sold[{}]", countForItemList.get(2));
        logger.info("  'tea'    sold[{}]", countForItemList.get(3));
        logger.info("CMS info {}", redisClient.cmsInfo(SOLD_ITEMS));
        logger.info("- ".repeat(20));
    }

    /**
     * Calculates the quantiles.
     * <p>
     * The t-digest is a probabilistic data structure that allows to estimate the percentile of a data stream.
     * </p>
     *
     * @param redisClient the redis client
     */
    public static void calculateQuantiles(RedisClient redisClient) {

        redisClient.del(HEIGHTS_MALE, HEIGHTS_FEMALE, HEIGHTS_ALL);

        redisClient.tdigestCreate(HEIGHTS_MALE);
        redisClient.tdigestAdd(HEIGHTS_MALE, 175.5, 181, 160.8, 152, 177, 196, 164);
        redisClient.tdigestCreate(HEIGHTS_FEMALE);
        redisClient.tdigestAdd(HEIGHTS_FEMALE, 155.5, 161, 168.5, 170, 157.5, 163, 171);
        redisClient.tdigestMerge(HEIGHTS_ALL, HEIGHTS_MALE, HEIGHTS_FEMALE);

        logger.info("t-digest:");
        List.of(HEIGHTS_MALE, HEIGHTS_FEMALE, HEIGHTS_ALL).forEach(key ->
                logger.info("  {} min[{}], max[{}], quantile of '0.75'{}", String.format("%-16s", "'" + key + "'"),
                        redisClient.tdigestMin(key), redisClient.tdigestMax(key),
                        redisClient.tdigestQuantile(key, 0.75)));

        final double[] quantileFractionArray = {0, 0.25, 0.5, 0.75, 1};
        final List<Double> quantileList = redisClient.tdigestQuantile(HEIGHTS_ALL, quantileFractionArray);
        final List<Long> rankList = redisClient.tdigestRank(HEIGHTS_ALL,
                quantileList.stream().mapToDouble(arg -> arg).toArray());
        StringBuilder strBld = new StringBuilder();
        strBld.append("\n  'all_heights'\n");
        IntStream.range(0, quantileFractionArray.length).forEach(i ->
                strBld.append(String.format("    fraction[%.2f], quantile[%.1f], rank[%2d]%n",
                        quantileFractionArray[i], quantileList.get(i), rankList.get(i))));
        logger.info(strBld.toString());

        logger.info("  Cumulative Distribution Function");
        // Note that for value 175.5 the CDF value is not exactly 0.75. Both values are estimates.
        logger.info("    value{} (retrieved for 'male_heights' and value '181.0')",
                redisClient.tdigestCDF(HEIGHTS_MALE, 181.0));
        logger.info("    value{} (retrieved for 'all_heights' and value '175.5')",
                redisClient.tdigestCDF(HEIGHTS_ALL, 175.5));
        logger.info("t-d info {}", redisClient.tdigestInfo(HEIGHTS_ALL));
        logger.info("- ".repeat(20));
    }

    /**
     * Calculates the rankings.
     * <p>
     * Tracks top K most frequent items in a data stream for efficient ranking without storing all items.
     * </p>
     *
     * @param redisClient the redis client
     */
    public static void calculateRankings(RedisClient redisClient) {

        redisClient.del(TOP_3);
        redisClient.topkReserve(TOP_3, 3L, 2000L, 7L, 0.925D);
        logger.info("Top-K:");
        logger.info("  increase score by increment, dropped items{}",
                redisClient.topkIncrBy(TOP_3, Map.of(
                        "AAA Item", 1L,
                        "BBB Item", 2L,
                        "CCC Item", 3L,
                        "DDD Item", 4L,
                        "EEE Item", 5L)));
        logger.info("  items in Top-K sketch{}", redisClient.topkList(TOP_3));
        redisClient.topkIncrBy(TOP_3, Map.of(
                "CCC Item", 30L,
                "BBB Item", 20L,
                "DDD Item", 10L));
        logger.info("  items in Top-K sketch{}", redisClient.topkList(TOP_3));
        redisClient.topkIncrBy(TOP_3, Map.of(
                "AAA Item", 300L,
                "BBB Item", 200L,
                "CCC Item", 100L));
        logger.info("  items in Top-K sketch{}", redisClient.topkList(TOP_3));

        final List<Boolean> list = redisClient.topkQuery(TOP_3, "AAA Item", "CCC Item", "EEE Item");
        logger.info("  query, 'AAA Item' present[{}], 'CCC Item' present[{}], 'EEE Item' present[{}]",
                list.getFirst(), list.get(1), list.get(2));
        logger.info("Top-K info {}", redisClient.topkInfo(TOP_3));
        logger.info("- ".repeat(20));
    }
}
