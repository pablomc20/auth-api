package com.compadres.na.model.auth;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_details")
public class UserDetail {

    @Id
    @Column(name = "user_detail_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID userDetailId;

    private String name;
    private String phone;
    private String url_image;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

}
