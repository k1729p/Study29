# Redis
### [Jedis guide](https://redis.io/docs/latest/develop/clients/jedis/)
### [Jedis API](https://www.javadoc.io/doc/redis.clients/jedis/latest/index.html)
### [Probabilistic](https://redis.io/docs/latest/develop/clients/jedis/prob/)

| Probabilistic data structure | Purpose |
|--|--|
| [Bloom filter](https://en.wikipedia.org/wiki/Bloom_filter) | Checks for presence of an item in a set |
| [Count-min sketch](https://en.wikipedia.org/wiki/Count%E2%80%93min_sketch) | Estimates the frequency of an item in a data stream |
| [Cuckoo filter](https://en.wikipedia.org/wiki/Cuckoo_filter) | Checks for presence of an item in a set |
| [HyperLogLog](https://en.wikipedia.org/wiki/HyperLogLog) |  Estimates the cardinality of a set |
| t-digest | Estimates the percentile of a data stream |
| Top-K | Finds the most frequent items in a data stream |

A **Bloom filter** can guarantee the **absence** of an item from a set, but it can only give an estimation about its **presence**.

A **quantile** is the value below which a certain fraction of samples lie. \
Quartiles & percentiles:
- the 1st quartile (Q1) - also known as the 25th percentile (P25)
- the 2nd quartile (Q2) or the median - also known as the 50th percentile (P50) 
- the 3rd quartile (Q3) - also known as the 75th percentile (P75)
---