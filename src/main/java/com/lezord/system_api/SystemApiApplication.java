package com.lezord.system_api;

import com.lezord.system_api.service.ApplicationUserRoleService;
import com.lezord.system_api.service.ApplicationUserService;
import com.lezord.system_api.service.CourseContentTypeService;
import com.lezord.system_api.service.S3BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@RequiredArgsConstructor
@EnableScheduling
public class SystemApiApplication implements CommandLineRunner {
	private final ApplicationUserService applicationUserService;
	private final ApplicationUserRoleService applicationUserRoleService;
	private final CourseContentTypeService courseContentTypeService;
	private final S3BucketService s3BucketService;

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(SystemApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		applicationUserRoleService.initializeRoles();
		courseContentTypeService.initializeCourseContentType();
		applicationUserService.initializeAdmin();
		s3BucketService.createBucket("sys-lezord-lms-s3");
	}
}
