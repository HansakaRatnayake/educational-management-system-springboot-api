package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestFAQDTO;
import com.lezord.system_api.dto.response.ResponseFAQDTO;
import com.lezord.system_api.entity.FAQ;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.repository.FAQRepository;
import com.lezord.system_api.service.FAQService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FAQServiceImpl implements FAQService {

    private final FAQRepository faqRepository;

    @Override
    public void create(RequestFAQDTO dto) {

        faqRepository.save(FAQ.builder()
                .question(dto.getQuestion())
                .answer(dto.getAnswer())
                .activeStatus(true)
                .orderId(generateOrderId())
                .build());
    }

    @Override
    public void update(RequestFAQDTO dto, String faqId) {
        FAQ faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new EntryNotFoundException("FAQ not found"));
        faq.setQuestion(dto.getQuestion());
        faq.setAnswer(dto.getAnswer());
        faqRepository.save(faq);
    }

    @Override
    public void delete(String faqId) {
        faqRepository.delete(faqRepository.findById(faqId)
                .orElseThrow(() -> new EntryNotFoundException("FAQ not found")));
    }

    @Override
    public void changeStatus(String faqId) {
        FAQ faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new EntryNotFoundException("FAQ not found"));
        faq.setActiveStatus(!faq.isActiveStatus());
        faqRepository.save(faq);
    }

    @Override
    public ResponseFAQDTO findById(String faqId) {
        return mapToResponseFAQDTO(faqRepository.findById(faqId)
                .orElseThrow(() -> new EntryNotFoundException("FAQ not found")));
    }

    @Override
    public List<ResponseFAQDTO> findAll(String searchText, int page, int size) {
        return faqRepository.findByQuestionContainingIgnoreCase(searchText, PageRequest.of(page, size, Sort.by("orderId").ascending()))
                .map(this::mapToResponseFAQDTO)
                .getContent();
    }

    private ResponseFAQDTO mapToResponseFAQDTO(FAQ faq) {
        return ResponseFAQDTO.builder()
                .faqId(faq.getFaqId())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .activeStatus(faq.isActiveStatus())
                .orderId(faq.getOrderId())
                .build();
    }

    private int generateOrderId() {
        return faqRepository.findMaxOrOrderId().orElse(0) + 1;
    }
}
