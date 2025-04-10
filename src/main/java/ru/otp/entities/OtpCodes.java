package ru.otp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.otp.enums.OtpStatus;

@Entity
@Table(name = "otp_codes")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class OtpCodes extends AbstractEntity implements Indexable<Long>{
    @Column
    private String otpCode;
    @Enumerated(EnumType.STRING)
    @Column
    private OtpStatus otpCodeStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public void setIndex(Long index) {
        setId(index);
    }

    @Override
    public Long getIndex() {
        return getId();
    }
}
