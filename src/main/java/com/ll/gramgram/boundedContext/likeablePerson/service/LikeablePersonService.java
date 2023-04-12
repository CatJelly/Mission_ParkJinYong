package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import jakarta.transaction.TransactionScoped;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;

    @Transactional
    public RsData<LikeablePerson> like(Member member, String username, int attractiveTypeCode) {
        if (member.hasConnectedInstaMember() == false ) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        // 중복 회원 호감을 체크하기 위한 from 리스트
        List<LikeablePerson> fromLikeablePeople = member.getInstaMember().getFromLikeablePeople();
        // 회원 아이디가 동일한 유저
        LikeablePerson duplicate = fromLikeablePeople.stream()
                .filter(p -> p.getToInstaMemberUsername().equals(username))
                .findFirst().orElse(null);

        // 본인 호감 표시 제한
        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }
        // 호감표시 10명 제한
        else if (fromLikeablePeople.size() == 10) {
            return RsData.of("F-2", "11명 이상을 호감상대로 등록할 수 없습니다.");
        }
        // 중복 회원 호감 표시인 경우
        else if (duplicate != null) {
            // 호감표시도 중복인 경우
            if (duplicate.getAttractiveTypeCode() ==  attractiveTypeCode) {
                return RsData.of("F-3", "중복으로 호감상대로 등록할 수 없습니다.");
            }
            duplicate.setAttractiveTypeCode(attractiveTypeCode);
        }


        InstaMember fromInstaMember = member.getInstaMember();
        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

        LikeablePerson likeablePerson = duplicate;
        if (duplicate == null) {
            likeablePerson = create(fromInstaMember, toInstaMember, attractiveTypeCode);
            fromInstaMember.addFromLikeablePerson(likeablePerson);
            toInstaMember.addToLikeablePerson(likeablePerson);
        }

        likeablePersonRepository.save(likeablePerson); // 저장

        return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeablePerson);
    }

    public LikeablePerson create(InstaMember fromInstaMember, InstaMember toInstaMember, int attractiveTypeCode) {
        return LikeablePerson
                .builder()
                .fromInstaMember(fromInstaMember) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(fromInstaMember.getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .build();
    }
    public LikeablePerson modify(LikeablePerson likeablePerson, InstaMember fromInstaMember, InstaMember toInstaMember, int attractiveTypeCode) {
        return likeablePerson
                .toBuilder()
                .fromInstaMember(fromInstaMember) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(fromInstaMember.getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .build();
    }

    public Optional<LikeablePerson> findById(Long id) {
        return likeablePersonRepository.findById(id);
    }

    @Transactional
    public RsData delete(LikeablePerson likeablePerson) {
        String toInstaemberUsername = likeablePerson.getFromInstaMember().getUsername();
        likeablePersonRepository.delete(likeablePerson);
        return RsData.of("S-1", "%s님에 대한 호감을 취소하였습니다.".formatted(toInstaemberUsername));
    }

    public RsData canActorDelete(Member actor, LikeablePerson likeablePerson) {
        if (likeablePerson == null)
            return  RsData.of("F-1", "이미 삭제되었습니다.");

        if (!Objects.equals(actor.getInstaMember().getId(), likeablePerson.getFromInstaMember().getId())) {
            return RsData.of("F-2", "권한이 없습니다.");
        }

        return RsData.of("S-1", "삭제가능합니다.");
    }


    public List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberId(fromInstaMemberId);
    }
}
