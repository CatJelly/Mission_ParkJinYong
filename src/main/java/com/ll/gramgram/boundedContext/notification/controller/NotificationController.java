package com.ll.gramgram.boundedContext.notification.controller;

import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.boundedContext.notification.entity.Notification;
import com.ll.gramgram.boundedContext.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/usr/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final Rq rq;
    private final NotificationService notificationService;

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String showList(Model model) {
        if (!rq.getMember().hasConnectedInstaMember()) {
            return rq.redirectWithMsg("/usr/instaMember/connect", "먼저 본인의 인스타그램 아이디를 입력해주세요.");
        }

        // 자기가 보낸 호감, 자기가 받은 호감에 대한 알림을 모두 찾기 위함
        List<Notification> notifications = notificationService.findByToInstaMember(rq.getMember().getInstaMember());
        model.addAttribute("notifications", notifications);

        for(Notification notification : notifications) {
            notification.setReadDate(LocalDateTime.now());
            notificationService.update(notification);
        }

        return "usr/notification/list";
    }
}
