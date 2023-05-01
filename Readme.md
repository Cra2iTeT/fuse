### 1. 基本流程
+ 上传csv文件（格式限制）
+ 本地临时保存
+ 调用python代码迅速解析出表中的最大最小日期（未必在开头和结尾）
+ 返回给前端最大最小时间，选择预测时间和预测集时间
+ 调用python代码预测（使用十个风机模型测试，返回十个结果），结果直接保存到数据库，FineBI自动刷新大屏

## 待做

### 1. 中国气象台获取气温风向等预测气象数据保存到数据库，FineBI从数据库导入生成对应的效果
### 2. 结果预测
+ 如何传递参数给python
### 3. 引入消息队列，抛出的异常、运行错误都生产一条消息
### 4. 实时性
+ 定时从数据库中读取天气数据，自动化预测下一天一整天的数据
+ weatherEachHour使用alias注解设置别名对应csv文件
### 5. 索引建立，时间戳或与时间戳有关的可以创建索引