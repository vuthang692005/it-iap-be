package com.example.it_iap.scheduler;

import com.example.it_iap.entity.Interview;
import com.example.it_iap.entity.InterviewQuestion;
import com.example.it_iap.entity.Json.AIFeedback;
import com.example.it_iap.entity.enums.InterviewQuestionStatus;
import com.example.it_iap.entity.enums.InterviewStatus;
import com.example.it_iap.repository.InterviewQuestionRepository;
import com.example.it_iap.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InterviewScheduler {
    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void closeExpiredInterviews() {
        AIFeedback aiFeedback = new AIFeedback(
                0f,
                0f,
                0f,
                "Hệ thống tự động kết thúc buổi phỏng vấn do vượt quá thời gian quy định (7 ngày). Do chưa hoàn tất quy trình, câu trả lời này không được đánh giá và ghi nhận điểm."
        );

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        List<Interview> expiredInterviews = interviewRepository
                .findByStatusAndStartAtBefore(InterviewStatus.IN_PROGRESS, sevenDaysAgo);

        expiredInterviews.forEach(interview -> {
            List<InterviewQuestion> interviewQuestions = interview.getInterviewQuestions();
            interviewQuestions.forEach(interviewQuestion -> {
                        if (interviewQuestion.getStatus() == InterviewQuestionStatus.UNANSWERED ||
                                interviewQuestion.getStatus() == InterviewQuestionStatus.ANSWERING){
                            interviewQuestion.setAiFeedback(aiFeedback);
                            interviewQuestion.setStatus(InterviewQuestionStatus.ANSWERED);
                        }
                }
            );

            interview.setStatus(InterviewStatus.COMPLETED);
            interview.setCompletedAt(LocalDateTime.now());
        });

        if (!expiredInterviews.isEmpty()) {
            interviewRepository.saveAll(expiredInterviews);
        }
    }
}
