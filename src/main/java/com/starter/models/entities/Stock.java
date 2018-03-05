package com.starter.models.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @NotNull
    private String ticker;

    private Boolean halfDump;

    private Boolean fullDump;
}
