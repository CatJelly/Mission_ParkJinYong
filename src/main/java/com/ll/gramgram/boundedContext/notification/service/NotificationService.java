package com.ll.gramgram.boundedContext.notification.service;

import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.notification.entity.Notification;
import com.ll.gramgram.boundedContext.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public List<Notification> findByToInstaMember(InstaMember toInstaMember) {
        return notificationRepository.findByToInstaMember(toInstaMember)
                .stream()
                .sorted((o1, o2) -> o2.getCreateDate().compareTo(o1.getCreateDate()))
                .collect(Collectors.toList());
    }

    @Transactional
    public RsData<Notification> create(InstaMember toInstaMember, InstaMember fromInstaMember, String typeCode, String oldGender, int oldAttractiveTypeCode, String newGender, int newAttractiveTypeCode) {
        Notification notification = Notification.builder()
                .readDate(null)
                .toInstaMember(toInstaMember)
                .fromInstaMember(fromInstaMember)
                .typeCode(typeCode)
                .oldGender(oldGender)
                .oldAttractiveTypeCode(oldAttractiveTypeCode)
                .newGender(newGender)
                .newAttractiveTypeCode(newAttractiveTypeCode)
                .build();

        notificationRepository.save(notification);

        return RsData.of("S-1", "알림이 등록되었습니다.", notification);
    }

    @Transactional
    public void update(Notification notification) {
        notification.setReadDate(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}
