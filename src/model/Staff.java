package model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Staff {
    private Long id;
    private Long companyId;
    private String name;
    private String sex;
    private LocalDate dateOfBirth;
    private String placeOfBirth;
    private String currentAddress;
    private String phone;
    private String email;
    private Long leaderId;
    private Long departmentId;
    private Long officeId;
    private Long positionId;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status; // YES or NO

}
