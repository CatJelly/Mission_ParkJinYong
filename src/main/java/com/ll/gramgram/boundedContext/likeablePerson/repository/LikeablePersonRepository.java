package com.ll.gramgram.boundedContext.likeablePerson.repository;

import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeablePersonRepository extends JpaRepository<LikeablePerson, Long> {
    List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId);

    // ToInstaMember들 중에서 username인 것들을 List로 리턴
    List<LikeablePerson> findByToInstaMember_username(String username);

    // FromInstaMemberId와 ToInstaMember가 username인 LikeablePerson 리턴
    LikeablePerson findByFromInstaMemberIdAndToInstaMember_username(long fromInstaMemberId, String username);



}