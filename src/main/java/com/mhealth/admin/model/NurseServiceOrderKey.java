package com.mhealth.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NurseServiceOrderKey  implements Serializable {

    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "service_id")
    private Integer serviceId;
}
