package com.fuse.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.fuse.domain.pojo.PredictResult;
import com.fuse.mapper.PredictResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description: csvload监听器
 * @author: w_jingbo
 * @date: 2023/5/30
 * @Copyright: 博客：http://coisini.wang
 */
@Component
public class PredictResultListener extends AnalysisEventListener<PredictResult> {

    private Logger logger = LoggerFactory.getLogger(PredictResultListener.class);

    private String location;

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    @Autowired
    private PredictResultMapper predictResultMapper;

    @Override
    public void invoke(PredictResult predictResult, AnalysisContext analysisContext) {
        //填充Date属性
        predictResult.setDate(predictResult.getDatetime());
        predictResult.setRegion(this.location);
        logger.info("读取到一条信息:{}", predictResult);
        predictResultMapper.saveOrUpdate(predictResult);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
