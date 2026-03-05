package com.disk.files.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.disk.files.domain.entity.FileChunkDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Mapper
public interface FileChunkMapper extends BaseMapper<FileChunkDO> {

    /**
     * 物理删除分片记录，避免逻辑删除占用唯一索引。
     *
     * @param ids 分片记录ID列表
     * @return 删除条数
     */
    int deleteByIdsPhysical(@Param("ids") List<Long> ids);

    /**
     * 清理同一分片唯一键下的失效记录（已逻辑删除或已过期）。
     *
     * @param identifier 文件唯一标识
     * @param chunkNumber 分片序号
     * @param createUser 用户ID
     * @param now 当前时间
     * @return 删除条数
     */
    int deleteDeletedOrExpiredByUniqueKey(@Param("identifier") String identifier,
                                          @Param("chunkNumber") Integer chunkNumber,
                                          @Param("createUser") Long createUser,
                                          @Param("now") Date now);
}
