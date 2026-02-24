package com.parking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.parking.entity.Member;

public interface MemberService extends IService<Member> {
    Member getMemberByUserId(Long userId);
    Member createMember(Member member);
    Member updateMember(Member member);
    boolean deleteMember(Long id);
    double getDiscountRate(Long userId);
    int getPoints(Long userId);
}