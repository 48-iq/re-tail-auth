package dev.ilya_anna.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Refresh token", name = "RefreshDto")
public class RefreshDto {
    @Schema(description = "refresh jwt - contain user id",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6ImFzZGZsdWdhc2Rsa2YiLCJpY" + 
            "XQiOjE3NDc0MjYxMzd9.Qi-XEDO1t9btH8U0BMWXOMNE-TgAuhmyyOYZC9Q2Sxw")
    @NotBlank
    private String refresh;
}
