package com.fuse.controller;

import com.fuse.common.SystemCode;
import com.fuse.domain.vo.R;
import com.fuse.service.CsvResolveService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 13:59
 */
@RestController
@RequestMapping("/csv")
public class CsvResolverController {

    private final CsvResolveService csvResolveService;

    public CsvResolverController(CsvResolveService csvResolveService) {
        this.csvResolveService = csvResolveService;
    }

    /**
     * 上传CSV文件解析最大最小时间
     *
     * @param csv
     * @return
     */
    public R CsvTimeDivide(@RequestParam("csv") MultipartFile csv) {
        if (csv.isEmpty() || !"csv".equals(csv.getContentType())) {
            return new R(SystemCode.CSV_RESOLVE_ERROR_TYPE_MISMATCH.getCode(),
                    SystemCode.CSV_RESOLVE_ERROR_TYPE_MISMATCH.getMsg(), null);
        }
        return csvResolveService.csvResolve(csv);
    }
}
