package com.disk.files.infrastructure.canal.sync;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.disk.files.infrastructure.canal.config.CanalSyncProperties;
import com.disk.files.infrastructure.es.entity.UserFileESEntity;
import com.disk.files.infrastructure.es.mapper.UserFileESMapper;
import jakarta.annotation.PreDestroy;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于 Canal 的 user_file -> ES 增量同步任务
 */
@Component
@EnableConfigurationProperties(CanalSyncProperties.class)
@ConditionalOnProperty(prefix = "com.disk.canal", name = "enable", havingValue = "true")
@ConditionalOnBean(UserFileESMapper.class)
public class CanalUserFileEsSyncRunner implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(CanalUserFileEsSyncRunner.class);

    private final CanalSyncProperties properties;
    private final UserFileESMapper userFileESMapper;

    private volatile boolean running = true;
    private Thread workerThread;
    private CanalConnector connector;

    public CanalUserFileEsSyncRunner(CanalSyncProperties properties, UserFileESMapper userFileESMapper) {
        this.properties = properties;
        this.userFileESMapper = userFileESMapper;
        startWorker();
    }

    private void startWorker() {
        workerThread = new Thread(this, "canal-user-file-es-sync");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    @Override
    public void run() {
        connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress(properties.getHost(), properties.getPort()),
                properties.getDestination(),
                StringUtils.defaultString(properties.getUsername()),
                StringUtils.defaultString(properties.getPassword())
        );

        try {
            connector.connect();
            connector.subscribe(properties.getSubscribeFilter());
            connector.rollback();
            log.info("canal sync started. destination={}, filter={}", properties.getDestination(), properties.getSubscribeFilter());

            while (running) {
                Message message = connector.getWithoutAck(properties.getBatchSize());
                long batchId = message.getId();
                List<CanalEntry.Entry> entries = message.getEntries();
                if (batchId == -1 || CollectionUtils.isEmpty(entries)) {
                    sleepQuietly(properties.getPollingIntervalMs());
                    continue;
                }

                boolean success = false;
                try {
                    handleEntries(entries);
                    success = true;
                } catch (Exception e) {
                    log.error("canal sync batch handle error, batchId={}", batchId, e);
                }

                if (success) {
                    connector.ack(batchId);
                } else {
                    connector.rollback(batchId);
                    sleepQuietly(properties.getPollingIntervalMs());
                }
            }
        } catch (Exception e) {
            log.error("canal sync stopped unexpectedly", e);
        } finally {
            if (connector != null) {
                connector.disconnect();
            }
        }
    }

    private void handleEntries(List<CanalEntry.Entry> entries) throws Exception {
        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() != CanalEntry.EntryType.ROWDATA) {
                continue;
            }

            CanalEntry.Header header = entry.getHeader();
            if (!"user_file".equalsIgnoreCase(header.getTableName())) {
                continue;
            }

            CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            CanalEntry.EventType eventType = rowChange.getEventType();
            for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                if (eventType == CanalEntry.EventType.DELETE) {
                    handleDelete(rowData.getBeforeColumnsList());
                    continue;
                }
                if (eventType == CanalEntry.EventType.INSERT || eventType == CanalEntry.EventType.UPDATE) {
                    handleUpsert(rowData.getAfterColumnsList());
                }
            }
        }
    }

    private void handleDelete(List<CanalEntry.Column> columns) {
        Map<String, String> values = toValueMap(columns);
        Long id = asLong(values.get("id"));
        if (id == null) {
            return;
        }
        userFileESMapper.deleteById(id);
    }

    private void handleUpsert(List<CanalEntry.Column> columns) {
        Map<String, String> values = toValueMap(columns);
        Long id = asLong(values.get("id"));
        if (id == null) {
            return;
        }

        UserFileESEntity entity = new UserFileESEntity();
        entity.setId(id);
        entity.setUserId(asLong(values.get("user_id")));
        entity.setParentId(asLong(values.get("parent_id")));
        entity.setRealFileId(asLong(values.get("real_file_id")));
        entity.setFilename(values.get("filename"));
        entity.setFolderFlag(asInteger(values.get("folder_flag")));
        entity.setFileSizeDesc(values.get("file_size_desc"));
        entity.setFileType(asInteger(values.get("file_type")));
        entity.setCreateUser(values.get("create_user"));
        entity.setUpdateUser(values.get("update_user"));
        entity.setGmtCreate(asDate(values.get("gmt_create")));
        entity.setGmtModified(asDate(values.get("gmt_modified")));
        entity.setDeleted(asInteger(values.get("deleted")));
        entity.setLockVersion(asInteger(values.get("lock_version")));

        Integer updated = userFileESMapper.updateById(entity);
        if (updated == null || updated <= 0) {
            userFileESMapper.insert(entity);
        }
    }

    private Map<String, String> toValueMap(List<CanalEntry.Column> columns) {
        Map<String, String> result = new HashMap<>();
        for (CanalEntry.Column column : columns) {
            result.put(column.getName(), column.getValue());
        }
        return result;
    }

    private Long asLong(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return Long.valueOf(value);
    }

    private Integer asInteger(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return Integer.valueOf(value);
    }

    private java.util.Date asDate(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        String normalized = value.trim();
        try {
            return Timestamp.valueOf(normalized);
        } catch (Exception ignore) {
            // ignore and retry using formatter
        }
        try {
            LocalDateTime dateTime = LocalDateTime.parse(normalized, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return Timestamp.valueOf(dateTime);
        } catch (Exception ignore) {
            return null;
        }
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        if (workerThread != null) {
            workerThread.interrupt();
        }
    }
}
