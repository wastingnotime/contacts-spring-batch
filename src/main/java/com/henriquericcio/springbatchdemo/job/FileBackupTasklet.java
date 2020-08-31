package com.henriquericcio.springbatchdemo.job;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.*;

public class FileBackupTasklet implements Tasklet, InitializingBean {


    private Resource resource;

    //I know.... but is just for fun...
    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        File file = resource.getFile();
        File backupFile = new File(file.getPath().concat(".bkp"));
        copyFileUsingStream(file, backupFile);

        return RepeatStatus.FINISHED;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void afterPropertiesSet() {
        Assert.notNull(resource, "resource must be set");
    }

}