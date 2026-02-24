package com.parking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.Member;
import com.parking.mapper.MemberMapper;
import com.parking.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public Member getMemberByUserId(Long userId) {
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return memberMapper.selectOne(wrapper);
    }

    @Override
    public Member createMember(Member member) {
        // 检查用户是否已有会员信息
        if (getMemberByUserId(member.getUserId()) != null) {
            throw new RuntimeException("用户已是会员");
        }
        
        // 设置默认值
        if (member.getPoints() == null) {
            member.setPoints(0);
        }
        if (member.getDiscountRate() == null) {
            member.setDiscountRate(getDiscountRateByLevel(member.getMemberLevel()));
        }
        if (member.getValidFrom() == null) {
            member.setValidFrom(LocalDate.now());
        }
        if (member.getValidTo() == null) {
            member.setValidTo(LocalDate.now().plusYears(1)); // 默认一年有效期
        }
        
        save(member);
        return member;
    }

    @Override
    public Member updateMember(Member member) {
        updateById(member);
        return member;
    }

    @Override
    public boolean deleteMember(Long id) {
        return removeById(id);
    }

    @Override
    public double getDiscountRate(Long userId) {
        Member member = getMemberByUserId(userId);
        if (member != null && member.getValidTo().isAfter(LocalDate.now())) {
            return member.getDiscountRate().doubleValue();
        }
        return 1.0; // 非会员或过期会员无折扣
    }

    @Override
    public int getPoints(Long userId) {
        Member member = getMemberByUserId(userId);
        if (member != null) {
            return member.getPoints();
        }
        return 0;
    }

    /**
     * 根据会员等级获取折扣率
     */
    private java.math.BigDecimal getDiscountRateByLevel(Integer level) {
        switch (level) {
            case 2: return java.math.BigDecimal.valueOf(0.95); // 银卡
            case 3: return java.math.BigDecimal.valueOf(0.90); // 金卡
            case 4: return java.math.BigDecimal.valueOf(0.85); // 钻石卡
            default: return java.math.BigDecimal.valueOf(1.00); // 普通会员
        }
    }
}