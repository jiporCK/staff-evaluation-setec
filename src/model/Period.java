package model;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Period {
    private Long id;
    private Long companyId;
    private String code;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Long createdBy;
    private String status; // YES or NO
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
