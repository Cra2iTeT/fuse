### 1. 基本流程
+ 上传csv文件（格式限制）
+ 本地临时保存
+ 调用python代码迅速解析出表中的最大最小日期（未必在开头和结尾）
+ 返回给前端最大最小时间，选择预测时间和预测集时间
+ 调用python代码预测，结果直接保存到数据库，FineBI自动刷新大屏