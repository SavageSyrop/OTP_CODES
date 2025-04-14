package ru.otp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "otp_config")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class OtpConfig implements Indexable<Long> {
    @Id
    @Column
    private Long configVersion;
    @Column
    private Long otpCodeLength;
    @Column
    private Long expiresInMillis;

    @Override
    public void setIndex(Long index) {
        setConfigVersion(index);
    }

    @Override
    public Long getIndex() {
        return getConfigVersion();
    }
}
