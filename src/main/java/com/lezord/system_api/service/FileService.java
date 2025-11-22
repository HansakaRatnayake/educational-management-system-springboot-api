package com.lezord.system_api.service;

import com.lezord.system_api.util.UploadedResourceBinaryDataDTO;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    public UploadedResourceBinaryDataDTO create(MultipartFile file, String bucket, String directory);
    public void delete( String fileName,String bucket,String directory);
    public byte[] download(String fileName, String bucket, String directory);

}
