package com.zero.sftp.common.config.sync;

import com.jcraft.jsch.ChannelSftp;
import com.zero.sftp.common.config.SftpProperties;
import jakarta.annotation.Resource;
import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.messaging.MessageHandler;

import java.io.File;

/**
 * 文件同步器
 */
@Configuration
public class FileSyncConfiguration {

    @Resource
    SessionFactory<ChannelSftp.LsEntry> sftpSessionFactory;

    @Resource
    private SftpProperties properties;

//    @Bean
//    public SftpInboundFileSynchronizer sftpInboundFileSynchronizer(SessionFactory<ChannelSftp.LsEntry> sftpSessionFactory) {
//        SftpInboundFileSynchronizer fileSynchronizer = new SftpInboundFileSynchronizer(sftpSessionFactory);
//        fileSynchronizer.setDeleteRemoteFiles(false);
//        fileSynchronizer.setRemoteDirectory(properties.getRemoteDir());
//        fileSynchronizer.setFilter(new SftpSimplePatternFileListFilter("*.*"));
//        return fileSynchronizer;
//    }

    @Bean
    public SftpInboundFileSynchronizer sftpInboundFileSynchronizer(SessionFactory<SftpClient.DirEntry> sftpSessionFactory) {
        SftpInboundFileSynchronizer fileSynchronizer = new SftpInboundFileSynchronizer(sftpSessionFactory);
        fileSynchronizer.setDeleteRemoteFiles(false);
        fileSynchronizer.setRemoteDirectory(properties.getRemoteDir());
        fileSynchronizer.setFilter(new SftpSimplePatternFileListFilter("*.*"));
        return fileSynchronizer;
    }

    /**
     * 配置Inbound Channel Adapter,监控sftp服务器文件的状态。一旦由符合条件的文件生成，就将其同步到本地服务器。
     * 需要条件：inboundFileChannel的bean；轮询的机制；文件同步bean,SftpInboundFileSynchronizer；
     * @return MessageSource
     */
    @Bean
    @InboundChannelAdapter(channel = "fileSynchronizerChannel",
            poller = @Poller(cron = "0/5 * * * * *",
                    //fixedDelay = "5000",
                    maxMessagesPerPoll = "1"))
    public MessageSource<File> sftpMessageSource(SftpInboundFileSynchronizer sftpInboundFileSynchronizer) {
        SftpInboundFileSynchronizingMessageSource source =
                new SftpInboundFileSynchronizingMessageSource(sftpInboundFileSynchronizer);
        source.setLocalDirectory(new File(properties.getLocalDir()));
        source.setAutoCreateLocalDirectory(true);
        source.setLocalFilter(new AcceptOnceFileListFilter<>());
        source.setMaxFetchSize(-1);
        return source;
    }

    @Bean
    @ServiceActivator(inputChannel = "fileSynchronizerChannel")
    public MessageHandler handler() {
        // 同步时打印文件信息
        return (m) -> {
            System.out.println(m.getPayload());
            m.getHeaders()
                    .forEach((key, value) -> System.out.println("\t\t|---" + key + "=" + value));
        };
    }
}
