package ru.otp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.otp.enums.OtpStatus;
import ru.otp.enums.OtpType;

@Entity
@Table(name = "otp_codes")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class OtpCode extends AbstractEntity implements Indexable<Long>{
    @Column
    private String otpCode;
    @Enumerated(EnumType.STRING)
    @Column
    private OtpStatus otpCodeStatus;
    @Enumerated(EnumType.STRING)
    @Column
    private OtpType otpType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Column
    private Long creationTime;

    @Override
    public void setIndex(Long index) {
        setId(index);
    }

    @Override
    public Long getIndex() {
        return getId();
    }
}
