package com.barter.common.s3;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3Service {

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;
	private final AmazonS3 amazonS3;

	public List<String> uploadFile(List<MultipartFile> multipartFiles) {
		return multipartFiles.stream()
			.parallel()
			.map(file -> {
				String name = FileNameHelper.createFileName(file.getOriginalFilename());
				ObjectMetadata metadata = new ObjectMetadata();
				metadata.setContentLength(file.getSize());
				metadata.setContentType(file.getContentType());

				try {
					InputStream inputStream = file.getInputStream();
					amazonS3.putObject(new PutObjectRequest(bucket, name, inputStream, metadata)
						.withCannedAcl(CannedAccessControlList.PublicRead));

				} catch (IOException e) {
					throw new IllegalStateException(e);
				}

				return name;
			})
			.toList();
	}

	public void deleteFile(String fileName) {
		amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
	}
}
