redis-cache-pollute







### 思考题

redis缓存淘汰策略是惰性和定时删除，如果一个主从集群，一个查询请求查询过期的key,走的是slave读取key的值，但是由于slave不会进行惰性删除，那么这个查询请求就会获取key的值返回给客户端，这个数据其实就产生了一致性问题。怎么避免这种情况啊？