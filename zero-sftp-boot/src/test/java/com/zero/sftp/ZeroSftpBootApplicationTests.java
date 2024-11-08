package com.zero.sftp;

import com.zero.sftp.common.config.SftpClientConfiguration;
import com.zero.sftp.service.SftpService;
import jakarta.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
class ZeroSftpBootApplicationTests {

//    @Autowired
//    private SftpConfiguration.SftpGateway sftpGateway;

    @Autowired
    private SftpClientConfiguration.SftpGateway sftpGateway;

    @Resource
    private SftpRemoteFileTemplate sftpRemoteFileTemplate;

    @Resource
    private SftpService sftpService;

    @Resource
    private MessagingTemplate messagingTemplate;

    @Resource
    private ApplicationContext applicationContext;


    @Test
    void contextLoads() {
    }

    @Test
    void testListFiles(){
        sftpGateway.listFile("/tmp/foo").stream().forEach((f)->{
            System.out.println(f);
        });
    }

    @Test
    void testUpload(){
        sftpGateway.upload(new File("D:\\dev-ide\\self_m2\\ali-settings.xml"));
    }

    @Test
    void testDownload(){
        List<File> downloadFiles = sftpGateway.downloadFiles("/tmp/foo");
        downloadFiles.stream().forEach((f)->{
            System.out.println(f.getName());
        });
    }

    @Test
    void rmTest() {
        sftpRemoteFileTemplate.execute(session -> {
            boolean exists = session.exists("/tmp/foo/home.js");
            System.out.println(exists);
            return exists;
        });
    }

    @Test
    void uploadTest() {
        sftpRemoteFileTemplate.execute(session -> {
            boolean exists = session.exists("/tmp/foo/home.js");
            System.out.println(exists);
            return exists;
        });
    }

    @Test
    void testExistFile() {
        boolean existFile = sftpService.existFile("/upload/home222.js");
        System.out.println(existFile);
    }

    @Test
    void listFileTest() {
        sftpService.listALLFile("/upload").stream().forEach(System.out::println);
    }

    @Test
    void upload2PathTest() throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(new File("D:\\tmp\\max.xml"));
        sftpService.upload(bytes, UUID.randomUUID().toString().concat(".xml"), "/tmp/audio/".concat(UUID.randomUUID().toString()));
    }

    @Test
    void testDownLoad() throws Exception {
        sftpService.downloadFile("/upload/home.js", "D:\\tmp\\c222c.js");
//
//        sftpService.uploadFile(new File("D:\\tmp\\cc.js"));


//        InputStream inputStream = sftpService.readFile("/upload/cc.js");
//
//        IOUtils.copy(inputStream, new FileOutputStream(new File("D:\\tmp\\" + UUID.randomUUID() + ".js")));

    }

    @Test
    void uploadFile() {
        sftpService.uploadFile(new File("D:\\tmp\\cc.js"));
    }

    @Test
    void nsltTest() {

        Arrays.asList(sftpService.nlstFile("/upload").split(",")).stream().forEach(System.out::println);
    }

    @Test
    void testMessagingTemplate() throws InterruptedException {
        DirectChannel bean = (DirectChannel) applicationContext.getBean("abc");

        while (true) {
            messagingTemplate.convertAndSend(bean, "aaa".getBytes(), (Map<String, Object>) null);
            Thread.sleep(5000L);
        }
    }

}
