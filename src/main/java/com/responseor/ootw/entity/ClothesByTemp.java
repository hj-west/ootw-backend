package com.responseor.ootw.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "OOTW_CLOTHES_BY_TEMPERATURE")
public class ClothesByTemp extends BaseEntity{
    /**
     * ID : ID
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * UUID : UUID
     */
    @Column(name = "UUID")
    private Long uuid;

    /**
     * START_TEMP : 시작 온도
     */
    @Column(name = "START_TEMP")
    private Integer startTemp;

    /**
     * END_TEMP : 끝 온도
     */
    @Column(name = "END_TEMP")
    private Integer endTemp;

    /**
     * CLOTHES : 옷 종류
     */
    @Column(name = "CLOTHES", length = 1000)
    private String clothes;
}
