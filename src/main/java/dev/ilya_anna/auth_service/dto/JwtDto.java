package dev.ilya_anna.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Access and refresh tokens", name = "JwtDto")
public class JwtDto {
    @Schema(description = "access jwt - contain user id, username, roles and permissions", 
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6ImFzZGZsdWdhc2Rsa2YiLCJ1c2VybmFtZSI6" 
            + "ImV4YW1wbGUtdXNlciIsInJvbGVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNzQ3NDI2MDYwfQ.5sZ5h6x4UaDDVYe" + 
            "WuAHYP5MTpWJ-vXRQG5Wq27EKwi8")
    private String access;
    @Schema(description = "refresh jwt - contain user id",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6ImFzZGZsdWdhc2Rsa2YiLCJpY" + 
            "XQiOjE3NDc0MjYxMzd9.Qi-XEDO1t9btH8U0BMWXOMNE-TgAuhmyyOYZC9Q2Sxw")
    private String refresh;
}
