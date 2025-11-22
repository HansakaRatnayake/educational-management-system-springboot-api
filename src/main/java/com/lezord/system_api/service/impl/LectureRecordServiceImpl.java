package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestLectureRecordDTO;
import com.lezord.system_api.dto.response.ResponseLectureRecordDetailsDTO;
import com.lezord.system_api.dto.response.ResponseLectureResourceLinkDTO;
import com.lezord.system_api.dto.response.ResponseRecordPlayDto;
import com.nozomi.system_api.dto.response.*;
import com.lezord.system_api.dto.response.paginate.PaginatedResponseLectureRecordDetailsDTO;
import com.lezord.system_api.entity.CourseStageContent;
import com.lezord.system_api.dto.response.paginate.PlayerDataResponseDto;
import com.lezord.system_api.entity.Intake;
import com.lezord.system_api.entity.LectureRecord;
import com.lezord.system_api.entity.LectureResourceLink;
import com.lezord.system_api.entity.core.ThumbnailFileResource;
import com.lezord.system_api.entity.core.VideoResource;
import com.lezord.system_api.exception.BadRequestException;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.exception.InternalServerException;
import com.lezord.system_api.repository.CourseStageContentRepository;
import com.lezord.system_api.repository.IntakeRepository;
import com.lezord.system_api.repository.LectureRecordRepository;
import com.lezord.system_api.repository.LectureResourceLinkRepo;
import com.lezord.system_api.service.FileService;
import com.lezord.system_api.service.LectureRecordService;
import com.lezord.system_api.util.FileDataHandler;
import com.lezord.system_api.util.UploadedResourceBinaryDataDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LectureRecordServiceImpl implements LectureRecordService {

    private final IntakeRepository intakeRepo;
    private final CourseStageContentRepository stageContentRepository;
    private final FileService fileService;
    private final CourseStageContentRepository courseStageContentRepository;
    private final LectureRecordRepository lectureRecordRepository;
    private final FileDataHandler fileDataHandler;
    private final LectureResourceLinkRepo lectureResourceLinkRepo;


    @Value("${aws.bucketName}")
    private String bucket;


    @Override
    public void addRecord(RequestLectureRecordDTO dto, MultipartFile thumbnail1, MultipartFile video1) throws SQLException {
        UploadedResourceBinaryDataDTO video = null;
        UploadedResourceBinaryDataDTO thumbnail = null;
        try {
            Intake selectedIntake = intakeRepo.findById(dto.getIntakeId()).orElseThrow(() -> new EntryNotFoundException("Intake Data was not found"));
            thumbnail = fileService.create(thumbnail1, bucket, "lecture-record/" + selectedIntake.getCourse().getPropertyId() + "/" + selectedIntake.getPropertyId() + "/thumbnail/");
            video = fileService.create(video1, bucket, "lecture-record/" + selectedIntake.getCourse().getPropertyId() + "/" + selectedIntake.getPropertyId() + "/record/" + UUID.randomUUID() + "/");
            LectureRecord lectureRecord = LectureRecord.builder()
                    .propertyId(UUID.randomUUID().toString())
                    .createdDate(dto.getDate().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate())
                    .freeAvailability(dto.isFreeAvailability())
                    .title(dto.getTitle())
                    .length(dto.getLength())
                    .activeState(true)
                    .thumbnailFileResource(new ThumbnailFileResource(
                            thumbnail.getHash(),
                            new SerialBlob(thumbnail.getDirectory().getBytes()),
                            thumbnail.getFilename(),
                            thumbnail.getResourceUrl()
                    ))
                    .videoResource(new VideoResource(
                            video.getHash(),
                            new SerialBlob(video.getDirectory().getBytes()),
                            video.getFilename(),
                            video.getResourceUrl(),
                            video.getSize()
                    ))
                    .courseStageContent(courseStageContentRepository.findById(dto.getCourseStageContentId()).orElseThrow(() -> new EntryNotFoundException("Stage content not found")))
                    .intake(selectedIntake)
                    .length(dto.getLength())
                    .downloadEnabled(true)
                    .build();

            lectureRecordRepository.save(lectureRecord);

        } catch (EntryNotFoundException | NullPointerException | SQLException e) {

            if (thumbnail != null) {
                fileService.delete(fileDataHandler.extractActualFileName(new InputStreamReader(thumbnail.getFilename().getBinaryStream())), bucket, thumbnail.getDirectory());

            }
            if (video != null) {
                fileService.delete(fileDataHandler.extractActualFileName(new InputStreamReader(video.getFilename().getBinaryStream())), bucket, video.getDirectory());

            }
            if (e instanceof EntryNotFoundException) {
                throw new EntryNotFoundException("Intake Data was not found");
            }
            throw new InternalServerException(e.getMessage());
        }
    }

    @Override
    public List<ResponseRecordPlayDto> getLessonLectureRecordData(String intakeId, String lessonId) throws SQLException {
        Intake intake = intakeRepo.findById(intakeId).orElseThrow(() -> new EntryNotFoundException("Intake Data was not found"));
        CourseStageContent courseStageContent = stageContentRepository.findById(lessonId).orElseThrow(() -> new EntryNotFoundException("Stage Content was not found"));


        return lectureRecordRepository.findByIntakeAndContentId(intake.getPropertyId(), courseStageContent.getPropertyId()).stream().map(lectureRecord ->
                ResponseRecordPlayDto.builder()
                        .id(lectureRecord.getPropertyId())
                        .createdAt(lectureRecord.getCreatedDate().toString())
                        .duration(lectureRecord.getLength())
                        .title(lectureRecord.getTitle())
                        .resourceUrl(lectureRecord.getThumbnailFileResource() != null ? fileDataHandler.blobToString(lectureRecord.getVideoResource().getVideoResourceUrl()) : "")
                        .thumbnail(lectureRecord.getThumbnailFileResource() != null ? fileDataHandler.blobToString(lectureRecord.getThumbnailFileResource().getThumbnailResourceUrl()):"")
                        .size(lectureRecord.getVideoResource() != null ? lectureRecord.getVideoResource().getSize().toString() : "")
                        .intakeId(lectureRecord.getIntake().getPropertyId())
                        .lessonId(lectureRecord.getCourseStageContent().getPropertyId())
                        .downloadEnabled(lectureRecord.isDownloadEnabled())
                        .build()
        ).sorted(Comparator.comparing(ResponseRecordPlayDto::getCreatedAt))
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<InputStreamResource> getRecordData(String id, String range) throws SQLException, IOException {
        return null;
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadRecord(String id) throws SQLException, IOException {
        return null;
    }

    @Override
    public long getVideoFileSize(String id) throws SQLException, IOException {
        return 0;
    }

    @Override
    public PaginatedResponseLectureRecordDetailsDTO getLatestRecordsForUser() throws SQLException, IOException {
        return null;
    }

    @Override
    public PaginatedResponseLectureRecordDetailsDTO findAll(int page, int size, String intakeId) {
        return null;
    }

    @Override
    public void updateFreeAvailability(String id, boolean status) {

    }

    @Override
    public void delete(String recordId) throws SQLException, UnsupportedEncodingException {
        List<LectureResourceLink> lectureResourceLinks = lectureResourceLinkRepo.findByRecordId(recordId);
        if (!lectureResourceLinks.isEmpty()) {
            throw new BadRequestException("Please Delete Lecture Resource Link ");
        }
        Optional<LectureRecord> lectureRecord = lectureRecordRepository.findById(recordId);
        if (lectureRecord.isEmpty()) {
            throw new EntryNotFoundException("Lecture Record not found.");
        }
        LectureRecord selectedRecord = lectureRecord.get();
        fileService.delete(
                fileDataHandler.extractActualFileName(
                        new InputStreamReader(
                                selectedRecord.getThumbnailFileResource().getThumbnailFileName().getBinaryStream())),
                bucket,
                new String(selectedRecord.getThumbnailFileResource().getThumbnailDirectory().getBytes(1,
                        (int) selectedRecord.getThumbnailFileResource().getThumbnailDirectory().length()))

        );
        fileService.delete(
                fileDataHandler.extractActualFileName(
                        new InputStreamReader(
                                selectedRecord.getVideoResource().getVideoFileName().getBinaryStream())),
                bucket,
                new String(selectedRecord.getVideoResource().getVideoDirectory().getBytes(1,
                        (int) selectedRecord.getVideoResource().getVideoDirectory().length()))

        );
        lectureRecordRepository.delete(selectedRecord);
    }

    @Override
    public List<ResponseLectureRecordDetailsDTO> findAllForStudent(String intakeId, String content) {
        return lectureRecordRepository.findByIntakeAndContentId(intakeId, content)
                .stream()
                .map(this::toResponseLectureDetails)
                .sorted(Comparator.comparing(ResponseLectureRecordDetailsDTO::getDate))
                .collect(Collectors.toList());
    }

    private ResponseLectureRecordDetailsDTO toResponseLectureDetails(LectureRecord e) {

        if (e == null) {
            return null;
        }

        return ResponseLectureRecordDetailsDTO.builder()
                .propertyId(e.getPropertyId())
                .date(Date.from(e.getCreatedDate().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .freeAvailability(e.isFreeAvailability())
                .title(e.getTitle())
                .length(e.getLength())
                .activeState(e.getActiveState())
                .downloadEnabled(e.isDownloadEnabled())
                .thumbnailFileResourceUrl(e.getThumbnailFileResource().getThumbnailResourceUrl() == null ? null : fileDataHandler.blobToString(e.getThumbnailFileResource().getThumbnailResourceUrl()))
                .lectureRecordResourceUrl(e.getVideoResource().getVideoResourceUrl() == null ? null : fileDataHandler.blobToString(e.getVideoResource().getVideoResourceUrl()))
                .intakeId(e.getIntake().getPropertyId())
                .stageContentDTO(null)
                .responseLectureResourceLinkDTOS(
                        e.getLectureResourceLinks()
                                .stream()
                                .map(this::toResponseLectureResourceLinkDTO)
                                .collect(Collectors.toList())
                )
                .build();
    }

    private ResponseLectureResourceLinkDTO toResponseLectureResourceLinkDTO(LectureResourceLink e) {
        if (e == null) return null;
        return ResponseLectureResourceLinkDTO.builder()
                .propertyId(e.getPropertyId())
                .resourceUrl(fileDataHandler.blobToString(e.getResourceUrl()))
                .resourceDate(LocalDate.from(e.getResourceDate()))
                .build();
    }

    @Override
    public PlayerDataResponseDto loadPlayerData( String intakeId, String lessonId) {

       /* String userId = otherServiceCallingPoint.getUserId(tokenHeader);
        if (userId == null) {
            throw new EntryNotFoundException("User not found, please log in.");
        }
        Optional<Student> student = studentRepo.findByUserId(userId);
        if (student.isEmpty()) {
            throw new EntryNotFoundException("Student not found, please register.");
        }*/


        List<LectureRecord> selectedRecord = lectureRecordRepository.findAll(intakeId, lessonId);

        if (selectedRecord.isEmpty()) {
            throw new EntryNotFoundException("Lecture Record not found.");
        }
        String intake = selectedRecord.get(0).getIntake().getPropertyId();
        String contentId = selectedRecord.get(0).getCourseStageContent().getPropertyId();
        /*Optional<Course> selectedCourse = courseRepo.findById(selectedRecord.get().getIntake().getCourseAvailableLanguage().getCourse().getPropertyId());

        if (selectedCourse.isEmpty()) {
            throw new EntryNotFoundException("Course not found.");
        }*/

       /* Optional<Registration> registrationData = registrationRepo.isAlreadyRegistered(student.get().getPropertyId(),
                intakeId, selectedCourse.get().getPropertyId());
*/
        return PlayerDataResponseDto.builder().initialVideo(
                convertLectureRecordDetails(selectedRecord.get(0))
        ).dataList(
                lectureRecordRepository.findAllWithIntakeIdAndContent(intakeId, contentId)
                        .stream().map(e -> convertLectureRecordDetails(e)).toList()
        ).course(/*convertCourse(selectedCourse.get())*/null).build();
    }

   /* private ResponseCourseDto convertCourse(Course p) {
        if (p == null) {
            return null;
        }
        Double rating;
        if (p.getCourseReviews().isEmpty()) {
            rating = 0.0;
        } else {
            long oneCount = p.getCourseReviews().stream().filter(e -> e.getRating() == 1).count();
            long twoCount = p.getCourseReviews().stream().filter(e -> e.getRating() == 2).count();
            long threeCount = p.getCourseReviews().stream().filter(e -> e.getRating() == 3).count();
            long fourCount = p.getCourseReviews().stream().filter(e -> e.getRating() == 4).count();
            long fiveCount = p.getCourseReviews().stream().filter(e -> e.getRating() == 5).count();
            rating = (1 * oneCount + 2 * twoCount + 3 * threeCount + 4 * fourCount + 5 * fiveCount) / (double) p.getCourseReviews().size();

        }
        return ResponseCourseDto.builder()
                .propertyId(p.getPropertyId())
                .activeState(p.getActiveState())
                .createdDate(p.getCreatedDate())
                .description(p.getDescription())
                .duration(p.getDuration())
                .goal(p.getGoal())
                .hours(p.getHours())
                .courseName(p.getCourseName())
                .isPartTimeCourse(p.getIsPartTimeCourse())
                .isKidsCourse(p.getIsKidsCourse())
                .vision(p.getVision())
                .rating(rating)
                .reviewCount(p.getCourseReviews().size())
                .bannerUrl(p.getCourseThumbnail() == null ? null : fileDataExtractor.byteArrayToString(p.getCourseThumbnail().getResourceUrl()))
                .courseLectureCount(p.getCourseLectureCountData() == null ? null : p.getCourseLectureCountData().getCount())
                .department(ResponseCourseDepartmentUtil.builder()
                        .propertyId(p.getCourseDepartment().getPropertyId())
                        .name(p.getCourseDepartment().getDepartment()).build())
                .type(ResponseCourseTypeUtil.builder()
                        .name(p.getCourseType().getTypeName()).propertyId(p.getCourseType().getPropertyId()).build())
                .build();
    }*/

    private ResponseLectureRecordDetailsDTO convertLectureRecordDetails(LectureRecord lr) {


        try {

          /*  long allReviewCountByRecord = lectureRecordReviewRepo.findAllReviewCountByRecord(lr.getPropertyId());
            List<LectureRecordReview> searchData = lectureRecordReviewRepo.search(lr.getPropertyId());
*/
            /*ResponseLectureContentDTO responseLectureContentDTOS = new ResponseLectureContentDTO(
                    lr.getLectureContent().getPropertyId(),
                    lr.getLectureContent().getActiveState(),
                    lr.getLectureContent().getCreatedDate(),
                    lr.getLectureContent().getIntakeId(),
                    lr.getLectureContent().getTopic()
            );*/
            ArrayList<ResponseLectureResourceLinkDTO> responseLectureResourceLinkDTOS = new ArrayList<>();
            for (LectureResourceLink lectureResourceLink : lr.getLectureResourceLinks()) {

                ZoneId zoneId = ZoneId.systemDefault();

                responseLectureResourceLinkDTOS.add(new ResponseLectureResourceLinkDTO(
                        lectureResourceLink.getPropertyId(),
                        lectureResourceLink.getResourceUrl() == null ? null : new String(lectureResourceLink.getResourceUrl().getBytes(1, (int) lectureResourceLink.getResourceUrl().length())),
                        lectureResourceLink.getResourceDate().atZone(zoneId).toLocalDate()

                ));
            }

            /*ResponseStageContentDto stageContentDtos = convertStageContent(lr.getStageContent());*/
            return ResponseLectureRecordDetailsDTO.builder()
                    .propertyId(lr.getPropertyId())
                    .date(Date.from(lr.getCreatedDate().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .freeAvailability(lr.isFreeAvailability())
                    .title(lr.getTitle())
                    .length(lr.getLength())
                    .activeState(lr.getActiveState())
                    .thumbnailFileResourceUrl(lr.getThumbnailFileResource() == null ? null : new String(lr.getThumbnailFileResource().getThumbnailResourceUrl().getBytes(1, (int) lr.getThumbnailFileResource().getThumbnailResourceUrl().length())))
                    .responseLectureResourceLinkDTOS(responseLectureResourceLinkDTOS)
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }


    @Override
    public ResponseRecordPlayDto getVideo(String recordId) throws Exception {
        LectureRecord lectureRecord = lectureRecordRepository.findById(recordId).orElseThrow(() -> new EntryNotFoundException("Lecture Record not found."));

        return ResponseRecordPlayDto.builder()
                .id(lectureRecord.getPropertyId())
                .createdAt(lectureRecord.getCreatedDate().toString())
                .duration(lectureRecord.getLength())
                .title(lectureRecord.getTitle())
                .thumbnail(lectureRecord.getThumbnailFileResource().toString())
                .resourceUrl(lectureRecord.getVideoResource().toString())
                .size(lectureRecord.getVideoResource() != null ? lectureRecord.getVideoResource().getSize().toString() : "")
                .intakeId(lectureRecord.getIntake().getPropertyId())
                .lessonId(lectureRecord.getCourseStageContent().getPropertyId())
                .build();
    }

    @Override
    public void changeDownloadOptionStatus(String recordId) {
        LectureRecord lectureRecord = lectureRecordRepository.findById(recordId).orElseThrow(() -> new EntryNotFoundException("Lecture Record not found."));
        lectureRecord.setDownloadEnabled(!lectureRecord.isDownloadEnabled());
    }
}
