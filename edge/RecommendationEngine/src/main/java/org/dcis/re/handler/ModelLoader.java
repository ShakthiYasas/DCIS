package org.dcis.re.handler;

import com.azure.storage.blob.*;
import com.azure.storage.common.*;
import com.azure.storage.blob.options.BlobDownloadToFileOptions;

import java.io.FileInputStream;
import java.util.Properties;
import java.io.IOException;

public class ModelLoader {
    private final Properties appProps;

    public ModelLoader() throws IOException {
        appProps = new Properties();
        appProps.load(new FileInputStream("server.properties"));
    }

    private BlobClient setupTunnel() {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(appProps.getProperty("endpoint"))
                .buildClient();

        BlobContainerClient containerClient = blobServiceClient
                .getBlobContainerClient(appProps.getProperty("container-name"));

        return containerClient.getBlobClient(appProps.getProperty("blob-name"));
    }

    // Downloads the trained recommendation model into the local edge device.
    // returns: Boolean status of the success of the operation. True if succeeded.
    public Boolean downloadModel() {
        try {
            BlobClient blobClient = setupTunnel();
            ParallelTransferOptions parallelTransferOptions = new ParallelTransferOptions()
                    .setBlockSizeLong((long) (4 * 1024 * 1024)) // 4 MiB block size
                    .setMaxConcurrency(2);

            BlobDownloadToFileOptions options = new BlobDownloadToFileOptions(
                    appProps.getProperty("blob-name"));
            options.setParallelTransferOptions(parallelTransferOptions);

            blobClient.downloadToFileWithResponse(options, null, null);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
