package com.henriquericcio.springbatchdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contact {
    private Long id;
    private String lastName;
    private String firstName;
    private String phoneNumber;
}