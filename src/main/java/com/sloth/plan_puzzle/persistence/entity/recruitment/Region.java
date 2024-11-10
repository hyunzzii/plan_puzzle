package com.sloth.plan_puzzle.persistence.entity.recruitment;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Region {
    private String province;
    private String city;

    @Builder
    public Region(final String province, final String city){
        this.province = province;
        this.city = city;
    }
}

