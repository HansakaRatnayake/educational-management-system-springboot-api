package com.lezord.system_api.entity;

import com.lezord.system_api.util.listener.AssignmentQuestionAudioListener;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "assignment_question_recording")
@EntityListeners(AssignmentQuestionAudioListener.class)
public class AssignmentQuestionRecording {
    @Id
    @Column(name = "property_id")
    private String propertyId;

    @Column(name = "created_date", nullable = false)
    private String createdDate;

    @Lob
    @Column(name = "directory", nullable = false,columnDefinition = "LONGBLOB")
    private byte[] directory;

    @Lob
    @Column(name = "file_name", nullable = false,columnDefinition = "LONGBLOB")
    private byte[] fileName;

    @Lob
    @Column(name = "hash", nullable = false,columnDefinition = "LONGBLOB")
    private byte[] hash;

    @Lob
    @Column(name = "resource_url",columnDefinition = "LONGBLOB")
    private byte[] resourceUrl;

    @OneToOne(mappedBy = "assignmentQuestionRecording",fetch = FetchType.LAZY)
    private AssignmentQuestion assignmentQuestion;
}
