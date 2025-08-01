package com.kp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
     private String accessToken;
//     Frontend eke mek gn problem ekk awoth refresh access token
}

