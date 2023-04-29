package com.fuse.service;

import com.fuse.domain.vo.R;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Cra2iTeT
 * @since 2023/4/29 13:53
 */
public interface CsvResolveService {
    R csvResolve(MultipartFile csv);
}
