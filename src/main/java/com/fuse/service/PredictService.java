package com.fuse.service;

import com.fuse.domain.pojo.PredictResult;
import com.fuse.domain.to.PredictTo;
import com.fuse.domain.vo.R;
import com.fuse.exception.PythonScriptRunException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 13:53
 */
public interface PredictService {
    R csvResolve(MultipartFile csv) throws PythonScriptRunException, IOException, InterruptedException;

    R predict(PredictTo predictTo) throws PythonScriptRunException;
}
