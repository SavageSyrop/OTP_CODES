package ru.otp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column
    private Long configVersion;
    @Column
    private Long otpCodeLength;
    @Column
    private Long exipesAfterMillis;

    @Override
    public void setIndex(Long index) {
        setConfigVersion(index);
    }

    @Override
    public Long getIndex() {
        return getConfigVersion();
    }
}
