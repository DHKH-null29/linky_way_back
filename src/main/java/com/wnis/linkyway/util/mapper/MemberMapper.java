package com.wnis.linkyway.util.mapper;

import com.wnis.linkyway.dto.member.JoinRequest;
import com.wnis.linkyway.dto.member.MemberResponse;
import com.wnis.linkyway.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberMapper instance = Mappers.getMapper(MemberMapper.class);

    // 회원 가입
    Member joinRequestToMember(JoinRequest joinRequest);

    @Mapping(source = "id", target = "memberId")
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "nickname", ignore = true)
    MemberResponse memberToJoinResponse(Member member);

    // email 조회

    @Mapping(target = "memberId", ignore = true)
    @Mapping(target = "nickname", ignore = true)
    MemberResponse memberToEmailResponse(Member member);

    // 마이 페이지 조회
    @Mapping(target = "memberId", ignore = true)
    MemberResponse memberToMyPageResponse(Member member);

}
