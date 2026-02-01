package com.disk.file.local.initializer;

import com.disk.file.config.LocalStorageEngineConfig;
import com.disk.file.local.LocalStorageEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 类描述: TODO
 *
 * @author weikunkun
 */
@Slf4j
@Component
public class FolderAndChunksFolderInitializer implements CommandLineRunner {

    @Autowired
    private LocalStorageEngineConfig localStorageEngineConfig;

    @Override
    public void run(String... args) throws Exception {
        FileUtils.forceMkdir(new File(localStorageEngineConfig.getRootFilePath()));
        log.info("the root file path has been created!");
        FileUtils.forceMkdir(new File(localStorageEngineConfig.getRootFileChunkPath()));
        log.info("the root file chunk path has been created!");
    }
}
