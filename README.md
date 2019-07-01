
####工具目的###
根据配置文件的ES查询语句导出Excel文件

####如何执行###
打成jar包后便可直接执行


####配置文件及生成的excel文件路径###
均在放在user.dir目录下


####ES配置描述###

//需要查询的index
elasticsearch.index=xxxxxx

//es查询语句，如下为例句
elasticsearch.query={"bool": {"must": [{"range": {"l_time": {"lte": 1549382400000}}},{"exists":{"field":"s_code"}}]}}

//排序字段
elasticsearch.order.field=l_time

//排序asc/desc
elasticsearch.order.type=desc

//总共查询出多少条
elasticsearch.total.size=10000


